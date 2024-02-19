package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.MissionRes;
import com.qooke.levelrunproject.model.RankerRes;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MissionApi {
    // 임무 완료
    @POST("/mission")
    Call<RankerRes> setExp(@Header("Authorization") String token,
                           @Body int mission);

    // 레벨 정보 가져오기
    @GET("/user/mission")
    Call<MissionRes> getMissionInfo(@Header("Authorization") String token);
}
