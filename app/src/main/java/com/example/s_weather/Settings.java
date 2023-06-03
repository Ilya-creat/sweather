package com.example.s_weather;

import org.json.JSONObject;

public class Settings {

    public String url;
    public String appid;

    public JSONObject county;

    public String url_icon;


    public Settings(String url, String appid, JSONObject county, String url_icon) {
        this.url = url;
        this.appid = appid;
        this.county = county;
        this.url_icon = url_icon;
    }
}
