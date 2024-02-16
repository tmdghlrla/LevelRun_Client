package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.Excercise;
import com.qooke.levelrunproject.model.Res;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ExcerciseApi {
    @POST
    Call<Res> setRecord(@Header("Authorization") String token,
                        @Body Excercise excercise);
}
