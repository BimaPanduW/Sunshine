package com.bima.sunshine;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bima.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static int MY_FORECAST_LOADED_ID = 0;

    private ListView listviewForecast;
    private ProgressBar waitData;

    private String postalCode = "94043";
    private String format = "json";
    private String days = "7";
    private ForecastAdapter forecastAdapter;
    private String unitType;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        listviewForecast = (ListView) view.findViewById(R.id.listview_forecast);
        waitData = (ProgressBar) view.findViewById(R.id.wait_data);


        setHasOptionsMenu(true);

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        forecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        listviewForecast.setAdapter(forecastAdapter);

        waitData.setVisibility(View.GONE);
        listviewForecast.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateWeather();
                return true;
            default:


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {
        String location = Utility.getPreferredLocation(getActivity());
        new FetchWeatherTask(getActivity()).execute(location);
    }

    private String getBaseUrl() {
        return "http://api.openweathermap.org/data/2.5/forecast/daily?";
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

        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            long dateTime;

            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low, unitType);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
    }

    private String getReadableDateString(long time) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    private String formatHighLows(double high, double low, String unitType) {

        if (unitType.equals(getString(R.string.pref_units_imperial))) {
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        } else if (!unitType.equals(getString(R.string.pref_units_metric))) {
            Log.d("sunshine", "Unit type not found: " + unitType);
        }

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MY_FORECAST_LOADED_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        CursorLoader cur = new CursorLoader(getActivity(), weatherForLocationUri,
                null, null, null, sortOrder);

        return cur;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        forecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastAdapter.swapCursor(null);
    }
}
