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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CreateBookFragment extends Fragment {
    private static final String ARG_BOOK_ID = "book_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE_STARTED = 0;

    private Book mBook;
    private EditText mTitleField;
    private EditText mAuthorField;
    private EditText mLengthField;
    private Button mDateStartedButton;
    private EditText mImageUrlField;
    private UUID mBookId;

    public static CreateBookFragment newInstance(UUID bookId) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_BOOK_ID, bookId);

        CreateBookFragment fragment = new CreateBookFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mBookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        mBook = BookLab.get(getActivity()).getBook(mBookId);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_book, container, false);

        if (mBook == null){
            mBook = new Book(mBookId);
            mBook.setDateStarted(new Date());
            mBook.setDateFinished(new Date(Long.MAX_VALUE));
            BookLab.get(getActivity()).addBook(mBook);
        }

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
                dialog.setTargetFragment(CreateBookFragment.this, REQUEST_DATE_STARTED);
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

        updateDate();
        return view;
    }

    private void updateDate() {
        String formattedStartDate = DateFormat.format("EEEE, MMM dd, yyyy", mBook.getDateStarted()).toString();
        mDateStartedButton.setText(formattedStartDate);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_create_book, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_book:
                BookLab.get(getActivity()).deleteBook(mBook.getId());
                getActivity().finish();
                return true;
            case R.id.menu_item_save_book:
                BookLab.get(getActivity()).updateBook(mBook);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onPause() {
        super.onPause();
        if (mBook.isEmpty()) {
            BookLab.get(getActivity()).deleteBook(mBook.getId());
        } else {
            BookLab.get(getActivity()).updateBook(mBook);
        }
    }
}
