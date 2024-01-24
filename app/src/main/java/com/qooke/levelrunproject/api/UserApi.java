package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.User;
import com.qooke.levelrunproject.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {

    // 회원가입
    @POST("/user/register")
    Call<UserRes> register(@Body User user);

    // 로그인
    @POST("/user/login")
    Call<UserRes> login(@Body User user);

    @GET("/user/getNick")
        // 파라미터에 보낼 데이터를 적는다. 데이터는 묶어서 보낸다.
    Call<UserRes> getNick(@Query("nickName") String nickName,
                          @Query("email") String email
    );

}