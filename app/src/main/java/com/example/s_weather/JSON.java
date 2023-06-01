package com.example.s_weather;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class JSON extends AppCompatActivity {
    JSONObject json = new JSONObject();

    public JSONObject CreateJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("language", "en");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
