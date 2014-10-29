package com.example.mike.tpdisk;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mike on 26.10.2014.
 */
public class JsonFileListParser {
    public static final String TAG = "PARSER";
    public static final String ADDRESS = "https://cloud-api.yandex.net/v1/";
    private String getStringOrNull(JSONObject object, String key){
        try {
            return object.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    private Integer getIntOrNull(JSONObject object, String key){
        try {
            return object.getInt(key);
        } catch (JSONException e) {
            return -1;
        }
    }

    private FileInstance getFileInstanse(JSONObject object){
        Log.d(TAG, object.toString());
        Integer size = getIntOrNull(object, "size");
        String public_key = getStringOrNull(object, "public_key");
        String origin_path =  getStringOrNull(object, "origin_path");
        String name =  getStringOrNull(object, "name");
        String created = getStringOrNull(object, "created");
        String public_url = getStringOrNull(object, "public_url");
        String modified = getStringOrNull(object, "modified");
        String path = getStringOrNull(object, "path");
        String media_type = getStringOrNull(object, "media_type");
        String preview = getStringOrNull(object, "preview");
        String type = getStringOrNull(object, "type");
        String mime_type =  getStringOrNull(object, "mime_type");
        String md5 = getStringOrNull(object, "md5");
        Embedded embedded = new Embedded();
        FileInstance fileInstance = new FileInstance(size, public_key, origin_path, name, created, public_url, modified,
                path, media_type, preview,type, mime_type, md5, embedded);
        return fileInstance;
    }
    public FileInstance parse(String s) {
        FileInstance instanse =  null;
        ArrayList<FileInstance> list = new ArrayList<FileInstance>();
        try{
            JSONObject reader = new JSONObject(s);
            instanse = getFileInstanse(reader);
            JSONObject embeded = reader.getJSONObject("_embedded");
            JSONArray array = embeded.getJSONArray("items");
            for(int i = 0; i < array.length(); i++){
                list.add(i, getFileInstanse(array.getJSONObject(i)));
            }
            instanse.setEmbedded(new Embedded(getStringOrNull(embeded, "sort"), list, getIntOrNull(embeded, "limit"),
                    getIntOrNull(embeded, "offset"), getStringOrNull(embeded,"path"), getIntOrNull(embeded,"total")));

        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return instanse;
    }
}
