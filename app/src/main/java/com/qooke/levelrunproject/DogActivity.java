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

public class DogActivity extends AppCompatActivity {
    ImageView imgDog1, imgDog2, imgDog3, imgDog4, imgDog5, imgDog6, imgDog7, imgDog8, imgDog9, dogBack;
    ArrayList<Character> characterArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog);

        imgDog1 = findViewById(R.id.imgDog1);
        imgDog2 = findViewById(R.id.imgDog2);
        imgDog3 = findViewById(R.id.imgDog3);
        imgDog4 = findViewById(R.id.imgDog4);
        imgDog5 = findViewById(R.id.imgDog5);
        imgDog6 = findViewById(R.id.imgDog6);
        imgDog7 = findViewById(R.id.imgDog7);
        imgDog8 = findViewById(R.id.imgDog8);
        imgDog9 = findViewById(R.id.imgDog9);
        dogBack = findViewById(R.id.dogBack);

        Intent intent = new Intent();
        characterArrayList = (ArrayList<Character>) getIntent().getSerializableExtra("charUrl");

        dogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 액티비티 종료
                finish();
            }
        });

        for (Character character: characterArrayList) {
            Log.i("Collection_tag", "id : " + character.id);
            switch (character.characterId) {
                case 10:
                    Glide.with(DogActivity.this).load(character.imgUrl).into(imgDog1);
                    break;
                case 11:
                    Glide.with(DogActivity.this).load(character.imgUrl).into(imgDog2);
                    break;
                case 12:
                    Glide.with(DogActivity.this).load(character.imgUrl).into(imgDog3);
                    break;
                case 13:
                    Glide.with(DogActivity.this).load(character.imgUrl).into(imgDog4);
                    break;
                case 14:
                    Glide.with(DogActivity.this).load(character.imgUrl).into(imgDog5);
                    break;
                case 15:
                    Glide.with(DogActivity.this).load(character.imgUrl).into(imgDog6);
                    break;
                case 16:
                    Glide.with(DogActivity.this).load(character.imgUrl).into(imgDog7);
                    break;
                case 17:
                    Glide.with(DogActivity.this).load(character.imgUrl).into(imgDog8);
                    break;
                case 18:
                    Glide.with(DogActivity.this).load(character.imgUrl).into(imgDog9);
                    break;
            }
        }
    }
}