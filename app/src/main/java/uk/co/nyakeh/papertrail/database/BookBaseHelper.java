package uk.co.nyakeh.papertrail.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import uk.co.nyakeh.papertrail.database.BookDbSchema.BookTable;

import static uk.co.nyakeh.papertrail.database.NoteDbSchema.*;

public class BookBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "bookBase.db";

    public BookBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BookTable.NAME + "(" + BookTable.Cols.ID + ", " + BookTable.Cols.TITLE + ", " + BookTable.Cols.AUTHOR + ", " + BookTable.Cols.PROGRESS + ", " + BookTable.Cols.LENGTH + ", " + BookTable.Cols.STARTED + ", " + BookTable.Cols.FINISHED + ", " + BookTable.Cols.IMAGE_URL +  ", " + BookTable.Cols.CATEGORY + ", " + BookTable.Cols.STATUS + ")");
        db.execSQL("create table " + NoteTable.NAME + "(" + NoteTable.Cols.ID + ", " + NoteTable.Cols.BOOK_ID + ", " + NoteTable.Cols.TITLE + ", " + NoteTable.Cols.CONTENT + ", " + NoteTable.Cols.CREATED + ", " + NoteTable.Cols.UPDATED + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}