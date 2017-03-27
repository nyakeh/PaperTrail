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
import uk.co.nyakeh.papertrail.database.ReadingListOrderCursorWrapper;

import static uk.co.nyakeh.papertrail.database.BookDbSchema.BookTable;
import static uk.co.nyakeh.papertrail.database.ReadingListOrderDbSchema.ReadingListOrderTable;

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
        BookCursorWrapper activeBooksCursor = queryBooks(BookTable.Cols.STATUS + " = ?", new String[]{Constants.READING});
        try {
            activeBooksCursor.moveToFirst();
            while (!activeBooksCursor.isAfterLast()) {
                books.add(activeBooksCursor.getBook());
                activeBooksCursor.moveToNext();
            }
        } finally {
            activeBooksCursor.close();
        }

        Comparator<Book> comparator = new Comparator<Book>() {
            public int compare(Book book1, Book book2) {
                float book1Progress = (float) book1.getProgress() / book1.getLength();
                float book2Progress = (float) book2.getProgress() / book2.getLength();

                if (book2Progress < book1Progress) {
                    return -1;
                } else if (book2Progress > book1Progress) {
                    return 1;
                }
                return  0;
            }
        };
        Collections.sort(books, comparator);

        return books;
    }

    public List<Book> getArchivedBooks() {
        ArrayList<Book> books = new ArrayList<>();
        BookCursorWrapper archivedBooksCursor = queryBooks(BookTable.Cols.STATUS + " = ?", new String[]{Constants.ARCHIVE});

        try {
            archivedBooksCursor.moveToFirst();
            while (!archivedBooksCursor.isAfterLast()) {
                books.add(archivedBooksCursor.getBook());
                archivedBooksCursor.moveToNext();
            }
        } finally {
            archivedBooksCursor.close();
        }

        Comparator<Book> comparator = new Comparator<Book>() {
            public int compare(Book book1, Book book2) {
                if (book2.getDateFinished().before(book1.getDateFinished())) {
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
        BookCursorWrapper queuedBooksCursor = queryBooks(BookTable.Cols.STATUS + " = ?", new String[]{Constants.QUEUE});
        try {
            queuedBooksCursor.moveToFirst();
            while (!queuedBooksCursor.isAfterLast()) {
                books.add(queuedBooksCursor.getBook());
                queuedBooksCursor.moveToNext();
            }
        } finally {
            queuedBooksCursor.close();
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

    private ReadingListOrderCursorWrapper queryReadingListOrder() {
        return new ReadingListOrderCursorWrapper(mDatabase.query(ReadingListOrderTable.NAME, null, null, null, null, null, null));
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

    public Statistics getBookStatistics() {
        int totalPagesRead = 0;
        int averagePageCount = 0;
        float averageRating = 0.0f;
        String mostReadCategory = "";
        int totalBooksRead = 0;

        Cursor cursor = mDatabase.rawQuery("SELECT SUM("+ BookTable.Cols.LENGTH +"), AVG(" + BookTable.Cols.LENGTH + "), AVG(" + BookTable.Cols.RATING + "), COUNT(1) FROM " + BookTable.NAME + " WHERE " + BookTable.Cols.STATUS + " = ?", new String[] { Constants.ARCHIVE });
        try {
            cursor.moveToFirst();
            totalPagesRead = cursor.getInt(0);
            averagePageCount = cursor.getInt(1);
            averageRating = cursor.getFloat(2);
            totalBooksRead = cursor.getInt(3);
        } finally {
            cursor.close();
        }

        Cursor modeCategoryCursor = mDatabase.rawQuery("SELECT " + BookTable.Cols.CATEGORY + ", COUNT(" + BookTable.Cols.CATEGORY + ") AS category_occurrence FROM " + BookTable.NAME + " GROUP BY " + BookTable.Cols.CATEGORY + " ORDER BY category_occurrence DESC LIMIT 1", null);
        try {
            modeCategoryCursor.moveToFirst();
            mostReadCategory = modeCategoryCursor.getString(0);
        } finally {
            modeCategoryCursor.close();
        }

        return new Statistics(totalPagesRead, averagePageCount, averageRating, mostReadCategory, totalBooksRead);
    }
}