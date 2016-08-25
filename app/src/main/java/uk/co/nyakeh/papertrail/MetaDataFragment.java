package uk.co.nyakeh.papertrail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Date;

public class MetaDataFragment extends Fragment {
    private EditText mTitleField;
    private EditText mAuthorField;
    private EditText mLengthField;
    private Button mDateStartedButton;
    private EditText mImageUrlField;
    private Spinner mCategoryField;
    private EditText mISBNField;
    private RatingBar mRating;
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
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(inputChar.toString());
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
                dialog.setTargetFragment(MetaDataFragment.this, Constants.REQUEST_DATE_STARTED);
                dialog.show(manager, Constants.DIALOG_DATE);
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

        mCategoryField = (Spinner) view.findViewById(R.id.book_category);

        String[] categories = getResources().getStringArray(R.array.book_categories);
        int selectedCategoryPosition = Arrays.asList(categories).indexOf(mBook.getCategory());
        mCategoryField.setSelection(selectedCategoryPosition);
        mCategoryField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Object selectedItem = mCategoryField.getSelectedItem();
                String category = selectedItem.toString();
                mBook.setCategory(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mISBNField = (EditText) view.findViewById(R.id.book_isbn);
        mISBNField.setText(mBook.getISBN());
        mISBNField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                mBook.setISBN(inputChar.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mRating = (RatingBar) view.findViewById(R.id.book_rating);
        mRating.setRating(mBook.getRating());
        mRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mBook.setRating(rating);
            }
        });

        updateDate();
        return view;
    }

    private void updateDate() {
        String formattedStartDate = DateFormat.format(Constants.DISPLAY_DATE_FORMAT, mBook.getDateStarted()).toString();
        mDateStartedButton.setText(formattedStartDate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == Constants.REQUEST_DATE_STARTED) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDateStarted(date);
            updateDate();
        }
    }
}