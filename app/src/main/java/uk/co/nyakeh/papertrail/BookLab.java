package uk.co.nyakeh.papertrail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import uk.co.nyakeh.papertrail.database.BookBaseHelper;
import uk.co.nyakeh.papertrail.database.BookCursorWrapper;

import static uk.co.nyakeh.papertrail.database.BookDbSchema.BookTable;

public class BookLab {
    private static BookLab sBookLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private BookLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new BookBaseHelper(mContext).getWritableDatabase();
    }

    public static BookLab get(Context context) {
        if (sBookLab == null) {
            sBookLab = new BookLab(context);
        }
        return sBookLab;
    }

    public void addBook(Book book) {
        if (getBook(book.getId()) == null) {
            ContentValues values = getBookContentValues(book);
            mDatabase.insert(BookTable.NAME, null, values);
        } else {
            updateBook(book);
        }
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

        Comparator<Book> comparator = new Comparator<Book>() {
            public int compare(Book book1, Book book2) {
                if (book2.getDateFinished().before(book1.getDateFinished())){
                    return -1;
                } else if (book2.getDateFinished().equals(book1.getDateFinished())) {
                    return 0;
                }
                return 1;
            }
        };
        Collections.sort(books, comparator);

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

        Collections.sort(books, new Comparator<Book>() {
            @Override
            public int compare(Book book1, Book book2) {
                return book1.getDateStarted().compareTo(book2.getDateStarted());
            }
        });

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
        ContentValues values = getBookContentValues(book);
        mDatabase.update(BookTable.NAME, values, BookTable.Cols.ID + " = ?", new String[]{uuidString});
    }

    private static ContentValues getBookContentValues(Book book) {
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
        values.put(BookTable.Cols.ISBN, book.getISBN());
        values.put(BookTable.Cols.DESCRIPTION, book.getDescription());
        values.put(BookTable.Cols.RATING, book.getRating());
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

    public void deleteBook(UUID id) {
        mDatabase.delete(BookTable.NAME, BookTable.Cols.ID + " = ?", new String[]{id.toString()});
    }

    public String getBackupData() {
        String header = "<table><tr><th>Started</th><th>Finished</th><th>Title</th><th>Author</th><th>Category</th><th>Status</th><th>Rating</th><th>Progress</th><th>Length</th><th>Description</th><th>ISBN</th><th>ImageUrl</th><th>Id</th></tr>";
        String csvData = "";

        BookCursorWrapper cursor = queryBooks(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String book = cursor.getBooksAsHtml();
                csvData += book;
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return header + csvData + "</table>";
    }
}