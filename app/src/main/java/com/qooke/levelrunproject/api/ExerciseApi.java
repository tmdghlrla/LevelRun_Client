package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.Exercise;
import com.qooke.levelrunproject.model.ExerciseRes;
import com.qooke.levelrunproject.model.Res;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ExerciseApi {
    @POST("/excercise")
    Call<Res> setRecord(@Header("Authorization") String token,
                        @Body Exercise excercise);
    @GET("/excercise")
    Call<ExerciseRes> getRecord(@Header("Authorization") String token);

    @GET("/excercise/list")
    Call<ExerciseRes> getRecordList(@Header("Authorization") String token);
}
