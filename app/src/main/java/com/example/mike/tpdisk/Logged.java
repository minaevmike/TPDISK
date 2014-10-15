package com.example.mike.tpdisk;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by Mike on 15.10.2014.
 */
public class Logged extends FragmentActivity {
    private String TAG = "LOGGEDACTIVITY";
    private static final int GET_ACCOUNT_CREDS_INTENT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logged);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "COMING");
        if (requestCode == GET_ACCOUNT_CREDS_INTENT) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String name = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                String type = bundle.getString(AccountManager.KEY_ACCOUNT_TYPE);
                Log.d(TAG, "GET_ACCOUNT_CREDS_INTENT: name="+name+" type="+type);
                //Account account = new Account(name, type);
                //getAuthToken(account);
            }
        }
    }
}
