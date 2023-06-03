package com.example.s_weather;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("weather")
    Call<com.example.s_weather.weather_now.Adapter> getWeather(@Query("lat") double lat,
                             @Query("lon") double lon, @Query("lang") String lang,
                             @Query("appid") String appid);

    @GET("forecast")
    Call<com.example.s_weather.weather_all.Adapter> getWeatherAll(@Query("lat") double lat,
                                      @Query("lon") double lon, @Query("lang") String lang,
                                      @Query("appid") String appid);

    @GET("{id}")
    Call<ResponseBody> getIcon(@Path("id") String id, @Query("appid") String appid);
}
