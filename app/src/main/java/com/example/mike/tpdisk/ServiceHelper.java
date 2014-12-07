package com.example.mike.tpdisk;

import android.content.Context;
import android.content.Intent;

import com.example.mike.tpdisk.Service.UrlService;

/**
 * Created by Mike on 07.12.2014.
 */
public class ServiceHelper {
    public void getFilesInFolder(Context context, String folder){
        Intent mServiceIntent = new Intent(context, UrlService.class);
        mServiceIntent.putExtra(UrlService.PARAM_FOLDER, folder);
        mServiceIntent.setAction(UrlService.ACTION_GET_URI);
        //mServiceIntent.putExtra(UrlService.PARAM_MESSENGER, new Messenger(MyActivity.handler));
        context.startService(mServiceIntent);
    }
}
