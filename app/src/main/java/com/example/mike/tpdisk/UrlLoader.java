package com.example.mike.tpdisk;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.HashMap;

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
    String type;
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
        headers.put("Authorization", "OAuth " + Credentials.getToken());
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
        FileInstance instanse = parser.parse(result);
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
