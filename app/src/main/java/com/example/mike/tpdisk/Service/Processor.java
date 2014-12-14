package com.example.mike.tpdisk.Service;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.mike.tpdisk.Connector;
import com.example.mike.tpdisk.Credentials;
import com.example.mike.tpdisk.DB.DB;
import com.example.mike.tpdisk.FileInstance;
import com.example.mike.tpdisk.JsonFileListParser;
import com.example.mike.tpdisk.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Mike on 07.12.2014.
 */
public class Processor {
    private static final String TAG = "PROCESSOR";
    private static final String URL = "https://cloud-api.yandex.net:443/v1/disk/resources?limit=1000&path=";
    private static final long day = 86400000;
    public String getFileInstanceByPath(Context context, String path, boolean is_force){
        String utf8Path = "";
        try {
            utf8Path = URLDecoder.decode(path, "UTF-8");//URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DB db = new DB(context);
        db.open();
        long fileMSec = db.getMSecPath(path);
        long fileUtf8Msec = db.getMSecPath(utf8Path);
        long curTime = System.currentTimeMillis();
        Log.d(TAG, "PATH: " + path);
        Log.d(TAG, "UTF-8 PATH: " + utf8Path);
        Log.d(TAG, "fileMsec: " + Long.toString(fileMSec));
        Log.d(TAG, "fileUTF-8Msec: " + Long.toString(fileUtf8Msec));
        Log.d(TAG, "Timediff: " + Long.toString(System.currentTimeMillis() - fileMSec));
        Log.d(TAG, "IS_FORCE: " + Boolean.toString(is_force));
        if ((curTime - fileMSec < day || curTime - fileUtf8Msec < day) && !is_force){
            Log.d(TAG, "NOT FORCE AND TOO LESS TIME PASS");
            return utf8Path;
        }
        db.deleteByPath(path);
        JsonFileListParser parser = new JsonFileListParser();
        String url = URL + path;
        Log.d(TAG, url);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth " + Credentials.getToken());
        Connector connector = new Connector();
        connector.setHeader(headers);
        connector.setUrl(url);
        String answer = connector.getByUrl();
        if (answer == null){
            Log.d(TAG, "Answer is null url: " + url);
            return null;
        }
        FileInstance instance = parser.parse(answer);

        //long fileMSec = db.getMSecPath(instance.getPath());
        db.insertMSecPath(instance.getPath());
        db.insertOrReplace(instance);
        Log.d(TAG, instance.getPath());

        db.close();
        return instance.getPath();
    }
}
