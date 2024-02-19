package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.MyAppUser;
import com.qooke.levelrunproject.model.Res;
import com.qooke.levelrunproject.model.UserInfoRes;
import com.qooke.levelrunproject.model.UserRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserApi {
    // 회원가입
    @POST("/user/register")
    Call<UserRes> register(@Body MyAppUser user);

    // 로그인
    @POST("/user/login")
    Call<UserRes> login(@Body MyAppUser user);

    // 카카오 로그인
    @POST("/user/kakaoLogin")
        // 파라미터에 보낼 데이터를 적는다. 데이터는 묶어서 보낸다.
    Call<UserRes> kakaoLogin(@Body MyAppUser user);

    // 유저 정보 수정
    @PUT("/user")
    Call<Res> profileChange(@Header("Authorization") String token,
                         @Part MultipartBody.Part image,
                         @Part ("nickName") RequestBody nickName);

    // 로그아웃
    @DELETE("/user/logout")
    Call<Res> logout(@Header("Authorization") String token);

    // 유정 정보 가져오기
    @GET("/user")
    // 파라미터에 보낼 데이터를 적는다. 데이터는 묶어서 보낸다.
    Call<UserInfoRes> getUserInfo(@Header("Authorization") String token);
}