import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecordFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "RecordFragment";
    private Map<Long, Integer> stepCountMap = new HashMap<>();
    private LinearLayout additionalInfoLayout;
    private TextView additionalInfoTextView;
    private TextView stepCountTextView;

    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private TextView stepCountView;
    private Button resetButton;

    // 현재 걸음 수
    private int currentSteps = 0;
    private static final int REQUEST_ACTIVITY_RECOGNITION_PERMISSION = 0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        additionalInfoLayout = view.findViewById(R.id.additionalInfoLayout);
        additionalInfoTextView = view.findViewById(R.id.additionalInfoTextView);
        stepCountTextView = view.findViewById(R.id.stepCountTextView);

        stepCountView = view.findViewById(R.id.stepCountView);
        resetButton = view.findViewById(R.id.resetButton);

        // 퍼미션 체크
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            // 퍼미션 요청
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_ACTIVITY_RECOGNITION_PERMISSION);
        }

        // 걸음 센서 연결
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // 디바이스에 걸음 센서의 존재 여부 체크
        if (stepCountSensor == null) {
            Toast.makeText(requireContext(), "No Step Sensor", Toast.LENGTH_SHORT).show();
        }

        // 리셋 버튼 추가 - 리셋 기능
        resetButton.setOnClickListener(v -> {
            // 현재 걸음수 초기화
            currentSteps = 0;
            updateStepCountView();
        });

        return view;
    }

    // onRequestPermissionsResult 오버라이드 메서드 추가
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACTIVITY_RECOGNITION_PERMISSION) {
            // 퍼미션 요청 결과 처리
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션이 허용된 경우 처리
            } else {
                // 퍼미션이 거부된 경우 처리
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (stepCountSensor != null) {
            // 센서 속도 설정
            // SENSOR_DELAY_NORMAL: 20,000 초 딜레이
            // SENSOR_DELAY_UI: 6,000 초 딜레이
            // SENSOR_DELAY_GAME: 20,000 초 딜레이
            // SENSOR_DELAY_FASTEST: 딜레이 없음
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (stepCountSensor != null) {
            // 프래그먼트가 활성 상태에서 벗어나면 센서 등록 해제
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 걸음 센서 이벤트 발생시
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                // 센서 이벤트가 발생할 때마다 걸음 수 증가
                currentSteps++;
                updateStepCountView();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서 정확도 변경시 처리 (여기서는 사용하지 않음)
    }

    // 날짜에서 밀리초로 변환
    private long getMillisFromDate(int year, int month, int dayOfMonth) {
        Date date = new Date(year - 1900, month, dayOfMonth);
        return date.getTime();
    }

    // 날짜에 대한 걸음수 추가
    private void addStepCountForDate(long dateMillis, int stepCount) {
        stepCountMap.put(dateMillis, stepCount);
    }

    // 추가 정보를 보여주는 메서드
    private void showAdditionalInfo(long dateMillis, int stepCount) {
        additionalInfoTextView.setText("걸음수: " + stepCount);
        additionalInfoLayout.setVisibility(View.VISIBLE);
    }

    // 항상 오늘 날짜의 추가 정보를 표시하는 메서드
    private void showAdditionalInfoForToday() {
        // 오늘 날짜 가져오기
        Date today = new Date();
        long todayMillis = today.getTime();

        // 오늘 날짜에 대한 걸음수 가져오기
        int stepCount = stepCountMap.getOrDefault(todayMillis, 0);

        // 오늘 날짜에 대한 추가 정보를 표시
        showAdditionalInfo(todayMillis, stepCount);
    }

    // UI 갱신을 메인 쓰레드에서 수행
    private void updateStepCountView() {
        requireActivity().runOnUiThread(() -> {
            stepCountView.setText(String.valueOf(currentSteps));
        });
    }
}
