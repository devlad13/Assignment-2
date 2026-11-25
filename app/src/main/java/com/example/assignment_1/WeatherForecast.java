
package com.example.assignment_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WeatherForecast extends AppCompatActivity {

    private static final String TAG = "WeatherForecast";

    private static final String API_KEY =
            "79cecf493cb6e52d25bb7b7050ff723c";

    private Spinner citySpinner;
    private ImageView weatherImage;
    private TextView currentTemperature;
    private TextView minTemperature;
    private TextView maxTemperature;
    private ProgressBar weatherProgress;

    private String selectedCity = "Ottawa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        // Initialize views
        citySpinner = findViewById(R.id.citySpinner);
        weatherImage = findViewById(R.id.weatherImage);
        currentTemperature = findViewById(R.id.currentTemperature);
        minTemperature = findViewById(R.id.minTemperature);
        maxTemperature = findViewById(R.id.maxTemperature);
        weatherProgress = findViewById(R.id.weatherProgress);

        // Initial progress visibility
        weatherProgress.setVisibility(View.INVISIBLE);

        // Canadian cities
        String[] cities = {
                "Ottawa",
                "Toronto",
                "Vancouver",
                "Montreal",
                "Calgary",
                "Edmonton",
                "Winnipeg",
                "Quebec City",
                "Halifax",
                "Saskatoon",
                "Regina",
                "Victoria",
                "Hamilton",
                "Waterloo",
                "Kitchener",
                "London",
                "Windsor",
                "St. John's",
                "Charlottetown",
                "Fredericton",
                "Yellowknife",
                "Whitehorse",
                "Iqaluit"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cities
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        citySpinner.setAdapter(adapter);

        // Fetch weather when a city is selected
        citySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        selectedCity = cities[position];

                        String weatherURL;

                        try {
                            String encodedCity = URLEncoder.encode(
                                    selectedCity,
                                    StandardCharsets.UTF_8.name()
                            );

                            weatherURL =
                                    "https://api.openweathermap.org/data/2.5/weather?q="
                                            + encodedCity
                                            + ",CA&APPID="
                                            + API_KEY
                                            + "&mode=xml&units=metric";
                            Log.i(TAG, "Weather URL: " + weatherURL);
                            // Start background task
                            weatherProgress.setProgress(0);
                            weatherProgress.setVisibility(View.VISIBLE);

                            new ForecastQuery().execute(weatherURL);

                        } catch (Exception e) {
                            Log.e(TAG, "Error creating weather URL", e);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // No action
                    }
                });
    }

    // Check whether image already exists in internal storage
    public boolean fileExistance(String fname) {

        File file = getBaseContext().getFileStreamPath(fname);

        return file.exists();
    }

    // Download and process weather information
    private class ForecastQuery
            extends android.os.AsyncTask<String, Integer, String> {

        private String currentTemp;
        private String minTemp;
        private String maxTemp;
        private String iconName;

        private Bitmap weatherBitmap;

        private String errorMessage;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            weatherProgress.setProgress(0);
            weatherProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... args) {

            HttpURLConnection connection = null;

            try {

                // Connect to weather API
                URL url = new URL(args[0]);

                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                connection.connect();

                int responseCode = connection.getResponseCode();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception(
                            "Weather API returned HTTP " + responseCode
                    );
                }

                InputStream inputStream = connection.getInputStream();

                // Create XML Pull Parser
                XmlPullParserFactory factory =
                        XmlPullParserFactory.newInstance();

                XmlPullParser parser = factory.newPullParser();

                parser.setInput(inputStream, "UTF-8");

                int eventType = parser.getEventType();

                // Parse XML
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {

                        String tagName = parser.getName();

                        if ("temperature".equals(tagName)) {

                            currentTemp = parser.getAttributeValue(null, "value");
                            publishProgress(25);

                            minTemp = parser.getAttributeValue(null, "min");
                            publishProgress(50);

                            maxTemp = parser.getAttributeValue(null, "max");
                            publishProgress(75);

                        } else if ("weather".equals(tagName)) {

                            iconName = parser.getAttributeValue(null, "icon");
                        }
                    }

                    eventType = parser.next();
                }

                inputStream.close();

                // Check that weather information was received
                if (currentTemp == null ||
                        minTemp == null ||
                        maxTemp == null ||
                        iconName == null) {

                    throw new Exception(
                            "Weather information missing from XML"
                    );
                }

                // Build image filename
                String imageFile = iconName + ".png";

                Log.i(TAG, "Looking for image: " + imageFile);

                // Check local storage first
                if (fileExistance(imageFile)) {

                    Log.i(TAG,
                            "Image found locally: " + imageFile);

                    FileInputStream fis = null;

                    try {

                        fis = openFileInput(imageFile);

                        weatherBitmap =
                                BitmapFactory.decodeStream(fis);

                    } finally {

                        if (fis != null) {
                            fis.close();
                        }
                    }

                } else {

                    Log.i(TAG,
                            "Image not found locally. Downloading: "
                                    + imageFile);

                    // Download weather icon
                    String imageURL =
                            "https://openweathermap.org/img/w/"
                                    + iconName + ".png";

                    URL imageUrl = new URL(imageURL);

                    HttpURLConnection imageConnection =
                            (HttpURLConnection) imageUrl.openConnection();

                    imageConnection.setConnectTimeout(15000);
                    imageConnection.setReadTimeout(15000);

                    try {

                        imageConnection.connect();

                        if (imageConnection.getResponseCode()
                                != HttpURLConnection.HTTP_OK) {

                            throw new Exception(
                                    "Image download failed: HTTP "
                                            + imageConnection.getResponseCode()
                            );
                        }

                        try (InputStream imageStream =
                                     imageConnection.getInputStream()) {

                            weatherBitmap =
                                    BitmapFactory.decodeStream(imageStream);
                        }

                    } finally {

                        imageConnection.disconnect();
                    }

                    if (weatherBitmap == null) {
                        throw new Exception("Unable to decode weather image");
                    }

                    // Save image in internal storage
                    try (FileOutputStream outputStream =
                                 openFileOutput(
                                         imageFile,
                                         MODE_PRIVATE
                                 )) {

                        weatherBitmap.compress(
                                Bitmap.CompressFormat.PNG,
                                80,
                                outputStream
                        );

                        outputStream.flush();
                    }

                    Log.i(TAG,
                            "Image downloaded and saved: " + imageFile);
                }

                if (weatherBitmap == null) {
                    throw new Exception("Weather image is unavailable");
                }

                // Download completed
                publishProgress(100);

                return "SUCCESS";

            } catch (Exception e) {

                Log.e(TAG, "Weather download error", e);

                errorMessage = e.getMessage();

                return "ERROR";

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);

            weatherProgress.setVisibility(View.VISIBLE);

            weatherProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            weatherProgress.setVisibility(View.INVISIBLE);

            if ("SUCCESS".equals(result)) {

                currentTemperature.setText(
                        "Current: " + currentTemp + " °C"
                );

                minTemperature.setText(
                        "Minimum: " + minTemp + " °C"
                );

                maxTemperature.setText(
                        "Maximum: " + maxTemp + " °C"
                );

                weatherImage.setImageBitmap(weatherBitmap);

                Log.i(TAG,
                        "Weather information displayed successfully");

            } else {

                Toast.makeText(
                        WeatherForecast.this,
                        "Unable to load weather: " + errorMessage,
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}
