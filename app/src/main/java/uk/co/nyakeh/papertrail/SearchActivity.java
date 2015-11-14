package uk.co.nyakeh.papertrail;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {
    private EditText mSearchText;
    private Button mSearchClearTextButton;
    private RecyclerView mSearchResultsRecyclerView;
    private SearchResultsAdapter mSearchResultsAdapter;

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
                if (!inputChar.toString().isEmpty()) {
                    new BookSearch().execute(inputChar.toString());
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

        mSearchResultsRecyclerView = (RecyclerView) findViewById(R.id.search_results);
        mSearchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
            String xml = "";

            URL url;
            HttpURLConnection urlConnection = null;
            String queryString = params[0];
            try {
                url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + queryString + "&filter=ebooks&key=" + getString(R.string.google_books_api_key));

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader isw = new InputStreamReader(in);

                BufferedReader br = new BufferedReader(isw);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                xml = sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return xml;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView txt = (TextView) findViewById(R.id.search_output);
            //txt.setText("Executed" + result);
            Log.d("Raw results: ", result);
            JSONObject resultObject = null;
            try {
                resultObject = new JSONObject(result);
                txt.setText("Total books: " + resultObject.getString("totalItems"));
                JSONArray bookArray = resultObject.getJSONArray("items");

                if (mSearchResultsAdapter == null) {
                    mSearchResultsAdapter = new SearchResultsAdapter(bookArray);
                    mSearchResultsRecyclerView.setAdapter(mSearchResultsAdapter);
                } else {
                    mSearchResultsAdapter.setBooks(bookArray);
                    mSearchResultsAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class SearchResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private JSONObject mSearchResult;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mIsbnTextView;

        public SearchResultHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_book_author);
            mIsbnTextView = (TextView) itemView.findViewById(R.id.list_item_book_date_finished);
        }

        private void bindSearchResult(JSONObject book) {
            mSearchResult = book;
            try {
                JSONObject volumeObject = book.getJSONObject("volumeInfo");
                mTitleTextView.setText(volumeObject.getString("title"));
                //mAuthorTextView.setText(String.valueOf(volumeObject.getJSONArray("authors").getJSONObject(0)));
                //mIsbnTextView.setText(volumeObject.getJSONArray("industryIdentifiers").getJSONObject("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            Snackbar.make(findViewById(R.id.settings_layout), mTitleTextView.getText(), Snackbar.LENGTH_LONG).show();
//            Intent intent = new Intent(SearchActivity.this, CreateBookActivity.class);
//            intent.putExtra("book_id", mBook.getId());
//            startActivity(intent);
        }
    }

    private class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultHolder> {
        private JSONArray mSearchResults;

        public SearchResultsAdapter(JSONArray searchResults) {
            mSearchResults = searchResults;
        }

        @Override
        public SearchResultHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(SearchActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_search_result, parent, false);
            return new SearchResultHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchResultHolder searchResultHolder, int position) {
            JSONObject searchResult = null;
            try {
                searchResult = (JSONObject) mSearchResults.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            searchResultHolder.bindSearchResult(searchResult);
        }

        @Override
        public int getItemCount() {
            return mSearchResults.length();
        }

        public void setBooks(JSONArray searchResults) {
            mSearchResults = searchResults;
        }
    }
}
