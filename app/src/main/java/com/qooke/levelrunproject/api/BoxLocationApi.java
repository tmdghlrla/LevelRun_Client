package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.BoxLocationRes;
import com.qooke.levelrunproject.model.MyAppUser;
import com.qooke.levelrunproject.model.RandomBox;
import com.qooke.levelrunproject.model.Res;
import com.qooke.levelrunproject.model.UserInfoRes;
import com.qooke.levelrunproject.model.UserRes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface BoxLocationApi {
    // 박스 위치 정보 가져오기
    @GET("/randomBox")
    // 파라미터에 보낼 데이터를 적는다. 데이터는 묶어서 보낸다.
    Call<BoxLocationRes> boxLocation(@Header("Authorization") String token);
}
