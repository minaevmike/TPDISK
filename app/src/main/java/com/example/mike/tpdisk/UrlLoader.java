package com.example.mike.tpdisk;

import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mike on 26.10.2014.
 */
public class UrlLoader extends AsyncTask<String, Void, String>{
    public static final String TAG = "LOADER";
    public static final String FILES = "FILES_LIST";
    FragmentActivity activity;
    public UrlLoader(FragmentActivity a){
        activity = a;
    }
    ProgressDialog progressDialog = null;
    @Override
    protected void onPreExecute() {
        showDialog();
    }

    public void hideDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    public void showDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("ASA");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
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
        Log.d(TAG, result);
        JsonFileListParser parser = new JsonFileListParser();
        FileInstanse instanse = parser.parse(result);
        hideDialog();
        FolderList folderList = new FolderList();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FILES, instanse);
        folderList.setArguments(bundle);
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, folderList);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
/*public class UrlLoader extends AsyncTaskLoader<String> {
    public String url;
    public static final String TAG = "LOADER";
    public UrlLoader(Context context, Bundle bundle) {
        super(context);
        if(bundle != null){
            url = bundle.getString(MyActivity.URL_BUNDLE);
        }
        Log.d(TAG, "UrlLoader created " + url);
    }

    @Override
    protected void onStartLoading(){
        super.onStartLoading();
        Log.d(TAG, "onStartLoadging");
    }

    @Override
    protected void onStopLoading(){
        super.onStopLoading();
        Log.d(TAG, "onStopLoading");
    }
    @Override
    public String loadInBackground() {
        Log.d(TAG, hashCode() + " loadInBackground start");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth " + Credintals.getToken());
        String answer = Connector.getByUrl(url, headers);
        Log.d(TAG, answer);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return answer;
    }
    @Override
    protected void onAbandon() {
        super.onAbandon();
        Log.d(TAG, hashCode() + " onAbandon");
    }

    @Override
    protected void onReset(){
        super.onReset();
        Log.d(TAG, "onReset");
    }
    void getResultFromTask(String result){
        deliverResult(result);
    }

}*/

