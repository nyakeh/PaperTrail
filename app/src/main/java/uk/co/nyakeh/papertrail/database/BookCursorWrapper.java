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
        String uuidString = getString(getColumnIndex(BookTable.Cols.UUID));
        String title = getString(getColumnIndex(BookTable.Cols.TITLE));
        String author = getString(getColumnIndex(BookTable.Cols.AUTHOR));
        int progress = getInt(getColumnIndex(BookTable.Cols.PROGRESS));
        int length = getInt(getColumnIndex(BookTable.Cols.LENGTH));
        Long dateStarted = getLong(getColumnIndex(BookTable.Cols.DATE_STARTED));
        Long dateFinished = getLong(getColumnIndex(BookTable.Cols.DATE_FINISHED));
        String imageUrl = getString(getColumnIndex(BookTable.Cols.IMAGE_URL));
        String category = getString(getColumnIndex(BookTable.Cols.CATEGORY));

        Book book = new Book(UUID.fromString(uuidString));
        book.setTitle(title);
        book.setAuthor(author);
        book.setProgress(progress);
        book.setLength(length);
        book.setDateStarted(new Date(dateStarted));
        book.setDateFinished(new Date(dateFinished));
        book.setImageUrl(imageUrl);
        book.setCategory(category);
        return book;
    }
}