package com.qooke.levelrunproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qooke.levelrunproject.R;
import com.qooke.levelrunproject.model.UserInfoRes;


import java.util.ArrayList;

// 2. RecyclerView.Adapter를 상속한다.
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {
    // 3. 멤버변수와 생성자를 만든다.
    Context context; // 엑티비티 정보
    ArrayList<UserInfoRes> userInfoResArrayList;

    public RankAdapter(Context context, ArrayList<UserInfoRes> userInfoResArrayList) {
        this.context = context;
        this.userInfoResArrayList = userInfoResArrayList;
    }

    // 4. 오버라이딩 함수를 구현한다.
    // onCreateViewHolder: 뷰 홀더를 생성하고 레이아웃을 연결하는 부분
    @NonNull
    @Override
    public RankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ranking_row, parent, false);
        return new RankAdapter.ViewHolder(view);
    }

    // 화면 담당
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserInfoRes userInfoRes = userInfoResArrayList.get(position);
        int rank = position+1;
        holder.txtRank.setText("" + rank);
        holder.txtNickName.setText(userInfoRes.nickName);
        holder.txtLevel.setText("" + userInfoRes.level);
        holder.txtExp.setText("" + userInfoRes.exp);
    }

    @Override
    public int getItemCount() {
        return userInfoResArrayList.size(); // 여기에 적힌 숫자만큼 recyclerView를 가져옴
    }

    // 1. 메모리에 있는 데이터를 연결할 ViewHolder를 만든다.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRank;
        TextView txtNickName;
        TextView txtLevel;
        TextView txtExp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRank = itemView.findViewById(R.id.txtRank);
            txtNickName = itemView.findViewById(R.id.txtNickName);
            txtLevel = itemView.findViewById(R.id.txtAirLevel);
            txtExp = itemView.findViewById(R.id.txtExp);
        }
    }
}