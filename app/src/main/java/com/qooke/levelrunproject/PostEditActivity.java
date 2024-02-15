package com.qooke.levelrunproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.model.Posting;
import com.qooke.levelrunproject.model.PostingDetail;

public class PostEditActivity extends AppCompatActivity {
    TextView txtTag, txtContent, txtCreatedAt;
    Button btnUpdate;
    ImageView imgBack, imgPhoto;
    PostingDetail postingDetail;
    String createdAt = "";
    String postingUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
        txtTag = findViewById(R.id.txtTag);
        txtContent = findViewById(R.id.txtContent);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        imgBack = findViewById(R.id.imgBack);
        imgPhoto = findViewById(R.id.imgPhoto);
        btnUpdate = findViewById(R.id.btnUpdate);

        postingDetail = (PostingDetail) getIntent().getSerializableExtra("postingDetail");
        String tags = getIntent().getStringExtra("tags");

        postingUrl = postingDetail.item.get(0).postingUrl;
        createdAt = postingDetail.item.get(0).createdAt;

        if (createdAt.contains("T")) {
            createdAt = createdAt.replace("T", " ");
        }

        Glide.with(PostEditActivity.this).load(postingUrl).into(imgPhoto);
        txtTag.setText(tags);
        txtContent.setText(postingDetail.item.get(0).content);
        txtCreatedAt.setText(createdAt);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}