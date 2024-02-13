package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.Res;
import com.qooke.levelrunproject.model.TagRes;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AutoTagApi {
    @Multipart
    @POST("/posting/label")
    Call<TagRes> autoTag(@Header("Authorization") String token,
                         @Part MultipartBody.Part image);
}
