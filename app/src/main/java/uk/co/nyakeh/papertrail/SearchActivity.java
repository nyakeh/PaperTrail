package uk.co.nyakeh.papertrail;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

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
import java.net.URLEncoder;
import java.util.Iterator;

public class SearchActivity extends AppCompatActivity {
    private String mBookCreationStatus;
    private String mSearchQuery;
    private RecyclerView mSearchResultsRecyclerView;
    private SearchResultsAdapter mSearchResultsAdapter;
    private SearchRecentSuggestions mRecentSuggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
    private FloatingActionButton mShowMoreResultsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleIntent(getIntent());

        Bundle extras = getIntent().getExtras();
        mBookCreationStatus = extras.getString(Constants.ARG_BOOK_CREATION_STATUS);
        mSearchResultsRecyclerView = (RecyclerView) findViewById(R.id.search_results);
        mSearchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mSearchResultsRecyclerView.addItemDecoration(itemDecoration);

        if (mSearchResultsAdapter == null) {
            mSearchResultsAdapter = new SearchResultsAdapter(new JSONArray());
            mSearchResultsRecyclerView.setAdapter(mSearchResultsAdapter);
        }

        Button mManualAddBookButton = (Button) findViewById(R.id.search_manual_add);
        mManualAddBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book book = new Book(mBookCreationStatus);
                Intent intent = new Intent(SearchActivity.this, CreateBookActivity.class);
                intent.putExtra(Constants.ARG_NEW_BOOK, new Gson().toJson(book));
                startActivity(intent);
            }
        });

        mShowMoreResultsButton = (FloatingActionButton) findViewById(R.id.search_show_more);
        mShowMoreResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BookSearch().execute(mSearchQuery, "20");
                mShowMoreResultsButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchQuery = intent.getStringExtra(SearchManager.QUERY);
            mSearchResultsAdapter.setBooks(new JSONArray());
            mSearchResultsAdapter.notifyDataSetChanged();
            new BookSearch().execute(mSearchQuery, "0");
            mRecentSuggestions.saveRecentQuery(mSearchQuery, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        searchItem.expandActionView();
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        return true;
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

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    private class BookSearch extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String xml = "";
            URL url;
            HttpURLConnection urlConnection;
            String queryString = params[0];
            String startIndex = params[1];
            try {
                url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + URLEncoder.encode(queryString, "UTF-8") + "&filter=ebooks&maxResults=20&printType=books&showPreorders=true&startIndex=" + startIndex + "&key=" + getString(R.string.google_books_api_key));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
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
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return xml;
        }

        @Override
        protected void onPostExecute(String result) {
            //Log.d("Raw results: ", result);
            if (result.equals("")) {
                //getSupportActionBar().setSubtitle("Sorry, we failed to retrieve any results.");
                mSearchResultsAdapter.setBooks(new JSONArray());
                mSearchResultsAdapter.notifyDataSetChanged();
            } else {
                JSONObject resultObject;
                try {
                    resultObject = new JSONObject(result);
                    JSONArray bookArray = resultObject.getJSONArray("items");
                    mSearchResultsAdapter.addBooks(bookArray);
                    mSearchResultsAdapter.notifyDataSetChanged();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private class SearchResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Book mSearchResult;
        private ImageView mImageView;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mIsbnTextView;

        public SearchResultHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mImageView = (ImageView) itemView.findViewById(R.id.list_item_book_image);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_book_author);
            mIsbnTextView = (TextView) itemView.findViewById(R.id.list_item_book_date_finished);
        }

        private void bindSearchResult(JSONObject book) {
            String title = "";
            StringBuilder authorBuilder = new StringBuilder("");
            String isbn = "";
            String imageUrl = "";
            int pageCount = 100;
            String description = "";
            try {
                JSONObject volumeObject = book.getJSONObject("volumeInfo");
                try {
                    title = volumeObject.getString("title");
                } catch (Exception e) {
                }

                try {
                    JSONArray authorArray = volumeObject.getJSONArray("authors");
                    for (int i = 0; i < authorArray.length(); i++) {
                        if (i > 0) {
                            authorBuilder.append(", ");
                        }
                        authorBuilder.append(authorArray.getString(i));
                    }
                } catch (Exception e) {
                }

                try {
                    JSONArray isbnArray = volumeObject.getJSONArray("industryIdentifiers");
                    for (int i = 0; i < isbnArray.length(); i++) {
                        JSONObject isbnObject = isbnArray.getJSONObject(i);
                        if (isbnObject.getString("type").equals("ISBN_10")) {
                            isbn = isbnObject.getString("identifier");
                        }
                    }
                } catch (Exception e) {
                }

                try {
                    imageUrl = volumeObject.getJSONObject("imageLinks").getString("thumbnail");
                    String safePicassoImageUrl = (imageUrl.isEmpty()) ? "fail_gracefully_pls" : imageUrl;
                    Picasso.with(SearchActivity.this)
                            .load(safePicassoImageUrl)
                            .placeholder(R.drawable.books)
                            .error(R.drawable.books)
                            .into(mImageView);
                } catch (Exception e) {
                }

                try {
                    String pageCountString = volumeObject.getString("pageCount");
                    if (!pageCountString.isEmpty()) {
                        pageCount = Integer.parseInt(pageCountString);
                    }
                } catch (Exception e) {
                }

                try {
                    description = volumeObject.getString("description");
                } catch (Exception e) {
                }
                mSearchResult = new Book(mBookCreationStatus, title, authorBuilder.toString(), isbn, pageCount, imageUrl, description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mTitleTextView.setText(mSearchResult.getTitle());
            mAuthorTextView.setText(mSearchResult.getAuthor());
            mIsbnTextView.setText(mSearchResult.getISBN());
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SearchActivity.this, CreateBookActivity.class);
            intent.putExtra(Constants.ARG_NEW_BOOK, new Gson().toJson(mSearchResult));
            startActivity(intent);
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
            } catch (Exception exception) {
                exception.printStackTrace();
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

        public void addBooks(JSONArray searchResults) throws JSONException {
            if (mSearchResults.length() == 0){
                mShowMoreResultsButton.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < searchResults.length(); i++) {
                mSearchResults.put(searchResults.getJSONObject(i)) ;
            }
        }
    }
}