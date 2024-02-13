package com.qooke.levelrunproject.api;


import com.qooke.levelrunproject.model.Posting;
import com.qooke.levelrunproject.model.PostingList;
import com.qooke.levelrunproject.model.Res;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PostingApi {

    // 포스팅 생성
    @POST("/posting")
    Call<Res> addPosting(@Header("Authorization") String token,
                         @Body Posting tags);

    // 랭커 포스팅 가져오기
//    @GET("/posting/ranker")
//    Call<PostingList> getRankerPost(@Header("Authorization") String token,
//                                    @Query("offset") int offset,
//                                    @Query("limit") int limit);

    // 전체 포스팅 가져오기(최신순)
    @GET("/posting")
    Call<PostingList> getAllPosting(@Header("Authorization") String token,
                                    @Query("offset") int offset,
                                    @Query("limit") int limit);

    // 포스팅 상세정보
    @GET("/posting/{postingId}")
    Call<PostingList> postDetail(@Header("Authorization") String token);



    // 인기순 포스팅 가져오기
    @GET("/posting/popularity")
    Call<PostingList> popularityPosting(@Header("Authorization") String token,
                                        @Query("offset") int offset,
                                        @Query("limit") int limit);


}
