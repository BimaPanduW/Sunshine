package com.bima.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements ForeCastView{

    private ListView listviewForecast;
    private ProgressBar waitData;

    private ForecasPresenter forecasPresenter;

    private String postalCode = "94043";

    private ArrayAdapter<String> arrayAdapter;

    public ForecastFragment() {
        forecasPresenter = new ForecasPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        listviewForecast = (ListView) view.findViewById(R.id.listview_forecast);
        waitData = (ProgressBar) view.findViewById(R.id.wait_data);

        setHasOptionsMenu(true);

        new FetchWeatherTask().execute(postalCode);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                new FetchWeatherTask().execute(postalCode);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            forecasPresenter.doInBackground(params[0]);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            forecasPresenter.onPost();
        }
    }


    @Override
    public void showLoading(boolean show) {
        if(show) {
            waitData.setVisibility(View.VISIBLE);
            listviewForecast.setVisibility(View.GONE);
        }else{
            waitData.setVisibility(View.GONE);
            listviewForecast.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setAdapter(ArrayList<String> foreCastArray) {
        arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                foreCastArray);

        listviewForecast.setAdapter(arrayAdapter);
    }
}
