// MissionActivity.java
package com.qooke.levelrunproject;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import androidx.appcompat.app.AppCompatActivity;

public class MissionActivity extends AppCompatActivity {

    private ImageButton backButton, btnClickMe1, btnClickMe2, btnClickMe3, btnClickMe4, btnClickMe5;
    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5, progressBar6;
    private ProgressBar levelProgressBar;

    private int totalSteps = 0;
    private int currentLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        // 레벨 텍스트뷰 초기화
        TextView levelTextView = findViewById(R.id.level_text);
        levelTextView.setText("Lv." + currentLevel);

        // UI 요소 초기화
        backButton = findViewById(R.id.imageButton7);
        btnClickMe1 = findViewById(R.id.imageButton2);
        btnClickMe2 = findViewById(R.id.imageButton3);
        btnClickMe3 = findViewById(R.id.imageButton4);
        btnClickMe4 = findViewById(R.id.imageButton5);
        btnClickMe5 = findViewById(R.id.imageButton6);

        //프로그레스바 기본값 설정
        levelProgressBar = findViewById(R.id.progress_bar_exp_text);
        levelProgressBar.setMax(1000); // 현재 레벨에서, 다음 레벨까지 필요한 경험치값
        levelProgressBar.setProgress(0); // 초기 설정 경험치값


        //프로그레스바
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);
        progressBar5 = findViewById(R.id.progressBar5);
        progressBar6 = findViewById(R.id.progressBar6);

        // 뒤로 가기 버튼에 대한 클릭 리스너 설정
        backButton.setOnClickListener(view -> finish());

        // 경험치 획득 버튼에 대한 클릭 리스너 설정
        btnClickMe1.setOnClickListener(v -> {
            gainExperience(100);
            enableButton(btnClickMe1, 1000, 100);
        });
        btnClickMe2.setOnClickListener(v -> {
            gainExperience(300);
            enableButton(btnClickMe2, 3000, 300);
        });
        btnClickMe3.setOnClickListener(v -> {
            gainExperience(500);
            enableButton(btnClickMe3, 5000, 500);
        });
        btnClickMe4.setOnClickListener(v -> {
            gainExperience(1000);
            enableButton(btnClickMe4, 10000, 1000);
        });
        btnClickMe5.setOnClickListener(v -> {
            gainExperience(2000);
            enableButton(btnClickMe5, 10000, 2000);
        });
    }

    // 토스트 메시지를 보여주는 메서드
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void gainExperience(int experience) {
        totalSteps += experience; // 경험치 획득
        updateProgressBars(); // 프로그레스바 갱신
        updateLevelProgressBar(experience); // 레벨 프로그레스바 및 exp_text 업데이트

        // 1000의 배수마다 레벨 업 확인
        if (totalSteps >= currentLevel * 1000) {
            levelUp();
            int remainingExp = (currentLevel + 1) * 1000 - totalSteps;
            showToast("경험치 획득! " + "다음 레벨까지 남은 경험치: " + remainingExp);
        }
    }

    private void updateLevelProgressBar(int experience) {
        int newProgress = levelProgressBar.getProgress() + experience;
        levelProgressBar.setProgress(Math.min(newProgress, levelProgressBar.getMax()));

        // 경험치바 업데이트
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(levelProgressBar, "progress", newProgress)
                .setDuration(500);  // 애니메이션 지속 시간 (500ms로 설정)
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();

        // 경험치바가 다 찼을 때, 레벨업하고 경험치바를 초기화
        if (newProgress >= levelProgressBar.getMax()) {
            levelUp();
        }
        // exp_text 업데이트
        TextView expTextView = findViewById(R.id.tv_exp_text);
        expTextView.setText(newProgress + "/" + levelProgressBar.getMax());
    }

    private void enableButton(ImageButton button, int requiredSteps, int experience) {
        if (totalSteps >= requiredSteps) {
            button.setEnabled(true);
            showToast(experience + " 경험치 획득!");
        } else {
            button.setEnabled(false);
        }
    }


    private void levelUp() {
        currentLevel++;
        levelProgressBar.setProgress(0);
        // 레벨 업 시 토스트 메시지
        showToast("레벨 UP!!");

        // 다음 레벨에서, 다음 레벨까지 필요한 경험치값 설정
        int nextLevelExp = (currentLevel + 1) * 1000;
        levelProgressBar.setMax(nextLevelExp);

        // 레벨 텍스트뷰 업데이트
        TextView levelTextView = findViewById(R.id.level_text);
        levelTextView.setText("Lv." + currentLevel);

    }

    private void updateProgressBars() {
        progressBar1.setProgress(Math.min(totalSteps, 1000));
        progressBar2.setProgress(Math.min(totalSteps, 3000));
        progressBar3.setProgress(Math.min(totalSteps, 5000));
        progressBar4.setProgress(Math.min(totalSteps, 10000));
        progressBar5.setProgress(Math.min(totalSteps, 10000));
        progressBar6.setProgress(Math.min(totalSteps, 20000));
    }
}