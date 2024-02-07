package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.PlaceList;
import com.qooke.levelrunproject.model.TranslateRes;
import com.qooke.levelrunproject.model.Translate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface PapagoApi {
    @POST("/v1/papago/n2mt")
    Call<TranslateRes> getTranslate(@Header("X-Naver-Client-Id") String id,
                                    @Header("X-Naver-Client-Secret") String secret,
                                    @Body Translate translate);
}
