package uk.co.nyakeh.papertrail.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import uk.co.nyakeh.papertrail.Book;

import static uk.co.nyakeh.papertrail.database.BookDbSchema.BookTable;

public class BookCursorWrapper extends CursorWrapper {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

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
        String isbn = getString(getColumnIndex(BookTable.Cols.ISBN));
        String description = getString(getColumnIndex(BookTable.Cols.DESCRIPTION));

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
        book.setISBN(isbn);
        book.setDescription(description);
        return book;
    }

    public String getBooksAsHtml() {
        String id = getString(getColumnIndex(BookTable.Cols.ID));
        String title = getString(getColumnIndex(BookTable.Cols.TITLE));
        String author = getString(getColumnIndex(BookTable.Cols.AUTHOR));
        int progress = getInt(getColumnIndex(BookTable.Cols.PROGRESS));
        int length = getInt(getColumnIndex(BookTable.Cols.LENGTH));
        Date dateStarted = new Date(getLong(getColumnIndex(BookTable.Cols.STARTED)));
        Date dateFinished = new Date(getLong(getColumnIndex(BookTable.Cols.FINISHED)));
        String imageUrl = getString(getColumnIndex(BookTable.Cols.IMAGE_URL));
        String category = getString(getColumnIndex(BookTable.Cols.CATEGORY));
        String status = getString(getColumnIndex(BookTable.Cols.STATUS));
        String isbn = getString(getColumnIndex(BookTable.Cols.ISBN));
        String description = getString(getColumnIndex(BookTable.Cols.DESCRIPTION));

        String finishedString = "";
        if (!dateFinished.equals(new Date(Long.MAX_VALUE))) {
            finishedString = DATE_FORMAT.format(dateFinished);
        }

        String bookAsString = "<tr><td>";
        bookAsString += DATE_FORMAT.format(dateStarted) + "</td><td>";
        bookAsString += finishedString + "</td><td>";
        bookAsString += title + "</td><td>";
        bookAsString += author + "</td><td>";
        bookAsString += category + "</td><td>";
        bookAsString += status + "</td><td>";
        bookAsString += progress + "</td><td>";
        bookAsString += length + "</td><td>";
        bookAsString += description + "</td><td>";
        bookAsString += isbn + "</td><td>";
        bookAsString += imageUrl + "</td><td>";
        bookAsString += id + "</td></tr>";
        return bookAsString;
    }
}