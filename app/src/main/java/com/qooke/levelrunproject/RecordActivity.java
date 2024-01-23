package com.qooke.levelrunproject;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //  CalendarView를 연결
        CalendarView calendarView = findViewById(R.id.calendarView);

        // CalendarView의 날짜 선택 리스너 등록
        // 람다 표현식으로 대체
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // 날짜가 선택되었을 때의 동작을 추가
            String selectedDate = year + "년 " + (month + 1) + "월 " + dayOfMonth + "일";
            Toast.makeText(RecordActivity.this, "선택된 날짜: " + selectedDate, Toast.LENGTH_SHORT).show();
        });
    }
}
