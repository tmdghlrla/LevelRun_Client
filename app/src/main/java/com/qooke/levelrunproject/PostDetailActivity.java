package com.qooke.levelrunproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgBack;
    ImageView imgProfile;
    TextView txtMyRank;
    TextView txtMyLevel;
    TextView txtMyNickname;
    ImageView imgShare;
    ImageView imgPhoto;
    ImageView imgLikes;
    TextView txtNickname;
    TextView txtLikerCnt;
    TextView txtTag1;
    TextView txtTag2;
    TextView txtTag3;
    TextView txtTag4;
    TextView txtTag5;
    TextView txtContent;
    TextView txtCreateAt;
    LinearLayout btnLayout;
    Button btnChange;
    Button btnDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
    }
}