package com.example.mike.tpdisk;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;



public class MyActivity extends FragmentActivity implements DownloadStateReceiver.resultGetter /*implements LoaderManager.LoaderCallbacks<String> */{
    private static final int GET_ACCOUNT_CREDS_INTENT = 100;

    private String TAG = "MainActivity";
    public static final String CLIENT_ID = "f26cda49439e40c6bd49414779cadbce";
    public static final String CLIENT_SECRET = "a559578417c34549a9a929c355e00e08";
    public static int counter = 0;
    public static final String ACCOUNT_TYPE = "com.yandex";

    public static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id="+CLIENT_ID;
    private static final String ACTION_ADD_ACCOUNT = "com.yandex.intent.ADD_ACCOUNT";
    private static final String KEY_CLIENT_SECRET = "clientSecret";
    public static final String URL_BUNDLE = "url";
    public static String USERNAME = "example.username";
    public static String TOKEN = "example.token";
    public static UrlLoader urlLoader = null;
    private static int created = 0;
    private DownloadStateReceiver mDownloadStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Utils utils = new Utils();
        String authToken = utils.getToken(this);
        // TODO: Fix expires
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.getSerializable(SplashScreenActivity.FILES_FROM_BEGIN) != null && created == 0){
            created ++;
            Log.d(TAG, "NOT NULL");
            FolderList folderList = new FolderList();
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable(UrlLoader.FILES, bundle.getSerializable(SplashScreenActivity.FILES_FROM_BEGIN));
            folderList.setArguments(bundle1);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, folderList);
            transaction.addToBackStack(null);
            transaction.commit();
            bundle.remove(SplashScreenActivity.FILES_FROM_BEGIN);
        }
        Log.d(TAG, authToken + " " + Integer.toString(utils.getExpires(this)));
        if (authToken == null) {
            String data = null;

            //Log.d(TAG, "THIS IS IT" + intent.getData().toString());
            Uri uri = intent.getData();
            if (uri != null) {
                data = uri.getFragment();
            }

            if (data == null) {
                new AuthDialogFragment().show(getSupportFragmentManager(), "auth");
                //getToken();
            } else {
                String[] parts = data.split("&");
                String token = parts[0].split("=")[1];
                Integer expires = Integer.parseInt(parts[2].split("=")[1]);
                utils.saveData(this, token, expires);
                Credintals.setToken(token);
                Log.d(TAG, data);
            }
        }
        else {
            Credintals.setToken(authToken);
        }
        /*Loader<String> loader= getLoaderManager().getLoader(1);
        if (loader != null){
            Log.d(TAG, "Not null");
            if(loader.isStarted()) {
                Log.d(TAG, "Started");
            }
        }*/

        IntentFilter mStatusIntentFilter = new IntentFilter(UrlService.ACTION_SEND_RESULT);
        //mStatusIntentFilter.addDataScheme("http");
        // Instantiates a new DownloadStateReceiver

        mDownloadStateReceiver = new DownloadStateReceiver(this);
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(mDownloadStateReceiver, mStatusIntentFilter);


        if (urlLoader != null){
            urlLoader.activity = this;
            if (!urlLoader.getStatus().equals(AsyncTask.Status.FINISHED)){
                urlLoader.showDialog();
            }
        }

        Log.d(TAG, Credintals.getToken() == null ? "NO TOKEN" : Credintals.getToken());
    }

    @Override
    protected void onDestroy(){
        //urlLoader.hideDialog();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadStateReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setResult(String result) {
        Log.d(TAG, result);
    }


    /*@Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        Loader<String> loader = null;
        loader = new UrlLoader(this, bundle);
        Log.d(TAG, "onCreateLoader");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<String> stringLoader, String s) {
        Log.d(TAG, "onLoadFinished: " + s);
        progressDialog.dismiss();
        getLoaderManager().destroyLoader(stringLoader.getId());
    }

    @Override
    public void onLoaderReset(Loader<String> stringLoader) {
        Log.d(TAG, "onLoaderReset");
    }*/

    public static class AuthDialogFragment extends DialogFragment {

        public AuthDialogFragment () {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Auth")
                    .setMessage("Start browser to authorize?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_URL)));
                            //startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_URL)), GET_ACCOUNT_CREDS_INTENT);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    })
                    .create();
        }
    };

}
