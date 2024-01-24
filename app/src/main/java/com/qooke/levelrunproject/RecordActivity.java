import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RecordActivity extends AppCompatActivity {

    // 날짜와 걸음수를 저장하는 맵
    private Map<Long, Integer> stepCountMap = new HashMap<>();
    private LinearLayout additionalInfoLayout;
    private TextView additionalInfoTextView;
    private TextView stepCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // CalendarView를 연결
        CalendarView calendarView = findViewById(R.id.calendarView);
        additionalInfoLayout = findViewById(R.id.additionalInfoLayout);
        additionalInfoTextView = findViewById(R.id.additionalInfoTextView);
        stepCountTextView = findViewById(R.id.stepCountTextView);

        // CalendarView의 날짜 선택 리스너 등록
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // 날짜가 선택되었을 때의 동작을 추가

            // 선택된 날짜에 대한 걸음수 가져오기
            long selectedDateMillis = getMillisFromDate(year, month, dayOfMonth);
            int stepCount = stepCountMap.getOrDefault(selectedDateMillis, 0);

            // 토스트 메시지로 걸음수 표시
            Toast.makeText(RecordActivity.this, "걸음수: " + stepCount, Toast.LENGTH_SHORT).show();

            // 선택된 날짜에 대한 추가 정보를 표시
            showAdditionalInfo(selectedDateMillis, stepCount);
        });

        // 테스트를 위해 몇 가지 날짜에 걸음수 추가
        addStepCountForDate(getMillisFromDate(2024, 0, 23), 5000);
        addStepCountForDate(getMillisFromDate(2024, 0, 24), 8000);
        addStepCountForDate(getMillisFromDate(2024, 0, 25), 10000);

        // 항상 추가 정보를 표시하도록 설정
        showAdditionalInfoForToday();

        // 만보기 데이터 업데이트를 위한 Google Fit API 설정
        setupGoogleFitApi();
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

    // Google Fit API 설정
    private void setupGoogleFitApi() {
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, new Scope(Fitness.SCOPE_ACTIVITY_READ_WRITE));

        if (!GoogleSignIn.hasPermissions(account, new Scope(Fitness.SCOPE_ACTIVITY_READ_WRITE))) {
            GoogleSignIn.requestPermissions(
                    this,
                    1,
                    account,
                    new Scope(Fitness.SCOPE_ACTIVITY_READ_WRITE)
            );
        } else {
            startStepCountUpdates();
        }
    }

    // Google Fit API로부터 걸음 수 업데이트 시작
    private void startStepCountUpdates() {
        OnDataPointListener onDataPointListener = dataPoint -> {
            for (com.google.android.gms.fitness.data.Field field : dataPoint.getDataType().getFields()) {
                int stepCount = dataPoint.getValue(field).asInt();
                updateStepCountTextView(stepCount);
            }
        };

        Fitness.getSensorsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .add(
                        new SensorRequest.Builder()
                                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                                .setSamplingRate(1, TimeUnit.SECONDS)
                                .build(),
                        onDataPointListener
                )
                .addOnCompleteListener(task -> {
                    // 센서 등록 완료
                });
    }

    // 걸음 수를 TextView에 업데이트
    private void updateStepCountTextView(int stepCount) {
        runOnUiThread(() -> stepCountTextView.setText(String.valueOf(stepCount)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                startStepCountUpdates();
            }
        }
    }
}
