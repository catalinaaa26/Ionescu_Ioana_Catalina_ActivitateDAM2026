package ro.example.weather;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherActivity extends Activity {
    private static final String BASE_URL = "https://dataservice.accuweather.com";
    private static final String STATE_CITY_KEY = "weather_city_key";
    private static final String STATE_CITY_NAME = "weather_city_name";
    private static final String STATE_QUERY = "weather_query";
    private static final String STATE_RESULT = "weather_result";
    private static final String STATE_BUTTON_MODE = "weather_button_mode";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executor;
    private TextInputEditText cityInput;
    private Spinner daysSpinner;
    private MaterialButton actionButton;
    private TextView resultText;
    private String cityKey;
    private String cityName;
    private String searchedQuery;
    private boolean showingForecast;
    private boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        executor = Executors.newSingleThreadExecutor();
        cityInput = findViewById(R.id.input_weather_city);
        daysSpinner = findViewById(R.id.spinner_weather_days);
        actionButton = findViewById(R.id.button_weather_action);
        resultText = findViewById(R.id.text_weather_result);
        View backButton = findViewById(R.id.button_weather_back);
        backButton.setOnClickListener(view -> finish());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.weather_forecast_days,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(adapter);

        if (savedInstanceState != null) {
            cityKey = savedInstanceState.getString(STATE_CITY_KEY);
            cityName = savedInstanceState.getString(STATE_CITY_NAME);
            searchedQuery = savedInstanceState.getString(STATE_QUERY);
            showingForecast = savedInstanceState.getBoolean(STATE_BUTTON_MODE, false);
            String savedResult = savedInstanceState.getString(STATE_RESULT);
            if (savedResult != null) {
                resultText.setText(savedResult);
            }
            updateButtonLabel();
        }

        cityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String currentQuery = editable.toString().trim();
                if (cityKey != null && !currentQuery.equals(searchedQuery)) {
                    cityKey = null;
                    cityName = null;
                    searchedQuery = null;
                    showingForecast = false;
                    updateButtonLabel();
                    resultText.setText(R.string.weather_result_placeholder);
                }
            }
        });

        actionButton.setOnClickListener(view -> {
            if (cityKey == null) {
                searchCity();
            } else {
                loadForecast();
            }
        });
    }

    private void searchCity() {
        String query = cityInput.getText() == null ? "" : cityInput.getText().toString().trim();
        if (query.isEmpty()) {
            cityInput.setError(getString(R.string.weather_city_required));
            return;
        }
        if (!hasApiKey()) {
            resultText.setText(R.string.weather_setup_body);
            Toast.makeText(this, R.string.weather_api_key_missing, Toast.LENGTH_LONG).show();
            return;
        }

        setLoading(true, R.string.weather_searching);
        executor.execute(() -> {
            try {
                String encodedCity = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
                String path = "/locations/v1/cities/search?q=" + encodedCity + "&language=ro-ro";
                JSONArray locations = new JSONArray(request(path));
                if (locations.length() == 0) {
                    throw new IllegalStateException(getString(R.string.weather_city_not_found));
                }

                JSONObject location = locations.getJSONObject(0);
                String key = location.getString("Key");
                String name = location.optString("LocalizedName", query);
                JSONObject administrativeArea = location.optJSONObject("AdministrativeArea");
                JSONObject country = location.optJSONObject("Country");
                String area = administrativeArea == null
                        ? ""
                        : administrativeArea.optString("LocalizedName", "");
                String countryName = country == null
                        ? ""
                        : country.optString("LocalizedName", "");

                String locationDetails = getString(
                        R.string.weather_location_details,
                        name,
                        area,
                        countryName
                );
                mainHandler.post(() -> {
                    cityKey = key;
                    cityName = name;
                    searchedQuery = query;
                    showingForecast = false;
                    resultText.setText(locationDetails + "\n\n"
                            + getString(R.string.weather_location_key, key));
                    updateButtonLabel();
                    setLoading(false, 0);
                });
            } catch (Exception exception) {
                mainHandler.post(() -> showError(exception));
            }
        });
    }

    private void loadForecast() {
        if (cityKey == null || !hasApiKey()) {
            return;
        }

        final String requestedCityKey = cityKey;
        final String requestedCityName = cityName;
        final int days = daysSpinner.getSelectedItemPosition() == 0 ? 1 : 5;
        setLoading(true, R.string.weather_loading_forecast);
        executor.execute(() -> {
            try {
                String path = "/forecasts/v1/daily/" + days + "day/" + requestedCityKey
                        + "?language=ro-ro&details=false&metric=true";
                JSONObject response = new JSONObject(request(path));
                JSONArray dailyForecasts = response.getJSONArray("DailyForecasts");
                String forecastText = formatForecasts(dailyForecasts, requestedCityName, days);
                mainHandler.post(() -> {
                    showingForecast = true;
                    resultText.setText(forecastText);
                    updateButtonLabel();
                    setLoading(false, 0);
                });
            } catch (Exception exception) {
                mainHandler.post(() -> showError(exception));
            }
        });
    }

    private String formatForecasts(JSONArray forecasts, String locationName, int days)
            throws JSONException {
        if (forecasts.length() == 0) {
            throw new IllegalStateException(getString(R.string.weather_network_error));
        }

        String dayCount = getResources().getQuantityString(R.plurals.weather_days, days, days);
        StringBuilder output = new StringBuilder();
        output.append(getString(R.string.weather_forecast_heading, locationName, dayCount));
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.getDefault());
        DecimalFormat temperatureFormat = new DecimalFormat("0.#", symbols);

        for (int index = 0; index < forecasts.length(); index++) {
            JSONObject forecast = forecasts.getJSONObject(index);
            JSONObject temperature = forecast.getJSONObject("Temperature");
            JSONObject minimum = temperature.getJSONObject("Minimum");
            JSONObject maximum = temperature.getJSONObject("Maximum");
            String unit = minimum.optString("Unit", "C");
            if (!unit.startsWith("°")) {
                unit = "°" + unit;
            }
            String date = forecast.optString("Date", "");
            if (date.length() >= 10) {
                date = date.substring(0, 10);
            }

            output.append("\n\n")
                    .append(getString(R.string.weather_date, date))
                    .append('\n')
                    .append(getString(
                            R.string.weather_min_max,
                            temperatureFormat.format(minimum.getDouble("Value")),
                            temperatureFormat.format(maximum.getDouble("Value")),
                            unit
                    ));

            JSONObject day = forecast.optJSONObject("Day");
            JSONObject night = forecast.optJSONObject("Night");
            if (day != null && day.has("IconPhrase")) {
                output.append('\n').append(getString(
                        R.string.weather_day_phrase,
                        day.optString("IconPhrase")
                ));
            }
            if (night != null && night.has("IconPhrase")) {
                output.append('\n').append(getString(
                        R.string.weather_night_phrase,
                        night.optString("IconPhrase")
                ));
            }
        }
        return output.toString();
    }

    private String request(String path) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(BASE_URL + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty(
                    "Authorization",
                    "Bearer " + BuildConfig.ACCUWEATHER_API_KEY
            );
            connection.setRequestProperty("Accept", "application/json");

            int status = connection.getResponseCode();
            InputStream stream = status >= 200 && status < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            String body = readBody(stream);
            if (status < 200 || status >= 300) {
                throw new IOException(getString(R.string.weather_http_error, status)
                        + (body.isEmpty() ? "" : ": " + body));
            }
            return body;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readBody(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }

    private boolean hasApiKey() {
        return BuildConfig.ACCUWEATHER_API_KEY != null
                && !BuildConfig.ACCUWEATHER_API_KEY.trim().isEmpty();
    }

    private void setLoading(boolean isLoading, int messageResource) {
        loading = isLoading;
        actionButton.setEnabled(!isLoading);
        if (isLoading) {
            actionButton.setText(messageResource);
        } else {
            updateButtonLabel();
        }
    }

    private void updateButtonLabel() {
        if (actionButton == null || loading) {
            return;
        }

        if (cityKey == null) {
            actionButton.setText(R.string.weather_search_button);
        } else if (showingForecast) {
            actionButton.setText(R.string.weather_refresh_button);
        } else {
            actionButton.setText(R.string.weather_forecast_button);
        }
    }

    private void showError(Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = getString(R.string.weather_network_error);
        }
        resultText.setText(getString(R.string.weather_network_error) + "\n\n" + message);
        setLoading(false, 0);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_CITY_KEY, cityKey);
        outState.putString(STATE_CITY_NAME, cityName);
        outState.putString(STATE_QUERY, searchedQuery);
        outState.putBoolean(STATE_BUTTON_MODE, showingForecast);
        outState.putString(STATE_RESULT, resultText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (executor != null) {
            executor.shutdownNow();
        }
        mainHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
