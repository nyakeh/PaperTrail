package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

public class NoteDialogFragment extends DialogFragment {
    private boolean mDeleteOnClose;
    private EditText mTitleEditText;
    private EditText mContentEditText;
    private String mNoteTitle;
    private String mNoteContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_note, container, false);

        mNoteTitle = getArguments().getString(getString(R.string.NoteTitle));
        mNoteContent = getArguments().getString(getString(R.string.NoteContent));

        //getDialog().setTitle(mNote.getTitle());

        mTitleEditText = (EditText) view.findViewById(R.id.dialog_note_title);
        mTitleEditText.setText(mNoteTitle);
        mContentEditText = (EditText) view.findViewById(R.id.dialog_note_content);
        mContentEditText.setText(mNoteContent);

        Button btnDelete = (Button) view.findViewById(R.id.dialog_note_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDeleteOnClose = true; dismiss();
            }

        });
        Button btnUpdate = (Button) view.findViewById(R.id.dialog_note_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });

        return view;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (!mDeleteOnClose){
            //TODO  updateNote()
        }
    }
}
