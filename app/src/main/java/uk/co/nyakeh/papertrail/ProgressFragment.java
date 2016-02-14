package uk.co.nyakeh.papertrail;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProgressFragment extends Fragment {
    private NumberPicker mProgressNumberPickerField;
    private SeekBar mProgressSeekbarField;
    private Button mBookFinishedButton;
    private ImageView mBookImage;
    private TextView mBookDescription;
    private TextView mBookProgressPercentage;

    private Book mBook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_page, container, false);
        scheduleStartPostponedTransition(view);
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
                Date preSelectedFinishedDate = mBook.getDateFinished();
                SimpleDateFormat day = new SimpleDateFormat(Constants.EXPORT_DATE_FORMAT);
                if (day.format(preSelectedFinishedDate).equals(day.format(new Date(Long.MAX_VALUE)))) {
                    preSelectedFinishedDate = new Date();
                }
                DatePickerFragment dialog = DatePickerFragment.newInstance(preSelectedFinishedDate);
                dialog.setTargetFragment(ProgressFragment.this, Constants.REQUEST_DATE_FINISHED);
                dialog.show(manager, Constants.DIALOG_DATE);
            }
        });

        mBookDescription = (TextView) view.findViewById(R.id.book_description);
        mBookDescription.setText(mBook.getDescription());
        mBookDescription.setMovementMethod(ScrollingMovementMethod.getInstance());

        mBookProgressPercentage = (TextView) view.findViewById(R.id.book_progress_percentage);
        float progressAsPercentage = (float) mBook.getProgress() / mBook.getLength();
        String progressAsPercentageString = String.format("%.0f", (progressAsPercentage * 100));
        mBookProgressPercentage.setText(progressAsPercentageString);

        updateDate();
        return view;
    }

    private void updateDate() {
        String date = "";
        Date dateFinished = mBook.getDateFinished();
        SimpleDateFormat day = new SimpleDateFormat(Constants.EXPORT_DATE_FORMAT);
        if (!day.format(dateFinished).equals(day.format(new Date(Long.MAX_VALUE)))) {
            date = DateFormat.format(Constants.DISPLAY_DATE_FORMAT, dateFinished).toString();
        }
        mBookFinishedButton.setText(date);
    }

    public void updateProgress(int progress) {
        mBook.setProgress(progress);
        float progressAsPercentage = (float) mBook.getProgress() / mBook.getLength();
        String progressAsPercentageString = String.format("%.0f", (progressAsPercentage * 100));
        mBookProgressPercentage.setText(progressAsPercentageString);
        if (mBook.getStatus().equals(Constants.ARCHIVE)) {
            BookActivity.updateStatusSwitch(false);
        }
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        ActivityCompat.startPostponedEnterTransition(getActivity());
                        return true;
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == Constants.REQUEST_DATE_FINISHED) {
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