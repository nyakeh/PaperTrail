package uk.co.nyakeh.papertrail;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

public class NoteFragment extends Fragment implements NoteDialogFragmentCallbackInterface {
    private EditText mCreateNoteField;
    private ImageView mCreateNoteEditField;
    private ImageView mCreateNoteSaveField;
    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mNoteAdapter;
    private Book mBook;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_page, container, false);

        mBook = (Book) container.getTag(R.string.book);

        mCreateNoteEditField = (ImageView) view.findViewById(R.id.note_edit);
        mCreateNoteEditField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNoteThroughActivity();
                mCreateNoteField.setText("");
            }
        });

        mCreateNoteSaveField = (ImageView) view.findViewById(R.id.note_save);
        mCreateNoteSaveField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCreateNoteField.getText() != null && !mCreateNoteField.getText().toString().isEmpty()) {
                    Note note = new Note(mBook.getId(), mCreateNoteField.getText().toString());
                    BookLab.get(getActivity()).addNote(note);
                    mCreateNoteField.setText("");
                    mCreateNoteField.clearFocus();
                    updateUI();
                }
            }
        });

        mNoteRecyclerView = (RecyclerView) view.findViewById(R.id.note_recycler_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    public void createNoteThroughActivity() {
        Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
        intent.putExtra("book_id", mBook.getId());
        intent.putExtra("note_content", mCreateNoteField.getText().toString());
        startActivity(intent);
    }

    public void updateUI() {
        List<Note> notes = BookLab.get(getActivity()).getNotes(mBook.getId());

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

    @Override
    public void onFinishCallBack() {
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

            if (note.getTitle() == null || note.getTitle().isEmpty()){
                mTitleTextView.setVisibility(View.GONE);
            }else  {
                mTitleTextView.setVisibility(View.VISIBLE);
                mTitleTextView.setText(note.getTitle());
            }
            mContentTextView.setText(note.getContent());
        }

        @Override
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            NoteDialogFragment noteDialogFragment = new NoteDialogFragment();
            Bundle args = new Bundle();
            args.putString(getString(R.string.note_title), mNote.getTitle());
            args.putString(getString(R.string.note_content), mNote.getContent());
            args.putString(getString(R.string.note), new Gson().toJson(mNote));
            noteDialogFragment.setArguments(args);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                noteDialogFragment.setEnterTransition(new Fade());
            }
            noteDialogFragment.setTargetFragment(NoteFragment.this, 1);
            noteDialogFragment.show(fm, "Note Dialog Fragment");
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
            View view = layoutInflater.inflate(R.layout.list_item_note, parent, false);
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