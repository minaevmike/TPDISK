package com.example.mike.tpdisk.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mike.tpdisk.Embedded;
import com.example.mike.tpdisk.FileInstance;
import com.example.mike.tpdisk.cache.db.CachedFileEntryContract;

import java.util.ArrayList;

/**
 * Created by Mike on 30.11.2014.
 */
public class DB {
    private static final String DB_NAME = "TP_YA_DISK";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "downloaded";
    private static final String DB_TABLE_FILE_URL = "file_url";

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
    public static final String COLUMN_PATH_TO_FILE = "path_to_file";
    public static final String COLUMN_MIME_TYPE = "mime_type";
    public static final String COLUMN_MD5 = "md5";
    public static final String COLUMN_FILE_URL_URL = "url";
    public static final String COLUMN_FILE_URL_PATH = "path";

    public static final String DB_CREATE =
            "create table " + DB_TABLE + " (" +
                    COLUMN_ID + " integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PATH + " text UNIQUE," +
                    COLUMN_TYPE + " text," +
                    COLUMN_NAME + " text UNIQUE," +
                    COLUMN_MODIFIED + " text," +
                    COLUMN_CREATED + " text," +
                    COLUMN_SIZE + " text," +
                    COLUMN_PUBLIC_KEY + " text," +
                    COLUMN_ORIG_PATH + " text," +
                    COLUMN_PUBLIC_URL + " text," +
                    COLUMN_MEDIA_TYPE + " text," +
                    COLUMN_PREVIEW + " text," +
                    COLUMN_PATH_TO_FILE + " text," +
                    COLUMN_MIME_TYPE + " text," +
                    COLUMN_MD5 + " text" +
                    ");";

    public static final String DB_CREATE_FILE_URL =
           "create table " + DB_TABLE_FILE_URL + " (" +
                   COLUMN_ID + " integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                   COLUMN_FILE_URL_URL + " text," +
                   COLUMN_FILE_URL_PATH + " text UNIQUE" +
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

    public void insertOrReplace(FileInstance fileInstance){
        /*final String RAW =
            "REPLACE INTO" + DB_TABLE + "(" +COLUMN_PATH + "," + COLUMN_TYPE+ "," + COLUMN_NAME+ "," + COLUMN_MODIFIED+ "," +
                COLUMN_CREATED+ "," + COLUMN_SIZE+ "," + COLUMN_PUBLIC_KEY+ "," + COLUMN_ORIG_PATH+ "," + COLUMN_PUBLIC_URL+ "," +
                "," + COLUMN_MEDIA_TYPE+ "," + COLUMN_PREVIEW+ "," + /*COLUMN_PREVIEW_LOCAL+ "," +*//* COLUMN_MIME_TYPE+ "," + COLUMN_MD5 + ")" + "VALUES ( " +
                "'" + fileInstance.getPath() + "','" + fileInstance.getType() + "','" + fileInstance.getName() + "','" + fileInstance.getModified() + "','" + fileInstance.getCreated() +
                "','" + fileInstance.getSize() + "','" + fileInstance.getPublic_key() + "','" + fileInstance.getOrigin_path() + "','" + fileInstance.getPublic_url() + "','" +
                fileInstance.getMedia_type() + "','" + fileInstance.getPreview() + "','" + /*fileInstance.getp + "','" + *//*fileInstance.getMime_type() + "','" +  fileInstance.getMd5() + "');";
        Log.d("sdasdfasdf", RAW);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.compileStatement(RAW).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        ContentValues values = new ContentValues();
        if(fileInstance.getEmbedded().getItems() != null) {
            ArrayList<FileInstance> items = fileInstance.getEmbedded().getItems();
            for (int i = 0; i < items.size(); ++i) {
                values.put(COLUMN_PATH, items.get(i).getPath());
                values.put(COLUMN_TYPE, items.get(i).getType());
                values.put(COLUMN_NAME, items.get(i).getName());
                values.put(COLUMN_MODIFIED, items.get(i).getModified());
                values.put(COLUMN_CREATED, items.get(i).getCreated());
                values.put(COLUMN_SIZE, items.get(i).getSize());
                values.put(COLUMN_PUBLIC_KEY, items.get(i).getPublic_key());
                values.put(COLUMN_ORIG_PATH, items.get(i).getOrigin_path());
                values.put(COLUMN_PUBLIC_URL, items.get(i).getPublic_url());
                values.put(COLUMN_MEDIA_TYPE, items.get(i).getMedia_type());
                values.put(COLUMN_PREVIEW, items.get(i).getPreview());
                values.put(COLUMN_MIME_TYPE, items.get(i).getMime_type());
                values.put(COLUMN_MD5, items.get(i).getMd5());
                values.put(COLUMN_PATH_TO_FILE, fileInstance.getPath());
                try {
                    database.replaceOrThrow(DB_TABLE, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                values.clear();
            }
        }
        values.put(COLUMN_PATH, fileInstance.getPath());
        values.put(COLUMN_TYPE, fileInstance.getType());
        values.put(COLUMN_NAME, fileInstance.getName());
        values.put(COLUMN_MODIFIED, fileInstance.getModified());
        values.put(COLUMN_CREATED, fileInstance.getCreated());
        values.put(COLUMN_SIZE, fileInstance.getSize());
        values.put(COLUMN_PUBLIC_KEY, fileInstance.getPublic_key());
        values.put(COLUMN_ORIG_PATH, fileInstance.getOrigin_path());
        values.put(COLUMN_PUBLIC_URL, fileInstance.getPublic_url());
        values.put(COLUMN_MEDIA_TYPE, fileInstance.getMedia_type());
        values.put(COLUMN_PREVIEW, fileInstance.getPreview());
        values.put(COLUMN_MIME_TYPE, fileInstance.getMime_type());
        values.put(COLUMN_MD5, fileInstance.getMd5());
        try {
            database.replaceOrThrow(DB_TABLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*public Cursor getAll() {
        return database.query(DB_TABLE, null,null, null, null, null, null);
    }*/

    public void insertOrReplacePathUrl(String Path, String URL)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_URL_URL, URL);
        values.put(COLUMN_FILE_URL_PATH, Path);
        try {
            database.replaceOrThrow(DB_TABLE_FILE_URL, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl(String Path){
        String[] Paths= new String[]{Path};
        Cursor cursor = database.query(DB_TABLE_FILE_URL, null, COLUMN_FILE_URL_PATH + " = ?", Paths, null, null,null,null);
        if(cursor.moveToFirst())
        {
            return cursor.getString(1);
        }
        else
            return null;
    }

    public FileInstance getElemByName(String name){
        FileInstance fileInstance = new FileInstance();
        String[] names= new String[]{name};
        Cursor cursor = database.query(DB_TABLE, null, COLUMN_NAME + " = ?", names, null, null,null,null);
        if(cursor.moveToFirst()) {
            fileInstance.setPath(cursor.getString(1));
            fileInstance.setType(cursor.getString(2));
            fileInstance.setName(cursor.getString(3));
            fileInstance.setModified(cursor.getString(4));
            fileInstance.setCreated(cursor.getString(5));
            fileInstance.setSize(cursor.getInt(6));
            fileInstance.setPublic_key(cursor.getString(7));
            fileInstance.setOrigin_path(cursor.getString(8));
            fileInstance.setPublic_url(cursor.getString(9));
            fileInstance.setMedia_type(cursor.getString(10));
            fileInstance.setPreview(cursor.getString(11));
            fileInstance.setMime_type(cursor.getString(12));
            fileInstance.setMd5(cursor.getString(13));

        }else{
            fileInstance.setSize(-1);
        }
        ArrayList<FileInstance> items = new ArrayList<FileInstance>();
        String[] path= new String[]{fileInstance.getPath()};
        cursor = database.query(DB_TABLE, null, COLUMN_PATH_TO_FILE + " = ?", path, null, null,null,null);
        if(!cursor.moveToFirst()) {
            return fileInstance;
        }else{
            FileInstance temp = new FileInstance();

            temp.setPath(cursor.getString(1));
            temp.setType(cursor.getString(2));
            temp.setName(cursor.getString(3));
            temp.setModified(cursor.getString(4));
            temp.setCreated(cursor.getString(5));
            temp.setSize(cursor.getInt(6));
            temp.setPublic_key(cursor.getString(7));
            temp.setOrigin_path(cursor.getString(8));
            temp.setPublic_url(cursor.getString(9));
            temp.setMedia_type(cursor.getString(10));
            temp.setPreview(cursor.getString(11));
            temp.setMime_type(cursor.getString(12));
            temp.setMd5(cursor.getString(13));
            items.add(temp);
        }
        while (cursor.moveToNext())
        {
            FileInstance temp = new FileInstance();
            temp.setPath(cursor.getString(1));
            temp.setType(cursor.getString(2));
            temp.setName(cursor.getString(3));
            temp.setModified(cursor.getString(4));
            temp.setCreated(cursor.getString(5));
            temp.setSize(cursor.getInt(6));
            temp.setPublic_key(cursor.getString(7));
            temp.setOrigin_path(cursor.getString(8));
            temp.setPublic_url(cursor.getString(9));
            temp.setMedia_type(cursor.getString(10));
            temp.setPreview(cursor.getString(11));
            temp.setMime_type(cursor.getString(12));
            temp.setMd5(cursor.getString(13));
            items.add(temp);
        }
        Embedded embedded = new Embedded();
        embedded.setPath(fileInstance.getPath());
        embedded.setItems(items);
        fileInstance.setEmbedded(embedded);
        return fileInstance;
    }

    public FileInstance getElemByPath(String Path) {
        FileInstance fileInstance = new FileInstance();
        String[] names= new String[]{Path};
        Cursor cursor = database.query(DB_TABLE, null, COLUMN_PATH + " = ?", names, null, null,null,null);
        if(cursor.moveToFirst()) {
            fileInstance.setPath(cursor.getString(1));
            fileInstance.setType(cursor.getString(2));
            fileInstance.setName(cursor.getString(3));
            fileInstance.setModified(cursor.getString(4));
            fileInstance.setCreated(cursor.getString(5));
            fileInstance.setSize(cursor.getInt(6));
            fileInstance.setPublic_key(cursor.getString(7));
            fileInstance.setOrigin_path(cursor.getString(8));
            fileInstance.setPublic_url(cursor.getString(9));
            fileInstance.setMedia_type(cursor.getString(10));
            fileInstance.setPreview(cursor.getString(11));
            fileInstance.setMime_type(cursor.getString(12));
            fileInstance.setMd5(cursor.getString(13));
        }else{
            fileInstance.setSize(-1);
        }
        ArrayList<FileInstance> items = new ArrayList<FileInstance>();
        String[] path= new String[]{fileInstance.getPath()};
        cursor = database.query(DB_TABLE, null, COLUMN_PATH_TO_FILE + " = ?", path, null, null,null,null);
        if(!cursor.moveToFirst()) {
            return fileInstance;
        }else{
            FileInstance temp = new FileInstance();
            temp.setPath(cursor.getString(1));
            temp.setType(cursor.getString(2));
            temp.setName(cursor.getString(3));
            temp.setModified(cursor.getString(4));
            temp.setCreated(cursor.getString(5));
            temp.setSize(cursor.getInt(6));
            temp.setPublic_key(cursor.getString(7));
            temp.setOrigin_path(cursor.getString(8));
            temp.setPublic_url(cursor.getString(9));
            temp.setMedia_type(cursor.getString(10));
            temp.setPreview(cursor.getString(11));
            temp.setMime_type(cursor.getString(12));
            temp.setMd5(cursor.getString(13));
            items.add(temp);
        }
        while (cursor.moveToNext())
        {
            FileInstance temp = new FileInstance();
            temp.setPath(cursor.getString(1));
            temp.setType(cursor.getString(2));
            temp.setName(cursor.getString(3));
            temp.setModified(cursor.getString(4));
            temp.setCreated(cursor.getString(5));
            temp.setSize(cursor.getInt(6));
            temp.setPublic_key(cursor.getString(7));
            temp.setOrigin_path(cursor.getString(8));
            temp.setPublic_url(cursor.getString(9));
            temp.setMedia_type(cursor.getString(10));
            temp.setPreview(cursor.getString(11));
            temp.setMime_type(cursor.getString(12));
            temp.setMd5(cursor.getString(13));
            items.add(temp);
        }
        Embedded embedded = new Embedded();
        embedded.setPath(fileInstance.getPath());
        embedded.setItems(items);
        fileInstance.setEmbedded(embedded);
        return fileInstance;
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context ctx, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(ctx, name,factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.d("SQLiteDatabaseOnCreate", DB_CREATE);
            sqLiteDatabase.execSQL(DB_CREATE);
            sqLiteDatabase.execSQL(DB_CREATE_FILE_URL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }
}
