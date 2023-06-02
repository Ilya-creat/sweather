
package com.example.s_weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.simple.parser.JSONParser;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherClearActivity extends Activity {

	Spinner spinnerLang;

	Config ConfigJSON;

	ArrayAdapter<String> languages;

	LanguageListener languageListener;

	private LocationRequest locationRequest;

	Settings API;

	TextView city;

	JSON JsonCl = new JSON();
	Retrofit retrofit;
	WeatherAPI WeatherAPI;
	Call<com.example.s_weather.weather_now.Adapter> WeatherNowRu, WeatherNowEn;
	com.example.s_weather.weather_now.Adapter MetaDataEN, MetaDataRU;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		languageListener = new LanguageListener();
		String[] language = getResources().getStringArray(R.array.languages);
		languages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				language);
		languages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		CreateJson(JsonCl.CreateJSON());
		ConfigJSON = ExportConfig();

		if (ConfigJSON.language.equals("ru")){
			setContentView(R.layout.weather_clear_ru);
			spinnerLang = findViewById(R.id.spinnerLang);
			spinnerLang.setAdapter(languages);
			spinnerLang.setSelection(1);
		}else{
			setContentView(R.layout.weather_clear_en);
			spinnerLang = findViewById(R.id.spinnerLang);
			spinnerLang.setAdapter(languages);
			spinnerLang.setSelection(0);
		}

		city =  (TextView) findViewById(R.id.city);


		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(5000);
		locationRequest.setFastestInterval(2000);

		Log.d("GPS", ConfigJSON.language + " " + String.valueOf(ConfigJSON.latitude) + " " + String.valueOf(ConfigJSON.longitude));

		if (ActivityCompat.checkSelfPermission(WeatherClearActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
			if (isGPSEnabled()){
				LocationServices.getFusedLocationProviderClient(WeatherClearActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
					@Override
					public void onLocationResult(@NonNull LocationResult locationResult) {
						super.onLocationResult(locationResult);

						LocationServices.getFusedLocationProviderClient(WeatherClearActivity.this ).removeLocationUpdates(this);

						if (locationResult != null && locationResult.getLocations().size() > 0){
							double latitude = locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLatitude();
							double longitude = locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLongitude();
							UpDate("latitude", latitude);
							UpDate("longitude", longitude);
							//Toast.makeText(WeatherClearActivity.this, String.valueOf(latitude) + " " + String.valueOf(longitude), Toast.LENGTH_SHORT).show();
						}
					}
				}, Looper.getMainLooper());
			}else {
				GPSTurnON();
			}
		}else {
			requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		}
		API = null;
		try {
			API = JsonCl.ImportJSONSettings(JSONSettings());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		retrofit = new Retrofit.Builder()
				.baseUrl("https://api.openweathermap.org/data/2.5/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		WeatherAPI = retrofit.create(WeatherAPI.class);
		WeatherNowRu = WeatherAPI.getWeather(ConfigJSON.latitude, ConfigJSON.longitude, "ru", API.appid);
		WeatherNowEn = WeatherAPI.getWeather(ConfigJSON.latitude, ConfigJSON.longitude, "en", API.appid);
		WeatherNowRu.enqueue(new Callback<com.example.s_weather.weather_now.Adapter>() {
			@Override
			public void onResponse(@NonNull Call<com.example.s_weather.weather_now.Adapter> call, @NonNull Response<com.example.s_weather.weather_now.Adapter> response) {
				if (!(response.isSuccessful())) {
					Toast.makeText(WeatherClearActivity.this, "Произошла ошибка!",
							Toast.LENGTH_LONG).show();
				} else {
					MetaDataRU = response.body();
					//Log.d("CITY", MyDataRu.getName());

				}
			}

			@Override
			public void onFailure(@NonNull Call<com.example.s_weather.weather_now.Adapter> call, @NonNull Throwable t) {
				Toast.makeText(WeatherClearActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		});
		WeatherNowEn.enqueue(new Callback<com.example.s_weather.weather_now.Adapter>() {
			@Override
			public void onResponse(@NonNull Call<com.example.s_weather.weather_now.Adapter> call, @NonNull Response<com.example.s_weather.weather_now.Adapter> response) {
				if (!(response.isSuccessful())) {
					Toast.makeText(WeatherClearActivity.this, "Error!",
							Toast.LENGTH_LONG).show();
				} else {
					MetaDataEN = response.body();
				}
			}

			@Override
			public void onFailure(@NonNull Call<com.example.s_weather.weather_now.Adapter> call, @NonNull Throwable t) {
				Toast.makeText(WeatherClearActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		});
		//Log.d("MyData", "1");
		new Thread(new Runnable() {
			@SuppressLint("SetTextI18n")
			@Override
			public void run() {
				while (MetaDataEN == null || MetaDataRU == null) {}
				if (ConfigJSON.language.equals("ru")) {
					try {
						//Log.d("COUNTRY", MetaDataRU.getName() + ", " + API.county.getJSONObject(MetaDataRU.getSys().getCountry()).getString("RU"));
						city.setText(MetaDataRU.getName() + ", " + API.county.getJSONObject(MetaDataRU.getSys().getCountry()).getString("RU"));
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				} else {
					try {
						city.setText(MetaDataEN.getName() + ", " + API.county.getJSONObject(MetaDataEN.getSys().getCountry()).getString("EN"));
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}).start();

		spinnerLang.setOnItemSelectedListener(languageListener);
	}

	private void GPSTurnON() {

		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
				.addLocationRequest(locationRequest);
		builder.setAlwaysShow(true);

		Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
				.checkLocationSettings(builder.build());

		result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
			@Override
			public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

				try {
					LocationSettingsResponse response = task.getResult(ApiException.class);
					Toast.makeText(WeatherClearActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

				} catch (ApiException e) {

					switch (e.getStatusCode()) {
						case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

							try {
								ResolvableApiException resolvableApiException = (ResolvableApiException) e;
								resolvableApiException.startResolutionForResult(WeatherClearActivity.this, 2);
							} catch (IntentSender.SendIntentException ex) {
								ex.printStackTrace();
							}
							break;

						case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
							//Device does not have location
							break;
					}
				}
			}
		});
	}

	public void CreateJson(JSONObject json) {
		File file = new File(this.getFilesDir(), "config.json");
		if (!file.exists()) {
			try {
				String jsonString = json.toString();
				FileOutputStream fos = this.openFileOutput("config.json", Context.MODE_PRIVATE);
				//Log.d("PATH", String.valueOf(this.getFilesDir()));
				fos.write(jsonString.getBytes());
				fos.close();
				//Toast.makeText(this, "Created!", Toast.LENGTH_SHORT).show();
				//Log.d("JSON", json.toString());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Config ExportConfig() {
		File file = new File(this.getFilesDir(), "config.json");
		if (file.exists()) {
			try {
				org.json.simple.JSONObject readJson = (org.json.simple.JSONObject) readJsonSimpleDemo(this.getFilesDir() + "/config.json");
				return new Config(readJson.get("language").toString(), Double.parseDouble(readJson.get("latitude").toString()), Double.parseDouble(readJson.get("longitude").toString()));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}	else{
			Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
		}
		return null;
	}

	public void UpDate(String key, double value) {
		File file = new File(this.getFilesDir(), "config.json");
		if (file.exists()) {
			try {
				org.json.simple.JSONObject readJson = (org.json.simple.JSONObject) readJsonSimpleDemo(this.getFilesDir() + "/config.json");
				//System.out.println(readJson.get(key));
				readJson.put(key, value);
				//System.out.println(readJson);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					Files.write(Paths.get(this.getFilesDir() + "/config.json"), readJson.toJSONString().getBytes());
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}	else{
			Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
		}
	}

	public void UpDate(String key, String value) {
		File file = new File(this.getFilesDir(), "config.json");
		if (file.exists()) {
			try {
				org.json.simple.JSONObject readJson = (org.json.simple.JSONObject) readJsonSimpleDemo(this.getFilesDir() + "/config.json");
				//System.out.println(readJson.get(key));
				readJson.put(key, value);
				//System.out.println(readJson.get(key));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					Files.write(Paths.get(this.getFilesDir() + "/config.json"), readJson.toJSONString().getBytes());
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}	else{
			Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
		}
	}

	public String JSONSettings() throws IOException {
		InputStream is = null;
		try {
			is = getAssets().open("settings.json");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int size = is.available();
		byte[] buffer = new byte[size];

		is.read(buffer);
		is.close();

		return new String(buffer, "UTF-8");
	}


	public Object readJsonSimpleDemo(String filename) throws Exception {
		FileReader reader = new FileReader(filename);
		JSONParser jsonParser = new JSONParser();
		return jsonParser.parse(reader);
	}

	class LanguageListener implements AdapterView.OnItemSelectedListener {
		@SuppressLint("SetTextI18n")
		@Override
		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			//Log.d("POSITION", position + "");
			switch(position){
				case 0:
					if (!ConfigJSON.language.equals("en")) {
						setContentView(R.layout.weather_clear_en);
						spinnerLang = findViewById(R.id.spinnerLang);
						city = findViewById(R.id.city);
						spinnerLang.setAdapter(languages);
						spinnerLang.setSelection(0);
						UpDate("language", "en");
						ConfigJSON = ExportConfig();
					}
					break;
				case 1:
					if (!ConfigJSON.language.equals("ru")) {
						setContentView(R.layout.weather_clear_ru);
						spinnerLang = findViewById(R.id.spinnerLang);
						city = findViewById(R.id.city);
						spinnerLang.setAdapter(languages);
						spinnerLang.setSelection(1);
						UpDate("language", "ru");
						ConfigJSON = ExportConfig();
					}
					break;
			}
			//Log.d("API", API.appid);
			if (MetaDataEN != null && MetaDataRU != null) {
				if (ConfigJSON.language.equals("ru")) {
					try {
						//Log.d("COUNTRY", MetaDataRU.getName() + ", " + API.county.getJSONObject(MetaDataRU.getSys().getCountry()).getString("RU"));
						city.setText(MetaDataRU.getName() + ", " + API.county.getJSONObject(MetaDataRU.getSys().getCountry()).getString("RU"));
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				} else {
					try {
						city.setText(MetaDataEN.getName() + ", " + API.county.getJSONObject(MetaDataEN.getSys().getCountry()).getString("EN"));
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				}
			}
			spinnerLang.setOnItemSelectedListener(languageListener);

		}
		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
		}

	}

	private boolean isGPSEnabled() {
		LocationManager locationManager = null;
		boolean isEnabled  = false;

		if (locationManager == null){
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}

		isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return isEnabled;
	}
}