package com.qooke.levelrunproject;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.qooke.levelrunproject.adapter.ExerciseAdapter;
import com.qooke.levelrunproject.api.ExerciseApi;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.ExerciseRes;
import com.qooke.levelrunproject.model.Exercise;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
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
    RecyclerView recyclerView;
    ExerciseAdapter adapter;
    ArrayList<Exercise> exerciseArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_record, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getNetworkData();
    }

    private void getNetworkData() {
        showProgress();
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        ExerciseApi api = retrofit.create(ExerciseApi.class);

        SharedPreferences sp = getContext().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<ExerciseRes> call = api.getRecordList(token);
        call.enqueue(new Callback<ExerciseRes>() {
            @Override
            public void onResponse(Call<ExerciseRes> call, Response<ExerciseRes> response) {
                dismissProgress();
                if(response.isSuccessful()){
                    ExerciseRes exerciseRes = response.body();
                    exerciseArrayList.clear();
                    exerciseArrayList.addAll(exerciseRes.items);

                    adapter = new ExerciseAdapter(getActivity(), exerciseArrayList);
                    recyclerView.setAdapter(adapter);


                }else{

                }
            }

            @Override
            public void onFailure(Call<ExerciseRes> call, Throwable t) {
                dismissProgress();
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