package com.example.mike.tpdisk;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

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
        TableLayout filesTable = (TableLayout) view.findViewById(R.id.files_list);
        FileInstanse instanse = (FileInstanse) getArguments().getSerializable(UrlLoader.FILES);
        Embedded embedded = instanse.getEmbedded();
        ArrayList<FileInstanse> files = embedded.getItems();
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 4);
        if (files != null) {
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
                        FileInstanse instanse1 = (FileInstanse) view.getTag();
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
        }
    }
}
