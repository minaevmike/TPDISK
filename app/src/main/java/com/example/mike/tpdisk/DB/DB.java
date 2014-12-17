package com.example.mike.tpdisk.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mike.tpdisk.Embedded;
import com.example.mike.tpdisk.FileInstance;
import com.example.mike.tpdisk.cache.db.CachedFileEntryContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mike on 30.11.2014.
 */
public class DB {
    private static final String DB_NAME = "TP_YA_DISK";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "downloaded";
    private static final String DB_TABLE_FILE_URL = "file_url";
    private static final String DB_TABLE_TIME_PATH = "time_url";

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
    public static final String COLUMN_FILE_TIME_TIME = "time";
    public static final String COLUMN_PATH_PREVIEW_SAVED = "path_preview_saved";

    public static final String DB_CREATE =
            "create table " + DB_TABLE + " (" +
                    COLUMN_ID + " integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PATH + " text UNIQUE," +
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
                    COLUMN_MIME_TYPE + " text," +
                    COLUMN_MD5 + " text," +
                    COLUMN_PATH_TO_FILE + " text," +
                    COLUMN_PATH_PREVIEW_SAVED + " text" +
                    ");";

    public static final String DB_CREATE_FILE_URL =
           "create table " + DB_TABLE_FILE_URL + " (" +
                   COLUMN_ID + " integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                   COLUMN_FILE_URL_URL + " text," +
                   COLUMN_PATH + " text UNIQUE" +
                   ");";

    public static final String DB_CREATE_TIME_PATH =
            "create table " + DB_TABLE_TIME_PATH + " (" +
                    COLUMN_ID + " integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FILE_TIME_TIME + " integer," +
                    COLUMN_PATH + " text UNIQUE" +
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

    public void setPathToFile(String md5, String path){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PATH_PREVIEW_SAVED, path);
        database.update(DB_TABLE, contentValues, COLUMN_MD5 + "= ?", new String[]{md5});
    }

    public void insertOrReplace(FileInstance fileInstance){
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
                values.put(COLUMN_PATH_PREVIEW_SAVED, "");
                Log.d("Inserting", items.get(i).getPath());
                try {
                    database.replaceOrThrow(DB_TABLE, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                values.clear();
            }
        }


    }


    public int deleteByPath(String Path){

        String[] Paths= new String[]{Path};
        return database.delete(DB_TABLE, COLUMN_PATH_TO_FILE + " = ?", Paths);
    }

    public void insertOrReplacePathUrl(String Path, String URL)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_URL_URL, URL);
        values.put(COLUMN_PATH, Path);
        try {
            database.replaceOrThrow(DB_TABLE_FILE_URL, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl(String Path){
        String[] Paths= new String[]{Path};
        Cursor cursor = database.query(DB_TABLE_FILE_URL, null, COLUMN_PATH + " = ?", Paths, null, null,null,null);
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
        /*if(!cursor.moveToFirst()) {
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
        }*/
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
    public Cursor getEByPath(String path){
        Log.d("DB", path);
        String[] Path= new String[]{path};
        Cursor cursor = database.query(DB_TABLE, null, COLUMN_PATH_TO_FILE + " = ?", Path, null, null,null,null);
        Log.d("DB Count", Integer.toString(cursor.getCount()));
        return cursor;
    }

    public long getMSecPath(String path){
        String[] names= new String[]{path};
        Cursor cursor = database.query(DB_TABLE_TIME_PATH, null, COLUMN_PATH + " = ?", names, null, null,null,null);
        if (cursor.moveToFirst())
            return cursor.getLong(1);
        else
            return -1;
    }

    public void insertMSecPath(String path){
        long time = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_TIME_TIME, time);
        values.put(COLUMN_PATH, path);
        try {
            database.replaceOrThrow(DB_TABLE_TIME_PATH, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cursor search_in_dir(String dir, String name){
        name = "%" + name + "%";
        String[] search = new String[]{dir, name};
        Log.d("search_in_dir dir name", dir + " " + name);
        Cursor cursor = database.query(DB_TABLE, null, COLUMN_PATH_TO_FILE + " = ? and " + COLUMN_NAME + " LIKE ?", search, null,null,null,null);
        if(cursor.moveToFirst())
        {
            return cursor;
        }
        else
            return null;
    }

    public  void refreshDir(String dir){
        String[] names= new String[]{dir};
        String[] columns= new String[]{COLUMN_PATH};
       // List<String> paths = new ArrayList<String>();
        Cursor cursor = database.query(DB_TABLE, columns, COLUMN_PATH_TO_FILE + " = ?", names, null, null,null,null);
        while(cursor.moveToNext()) {
            //paths.add(cursor.getString(0));
            //}
            try {
                ContentValues values = new ContentValues();
                values.put(COLUMN_FILE_TIME_TIME, -1);
                String[] paths = new String[]{cursor.getString(0)};
                //database.delete(DB_TABLE_TIME_URL, COLUMN_PATH_TO_FILE + " = ?", names);
                database.update(DB_TABLE_TIME_PATH, values, COLUMN_PATH + " = ?", paths);
                //database.update(DB_TABLE_TIME_PATH, values, COLUMN_PATH + " = ?", paths.toArray(new String[paths.size()]));
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

    }

    public void clearBase(){
        try {
            database.delete(DB_TABLE, null, null);
            database.delete(DB_TABLE_FILE_URL, null, null);
            database.delete(DB_TABLE_TIME_PATH, null, null);
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

    }
    @Deprecated
    public FileInstance getElemByPath(String Path) {
        FileInstance fileInstance = new FileInstance();
        String[] names= new String[]{Path};
        Cursor cursor = database.query(DB_TABLE, null, COLUMN_PATH + " = ?", names, null, null, null, null);
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
            sqLiteDatabase.execSQL(DB_CREATE_TIME_PATH);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }
}
