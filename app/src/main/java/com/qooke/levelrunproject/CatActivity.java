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

public class CatActivity extends AppCompatActivity {
    ImageView imgCat1, imgCat2, imgCat3, imgCat4, imgCat5, imgCat6, imgCat7, imgCat8, imgCat9, back;
    ArrayList<Character> characterArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat);

        imgCat1 = findViewById(R.id.imgCat1);
        imgCat2 = findViewById(R.id.imgCat2);
        imgCat3 = findViewById(R.id.imgCat3);
        imgCat4 = findViewById(R.id.imgCat4);
        imgCat5 = findViewById(R.id.imgCat5);
        imgCat6 = findViewById(R.id.imgCat6);
        imgCat7 = findViewById(R.id.imgCat7);
        imgCat8 = findViewById(R.id.imgCat8);
        imgCat9 = findViewById(R.id.imgCat9);
        back = findViewById(R.id.back);

        Intent intent = new Intent();
        characterArrayList = (ArrayList<Character>) getIntent().getSerializableExtra("charUrl");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 액티비티 종료
                finish();
            }
        });

        for (Character character: characterArrayList) {
            Log.i("Collection_tag", "id : " + character.id);
            switch (character.characterId) {
                case 1:
                    Glide.with(CatActivity.this).load(character.imgUrl).into(imgCat1);
                    break;
                case 2:
                    Glide.with(CatActivity.this).load(character.imgUrl).into(imgCat2);
                    break;
                case 3:
                    Glide.with(CatActivity.this).load(character.imgUrl).into(imgCat3);
                    break;
                case 4:
                    Glide.with(CatActivity.this).load(character.imgUrl).into(imgCat4);
                    break;
                case 5:
                    Glide.with(CatActivity.this).load(character.imgUrl).into(imgCat5);
                    break;
                case 6:
                    Glide.with(CatActivity.this).load(character.imgUrl).into(imgCat6);
                    break;
                case 7:
                    Glide.with(CatActivity.this).load(character.imgUrl).into(imgCat7);
                    break;
                case 8:
                    Glide.with(CatActivity.this).load(character.imgUrl).into(imgCat8);
                    break;
                case 9:
                    Glide.with(CatActivity.this).load(character.imgUrl).into(imgCat9);
                    break;
            }
        }
    }
}