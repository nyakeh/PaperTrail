package uk.co.nyakeh.papertrail;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.gson.Gson;

import java.util.Date;

public class CreateBookActivity extends AppCompatActivity implements DateDialogCallbackInterface {
    private Book mBook;
    private EditText mTitleField;
    private EditText mAuthorField;
    private EditText mLengthField;
    private Button mDateStartedButton;
    private EditText mImageUrlField;
    private EditText mCategoryField;
    private EditText mISBNField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        String jsonBook = extras.getString(Constants.ARG_NEW_BOOK);
        mBook = new Gson().fromJson(jsonBook, Book.class);
        BookLab.get(this).addBook(mBook);

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
                dialog.show(manager, Constants.DIALOG_DATE);
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

        mISBNField = (EditText) findViewById(R.id.book_isbn);
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

        updateDate();
    }

    private void updateDate() {
        String formattedStartDate = DateFormat.format(Constants.DISPLAY_DATE_FORMAT, mBook.getDateStarted()).toString();
        mDateStartedButton.setText(formattedStartDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                BookLab.get(this).deleteBook(mBook.getId());
                finish();
                return true;
            case R.id.menu_item_delete_book:
                BookLab.get(this).deleteBook(mBook.getId());
                finish();
                return true;
            case R.id.menu_item_save_book:
                BookLab.get(this).updateBook(mBook);
                if (mBook.getStatus().equals(Constants.READING)) {
                    Intent intent = new Intent(CreateBookActivity.this, BookListActivity.class);
                    startActivity(intent);
                } else if (mBook.getStatus().equals(Constants.QUEUE)) {
                    Intent intent = new Intent(CreateBookActivity.this, ReadingListActivity.class);
                    startActivity(intent);
                } else {
                    finish();
                }
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