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

public class MetaDataFragment extends Fragment {
    private static final int REQUEST_DATE_STARTED = 0;
    private static final String DIALOG_DATE = "DialogDate";

    private EditText mTitleField;
    private EditText mAuthorField;
    private EditText mLengthField;
    private Button mDateStartedButton;
    private EditText mImageUrlField;
    private EditText mCategoryField;

    private Book mBook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.metadata_page, container, false);

        mBook = (Book) container.getTag(R.string.book);

        mTitleField = (EditText) view.findViewById(R.id.book_title);
        mTitleField.setText(mBook.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                mBook.setTitle(inputChar.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mAuthorField = (EditText) view.findViewById(R.id.book_author);
        mAuthorField.setText(mBook.getAuthor());
        mAuthorField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                mBook.setAuthor(inputChar.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mLengthField = (EditText) view.findViewById(R.id.book_length);
        mLengthField.setText(Integer.toString(mBook.getLength()));
        mLengthField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                String inputString = inputChar.toString();
                if (!inputString.isEmpty()) {
                    mBook.setLength(Integer.parseInt(inputChar.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDateStartedButton = (Button) view.findViewById(R.id.book_started_date);
        mDateStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBook.getDateStarted());
                dialog.setTargetFragment(MetaDataFragment.this, REQUEST_DATE_STARTED);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mImageUrlField = (EditText) view.findViewById(R.id.book_image_url);
        mImageUrlField.setText(mBook.getImageUrl());
        mImageUrlField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                mBook.setImageUrl(inputChar.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mCategoryField = (EditText) view.findViewById(R.id.book_category);
        mCategoryField.setText(mBook.getCategory());
        mCategoryField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                mBook.setCategory(inputChar.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        updateDate();
        return view;
    }

    private void updateDate() {
        String formattedStartDate = DateFormat.format("EEEE, MMM dd, yyyy", mBook.getDateStarted()).toString();
        mDateStartedButton.setText(formattedStartDate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE_STARTED) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDateStarted(date);
            updateDate();
        }
    }
}