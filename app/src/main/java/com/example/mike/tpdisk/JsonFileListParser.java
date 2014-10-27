package com.example.mike.tpdisk;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Mike on 26.10.2014.
 */
public class JsonFileListParser {
    public static final String ADDRESS = "https://cloud-api.yandex.net/v1/";
    private String getOrNull(JSONObject object, String key){
        try {
            return object.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }
    public List<FileInstanse> parse(String s) {

        List<FileInstanse> list = null;
        try{
            JSONObject reader = new JSONObject(s);
            JSONObject embeded = reader.getJSONObject("_embedded");
            JSONArray files = embeded.getJSONArray("items");
            for(int i = 0;i < files.length();i++){
                //FileInstanse temp = new FileInstanse();
                int size = files.getJSONObject(i).getInt("size");
                String public_key = files.getJSONObject(i).getString("public_key");
                String origin_path  = files.getJSONObject(i).getString("origin_path");
                String name   = files.getJSONObject(i).getString("name");
                String created  = files.getJSONObject(i).getString("created");
                String public_url  = files.getJSONObject(i).getString("public_url");
                String modified  = files.getJSONObject(i).getString("modified");
                String path  = files.getJSONObject(i).getString("path");
                String media_type = files.getJSONObject(i).getString("media_type");
                String preview  = files.getJSONObject(i).getString("preview");
                String type  = files.getJSONObject(i).getString("type");
                String mime_type  = files.getJSONObject(i).getString("mime_type");
                String md5  = files.getJSONObject(i).getString("md5");
                list.add(i,new FileInstanse(size, public_key, origin_path, name, created, public_url,
                        modified, path, media_type, preview, type, mime_type, md5 ));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }
}
