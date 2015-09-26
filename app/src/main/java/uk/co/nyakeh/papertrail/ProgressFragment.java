package uk.co.nyakeh.papertrail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import java.util.Date;

public class ProgressFragment extends Fragment {
    private static final int REQUEST_DATE_FINISHED = 1;
    private static final String DIALOG_DATE = "DialogDate";
    private EditText mProgressField;
    private SeekBar mProgressSeekbarField;
    private Button mDateFinishedButton;

    private Book mBook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_page, container, false);

        mBook = (Book) container.getTag(R.string.book);

        mProgressField = (EditText) view.findViewById(R.id.book_progress);
        mProgressField.setText(Integer.toString(mBook.getProgress()));
        mProgressField.setFilters(new InputFilter[]{new InputFilterMinMax("0", Integer.toString(mBook.getLength()))});
        mProgressField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                String progressString = inputChar.toString();
                if (!progressString.isEmpty()) {
                    int progressValue = Integer.parseInt(progressString);
                    mBook.setProgress(progressValue);
                    mProgressSeekbarField.setProgress(progressValue);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mProgressSeekbarField = (SeekBar) view.findViewById(R.id.book_progress_seekbar);
        mProgressSeekbarField.setMax(mBook.getLength());
        mProgressSeekbarField.setProgress(mBook.getProgress());
        mProgressSeekbarField.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBook.setProgress(progress);
                mProgressField.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

    private void updateProgress(int progressValue) {
        mBook.setProgress(progressValue);
        mProgressField.setText(Integer.toString(progressValue));
        mProgressSeekbarField.setProgress(progressValue);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE_FINISHED) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDateFinished(date);
            updateDate();
            updateProgress(mBook.getLength());
        }
    }
}