package com.example.s_weather;

import org.json.JSONObject;

public class Settings {

    public String url;
    public String appid;

    public JSONObject county;


    public Settings(String url, String appid, JSONObject county) {
        this.url = url;
        this.appid = appid;
        this.county = county;
    }
}
