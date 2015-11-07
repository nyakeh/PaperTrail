package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReadingListFragment extends Fragment {
    private RecyclerView mBookRecyclerView;
    private ReadingListBookAdapter mReadingListBookAdapter;
    private TextView mBookListEmptyMessageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reading_list, container, false);

        mBookListEmptyMessageView = (TextView) view.findViewById(R.id.book_list_empty_message);
        mBookRecyclerView = (RecyclerView) view.findViewById(R.id.book_recycler_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    private void AddNewBook() {
        Book book = new Book(Constants.QUEUE);
        BookLab.get(getActivity()).addBook(book);

        Intent intent = new Intent(getActivity(), CreateBookActivity.class);
        intent.putExtra("book_id", book.getId());
        intent.putExtra("book_status", Constants.QUEUE);
        startActivity(intent);
    }

    private void updateUI() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getReadingList();

        if (mReadingListBookAdapter == null) {
            mReadingListBookAdapter = new ReadingListBookAdapter(books);
            mBookRecyclerView.setAdapter(mReadingListBookAdapter);
        } else {
            mReadingListBookAdapter.setBooks(books);
            mReadingListBookAdapter.notifyDataSetChanged();
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

    private class ReadingListBookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Book mBook;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;

        public ReadingListBookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_book_author);
        }

        private void bindBook(Book book) {
            mBook = book;
            mTitleTextView.setText(book.getTitle());
            mAuthorTextView.setText(book.getAuthor());
        }

        @Override
        public void onClick(View v) {
            Intent intent = BookActivity.newIntent(getActivity(), mBook.getId());
            startActivity(intent);
        }
    }

    private class ReadingListBookAdapter extends RecyclerView.Adapter<ReadingListBookHolder> {

        private List<Book> mBooks;

        public ReadingListBookAdapter(List<Book> books) {
            mBooks = books;
        }

        @Override
        public ReadingListBookHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_book_reading_list, parent, false);
            return new ReadingListBookHolder(view);
        }

        @Override
        public void onBindViewHolder(ReadingListBookHolder archivedBookHolder, int position) {
            Book book = mBooks.get(position);
            archivedBookHolder.bindBook(book);
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
