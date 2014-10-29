package com.example.mike.tpdisk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        //TableLayout filesTable = (TableLayout) view.findViewById(R.id.files_list);
        FileInstance instanse = (FileInstance) getArguments().getSerializable(UrlLoader.FILES);
        Embedded embedded = instanse.getEmbedded();
        /*ArrayList<FileInstance> files = embedded.getItems();
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 4);*/

        FileAdapter2 adapter = new FileAdapter2(embedded.getItems().toArray(new FileInstance[embedded.getItems().size()]));
        ListView filesTable = (ListView) view.findViewById(R.id.files_list);

        filesTable.setAdapter(adapter);

        /*if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                LinearLayout cell = new LinearLayout(getActivity());
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(layoutParams);
                TableRow file = new TableRow(getActivity());
                file.setLayoutParams(layoutParams);
                TextView fileName = new TextView(getActivity());
                fileName.setText(files.get(i).getName());
                cell.addView(fileName);
                file.addView(cell);
                file.setTag(files.get(i));
                file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FileInstance instanse1 = (FileInstance) view.getTag();
                        Log.d("AA", instanse1.getPath());
                        String path = "";
                        try {
                            path = URLEncoder.encode(instanse1.getPath(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        UrlLoader urlLoader = new UrlLoader(getActivity());
                        urlLoader.execute("https://cloud-api.yandex.net:443/v1/disk/resources?path=" + path);
                    }
                });
                filesTable.addView(file);
            }
        }*/
    }

    // Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onArticleSelected(String city);
    }
    private class FileAdapter2 extends ArrayAdapter<FileInstance> {
        public FileAdapter2(FileInstance[] objects) {
            super(getActivity(), 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("FileAdapter2", "getView, pos"+String.valueOf(position));
            if (convertView == null) {
                Log.d("FileAdapter2", "create new");
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.file_list_item, parent, false);
            }
            FileInstance instance = (FileInstance) getItem(position);

            if (instance != null) {

                TextView name = (TextView) convertView.findViewById(R.id.text);

                if (name != null) {
                    name.setText(instance.getName());
                    name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView instanse1 = (TextView) view.findViewById(R.id.text);

                            if (instanse1 != null) {
                                String path = "";
                                try {
                                    path = URLEncoder.encode(instanse1.getText().toString(), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                UrlLoader urlLoader = new UrlLoader(getActivity());
                                urlLoader.execute("https://cloud-api.yandex.net:443/v1/disk/resources?path=" + path);

                            }else{
                                Log.d("AA","no text view");
                            }
                        }
                    });
                    //convertView.setTag(1);
                }
                ImageView img = (ImageView) convertView.findViewById(R.id.image);


                //TODO: REAPAIR CLICK

            }
            return convertView;
        }
    }
}
