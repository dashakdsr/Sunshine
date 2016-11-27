package com.android.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static int getPreferredUnits(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String unitType = prefs.getString(
                context.getString(R.string.pref_temperature_units_key),
                context.getString(R.string.pref_temperature_units_default));

        String[] unitsEntries = context.getResources().getStringArray(R.array.pref_temperature_units_entry_value);
        {
            if (unitType.equals(unitsEntries[0])) {
                return 1;
            } else if (unitType.equals(unitsEntries[1])) {
                return 2;
            } else if (!unitType.equals(unitsEntries[2])) {
                return 3;
            }
            return 0;
        }
    }

    public static String formatTemperature(double temperature, int units) {
        switch (units) {
            case 1:
                return String.format(Locale.US, "%.0f", temperature * 1.8 + 32);
            case 2:
                return String.format(Locale.US, "%.0f", temperature);
            case 3:
                return String.format(Locale.US, "%.0f", temperature + 273.15);
            default:
                return null;
        }
    }

    static String formatDate(long dateInMillis) {
        return new SimpleDateFormat("EEE, MMM dd", Locale.US)
                .format(dateInMillis);
    }
}
