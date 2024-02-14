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

public class WolfActivity extends AppCompatActivity {
    ImageView imgWolf1, imgWolf2, imgWolf3, imgWolf4, imgWolf5, imgWolf6, imgWolf7, imgWolf8, imgWolf9, back;
    ArrayList<Character> characterArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wolf);

        imgWolf1 = findViewById(R.id.imgWolf1);
        imgWolf2 = findViewById(R.id.imgWolf2);
        imgWolf3 = findViewById(R.id.imgWolf3);
        imgWolf4 = findViewById(R.id.imgWolf4);
        imgWolf5 = findViewById(R.id.imgWolf5);
        imgWolf6 = findViewById(R.id.imgWolf6);
        imgWolf7 = findViewById(R.id.imgWolf7);
        imgWolf8 = findViewById(R.id.imgWolf8);
        imgWolf9 = findViewById(R.id.imgWolf9);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
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
                case 28:
                    Glide.with(WolfActivity.this).load(character.imgUrl).into(imgWolf1);
                    break;
                case 29:
                    Glide.with(WolfActivity.this).load(character.imgUrl).into(imgWolf2);
                    break;
                case 30:
                    Glide.with(WolfActivity.this).load(character.imgUrl).into(imgWolf3);
                    break;
                case 31:
                    Glide.with(WolfActivity.this).load(character.imgUrl).into(imgWolf4);
                    break;
                case 32:
                    Glide.with(WolfActivity.this).load(character.imgUrl).into(imgWolf5);
                    break;
                case 33:
                    Glide.with(WolfActivity.this).load(character.imgUrl).into(imgWolf6);
                    break;
                case 34:
                    Glide.with(WolfActivity.this).load(character.imgUrl).into(imgWolf7);
                    break;
                case 35:
                    Glide.with(WolfActivity.this).load(character.imgUrl).into(imgWolf8);
                    break;
                case 36:
                    Glide.with(WolfActivity.this).load(character.imgUrl).into(imgWolf9);
                    break;
            }
        }
    }
}