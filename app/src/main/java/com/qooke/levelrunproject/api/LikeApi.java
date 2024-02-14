package com.qooke.levelrunproject.api;

import com.qooke.levelrunproject.model.LikeRes;
import com.qooke.levelrunproject.model.Res;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LikeApi {

    // 좋아요 API
    @POST("/like/{postingId}")
    Call<Res> setLike(@Header("Authorization") String token,
                      @Path("postingId") int postingId);

    // 좋아요 해지 API
    @GET("/like/{postingId}")
    Call<LikeRes> getLike(@Header("Authorization") String token,
                          @Path("postingId") int postingId);

}
