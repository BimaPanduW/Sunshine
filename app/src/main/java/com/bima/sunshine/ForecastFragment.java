package com.bima.sunshine;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ListView listviewForecast;

    public ForecastFragment() {
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

        try {
            new FetchWeatherTask().execute(getWeatherUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return view;
    }

    private URL getWeatherUrl() throws MalformedURLException {
        return new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043,US&cnt=7&units=metric&appid=b92f80be5981262933e52d95fb637140");
    }

    private class FetchWeatherTask extends AsyncTask<URL, Integer, Long>{
        HttpURLConnection urlConnection;
        BufferedReader bufferedReader;
        String forecastJsonStr;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }

        @Override
        protected Long doInBackground(URL... params) {
            try{
                urlConnection = (HttpURLConnection) params[0].openConnection();
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
