package com.example.mike.tpdisk;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mike on 26.10.2014.
 */
public class FolderList extends Fragment {
    //private OnItemSelectedListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("CityList", "On CreateView");
        return inflater.inflate(R.layout.list, null);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        /*try {
            mCallback = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener");
        }*/
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        /*Log.d("CityList", "On View Created");
        ListView list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(new NewsListAdapter(cities));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TextView c = (TextView) view.findViewById(R.id.text);
                mCallback.onArticleSelected(c.getText().toString());
                Log.i("onViewCreated DetailFragment", c.getText().toString());
            }
        });*/
    }
}
