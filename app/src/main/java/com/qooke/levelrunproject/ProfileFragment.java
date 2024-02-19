package com.qooke.levelrunproject;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.adapter.CharacterAdapter;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.UserApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Character;
import com.qooke.levelrunproject.model.CharacterUrl;
import com.qooke.levelrunproject.model.MyAppUser;
import com.qooke.levelrunproject.model.UserInfoRes;

import java.util.ArrayList;

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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
    boolean isComplete = false;
    int count = 0;
    int boxCount;
    ArrayList<Character> items = new ArrayList<>();
    ArrayList<CharacterUrl> urlList = new ArrayList<>();


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
    CharacterAdapter adapter;
    ArrayList<Character> characterArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    FrameLayout btnCollection;
    FrameLayout imgBox;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);
        imgSetting = rootView.findViewById(R.id.imgSetting);
        imgProfile = rootView.findViewById(R.id.imgProfile);
        txtCount = rootView.findViewById(R.id.txtCount);
        txtNickName = rootView.findViewById(R.id.txtNickName);
        txtRank = rootView.findViewById(R.id.txtRank);
        txtLevel = rootView.findViewById(R.id.txtAirLevel);
        txtExp = rootView.findViewById(R.id.txtExp);
        btnCollection = rootView.findViewById(R.id.btnCollection);
        imgBox = rootView.findViewById(R.id.imgBox);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비동기 처리 데이터베이스에서 이미지 url를 불러오는 작업이 아직 안 끝났을 때
                if(!isComplete) {
                    Toast.makeText(getActivity(), "데이터를 불러오는 중입니다. 잠시후 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("ProfileFrgment_tag", profileUrl);
                MyAppUser myAppUser = new MyAppUser(nickName, email, "", profileUrl);
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra("myAppUser", myAppUser);
                getActivity().startActivity(intent);
            }
        });

        btnCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CollectionActivity.class);
                intent.putExtra("charUrl", characterArrayList);
                startActivity(intent);
            }
        });

        imgBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GachaActivity.class);
                intent.putExtra("boxCount", boxCount);
                Log.i("ProfileFragment_tag", "boxCount : " + boxCount);
                startActivity(intent);
            }
        });
        //여기서 작업
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();

    }

    private void getUserInfo() {
        showProgress();
        // 1. retrofit 변수 생성
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        // 2. api 패키지에 있는 interface 생성
        UserApi api = retrofit.create(UserApi.class);

        // 유저의 토큰 값을 가져온다.
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;
        Log.i("ProfileFragment_tag", "token : " + token);

        // 3. api 호출
        Call<UserInfoRes> call = api.getUserInfo(token);

        // 5. 서버로부터 받은 응답을 처리하는 코드 작성
        call.enqueue(new Callback<UserInfoRes>() {

            // 성공했을 때
            @Override
            public void onResponse(Call<UserInfoRes> call, Response<UserInfoRes> response) {
                dismissProgress();
                // 서버에서 보낸 응답이 200 OK 일 때 처리하는 코드
                // 데이터 베이스에 카카오 로그인 정보가 없을 때
                if(response.isSuccessful()) {
                    UserInfoRes userInfoRes = response.body();
                    int rank = userInfoRes.rank;
                    nickName = userInfoRes.nickName;
                    email = userInfoRes.email;
                    profileUrl = userInfoRes.profileUrl;
                    int level = userInfoRes.level;
                    int exp = userInfoRes.exp;
                    boxCount = userInfoRes.boxCount;
                    items.addAll(userInfoRes.items);

                    // 정보 세팅하기
                    Glide.with(getActivity()).load(profileUrl).into(imgProfile);

                    txtCount.setText("보유 상자 " + boxCount + " 개");

                    txtNickName.setText(nickName);
                    txtRank.setText(""+ rank);
                    txtLevel.setText("" + level);
                    txtExp.setText("" + exp);

                    characterArrayList.clear();
                    characterArrayList.addAll(userInfoRes.items);
                    urlList.clear();
                    for (int i = 0; i < characterArrayList.size();) {
                        CharacterUrl url = new CharacterUrl(
                                characterArrayList.get(i).imgUrl,
                                (i+1 < characterArrayList.size()) ? characterArrayList.get(i+1).imgUrl : "",
                                (i+2 < characterArrayList.size()) ? characterArrayList.get(i+2).imgUrl : "",
                                (i+3 < characterArrayList.size()) ? characterArrayList.get(i+3).imgUrl : "",
                                (i+4 < characterArrayList.size()) ? characterArrayList.get(i+4).imgUrl : ""
                        );
                        Log.i("ProfileFragment_tag", "url1 : " + url.url1);
                        Log.i("ProfileFragment_tag", "url2 : " + url.url2);
                        Log.i("ProfileFragment_tag", "url3 : " + url.url3);
                        Log.i("ProfileFragment_tag", "url4 : " + url.url4);
                        Log.i("ProfileFragment_tag", "url5 : " + url.url5);
                        urlList.add(url);
                        i = i+5;
                    }

                    isComplete = true;
                    adapter = new CharacterAdapter(getActivity(), characterArrayList, urlList);
                    recyclerView.setAdapter(adapter);


                } else {
                    Log.i("Profile_tag", "response.code : " +response.code());
                }
            }

            // 실패 했을 때
            @Override
            public void onFailure(Call<UserInfoRes> call, Throwable t) {
                dismissProgress();
                // 유저한테 네트워크 통신 실패 했다고 알려준다.
                Toast.makeText(getActivity(), "네트워크 파싱 오류입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    Dialog dialog;
    private void showProgress(){
        dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(getActivity()));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissProgress(){
        dialog.dismiss();
    }
}