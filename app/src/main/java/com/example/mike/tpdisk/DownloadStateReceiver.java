package com.example.mike.tpdisk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mike.tpdisk.Service.UrlService;

public class DownloadStateReceiver extends BroadcastReceiver {
    private String TAG = "DownloadStateReceiver";
    resultGetter mParent;
    public DownloadStateReceiver(resultGetter parent) {
        mParent = parent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Bundle bundle = intent.getExtras();
        String result = bundle.getString(UrlService.PARAM_RESULT);
        mParent.setResult(result);
    }

    public interface resultGetter{
        void setResult(String result);
    }
}
