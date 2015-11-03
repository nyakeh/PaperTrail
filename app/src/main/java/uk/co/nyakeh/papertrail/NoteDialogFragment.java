package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

public class NoteDialogFragment extends DialogFragment {
    private boolean mDeleteOnClose;
    private EditText mTitleEditText;
    private EditText mContentEditText;
    private Note mNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_note, container, false);

        String jsonNote = getArguments().getString(getString(R.string.note));
        mNote = new Gson().fromJson(jsonNote, Note.class);

        mTitleEditText = (EditText) view.findViewById(R.id.dialog_note_title);
        mTitleEditText.setText(mNote.getTitle());
        mTitleEditText.addTextChangedListener(new TextWatcher() {
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

        mContentEditText = (EditText) view.findViewById(R.id.dialog_note_content);
        mContentEditText.setText(mNote.getContent());
        mContentEditText.addTextChangedListener(new TextWatcher() {
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

        ImageView btnDelete = (ImageView) view.findViewById(R.id.dialog_note_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDeleteOnClose = true;
                dismiss();
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mDeleteOnClose){
            BookLab.get(getActivity()).deleteNote(mNote.getId());
        }
        else {
            BookLab.get(getActivity()).updateNote(mNote);
        }
        DialogFragmentCallbackInterface callback = (DialogFragmentCallbackInterface) getTargetFragment();
        callback.callBackMethod();
    }
}