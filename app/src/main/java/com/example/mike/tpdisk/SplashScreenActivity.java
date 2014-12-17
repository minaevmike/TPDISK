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
    boolean is_run = true;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent i = new Intent(SplashScreenActivity.this,
                    MyActivity.class);
            i.putExtra(FILES_FROM_BEGIN, (String)msg.obj);
            startActivity(i);
            is_run = false;
            finish();

        }
    };
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            int i = 0;
            try {
                while (true){
                    Thread.sleep(50);
                    if (i % 10 < 5) {
                        scaleImage(logo, 1);
                    }
                    else {
                        scaleImage(logo, -1);
                    }
                }
            } catch (Exception e){

            }

        }
    });
    private void scaleImage(ImageView view, int factor)
    {
        Bitmap bitmap = ((BitmapDrawable)view.getDrawable()).getBitmap();
        Bitmap resultBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() + (int)(factor * 20), bitmap.getHeight() +(int)(factor* 20), true);
        view.setImageBitmap(resultBitmap);
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
        logo = (ImageView)findViewById(R.id.imgLogo);
        //thread.start();
        //scaleImage();
        //scaleImage(logo, 0);
        //thread.start();
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