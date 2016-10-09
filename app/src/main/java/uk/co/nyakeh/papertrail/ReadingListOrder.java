package uk.co.nyakeh.papertrail;

import java.util.UUID;

public class ReadingListOrder {
    private UUID mId;
    public UUID mBookId;
    private int mPosition = 0;

    public ReadingListOrder(UUID id, UUID bookId, int position) {
        mId = id;
        mBookId = bookId;
        mPosition = position;
    }
}