package uk.co.nyakeh.papertrail.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import uk.co.nyakeh.papertrail.Note;

import static uk.co.nyakeh.papertrail.database.NoteDbSchema.NoteTable;

public class NoteCursorWrapper extends CursorWrapper {
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Note getNote() {
        String id = getString(getColumnIndex(NoteTable.Cols.ID));
        String bookId = getString(getColumnIndex(NoteTable.Cols.BOOK_ID));
        String title = getString(getColumnIndex(NoteTable.Cols.TITLE));
        String content = getString(getColumnIndex(NoteTable.Cols.CONTENT));
        Long created = getLong(getColumnIndex(NoteTable.Cols.CREATED));
        Long updated = getLong(getColumnIndex(NoteTable.Cols.UPDATED));

        Note note = new Note(UUID.fromString(id));
        note.setBookId(UUID.fromString(bookId));
        note.setTitle(title);
        note.setContent(content);
        note.setCreated(new Date(created));
        note.setUpdated(new Date(updated));
        return note;
    }
}