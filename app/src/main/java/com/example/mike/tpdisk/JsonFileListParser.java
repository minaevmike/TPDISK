package com.example.mike.tpdisk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mike on 26.10.2014.
 */
public class JsonFileListParser {
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
                list.add(i,new FileInstanse());
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }
}
