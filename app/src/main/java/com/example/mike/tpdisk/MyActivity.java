package com.example.mike.tpdisk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.example.mike.tpdisk.DB.DB;
import com.example.mike.tpdisk.preferences.PreferencesActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivity extends FragmentActivity /*implements LoaderManager.LoaderCallbacks<String> */{
    private static final int GET_ACCOUNT_CREDS_INTENT = 100;

    private String TAG = "MainActivity";
    public static final String  CLIENT_ID = "f26cda49439e40c6bd49414779cadbce";
    public static final String CLIENT_SECRET = "a559578417c34549a9a929c355e00e08";
    public static int counter = 0;
    public static final String ACCOUNT_TYPE = "com.yandex";

    private String[] sideMenuStringItems;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence barTitle;


    public static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id="+CLIENT_ID;
    private static final String ACTION_ADD_ACCOUNT = "com.yandex.intent.ADD_ACCOUNT";
    private static final String KEY_CLIENT_SECRET = "clientSecret";
    public static final String URL_BUNDLE = "url";
    public static String USERNAME = "example.username";
    public static String TOKEN = "example.token";
    public static UrlLoader urlLoader = null;
    private static int created = 0;
    private static String curPage;
    private DB db;

    public void putFilesOnScreen(String path, boolean put_to_back_stack){

        FolderList folderList = new FolderList();
        Bundle bundle = new Bundle();
        bundle.putString(FolderList.PATH, path);
        folderList.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, folderList, path);
        if (put_to_back_stack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        curPage = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Utils utils = new Utils();
        db = new DB(this);
        db.open();
        String authToken = utils.getToken(this);
        // TODO: Fix expires
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if( getSupportFragmentManager().findFragmentById(R.id.container) != null) {
            Log.d(TAG, "1");
            created = 1;
        }else {
            Log.d(TAG, "0");
            created = 0;
        }
        if (bundle != null && bundle.getSerializable(SplashScreenActivity.FILES_FROM_BEGIN) != null && created == 0){
            //created ++;
            Log.d(TAG, "NOT NULL");
            String path = bundle.getString(SplashScreenActivity.FILES_FROM_BEGIN);
            putFilesOnScreen(path, false);
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
                Credentials.setToken(token);
                Log.d(TAG, data);
                Intent i = new Intent(MyActivity.this, SplashScreenActivity.class);
                i.putExtra(SplashScreenActivity.TOKEN, token);
                startActivity(i);
                finish();
            }
        }
        else {
            Credentials.setToken(authToken);
        }

        sideMenuStringItems = getResources().getStringArray(R.array.side_menu_text_array);
        drawerList = (ListView) findViewById(R.id.side_menu);
        TypedArray images = getResources().obtainTypedArray(R.array.side_menu_image_array);
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < sideMenuStringItems.length; i++) {
            Map<String, Object> datum = new HashMap<String, Object>(2);
            datum.put("text", sideMenuStringItems[i]);
            datum.put("image", images.getResourceId(i, 0));
            data.add(datum);
        }
        drawerList.setAdapter(new SimpleAdapter(this, data, R.layout.drawer_list_item,
                new String[] {"text","image"},
                new int[] {R.id.drawer_item_text, R.id.drawer_item_image}));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        barTitle = getTitle();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_menu,
                R.string.open_desc,
                R.string.close_desc
        ){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(barTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getActionBar().setTitle("Menu");
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        Log.d(TAG, Credentials.getToken() == null ? "NO TOKEN" : Credentials.getToken());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = ((DrawerLayout) findViewById(R.id.drawer_layout)).isDrawerOpen(drawerList);
        menu.findItem(R.id.search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy");
        //db.close();
        //urlLoader.hideDialog();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
            ImageView v = (ImageView) search.findViewById(searchImgId);
            //v.setImageResource(R.drawable.ic_action_search);
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d(TAG + " SEARCH", s);
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String query) {

                    Log.d(TAG + " SEARCH", query);

                    return true;

                }

            });
            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Log.d("CLOSE", "CLOSE");
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    public DB getDb() {
        return db;
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

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position) {
                case 0:
                    break;
                case 1:
                    onInfoButtonClick();
                    break;
                case 2:
                    finish();
                    break;
            }
        }

        private void onInfoButtonClick() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
            builder.setTitle("Информация")
                    .setMessage("Создано TP-GABEN-TEAM!")
                    .setIcon(R.drawable.ic_info_gaben)
                    .setCancelable(false)
                    .setNegativeButton("ОК...",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


}
