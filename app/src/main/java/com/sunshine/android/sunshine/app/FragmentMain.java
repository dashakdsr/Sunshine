package com.sunshine.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class FragmentMain extends Fragment {
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
        ArrayAdapter<String> weekForecast = new ArrayAdapter<>(getActivity(),R.layout.list_item_forecast, R.id.list_item_forecast_textview, data);

        return inflater.inflate(R.layout.fragment_main, container, false);


    }
}