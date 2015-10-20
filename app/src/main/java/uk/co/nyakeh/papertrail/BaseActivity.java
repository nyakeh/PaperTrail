package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static final CharSequence NAVDRAWER_ITEM_INVALID = "Invalid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        CharSequence itemTitle = item.getTitle();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (itemTitle.equals(getSelfNavDrawerItem())) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        Intent intent = new Intent(this, BookListActivity.class);
        if (itemId == R.id.nav_archive) {
            intent = new Intent(this, ArchiveActivity.class);
        } else if (itemId == R.id.nav_reading_list) {
            intent = new Intent(this, ReadingListActivity.class);
        }
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }

    protected CharSequence getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }
}
