package com.qooke.levelrunproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class CalendarDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 현재 날짜를 기본값으로 설정
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog 생성 및 설정
        return new DatePickerDialog(getActivity(), this, year, month, dayOfMonth);
    }

    // CalendarDialogFragment.java
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // 사용자가 날짜를 선택했을 때 호출되는 콜백 메서드
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, year);
        selectedDate.set(Calendar.MONTH, month);
        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // 선택한 날짜로 걸음 수 업데이트
        ((MainFragment) requireParentFragment()).updateStepCount(selectedDate);
    }
}
