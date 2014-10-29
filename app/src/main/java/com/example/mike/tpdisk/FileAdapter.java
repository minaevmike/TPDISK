package com.example.mike.tpdisk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by user on 28.10.2014.
 */
public class FileAdapter  extends BaseAdapter {
    private Embedded mEmbedded = null;
    private Context mContext = null;
    @Override
    public int getCount() {
        return mEmbedded.getItems().size();
    }

    public FileAdapter(Context context, Embedded embedded) {
        mEmbedded = embedded;
        mContext = context;
    }

    @Override
    public Object getItem(int i) {
        return mEmbedded.getItems().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View fileView;
        if (view == null) {
            fileView = LayoutInflater.from(mContext).inflate(R.layout.file_list_item, null);
        } else {
            fileView = view;
        }

        FileInstance instance = (FileInstance) getItem(i);

        if (instance != null) {

            TextView tt = (TextView) fileView.findViewById(R.id.text);

            if (tt != null) {
                tt.setText(instance.getName());
            }
        }
        return fileView;
    }
}


