package com.sam_chordas.android.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockWidgetService;

/**
 * Created by rithm on 11/12/2016.
 */

public class StockWidgetProvider extends AppWidgetProvider {

    public static final String LAUNCH_DETAIL = "com.sam_chordas.android.stockhawk.ui.LAUNCH";
    public static final String EXTRA_SYMBOL = "com.sam_chordas.android.stockhawk.ui.SYMBOL";
    public static final String EXTRA_BID = "com.sam_chordas.android.stockhawk.ui.BID";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent){

        if (intent.getAction().equals(LAUNCH_DETAIL)){
            String symbol = intent.getStringExtra(EXTRA_SYMBOL);
            String bid = intent.getStringExtra(EXTRA_BID);
            Intent stockIntent = new Intent(context, StockDetailActivity.class);
            stockIntent.putExtra(StockDetailFragment.STOCK, symbol);
            stockIntent.putExtra(StockDetailFragment.CURRENT_BID, bid);
            stockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(stockIntent);
        }

        super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for (int i=0; i<appWidgetIds.length; ++i){
            Intent intent = new Intent(context, StockWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.stock_widget_layout);
            rv.setRemoteAdapter(R.id.stock_widget_list, intent);
            rv.setEmptyView(R.id.stock_widget_list, R.id.stock_widget_empty);

            Intent launchDetailIntent = new Intent(context, StockWidgetProvider.class);
            launchDetailIntent.setAction(StockWidgetProvider.LAUNCH_DETAIL);
            launchDetailIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            launchDetailIntent.setData(Uri.parse(launchDetailIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent launchPending = PendingIntent.getBroadcast(context, 0, launchDetailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stock_widget_list, launchPending);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

        }
        super.onUpdate(context,appWidgetManager, appWidgetIds);
    }
}
