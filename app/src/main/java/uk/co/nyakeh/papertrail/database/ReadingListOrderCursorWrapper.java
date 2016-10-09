package uk.co.nyakeh.papertrail.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import java.util.UUID;

import uk.co.nyakeh.papertrail.ReadingListOrder;

import static uk.co.nyakeh.papertrail.database.ReadingListOrderDbSchema.ReadingListOrderTable;

public class ReadingListOrderCursorWrapper extends CursorWrapper {
    public ReadingListOrderCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ReadingListOrder getReadingListOrder() {
        String id = getString(getColumnIndex(ReadingListOrderTable.Cols.ID));
        String bookId = getString(getColumnIndex(ReadingListOrderTable.Cols.BOOK_ID));
        int position = getInt(getColumnIndex(ReadingListOrderTable.Cols.POSITION));

        ReadingListOrder readingListOrder = new ReadingListOrder(UUID.fromString(id),UUID.fromString(bookId),position);
        return readingListOrder;
    }
}