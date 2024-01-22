package com.qooke.levelrunproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.R;
import com.qooke.levelrunproject.model.Posting;

import java.util.ArrayList;

public class PostingAdapter extends RecyclerView.Adapter<PostingAdapter.ViewHolder>{

    Context context;
    ArrayList<Posting> postingArrayList;

    public PostingAdapter(Context context, ArrayList<Posting> postingArrayList) {
        this.context = context;
        this.postingArrayList = postingArrayList;
    }

    @NonNull
    @Override
    public PostingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_row, parent,false);
        return new PostingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posting posting = postingArrayList.get(position);
        holder.txtDescription.setText(posting.description);
        Glide.with(context).load(posting.mediumUrl).into(holder.imgPosting);
    }

    @Override
    public int getItemCount() {
        return postingArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPosting;
        Posting posting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPosting = itemView.findViewById(R.id.imgPosting);

            // 포스팅 이미지 눌렀을때 게시물 세부 정보로 이동
            imgPosting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    posting = postingArrayList.get(index);

                    Intent intent = new Intent(context, PostingDetailActivity.class);
                    context.startActivity(intent);

                }
            });


        }
    }
}
