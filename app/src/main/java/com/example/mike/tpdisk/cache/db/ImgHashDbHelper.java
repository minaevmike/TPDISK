package com.example.mike.tpdisk.cache.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.mike.tpdisk.cache.db.CachedFileEntryContract.CachedFileEntry;

/**
 * Created by Andrey 
 * 26.11.2014.
 */
public class ImgHashDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ImageCache.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CachedFileEntry.TABLE_NAME + " (" +
            CachedFileEntry._ID + " INTEGER PRIMARY KEY," +
            CachedFileEntry.COLUMN_NAME_HASH + TEXT_TYPE + COMMA_SEP +
            CachedFileEntry.COLUMN_NAME_PATH + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CachedFileEntry.TABLE_NAME;

    public ImgHashDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Just a cache  - no tragedy in purging
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}