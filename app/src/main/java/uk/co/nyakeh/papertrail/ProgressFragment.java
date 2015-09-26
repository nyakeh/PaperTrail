package uk.co.nyakeh.papertrail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

public class ProgressFragment extends Fragment {
    private static final int REQUEST_DATE_FINISHED = 1;
    private static final String DIALOG_DATE = "DialogDate";
    private EditText mProgressField;
    private Button mDateFinishedButton;

    private Book mBook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_page, container, false);

        mBook = (Book) container.getTag(R.string.book);

        mProgressField = (EditText) view.findViewById(R.id.book_progress);
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

        mDateFinishedButton = (Button) view.findViewById(R.id.book_finished_date);
        mDateFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date());
                dialog.setTargetFragment(ProgressFragment.this, REQUEST_DATE_FINISHED);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        updateDate();
        return view;
    }

    private void updateDate() {
        Date dateFinished = mBook.getDateFinished();
        if (!dateFinished.equals(new Date(Long.MAX_VALUE))) {
            String formattedFinishedDate = DateFormat.format("EEEE, MMM dd, yyyy", dateFinished).toString();
            mDateFinishedButton.setText(formattedFinishedDate);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE_FINISHED) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDateFinished(date);
            mBook.setProgress(mBook.getLength());
            mProgressField.setText(Integer.toString(mBook.getProgress()));
            updateDate();
        }
    }
}