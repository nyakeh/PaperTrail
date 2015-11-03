package uk.co.nyakeh.papertrail;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CreateNoteActivity extends SingleFragmentActivity {
    private static final String EXTRA_BOOK_ID = "uk.co.nyakeh.papertrail.book_id";
    private static final String EXTRA_NOTE_CONTENT = "uk.co.nyakeh.papertrail.note_content";

    public static Intent newIntent(Context packageContext, UUID bookId, String startingNoteContent) {
        Intent intent = new Intent(packageContext, CreateNoteActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        intent.putExtra(EXTRA_NOTE_CONTENT, startingNoteContent);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID bookId = (UUID) getIntent().getSerializableExtra(EXTRA_BOOK_ID);
        String startingNoteContent = (String) getIntent().getSerializableExtra(EXTRA_NOTE_CONTENT);
        return CreateNoteFragment.newInstance(bookId, startingNoteContent);
    }

    @Override
    protected CharSequence getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }
}