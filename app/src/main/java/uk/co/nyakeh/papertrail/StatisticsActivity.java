package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class StatisticsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        BookLab bookLab = BookLab.get(this);
        Statistics statistics = bookLab.getBookStatistics();

        TextView totalPagesRead = (TextView) findViewById(R.id.total_pages_read);
        TextView averagePageCount = (TextView) findViewById(R.id.average_page_count);
        TextView averageRating = (TextView) findViewById(R.id.average_rating);
        TextView mostReadCategory = (TextView) findViewById(R.id.most_read_category);

        totalPagesRead.setText(String.valueOf(statistics.TotalPagesRead));
        averagePageCount.setText(String.valueOf(statistics.AveragePageCount));
        averageRating.setText(String.valueOf(statistics.AverageRating));
        mostReadCategory.setText(statistics.MostReadCategory);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Intent intent = new Intent(this, ArchiveActivity.class);
        if (itemId == R.id.nav_currently_reading) {
            intent = new Intent(this, BookListActivity.class);
        } else if (itemId == R.id.nav_reading_list) {
            intent = new Intent(this, ReadingListActivity.class);
        } else if (itemId == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
        } else if (itemId == R.id.nav_statistics) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }
}