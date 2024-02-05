package com.qooke.levelrunproject;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements SensorEventListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ToggleButton toggleButton;
    private TextView stepsTextView;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorAvailable = false;

    private TextView stepsValueTextView;
    private TextView caloriesValueTextView;
    private TextView timeValueTextView;
    private TextView distanceValueTextView;
    private final int[] stepDataByDay = {5000, 8000, 6000, 7000, 7500, 9000, 4000};

    private boolean isCounting = false;
    private int stepCount = 0;
    private Calendar selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_social, container, false);

        toggleButton = rootView.findViewById(R.id.toggleButton);
        stepsTextView = rootView.findViewById(R.id.stepsTextView);
        stepsValueTextView = rootView.findViewById(R.id.stepsValue);
        caloriesValueTextView = rootView.findViewById(R.id.caloriesValue);
        timeValueTextView = rootView.findViewById(R.id.timeValue);
        distanceValueTextView = rootView.findViewById(R.id.distanceValue);

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        initStepCounter();

        if (stepSensor != null) {
            isSensorAvailable = true;
        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startCounting();
                } else {
                    pauseCounting();
                }
            }
        });
        return rootView;
    }

    // 텍스트뷰를 클릭했을 때 호출되는 메서드
    public void openCalendarDialog(View view) {
        // 현재 날짜를 기본값으로 설정
        final Calendar currentDate = Calendar.getInstance();
        // DatePickerDialog 생성 및 설정
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 사용자가 날짜를 선택했을 때 호출되는 콜백 메서드
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, monthOfYear);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // 선택한 날짜로 걸음 수 업데이트
                        updateStepCount(selectedDate);
                    }
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );

        // 다이얼로그 표시
        datePickerDialog.show();
    }


    private void initStepCounter() {
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        resetDailyCount();
    }

    private void startCounting() {
        isCounting = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isCounting) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stepCount++;
                    updateStepsTextView();
                }
            }
        }).start();
    }

    private void pauseCounting() {
        isCounting = false;
    }

    private void resetDailyCount() {
        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        String savedDate = preferences.getString("lastResetDate", "");

        if (!getCurrentDate().equals(savedDate)) {
            stepCount = 0;
            preferences.edit().putString("lastResetDate", getCurrentDate()).apply();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSensorAvailable) {
            resetDailyCount();
            sensorManager.registerListener((SensorEventListener) getActivity(), stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isSensorAvailable) {
            sensorManager.unregisterListener((SensorListener) getActivity());
        }
        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        preferences.edit().putInt("initialStepCount", stepCount).apply();
    }

    private void updateStepsTextView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stepsTextView.setText(" " + stepCount);
                float calories = calculateCalories(stepCount);
                String time = calculateTime(stepCount);
                float distanceValue = calculateDistance(stepCount);

                stepsValueTextView.setText(String.valueOf(stepCount));
                caloriesValueTextView.setText(String.valueOf(calories));
                timeValueTextView.setText(time);
                distanceValueTextView.setText(String.format("%.2f km", distanceValue / 1000));
            }
        });
    }

    private float calculateDistance(float steps) {
        return steps * 0.7f / 1000;
    }

    private void showCalendarDialog() {
        // CalendarDialogFragment를 띄웁니다.
        DialogFragment calendarDialogFragment = new CalendarDialogFragment();
        calendarDialogFragment.show(getActivity().getSupportFragmentManager(), "calendar_dialog");
    }

    protected void updateStepCount(Calendar selectedDate) {
        int steps = getDummyStepCountByDate(selectedDate);
        stepCount = steps;
        updateStepsTextView();
    }


    private int getDummyStepCountByDate(Calendar selectedDate) {
        int dayOfWeek = (selectedDate.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        return stepDataByDay[dayOfWeek];
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float steps = event.values[0];
        updateStepsTextView();
        float calories = calculateCalories(steps);
        String time = calculateTime(steps);
        float distanceValue = calculateDistance(steps);
        String distance = String.format("%.2f km", distanceValue / 1000);

        stepsValueTextView.setText(String.valueOf(steps));
        caloriesValueTextView.setText(String.valueOf(calories));
        timeValueTextView.setText(time);
        distanceValueTextView.setText(distance);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서 정확도 변경 시 호출되는 메서드
    }

    private float calculateCalories(float steps) {
        return steps * 0.05f;
    }

    private String calculateTime(float steps) {
        int seconds = (int) (steps * 0.75);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%d분 %02d초", minutes, seconds);
    }
}