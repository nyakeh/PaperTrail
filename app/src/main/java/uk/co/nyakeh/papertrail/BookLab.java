package uk.co.nyakeh.papertrail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import uk.co.nyakeh.papertrail.database.BookCursorWrapper;
import uk.co.nyakeh.papertrail.database.DbBaseHelper;
import uk.co.nyakeh.papertrail.database.NoteCursorWrapper;

import static uk.co.nyakeh.papertrail.database.BookDbSchema.BookTable;
import static uk.co.nyakeh.papertrail.database.NoteDbSchema.NoteTable;

public class BookLab {
    private static BookLab sBookLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private BookLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DbBaseHelper(mContext).getWritableDatabase();
    }

    public static BookLab get(Context context) {
        if (sBookLab == null) {
            sBookLab = new BookLab(context);
        }
        return sBookLab;
    }

    public void addBook(Book book) {
        ContentValues values = getContentValues(book);
        mDatabase.insert(BookTable.NAME, null, values);
    }

    public List<Book> getActiveBooks() {
        ArrayList<Book> books = new ArrayList<>();
        BookCursorWrapper cursor = queryBooks(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Book book = cursor.getBook();
                if (book.getStatus().equals(Constants.READING)) {
                    books.add(book);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return books;
    }

    public List<Book> getArchivedBooks() {
        ArrayList<Book> books = new ArrayList<>();
        BookCursorWrapper cursor = queryBooks(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Book book = cursor.getBook();
                if (book.getStatus().equals(Constants.ARCHIVE)) {
                    books.add(book);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return books;
    }

    public List<Book> getReadingList() {
        ArrayList<Book> books = new ArrayList<>();
        BookCursorWrapper cursor = queryBooks(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Book book = cursor.getBook();
                if (book.getStatus().equals(Constants.QUEUE)) {
                    books.add(book);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return books;
    }

    public Book getBook(UUID id) {
        BookCursorWrapper cursor = queryBooks(BookTable.Cols.ID + " = ?", new String[]{id.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getBook();
        } finally {
            cursor.close();
        }
    }

    public void updateBook(Book book) {
        String uuidString = book.getId().toString();
        ContentValues values = getContentValues(book);
        mDatabase.update(BookTable.NAME, values, BookTable.Cols.ID + " = ?", new String[]{uuidString});
    }

    private static ContentValues getContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookTable.Cols.ID, book.getId().toString());
        values.put(BookTable.Cols.TITLE, book.getTitle());
        values.put(BookTable.Cols.AUTHOR, book.getAuthor());
        values.put(BookTable.Cols.PROGRESS, book.getProgress());
        values.put(BookTable.Cols.LENGTH, book.getLength());
        values.put(BookTable.Cols.STARTED, book.getDateStarted().getTime());
        values.put(BookTable.Cols.FINISHED, book.getDateFinished().getTime());
        values.put(BookTable.Cols.IMAGE_URL, book.getImageUrl());
        values.put(BookTable.Cols.CATEGORY, book.getCategory());
        values.put(BookTable.Cols.STATUS, book.getStatus());
        return values;
    }

    private BookCursorWrapper queryBooks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(BookTable.NAME,
                null,  // Columns - null selects *
                whereClause,
                whereArgs,
                null,  // groupBy
                null,  // having
                null); // orderBy

        return new BookCursorWrapper(cursor);
    }

    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(NoteTable.NAME,
                null,  // Columns - null selects *
                whereClause,
                whereArgs,
                null,  // groupBy
                null,  // having
                null); // orderBy

        return new NoteCursorWrapper(cursor);
    }

    public void deleteBook(UUID id) {
        mDatabase.delete(BookTable.NAME, BookTable.Cols.ID + " = ?", new String[]{id.toString()});
    }

    public List<Note> getNotes(UUID bookId) {
        ArrayList<Note> notes = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(NoteTable.Cols.BOOK_ID + " = ?", new String[]{bookId.toString()});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = cursor.getNote();
                notes.add(note);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }
}