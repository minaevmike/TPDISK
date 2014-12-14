package com.example.mike.tpdisk;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Messenger;

import com.example.mike.tpdisk.Service.UrlService;

/**
 * Created by Mike on 08.12.2014.
 */
public class ServiceHelper {

    public void getFilesInFolder(Context context, String folder, Handler handler){
        getFilesInFolder(context, folder, handler, false);
    }

    public void getFilesInFolder(Context context, String folder, Handler handler, boolean is_force){
        Intent mServiceIntent = new Intent(context, UrlService.class);
        mServiceIntent.putExtra(UrlService.PARAM_FOLDER, folder);
        mServiceIntent.setAction(UrlService.ACTION_GET_URI);
        mServiceIntent.putExtra(UrlService.PARAM_MESSENGER, new Messenger(handler));
        mServiceIntent.putExtra(UrlService.IS_FORCE, is_force);
        context.startService(mServiceIntent);
    }
}
