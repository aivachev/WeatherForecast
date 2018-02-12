package com.example.andrew.weatherforecast.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.andrew.weatherforecast.Controller.AppController;
import com.example.andrew.weatherforecast.model.Forecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Andrew on 07.02.2018.
 */

public class ForecastData {
    ArrayList<Forecast> forecastArrayList = new ArrayList<>();

    //nome%2C%20ak
    String urlOne = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22";
    String urlTwo = "%22%20)%20and%20u%3D'c'&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    //String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+CITY+"%2C%20"+REGION+"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    public void getForecast(final ForecastListAsyncResponse callback, String locationText) {
        String url = urlOne + locationText + urlTwo;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject query = response.getJSONObject("query");
                    JSONObject results = query.getJSONObject("results");
                    JSONObject channel = results.getJSONObject("channel");
                    JSONObject location = channel.getJSONObject("location");

                    //Item Object
                    JSONObject itemObject = channel.getJSONObject("item");

                    //Condition Object
                    JSONObject conditionObject = itemObject.getJSONObject("condition");

                    //Forecast JsonArray
                    JSONArray forecastArray = itemObject.getJSONArray("forecast");
                    for (int i = 0; i < forecastArray.length(); i++) {
                        JSONObject forecastObject = forecastArray.getJSONObject(i);

                        Forecast forecast = new Forecast();

                        forecast.setForecastDate(forecastObject.getString("date"));
                        forecast.setForecastDay(forecastObject.getString("day"));
                        forecast.setForecastHighTemp(forecastObject.getString("high"));
                        forecast.setForecastLowTemp(forecastObject.getString("low"));
                        forecast.setForecastWeatherDescription(forecastObject.getString("text"));

                        if (i == 0) {
                            forecast.setDate(conditionObject.getString("date"));
                            forecast.setCurrentTemperature(conditionObject.getString("temp"));
                            forecast.setCurrentWeatherDescription(conditionObject.getString("text"));
                            forecast.setCity(location.getString("city"));
                            forecast.setRegion(location.getString("region"));
                            forecast.setDescriptionHTML(itemObject.getString("description"));
                        }
                        forecastArrayList.add(forecast);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } if (null != callback) callback.processFinished(forecastArrayList);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

}
