package com.qooke.levelrunproject;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qooke.levelrunproject.adapter.PostingAdapter;
import com.qooke.levelrunproject.adapter.RankerSocialAdapter;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PostingApi;
import com.qooke.levelrunproject.api.RankerApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Posting;
import com.qooke.levelrunproject.model.PostingList;
import com.qooke.levelrunproject.model.Ranker;
import com.qooke.levelrunproject.model.RankerRes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SocialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialFragment newInstance(String param1, String param2) {
        SocialFragment fragment = new SocialFragment();
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


    FloatingActionButton fbtnPostingAdd;
    Switch switchFilter;
    ProgressBar progressBar;


    // 페이징 관련 처리 함수
    int offset;
    int limit = 15;
    int count;
    Boolean isSwitchOn = false;


    // 리사이클러뷰 관련 멤버변수
    RecyclerView recyclerviewRanker;
    RankerSocialAdapter rankerSocialAdapter;
    ArrayList<Ranker> rankerArrayList = new ArrayList<>();

    RecyclerView recyclerviewPosting;
    PostingAdapter postingAdapter;
    ArrayList<Posting> postingArrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_social, container, false);

        fbtnPostingAdd = rootView.findViewById(R.id.fbtnPostingAdd);
        switchFilter = rootView.findViewById(R.id.switchFilter);
        progressBar = rootView.findViewById(R.id.progressBar);


        // 랭커 리사이클러뷰 처리하는 함수
        recyclerviewRanker = rootView.findViewById(R.id.recyclerviewRanker);
        recyclerviewRanker.setHasFixedSize(true);
        recyclerviewRanker.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recyclerviewRanker.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount) {
                    if (limit == count) {
                        RankerNetworkData();
                    }
                }

            }
        });

        getNetworkData();
        // 포스팅 리사이클러뷰 페이징 처리하는 함수
        recyclerviewPosting = rootView.findViewById(R.id.recyclerviewPosting);
        recyclerviewPosting.setHasFixedSize(true);
        recyclerviewPosting.setLayoutManager(new GridLayoutManager(getActivity(),3));

        recyclerviewPosting.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount) {
                    if (limit == count) {
                        postNetworkData();
                    }
                }
            }
        });

        // 포스팅 생성 버튼 눌렀을때
        fbtnPostingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostingAddActivity.class);
                startActivity(intent);
            }
        });



        switchFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // 스위치가 On인 경우 인기순
                    isSwitchOn = true;
                    getPopular();
                } else {
                    // 스위치가 Off인 경우 최신순
                    isSwitchOn = false;
                    getRecent();

                }
            }
        });
        return rootView;
    }

    private void getRecent() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi postingApi = retrofit.create(PostingApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<PostingList> api = postingApi.getAllPosting(token, offset, limit);
        api.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {

                    offset = 0;
                    count = 0;

                    PostingList postingList = response.body();
                    postingArrayList.clear();
                    postingArrayList.addAll(postingList.items);
                    count = postingList.count;

                    postingAdapter = new PostingAdapter(getActivity(), postingArrayList);
                    recyclerviewPosting.setAdapter(postingAdapter);
                }
            }
            @Override
            public void onFailure (Call < PostingList > call, Throwable t){

            }
        });
    }

    // 인기순
    private void getPopular() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi postingApi = retrofit.create(PostingApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<PostingList> api = postingApi.popularityPosting(token, offset, limit);
        api.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {

                    offset = 0;
                    count = 0;

                    PostingList postingList = response.body();
                    postingArrayList.clear();
                    postingArrayList.addAll(postingList.items);
                    count = postingList.count;

                    postingAdapter = new PostingAdapter(getActivity(), postingArrayList);
                    recyclerviewPosting.setAdapter(postingAdapter);
                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if(isSwitchOn) {
            // 스위치가 On인 경우 인기순
            isSwitchOn = true;
            getPopular();
        } else {
            // 스위치가 Off인 경우 최신순
            isSwitchOn = false;
            getRecent();

        }
        switchFilter.setChecked(isSwitchOn);
    }

    // 포스팅 작성 후 새로고침
    private void getNetworkData() {

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi postingApi = retrofit.create(PostingApi.class);
        RankerApi rankerApi = retrofit.create(RankerApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        // 포스팅 이미지
        Call<PostingList> postCall = postingApi.getAllPosting(token, offset, limit);
        postCall.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {

                    offset = 0;
                    count = 0;

                    PostingList postingList = response.body();
                    postingArrayList.clear();
                    postingArrayList.addAll(postingList.items);
                    count = postingList.count;

                    postingAdapter = new PostingAdapter(getActivity(), postingArrayList);
                    recyclerviewPosting.setAdapter(postingAdapter);
                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

        // 랭커 프로필
        Call<RankerRes> rankerCall = rankerApi.rankerimg(token);
        rankerCall.enqueue(new Callback<RankerRes>() {
            @Override
            public void onResponse(Call<RankerRes> rankerCall, Response<RankerRes> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    RankerRes rankerRes = response.body();
                    rankerArrayList.clear();
                    rankerArrayList.addAll(rankerRes.items);
                    count = rankerRes.count;

                    Log.i("AAA", "어레이 리스트 : "+postingArrayList);

                    rankerSocialAdapter = new RankerSocialAdapter(getActivity(), rankerArrayList);
                    recyclerviewRanker.setAdapter(rankerSocialAdapter);
                }
            }

            @Override
            public void onFailure(Call<RankerRes> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }



    // 포스팅 데이터 페이징 함수
    private void postNetworkData() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        offset = offset + count;

        Call<PostingList> call = api.getAllPosting(token, offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()) {

                    PostingList postingList = response.body();
                    postingArrayList.addAll(postingList.items);
                    count = postingList.count;

                    postingAdapter.notifyDataSetChanged();

                } else {

                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    // 랭커 데이터 함수
    public void RankerNetworkData() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        RankerApi api = retrofit.create(RankerApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        offset = offset + count;

        Call<RankerRes> call = api.rankerimg(token);
        call.enqueue(new Callback<RankerRes>() {
            @Override
            public void onResponse(Call<RankerRes> call, Response<RankerRes> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()) {

                    RankerRes rankerRes = response.body();
                    rankerArrayList.addAll(rankerRes.items);
                    count = rankerRes.count;
                    rankerSocialAdapter.notifyDataSetChanged();

                } else {

                }
            }

            @Override
            public void onFailure(Call<RankerRes> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
