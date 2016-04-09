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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
public class ForecastFragment extends Fragment {

    private ListView listviewForecast;
    private ProgressBar waitData;

    private String postalCode = "94043";
    private String format = "json";
    private String units = "metrics";
    private String days = "7";
    private ArrayAdapter<String> arrayAdapter;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        listviewForecast = (ListView) view.findViewById(R.id.listview_forecast);
        waitData = (ProgressBar) view.findViewById(R.id.wait_data);

        setHasOptionsMenu(true);

        new FetchWeatherTask().execute(postalCode);

        listviewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), arrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });

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

    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        Time dayTime = new Time();
        dayTime.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        dayTime = new Time();

        String[] resultStrs = new String[numDays];

        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            long dateTime;

            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
    }

    private String getReadableDateString(long time){
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    private String formatHighLows(double high, double low) {
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
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
        protected void onPreExecute() {
            super.onPreExecute();
            waitData.setVisibility(View.VISIBLE);
            listviewForecast.setVisibility(View.GONE);
        }

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

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            waitData.setVisibility(View.GONE);
            listviewForecast.setVisibility(View.VISIBLE);
            try {
                String[] data = getWeatherDataFromJson(forecastJsonStr, 7);
                ArrayList<String> foreCastArray = new ArrayList<>(Arrays.asList(data));
                arrayAdapter = new ArrayAdapter<>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        foreCastArray);

                listviewForecast.setAdapter(arrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
