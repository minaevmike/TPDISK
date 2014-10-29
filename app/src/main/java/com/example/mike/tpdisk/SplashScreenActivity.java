package com.example.mike.tpdisk;

/**
 * Created by Andrey
 * 29.10.2014.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {

    private static final int SPLASH_SHOW_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new BackgroundSplashTask().execute();

    }

    /**
     * Async Task: can be used to load DB, images during which the splash screen
     * is shown to user
     */
    private class BackgroundSplashTask extends AsyncTask<Object, Integer, Integer> {

        @Override
        protected Integer doInBackground(Object[] objects) {
            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(null);
            Intent i = new Intent(SplashScreenActivity.this,
                    MyActivity.class);
            // any info loaded can during splash_show
            // can be passed to main activity using
            // below
            //i.putExtra("loaded_info", " ");
            startActivity(i);
            finish();
        }

    }
}