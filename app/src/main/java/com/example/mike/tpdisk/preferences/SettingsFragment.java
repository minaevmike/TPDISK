package com.example.mike.tpdisk.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.mike.tpdisk.R;

/**
 * Created by Andrey
 * 01.12.2014.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}