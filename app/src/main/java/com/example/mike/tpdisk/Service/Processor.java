package com.example.mike.tpdisk.Service;

import android.content.Context;
import android.util.Log;

import com.example.mike.tpdisk.Connector;
import com.example.mike.tpdisk.Credentials;
import com.example.mike.tpdisk.DB.DB;
import com.example.mike.tpdisk.FileInstance;
import com.example.mike.tpdisk.JsonFileListParser;
import com.example.mike.tpdisk.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Mike on 07.12.2014.
 */
public class Processor {
    private static final String TAG = "PROCESSOR";
    private static final String URL = "https://cloud-api.yandex.net:443/v1/disk/resources?limit=10&path=";
    public String getFileInstanceByPath(Context context, String path){
        /*try {
            path = URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
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
        DB db = new DB(context);
        db.open();
        db.insertOrReplace(instance);
        db.close();
        return instance.getPath();
    }
}
