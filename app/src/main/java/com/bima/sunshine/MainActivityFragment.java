package com.bima.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ListView listviewForecast;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        listviewForecast = (ListView) view.findViewById(R.id.listview_forecast);

        String[] foreCastArray = {
                            "Today - Sunny - 88 / 63",
                            "Tomorrow - Foggy - 70 / 46",
                            "Weds - Cloudy - 72 / 63",
                            "Thurs - Rainy - 64 / 51",
                            "Fri - Foggy - 70 / 46",
                            "Sat - Sunny - 76 / 68"
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                foreCastArray);

        listviewForecast.setAdapter(arrayAdapter);

        return view;
    }
}
