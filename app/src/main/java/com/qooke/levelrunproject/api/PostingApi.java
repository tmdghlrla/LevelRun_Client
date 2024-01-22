package com.qooke.levelrunproject.api;


import com.qooke.levelrunproject.model.PostingList;
import com.qooke.levelrunproject.model.Res;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PostingApi {

    // 포스팅 생성 API
    @Multipart
    @POST("/posting")
    Call<Res> addPosting(@Header("Authorization") String token,
                         @Part MultipartBody.Part image,
                         @Part ("content")RequestBody content);

    // 최신순 포스팅 가져오는 API
    @GET("/posting/latest")
    Call<PostingList> latestPosting(@Header("Authorization") String token,
                                    @Query("offset") int offset,
                                    @Query("limit") int limit);


    // 인기순 포스팅 가져오는 API
    @GET("/posting/popularity")
    Call<PostingList> popularityPosting(@Header("Authorization") String token,
                                   @Query("offset") int offset,
                                   @Query("limit") int limit);

    // 내 포스팅 리스트 가져오는 API
    @GET("/posting/me")
    Call<PostingList> getMyPosting(@Header("Authorization") String token,
                                   @Query("offset") int offset,
                                   @Query("limit") int limit);
}
