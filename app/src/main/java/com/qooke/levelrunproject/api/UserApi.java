package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.MyAppUser;
import com.qooke.levelrunproject.model.Res;
import com.qooke.levelrunproject.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {

    // 회원가입
    @POST("/user/register")
    Call<UserRes> register(@Body MyAppUser user);

    // 로그인
    @POST("/user/login")
    Call<UserRes> loginDefault(@Body MyAppUser user);

    // 로그아웃 API
    @DELETE("/user/logout")
    Call<Res> logout(@Header("Authorization") String token);

    @POST("/user/kakaoLogin")
        // 파라미터에 보낼 데이터를 적는다. 데이터는 묶어서 보낸다.
    Call<UserRes> kakaoLogin(@Body MyAppUser user);
}
