package uk.co.nyakeh.papertrail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;

import com.squareup.picasso.Picasso;

import java.util.Date;

public class ProgressFragment extends Fragment {
    private static final int REQUEST_DATE_FINISHED = 1;
    private static final String DIALOG_DATE = "DialogDate";
    private NumberPicker mProgressNumberPickerField;
    private SeekBar mProgressSeekbarField;
    private Button mBookFinishedButton;
    private ImageView mBookImage;

    private Book mBook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_page, container, false);

        mBook = (Book) container.getTag(R.string.book);

        mProgressNumberPickerField = (NumberPicker) view.findViewById(R.id.book_progress_number_picker);
        mProgressNumberPickerField.setMinValue(0);
        mProgressNumberPickerField.setMaxValue(mBook.getLength());
        mProgressNumberPickerField.setValue(mBook.getProgress());
        mProgressNumberPickerField.setWrapSelectorWheel(false);
        mProgressNumberPickerField.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldValue, int newValue) {
                    updateProgress(newValue);
                    mProgressSeekbarField.setProgress(newValue);
                }
            });

        mProgressSeekbarField = (SeekBar) view.findViewById(R.id.book_progress_bar);
        mProgressSeekbarField.setMax(mBook.getLength());
        mProgressSeekbarField.setProgress(mBook.getProgress());
        mProgressSeekbarField.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateProgress(progress);
                mProgressNumberPickerField.setValue(progress);
                updateDate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mBookImage = (ImageView) view.findViewById(R.id.book_image);
        String safePicassoImageUrl = (mBook.getImageUrl().isEmpty()) ? "fail_gracefully_pls" : mBook.getImageUrl();
        Picasso.with(getActivity())
                .load(safePicassoImageUrl)
                .placeholder(R.drawable.books)
                .error(R.drawable.books)
                .into(mBookImage);

        mBookFinishedButton = (Button) view.findViewById(R.id.book_finished_date);
        mBookFinishedButton.setOnClickListener(new View.OnClickListener() {
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
        String date = "";
        Date dateFinished = mBook.getDateFinished();
        if (!dateFinished.equals(new Date(Long.MAX_VALUE))) {
            date = DateFormat.format("EEEE, MMM dd, yyyy", dateFinished).toString();
        }
        mBookFinishedButton.setText(date);
    }

    public void updateProgress(int progress) {
        mBook.setProgress(progress);
        if (mBook.getStatus().equals(Constants.ARCHIVE)){
            BookFragment.updateStatusSwitch(false);
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
            updateDate();
            int progressValue = mBook.getLength();
            updateProgress(progressValue);
            mProgressNumberPickerField.setValue(progressValue);
            mProgressSeekbarField.setProgress(progressValue);
        }
    }

}