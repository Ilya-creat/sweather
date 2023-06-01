
package com.example.s_weather;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.simple.parser.JSONParser;


import androidx.annotation.RequiresApi;

import com.google.gson.JsonObject;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WeatherClearActivity extends Activity {

	Spinner spinnerLang;

	Config lang;

	ArrayAdapter<String> languages;

	LanguageListener languageListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		languageListener = new LanguageListener();
		String[] language = getResources().getStringArray(R.array.languages);
		languages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				language);
		languages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


		JSON JsonCl = new JSON();
		CreateJson(JsonCl.CreateJSON());
		lang = new Config(
				ExportConfig()
		);

		if (lang.language.equals("ru")){
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

		spinnerLang.setOnItemSelectedListener(languageListener);





/*		buttonClick = (Button) findViewById(R.id.buttonClick) ;
		String url = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
		String apikey = "89dab736cfb0fa36b4137365d27b13e5";
		buttonClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Retrofit retrofit = new Retrofit.Builder()
						.baseUrl("https://api.openweathermap.org/data/2.5/")
						.addConverterFactory(GsonConverterFactory.create())
						.build();

				WeatherAPI MyApi = retrofit.create(WeatherAPI.class);
				Call<Adapter> ex = MyApi.getWeather(et.getText().toString().trim(), apikey);

				ex.enqueue(new Callback<Adapter>() {
					@Override
					public void onResponse(Call<Adapter> call, Response<Adapter> response) {
						if (response.code() == 404) {
							Toast.makeText(WeatherClearActivity.this, "Пожалуйста укажите город",
									Toast.LENGTH_LONG).show();
						} else if (!(response.isSuccessful())) {
							Toast.makeText(WeatherClearActivity.this, "Произошла ошибка!",
									Toast.LENGTH_LONG).show();
						} else {
							Adapter MyData = response.body();

							Main main = MyData.getMain();

							Double temp = main.getTemp();
							Integer temperature = (int) (temp - 273.15);
							tv.setText(String.valueOf(temperature) + "C");
						}
					}

					@Override
					public void onFailure(Call<Adapter> call, Throwable t) {
						Toast.makeText(WeatherClearActivity.this, t.getMessage(),
								Toast.LENGTH_LONG).show();
					}
				});
			}
		});*/
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

	public String ExportConfig() {
		File file = new File(this.getFilesDir(), "config.json");
		if (file.exists()) {
			try {
				org.json.simple.JSONObject readJson = (org.json.simple.JSONObject) readJsonSimpleDemo(this.getFilesDir() + "/config.json");
				return readJson.get("language").toString();
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

	public void UpDate(String key, String value) {
		File file = new File(this.getFilesDir(), "config.json");
		if (file.exists()) {
			try {
				org.json.simple.JSONObject readJson = (org.json.simple.JSONObject) readJsonSimpleDemo(this.getFilesDir() + "/config.json");
				System.out.println(readJson.get(key));
				readJson.put(key, value);
				System.out.println(readJson.get(key));
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
		@Override
		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			Log.d("POSITION", position + "");
			switch(position){
				case 0:
					if (!lang.language.equals("en")) {
						setContentView(R.layout.weather_clear_en);
						spinnerLang = findViewById(R.id.spinnerLang);
						spinnerLang.setAdapter(languages);
						spinnerLang.setSelection(0);
						UpDate("language", "en");
						lang.language = "en";
					}
					break;
				case 1:
					if (!lang.language.equals("ru")) {
						setContentView(R.layout.weather_clear_ru);
						spinnerLang = findViewById(R.id.spinnerLang);
						spinnerLang.setAdapter(languages);
						spinnerLang.setSelection(1);
						UpDate("language", "ru");
						lang.language = "ru";
					}
					break;
			}
			spinnerLang.setOnItemSelectedListener(languageListener);
		}
		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
		}

	}
}