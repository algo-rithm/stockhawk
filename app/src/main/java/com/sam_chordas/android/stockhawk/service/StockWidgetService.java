package com.sam_chordas.android.stockhawk.service;

import android.app.LauncherActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.pojos.Quote;
import com.sam_chordas.android.stockhawk.ui.StockWidgetProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rithm on 11/12/2016.
 */

public class StockWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent){
        return new StockRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StockRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private List<Quote> mQuotes = new ArrayList<>();
    private Cursor mCursor;

    public StockRemoteViewsFactory(Context context, Intent intent){
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int getCount(){
        return mQuotes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.stock_widget_item);

        rv.setTextViewText(R.id.widget_stock_symbol, mQuotes.get(position).getSymbol());
        rv.setTextViewText(R.id.widget_bid_price, mQuotes.get(position).getBid());
        rv.setTextViewText(R.id.widget_change, mQuotes.get(position).getChange());

        if(mQuotes.get(position).getIsUp() == 1){
            rv.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            rv.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }

        Bundle extras = new Bundle();
        extras.putString(StockWidgetProvider.EXTRA_SYMBOL, mQuotes.get(position).getSymbol());
        extras.putString(StockWidgetProvider.EXTRA_BID, mQuotes.get(position).getBid());
        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_box, fillIntent);


        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    public boolean hasStableIds() {
        return false;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onDataSetChanged(){
        mQuotes = new ArrayList<>();

        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + "= ?",
                new String[]{"1"}, null);

        if(mCursor != null){
            if(mCursor.moveToFirst()){
                while (!mCursor.isAfterLast()){
                    Quote quote = new Quote();
                    quote.setSymbol(mCursor.getString(mCursor.getColumnIndex("symbol")));
                    quote.setBid(mCursor.getString(mCursor.getColumnIndex("bid_price")));
                    quote.setChange(mCursor.getString(mCursor.getColumnIndex("change")));
                    quote.setIsUp(mCursor.getInt(mCursor.getColumnIndex("is_up")));
                    mQuotes.add(quote);
                    Log.d("&*&*","-->" + quote.getSymbol());
                    mCursor.moveToNext();
                }
            }

            mCursor.close();
        }



    }

    @Override
    public void onDestroy(){
        mQuotes = null;
        mCursor.close();
    }


}
