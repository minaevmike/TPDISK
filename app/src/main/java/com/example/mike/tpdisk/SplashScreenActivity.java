package com.example.mike.tpdisk;

/**
 * Created by Andrey
 * 29.10.2014.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.HashMap;

public class SplashScreenActivity extends Activity {
    public static final String FILES_FROM_BEGIN = "FILES_FROM_BEGIN";
    private static final int SPLASH_SHOW_TIME = 1000;
    private String TAG = "SplashAsync";
    public static final String TOKEN = "TOKEN";
    private String token = null;
    ImageView logo;
    private Thread thread = new Thread(){
        @Override
    public void run(){
            try {
                while (true){
                    sleep(50);
                    logo.setScaleX((float)1.1);
                }
            }
            catch (InterruptedException e){

            }
        }
    };
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
    private void scaleImage(ImageView view, double factor)
    {
    }

    private int dpToPx(int dp)
    {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Bundle bundle = getIntent().getExtras();
        //logo = (ImageView)findViewById(R.id.imgLogo);
        //thread.start();
        //scaleImage();
        try {
            Thread.sleep(2000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
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
}