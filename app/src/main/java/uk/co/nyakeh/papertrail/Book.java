package uk.co.nyakeh.papertrail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Book {
    private UUID mId;
    private String mTitle = "";
    private String mAuthor = "";
    private int mProgress = 0;
    private int mLength = 100;
    private Date mDateStarted;
    private Date mDateFinished;
    private String mImageUrl = "";
    private String mCategory = "";
    private String mStatus = "";

    private static final Date DATE_MAX = new Date(Long.MAX_VALUE);

    public Book(String status) {
        this(UUID.randomUUID());
        mDateStarted = new Date();
        mDateFinished = DATE_MAX;
        mStatus = status;
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

    public int getProgress() { return mProgress; }

    public void setProgress(int progress) {
        if (progress == mProgress){ return; }

        if (mProgress == 0 && progress != 0){
            setDateStarted(new Date());
        }
        mProgress = progress;
        if (isFinished())
        {
            setDateFinished(new Date());
            setStatus(Constants.ARCHIVE);
        }else
        {
            setDateFinished(DATE_MAX);
            setStatus(Constants.READING);
        }
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public String getImageUrl() { return mImageUrl; }

    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }

    public String getCategory() { return mCategory; }

    public void setCategory(String category) { mCategory = category; }

    public String getStatus() { return mStatus; }

    public void setStatus(String status) { mStatus = status; }

    public boolean isEmpty() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        boolean startedEqualsToday = fmt.format(mDateStarted).equals(fmt.format(new Date()));

        if (mTitle.isEmpty() && mAuthor.isEmpty() && mProgress == 0 && mLength == 100 && mImageUrl.isEmpty() && startedEqualsToday && mDateFinished.equals(DATE_MAX)) {
            return true;
        }
        return false;
    }

    public boolean isFinished() {
        return mProgress == mLength;
    }
}