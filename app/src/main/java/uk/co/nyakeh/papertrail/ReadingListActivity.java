package uk.co.nyakeh.papertrail;

import android.support.v4.app.Fragment;

public class ReadingListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ReadingListFragment();
    }

    @Override
    protected CharSequence getSelfNavDrawerItem() {
        return getString(R.string.reading_list);
    }
}