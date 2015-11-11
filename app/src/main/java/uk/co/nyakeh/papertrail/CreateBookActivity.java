package uk.co.nyakeh.papertrail;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CreateBookActivity extends AppCompatActivity implements DateDialogCallbackInterface {
    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_BOOK_STATUS = "book_status";
    private static final String DIALOG_DATE = "DialogDate";

    private Book mBook;
    private EditText mTitleField;
    private EditText mAuthorField;
    private EditText mLengthField;
    private Button mDateStartedButton;
    private EditText mImageUrlField;
    private EditText mCategoryField;
    private UUID mBookId;
    private String mBookStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        mBookId = (UUID) extras.get(ARG_BOOK_ID);
        mBookStatus = extras.getString(ARG_BOOK_STATUS);
        mBook = BookLab.get(this).getBook(mBookId);
        if (mBook == null) {
            mBook = new Book(mBookId);
            mBook.setDateStarted(new Date());
            mBook.setDateFinished(new Date(Long.MAX_VALUE));
            mBook.setStatus(mBookStatus);
            BookLab.get(this).addBook(mBook);
        }

        mTitleField = (EditText) findViewById(R.id.book_title);
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

        mAuthorField = (EditText) findViewById(R.id.book_author);
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

        mLengthField = (EditText) findViewById(R.id.book_length);
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

        mDateStartedButton = (Button) findViewById(R.id.book_started_date);
        mDateStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBook.getDateStarted());
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mImageUrlField = (EditText) findViewById(R.id.book_image_url);
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

        mCategoryField = (EditText) findViewById(R.id.book_category);
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
    }

    private void updateDate() {
        String formattedStartDate = DateFormat.format("EEEE, MMM dd, yyyy", mBook.getDateStarted()).toString();
        mDateStartedButton.setText(formattedStartDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_create_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_book:
                BookLab.get(this).deleteBook(mBook.getId());
                finish();
                return true;
            case R.id.menu_item_save_book:
                BookLab.get(this).updateBook(mBook);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBook.isEmpty()) {
            BookLab.get(this).deleteBook(mBook.getId());
        } else {
            BookLab.get(this).updateBook(mBook);
        }
    }

    @Override
    public void onDateSelectedCallBack(Date date) {
        mBook.setDateStarted(date);
        updateDate();
    }
}