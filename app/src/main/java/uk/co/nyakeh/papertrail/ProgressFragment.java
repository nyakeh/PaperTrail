package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ProgressFragment extends Fragment {
    private EditText mProgressField;

    private Book mBook;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.progress_page, container, false);

        mBook = (Book) container.getTag(R.string.book);

        mProgressField = (EditText) rootView.findViewById(R.id.book_progress);
        mProgressField.setText(Integer.toString(mBook.getProgress()));
        mProgressField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                String inputString = inputChar.toString();
                if (!inputString.isEmpty()) {
                    mBook.setProgress(Integer.parseInt(inputString));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return rootView;
    }
}
