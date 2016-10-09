package uk.co.nyakeh.papertrail;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.UUID;

import uk.co.nyakeh.papertrail.chrome.CustomTabActivityHelper;
import uk.co.nyakeh.papertrail.chrome.WebviewFallback;

public class BookActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private Book mBook;
    private static Switch mSwitch_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        UUID bookId = (UUID) extras.get(Constants.ARG_BOOK_ID);
        mBook = BookLab.get(this).getBook(bookId);
        getSupportActionBar().setTitle(mBook.getTitle());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager((ViewPager) findViewById(R.id.viewpager));
        }
        mViewPager.setTag(R.string.book, mBook);
        mViewPager.setClipChildren(false); // not currently used but one day...
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book, menu);

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

        if (mBook.getISBN() == null || mBook.getISBN().isEmpty()){
            MenuItem book_link = menu.findItem(R.id.menu_item_book_link);
            book_link.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.menu_item_delete_book:
                BookLab.get(this).deleteBook(mBook.getId());
                finish();
                return true;
            case R.id.menu_item_book_link:
                Uri uri = Uri.parse("https://www.goodreads.com/book/isbn/" + mBook.getISBN());
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                intentBuilder.setShowTitle(true);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back_white_24dp);
                intentBuilder.setCloseButtonIcon(icon);
                intentBuilder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
                intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                intentBuilder.addMenuItem("Share", createShareMenuIntent());
                CustomTabActivityHelper.openCustomTab(BookActivity.this, intentBuilder.build(), uri, new WebviewFallback());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private PendingIntent createShareMenuIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Goodreads: " + mBook.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, ("https://www.goodreads.com/book/isbn/" + mBook.getISBN()));
        return PendingIntent.getActivity(this, 0, shareIntent, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        BookLab.get(this).updateBook(mBook);
    }

    private void setupViewPager(ViewPager viewPager) {
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProgressFragment(), getString(R.string.progress));
        adapter.addFragment(new MetaDataFragment(), getString(R.string.meta));
        viewPager.setAdapter(adapter);
    }

    public static void updateStatusSwitch(boolean isChecked) {
        mSwitch_status.setChecked(isChecked);
    }
}