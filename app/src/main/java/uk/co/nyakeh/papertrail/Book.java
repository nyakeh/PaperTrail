package uk.co.nyakeh.papertrail;

import java.util.Date;
import java.util.UUID;

public class Book {
    public static final Date DATE_MAX = new Date(Long.MAX_VALUE);
    private UUID mId;
    private String mTitle ="";
    private String mAuthor ="";
    private String mBlurb ="";
    private String mLength ="";
    private Date mDateStarted;
    private Date mDateFinished;
    private String mISBN ="";
    private String mImageUrl ="";

    public Book() {
        this(UUID.randomUUID());
        mDateStarted = new Date();
        mDateFinished = DATE_MAX;
    }

    public Book(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDateStarted() {
        return mDateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        mDateStarted = dateStarted;
    }

    public Date getDateFinished() {
        return mDateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        mDateFinished = dateFinished;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getLength() {
        return mLength;
    }

    public void setLength(String length) {
        mLength = length;
    }

    public String getBlurb() {
        return mBlurb;
    }

    public void setBlurb(String blurb) {
        mBlurb = blurb;
    }

    public String getISBN() {
        return mISBN;
    }

    public void setISBN(String ISBN) {
        mISBN = ISBN;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public boolean isFinished() {
        return mDateFinished.before(new Date());
    }

    public boolean isEmpty() {
        if (mTitle.isEmpty() && mAuthor.isEmpty() && mBlurb.isEmpty() && mLength.isEmpty() && mISBN.isEmpty() && mImageUrl.isEmpty() && mDateFinished.equals(DATE_MAX)) {
            return true;
        }
        return false;
    }
}