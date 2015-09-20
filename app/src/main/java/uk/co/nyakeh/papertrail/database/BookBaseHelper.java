package uk.co.nyakeh.papertrail.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import uk.co.nyakeh.papertrail.database.BookDbSchema.BookTable;

public class BookBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "bookBase.db";

    public BookBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BookTable.NAME + "(" + BookTable.Cols.UUID + ", " + BookTable.Cols.TITLE + ", " + BookTable.Cols.AUTHOR + ", " + BookTable.Cols.BLURB + ", " + BookTable.Cols.LENGTH + ", " + BookTable.Cols.DATE_STARTED + ", " + BookTable.Cols.DATE_FINISHED + ", " + BookTable.Cols.ISBN + ", " + BookTable.Cols.IMAGE_URL + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
