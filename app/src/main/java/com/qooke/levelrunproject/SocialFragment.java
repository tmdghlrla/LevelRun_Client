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
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PostingApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Posting;
import com.qooke.levelrunproject.model.PostingList;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_social, container, false);

        fbtnPostingAdd = rootView.findViewById(R.id.fbtnPostingAdd);
        imgBack = rootView.findViewById(R.id.imgBack);
        switchFilter = rootView.findViewById(R.id.switchFilter);

        recyclerviewRanker = rootView.findViewById(R.id.recyclerviewRanker);
        recyclerviewRanker.setHasFixedSize(true);
        recyclerviewRanker.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 랭커 리사이클러뷰 페이징 처리하는 함수
        recyclerviewRanker.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount) {
                    // 네트워크 통해서 데이터를 더 불러온다.
                    if (limit == count) {
                        // DB에 데이터가 더 존재할 수 있으니까, 데이터를 불러온다.(네트워크 낭비 제한)
                        // 네트워크 통하는 함수 만들기(페이징 처리)
                        addNetworkData();
                    }
                }
            }
        });

        recyclerviewPosting = rootView.findViewById(R.id.recyclerviewPosting);
        recyclerviewPosting.setHasFixedSize(true);
        recyclerviewPosting.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 포스팅 리사이클러뷰 페이징 처리하는 함수
        recyclerviewPosting.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount) {
                    // 네트워크 통해서 데이터를 더 불러온다.
                    if (limit == count) {
                        // DB에 데이터가 더 존재할 수 있으니까, 데이터를 불러온다.(네트워크 낭비 제한)
                        // 네트워크 통하는 함수 만들기(페이징 처리)
                        addNetworkData();
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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getNetworkData();
    }

    // 포스팅 작성 후 새로 불러오는 함수
    private void getNetworkData() {

        // 변수 초기화
        offset = 0;
        count = 0;

        // 네트워크 API 호출한다.
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<PostingList> call = api.getMyPosting(token, offset, limit);
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


    // 데이터 페이징 함수
    private void addNetworkData() {

        progressBar.setVisibility(View.VISIBLE);

        // 네트워크로 API 호출한다.
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        // 오프셋을 count만큼 증가시킬 수 있도록 셋팅
        offset = offset + count;

        Call<PostingList> call = api.getMyPosting(token, offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()) {

                    PostingList postingList = response.body();
                    postingArrayList.addAll(postingList.items);
                    count = postingList.count;

                    adapter.notifyDataSetChanged();

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