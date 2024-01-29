package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.RankerList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface RankerApi {

    // 랭킹 프레그먼트 리스트
    @GET("/rankerlist")
    Call<RankerList> rankerList(@Header("Authorization") String token);

    // 소셜 랭커 프로필 사진 가져오기
    @GET("/ranker")
    Call<RankerList> rankerimg(@Header("Authorization") String token);
}
