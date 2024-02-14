package com.qooke.levelrunproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.model.Character;

import java.util.ArrayList;

public class FoxActivity extends AppCompatActivity {
    ImageView imgFox1, imgFox2, imgFox3, imgFox4, imgFox5, imgFox6, imgFox7, imgFox8, imgFox9, foxBack;
    ArrayList<Character> characterArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fox);

        imgFox1 = findViewById(R.id.imgFox1);
        imgFox2 = findViewById(R.id.imgFox2);
        imgFox3 = findViewById(R.id.imgFox3);
        imgFox4 = findViewById(R.id.imgFox4);
        imgFox5 = findViewById(R.id.imgFox5);
        imgFox6 = findViewById(R.id.imgFox6);
        imgFox7 = findViewById(R.id.imgFox7);
        imgFox8 = findViewById(R.id.imgFox8);
        imgFox9 = findViewById(R.id.imgFox9);
        foxBack = findViewById(R.id.foxBack);

        foxBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 액티비티 종료
                finish();
            }
        });

        Intent intent = new Intent();
        characterArrayList = (ArrayList<Character>) getIntent().getSerializableExtra("charUrl");

        for (Character character: characterArrayList) {
            Log.i("Collection_tag", "id : " + character.id);
            switch (character.characterId) {
                case 19:
                    Glide.with(FoxActivity.this).load(character.imgUrl).into(imgFox1);
                    break;
                case 20:
                    Glide.with(FoxActivity.this).load(character.imgUrl).into(imgFox2);
                    break;
                case 21:
                    Glide.with(FoxActivity.this).load(character.imgUrl).into(imgFox3);
                    break;
                case 22:
                    Glide.with(FoxActivity.this).load(character.imgUrl).into(imgFox4);
                    break;
                case 23:
                    Glide.with(FoxActivity.this).load(character.imgUrl).into(imgFox5);
                    break;
                case 24:
                    Glide.with(FoxActivity.this).load(character.imgUrl).into(imgFox6);
                    break;
                case 25:
                    Glide.with(FoxActivity.this).load(character.imgUrl).into(imgFox7);
                    break;
                case 26:
                    Glide.with(FoxActivity.this).load(character.imgUrl).into(imgFox8);
                    break;
                case 27:
                    Glide.with(FoxActivity.this).load(character.imgUrl).into(imgFox9);
                    break;
            }
        }
    }
}