package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.UUID;

public class BookActivity extends AppCompatActivity {
    private static final String ARG_BOOK_ID = "book_id";

    private ViewPager mViewPager;
    private Book mBook;
    static Switch mSwitch_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        UUID bookId = (UUID) extras.get(ARG_BOOK_ID);
        mBook = BookLab.get(this).getBook(bookId);
        getSupportActionBar().setTitle(mBook.getTitle());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager((ViewPager) findViewById(R.id.viewpager));
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setTag(R.string.book, mBook);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_book, menu);

        MenuItem item = menu.findItem(R.id.status_switch_item);
        item.setActionView(R.layout.switch_layout);
        mSwitch_status = (Switch) item.getActionView().findViewById(R.id.status_switch);
        if (mBook.getStatus().equals(Constants.READING)) {
            mSwitch_status.setChecked(true);
        }

        mSwitch_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBook.setStatus(Constants.READING);
                } else {
                    if (mBook.isFinished()) {
                        mBook.setStatus(Constants.ARCHIVE);
                    } else {
                        mBook.setStatus(Constants.QUEUE);
                    }
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_book:
                BookLab.get(this).deleteBook(mBook.getId());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BookLab.get(this).updateBook(mBook);
    }

    private void setupViewPager(ViewPager viewPager) {
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProgressFragment(), getString(R.string.Progress));
        adapter.addFragment(new NoteFragment(), getString(R.string.notes));
        adapter.addFragment(new MetaDataFragment(), getString(R.string.meta));
        viewPager.setAdapter(adapter);
    }

    public static void updateStatusSwitch(boolean isChecked){
        mSwitch_status.setChecked(isChecked);
    }
}