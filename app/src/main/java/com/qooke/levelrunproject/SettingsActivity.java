package com.qooke.levelrunproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.UserApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Res;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingsActivity extends AppCompatActivity {

    ImageView imgBack;
    Button btnSave;
    ImageView imgChange;
    EditText editNickname;
    Button btnLogout;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imgBack = findViewById(R.id.imgBack);
        btnSave = findViewById(R.id.btnSave);
        imgChange = findViewById(R.id.imgChange);
        editNickname = findViewById(R.id.editNickname);
        btnLogout = findViewById(R.id.btnLogout);

        // 뒤로가기 버튼
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 저장 버튼
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editNickname.getText().toString().trim();

            }
        });

        // 로그아웃 버튼
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("로그아웃");
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setPositiveButton("확인", (dialog, which) -> {
            logoutUser();
        });
        builder.setNegativeButton("취소", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private void logoutUser() {
        // 저장된 토큰을 삭제하고 로그아웃처리(로그인 액티비티로 이동)
        Retrofit retrofit = NetworkClient.getRetrofitClient(SettingsActivity.this);
        UserApi api = retrofit.create(UserApi.class);

        Call<Res> call = api.logout(token);
        call.enqueue(new Callback<Res>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                if (response.isSuccessful()) {
                    SharedPreferences sp = SettingsActivity.this.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("token", "");
                    editor.apply();

                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);

                    SettingsActivity.this.finish();
                } else {
                    Toast.makeText(SettingsActivity.this, "로그아웃 실패", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<Res> call, Throwable t) {

            }


        });
    }
}