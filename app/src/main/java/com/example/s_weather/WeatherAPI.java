package com.example.s_weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("weather")
    Call<com.example.s_weather.weather_now.Adapter> getWeather(@Query("lat") double lat,
                             @Query("lon") double lon, @Query("lang") String lang,
                             @Query("appid") String appid);

    @GET("forecast")
    Call<?> getWeatherNotOneDay(@Query("lat") double lat,
                                      @Query("lon") double lon, @Query("lang") String lang,
                                      @Query("appid") String appid);
}
