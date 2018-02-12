package com.example.andrew.weatherforecast.Util;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Andrew on 08.02.2018.
 */

public class Prefs {
    SharedPreferences preferences;

    public Prefs(Activity activity) {
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public void setLocation(String location) {
        preferences.edit().putString("location", location).apply();
    }

    public String getLocation() {
        return preferences.getString("location", "Moscow russia");
    }
}
