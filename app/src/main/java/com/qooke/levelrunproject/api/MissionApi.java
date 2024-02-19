package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.RankerRes;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MissionApi {
    // 경험치 획득
    @POST("/mission")
    Call<RankerRes> setExp(@Header("Authorization") String token,
                           @Body int mission);
}
