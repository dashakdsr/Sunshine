package com.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

class ForecastAdapter extends CursorAdapter {

    final private String LOG_SPAWNER = getClass().getSimpleName();

    final private Context mContext;

    ForecastAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    private String formatHighLows(double high, double low) {
        int units = Utility.getPreferredUnits(mContext);
        return Utility.formatTemperature(high, units) + "/" + Utility.formatTemperature(low, units);
    }

    private String convertContentValuesToUXFormat(Cursor cursor) {
        // return strings to keep UI functional for now

        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view;
        tv.setText(convertContentValuesToUXFormat(cursor));
    }
}
