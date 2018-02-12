package com.example.andrew.weatherforecast;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrew.weatherforecast.Util.Prefs;
import com.example.andrew.weatherforecast.data.ForecastData;
import com.example.andrew.weatherforecast.data.ForecastFragment;
import com.example.andrew.weatherforecast.data.ForecastListAsyncResponse;
import com.example.andrew.weatherforecast.data.ForecastViewPagerAdapter;
import com.example.andrew.weatherforecast.model.Forecast;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ForecastViewPagerAdapter forecastViewPagerAdapter;
    private ViewPager viewPager;
    private TextView locationText;
    private TextView currentTempText;
    private TextView currentDate;
    private EditText userLocationText;
    private String userEnteredString;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationText = findViewById(R.id.locationTextViewId);
        currentTempText = findViewById(R.id.currentTempID);
        currentDate = findViewById(R.id.currentsDateID);
        imageView = findViewById(R.id.weatherIcon);

        //Log.d("IMAGE", getImageUrl());

        final Prefs prefs = new Prefs(this);
        String prefsLocation = prefs.getLocation();
        getWeather(prefsLocation);

        userLocationText = findViewById(R.id.locationNameId);

        userLocationText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && i == KeyEvent.KEYCODE_ENTER) {
                    //Toast.makeText(getApplicationContext(), userLocationText.getText().toString(),
                    //        Toast.LENGTH_SHORT).show();

                    userEnteredString = userLocationText.getText().toString();
                    prefs.setLocation(userEnteredString);
                    getWeather(userEnteredString);

                    return true;
                }
                return false;
            }
        });
    }

    private String getImageUrl(String html) {
        String imgRegex = "(?i)<img[^>]+?src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
        //String htmlString = "<![CDATA[<img src=\"http://l.yimg.com/a/i/us/we/52/23.gif\"/>\n<BR />\n<b>Current Conditions:</b>\n<BR />Breezy\n<BR />\n<BR />\n<b>Forecast:</b>\n<BR /> Thu - Breezy. High: 16Low: 14\n<BR /> Fri - Breezy. High: 21Low: 14\n<BR /> Sat - Breezy. High: 25Low: 20\n<BR /> Sun - Breezy. High: 31Low: 23\n<BR /> Mon - Mostly Cloudy. High: 28Low: 22\n<BR />\n<BR />\n<a href=\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-2460286/\">Full Forecast at Yahoo! Weather</a>\n<BR />\n<BR />\n<BR />\n]]>";
        String imgSrc = null;

        Pattern p = Pattern.compile(imgRegex);
        Matcher m = p.matcher(html);

        while (m.find()) {
            imgSrc = m.group(1);

            //String base = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/tanadgomaaa";
            //String imagePath = "file://" + base + "/test.jpg";
            //imgSrc = imgSrc.replace(imgSrc, imagePath);
        }
        return imgSrc;
    }

    private void getWeather(String currentLocation) {

        forecastViewPagerAdapter = new ForecastViewPagerAdapter(getSupportFragmentManager(),
                getFragments(currentLocation));

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(forecastViewPagerAdapter);
    }

    private List<Fragment> getFragments(String locationString) {
        final List<Fragment> fragmentList = new ArrayList<>();

        new ForecastData().getForecast(new ForecastListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Forecast> forecastArrayList) {

                fragmentList.clear();

                Picasso.with(getApplicationContext())
                        .load(getImageUrl(forecastArrayList.get(0).getDescriptionHTML()))
                        .into(imageView);
                locationText.setText(" " + forecastArrayList.get(0).getCity() + ", \n" + forecastArrayList.get(0).getRegion());
                currentTempText.setText(forecastArrayList.get(0).getCurrentTemperature() + "C");


                SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd yyyy", Locale.US);
                Date dt = new Date();
                try {
                    dt = sdf.parse(forecastArrayList.get(0).getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentDate.setText("Today - " + sdf.format(dt));

                for (int i = 0; i < forecastArrayList.size(); i++) {
                    ForecastFragment forecastFragment = ForecastFragment.newInstance(forecastArrayList.get(i));
                    fragmentList.add(forecastFragment);
                }
                forecastViewPagerAdapter.notifyDataSetChanged();
            }
        }, locationString);
        return fragmentList;
    }
}