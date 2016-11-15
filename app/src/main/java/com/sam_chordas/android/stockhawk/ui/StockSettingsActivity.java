package com.sam_chordas.android.stockhawk.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.sam_chordas.android.stockhawk.R;

/**
 * Created by rithm on 11/13/2016.
 */

public class StockSettingsActivity extends PreferenceActivity {

    public static final String STOCK_PREFS = "stock_prefs";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, new StockSettingsFragment());
        transaction.commit();
    }

    public static class StockSettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            PreferenceManager manager = getPreferenceManager();
            manager.setSharedPreferencesName(STOCK_PREFS);
            addPreferencesFromResource(R.xml.preference);

        }
    }
}
