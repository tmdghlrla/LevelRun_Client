package com.qooke.levelrunproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.api.LikeApi;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PostingApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.MyAppUser;
import com.qooke.levelrunproject.model.Posting;
import com.qooke.levelrunproject.model.Ranker;
import com.qooke.levelrunproject.model.Res;
import com.qooke.levelrunproject.model.UserInfoRes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgBack;
    ImageView imgProfile;
    TextView txtRank;
    TextView txtLevel;
    TextView txtNickname;
    ImageView imgShare;
    ImageView imgPhoto;
    ImageView imgLikes;
    TextView txtLikerNickname;
    TextView txtLikerCnt;
    TextView txtTag;
    TextView txtContent;
    TextView txtCreateAt;
    LinearLayout btnLayout;
    Button btnChange;
    Button btnDelete;

    // 멤버변수화
    String nickName;
    String profileUrl;
    int level;
    String imgURL;
    String content;
    int userId;
    String createdAt;
    int likeCnt;
    int isLike;

    Ranker ranker;
    Posting posting;
    MyAppUser myAppUser;

    int index;
    ArrayList<Posting> postingArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        imgBack = findViewById(R.id.imgBack);
        imgProfile = findViewById(R.id.imgProfile);
        txtRank = findViewById(R.id.txtRank);
        txtLevel = findViewById(R.id.txtAirLevel);
        txtNickname = findViewById(R.id.txtNickName);
        imgShare = findViewById(R.id.imgShare);
        imgPhoto = findViewById(R.id.imgPhoto);
        imgLikes = findViewById(R.id.imgLikes);
        txtLikerNickname = findViewById(R.id.txtNickName);
        txtLikerCnt = findViewById(R.id.txtLikerCnt);
        txtTag = findViewById(R.id.txtTag);
        txtContent = findViewById(R.id.txtContent);
        txtCreateAt = findViewById(R.id.txtCreateAt);
        btnLayout = findViewById(R.id.btnLayout);
        btnChange = findViewById(R.id.btnChange);
        btnDelete = findViewById(R.id.btnDelete);


        // 소셜 프레그먼트 랭커 데이터 받아오기
        ranker = (Ranker) getIntent().getSerializableExtra("ranker");
        Log.i("AAA", "랭커 받아온 데이터 : " + ranker.nickName);

        if(ranker != null) {

            nickName = ranker.nickName;
            profileUrl = ranker.profileUrl;
            level = ranker.level;

            txtNickname.setText(nickName);
            txtRank.setText(level);
            Glide.with(PostDetailActivity.this).load(ranker.profileUrl).into(imgProfile);
        }

        // 소셜 프레그먼트 포스팅 데이터 받아오기
        posting = (Posting) getIntent().getSerializableExtra("posting");
        Log.i("AAA", "포스팅 받아온 데이터 : " + posting.userId);

        if (posting != null) {
            imgURL = posting.imgURL;
            content = posting.content;
            userId = posting.userId;
            createdAt = posting.createdAt;
            likeCnt = posting.likeCnt;
            isLike = posting.isLike;

            txtContent.setText(content);
            txtCreateAt.setText(createdAt);
            txtLikerCnt.setText(likeCnt);
            Glide.with(PostDetailActivity.this).load(posting.imgURL).into(imgPhoto);
        }


        // 뒤로가기 이미지 눌렀을때
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 좋아요 처리
        imgLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = NetworkClient.getRetrofitClient(PostDetailActivity.this);
                LikeApi api = retrofit.create(LikeApi.class);

                SharedPreferences sp = PostDetailActivity.this.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                String token = sp.getString("token", "");
                token = "Bearer " + token;

                if (posting.isLike == 0) {
                    // 좋아요 API
                    Call<Res> call = api.setLike(posting.id, token);
                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if (response.isSuccessful()) {
                                posting.isLike = 1;
                                return;

                            } else {

                            }
                        }
                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {

                        }
                    });

                } else {
                    // 좋아요 해지
                    Call<Res> call = api.deleteLike(posting.id, token);
                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if (response.isSuccessful()) {
                                posting.isLike = 0;
                                return;

                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {

                        }
                    });
                }
            }
        });

        if (posting.userId == myAppUser.id) {
            btnLayout.setVisibility(View.VISIBLE);

            // 포스팅 수정 버튼
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PostDetailActivity.this, PostEditActivity.class);
                    startActivity(intent);
                }
            });

            // 포스팅 삭제 버튼
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog();
                }
            });
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        builder.setCancelable(false);
        builder.setTitle("삭제");
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int postingId = posting.id;

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString("token", "");
                token = "Bearer " + token;

                Retrofit retrofit = NetworkClient.getRetrofitClient(PostDetailActivity.this);
                PostingApi api = retrofit.create(PostingApi.class);

                Call<Res> call = api.deletePost(postingId, token);
                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        if (response.isSuccessful()) {
                            postingArrayList.remove(index);
                            finish();
                            startActivity(getIntent());
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {

                    }
                });
            }
        });
        builder.show();
    }
}