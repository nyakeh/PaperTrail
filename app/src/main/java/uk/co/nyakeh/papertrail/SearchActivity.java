package uk.co.nyakeh.papertrail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {
    private EditText mSearchText;
    private Button mSearchClearTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSearchText = (EditText) findViewById(R.id.search_text);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                // performSearch()
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
}
