package com.qooke.levelrunproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qooke.levelrunproject.adapter.PostingAdapter;
import com.qooke.levelrunproject.adapter.RankerSocialAdapter;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PostingApi;
import com.qooke.levelrunproject.api.RankerApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Posting;
import com.qooke.levelrunproject.model.PostingList;
import com.qooke.levelrunproject.model.RankerProfile;
import com.qooke.levelrunproject.model.RankerList;

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
    ImageView imgBack;
    Switch switchFilter;
    ProgressBar progressBar;

    RecyclerView recyclerviewRanker;
    PostingAdapter adapter;
    ArrayList<Posting> postingArrayList = new ArrayList<>();

    RecyclerView recyclerviewPosting;


    // 페이징 관련 처리 함수
    int offset;
    int limit = 5;
    int count;

    // 멤버변수화
    String token;

    // 리사이클러뷰 관련 멤버변수
    RecyclerView recyclerView;
    RankerSocialAdapter rankerSocialAdapter;
    PostingAdapter postingAdapter;
    ArrayList<RankerProfile> rankerProfileListArrayList = new ArrayList<>();
    ArrayList<Posting> postingListArrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_social, container, false);

        fbtnPostingAdd = rootView.findViewById(R.id.fbtnPostingAdd);
        imgBack = rootView.findViewById(R.id.imgBack);
        switchFilter = rootView.findViewById(R.id.switchFilter);
        progressBar = rootView.findViewById(R.id.progressBar);

        // 랭커 리사이클러뷰 처리하는 함수
        recyclerviewRanker = rootView.findViewById(R.id.recyclerviewRanker);
        recyclerviewRanker.setHasFixedSize(true);
        recyclerviewRanker.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerviewRanker.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount) {
                    if (limit == count) {
                        rankerNetworkData();
                    }
                }
            }
        });


        // 포스팅 리사이클러뷰 페이징 처리하는 함수
        recyclerviewPosting = rootView.findViewById(R.id.recyclerviewPosting);
        recyclerviewPosting.setHasFixedSize(true);
        recyclerviewPosting.setLayoutManager(new LinearLayoutManager(getActivity()));

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

        // 최신순, 인기순 버튼 선택시
        switchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchFilter.isChecked()) {

                } else {

                }
            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        getNetworkData();
    }

    // 포스팅 작성 후 새로고침
    private void getNetworkData() {

        offset = 0;
        count = 0;

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<PostingList> call = api.getAllPosting(token, offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    PostingList postingList = response.body();

                    count = postingList.count;

                    postingArrayList.clear();

                    postingArrayList.addAll(postingList.items);

                    adapter = new PostingAdapter(getActivity(), postingArrayList);
                    recyclerviewPosting.setAdapter(adapter);

                } else {

                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

            }
        });

    }


    // 랭커 데이터 페이징 함수
    private void rankerNetworkData() {

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        RankerApi api = retrofit.create(RankerApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        offset = offset + count;

        Call<RankerList> call = api.rankerimg(token);
        call.enqueue(new Callback<RankerList>() {
            @Override
            public void onResponse(Call<RankerList> call, Response<RankerList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()) {

                    RankerList rankerList = response.body();
                    rankerProfileListArrayList.addAll(rankerList.items);
                    count = rankerList.count;

                    rankerSocialAdapter.notifyDataSetChanged();

                } else {

                }
            }

            @Override
            public void onFailure(Call<RankerList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    // 포스팅 데이터 페이징 함수
    private void postNetworkData() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        offset = offset + count;

        Call<PostingList> call = api.getAllPosting(token, offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()) {

                    PostingList postingList = response.body();
                    postingListArrayList.addAll(postingList.items);
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

}