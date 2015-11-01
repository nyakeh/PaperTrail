package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

public class NoteFragment extends Fragment {

    private List<Note> mNotes;
    private EditText mCreateField;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_page, container, false);

        mNotes = (List<Note>) container.getTag( R.string.note);

        mCreateField = (EditText) view.findViewById(R.id.note_create);
        mCreateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getActivity().findViewById(R.id.content_container), "Archive", Snackbar.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}