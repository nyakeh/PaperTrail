package uk.co.nyakeh.papertrail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_prefs);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sp.registerOnSharedPreferenceChangeListener(this);

            Preference button = findPreference(getString(R.string.backupButton));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    backupBookData();
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

            Snackbar.make(getView(), "Data copied to your clipboard", Snackbar.LENGTH_LONG).show();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("pref_local_times"))
                Log.d("onSharedPreference:", key + " ");
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