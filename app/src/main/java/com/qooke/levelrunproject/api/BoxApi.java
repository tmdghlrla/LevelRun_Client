package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.Res;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface BoxApi {
    @PUT("/box")
    Call<Res> getBox(@Header("Authorization") String token);
}
