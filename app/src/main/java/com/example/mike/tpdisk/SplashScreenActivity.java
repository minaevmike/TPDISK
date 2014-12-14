package com.example.mike.tpdisk;

/**
 * Created by Andrey
 * 29.10.2014.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;

public class SplashScreenActivity extends Activity {
    public static final String FILES_FROM_BEGIN = "FILES_FROM_BEGIN";
    private static final int SPLASH_SHOW_TIME = 1000;
    private String TAG = "SplashAsync";
    public static final String TOKEN = "TOKEN";
    private String token = null;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent i = new Intent(SplashScreenActivity.this,
                    MyActivity.class);
            i.putExtra(FILES_FROM_BEGIN, (String)msg.obj);
            startActivity(i);
            finish();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            token = bundle.getString(TOKEN, null);
        }
        Utils utils = new Utils();
        if (token == null) {
            token = utils.getToken(this);
        }
        if(token != null) {
            Credentials.setToken(token);
            ServiceHelper helper = new ServiceHelper();
            helper.getFilesInFolder(this, "disk:/", handler);
        }
        else {
            Intent i = new Intent(SplashScreenActivity.this,
                    MyActivity.class);
            startActivity(i);
            finish();
        }

    }

    private class BackgroundSplashTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            Log.d(TAG, hashCode() + " loadInBackground start");
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "OAuth " + token);
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
            Log.d(TAG, instanse.toString());
            Intent i = new Intent(SplashScreenActivity.this,
                    MyActivity.class);
            i.putExtra(FILES_FROM_BEGIN, instanse);
            startActivity(i);
            finish();
        }

    }
}