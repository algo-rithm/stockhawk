package com.sam_chordas.android.stockhawk.loaders;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sam_chordas.android.stockhawk.pojos.Quote;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rithm on 11/8/2016.
 */

public class StockHistoryLoader extends AsyncTaskLoader<List<Quote>> {

    private String stock;
    private String startDate;
    private String endDate;
    private List<Quote> quotes;


    public StockHistoryLoader(Context context, String stock, String startDate, String endDate){
        super(context);
        this.stock = stock;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    @Override
    protected void onStartLoading(){
        if(quotes != null){
            deliverResult(quotes);
        }
        if (takeContentChanged() || quotes == null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading(){
        cancelLoad();
    }

    @Override
    protected void onForceLoad(){
        super.onForceLoad();

    }

    @Override
    public List<Quote> loadInBackground(){

        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String jsonString;

        quotes = new ArrayList<>();

        Log.i("StockHistoryLoader", "Fetching Stock History in background.");

        StringBuilder urlBuilder = new StringBuilder();

        try{
            urlBuilder.append("http://query.yahooapis.com/v1/public/yql?q=");
            urlBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = \"", "UTF-8"));
            urlBuilder.append(stock);
            urlBuilder.append(URLEncoder.encode("\" and startDate = \"", "UTF-8"));
            urlBuilder.append(startDate);
            urlBuilder.append(URLEncoder.encode("\" and endDate = \"", "UTF-8"));
            urlBuilder.append(endDate);
            urlBuilder.append("\"&format=json&diagnostics=true&env=store://datatables.org/alltableswithkeys");
            Log.i("StockHistoryLoader", "URL= " + urlBuilder.toString());
            URL url = new URL(urlBuilder.toString());

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null){return null;}
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null){buffer.append(line+"\n");}
            if(buffer.length() == 0){return null;}
            jsonString = buffer.toString();

            JSONObject results = new JSONObject(jsonString);
            JSONArray jsonQuotes = results.getJSONObject("query").getJSONObject("results").getJSONArray("quote");

            for(int i = 0; i < jsonQuotes.length(); i++){
                JSONObject quoteResult = jsonQuotes.getJSONObject(i);
                Quote quote = new Quote();
                quote.setSymbol(quoteResult.getString("Symbol"));
                quote.setDate(quoteResult.getString("Date"));
                quote.setOpen(quoteResult.getString("Open"));
                quote.setHigh(quoteResult.getString("High"));
                quote.setLow(quoteResult.getString("Low"));
                quote.setClose(quoteResult.getString("Close"));
                quote.setVolume(quoteResult.getString("Volume"));
                quote.setAdj_close(quoteResult.getString("Adj_Close"));

                quotes.add(quote);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (conn != null){
                conn.disconnect();
            }
        }

        return  quotes;
    }


    @Override
    public void deliverResult(List<Quote> result){

        this.quotes = result;
        super.deliverResult(result);
    }

    @Override
    protected void onReset(){
        super.onReset();
        onStopLoading();
        if(quotes != null){
            quotes = null;}
    }

    @Override
    public void onCanceled(List<Quote> result){
        onContentChanged();
    }
}
