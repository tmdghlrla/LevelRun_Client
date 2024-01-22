package com.qooke.levelrunproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    ImageView imgKakaoLogin;
    TextView txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        imgKakaoLogin = findViewById(R.id.imgKakaoLogin);
        txtRegister = findViewById(R.id.txtRegister);


        // 로그인 버튼 눌렀을때
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();


                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "필수 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (pattern.matcher(email).matches() == false) {
                    Toast.makeText(LoginActivity.this, "이메일을 정확히 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 4 || password.length() > 12) {
                    Toast.makeText(LoginActivity.this, "비밀번호 길이를 확인하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgress();

                // 로그인 api

            }
        });


        // 카카오 로그인 눌렀을때(카카오 로그인 API)
        imgKakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // 회원가입 텍스트뷰 눌렀을때
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    // 다이얼로그
    Dialog dialog;
    private void showProgress() {
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissProgress() {
        dialog.dismiss();
    }

}
