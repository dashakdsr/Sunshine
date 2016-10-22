package com.sunshine.android.sunshine.app;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.activity_main, new FragmentMain())
                    .commit();
        }
    }


    public static class FragmentMain extends Fragment {
        public FragmentMain() {
        }

        ArrayAdapter<String> Forecast;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            String[] data =
                    {
                            "Today - Sunny - 88/63",
                            "Tomorrow - Foggy - 70/46",
                            "Weds - Cloudy - 72/63",
                            "Thurs - Rainy - 64/51",
                            "Fri - Foggy - 70/46",
                            "Sat - Sunny - 76/68"
                    };
            ArrayAdapter<String> weekForecast = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, data);
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.ListViewForecast);
            listView.setAdapter(Forecast);
            return rootView;


        }
    }
}
