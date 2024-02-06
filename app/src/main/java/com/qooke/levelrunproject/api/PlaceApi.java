package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.PlaceList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceApi {
    @GET("/maps/api/place/nearbysearch/json")
    Call<PlaceList> getPlaceList(@Query("location") String location,
                                 @Query("radius") int radius,
                                 @Query("language") String language,
                                 @Query("keyword") String keyword,
                                 @Query("key") String apiKey);
}
