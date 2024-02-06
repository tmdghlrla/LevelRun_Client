package com.qooke.levelrunproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qooke.levelrunproject.model.Character;
import com.qooke.levelrunproject.model.CharacterUrl;

import java.util.ArrayList;

// 2. RecyclerView.Adapter를 상속한다.
public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {
    // 3. 멤버변수와 생성자를 만든다.
    Context context; // 엑티비티 정보
    ArrayList<Character> characterArrayList;
    ArrayList<CharacterUrl> urlList;

    public CharacterAdapter(Context context, ArrayList<Character> characterArrayList, ArrayList<CharacterUrl> urlList) {
        this.context = context;
        this.characterArrayList = characterArrayList;
        this.urlList = urlList;
    }

    // 4. 오버라이딩 함수를 구현한다.
    // onCreateViewHolder: 뷰 홀더를 생성하고 레이아웃을 연결하는 부분
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.character_row, parent, false);
        return new ViewHolder(view);
    }

    // 화면 담당
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!(urlList.get(position).url1.equals(""))) {
            holder.imgCharacter1.setVisibility(View.VISIBLE);
            Glide.with(context).load(urlList.get(position).url1).into(holder.imgCharacter1);
        }
        if(!(urlList.get(position).url2.equals(""))) {
            holder.imgCharacter2.setVisibility(View.VISIBLE);
            Glide.with(context).load(urlList.get(position).url2).into(holder.imgCharacter2);
        }
        if(!(urlList.get(position).url3.equals(""))) {
            holder.imgCharacter3.setVisibility(View.VISIBLE);
            Glide.with(context).load(urlList.get(position).url3).into(holder.imgCharacter3);
        }
        if(!(urlList.get(position).url4.equals(""))) {
            holder.imgCharacter4.setVisibility(View.VISIBLE);
            Glide.with(context).load(urlList.get(position).url4).into(holder.imgCharacter4);
        }
        if(!(urlList.get(position).url5.equals(""))) {
            holder.imgCharacter5.setVisibility(View.VISIBLE);
            Glide.with(context).load(urlList.get(position).url5).into(holder.imgCharacter5);
        }

    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    // 1. 메모리에 있는 데이터를 연결할 ViewHolder를 만든다.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCharacter1, imgCharacter2, imgCharacter3, imgCharacter4, imgCharacter5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            imgCharacter1 = itemView.findViewById(R.id.imgCharacter1);
            imgCharacter2 = itemView.findViewById(R.id.imgCharacter2);
            imgCharacter3 = itemView.findViewById(R.id.imgCharacter3);
            imgCharacter4 = itemView.findViewById(R.id.imgCharacter4);
            imgCharacter5 = itemView.findViewById(R.id.imgCharacter5);
        }
    }
}