package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.GachaData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GachaApi {

    @GET("/gacha")
    Call<GachaData> getGacha(@Header ("Authorization") String token);
}