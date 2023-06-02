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
            json.put("latitude", 0.0);
            json.put("longitude", 0.0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public Settings ImportJSONSettings(String json){
        try {
            JSONObject js = new JSONObject(json);
            JSONObject api = js.getJSONObject("settings").getJSONObject("api");
            JSONObject county = js.getJSONObject("country");

            return new Settings(api.getString("url"), api.getString("appid"), county);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
