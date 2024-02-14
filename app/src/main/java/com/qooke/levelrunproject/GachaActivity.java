package com.qooke.levelrunproject;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.api.GachaApi;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.GachaData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GachaActivity extends AppCompatActivity {

    private ImageView box, gachaBack;
    private ImageView mon;
    private Animation shakeAnimation;
    private MediaPlayer mediaPlayer;
    private TextView boxCnt;

    private boolean isShowingMonster = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gacha);

        // XML에서 정의한 뷰들을 찾아서 초기화
        box = findViewById(R.id.box);
        mon = findViewById(R.id.mon);
        boxCnt = findViewById(R.id.boxCnt);
        gachaBack = findViewById(R.id.gachaBack);

        int boxCount = getIntent().getIntExtra("boxCount", 0);
        boxCnt.setText("" + boxCount);

        // 흔들기 애니메이션 및 효과음을 초기화
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        // box에 클릭 이벤트 리스너 추가
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(boxCnt.getText().toString()) > 0) {
                    v.startAnimation(shakeAnimation);
                    playSound();
                    // 1초 후에 API 호출
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getNetWorkData();
                        }
                    }, 1000);
                    // box를 비활성화하여 여러 번 눌리지 않도록 함
                    box.setEnabled(false);
                }
            }
        });

        // gachaBack에 클릭 이벤트 리스너 추가
        gachaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 액티비티 종료
                finish();
            }
        });

        // 몬스터 이미지뷰에 클릭 이벤트 리스너 추가
        mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 몬스터 이미지를 눌러도 아무 동작하지 않도록 처리
            }
        });
    }

    public void getNetWorkData() {
        // API 호출
        Retrofit retrofit = NetworkClient.getRetrofitClient(GachaActivity.this);
        GachaApi api = retrofit.create(GachaApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        // 서버로 요청할 데이터 설정
        Call<GachaData> call = api.getGacha(token);

        call.enqueue(new Callback<GachaData>() {
            @Override
            public void onResponse(Call<GachaData> call, Response<GachaData> response) {
                if (response.isSuccessful()) {
                    // 서버 응답에 따른 동작 수행
                    GachaData gachaData = response.body();
                    // 응답에서 받은 데이터를 화면에 표시
                    displayGachaData(gachaData);
                } else {
                    // 서버 응답이 실패한 경우의 처리
                }
            }

            @Override
            public void onFailure(Call<GachaData> call, Throwable t) {
                // 서버 통신 실패 시의 처리
            }
        });
    }

    private void displayGachaData(GachaData gachaData) {
        // 획득한 몬스터 이미지를 화면에 표시
        String imgUrl = gachaData.items.get(0).imgUrl;

        box.setVisibility(View.GONE);


        Glide.with(GachaActivity.this).load(imgUrl).into(mon);
        // 남은 상자 갯수를 업데이트
        boxCnt.setText(String.valueOf(gachaData.boxCount));

        // 몬스터 이미지뷰를 보여주고 1.5초 뒤에 다시 상자를 보여줌
        mon.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resetBox();
            }
        }, 1500);
    }

    // 효과음을 재생하는 메서드
    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    // 상자를 초기 상태로 리셋하는 메서드
    private void resetBox() {
        mon.setVisibility(View.GONE);
        box.setVisibility(View.VISIBLE);
        // box를 다시 활성화하여 클릭할 수 있도록 함
        box.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MediaPlayer 리소스 해제
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}