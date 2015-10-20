package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ReadingListFragment extends Fragment {
    private RecyclerView mBookRecyclerView;
    private ArchivedBookAdapter mArchivedBookAdapter;
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

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mBookRecyclerView.addItemDecoration(itemDecoration);

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
        Intent intent = CreateBookActivity.newIntent(getActivity(), book.getId(), Constants.QUEUE);
        startActivity(intent);
    }

    private void updateUI() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getReadingList();

        if (mArchivedBookAdapter == null) {
            mArchivedBookAdapter = new ArchivedBookAdapter(books);
            mBookRecyclerView.setAdapter(mArchivedBookAdapter);
        } else {
            mArchivedBookAdapter.setBooks(books);
            mArchivedBookAdapter.notifyDataSetChanged();
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

    private class ArchivedBookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Book mBook;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mDateFinishedTextView;
        private ImageView mImageView;

        public ArchivedBookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_book_author);
            mDateFinishedTextView = (TextView) itemView.findViewById(R.id.list_item_book_date_finished);
            mImageView = (ImageView) itemView.findViewById(R.id.list_item_book_image);
        }

        private void bindBook(Book book) {
            mBook = book;
            mTitleTextView.setText(book.getTitle());
            mAuthorTextView.setText(book.getAuthor());
            String formattedFinishedDate = DateFormat.format("EEEE, MMM dd, yyyy", mBook.getDateFinished()).toString();
            mDateFinishedTextView.setText(formattedFinishedDate);

            String safePicassoImageUrl = (book.getImageUrl().isEmpty()) ? "fail_gracefully_pls" : book.getImageUrl();
            Picasso.with(getActivity())
                    .load(safePicassoImageUrl)
                    .placeholder(R.drawable.books)
                    .error(R.drawable.books)
                    .resize(90, 90)
                    .centerCrop()
                    .into(mImageView);
        }

        @Override
        public void onClick(View v) {
            Intent intent = BookActivity.newIntent(getActivity(), mBook.getId());
            startActivity(intent);
        }
    }

    private class ArchivedBookAdapter extends RecyclerView.Adapter<ArchivedBookHolder> {

        private List<Book> mBooks;

        public ArchivedBookAdapter(List<Book> books) {
            mBooks = books;
        }

        @Override
        public ArchivedBookHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_book_archived, parent, false);
            return new ArchivedBookHolder(view);
        }

        @Override
        public void onBindViewHolder(ArchivedBookHolder archivedBookHolder, int position) {
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
