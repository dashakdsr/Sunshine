package com.android.sunshine;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
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
                        .putExtra("forecast data", forecastAdapter.getItem(i)));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                updateWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        new FetchWeatherTask(getActivity(), forecastAdapter)
                .execute(sharedPreferences.getString(getString(R.string.pref_location_key),
                        getString(R.string.pref_location_default)));
    }

//    public class ForecastUpdateTask extends AsyncTask<String, Void, String[]> {
//
//        final private String LOG_SPAWNER = getClass().getSimpleName();
//
//        private ArrayAdapter<String> mForecastAdapter;
//        private final Context mContext;
//
//        public ForecastUpdateTask(Context context, ArrayAdapter<String> forecastAdapter) {
//            this.mContext = context;
//            this.mForecastAdapter = forecastAdapter;
//        }
//
//        private boolean DEBUG = true;
//
//        /**
//         * Helper method to handle insertion of a new location in the weather database.
//         *
//         * @param locationSetting The location string used to request updates from the server.
//         * @param cityName        A human-readable city name, e.g "Mountain View"
//         * @param lat             the latitude of the city
//         * @param lon             the longitude of the city
//         * @return the row ID of the added location.
//         */
//        long addLocation(String locationSetting, String cityName, double lat, double lon) {
//            // Students: First, check if the location with this city name exists in the db
//            // If it exists, return the current ID
//            // Otherwise, insert it using the content resolver and the base URI
//            return -1;
//        }
//
//        String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
//            // return strings to keep UI functional for now
//            String[] resultStrs = new String[cvv.size()];
//            for (int i = 0; i < cvv.size(); i++) {
//                ContentValues weatherValues = cvv.elementAt(i);
//                String highAndLow = formatHighLows(
//                        weatherValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP),
//                        weatherValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
//                resultStrs[i] = getReadableDateString(
//                        weatherValues.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)) +
//                        " - " + weatherValues.getAsString(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC) +
//                        " - " + highAndLow;
//            }
//            return resultStrs;
//        }
//
//        @Override
//        protected String[] doInBackground(String... params) {
//            if (params.length == 0) {
//                return null;
//            }
//
//            String forecastJsonResponse = null;
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            try {
//                urlConnection = (HttpURLConnection)
//                        new URL(Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily?").buildUpon()
//                                .appendQueryParameter("q", params[0])
//                                .appendQueryParameter("mode", "json")
//                                .appendQueryParameter("units", params[1])
//                                .appendQueryParameter("cnt", "16")
//                                .appendQueryParameter("APPID", BuildConfig.OPEN_WEATHER_MAP_API_KEY)
//                                .build().toString()).openConnection();
//
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                StringBuilder buffer = new StringBuilder();
//                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                if (buffer.length() == 0) {
//                    return null;
//                }
//                forecastJsonResponse = buffer.toString();
//            } catch (IOException e) {
//                Log.e(LOG_SPAWNER, "error", e);
//                return null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (IOException e) {
//                        Log.e(LOG_SPAWNER, "error", e);
//                    }
//                }
//            }
//            try {
//                return getForecastDataFromJSON(forecastJsonResponse);
//            } catch (JSONException e) {
//                Log.e(LOG_SPAWNER, "error", e);
//            }
//            return null;
//        }
//
//        private String[] getForecastDataFromJSON(String data)
//                throws JSONException {
//
//            JSONObject forecastJson = new JSONObject(data);
//
//            JSONArray weatherArray = forecastJson.getJSONArray("list");
//
//            String[] weakForecastStrs = new String[weatherArray.length()];
//            for (int i = 0; i < weatherArray.length(); i++) {
//                JSONObject dayForecastObj = weatherArray.getJSONObject(i);
//
//                String day = new SimpleDateFormat("EEE, MMM dd", Locale.US)
//                        .format(dayForecastObj.getLong("dt") * 1000);
//
//                String description = dayForecastObj
//                        .getJSONArray("weather")
//                        .getJSONObject(0)
//                        .getString("main");
//
//                JSONObject temperatureObject = dayForecastObj.getJSONObject("temp");
//
//                weakForecastStrs[i] = day + " - " + description + " "
//                        + Math.round(temperatureObject.getDouble("max")) + "/" + Math.round(temperatureObject.getDouble("min"));
//            }
//            return weakForecastStrs;
//        }
//
//        @Override
//        protected void onPostExecute(String[] data) {
//            if (data != null) {
//                forecastAdapter.clear();
//                forecastAdapter.addAll(data);
//            }
//        }
//    }
}
