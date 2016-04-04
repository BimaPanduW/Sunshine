package com.bima.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ListView listviewForecast;

    private String postalCode = "94043";
    private String format = "json";
    private String units = "metrics";
    private String days = "7";

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        listviewForecast = (ListView) view.findViewById(R.id.listview_forecast);

        setHasOptionsMenu(true);

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

    private String getBaseUrl() {
        return "http://api.openweathermap.org/data/2.5/forecast/daily?";
    }

    private String getAppId(){
        return "b92f80be5981262933e52d95fb637140";
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, Void>{
        final String QUERY_PARAM = "q";
        final String FORMAT_PARAM = "mode";
        final String UNITS_PARAM = "units";
        final String DAYS_PARAM = "cnt";
        final String APPID_PARAM = "appid";

        HttpURLConnection urlConnection;
        BufferedReader bufferedReader;
        String forecastJsonStr;

        @Override
        protected Void doInBackground(String... params) {
            try{
                Uri uri = Uri.parse(getBaseUrl()).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(DAYS_PARAM, days)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(APPID_PARAM, getAppId()).build();

                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();

                if(inputStream != null){
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    if (stringBuilder.length() > 0) {
                        forecastJsonStr = stringBuilder.toString();
                    }
                }

            } catch (IOException e) {
                Log.e("Bima", "Error ", e);
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }

                if(bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("Bima", "Error closing stream", e);
                    }
                }
            }

            Log.d("Bima", forecastJsonStr);
            return null;
        }
    }
}
