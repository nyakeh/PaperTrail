package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NoteDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_note, container, false);
        getDialog().setTitle("Note Tings");

        Button dismiss = (Button) rootView.findViewById(R.id.dialog_note_delete);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });

        return rootView;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //TODO  updateNote()
    }
}
