package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BookListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mBookRecyclerView;
    private BookAdapter mBookAdapter;
    private boolean mSubtitleVisable;
    private TextView mBookListEmptyMessageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        mBookListEmptyMessageView = (TextView) view.findViewById(R.id.book_list_empty_message);
        mBookRecyclerView = (RecyclerView) view.findViewById(R.id.book_recycler_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisable = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewBook();
            }
        });

        updateUI();
        return view;
    }

    private void updateUI() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getBooks();

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_book:
                AddNewBook();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void AddNewBook() {
        Book book = new Book();
        BookLab.get(getActivity()).addBook(book);
        Intent intent = CreateBookActivity.newIntent(getActivity(), book.getId());
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisable);
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Book mBook;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mStartedTextView;

        public BookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title_text_view);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_book_author_text_view);
            mStartedTextView = (TextView) itemView.findViewById(R.id.list_item_book_started_text_view);
        }

        private void bindBook(Book book) {
            mBook = book;
            mTitleTextView.setText(book.getTitle());
            mAuthorTextView.setText(book.getAuthor());

            String formattedStartDate = DateFormat.format("EEEE, MMM dd", mBook.getDateStarted()).toString();
            mStartedTextView.setText(formattedStartDate);
        }

        @Override
        public void onClick(View v) {
            Intent intent = BookActivity.newIntent(getActivity(), mBook.getId());
            startActivity(intent);
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {

        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_book, parent, false);

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
}
