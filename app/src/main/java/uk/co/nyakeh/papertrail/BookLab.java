package uk.co.nyakeh.papertrail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
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

    public void addBook(Book book){
        ContentValues values = getContentValues(book);
        mDatabase.insert(BookTable.NAME, null, values);
    }

    public List<Book> getBooks() {
        ArrayList<Book> books = new ArrayList<>();
        BookCursorWrapper cursor = queryBooks(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return books;
    }

    public Book getBook(UUID id) {

        BookCursorWrapper cursor = queryBooks(BookTable.Cols.UUID + " = ?", new String[]{id.toString()});

        try {
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getBook();
        } finally {
            cursor.close();
        }
    }

    public void updateBook(Book book){
        String uuidString = book.getId().toString();
        ContentValues values = getContentValues(book);
        mDatabase.update(BookTable.NAME, values, BookTable.Cols.UUID + " = ?", new String[] { uuidString });
    }

    private static ContentValues getContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookTable.Cols.UUID, book.getId().toString());
        values.put(BookTable.Cols.TITLE, book.getTitle());
        values.put(BookTable.Cols.AUTHOR, book.getAuthor());
        values.put(BookTable.Cols.BLURB, book.getBlurb());
        values.put(BookTable.Cols.LENGTH, book.getLength());
        values.put(BookTable.Cols.DATE_STARTED, book.getDateStarted().getTime());
        values.put(BookTable.Cols.DATE_FINISHED, book.getDateFinished().getTime());
        values.put(BookTable.Cols.ISBN, book.getISBN());
        values.put(BookTable.Cols.IMAGE_URL, book.getImageUrl());
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
        mDatabase.delete(BookTable.NAME, BookTable.Cols.UUID + " = ?", new String[] { id.toString() });
    }
}