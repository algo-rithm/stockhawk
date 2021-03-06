package com.sam_chordas.android.stockhawk.rest;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockWidgetProvider;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON, Context context){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");

              final String symbol = jsonObject.getString("symbol");

              if(jsonObject.getString("Bid").equals("null")){
                final Context toastC = context;
                Handler handler = new Handler(Looper.getMainLooper());
                if(jsonObject.getString("PreviousClose").equals("null")){
                  handler.post(new Runnable() {
                    @Override
                    public void run() {
                      Toast.makeText(toastC, "Sorry -> "+ symbol + " does not exist.", Toast.LENGTH_LONG).show();
                    }
                  });
                } else {
                handler.post(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(toastC, "Market is closed. Bid for " + symbol + "is not available.", Toast.LENGTH_LONG).show();
                  }
                });}
              } else {
              batchOperations.add(buildBatchOperation(jsonObject));
              AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
              int[] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, StockWidgetProvider.class));
              appWidgetManager.notifyAppWidgetViewDataChanged(appIds, R.id.stock_widget_list);}

        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");
          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);

              final String symbol = jsonObject.getString("symbol");

              if(jsonObject.getString("Bid").equals("null")){
                final Context toastC = context;
                Handler handler = new Handler(Looper.getMainLooper());
                if(jsonObject.getString("PreviousClose").equals("null")){
                  handler.post(new Runnable() {
                    @Override
                    public void run() {
                      Toast.makeText(toastC, "Sorry -> "+ symbol + " does not exist.", Toast.LENGTH_LONG).show();
                    }
                  });
                } else {
                  handler.post(new Runnable() {
                    @Override
                    public void run() {
                      Toast.makeText(toastC, "Market is closed. Bid for " + symbol + "is not available.", Toast.LENGTH_LONG).show();
                    }
                  });}
              } else {
              batchOperations.add(buildBatchOperation(jsonObject));
              AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
              int[] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, StockWidgetProvider.class));
              appWidgetManager.notifyAppWidgetViewDataChanged(appIds, R.id.stock_widget_list);
            }}
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice){

      bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));


    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
          jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }
}
