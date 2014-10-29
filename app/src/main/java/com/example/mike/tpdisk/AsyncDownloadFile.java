package com.example.mike.tpdisk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Mike on 29.10.2014.
 */
public class AsyncDownloadFile extends AsyncTask<String, Void, String> {
    private static final String TAG = "AsyncDownloadFile";
    public FragmentActivity activity;
    AsyncDownloadFile(FragmentActivity activity){
        this.activity = activity;
    }
    @Override
    protected String doInBackground(String... strings) {
        String path = strings[0];
        String url = "https://cloud-api.yandex.net:443/v1/disk/resources/download?path=" + path;
        Log.d(TAG, hashCode() + " loadInBackground start");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth " + Credintals.getToken());
        Connector connector = new Connector();
        connector.setHeader(headers);
        connector.setUrl(url);
        String answer = connector.getByUrl();
        if (answer == null){
            Log.d(TAG, url);
        }
        //Log.d(TAG, answer);
        return answer;
    }
    @Override
    protected void onPostExecute(String result) {
        JsonParser parser = new JsonParser();
        LinkInstance instance = parser.parse(result);
        Intent mServiceIntent = new Intent(activity, UrlService.class);
        mServiceIntent.putExtra(UrlService.PARAM_URL, instance.getHref());
        mServiceIntent.setAction(UrlService.ACTION_GET_URI);
        activity.startService(mServiceIntent);
    }
}
