package com.example.andrew.weatherforecast.data;

import com.example.andrew.weatherforecast.model.Forecast;

import java.util.ArrayList;

/**
 * Created by Andrew on 07.02.2018.
 */

public interface ForecastListAsyncResponse {
    void processFinished(ArrayList<Forecast> forecastArrayList);
}
