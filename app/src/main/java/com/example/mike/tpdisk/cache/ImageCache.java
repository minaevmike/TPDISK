package com.example.mike.tpdisk.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
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

    public ImageCache() {
        if(!CACHE_FOLDER.exists()) {
            CACHE_FOLDER.mkdir();
        }
    }

    public boolean isCached(String path) {
        return (new File(CACHE_FOLDER + File.separator + path)).exists();
    }

    public Bitmap getPreview(String path) {
        return BitmapFactory.decodeFile(CACHE_FOLDER + File.separator +  path);
    }

    public void cache(Bitmap b, String path) {
        File file = new File(CACHE_FOLDER + File.separator + path);
        boolean exists = file.getParentFile().mkdirs();

        try {
            boolean exists_file = file.createNewFile();
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
    }
}