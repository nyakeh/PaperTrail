package uk.co.nyakeh.papertrail;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CreateNoteActivity extends SingleFragmentActivity {
    private static final String EXTRA_BOOK_ID = "uk.co.nyakeh.papertrail.book_id";

    public static Intent newIntent(Context packageContext, UUID bookId) {
        Intent intent = new Intent(packageContext, CreateNoteActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID bookId = (UUID) getIntent().getSerializableExtra(EXTRA_BOOK_ID);
        return CreateNoteFragment.newInstance(bookId);
    }

    @Override
    protected CharSequence getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }
}