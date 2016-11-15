package com.sam_chordas.android.stockhawk.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by rithm on 11/8/2016.
 */

public class StockDetailActivity extends FragmentActivity {

    //public static final String PREFS_NAME = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("history", "");
        editor.commit();

        setContentView(R.layout.stock_detail_activity_layout);
    }
}
