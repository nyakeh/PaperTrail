package uk.co.nyakeh.papertrail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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

public class BookFragment extends Fragment {
    private static final String ARG_BOOK_ID = "book_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE_STARTED = 0;
    private static final int REQUEST_DATE_FINISHED = 1;

    private ViewPager mViewPager;
    private Book mBook;
    private EditText mTitleField;
    private EditText mAuthorField;
    private EditText mBlurbField;
    private EditText mProgressField;
    private EditText mLengthField;
    private Button mDateStartedButton;
    private Button mDateFinishedButton;
    private EditText mISBNField;
    private EditText mImageUrlField;

    public static BookFragment newInstance(UUID bookId) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_BOOK_ID, bookId);

        BookFragment fragment = new BookFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID bookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        mBook = BookLab.get(getActivity()).getBook(bookId);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager((ViewPager) view.findViewById(R.id.viewpager));
        }
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
//
//        mTitleField = (EditText) view.findViewById(R.id.book_title);
//        mTitleField.setText(mBook.getTitle());
//        mTitleField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
//                mBook.setTitle(inputChar.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        mAuthorField = (EditText) view.findViewById(R.id.book_author);
//        mAuthorField.setText(mBook.getAuthor());
//        mAuthorField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
//                mBook.setAuthor(inputChar.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        mBlurbField = (EditText) view.findViewById(R.id.book_blurb);
//        mBlurbField.setText(mBook.getBlurb());
//        mBlurbField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
//                mBook.setBlurb(inputChar.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        mProgressField = (EditText) view.findViewById(R.id.book_progress);
//        mProgressField.setText(Integer.toString(mBook.getProgress()));
//        mProgressField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
//                String inputString = inputChar.toString();
//                if (!inputString.isEmpty()) {
//                    mBook.setProgress(Integer.parseInt(inputString));
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        mLengthField = (EditText) view.findViewById(R.id.book_length);
//        mLengthField.setText(Integer.toString(mBook.getLength()));
//        mLengthField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
//                String inputString = inputChar.toString();
//                if (!inputString.isEmpty()) {
//                    mBook.setLength(Integer.parseInt(inputChar.toString()));
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        mDateStartedButton = (Button) view.findViewById(R.id.book_started_date);
//        mDateStartedButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager manager = getFragmentManager();
//                DatePickerFragment dialog = DatePickerFragment.newInstance(mBook.getDateStarted());
//                dialog.setTargetFragment(BookFragment.this, REQUEST_DATE_STARTED);
//                dialog.show(manager, DIALOG_DATE);
//            }
//        });
//
//        mDateFinishedButton = (Button) view.findViewById(R.id.book_finished_date);
//        mDateFinishedButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager manager = getFragmentManager();
//                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date());
//                dialog.setTargetFragment(BookFragment.this, REQUEST_DATE_FINISHED);
//                dialog.show(manager, DIALOG_DATE);
//            }
//        });
//
//        mISBNField = (EditText) view.findViewById(R.id.book_isbn);
//        mISBNField.setText(mBook.getISBN());
//        mISBNField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
//                mBook.setISBN(inputChar.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        mImageUrlField = (EditText) view.findViewById(R.id.book_image_url);
//        mImageUrlField.setText(mBook.getImageUrl());
//        mImageUrlField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
//                mBook.setImageUrl(inputChar.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        updateDate();
        return view;
    }

    private void updateDate() {
        String formattedStartDate = DateFormat.format("EEEE, MMM dd, yyyy", mBook.getDateStarted()).toString();
        mDateStartedButton.setText(formattedStartDate);

        Date dateFinished = mBook.getDateFinished();
        if (!dateFinished.equals(new Date(Long.MAX_VALUE))) {
            String formattedFinishedDate = DateFormat.format("EEEE, MMM dd, yyyy", dateFinished).toString();
            mDateFinishedButton.setText(formattedFinishedDate);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_book:
                BookLab.get(getActivity()).deleteBook(mBook.getId());
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

        if (requestCode == REQUEST_DATE_FINISHED) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDateFinished(date);
            updateDate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BookLab.get(getActivity()).updateBook(mBook);
    }

    private void setupViewPager(ViewPager viewPager) {
        PageAdapter adapter = new PageAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new ProgressFragment(), "Progress");
        adapter.addFragment(new MetaDataFragment(), "Meta");
        viewPager.setAdapter(adapter);
    }
}
