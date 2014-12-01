package com.example.mike.tpdisk.preferences;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Andrey
 * 01.12.2014.
 */
public class PreferencesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
