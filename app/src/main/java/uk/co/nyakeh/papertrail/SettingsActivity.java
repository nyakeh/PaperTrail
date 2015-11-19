package uk.co.nyakeh.papertrail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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

            Preference backup_button = findPreference(getString(R.string.settings_backup));
            backup_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    backupBookData();
                    return true;
                }
            });
            Preference clear_search_button = findPreference(getString(R.string.settings_clear_search_history));
            clear_search_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    clearSearchHistory();
                    return true;
                }
            });
            Preference restore_button = findPreference(getString(R.string.settings_restore));
            restore_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    importBookData();
                    return true;
                }
            });
        }

        private void backupBookData() {
            BookLab bookLab = BookLab.get(getActivity());
            String readingDataHtml = bookLab.getBackupData();
            Log.d("backupBookData:", readingDataHtml);

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", readingDataHtml);
            clipboard.setPrimaryClip(clip);

            Snackbar.make(getView(), "Data copied to your clipboard.", Snackbar.LENGTH_LONG).show();
        }

        private void clearSearchHistory() {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(), SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.clearHistory();
            Snackbar.make(getView(), "Book search history cleared.", Snackbar.LENGTH_LONG).show();
        }

        private void importBookData() {
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
                                readTextFromUri(uri);
                            }
                        } catch (Exception e) {
                            Log.e("Import CSV fail: ", e.toString());
                        }
                    }
                }
            }
        }

        private void readTextFromUri(Uri uri) {
            InputStream inputStream;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                BookLab bookLab = BookLab.get(getActivity());
                String line;
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    Log.d("CSV line: ", line);
                    Book book = extractBook(line);
                    bookLab.addBook(book);
                }
                inputStream .close();
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(getView(), "Book CSV import failed.", Snackbar.LENGTH_LONG).show();
                return;
            }
            Snackbar.make(getView(), "Book CSV import successful.", Snackbar.LENGTH_LONG).show();
        }

        private Book extractBook(String line) {
            String[] tokens = line.split(",(?=(?:[^\"]|\"[^\"]*\")*$)");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
            if (!tokens[7].isEmpty()) {
                pageCount = Integer.parseInt(tokens[7]);
            }
            int progress = 100;
            if (!tokens[6].isEmpty()) {
                progress = Integer.parseInt(tokens[6]);
            }

            Book book = new Book(UUID.randomUUID());
            try {
                if (!tokens[11].isEmpty()) {
                    book = new Book(UUID.fromString(tokens[11]));
                }
            } catch (Exception e) {
            }

            book.setStatus(tokens[5]);
            book.setTitle(tokens[2]);
            book.setAuthor(tokens[3]);
            book.setISBN(tokens[9]);
            book.setLength(pageCount);
            book.setImageUrl(tokens[10]);
            book.setDescription(tokens[8]);
            book.setDateStarted(started);
            book.setDateFinished(finished);
            book.setCategory(tokens[4]);
            book.setProgress(progress);
            return book;
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
        }
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }
}