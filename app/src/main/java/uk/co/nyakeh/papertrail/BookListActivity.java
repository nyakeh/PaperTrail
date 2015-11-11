package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BookListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mBookRecyclerView;
    private BookAdapter mBookAdapter;
    private TextView mBookListEmptyMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode transition = new Explode();
            getWindow().setEnterTransition(transition);
            getWindow().setExitTransition(transition);
        }
        setContentView(R.layout.fragment_book_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mBookListEmptyMessageView = (TextView) findViewById(R.id.book_list_empty_message);
        mBookRecyclerView = (RecyclerView) findViewById(R.id.book_recycler_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewBook();
            }
        });

        updateUI();
    }

    private void updateUI() {
        BookLab bookLab = BookLab.get(this);
        List<Book> books = bookLab.getActiveBooks();

        if (mBookAdapter == null) {
            mBookAdapter = new BookAdapter(books);
            mBookRecyclerView.setAdapter(mBookAdapter);
        } else {
            mBookAdapter.setBooks(books);
            mBookAdapter.notifyDataSetChanged();
        }

        if (books.isEmpty()) {
            mBookRecyclerView.setVisibility(View.GONE);
            mBookListEmptyMessageView.setVisibility(View.VISIBLE);
        } else {
            mBookRecyclerView.setVisibility(View.VISIBLE);
            mBookListEmptyMessageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void AddNewBook() {
        Book book = new Book(Constants.READING);
        BookLab.get(this).addBook(book);
        Intent intent = new Intent(BookListActivity.this, CreateBookActivity.class);
        intent.putExtra("book_id", book.getId());
        intent.putExtra("book_status", Constants.READING);
        startActivity(intent);
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Book mBook;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mProgressTextView;
        private ImageView mImageView;
        private ProgressBar mProgressBar;

        public BookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_book_author);
            mProgressTextView = (TextView) itemView.findViewById(R.id.list_item_book_progress);
            mImageView = (ImageView) itemView.findViewById(R.id.list_item_book_image);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.list_item_book_progress_bar);
        }

        private void bindBook(Book book) {
            mBook = book;
            mTitleTextView.setText(book.getTitle());
            mAuthorTextView.setText(book.getAuthor());

            float progressAsPercentage = (float) book.getProgress() / book.getLength();
            String s = String.format("%.0f", (progressAsPercentage * 100));
            mProgressTextView.setText(s);

            String safePicassoImageUrl = (book.getImageUrl().isEmpty()) ? "fail_gracefully_pls" : book.getImageUrl();
            Picasso.with(BookListActivity.this)
                    .load(safePicassoImageUrl)
                    .placeholder(R.drawable.books)
                    .error(R.drawable.books)
                    .into(mImageView);

            mProgressBar.setMax(mBook.getLength());
            mProgressBar.setProgress(mBook.getProgress());
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(BookListActivity.this, BookActivity.class);
            intent.putExtra("book_id", mBook.getId());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                String transitionName = getString(R.string.transition_book_cover);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(BookListActivity.this, mImageView, transitionName);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        }
    }

    public class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(BookListActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_book_active, parent, false);
            return new BookHolder(view);
        }

        @Override
        public void onBindViewHolder(BookHolder bookHolder, int position) {
            Book book = mBooks.get(position);
            bookHolder.bindBook(book);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        public void setBooks(List<Book> books) {
            mBooks = books;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Intent intent = new Intent(this, ArchiveActivity.class);
        if (itemId == R.id.nav_currently_reading) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.nav_reading_list) {
            intent = new Intent(this, ReadingListActivity.class);
        } else if (itemId == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
        }
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }
}