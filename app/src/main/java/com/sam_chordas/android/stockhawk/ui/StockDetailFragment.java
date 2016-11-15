package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.loaders.StockHistoryLoader;
import com.sam_chordas.android.stockhawk.pojos.Quote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by rithm on 11/10/2016.
 */

public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Quote>> {

    public static final int STOCK_CHART_LOADER_ID = 1;
    public static final String STOCK = "stock";
    public static final String CURRENT_BID = "current_bid";
    private String mStock;
    private String mCurrentBid;
    private String mStartDate;
    private String mEndDate;
    private List<Quote> quotes = null;

    private Calendar today;
    private Calendar beginDay;

    LineSet dataHigh;
    LineSet dataLow;
    LineSet dataOpen;
    LineSet dataClose;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.stock_detail_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);




        quotes = new ArrayList<>();

        Intent intent = getActivity().getIntent();
        mStock = intent.getStringExtra(STOCK);
        mCurrentBid = intent.getStringExtra(CURRENT_BID);

        SharedPreferences history = getActivity().getSharedPreferences(StockSettingsActivity.STOCK_PREFS, 0);
        int days = Integer.parseInt(history.getString("pref_chartHistoryLength", "30"));


        today = GregorianCalendar.getInstance();
        beginDay = (Calendar) today.clone();
        beginDay.add(Calendar.DAY_OF_YEAR, -days);

        Date dateToday = today.getTime();
        Date dateBegin = beginDay.getTime();


        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        mStartDate = simpleDateFormat.format(dateBegin);
        mEndDate = simpleDateFormat.format(dateToday);

        Button sevenDay = (Button) getView().findViewById(R.id.button_7day);
        sevenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar newBeginDay = (Calendar) today.clone();
                newBeginDay.add(Calendar.DAY_OF_YEAR, -7);
                Date thisBegin = newBeginDay.getTime();
                mStartDate = simpleDateFormat.format(thisBegin);
                getLoaderManager().restartLoader(STOCK_CHART_LOADER_ID, null, StockDetailFragment.this);

            }
        });

        Button thirtyDay = (Button) getView().findViewById(R.id.button_1month);
        thirtyDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar newBeginDay = (Calendar) today.clone();
                newBeginDay.add(Calendar.DAY_OF_YEAR, -30);
                Date thisBegin = newBeginDay.getTime();
                mStartDate = simpleDateFormat.format(thisBegin);
                getLoaderManager().restartLoader(STOCK_CHART_LOADER_ID, null, StockDetailFragment.this);

            }
        });

        Button ninetyDay = (Button) getView().findViewById(R.id.button_3month);
        ninetyDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar newBeginDay = (Calendar) today.clone();
                newBeginDay.add(Calendar.DAY_OF_YEAR, -90);
                Date thisBegin = newBeginDay.getTime();
                mStartDate = simpleDateFormat.format(thisBegin);
                getLoaderManager().restartLoader(STOCK_CHART_LOADER_ID, null, StockDetailFragment.this);

            }
        });

        Button sixMonths = (Button) getView().findViewById(R.id.button_6month);
        sixMonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar newBeginDay = (Calendar) today.clone();
                newBeginDay.add(Calendar.DAY_OF_YEAR, -180);
                Date thisBegin = newBeginDay.getTime();
                mStartDate = simpleDateFormat.format(thisBegin);
                getLoaderManager().restartLoader(STOCK_CHART_LOADER_ID, null, StockDetailFragment.this);

            }
        });

        Button oneYear = (Button) getView().findViewById(R.id.button_1year);
        oneYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar newBeginDay = (Calendar) today.clone();
                newBeginDay.add(Calendar.DAY_OF_YEAR, -365);
                Date thisBegin = newBeginDay.getTime();
                mStartDate = simpleDateFormat.format(thisBegin);
                getLoaderManager().restartLoader(STOCK_CHART_LOADER_ID, null, StockDetailFragment.this);

            }
        });

        LoaderManager lm = getActivity().getSupportLoaderManager();
        /*
        Bundle bundle = new Bundle();
        bundle.putString(STOCK, stock);
        bundle.putString(START_DATE, start_date);
        bundle.putString(END_DATE, end_date);
*/
        lm.initLoader(STOCK_CHART_LOADER_ID, null, StockDetailFragment.this);
    }

    @Override
    public void onStart(){
        super.onStart();
        TextView symbolTV = (TextView) getView().findViewById(R.id.list_detail_symbol);
        symbolTV.setText(mStock);

        TextView currentBidTV = (TextView) getView().findViewById(R.id.list_detail_bid);
        currentBidTV.setText(mCurrentBid);
    }

    @Override
    public Loader<List<Quote>> onCreateLoader(int id, Bundle args){
        Loader res = null;
        switch(id){
            case STOCK_CHART_LOADER_ID:
                res = new StockHistoryLoader(getActivity(), mStock, mStartDate, mEndDate);
                break;
        }
        return res;
    }

    @Override
    public void onLoadFinished(Loader<List<Quote>> loader, List<Quote> quoteList){
        switch(loader.getId()){
            case STOCK_CHART_LOADER_ID:

                LineChartView chart = (LineChartView) getView().findViewById(R.id.chart);
                chart.reset();

                Collections.reverse(quoteList);
                quotes = quoteList;

                createChart();

                ListView listView = (ListView) getView().findViewById(R.id.stock_history_list);
                ListAdapter adapter = new StockHistoryListAdapter(getContext(), quoteList);
                listView.setAdapter(adapter);
                //setupButtons();

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Quote>> loader){
    }


    private void setupButtons(){


        today = GregorianCalendar.getInstance();
        beginDay = (Calendar) today.clone();
        Date dateToday = today.getTime();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


        mEndDate = simpleDateFormat.format(dateToday);


        Button sevenDay = (Button) getView().findViewById(R.id.button_7day);
        sevenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginDay.add(Calendar.DAY_OF_YEAR, -7);
                Date thisBegin = beginDay.getTime();
                mStartDate = simpleDateFormat.format(thisBegin);
                getLoaderManager().restartLoader(STOCK_CHART_LOADER_ID, null, StockDetailFragment.this);


            }
        });



    }

    private void updateChart(){
        LineChartView chart = (LineChartView) getView().findViewById(R.id.chart);
        chart.reset();
    }

    private void createChart(){
        LineChartView chart = (LineChartView) getView().findViewById(R.id.chart);
        dataHigh = new LineSet();
        dataLow = new LineSet();
        dataOpen = new LineSet();
        dataClose = new LineSet();
        float minValue = 10000f;
        float maxValue = 0f;

        for(Quote quote : quotes){

            float high = Float.parseFloat(quote.getHigh());
            dataHigh.addPoint(quote.getDate(), high);
            if (high > maxValue)maxValue = high;

            float low = Float.parseFloat(quote.getLow());
            dataLow.addPoint(quote.getDate(), low);
            if(low < minValue)minValue = low;

            float open = Float.parseFloat(quote.getOpen());
            dataOpen.addPoint(quote.getDate(), open);

            float close = Float.parseFloat(quote.getClose());
            dataClose.addPoint(quote.getDate(), close);
        }

        dataHigh.setColor(Color.GREEN);
        dataHigh.setThickness(4f);
        dataLow.setColor(Color.RED);
        dataLow.setThickness(4f);
        dataOpen.setColor(Color.YELLOW);
        dataOpen.setThickness(2f);
        dataClose.setColor(Color.CYAN);
        dataClose.setThickness(2f);
        chart.addData(dataHigh);
        chart.addData(dataLow);
        chart.addData(dataOpen);
        chart.addData(dataClose);
        chart.setBackgroundColor(Color.WHITE);
        chart.setAxisBorderValues((int) minValue - 1,(int) maxValue + 1);
        chart.setXLabels(AxisRenderer.LabelPosition.NONE);
        chart.setStep(1);
        chart.show();
    }
}
