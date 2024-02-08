package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.PlaceList;
import com.qooke.levelrunproject.model.WeatherRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("/data/2.5/weather")
    Call<WeatherRes> getWeather(@Query("lat") double lat,
                                  @Query("lon") double lon,
                                  @Query("appid") String appid,
                                  @Query("units") String units,
                                  @Query("lang") String lang);

    @GET("/data/2.5/air_pollution")
    Call<WeatherRes> getCondition(@Query("lat") double lat,
                                  @Query("lon") double lon,
                                  @Query("appid") String appid
                                  );
    @GET("/data/2.5/forecast")
    Call<WeatherRes> getTimelyWeather(@Query("lat") double lat,
                                      @Query("lon") double lon,
                                      @Query("appid") String appid,
                                      @Query("units") String units,
                                      @Query("lang") String lang);

}
