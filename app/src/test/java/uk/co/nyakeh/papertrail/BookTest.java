package uk.co.nyakeh.papertrail;

import org.junit.Test;
import org.junit.Before;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class BookTest {
    public static final String TITLE = "Enders Game";
    private Book mBook;

    @Before
    public void whenANewBookIsCreated() throws Exception {
        mBook = new Book(Constants.READING);
    }

    @Test
    public void thenItIsEmpty() throws Exception {
        assertEquals(true, mBook.isEmpty());
    }

    @Test
    public void thenAttributesCanBeOverrode() throws Exception {
        mBook.setTitle(TITLE);

        assertEquals(TITLE, mBook.getTitle());
        assertEquals(false, mBook.isEmpty());
    }

    @Test
    public void whenTheProgressIsUpdatedThenTheProgressIsSet() throws Exception {
        mBook.setProgress(17);

        assertEquals(17, mBook.getProgress());
        assertEquals(false, mBook.isFinished());
        assertEquals(Constants.READING, mBook.getStatus());
    }

    @Test
    public void whenTheProgressIsUpdatedToOneHundredPercentThenTheDateFinishedIsSetToTodayAndTheCurrentStatusIsArchive() throws Exception {
        SimpleDateFormat day = new SimpleDateFormat(Constants.EXPORT_DATE_FORMAT);
        mBook.setProgress(100);

        assertEquals(100, mBook.getProgress());
        assertEquals(true, mBook.isFinished());
        assertEquals(Constants.ARCHIVE, mBook.getStatus());
        assertEquals(true, day.format(mBook.getDateFinished()).equals(day.format(new Date())));
    }

    @Test
    public void whenTheProgressIsUpdatedToZeroThenTheDateStartedIsUpdatedToToday() throws Exception {
        SimpleDateFormat day = new SimpleDateFormat(Constants.EXPORT_DATE_FORMAT);
        mBook.setProgress(0);

        assertEquals(0, mBook.getProgress());
        assertEquals(false, mBook.isFinished());
        assertEquals(Constants.READING, mBook.getStatus());
        assertEquals(true, day.format(mBook.getDateStarted()).equals(day.format(new Date())));
        assertEquals(true, day.format(mBook.getDateFinished()).equals(day.format(new Date(Long.MAX_VALUE))));
    }
}