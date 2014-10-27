package com.example.mike.tpdisk;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UrlService extends IntentService {

    private String TAG = "IntentService";
    public static final String ACTION_GET_URI = "com.example.mike.tpdisk.action.ACTION_GET_URI";
    public static final String ACTION_SEND_RESULT = "com.example.mike.tpdisk.action.ACTION_SEND_RESULT";

    public static final String PARAM_URL = "com.example.mike.tpdisk.extra.PARAM_URL";
    public static final String PARAM_RESULT = "com.example.mike.tpdisk.extra.PARAM_RESULT";

    // TODO: Customize helper method
    public static void startActionFoo(Context context, String url) {
        Intent intent = new Intent(context, UrlService.class);
        intent.setAction(ACTION_GET_URI);
        intent.putExtra(PARAM_URL, url);
        context.startService(intent);
    }

    public UrlService() {
        super("UrlService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_URI.equals(action)) {
                final String url = intent.getStringExtra(PARAM_URL);
                handleActionGetUri(url);
            } else {
                throw new UnsupportedOperationException("Wrong intent action");
            }
        }
    }
    private void handleActionGetUri(String url) {
        Log.d(TAG, "handleActionGetUri");
        Log.d(TAG, hashCode() + " loadInBackground start");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth " + Credintals.getToken());
        Connector connector = new Connector();
        connector.setHeader(headers);
        connector.setUrl(url);
        String answer = connector.getByUrl();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SendResult(answer);
    }
    private void SendResult(String result) {
        Log.d(TAG, "SendResult");
        Intent localIntent = new Intent(ACTION_SEND_RESULT).putExtra(PARAM_RESULT, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
