package com.example.mike.tpdisk;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mike.tpdisk.cache.ImageCache;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;


/**
 * Created by Mike on 26.10.2014.
 */
public class FolderList extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FolderList", "On CreateView");
        return inflater.inflate(R.layout.list, null);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        Log.d("FolderList", "On View Created");




        FileInstance instance = (FileInstance) getArguments().getSerializable(UrlLoader.FILES);
        Embedded embedded = instance.getEmbedded();
        FileAdapter adapter = new FileAdapter(embedded.getItems().toArray(new FileInstance[embedded.getItems().size()]));

        final ListView filesTable = (ListView) view.findViewById(R.id.files_list);
        filesTable.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                (new FileOperationsDialog(/*(FileInstance)adapterView.getItemAtPosition(pos)*/)).show(getFragmentManager(), getTag());
                // либо передавать файл параметром, либо сделать диалог внутренним классом для реализации функционала над файлами
                return true;
            }
        });
        filesTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                FileInstance file = (FileInstance)adapterView.getItemAtPosition(pos);
                if (file != null) {
                    String path = null;
                    try {
                        path = URLEncoder.encode(file.getPath(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(file.isDirectory()) {
                        UrlLoader urlLoader = new UrlLoader(getActivity());
                        urlLoader.execute("https://cloud-api.yandex.net:443/v1/disk/resources?path=" + path);
                    }else {
                        (new AsyncDownloadFile()).execute(path, getActivity(), file.getName());
                    }

                }else{
                    Log.d("AA","no text view");
                }
            }
        });

        filesTable.setAdapter(adapter);
    }

    private class FileAdapter extends ArrayAdapter<FileInstance> {
        public FileAdapter(FileInstance[] objects) {
            super(getActivity(), 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.file_list_item, parent, false);
            }
            FileInstance instance = getItem(position);

            if (instance != null) {

                TextView name = (TextView) convertView.findViewById(R.id.text);

                if (name != null) {
                    name.setText(instance.getName());
                    name.setTag(instance);
                    setAndCachePreview(instance, convertView);
                }

                //TODO: REAPAIR CLICK

            }
            return convertView;
        }
    }

    private void setAndCachePreview(FileInstance instance, View convertView) {
        if(instance.isDirectory()) {
            ((ImageView) convertView.findViewById(R.id.image)).setImageResource(R.drawable.folder);
        } else {
            ((ImageView) convertView.findViewById(R.id.image)).setImageResource(R.drawable.default_ico);
        }
        if(instance.hasPreview()) {
            ImageCache cache = new ImageCache();
            if(cache.isCached(instance.getNormalizedPath())) {
                ((ImageView) convertView.findViewById(R.id.image)).setImageBitmap(cache.getPreview(instance.getNormalizedPath()));
                Log.d("CACHE", "WAS CACHED!!!");
            } else {
                new PreviewDownloader().execute(convertView, instance.getPreview(), instance.getNormalizedPath());
                Log.d("CACHE", "DOWNLOAD!!!");
            }

        }
    }


    public class PreviewDownloader extends AsyncTask<Object, Void, Bitmap> {
        Bitmap bitmap = null;
        View view = null;
        String cache_path = null;
        @Override
        protected Bitmap doInBackground(Object[] params) {
            view = (View) params[0];
            cache_path = (String) params[2];
            try {
                bitmap = HttpDownloadUtility.bitmapDownloadUrl((String) params[1]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            ((ImageView) view.findViewById(R.id.image)).setImageBitmap(result);
            ImageCache c = new ImageCache();
            c.cache(result, cache_path);
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
        }
    }




}
