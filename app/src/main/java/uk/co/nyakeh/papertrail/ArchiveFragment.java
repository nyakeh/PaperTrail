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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArchiveFragment extends Fragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private RecyclerView mBookRecyclerView;
    private ArchivedBookAdapter mArchivedBookAdapter;
    private TextView mBookListEmptyMessageView;
    private List<Integer> headerPositionList = new ArrayList<>();
    private Map<String, Integer> pageReadSums = new HashMap<>();

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
        List<Book> archivedBooks = bookLab.getArchivedBooks();

        List<Book> bookList = new ArrayList<>();
        headerPositionList = new ArrayList<>();
        if (!archivedBooks.isEmpty()) {
            int position = 1;
            String currentMonth = DateFormat.format(Constants.MONTH_DATE_FORMAT, archivedBooks.get(0).getDateFinished()).toString();
            Book firstBookHeader = new Book(UUID.randomUUID());
            firstBookHeader.setTitle(currentMonth);
            bookList.add(firstBookHeader);
            headerPositionList.add(0);
            int monthlyPageSum = 0;
            for (Iterator<Book> i = archivedBooks.iterator(); i.hasNext(); ) {
                Book book = i.next();
                String bookMonth = DateFormat.format(Constants.MONTH_DATE_FORMAT, book.getDateFinished()).toString();
                if (!bookMonth.equals(currentMonth)) {
                    monthlyPageSum = 0;
                    currentMonth = bookMonth;
                    Book bookHeader = new Book(UUID.randomUUID());
                    bookHeader.setTitle(bookMonth);
                    bookList.add(bookHeader);
                    headerPositionList.add(position);
                    position++;
                }
                bookList.add(book);
                position++;
                monthlyPageSum += book.getLength();
                pageReadSums.put(currentMonth, monthlyPageSum);
            }
        }

        if (mArchivedBookAdapter == null) {
            mArchivedBookAdapter = new ArchivedBookAdapter(bookList);
            mBookRecyclerView.setAdapter(mArchivedBookAdapter);
        } else {
            mArchivedBookAdapter.setBooks(bookList);
            mArchivedBookAdapter.notifyDataSetChanged();
        }

        if (archivedBooks.isEmpty()) {
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
        private TextView mCategoryTextView;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mRatingTextView;

        public ArchivedBookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mCategoryTextView = (TextView) itemView.findViewById(R.id.list_item_book_letter);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_book_author);
            mRatingTextView = (TextView) itemView.findViewById(R.id.list_item_book_rating);
        }

        private void bindBook(Book book) {
            mBook = book;
            String category = (book.getCategory().isEmpty()) ? "" : book.getCategory().substring(0, 1);
            mCategoryTextView.setText(category);
            mTitleTextView.setText(book.getTitle());
            mAuthorTextView.setText(book.getAuthor());
            String rating = (book.getRating() <= 0) ? "" : String.valueOf(book.getRating());
            mRatingTextView.setText(rating);
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
                VHheader.mMonth.setText(book.getTitle());
                VHheader.mPages.setText(pageReadSums.get(book.getTitle()) + " pages");
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
            TextView mMonth;
            TextView mPages;

            public VHHeader(View itemView) {
                super(itemView);
                this.mMonth = (TextView) itemView.findViewById(R.id.list_item_archive_heading_month);
                this.mPages = (TextView) itemView.findViewById(R.id.list_item_archive_heading_pages);
            }
        }
    }
}
