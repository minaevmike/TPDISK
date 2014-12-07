package com.example.mike.tpdisk.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mike on 30.11.2014.
 */
public class DB {
    private static final String DB_NAME = "TP_YA_DISK";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "downloaded";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MODIFIED = "modified";
    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_PUBLIC_KEY = "public_key";
    public static final String COLUMN_ORIG_PATH = "orig_path";
    public static final String COLUMN_PUBLIC_URL = "public_url";
    public static final String COLUMN_MEDIA_TYPE = "media_type";
    public static final String COLUMN_PREVIEW = "preview_url";
    public static final String COLUMN_PREVIEW_LOCAL = "preview_local";
    public static final String COLUMN_MIME_TYPE = "mime_type";
    public static final String COLUMN_MD5 = "md5";

    public static final String DB_CREATE =
            "create table " + DB_TABLE + " (" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_PATH + " text," +
                    COLUMN_TYPE + " text," +
                    COLUMN_NAME + " text," +
                    COLUMN_MODIFIED + " text," +
                    COLUMN_CREATED + " text," +
                    COLUMN_SIZE + " text," +
                    COLUMN_PUBLIC_KEY + " text," +
                    COLUMN_ORIG_PATH + " text," +
                    COLUMN_PUBLIC_URL + " text," +
                    COLUMN_MEDIA_TYPE + " text," +
                    COLUMN_PREVIEW + " text," +
                    COLUMN_PREVIEW_LOCAL + " text," +
                    COLUMN_MIME_TYPE + " text," +
                    COLUMN_MD5 + " text," +
                    ");";

    private static Context context;

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public DB(Context ctx) {
        context = ctx;
    }

    public void open() {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        if (dbHelper != null){
            dbHelper.close();
        }
    }

    public Cursor getAll() {
        return database.query(DB_TABLE, null,null, null, null, null, null);
    }



    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context ctx, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(ctx, name,factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }
}
