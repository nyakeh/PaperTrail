package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArchiveFragment extends Fragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private RecyclerView mBookRecyclerView;
    private ArchivedBookAdapter mArchivedBookAdapter;
    private TextView mBookListEmptyMessageView;
    private List<Integer> headerPositionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archive, container, false);

        mBookListEmptyMessageView = (TextView) view.findViewById(R.id.book_list_empty_message);
        mBookRecyclerView = (RecyclerView) view.findViewById(R.id.book_recycler_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mBookRecyclerView.addItemDecoration(itemDecoration);

        updateUI();
        return view;
    }

    private void updateUI() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getArchivedBooks();

        List<Book> bookList = new ArrayList<>();
        headerPositionList = new ArrayList<>();

        if (!books.isEmpty()) {
            int position = 1;
            String currentMonth = DateFormat.format(Constants.MONTH_DATE_FORMAT, books.get(0).getDateFinished()).toString();
            Book book1 = new Book("");
            book1.setTitle(currentMonth);
            bookList.add(book1);
            headerPositionList.add(0);
            for (Iterator<Book> i = books.iterator(); i.hasNext(); ) {
                Book book = i.next();
                String bookMonth = DateFormat.format(Constants.MONTH_DATE_FORMAT, book.getDateFinished()).toString();
                if (!bookMonth.equals(currentMonth)){
                    currentMonth = bookMonth;
                    Book bookHeader = new Book("");
                    bookHeader.setTitle(bookMonth);
                    bookList.add(bookHeader);
                    headerPositionList.add(position);
                    position++;
                }
                bookList.add(book);
                position++;
            }
        }
        if (mArchivedBookAdapter == null) {
            mArchivedBookAdapter = new ArchivedBookAdapter(bookList);
            mBookRecyclerView.setAdapter(mArchivedBookAdapter);
        } else {
            mArchivedBookAdapter.setBooks(bookList);
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
        private TextView mLetterTextView;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mDateFinishedTextView;

        public ArchivedBookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mLetterTextView = (TextView) itemView.findViewById(R.id.list_item_book_letter);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_book_author);
            mDateFinishedTextView = (TextView) itemView.findViewById(R.id.list_item_book_date_finished);
        }

        private void bindBook(Book book) {
            mBook = book;
            String letter = (book.getCategory().isEmpty()) ? "" : book.getCategory().substring(0, 1);
            mLetterTextView.setText(letter);
            mTitleTextView.setText(book.getTitle());
            mAuthorTextView.setText(book.getAuthor());
            String formattedFinishedDate = DateFormat.format(Constants.DISPLAY_DATE_FORMAT, mBook.getDateFinished()).toString();
            mDateFinishedTextView.setText(formattedFinishedDate);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), BookActivity.class);
            intent.putExtra(Constants.ARG_BOOK_ID, mBook.getId());
            startActivity(intent);
        }
    }

    private class ArchivedBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Book> mBooks;

        public ArchivedBookAdapter(List<Book> books) {
            mBooks = books;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if (viewType == TYPE_HEADER) {
                View view = layoutInflater.from(parent.getContext()).inflate(R.layout.list_item_archive_heading, parent, false);
                return new VHHeader(view);
            } else {
                View view = layoutInflater.inflate(R.layout.list_item_book_archived, parent, false);
                return new ArchivedBookHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Book book = mBooks.get(position);
            if (holder instanceof VHHeader) {
                VHHeader VHheader = (VHHeader) holder;
                VHheader.mHeading.setText("hello");
            } else if (holder instanceof ArchivedBookHolder) {
                ArchivedBookHolder archivedBookHolder = (ArchivedBookHolder) holder;
                archivedBookHolder.bindBook(book);
            }
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        public void setBooks(List<Book> books) {
            mBooks = books;
        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position))
                return TYPE_HEADER;
            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return headerPositionList.contains(position);
        }

        class VHHeader extends RecyclerView.ViewHolder {
            TextView mHeading;

            public VHHeader(View itemView) {
                super(itemView);
                this.mHeading = (TextView) itemView.findViewById(R.id.list_item_archive_heading);
            }
        }
    }
}
