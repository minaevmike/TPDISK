package com.example.mike.tpdisk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
        ListView filesTable = (ListView) view.findViewById(R.id.files_list);

        filesTable.setAdapter(adapter);
    }

    // Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onArticleSelected(String city);
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
                    name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FileInstance instance1 = (FileInstance)view.getTag();

                            if (instance1 != null) {
                                String path = "";
                                try {
                                    path = URLEncoder.encode(instance1.getPath(), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                if(instance1.isDirectory()) {
                                    UrlLoader urlLoader = new UrlLoader(getActivity());
                                    urlLoader.execute("https://cloud-api.yandex.net:443/v1/disk/resources?path=" + path);
                                }else {
                                    (new AsyncDownloadFile()).execute(path);
                                }

                            }else{
                                Log.d("AA","no text view");
                            }
                        }
                    });
                    name.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            FileInstance instance = (FileInstance)view.getTag();
                            (new FileOperationsDialog()).show(getFragmentManager(),getTag());
                            return false;
                        }
                    });
                    //convertView.setTag(1);

                    if(instance.isDirectory()) {
                        ((ImageView) convertView.findViewById(R.id.image)).setImageResource(R.drawable.folder);
                    } else {
                        ((ImageView) convertView.findViewById(R.id.image)).setImageResource(R.drawable.default_ico);
                    }
                    if(instance.hasPreview()) {
                        Log.d("ADAPTER________", instance.getPreview());
                        new PreviewDownloader().execute(new Pair<View, String>(convertView, instance.getPreview()));
                    }
                }

                //TODO: REAPAIR CLICK

            }
            return convertView;
        }
    }


    public class PreviewDownloader extends AsyncTask<Pair<View, String>, Void, Bitmap> {
        Bitmap bitmap = null;
        View view = null;
        @Override
        protected Bitmap doInBackground(Pair<View, String>[] params) {
            view = params[0].first;
            try {
                bitmap = HttpDownloadUtility.bitmapDownloadUrl(params[0].second);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            ((ImageView) view.findViewById(R.id.image)).setImageBitmap(result);
        }
    }

    public class AsyncDownloadFile extends AsyncTask<String, Void, String> {
        private static final String TAG = "AsyncDownloadFile";
        @Override
        protected String doInBackground(String... strings) {
            String path = strings[0];
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
            Intent mServiceIntent = new Intent(getActivity(), UrlService.class);
            mServiceIntent.putExtra(UrlService.PARAM_URL, instance.getHref());
            mServiceIntent.setAction(UrlService.ACTION_GET_URI);
            getActivity().startService(mServiceIntent);
        }
    }




}
