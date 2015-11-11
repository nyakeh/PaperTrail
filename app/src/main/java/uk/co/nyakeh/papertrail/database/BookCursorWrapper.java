package uk.co.nyakeh.papertrail.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import uk.co.nyakeh.papertrail.Book;
import static uk.co.nyakeh.papertrail.database.BookDbSchema.BookTable;

public class BookCursorWrapper extends CursorWrapper {
    public BookCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Book getBook() {
        String id = getString(getColumnIndex(BookTable.Cols.ID));
        String title = getString(getColumnIndex(BookTable.Cols.TITLE));
        String author = getString(getColumnIndex(BookTable.Cols.AUTHOR));
        int progress = getInt(getColumnIndex(BookTable.Cols.PROGRESS));
        int length = getInt(getColumnIndex(BookTable.Cols.LENGTH));
        Long dateStarted = getLong(getColumnIndex(BookTable.Cols.STARTED));
        Long dateFinished = getLong(getColumnIndex(BookTable.Cols.FINISHED));
        String imageUrl = getString(getColumnIndex(BookTable.Cols.IMAGE_URL));
        String category = getString(getColumnIndex(BookTable.Cols.CATEGORY));
        String status = getString(getColumnIndex(BookTable.Cols.STATUS));

        Book book = new Book(UUID.fromString(id));
        book.setTitle(title);
        book.setAuthor(author);
        book.setProgress(progress);
        book.setLength(length);
        book.setDateStarted(new Date(dateStarted));
        book.setDateFinished(new Date(dateFinished));
        book.setImageUrl(imageUrl);
        book.setCategory(category);
        book.setStatus(status);
        return book;
    }

    public String getBookAsString() {
        String id = getString(getColumnIndex(BookTable.Cols.ID));
        String title = getString(getColumnIndex(BookTable.Cols.TITLE));
        String author = getString(getColumnIndex(BookTable.Cols.AUTHOR));
        int progress = getInt(getColumnIndex(BookTable.Cols.PROGRESS));
        int length = getInt(getColumnIndex(BookTable.Cols.LENGTH));
        Long dateStarted = getLong(getColumnIndex(BookTable.Cols.STARTED));
        Long dateFinished = getLong(getColumnIndex(BookTable.Cols.FINISHED));
        String imageUrl = getString(getColumnIndex(BookTable.Cols.IMAGE_URL));
        String category = getString(getColumnIndex(BookTable.Cols.CATEGORY));
        String status = getString(getColumnIndex(BookTable.Cols.STATUS));

        String bookAsString = "";

        bookAsString += id + ",";
        bookAsString += title + ",";
        bookAsString += author + ",";
        bookAsString += category + ",";
        bookAsString += status + ",";
        bookAsString += progress + ",";
        bookAsString += length + ",";
        bookAsString += new Date(dateStarted) + ",";
        bookAsString += new Date(dateFinished) + ",";
        bookAsString += imageUrl + ",\\r\\n,";
        return bookAsString;
    }
}