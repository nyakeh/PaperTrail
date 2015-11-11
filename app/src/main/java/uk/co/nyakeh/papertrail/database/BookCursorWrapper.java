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

    public String getBooksAsHtml() {
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

        String bookAsString = "<tr><td>";

        bookAsString += id + "</td><td>";
        bookAsString += title + "</td><td>";
        bookAsString += author + "</td><td>";
        bookAsString += category + "</td><td>";
        bookAsString += status + "</td><td>";
        bookAsString += new Date(dateStarted) + "</td><td>";
        Date finished = new Date(dateFinished);
        if (finished.equals(new Date(Long.MAX_VALUE))) {
            bookAsString += "</td><td>";
        }else {
            bookAsString += finished + "</td><td>";
        }
        bookAsString += progress + "</td><td>";
        bookAsString += length + "</td><td>";
        bookAsString += imageUrl + "</td></tr>";
        return bookAsString;
    }
}