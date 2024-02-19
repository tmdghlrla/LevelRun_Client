// MissionActivity.java
package com.qooke.levelrunproject;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qooke.levelrunproject.api.ExerciseApi;
import com.qooke.levelrunproject.api.MissionApi;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PostingApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Exercise;
import com.qooke.levelrunproject.model.ExerciseRes;
import com.qooke.levelrunproject.model.PostingDetail;
import com.qooke.levelrunproject.model.RankerRes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MissionActivity extends AppCompatActivity {

    private ImageButton btnBack, btnClear, btnClear2, btnClear3, btnClear4, btnClear5;
    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5, progressBar6;
    private ProgressBar progressBarExp;
    TextView txtLevel, txtExp;
    Gson gson;
    int steps = 0;
    int mission = 0;
    int isClear1 = 0;
    int isClear2 = 0;
    int isClear3 = 0;
    int isClear4 = 0;
    int isClear5 = 0;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        txtLevel = findViewById(R.id.txtLevel);
        progressBarExp = findViewById(R.id.progressBarExp);
        txtExp = findViewById(R.id.txtExp);

        // UI 요소 초기화
        btnBack = findViewById(R.id.btnBack);
        btnClear = findViewById(R.id.btnClear);
        btnClear2 = findViewById(R.id.btnClear2);
        btnClear3 = findViewById(R.id.btnClear3);
        btnClear4 = findViewById(R.id.btnClear4);
        btnClear5 = findViewById(R.id.btnClear5);

        // 임무 프로그레스바
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);

        // 뒤로가기 버튼
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(steps < 1000) {
                    return;
                }

                // todo: db에 저장하는 코드
                mission = 1;
                setExp();
            }
        });
    }

    private void setExp() {
        // 0. 다이얼로그를 화면에 보여준다.
        showProgress();

        Retrofit retrofit = NetworkClient.getRetrofitClient(MissionActivity.this);

        MissionApi api = retrofit.create(MissionApi.class);

        // 2-1. 토큰 받아와야 할 때 토큰을 받아오고 token을 초기화 한다.
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<RankerRes> call = api.setExp(token, mission);

        call.enqueue(new Callback<RankerRes>() {

            // 성공했을 때
            @Override
            public void onResponse(Call<RankerRes> call, Response<RankerRes> response) {
                // 서버에서 보낸 응답이 200 OK 일 때 처리하는 코드
                if (response.isSuccessful()) {
                    RankerRes RankerRes = response.body();
//                    txtLevel.setText("Lv." + RankerRes.);
                } else if (response.code() == 500) {
                    return;
                }

            }

            // 실패 했을 때
            @Override
            public void onFailure(Call<RankerRes> call, Throwable t) {
                // 유저한테 네트워크 통신 실패 했다고 알려준다.
                Toast.makeText(MissionActivity.this, "네트워크 파싱 오류입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        gson = new GsonBuilder().create();
        String values = sp.getString("exercise", "");

        // 앱내 저장소에 값이 저장되어 있는 경우
        // 보상을 받았는지 확인한다.
        if (!values.equals("")) {
            Exercise exercise = gson.fromJson(values, Exercise.class);
            steps = exercise.steps;
            progressBar1.setProgress(steps);
            progressBar2.setProgress(steps);
            progressBar3.setProgress(steps);
            progressBar4.setProgress(steps);
        }
        // 걸음수 호출
        else {
            getRecord();
        }
    }

    private void getRecord() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(MissionActivity.this);

        ExerciseApi api = retrofit.create(ExerciseApi.class);

        sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<ExerciseRes> call = api.getRecord(token);
        call.enqueue(new Callback<ExerciseRes>() {
            @Override
            public void onResponse(Call<ExerciseRes> call, Response<ExerciseRes> response) {
                if(response.isSuccessful()){
                    ExerciseRes exerciseRes = response.body();
                    steps = exerciseRes.items.get(0).steps;
                    progressBar1.setProgress(steps);
                    progressBar2.setProgress(steps);
                    progressBar3.setProgress(steps);
                    progressBar4.setProgress(steps);
                }

                else{

                }
            }

            @Override
            public void onFailure(Call<ExerciseRes> call, Throwable t) {

            }
        });
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