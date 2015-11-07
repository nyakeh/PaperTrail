package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.UUID;

public class CreateNoteActivity extends AppCompatActivity {
    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_NOTE_CONTENT = "note_content";

    private EditText mNoteTitle;
    private EditText mNoteContent;
    private Note mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        UUID bookId = (UUID) extras.get(ARG_BOOK_ID);
        String startingNoteContent = extras.getString(ARG_NOTE_CONTENT);

        Note note = new Note(bookId, startingNoteContent);
        BookLab.get(this).addNote(note);
        mNote = note;


        mNoteTitle = (EditText) findViewById(R.id.note_title);
        mNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                mNote.setTitle(inputChar.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mNoteContent = (EditText) findViewById(R.id.note_content);
        mNoteContent.setText(mNote.getContent());
        mNoteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                mNote.setContent(inputChar.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_create_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_note:
                BookLab.get(this).deleteNote(mNote.getId());
                finish();
                return true;
            case R.id.menu_item_save_note:
                if (noteEmpty()) {
                    BookLab.get(this).deleteNote(mNote.getId());
                } else {
                    BookLab.get(this).updateNote(mNote);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BookLab.get(this).updateNote(mNote);
    }

    public boolean noteEmpty() {
        return (mNote.getContent() == null || mNote.getContent().isEmpty()) && (mNote.getTitle() == null || mNote.getTitle().isEmpty()) ;
    }
}