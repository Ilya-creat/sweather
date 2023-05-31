
package com.example.s_weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class weather_clear_activity extends Activity {

	Spinner spinnerLang;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_clear_en);

		spinnerLang = findViewById(R.id.spinnerLang);

		String[] language = getResources().getStringArray(R.array.languages);
		ArrayAdapter languages = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
				language);

		languages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerLang.setAdapter(languages);
	}
}
	
	