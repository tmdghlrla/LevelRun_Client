package com.qooke.levelrunproject.adapter;

import static android.media.CamcorderProfile.get;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.PostDetailActivity;
import com.qooke.levelrunproject.R;
import com.qooke.levelrunproject.model.Ranker;
import com.qooke.levelrunproject.model.RankerProfile;

import java.util.ArrayList;


public class RankerSocialAdapter extends RecyclerView.Adapter<RankerSocialAdapter.ViewHolder> {

    Context context;
    ArrayList<Ranker> rankerArrayList;

    public RankerSocialAdapter(Context context, ArrayList<RankerProfile> rankerProfileArrayList) {
        this.context = context;
        this.rankerArrayList = rankerArrayList;
    }

    @NonNull
    @Override
    public RankerSocialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rankersocial_row, parent, false);
        return new RankerSocialAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ranker ranker = rankerArrayList.get(position);
        Glide.with(context).load(ranker.profileUrl).into(holder.imgRanker);
    }

    @Override
    public int getItemCount() {
        return rankerArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgRanker;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgRanker = itemView.findViewById(R.id.imgRanker);
            cardView = itemView.findViewById(R.id.cardView);


            // 카드뷰 눌렀을때 실행되는 함수
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Ranker ranker = rankerArrayList.get(index);

                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("index", index);
                    intent.putExtra("ranker", ranker);
                    context.startActivity(intent);

                }
            });
        }
    }
}
