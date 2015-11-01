package uk.co.nyakeh.papertrail;

import java.util.Date;
import java.util.UUID;

public class Note {
    private UUID mId;
    private UUID mBookId;
    private String mTitle;
    private String mContent;
    private Date mCreated;
    private Date mUpdated;

    public Note(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public UUID getBookId() {
        return mBookId;
    }

    public void setBookId(UUID bookId) {
        mBookId = bookId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public Date getCreated() {
        return mCreated;
    }

    public void setCreated(Date created) {
        mCreated = created;
    }

    public Date getUpdated() {
        return mUpdated;
    }

    public void setUpdated(Date updated) {
        mUpdated = updated;
    }
}
