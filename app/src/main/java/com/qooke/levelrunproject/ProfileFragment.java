package com.qooke.levelrunproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView imgSetting;
    ImageView imgProfile;
    TextView txtCount;
    TextView txtNickName;
    TextView txtRank;
    TextView txtLevel;
    TextView txtExp;
    String email = null;
    String nickName = null;
    String profileUrl = null;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);
        imgSetting = rootView.findViewById(R.id.imgSetting);
        imgProfile = rootView.findViewById(R.id.imgProfile);
        txtCount = rootView.findViewById(R.id.txtCount);
        txtNickName = rootView.findViewById(R.id.editEmail);
        txtRank = rootView.findViewById(R.id.txtRank);
        txtLevel = rootView.findViewById(R.id.txtLevel);
        txtExp = rootView.findViewById(R.id.txtExp);

        // 설정 버튼 눌렀을때
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        //여기서 작업
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getUserInfo();

//        Glide.with(this).load(profileUrl).into(imgProfile);
//        txtCount.setText("보유 상자 " + count + "개");

    }

//    private void getUserInfo() {
//        // 1. retrofit 변수 생성
//        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
//
//        // 2. api 패키지에 있는 interface 생성
//        UserApi api = retrofit.create(UserApi.class);
//
//        // 유저의 토큰 값을 가져온다.
//        SharedPreferences sp = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
//        String token = sp.getString("token", "");
//
//        // 3. api 호출
//        Call<UserRes> call = api.kakaoLogin(user);
//
//        // 5. 서버로부터 받은 응답을 처리하는 코드 작성
//        call.enqueue(new Callback<UserRes>() {
//
//            // 성공했을 때
//            @Override
//            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
//                // 서버에서 보낸 응답이 200 OK 일 때 처리하는 코드
//                // 데이터 베이스에 카카오 로그인 정보가 없을 때
//                if(response.isSuccessful()) {
//                    UserRes userRes = response.body();
//                    String result = userRes.result;
//
//                    SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
//                    SharedPreferences.Editor  editor = sp.edit();
//
//                    String token = userRes.accessToken;
//
//                    editor.putString("token", token);
//                    editor.apply();
//
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("email", email);
//                    intent.putExtra("nickName", nickName);
//                    intent.putExtra("profileUrl", profileUrl);
//
//                    startActivity(intent);
//                    finish();
//
//
//                    // 중복된 아이디나 중복된 이메일이 있을 경우
//                }else if (response.code() == 400) {
//                    String result = null;
//                    try {
//                        result = "" + response.errorBody().string();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    result = StringEscapeUtils.unescapeJava(result);
//
//
//                    if(result.contains("이메일")) {
//                        Log.i("Login_tag", "여기는 이메일입니다.");
//                        Toast.makeText(LoginActivity.this, "해당 이메일 주소로 가입된 정보가 있습니다.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    else if(result.contains("닉네임")) {
//                        Log.i("Login_tag", "여기는 닉네임입니다.");
//                        Intent intent = new Intent(LoginActivity.this, NickNameActivity.class);
//                        intent.putExtra("email", email);
//                        intent.putExtra("profileUrl", profileUrl);
//                        startActivity(intent);
//                    }
//
//                } else {
//                    Log.i("Login_tag", "response.code : " +response.code());
//                }
//            }
//
//            // 실패 했을 때
//            @Override
//            public void onFailure(Call<UserRes> call, Throwable t) {
//                // 유저한테 네트워크 통신 실패 했다고 알려준다.
//                Toast.makeText(LoginActivity.this, "네트워크 파싱 오류입니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}