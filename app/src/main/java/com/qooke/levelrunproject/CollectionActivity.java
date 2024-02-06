package com.qooke.levelrunproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CollectionActivity extends AppCompatActivity {
    Button btnCat, btnWolf, btnDog, btnFox;
    ArrayList<java.lang.Character> characterArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        btnCat = findViewById(R.id.btnCat);
        btnWolf = findViewById(R.id.btnWolf);
        btnDog = findViewById(R.id.btnDog);
        btnFox = findViewById(R.id.btnFox);

        btnCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionActivity.this, CatActivity.class);
                characterArrayList = (ArrayList<java.lang.Character>) getIntent().getSerializableExtra("charUrl");
                intent.putExtra("charUrl", characterArrayList);
                startActivity(intent);
            }
        });
        btnDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionActivity.this, DogActivity.class);
                characterArrayList = (ArrayList<java.lang.Character>) getIntent().getSerializableExtra("charUrl");
                intent.putExtra("charUrl", characterArrayList);
                startActivity(intent);
            }
        });
        btnFox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionActivity.this, FoxActivity.class);
                characterArrayList = (ArrayList<java.lang.Character>) getIntent().getSerializableExtra("charUrl");
                intent.putExtra("charUrl", characterArrayList);
                startActivity(intent);
            }
        });
        btnWolf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionActivity.this, WolfActivity.class);
                characterArrayList = (ArrayList<Character>) getIntent().getSerializableExtra("charUrl");
                intent.putExtra("charUrl", characterArrayList);
                startActivity(intent);
            }
        });


    }
}