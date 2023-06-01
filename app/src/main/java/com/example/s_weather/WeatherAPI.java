package com.example.s_weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("weather")
    Call<Adapter> getWeather(@Query("q") String cityname,
                             @Query("appid") String apikey);
}
