package com.example.mike.tpdisk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mike on 29.10.2014.
 */
public class JsonParser {
    public LinkInstance parse(String s){
        try {
            JSONObject object = new JSONObject(s);
            return new LinkInstance(object.getString("href"), object.getString("method"), object.getString("templated"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
