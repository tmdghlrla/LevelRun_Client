package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.User;
import com.qooke.levelrunproject.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    // 회원가입
    @POST("/user/register")
    Call<UserRes> register(@Body User user);

    // 로그인
    @POST("/user/login")
    Call<UserRes> login(@Body User user);

}
