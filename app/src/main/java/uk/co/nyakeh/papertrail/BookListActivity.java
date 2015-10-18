package uk.co.nyakeh.papertrail;

import android.support.v4.app.Fragment;

public class BookListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new BookListFragment();
    }

    @Override
    protected CharSequence getSelfNavDrawerItem() {
        return getString(R.string.currently_reading);
    }
}