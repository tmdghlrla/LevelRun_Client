package com.qooke.levelrunproject;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.adapter.CharacterAdapter;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.UserApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.CharacterUrl;
import com.qooke.levelrunproject.model.UserInfoRes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RankingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RankingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RankingFragment newInstance(String param1, String param2) {
        RankingFragment fragment = new RankingFragment();
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
    TextView txtRank, txtNickName, txtLevel, txtExp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_ranking, container, false);

        txtRank = rootView.findViewById(R.id.txtRank);
        txtNickName = rootView.findViewById(R.id.txtNickName);
        txtLevel = rootView.findViewById(R.id.txtLevel);
        txtExp = rootView.findViewById(R.id.txtExp);


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
                    String nickName = userInfoRes.nickName;
                    int level = userInfoRes.level;
                    int exp = userInfoRes.exp;

                    txtNickName.setText(nickName);
                    txtRank.setText(""+ rank);
                    txtLevel.setText("" + level);
                    txtExp.setText("" + exp);

//                    adapter = new CharacterAdapter(getActivity(), characterArrayList, urlList);
//                    recyclerView.setAdapter(adapter);


                } else {
                    Log.i("RankingFragment_tag", "response.code : " +response.code());
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