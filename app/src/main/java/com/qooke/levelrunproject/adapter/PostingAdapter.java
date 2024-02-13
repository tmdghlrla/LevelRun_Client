package com.qooke.levelrunproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.PostDetailActivity;
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
        View view = LayoutInflater.from(context).inflate(R.layout.social_row, parent,false);
        return new PostingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posting posting = postingArrayList.get(position);
        Glide.with(context).load(posting.imgURL).into(holder.imgPosting);
    }

    @Override
    public int getItemCount() {
        return postingArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPosting;
        CardView cardViewPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPosting = itemView.findViewById(R.id.imgPosting);
            cardViewPost = itemView.findViewById(R.id.cardViewPost);

            // 포스팅 이미지 눌렀을때 함수
            cardViewPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Posting posting = postingArrayList.get(index);

                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("index", index);
                    intent.putExtra("posting", posting);
                    context.startActivity(intent);

                }
            });


        }
    }
}
