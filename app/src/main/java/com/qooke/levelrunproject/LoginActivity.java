package com.qooke.levelrunproject;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.UserApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.MyAppUser;
import com.qooke.levelrunproject.model.UserRes;
import java.io.IOException;
import java.util.regex.Pattern;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import org.apache.commons.lang3.StringEscapeUtils;
public class LoginActivity extends AppCompatActivity {
    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    ImageButton btnKakaoLogin;
    TextView txtRegister;
    String email = null;
    String nickName = null;
    String profileUrl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnKakaoLogin = findViewById(R.id.btnKakaoLogin);
        txtRegister = findViewById(R.id.txtRegister);
        // 카카오가 설치되어 있는지 확인 하는 메소드 또한 카카오에서 제공 콜백 객체를 이용함
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                // 이때 토큰이 전달이 되면 로그인이 성공한 것이고 토큰이 전달되지 않았다면 로그인 실패
                updateKakaoLoginUi();
                return null;
            }
        };
        // 로그인 버튼 눌렀을때
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editEmail.getText().toString().trim();
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
                Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                MyAppUser user = new MyAppUser();
                user.email = email;
                user.password = password;

                Call<UserRes> call = api.login(user);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        if (response.isSuccessful()) {
                            UserRes userRes = response.body();
                            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", userRes.accessToken);
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 400) {
                            Toast.makeText(LoginActivity.this, "회원가입이 되지 않은 이메일이거나 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (response.code() == 500) {
                            Toast.makeText(LoginActivity.this, "데이터 베이스에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(LoginActivity.this, "잠시후 다시 이용하세요.", Toast.LENGTH_SHORT).show();
                            Log.i("Login_tag", "" + response.code());
                            return;
                        }
                    }
                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();
                        Toast.makeText(LoginActivity.this, "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
        });
        // 카카오 로그인 눌렀을때(카카오 로그인 API)
        btnKakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }
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
    private void kakaoLogin() {
        showProgress();
        // 1. retrofit 변수 생성
        Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);
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
                dismissProgress();
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("nickName", nickName);
                    intent.putExtra("profileUrl", profileUrl);
                    startActivity(intent);
                    finish();
                    // 중복된 아이디나 중복된 이메일이 있을 경우
                }else if (response.code() == 400) {
                    String result = null;
                    try {
                        result = "" + response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    result = StringEscapeUtils.unescapeJava(result);
                    if(result.contains("이메일")) {
                        Log.i("Login_tag", "여기는 이메일입니다.");
                        Toast.makeText(LoginActivity.this, "해당 이메일 주소로 가입된 정보가 있습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(result.contains("닉네임")) {
                        Log.i("Login_tag", "여기는 닉네임입니다.");
                        Intent intent = new Intent(LoginActivity.this, NickNameActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("profileUrl", profileUrl);
                        startActivity(intent);
                    }
                } else {
                    Log.i("Login_tag", "response.code : " +response.code());
                }
            }
            // 실패 했을 때
            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                dismissProgress();
                // 유저한테 네트워크 통신 실패 했다고 알려준다.
                Toast.makeText(LoginActivity.this, "네트워크 파싱 오류입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateKakaoLoginUi() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                // 핸드폰에 카카오 로그인이 되었있으면
                if(user != null) {
                    // 유저 이메일
                    Log.i("Login_tag", "invoke: email = " + user.getKakaoAccount().getEmail());
                    // 유저 닉네임
                    Log.i("Login_tag", "invoke: nickName = " + user.getKakaoAccount().getProfile().getNickname());
                    // 유저 프로필
                    Log.i("Login_tag", "invoke: url = " + user.getKakaoAccount().getProfile().getThumbnailImageUrl());
                    email = user.getKakaoAccount().getEmail().toString();
                    nickName = user.getKakaoAccount().getProfile().getNickname().toString();
                    profileUrl = user.getKakaoAccount().getProfile().getThumbnailImageUrl().toString();
                    kakaoLogin();
                } else {
                    // 핸드폰에 로그인이 안되어 있을 때
                    Log.i("Login_tag", "로그인이 되어 있지 않음");
                }
                return null;
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