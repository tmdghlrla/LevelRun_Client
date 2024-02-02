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

        imgBack = findViewById(R.id.imgBack);
        imgProfile = findViewById(R.id.imgProfile);
        txtMyRank = findViewById(R.id.txtMyRank);
        txtMyLevel = findViewById(R.id.txtMyLevel);
        txtMyNickname = findViewById(R.id.txtMyNickname);
        imgShare = findViewById(R.id.imgShare);
        imgPhoto = findViewById(R.id.imgPhoto);
        imgLikes = findViewById(R.id.imgLikes);
        txtNickname = findViewById(R.id.txtNickname);
        txtLikerCnt = findViewById(R.id.txtLikerCnt);
        txtTag1 = findViewById(R.id.txtTag1);
        txtTag2 = findViewById(R.id.txtTag2);
        txtTag3 = findViewById(R.id.txtTag3);
        txtTag4 = findViewById(R.id.txtTag4);
        txtTag5 = findViewById(R.id.txtTag5);
        txtContent = findViewById(R.id.txtContent);
        txtCreateAt = findViewById(R.id.txtCreateAt);
        btnLayout = findViewById(R.id.btnLayout);
        btnChange = findViewById(R.id.btnChange);
        btnDelete = findViewById(R.id.btnDelete);






    }
}