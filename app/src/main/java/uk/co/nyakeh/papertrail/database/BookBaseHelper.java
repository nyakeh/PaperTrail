package uk.co.nyakeh.papertrail.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import uk.co.nyakeh.papertrail.database.BookDbSchema.BookTable;
import uk.co.nyakeh.papertrail.database.ReadingListOrderDbSchema.ReadingListOrderTable;

public class BookBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 3;
    private static final String DATABASE_NAME = "bookBase.db";

    public BookBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BookTable.NAME + "(" + BookTable.Cols.ID + ", " + BookTable.Cols.TITLE + ", " + BookTable.Cols.AUTHOR + ", " + BookTable.Cols.PROGRESS + ", " + BookTable.Cols.LENGTH + ", " + BookTable.Cols.STARTED + ", " + BookTable.Cols.FINISHED + ", " + BookTable.Cols.IMAGE_URL + ", " + BookTable.Cols.CATEGORY + ", " + BookTable.Cols.STATUS + ", " + BookTable.Cols.ISBN + ", " + BookTable.Cols.DESCRIPTION + ", " + BookTable.Cols.RATING + ")");
        db.execSQL("create table " + ReadingListOrderTable.NAME + "(" + ReadingListOrderTable.Cols.ID + ", " + ReadingListOrderTable.Cols.BOOK_ID + ", " + ReadingListOrderTable.Cols.POSITION + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table " + ReadingListOrderTable.NAME + "(" + ReadingListOrderTable.Cols.ID + ", " + ReadingListOrderTable.Cols.BOOK_ID + ", " + ReadingListOrderTable.Cols.POSITION + ")");
    }
}