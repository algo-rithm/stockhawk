package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.pojos.Quote;

import java.util.List;

/**
 * Created by rithm on 11/10/2016.
 */

public class StockHistoryListAdapter extends ArrayAdapter<Quote> {

    private final Context context;
    private final List<Quote> quotes;

    public StockHistoryListAdapter(Context context, List<Quote> quotes){
        super(context, -1, quotes);
//        Log.i("StockHistoryListAdapter", "GettingSymbol." + quotes.get(2).getSymbol());
        this.context = context;
        this.quotes = quotes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.stock_history_row, parent, false);

        TextView dateTV = (TextView) rowView.findViewById(R.id.date);
        TextView openTV = (TextView) rowView.findViewById(R.id.open);
        TextView closeTV = (TextView) rowView.findViewById(R.id.close);
        TextView highTV = (TextView) rowView.findViewById(R.id.high);
        TextView lowTV = (TextView) rowView.findViewById(R.id.low);

        double high = Double.parseDouble(quotes.get(position).getHigh());
        double low = Double.parseDouble(quotes.get(position).getLow());
        double open = Double.parseDouble(quotes.get(position).getOpen());
        double close = Double.parseDouble(quotes.get(position).getClose());

        if(open > close){
            closeTV.setTextColor(Color.RED);
        } else {
            closeTV.setTextColor(Color.GREEN);
        }
        openTV.setTextColor(Color.GRAY);
        dateTV.setText(quotes.get(position).getDate());
        openTV.setText(String.format("%1$.2f",open));
        closeTV.setText(String.format("%1$.2f",close));
        highTV.setText(String.format("%1$.2f",high));
        lowTV.setText(String.format("%1$.2f", low));


        return rowView;
    }
}
