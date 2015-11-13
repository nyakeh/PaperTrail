package uk.co.nyakeh.papertrail;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class SearchActivity extends AppCompatActivity {
    private EditText mSearchText;
    private Button mSearchClearTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSearchText = (EditText) findViewById(R.id.search_text);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                // performSearch()
                if (!inputChar.toString().isEmpty()) {
                    new BookSearch().execute("quiet");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSearchClearTextButton = (Button) findViewById(R.id.search_clear_text);
        mSearchClearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
                mSearchText.requestFocus();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class BookSearch extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return String.valueOf(new Random().nextInt());
        }

        @Override
        protected void onPostExecute(String result) {
            TextView txt = (TextView) findViewById(R.id.search_output);
            txt.setText("Executed" + result);
        }
    }
}
