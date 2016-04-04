package com.bima.sunshine;

import android.net.Uri;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bima.sunshine.models.Day;
import com.bima.sunshine.models.Forecast;
import com.bima.sunshine.models.Temp;
import com.bima.sunshine.models.Weather;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by bimapw on 4/5/16.
 */
public class ForecasPresenter {

    private ForeCastView foreCastView;
    private String forecastJsonStr;
    private String format = "json";
    private String units = "metrics";
    private String days = "7";

    private HttpURLConnection urlConnection;
    private BufferedReader bufferedReader;

    public ForecasPresenter(ForeCastView foreCastView) {
        this.foreCastView = foreCastView;
    }

    public String getBaseUrl() {
        return "http://api.openweathermap.org/data/2.5/forecast/daily?";
    }

    public String getAppId(){
        return "b92f80be5981262933e52d95fb637140";
    }

    public String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        Gson gson = new Gson();
        Forecast forecast = gson.fromJson(forecastJsonStr, Forecast.class);

        Time dayTime = new Time();
        dayTime.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        dayTime = new Time();

        String[] resultStrs = new String[numDays];

        for(int i = 0; i < forecast.getList().size(); i++) {
            String day;
            String description;
            String highAndLow;

            Day dayForecast = forecast.getList().get(i);

            long dateTime;

            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            Weather weatherObject = dayForecast.getWeather().get(0);
            description = weatherObject.getMain();

            Temp temperatureObject = dayForecast.getTemp();
            double high = temperatureObject.getMax();
            double low = temperatureObject.getMin();

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

    public void doInBackground(String param) {
        String QUERY_PARAM = "q";
        String FORMAT_PARAM = "mode";
        String UNITS_PARAM = "units";
        String DAYS_PARAM = "cnt";
        String APPID_PARAM = "appid";

        try{
            Uri uri = Uri.parse(getBaseUrl()).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, param)
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
    }

    public void onPost() {
        foreCastView.showLoading(false);
        try {
            String[] data = getWeatherDataFromJson(forecastJsonStr, 7);
            ArrayList<String> foreCastArray = new ArrayList<>(Arrays.asList(data));
            foreCastView.setAdapter(foreCastArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
