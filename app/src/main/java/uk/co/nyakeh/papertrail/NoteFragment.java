package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class NoteFragment extends Fragment {
    private EditText mCreateField;
    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mNoteAdapter;
    private Book mBook;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_page, container, false);

        mBook = (Book) container.getTag(R.string.book);

        mCreateField = (EditText) view.findViewById(R.id.note_create);
        mCreateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CreateNoteActivity.newIntent(getActivity(), mBook.getId());
                startActivity(intent);
            }
        });

        mNoteRecyclerView = (RecyclerView) view.findViewById(R.id.note_recycler_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Note> notes = bookLab.getNotes(mBook.getId());

        if (mNoteAdapter == null) {
            mNoteAdapter = new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mNoteAdapter);
        } else {
            mNoteAdapter.setNotes(notes);
            mNoteAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }


    private class ReadingListBookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Note mNote;
        private TextView mTitleTextView;
        private TextView mContentTextView;

        public ReadingListBookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title);
            mContentTextView = (TextView) itemView.findViewById(R.id.list_item_book_author);
        }

        private void bindNote(Note note) {
            mNote = note;
            mTitleTextView.setText(note.getTitle());
            mContentTextView.setText(note.getContent());
        }

        @Override
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            NoteDialogFragment dialogFragment = new NoteDialogFragment();
            dialogFragment.show(fm, "Sample Fragment");
        }
    }

    private class NoteAdapter extends RecyclerView.Adapter<ReadingListBookHolder> {

        private List<Note> mNotes;

        public NoteAdapter(List<Note> notes) {
            mNotes = notes;
        }

        @Override
        public ReadingListBookHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_book_reading_list, parent, false);
            return new ReadingListBookHolder(view);
        }

        @Override
        public void onBindViewHolder(ReadingListBookHolder archivedBookHolder, int position) {
            Note note = mNotes.get(position);
            archivedBookHolder.bindNote(note);
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        public void setNotes(List<Note> notes) {
            mNotes = notes;
        }
    }
}