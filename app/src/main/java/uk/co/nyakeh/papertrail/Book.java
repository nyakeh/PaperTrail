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
    private Date mDateStarted = new Date();
    private Date mDateFinished = DATE_MAX;
    private String mImageUrl = "";
    private String mCategory = "";
    private String mStatus = "";
    private String mISBN = "";
    private String mDescription = "";

    private static final Date DATE_MAX = new Date(Long.MAX_VALUE);
    private SimpleDateFormat mDayDateFormat = new SimpleDateFormat(Constants.EXPORT_DATE_FORMAT);

    public Book(UUID id) {
        mId = id;
    }

    public Book(String status) {
        this(UUID.randomUUID());
        mDateStarted = new Date();
        mDateFinished = DATE_MAX;
        mStatus = status;
    }

    public Book(String status, String title, String author, String isbn, Integer pageCount, String imageUrl, String description) {
        this(UUID.randomUUID());
        mDateStarted = new Date();
        mDateFinished = DATE_MAX;
        mStatus = status;
        mTitle = title;
        mAuthor = author;
        mISBN = isbn;
        mLength = pageCount;
        mImageUrl = imageUrl;
        mDescription = description;
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

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        if (progress == mProgress) {
            return;
        }

        if (mProgress == 0 && progress != 0) {
            setDateStarted(new Date());
        }
        mProgress = progress;
        if (isFinished()) {
            if (mDayDateFormat.format(mDateFinished).equals(mDayDateFormat.format(DATE_MAX))) {
                setDateFinished(new Date());
            }
            setStatus(Constants.ARCHIVE);
        } else {
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

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getISBN() {
        return mISBN;
    }

    public void setISBN(String ISBN) {
        mISBN = ISBN;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isEmpty() {
        boolean startedToday = mDayDateFormat.format(mDateStarted).equals(mDayDateFormat.format(new Date()));
        boolean notFinished = mDayDateFormat.format(mDateFinished).equals(mDayDateFormat.format(DATE_MAX));

        if (mTitle.isEmpty() && mAuthor.isEmpty() && mProgress == 0 && mLength == 100 && mImageUrl.isEmpty() && startedToday && notFinished) {
            return true;
        }
        return false;
    }

    public boolean isFinished() {
        return mProgress == mLength;
    }
}