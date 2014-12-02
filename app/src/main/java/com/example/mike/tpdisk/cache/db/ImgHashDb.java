package com.example.mike.tpdisk.cache.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mike.tpdisk.cache.db.CachedFileEntryContract.*;

import java.util.jar.Attributes;

/**
 * Created by Andrey
 * 02.12.2014.
 */
public class ImgHashDb {
    private ImgHashDbHelper imgHashDbHelper= null;

    public ImgHashDb(Context context) {
        imgHashDbHelper = new ImgHashDbHelper(context);
    }

    public boolean validateHash(String path, String hash) {
        SQLiteDatabase db = imgHashDbHelper.getReadableDatabase();

        String[] projection = { CachedFileEntry.COLUMN_NAME_HASH };
        String selection = CachedFileEntry.COLUMN_NAME_PATH + " LIKE ?";
        String[] selectionArgs = { path };

        Cursor cursor = db.query(
                CachedFileEntry.TABLE_NAME,               // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        Log.d("DATABASE VALIDATE HASH QUERY", hash);
        String storedHash;
        try {
            cursor.moveToFirst();
            storedHash = cursor.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        Log.d("DATABASE VALIDATE HASH VALUE", storedHash);
        return storedHash.equals(hash);
    }

    public void addOrUpdateHash_RAW(String path, String hash) {

        final String RAW =
        "REPLACE INTO " + CachedFileEntry.TABLE_NAME + " (" +
        CachedFileEntry._ID + "," +
        CachedFileEntry.COLUMN_NAME_HASH + "," +
        CachedFileEntry.COLUMN_NAME_PATH +  " ) VALUES ( " +
            "(SELECT " + CachedFileEntry._ID +
            " FROM " + CachedFileEntry.TABLE_NAME +
            " WHERE " + CachedFileEntry.COLUMN_NAME_PATH + "='" + path +
            "'), '" +
            hash + "', '" +
            path + "')";
        Log.d("sdasdfasdf", RAW);

        SQLiteDatabase db = imgHashDbHelper.getWritableDatabase();
        try {
            db.compileStatement(RAW).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
