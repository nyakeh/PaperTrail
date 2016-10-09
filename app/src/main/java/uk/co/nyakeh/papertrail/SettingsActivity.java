package uk.co.nyakeh.papertrail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatPreferenceActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public static class SettingsFragment extends PreferenceFragment {

        public static final int RESTORE_REQUEST_CODE = 47;

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_prefs);

            Preference backupPreference = findPreference(getString(R.string.settings_backup));
            backupPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    backupBookData();
                    return true;
                }
            });

            Preference clearSearchPreference = findPreference(getString(R.string.settings_clear_search_history));
            clearSearchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    clearSearchHistory();
                    return true;
                }
            });
            Preference restorePreference = findPreference(getString(R.string.settings_restore));
            restorePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    openDocumentSearchForBackup();
                    return true;
                }
            });
            final EditTextPreference restorePastePreference = (EditTextPreference) findPreference(getString(R.string.settings_manual_restore));
            restorePastePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String bookCsv = restorePastePreference.getText();
                    importBookData(bookCsv);
                    return true;
                }
            });
        }

        private void backupBookData() {
            BookLab bookLab = BookLab.get(getActivity());
            final String bookDataHtml = bookLab.getBackupData();
            Log.d("backupBookData:", bookDataHtml);

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Book back up data", bookDataHtml);
            clipboard.setPrimaryClip(clip);

            Snackbar
                    .make(getView(), "Data copied to your clipboard.", Snackbar.LENGTH_LONG)
                    .setAction("Share", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            shareBookData(bookDataHtml);
                        }

                    })
                    .show();
        }

        private void shareBookData(String bookDataHtml) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.EXPORT_DATE_FORMAT);
            String formattedDate = dateFormat.format(new Date());

            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, formattedDate + " : Paper Trail book data backup");
            shareIntent.putExtra(Intent.EXTRA_TEXT, bookDataHtml);
            startActivity(Intent.createChooser(shareIntent, "Share book data backup"));
        }

        private void clearSearchHistory() {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(), SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.clearHistory();
            Snackbar.make(getView(), "Book search history cleared.", Snackbar.LENGTH_LONG).show();
        }

        private void openDocumentSearchForBackup() {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");
            startActivityForResult(intent, RESTORE_REQUEST_CODE);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
            switch (requestCode) {
                case RESTORE_REQUEST_CODE: {
                    if (resultCode == RESULT_OK) {
                        try {
                            Uri uri;
                            if (resultData != null) {
                                uri = resultData.getData();
                                importBookData(uri);
                            }
                        } catch (Exception e) {
                            Log.e("Import CSV fail: ", e.toString());
                        }
                    }
                }
            }
        }

        private Book extractBook(String[] tokens) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.EXPORT_DATE_FORMAT);

            Date started = new Date();
            if (!tokens[0].isEmpty()) {
                try {
                    started = dateFormat.parse(tokens[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Date finished = new Date(Long.MAX_VALUE);
            if (!tokens[1].isEmpty()) {
                try {
                    finished = dateFormat.parse(tokens[1]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            int pageCount = 100;
            if (!tokens[8].isEmpty()) {
                pageCount = Integer.parseInt(tokens[8]);
            }
            int progress = 100;
            if (!tokens[7].isEmpty()) {
                progress = Integer.parseInt(tokens[7]);
            }
            Float rating = 0f;
            if (!tokens[6].isEmpty()) {
                rating = Float.parseFloat(tokens[6]);
            }
            String isbn = tokens[10];
            if (isbn.length() == 10) {
                isbn = "0" + isbn;
            }

            Book book = new Book(UUID.randomUUID());
            try {
                if (!tokens[11].isEmpty()) {
                    book = new Book(UUID.fromString(tokens[12]));
                }
            } catch (Exception e) {
            }

            book.setStatus(tokens[5]);
            book.setTitle(tokens[2].replace("\"", ""));
            book.setAuthor(tokens[3].replace("\"", ""));
            book.setISBN(isbn);
            book.setLength(pageCount);
            book.setProgress(progress);
            book.setRating(rating);
            book.setImageUrl(tokens[11]);
            book.setDescription(tokens[9]);
            book.setCategory(tokens[4]);
            book.setDateStarted(started);
            book.setDateFinished(finished);
            return book;
        }

        private void importBookData(Uri uri) {
            InputStream inputStream;
            int bookImportCount = 0;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                BookLab bookLab = BookLab.get(getActivity());
                String line;
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    Log.d("CSV line: ", line);
                    String[] tokens = line.split(",(?=(?:[^\"]|\"[^\"]*\")*$)");
                    if (tokens.length == 13) {
                        Book book = extractBook(tokens);
                        bookLab.addBook(book);
                        bookImportCount++;
                    } else {
                        Log.d("Invalid line", String.valueOf(tokens.length) + " tokens");
                    }
                }
                inputStream.close();
            } catch (Exception exception) {
                exception.printStackTrace();
                Snackbar.make(getView(), "Book CSV import failed.", Snackbar.LENGTH_LONG).show();
                return;
            }
            Snackbar.make(getView(), bookImportCount + " books successfully imported.", Snackbar.LENGTH_LONG).show();
        }

        private void importBookData(String bookCsv) {
            int bookImportCount = 0;
            try {
                String[] parts = bookCsv.split("</tr>");
                BookLab bookLab = BookLab.get(getActivity());
                String line;
                for (int i = 1; i < parts.length; i++) {
                    line = parts[i].substring(4);
                    Log.d("CSV line: ", line);
                    List<String> sections = new ArrayList<>();
                    Pattern pattern = Pattern.compile("<td><\\/td>|<td>(.+?)<\\/td>");
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        String group = matcher.group();
                        sections.add(group.substring(4, group.length()-5));
                    }
                    if (sections.size() == 13) {
                        String[] simpleArray = new String[ sections.size() ];
                        sections.toArray( simpleArray );
                        Book book = extractBook(simpleArray);
                        bookLab.addBook(book);
                        bookImportCount++;
                    } else {
                        Log.d("Invalid line", String.valueOf(sections.size()) + " tokens");
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                Snackbar.make(getView(), "Book CSV import failed.", Snackbar.LENGTH_LONG).show();
                return;
            }
            Snackbar.make(getView(), bookImportCount + " books successfully imported.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Intent intent = new Intent(this, ArchiveActivity.class);
        if (itemId == R.id.nav_settings) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.nav_reading_list) {
            intent = new Intent(this, ReadingListActivity.class);
        } else if (itemId == R.id.nav_currently_reading) {
            intent = new Intent(this, BookListActivity.class);
        } else if (itemId == R.id.nav_statistics) {
            intent = new Intent(this, StatisticsActivity.class);
        }
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }
}