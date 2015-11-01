package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.UUID;

public class CreateNoteFragment extends Fragment {
    private static final String ARG_BOOK_ID = "book_id";

    private EditText mNoteTitle;
    private EditText mNoteContent;
    private Note mNote;

    public static CreateNoteFragment newInstance(UUID bookId) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_BOOK_ID, bookId);
        CreateNoteFragment fragment = new CreateNoteFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID mBookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        Note note = new Note();
        note.setBookId(mBookId);
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
    public void onPause() {
        super.onPause();
        BookLab.get(getActivity()).updateNote(mNote);
    }
}