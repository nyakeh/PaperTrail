package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.UUID;

public class CreateNoteFragment extends Fragment {
    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_NOTE_CONTENT = "note_content";

    private EditText mNoteTitle;
    private EditText mNoteContent;
    private Note mNote;

    public static CreateNoteFragment newInstance(UUID bookId, String startingNoteContent) {
        CreateNoteFragment fragment = new CreateNoteFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_BOOK_ID, bookId);
        arguments.putSerializable(ARG_NOTE_CONTENT, startingNoteContent);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        String startingNoteContent = (String) getArguments().getSerializable(ARG_NOTE_CONTENT);
        UUID bookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        Note note = new Note(bookId, startingNoteContent);
        BookLab.get(getActivity()).addNote(note);
        mNote = note;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_note, container, false);

        mNoteTitle = (EditText) view.findViewById(R.id.note_title);
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

        mNoteContent = (EditText) view.findViewById(R.id.note_content);
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

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_create_note, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_note:
                BookLab.get(getActivity()).deleteNote(mNote.getId());
                getActivity().finish();
                return true;
            case R.id.menu_item_save_note:
                BookLab.get(getActivity()).updateNote(mNote);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BookLab.get(getActivity()).updateNote(mNote);
    }
}