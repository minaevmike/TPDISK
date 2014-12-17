package com.example.mike.tpdisk;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mike.tpdisk.DB.DB;
import com.example.mike.tpdisk.RoundImageView.MLRoundedImageView;
import com.example.mike.tpdisk.Utils;
import com.example.mike.tpdisk.Service.UrlService;
import com.example.mike.tpdisk.cache.ImageCache;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;


/**
 * Created by Mike on 26.10.2014.
 */
enum ECursors { GETBYPATH, GETBYNAMEINDIR }

public class FolderList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    Toast toast;
    public static final String FILES = "FILES_LIST";
    public static final String PATH = "PATH";
    public static final String NAME = "NAME";
    private static final String TAG = "FOLDER_LIST";
    SimpleCursorAdapter cursorAdapter;
    private CustomCursorAdapter customCursorAdapter;
    private DB db;
    private String CUR_PATH;
    private static int i = 0;
    private SwipeRefreshLayout swipeRefreshLayout;


    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MyActivity activity = (MyActivity)getActivity();
            activity.putFilesOnScreen(msg.obj.toString());
            Log.d("FOLDER_LIST", msg.obj.toString());

        }
    };

    public Handler handler_to_init_loader = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            getClass().getSuperclass();
            MyActivity activity = (MyActivity)getActivity();
            String path = msg.obj.toString();
            Bundle bundle = new Bundle();
            bundle.putString(PATH, path);
            getActivity().getSupportLoaderManager().initLoader(i, bundle, FolderList.this);
            getActivity().getSupportLoaderManager().getLoader(i).forceLoad();
            i++;
            //activity.putFilesOnScreen(msg.obj.toString());
            Log.d("FOLDER_LIST", msg.obj.toString());

        }
    };

    public void refresh(String path){
        try {
            path = URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ServiceHelper helper = new ServiceHelper();
        helper.getFilesInFolder(getActivity(), path, handler);
    }

    public void search(String name){
        Bundle bundle = new Bundle();
        bundle.putString(NAME, name);
        getActivity().getSupportLoaderManager().initLoader(i, bundle, this);
        getActivity().getSupportLoaderManager().getLoader(i).forceLoad();
        i++;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FolderList", "On CreateView");
        return inflater.inflate(R.layout.list, null);

    }

    @Override
    public void onRefresh(){
        Toast.makeText(getActivity(), "I started", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(true);
        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, folderList, curPage);
        transaction.addToBackStack(null);
        transaction.commit();*/
        final Context context = getActivity();
        Log.d("onRefresh", CUR_PATH);
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyActivity activity = (MyActivity)getActivity();
                activity.getDb().refreshDir(CUR_PATH);
                ServiceHelper helper = new ServiceHelper();
                helper.getFilesInFolder(context, CUR_PATH, handler_to_init_loader, true);
                //Log.d("_______swipeRefreshLayout.postDelayed", curPage);
                //folderList.refresh(curPage);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 0);
    }
    public void setEnablesSwipe(boolean flag){
        if (swipeRefreshLayout != null){
            swipeRefreshLayout.setEnabled(flag);
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);
        customCursorAdapter = new CustomCursorAdapter(getActivity(), null);
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        Log.d("FolderList", "On View Created");
        String path = getArguments().getString(PATH);
        String name = getArguments().getString(NAME);
        Bundle bundle = new Bundle();
        //if(path != null) {
            CUR_PATH = path;
            //Log.d(TAG, path);
            bundle.putString(PATH, path);
        //}else{
        //    bundle.putString(NAME, name);
        //}
        getActivity().getSupportLoaderManager().initLoader(i, bundle, this);
        getActivity().getSupportLoaderManager().getLoader(i).forceLoad();
        i++;
        //FileInstance instance = (FileInstance) getArguments().getSerializable(UrlLoader.FILES);
        //Embedded embedded = instance.getEmbedded();
        //FileAdapter adapter = new FileAdapter(embedded.getItems().toArray(new FileInstance[embedded.getItems().size()]));
        //MyActivity activity = (MyActivity) getActivity();
        //final Cursor cursor = activity.getDb().getEByPath(path);
        //cursor.moveToFirst();
        //customCursorAdapter.swapCursor(cursor);
        final ListView filesTable = (ListView) view.findViewById(R.id.files_list);
        filesTable.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                (new FileOperationsDialog()).show(getFragmentManager(), getTag());
                // либо передавать файл параметром, либо сделать диалог внутренним классом для реализации функционала над файлами
                return true;
            }
        });
        filesTable.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                MyActivity activity = (MyActivity)getActivity();
                if (i == 0){
                    setEnablesSwipe(true);
                }
                else {
                   setEnablesSwipe(false);
                }
            }
        });
        filesTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Cursor cursor1 = (Cursor)adapterView.getItemAtPosition(pos);//(Cursor) adapterView.getItemAtPosition(pos);
                String path = cursor1.getString(cursor1.getColumnIndexOrThrow(DB.COLUMN_PATH));
                String type = cursor1.getString(cursor1.getColumnIndexOrThrow(DB.COLUMN_TYPE));
                String name = cursor1.getString(cursor1.getColumnIndexOrThrow(DB.COLUMN_NAME));
                try {
                    path = URLEncoder.encode(path, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (type.equals(FileInstance.DIR)){
                    ServiceHelper helper = new ServiceHelper();
                    helper.getFilesInFolder(getActivity(), path, handler);
                }
                else {
                    (new AsyncDownloadFile()).execute(path, getActivity(), name);
                }
            }
        });

        filesTable.setAdapter(customCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String path = bundle.getString(PATH);
        String name = bundle.getString(NAME);
        String cur_path = CUR_PATH;//new String(CUR_PATH);
        //cur_path += CUR_PATH;
        MyActivity activity = (MyActivity) getActivity();
        if(path != null) {
            return new BestCursorLoader(activity, activity.getDb(), path, cur_path, ECursors.GETBYPATH);
        }else{
            return new BestCursorLoader(activity, activity.getDb(), name, cur_path, ECursors.GETBYNAMEINDIR);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        customCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private class CustomCursorAdapter extends CursorAdapter{

        private Context context;
        private int selectedPosition;
        LayoutInflater layoutInflater;

        public CustomCursorAdapter(Context context, Cursor c){
            super(context, c, 0);
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View v = layoutInflater.inflate(R.layout.file_list_item, viewGroup, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView textView = (TextView)view.findViewById(R.id.text);
            textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DB.COLUMN_NAME)));
            setAndCachePreview(cursor, view);
        }

        private void setAndCachePreview(Cursor c, View convertView) {
            Utils u = new Utils();
            boolean is_dir = u.getStringFromCursor(c, DB.COLUMN_TYPE).equals(FileInstance.DIR);
            String preview = u.getStringFromCursor(c, DB.COLUMN_PREVIEW);//c.getString(c.getColumnIndexOrThrow(DB.COLUMN_PREVIEW));
            String md5 = u.getStringFromCursor(c, DB.COLUMN_MD5);// c.getString(c.getColumnIndexOrThrow(DB.COLUMN_MD5));
            String path = u.getStringFromCursor(c, DB.COLUMN_PATH);
            String cache_file = u.getStringFromCursor(c, DB.COLUMN_PATH_TO_FILE);
            String norm_path = path.split(":/")[1];
            if(is_dir) {
                ((ImageView) convertView.findViewById(R.id.image)).setImageResource(R.drawable.folder);
            } else {
                ((ImageView) convertView.findViewById(R.id.image)).setImageResource(R.drawable.default_ico);
            }
            if(preview != null && !preview.equals("")) {
                ImageCache cache = new ImageCache(getActivity());
                if (cache_file != null && ! cache_file.equals("") && cache.isCached(norm_path)) {
                    ((ImageView)convertView.findViewById(R.id.image)).setImageBitmap(cache.getPreview(norm_path));
                    //Log.d("CACHE", "WAS CACHED!!!");
                }
                else {
                    new PreviewDownloader().execute(convertView, cache, preview, md5, norm_path);
                    //Log.d("CACHE", "DOWNLOAD!!!");
                }
                /*ImageCache cache = new ImageCache(getActivity());
                if(cache.isCached(norm_path, md5)) {
                    ((ImageView) convertView.findViewById(R.id.image)).setImageBitmap(cache.getPreview(norm_path));
                    Log.d("CACHE", "WAS CACHED!!!");
                } else {
                    new PreviewDownloader().execute(convertView, cache, preview, md5, norm_path);
                    Log.d("CACHE", "DOWNLOAD!!!");
                }*/
            }
        }
    }

    public class PreviewDownloader extends AsyncTask<Object, Void, Bitmap> {
        //FileInstance instance = null;
        View view = null;
        ImageCache imageCache = null;
        String md5;
        String preview;
        String norm_path;
        @Override
        protected Bitmap doInBackground(Object[] params) {
            view = (View) params[0];
            imageCache = (ImageCache) params[1];
            preview = (String) params[2];
            md5 = (String) params[3];
            norm_path = (String) params[4];

            Bitmap bitmap = null;
            try {
                bitmap = HttpDownloadUtility.bitmapDownloadUrl(preview);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            ((MLRoundedImageView) view.findViewById(R.id.image)).setImageBitmap(result);
            imageCache.cache(result, norm_path, md5);
        }
    }

    public class AsyncDownloadFile extends AsyncTask<Object, Void, String> {
        private Activity activity = null;
        private String name = null;

        private static final String TAG = "AsyncDownloadFile";
        @Override
        protected String doInBackground(Object... params) {
            String path = (String) params[0];
            activity = (Activity) params[1];
            name = (String) params[2];
            String url = "https://cloud-api.yandex.net:443/v1/disk/resources/download?path=" + path;
            Log.d(TAG, hashCode() + " loadInBackground start");
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "OAuth " + Credentials.getToken());
            Connector connector = new Connector();
            connector.setHeader(headers);
            connector.setUrl(url);
            String answer = connector.getByUrl();
            if (answer == null){
                Log.d(TAG, url);
            }
            //Log.d(TAG, answer);
            return answer;
        }
        @Override
        protected void onPostExecute(String result) {
            JsonParser parser = new JsonParser();
            LinkInstance instance = parser.parse(result);

            DownloadManager downloadmanager =(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadmanager.enqueue(new DownloadManager.Request(Uri.parse(instance.getHref()))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(true)
                    .setTitle(name)
                    .setDescription("TPDisk download")
                    .addRequestHeader("Authorization", "OAuth " + Credentials.getToken())
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED));

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
            final String NOTIFY_DOWNLOAD = activity.getApplicationContext().getString(R.string.pref_notify_download_key);
            if(sharedPref.getBoolean(NOTIFY_DOWNLOAD, true)) {
                showToastWithoutSpam("Download started", activity.getApplicationContext());
            }
        }
    }


    public void showToastWithoutSpam (String text, Context context){
        try{ toast.getView().isShown();
            toast.setText(text);
        } catch (Exception e) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    static class BestCursorLoader extends CursorLoader {
        DB db;
        String path_or_name;
        String cur_path;
        ECursors ecursors;
        HashMap<String, Cursor> map = new HashMap<String, Cursor>();
        public BestCursorLoader(Context context, DB db, String path_or_name, String cur_path, ECursors ecursors) {
            super(context);
            this.path_or_name = path_or_name;
            this.cur_path = cur_path;
            this.db = db;
            this.ecursors = ecursors;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor;
            if(this.ecursors == ECursors.GETBYNAMEINDIR) {
                cursor = this.db.search_in_dir(this.cur_path, this.path_or_name);
            }
            else{
                cursor = this.db.getEByPath(this.path_or_name);
            }
            return cursor;
        }
    }

}
