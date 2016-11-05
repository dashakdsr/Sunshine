package com.sunshine.android.sunshine.app;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View list_view = inflater.inflate(R.layout.fragment_main, container, false);
        String[] data = {
                "Mon - Sunny - 31/17",
                "Tue - Foggy - 21/8",
                "Wed - Cloudy - 22/17",
                "Thurs - Rainy - 18/11",
                "Fri - Foggy - 21/10",
                "Sat - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun - Sunny - 20/7"
        };

        forecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, new ArrayList<>(Arrays.asList(data)));

        ((ListView) list_view.findViewById(R.id.listview_forecast))
                .setAdapter(forecastAdapter);
        return list_view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                new ForecastUpdateTask()
                        .execute("3389339");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ForecastUpdateTask extends AsyncTask<String, Void, String[]> {

        final private String LOG_TAG = ForecastUpdateTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String forecastJsonResponse = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                urlConnection = (HttpURLConnection)
                        new URL(Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily?").buildUpon()
                                .appendQueryParameter("id", params[0])
                                .appendQueryParameter("mode", "json")
                                .appendQueryParameter("units", "metric")
                                .appendQueryParameter("cnt", "7")
                                .appendQueryParameter("APPID", BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                                .build().toString()
                        ).openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonResponse = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "error", e);
                    }
                }
            }
            try {
                return getForecastDataFromJSON(forecastJsonResponse);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "error", e);
            }
            return null;
        }

        private String[] getForecastDataFromJSON(String data)
                throws JSONException    {

            JSONObject forecastJson = new JSONObject(data);

            JSONArray weatherArray = forecastJson.getJSONArray("list");

            String[] weakForecastStrs = new String[weatherArray.length()];
            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject dayForecastObj = weatherArray.getJSONObject(i);

                String day = new SimpleDateFormat("EEE, MMM dd", Locale.US)
                        .format(dayForecastObj.getLong("dt") * 1000);

                String description = dayForecastObj
                        .getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("main");

                JSONObject temperatureObject = dayForecastObj.getJSONObject("temp");

                weakForecastStrs[i] = day + " - " + description + " "
                        + Math.round(temperatureObject.getDouble("max")) + "/" + Math.round(temperatureObject.getDouble("min"));
            }
            return weakForecastStrs;
        }

        @Override
        protected void onPostExecute(String[] data) {
            if (data != null) {
                forecastAdapter.clear();
                forecastAdapter.addAll(data);
            }
        }
    }
}
