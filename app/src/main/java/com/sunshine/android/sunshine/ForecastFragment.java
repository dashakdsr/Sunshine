package com.android.sunshine;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Locale;

public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_forecast, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        forecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast);
        ListView listView = (ListView) view.findViewById(R.id.list_view_forecast);
        listView.setAdapter(forecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity(), DetailActivity.class)
                        .putExtra("main data", forecastAdapter.getItem(i)));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        new ForecastUpdateTask().execute(
                sharedPreferences.getString(
                        getString(R.string.pref_location_key),
                        getString(R.string.pref_location_default)),
                sharedPreferences.getString(getString(
                        R.string.pref_units_of_temp_key),
                        getString(R.string.pref_units_of_temp_default)));
    }

    public class ForecastUpdateTask extends AsyncTask<String, Void, String[]> {

        final private String LOG_SPAWNER = ForecastUpdateTask.class.getSimpleName();

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
                                .appendQueryParameter("q", params[0])
                                .appendQueryParameter("mode", "json")
                                .appendQueryParameter("units", params[1])
                                .appendQueryParameter("cnt", "16")
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
                Log.e(LOG_SPAWNER, "error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_SPAWNER, "error", e);
                    }
                }
            }
            try {
                return getForecastDataFromJSON(forecastJsonResponse);
            } catch (JSONException e) {
                Log.e(LOG_SPAWNER, "error", e);
            }
            return null;
        }

        private String[] getForecastDataFromJSON(String data)
                throws JSONException {

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
