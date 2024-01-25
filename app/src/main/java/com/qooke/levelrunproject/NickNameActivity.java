package com.qooke.levelrunproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.UserApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.MyAppUser;
import com.qooke.levelrunproject.model.UserRes;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NickNameActivity extends AppCompatActivity {
    TextView txtNickName;
    Button btnStart;
    String nickName = null;
    String email = null;
    String profileUrl = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name);

        txtNickName = findViewById(R.id.txtNickName);
        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickName = txtNickName.getText().toString().trim();

                if(nickName.isEmpty()) {
                    Toast.makeText(NickNameActivity.this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();
                email = getIntent().getStringExtra("email");
                profileUrl = getIntent().getStringExtra("profileUrl");
                kakaoLogin();
            }
        });


    }

    private void kakaoLogin() {
        // 1. retrofit 변수 생성
        Retrofit retrofit = NetworkClient.getRetrofitClient(NickNameActivity.this);

        // 2. api 패키지에 있는 interface 생성
        UserApi api = retrofit.create(UserApi.class);

        MyAppUser user = new MyAppUser(nickName, email);
        user.profileUrl = profileUrl;

        // 3. api 호출
        Call<UserRes> call = api.kakaoLogin(user);

        // 5. 서버로부터 받은 응답을 처리하는 코드 작성
        call.enqueue(new Callback<UserRes>() {

            // 성공했을 때
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                // 서버에서 보낸 응답이 200 OK 일 때 처리하는 코드
                // 데이터 베이스에 카카오 로그인 정보가 없을 때
                if(response.isSuccessful()) {
                    UserRes userRes = response.body();
                    String result = userRes.result;

                    SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor  editor = sp.edit();

                    String token = userRes.accessToken;

                    editor.putString("token", token);
                    editor.apply();

                    Intent intent = new Intent(NickNameActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    // 중복된 이메일이 있을 경우
                }else if (response.code() == 400) {
                    String result = null;
                    try {
                        result = "" + response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    result = StringEscapeUtils.unescapeJava(result);

                    if(result.contains("닉네임")) {
                        Toast.makeText(NickNameActivity.this, "중복된 닉네임이 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // 실패 했을 때
            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                // 유저한테 네트워크 통신 실패 했다고 알려준다.
                Toast.makeText(NickNameActivity.this, "네트워크 파싱 오류입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
