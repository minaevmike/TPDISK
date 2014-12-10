package com.example.mike.tpdisk.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.example.mike.tpdisk.DB.DB;
import com.example.mike.tpdisk.MyActivity;
import com.example.mike.tpdisk.cache.db.ImgHashDb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Andrey
 * 26.11.2014.
 */


public class ImageCache {
    private static final File ROOT = Environment.getExternalStorageDirectory();
    private static final File CACHE_FOLDER = new File(ROOT, "tpyadisk_cache");

    //private final ImgHashDb imgHashDb;
    private final DB db;
    public ImageCache(Context context) {
        if(!CACHE_FOLDER.exists()) {
            //noinspection ResultOfMethodCallIgnored
            CACHE_FOLDER.mkdir();
        }
        MyActivity activity = (MyActivity) context;
        db = activity.getDb();
        //imgHashDb = new ImgHashDb(context);
    }

    public boolean isCached(String path) {
        return (new File(CACHE_FOLDER + File.separator + path)).exists();
    }

    public Bitmap getPreview(String path) {
        return BitmapFactory.decodeFile(CACHE_FOLDER + File.separator +  path);
    }

    public void cache(Bitmap b, String path, String hash) {
        File file = new File(CACHE_FOLDER + File.separator + path);
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();

        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fo != null) {
            try {
                b.compress(Bitmap.CompressFormat.PNG, 100, fo);
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        db.setPathToFile(hash, path);
        //imgHashDb.addOrUpdateHash_RAW(path, hash);
    }
}