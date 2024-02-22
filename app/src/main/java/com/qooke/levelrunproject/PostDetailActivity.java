package com.qooke.levelrunproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.api.LikeApi;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PostingApi;
import com.qooke.levelrunproject.api.RankerApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.LikeRes;
import com.qooke.levelrunproject.model.Posting;
import com.qooke.levelrunproject.model.PostingDetail;
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
    TextView txtCreatedAt;
    TextView txtLikers;
    TextView txtLike;
    ImageView imgDefault;
    LinearLayout btnLayout;
    Button btnChange;
    Button btnDelete;
    LinearLayout linearLayout;

    // 멤버변수화
    String nickName = "";
    String profileUrl = "";
    int level = 0;
    int ranking;
    String imgURL = "";
    String content = "";
    String createdAt = "";
    int postingId = 0;
    int likeCnt = 0;
    int isLike = 0;
    int index = 0;
    int userId = 0;
    String tags = "";
    Ranker ranker;
    Posting posting;
    int postingUserId = 0;
    ArrayList<UserInfoRes> userInfoResArrayList = new ArrayList<>();

    ArrayList<Posting> postingArrayList = new ArrayList<>();
    ArrayList<Ranker> rankerArrayList = new ArrayList<>();
    PostingDetail postingDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        imgBack = findViewById(R.id.imgBack);
        imgProfile = findViewById(R.id.imgProfile);
        txtRank = findViewById(R.id.txtRank);
        txtLevel = findViewById(R.id.txtLevel);
        txtNickname = findViewById(R.id.txtNickName);
        imgPhoto = findViewById(R.id.imgPhoto);
        imgLikes = findViewById(R.id.imgLikes);
        txtLikerNickname = findViewById(R.id.txtLikerNickname);
        txtLikers = findViewById(R.id.txtLikers);
        txtLikerCnt = findViewById(R.id.txtLikerCnt);
        txtTag = findViewById(R.id.editTag);
        txtContent = findViewById(R.id.editContent);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        btnLayout = findViewById(R.id.btnLayout);
        btnChange = findViewById(R.id.btnChange);
        btnDelete = findViewById(R.id.btnDelete);
        txtLike = findViewById(R.id.txtLike);
        imgDefault = findViewById(R.id.imgDefault);
        linearLayout = findViewById(R.id.linearLayout);


        // 소셜 프레그먼트 랭커 데이터 받아오기
        ranker = (Ranker) getIntent().getSerializableExtra("ranker");
        index = getIntent().getIntExtra("index", 0);
        posting = (Posting) getIntent().getSerializableExtra("posting");


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
                likeHandler();
            }

        });

        // 포스팅 수정 버튼
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, PostEditActivity.class);
                intent.putExtra("postingDetail", postingDetail);
                intent.putExtra("tags", tags);
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

    @Override
    protected void onResume() {
        super.onResume();
        if(ranker != null) {
            postingId = ranker.postingId;
            postingUserId = ranker.userId;

            if(postingId == 0) {
                Glide.with(PostDetailActivity.this).load(ranker.profileUrl).into(imgProfile);
                txtRank.setText("" + (index+1));
                txtLevel.setText("" + ranker.level);
                txtNickname.setText("" + ranker.nickName);

                linearLayout.setVisibility(View.GONE);
                imgDefault.setVisibility(View.VISIBLE);


                return;
            }
            getTag();
            isConfirmed();

        }

        if(posting != null) {
            // 소셜 프레그먼트 포스팅 데이터 받아오기
            postingId = posting.id;
            postingUserId = posting.userId;
            getTag();
            isConfirmed();
            content = posting.content;
            createdAt = posting.createdAt;
            likeCnt = posting.likeCnt;
            isLike = posting.isLike;
        }
    }

    private void likeHandler() {
        // 1. retrofit 변수 생성
        Retrofit retrofit = NetworkClient.getRetrofitClient(PostDetailActivity.this);

        // 2. api 패키지에 있는 interface 생성
        LikeApi api = retrofit.create(LikeApi.class);

        // 유저의 토큰 값을 가져온다.
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;
        Log.i("PostDetailActivity_tag", "token : " + token);
        // 3. api 호출
        Call<Res> call = api.setLike(token, postingId);

        // 5. 서버로부터 받은 응답을 처리하는 코드 작성
        call.enqueue(new Callback<Res>() {

            // 성공했을 때
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                dismissProgress();
                // 서버에서 보낸 응답이 200 OK 일 때 처리하는 코드
                // 데이터 베이스에 카카오 로그인 정보가 없을 때
                if(response.isSuccessful()) {
                    if(isLike == 1) {
                        isLike = 0;
                        imgLikes.setImageResource(R.drawable.dis_like);
                        getTag();
                        return;
                    } else {
                        isLike = 1;
                        imgLikes.setImageResource(R.drawable.is_like);
                        getTag();
                        return;
                    }


                } else {
                    Log.i("PostDetailActivity_tag", "response.code : " +response.code());
                }
            }

            // 실패 했을 때
            @Override
            public void onFailure(Call<Res> call, Throwable t) {
                dismissProgress();
                // 유저한테 네트워크 통신 실패 했다고 알려준다.
                Toast.makeText(PostDetailActivity.this, "네트워크 파싱 오류입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isConfirmed() {
        // 1. retrofit 변수 생성
        Retrofit retrofit = NetworkClient.getRetrofitClient(PostDetailActivity.this);

        // 2. api 패키지에 있는 interface 생성
        LikeApi api = retrofit.create(LikeApi.class);

        // 유저의 토큰 값을 가져온다.
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        // 3. api 호출
        Call<LikeRes> call = api.getLike(token, postingId);

        // 5. 서버로부터 받은 응답을 처리하는 코드 작성
        call.enqueue(new Callback<LikeRes>() {

            // 성공했을 때
            @Override
            public void onResponse(Call<LikeRes> call, Response<LikeRes> response) {
                dismissProgress();
                // 서버에서 보낸 응답이 200 OK 일 때 처리하는 코드
                // 데이터 베이스에 카카오 로그인 정보가 없을 때
                if(response.isSuccessful()) {
                    LikeRes likeRes = response.body();
                    isLike = likeRes.isLike;


                    if(isLike == 1) {
                        imgLikes.setImageResource(R.drawable.is_like);
                        getTag();
                    } else {
                        imgLikes.setImageResource(R.drawable.dis_like);
                        getTag();
                    }


                } else {
                    Log.i("PostDetailActivity_tag", "response.code : " +response.code());
                }
            }

            // 실패 했을 때
            @Override
            public void onFailure(Call<LikeRes> call, Throwable t) {
                dismissProgress();
                // 유저한테 네트워크 통신 실패 했다고 알려준다.
                Toast.makeText(PostDetailActivity.this, "네트워크 파싱 오류입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRank() {
        // 1. retrofit 변수 생성
        Retrofit retrofit = NetworkClient.getRetrofitClient(PostDetailActivity.this);

        // 2. api 패키지에 있는 interface 생성
        RankerApi api = retrofit.create(RankerApi.class);

        // 유저의 토큰 값을 가져온다.
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        // 3. api 호출
        Call<Ranker> call = api.getRanking(token);

        // 5. 서버로부터 받은 응답을 처리하는 코드 작성
        call.enqueue(new Callback<Ranker>() {

            // 성공했을 때
            @Override
            public void onResponse(Call<Ranker> call, Response<Ranker> response) {
                dismissProgress();
                // 서버에서 보낸 응답이 200 OK 일 때 처리하는 코드
                // 데이터 베이스에 카카오 로그인 정보가 없을 때
                if(response.isSuccessful()) {
                    Ranker ranker = response.body();

                    userInfoResArrayList.clear();
                    userInfoResArrayList.addAll(ranker.items);
                    int myIndex = ranker.myRank -1;
                    for(int i = 0; i<userInfoResArrayList.size(); i++) {
                        if(nickName.equals(userInfoResArrayList.get(i).nickName)) {
                            index = i+1;

                            txtRank.setText("" + index);

                            if(myIndex == i) {
                                userId = userInfoResArrayList.get(i).userId;
                            }
                            Log.i("PostDetailActivity_tag", "userId : " + userId);

                            if(postingUserId != userId) {
                                btnChange.setVisibility(View.GONE);
                                btnDelete.setVisibility(View.GONE);
                            }
                            return;
                        }
                    }


                } else {
                    Log.i("PostDetailActivity_tag", "response.code : " +response.code());
                }
            }

            // 실패 했을 때
            @Override
            public void onFailure(Call<Ranker> call, Throwable t) {
                dismissProgress();
                // 유저한테 네트워크 통신 실패 했다고 알려준다.
                Toast.makeText(PostDetailActivity.this, "네트워크 파싱 오류입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTag() {
        // 0. 다이얼로그를 화면에 보여준다.
        showProgress();

        Retrofit retrofit = NetworkClient.getRetrofitClient(PostDetailActivity.this);

        PostingApi api = retrofit.create(PostingApi.class);

        // 2-1. 토큰 받아와야 할 때 토큰을 받아오고 token을 초기화 한다.
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<PostingDetail> call = api.postDetail(postingId, token);

        call.enqueue(new Callback<PostingDetail>() {

            // 성공했을 때
            @Override
            public void onResponse(Call<PostingDetail> call, Response<PostingDetail> response) {
                // 서버에서 보낸 응답이 200 OK 일 때 처리하는 코드
                if (response.isSuccessful()) {
                    postingDetail = response.body();

                    Glide.with(PostDetailActivity.this).load(postingDetail.item.get(0).profileUrl).into(imgProfile);

                    for (int i = 0; i < postingDetail.tagList.size(); i++) {
                        if (i == 0) {
                            tags = "#" + postingDetail.tagList.get(i);
                        } else {
                            tags = tags + " #" + postingDetail.tagList.get(i);
                        }
                    }
                    txtTag.setText(tags);
                    txtLevel.setText("" + postingDetail.item.get(0).level);
                    nickName = postingDetail.item.get(0).nickName;
                    txtNickname.setText(nickName);
                    txtContent.setText(postingDetail.item.get(0).content);
                    createdAt = postingDetail.item.get(0).createdAt;

                    if (createdAt.contains("T")) {
                        createdAt = createdAt.replace("T", " ");
                    }

                    txtCreatedAt.setText(createdAt);


                    Glide.with(PostDetailActivity.this).load(postingDetail.item.get(0).postingUrl).into(imgPhoto);
                    getRank();
                    if(postingDetail.likerList.size() == 0) {
                        likeCnt = 0;
                    } else {
                        likeCnt = postingDetail.likerList.size();
                    }

                    if (likeCnt == 0) {
                        txtLikerNickname.setText("");
                        txtLikers.setText("");
                        txtLikerCnt.setText("");
                        txtLike.setText("0명이 레벨업을 눌렀습니다.");
                    } else if (likeCnt == 1) {
                        txtLikerNickname.setText(postingDetail.likerList.get(0));
                        txtLikers.setText("님이");
                        txtLikerCnt.setText("");
                        txtLike.setText("레벨업을 눌렀습니다.");
                    } else {
                        txtLikerNickname.setText(postingDetail.likerList.get(0));
                        txtLikers.setText("님 외");
                        txtLikerCnt.setText("" + (likeCnt - 1));
                        txtLike.setText("명이 레벨업을 눌렀습니다.");
                    }
                } else if (response.code() == 500) {
                    return;
                }

            }

            // 실패 했을 때
            @Override
            public void onFailure(Call<PostingDetail> call, Throwable t) {
                // 유저한테 네트워크 통신 실패 했다고 알려준다.
                Toast.makeText(PostDetailActivity.this, "네트워크 파싱 오류입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        builder.setCancelable(false);
        builder.setTitle("삭제");
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
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
                            if (index >= 0 && index < postingArrayList.size()) {
                                postingArrayList.remove(index);
                            }
                                finish();
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

    Dialog dialog;
    private void showProgress(){
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissProgress(){
        dialog.dismiss();
    }
}