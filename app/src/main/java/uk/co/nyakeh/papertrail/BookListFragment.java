package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BookListFragment extends Fragment {
    private RecyclerView mBookRecyclerView;
    private BookAdapter mBookAdapter;
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
        BookLab.get(getActivity()).addBook(book);
        Intent intent = CreateBookActivity.newIntent(getActivity(), book.getId(), Constants.READING);
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
            Picasso.with(getActivity())
                    .load(safePicassoImageUrl)
                    .placeholder(R.drawable.books)
                    .error(R.drawable.books)
                    .resize(90, 90)
                    .centerCrop()
                    .into(mImageView);

            mProgressBar.setMax(mBook.getLength());
            mProgressBar.setProgress(mBook.getProgress());
        }

        @Override
        public void onClick(View view) {
            Intent intent = BookActivity.newIntent(getActivity(), mBook.getId());

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<View, String>(view.findViewById(R.id.list_item_book_image), getString(R.string.transition_name_book_image))
            );
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        }
    }

    public class BookAdapter extends RecyclerView.Adapter<BookHolder> {

        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
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
}