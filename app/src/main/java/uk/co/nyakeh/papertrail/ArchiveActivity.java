package uk.co.nyakeh.papertrail;

import android.support.v4.app.Fragment;

public class ArchiveActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ArchiveFragment();
    }

    @Override
    protected CharSequence getSelfNavDrawerItem() {
        return getString(R.string.archive);
    }
}