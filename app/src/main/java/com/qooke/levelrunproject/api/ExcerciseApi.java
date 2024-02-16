package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.Exercise;
import com.qooke.levelrunproject.model.ExcerciseRes;
import com.qooke.levelrunproject.model.Res;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ExcerciseApi {
    @POST("/excercise")
    Call<Res> setRecord(@Header("Authorization") String token,
                        @Body Exercise excercise);
    @GET("/excercise")
    Call<ExcerciseRes> getRecord(@Header("Authorization") String token);
}
