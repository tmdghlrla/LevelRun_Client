package com.qooke.levelrunproject;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qooke.levelrunproject.api.BoxApi;
import com.qooke.levelrunproject.api.ExerciseApi;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PapagoApi;
import com.qooke.levelrunproject.api.PlaceApi;
import com.qooke.levelrunproject.api.WeatherApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Exercise;
import com.qooke.levelrunproject.model.ExerciseRes;
import com.qooke.levelrunproject.model.Place;
import com.qooke.levelrunproject.model.PlaceList;
import com.qooke.levelrunproject.model.RandomBox;
import com.qooke.levelrunproject.model.Res;
import com.qooke.levelrunproject.model.Translate;
import com.qooke.levelrunproject.model.TranslateRes;
import com.qooke.levelrunproject.model.WeatherRes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment  implements SensorEventListener, TextToSpeech.OnInitListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
    private Button btnStart;
    private TextView txtDate;
    private boolean isSensorAvailable = false;
    // 기록 정보
    TextView txtDistance, txtKcal, txtTime, txtSteps;
    private final int[] stepDataByDay = {5000, 8000, 6000, 7000, 7500, 9000, 4000};
    private boolean isCounting = false;
    ImageView imgWeather;
    ImageView imgLoading;
    ImageView imgMission;
    TextView txtLocation;
    TextView txtDetail;
    TextView txtWeather;

    // 소리관련 처리
    MediaPlayer mp;

    // 위치관련 처리
    LocationManager locationManager;
    // 동작 처리
    LocationListener locationListener;
    SupportMapFragment mapFragment;
    BitmapDescriptor customIcon, customIcon2;
    Bitmap bitmapIcon, bitmapIcon2;

    // 지도에 내위치를 가져 왔는지 체크하는 변수
    boolean isLocationReady = false;

    // 데이터 베이스에 상자 위치 저장이 끝났는지 확인하는 변수
    boolean isInsertReady = false;
    double lat = 0;
    double lng = 0;
    final int radius = 2000;
    final String language = "ko";
    int count = 0;
    String keyword = "";
    ArrayList<Place> placeArrayList = new ArrayList<>();
    ArrayList<RandomBox> randomBoxArrayList = new ArrayList<>();
    Random random = new Random();
    ArrayList<Integer> randomNumber = new ArrayList<>();
    boolean isAlarm = false;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String weatherUrl = "";
    boolean isDuplicate = false;
    boolean isCamer = false;
    public TextToSpeech tts;
    ProgressBar progressBar;
    double calories = 0;
    double distance = 0;
    int steps = 0;
    String time = "";
    int seconds = 0;
    int minutes = 0;
    int hour = 0;
    int isStart = 0;
    Exercise exercise;
    Gson gson;
    CardView imgCardView;

    // 자이로센서 변수
    private SensorManager sensorManager;
    private Sensor stepSensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        btnStart = rootView.findViewById(R.id.btnStart);
        txtDate = rootView.findViewById(R.id.txtDate);
        txtSteps = rootView.findViewById(R.id.txtSteps);
        txtKcal = rootView.findViewById(R.id.txtKcal);
        txtTime = rootView.findViewById(R.id.txtTime);
        imgMission = rootView.findViewById(R.id.imgMission);
        txtDistance = rootView.findViewById(R.id.txtDistance);
        imgWeather = rootView.findViewById(R.id.imgWeather);
        imgLoading = rootView.findViewById(R.id.imgLoading);
        txtDetail = rootView.findViewById(R.id.txtDetail);
        txtWeather = rootView.findViewById(R.id.txtWeather);
        progressBar = rootView.findViewById(R.id.progressBar);
        imgCardView = rootView.findViewById(R.id.imgCardView);

        txtLocation = rootView.findViewById(R.id.txtLocation);

        Glide.with(this).asGif().load(R.drawable.running).into(imgLoading);

        // 현재 날짜와 시간을 가져오기 위해 Calendar 객체 생성
        Calendar calendar = Calendar.getInstance();

        // 현재 날짜 가져오기
        // int year = calendar.get(Calendar.YEAR); 연도 나중에 필요시 사용
        int month = calendar.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        txtDate.setText(month + "월 " + day + "일");
        Log.i("MainFragment_tag", txtDate.getText().toString());

        // 미디어 플레이어 준비
        mp = MediaPlayer.create(getActivity(), R.raw.get_box);

        sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);

        MapsInitializer.initialize(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);

        // 리스너를 만든다. 위치가 변할때마다 호출되는 함수를 작성
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // 위도, 경도값을 뽑아서 맞는 코드를 작성한다.
                // (Double)위도 값
                lat = location.getLatitude();
                // (Double)경도 값
                lng = location.getLongitude();

                if(!isLocationReady) {
                    getWeatherData();
                    isLocationReady = true;
                }

                if(randomBoxArrayList.size() != 10) {
                    getNetworkData();
                }

                Log.i("MainFragment_tag", "위도 : " + lat);
                Log.i("MainFragment_tag", "경도 : " + lng);

                // 상자와 내거리를 계산
                if (randomBoxArrayList.size() != 0) {
                    Log.i("MainFragment_tag", "randomBoxArrayListSize : " + randomBoxArrayList.size());
                    for(int i = 0; i<randomBoxArrayList.size(); i++) {
                        int distance = (int) DistanceByDegreeAndroid(lat, lng,
                                randomBoxArrayList.get(i).boxLat,
                                randomBoxArrayList.get(i).boxLng);

                        // 상자와의 거리가 10m 이하가 되면 메세지 음성으로 안내
                        if(!isAlarm && distance < 50) {
                            isAlarm = true;
                            tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status == TextToSpeech.SUCCESS) {
                                        // 초기화 성공 시 작업 수행
                                        speakOut();
                                    } else {
                                        // 초기화 실패 시 작업 수행
                                    }
                                }
                            });

                        }

                        // 획득 효과음 실행
                        if(distance < 10) {
                            isAlarm = false;
                            mp.start();
                            randomBoxArrayList.remove(i);
                            getBox();

                        }
                        // 상자가 내위치 기준해서 범위 밖으로 나가면 삭제한다.
                        if(distance > radius) {
                            randomBoxArrayList.remove(i);
                        }
                    }
                }

                if (mapFragment == null) {
                    mapFragment = SupportMapFragment.newInstance();
                    getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
                }

                // 마커를 만들어서 지도에 표시
                bitmapIcon = BitmapFactory.decodeResource(getResources(), R.drawable.my_marker);
                bitmapIcon2 = BitmapFactory.decodeResource(getResources(), R.drawable.box_marker2);
                if (bitmapIcon != null) {
                    // 이미지 크기 조절
                    int width = 108;  // 원하는 가로 크기
                    int height = 108; // 원하는 세로 크기
                    bitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, width, height, false);
                    bitmapIcon2 = Bitmap.createScaledBitmap(bitmapIcon2, width, height, false);
                    customIcon = BitmapDescriptorFactory.fromBitmap(bitmapIcon);
                    customIcon2 = BitmapDescriptorFactory.fromBitmap(bitmapIcon2);
                } else {
                    // 기본 마커 아이콘 사용
                    customIcon = BitmapDescriptorFactory.defaultMarker();
                }

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        googleMap.clear();
                        imgCardView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                        // 구글맵 불러오는데 시간이 걸리기 때문에 구글맵 불러온 뒤 마커를 이미지로 바꾼다.
                        if(lat == 0 || lng == 0) {
                            customIcon = BitmapDescriptorFactory.fromBitmap(bitmapIcon);
                        }

                        if (randomBoxArrayList.size() != 0) {
                            for(int i = 0; i<randomBoxArrayList.size(); i++) {
                                MarkerOptions markerOptions2 = new MarkerOptions();

                                markerOptions2.position(new LatLng(randomBoxArrayList.get(i).boxLat,
                                        randomBoxArrayList.get(i).boxLng));
                                markerOptions2.icon(customIcon2);
                                googleMap.addMarker(markerOptions2).setTag(i);
                            }
                        }
                        // 맵이 화면에 나타나면 작업하고 싶은 코드를 여기에 작성

                        // 위도, 경도로 위치데이터 셋팅
                        LatLng myLocation = new LatLng(lat, lng);

                        // 지도의 중심을 내가 정한 위치로 셋팅
                        // zoom 값이 커지면 확대 되고 작아지면 축소된다.

                        if(!isCamer) {
                            getNetworkData();
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
                            isCamer = true;
                        }


                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(myLocation);

                        markerOptions.icon(customIcon);
                        markerOptions.title("내위치");
                        googleMap.addMarker(markerOptions).setTag(0);

                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                int tagId = (int) marker.getTag();

                                if(tagId == 0 ) {

                                } else if (tagId == 1) {

                                }
                                String title = marker.getTitle();
                                Toast.makeText(getActivity(), title + "입니다.", Toast.LENGTH_SHORT).show();

                                return false;
                            }
                        });
                    }
                });

            }
        };
        // 권한 허용하는 코드
        if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return rootView;
        }
        // 권한이 있을 때 실행
        // 3초마다 위치 알려줌, 10m 이동할 때마다 위치 알려줌
        // 거리 위치 사용을 원치 않는경우 -1로 쓴다.
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                -1,
                locationListener
        );

        // 활동 퍼미션 체크
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        txtDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLocationReady) {
                    Toast.makeText(getActivity(), "위치 정보를 불러오는 중입니다.\n잠시후에 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);
            }
        });
        imgMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MissionActivity.class);
                getActivity().startActivity(intent);
            }
        });

        // 시작 버튼 눌렀을 때
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tag", "isStart : " + isStart);
                if(isStart == 1) {
                    return;
                }
                isStart = 1;
                sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

                // 디바이스에 걸음 센서의 존재 여부 체크
                if (stepSensor == null) {

                }
                sensorManager.registerListener(MainFragment.this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);

                excerciseRecord();
            }
        });



        return rootView;
    }

    // 운동정보를 db에 기록
    private void excerciseRecord() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        ExerciseApi api = retrofit.create(ExerciseApi.class);

        sp = getContext().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;
        Exercise excercise = new Exercise(distance, calories, time, steps);
        Call<Res> call = api.setRecord(token, excercise);
        call.enqueue(new Callback<Res>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                if(response.isSuccessful()){
                    sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                    editor = sp.edit();

                    editor.remove("exercise");
                    editor.commit();

                }else{

                }
            }

            @Override
            public void onFailure(Call<Res> call, Throwable t) {

            }
        });
    }

    private void getBox() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        BoxApi api = retrofit.create(BoxApi.class);

        sp = getContext().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<Res> call = api.getBox(token);
        call.enqueue(new Callback<Res>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                Log.i("MainFragment_tag", "Box : " + response.code());
                if(response.isSuccessful()){

                }else{

                }
            }

            @Override
            public void onFailure(Call<Res> call, Throwable t) {

            }
        });
    }

    private void getWeatherData() {
        Retrofit retrofit = NetworkClient.getWeatherRetrofitClient(getActivity());

        WeatherApi api = retrofit.create(WeatherApi.class);

        Call<WeatherRes> call = api.getWeather(
                lat,
                lng,
                Config.OPENWEATHERMAP_API_KEY,
                "metric",
                "kr");
        call.enqueue(new Callback<WeatherRes>() {
            @Override
            public void onResponse(Call<WeatherRes> call, Response<WeatherRes> response) {
                Log.i("MainFragment_tag", ""+response.code());
                if(response.isSuccessful()){
                    WeatherRes weatherRes = response.body();

                    weatherUrl = Config.WEATHER_IMAGE_URL + weatherRes.weather.get(0).icon + "@2x.png";

                    String min = "" + weatherRes.main.temp_min;
                    String max = "" + weatherRes.main.temp_max;
                    String temp = "" + weatherRes.main.temp;
                    String location = weatherRes.name;
                    txtWeather.setText(weatherRes.weather.get(0).description);
                    if(location.contains("-")) {
                        location = location.replace("-", "");
                    }

                    getTranslatedData(location);
                    Log.i("MainFragment_tag", "location : " + location);

                    Glide.with(getActivity()).load(weatherUrl).into(imgWeather);


                    txtLocation.setText("");

                }else{

                }
            }

            @Override
            public void onFailure(Call<WeatherRes> call, Throwable t) {
            }
        });
    }

    private void getTranslatedData(String location) {
        // API 호출

        Retrofit retrofit = NetworkClient.getTranslateRetrofitClient(getActivity());

        PapagoApi api = retrofit.create(PapagoApi.class);
        Translate translate = new Translate("en", "ko", location);

        Call<TranslateRes> call = api.getTranslate(
                Config.X_NAVER_CLIENT_ID,
                Config.X_NAVER_CLIENT_SECRET,
                translate);
        call.enqueue(new Callback<TranslateRes>() {
            @Override
            public void onResponse(Call<TranslateRes> call, Response<TranslateRes> response) {
                if(response.isSuccessful()){
                    TranslateRes translateRes = response.body();
                    txtLocation.setText(translateRes.message.result.translatedText);

                } else{

                }
            }

            @Override
            public void onFailure(Call<TranslateRes> call, Throwable t) {
            }
        });
    }


    private void getNetworkData() {
        // API 호출

        Retrofit retrofit = NetworkClient.getPlaceRetrofitClient(getActivity());

        PlaceApi api = retrofit.create(PlaceApi.class);

        Call<PlaceList> call = api.getPlaceList(
                lat+","+lng,
                radius,
                language,
                keyword,
                Config.GOOGLE_API_KEY);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                Log.i("MainFragment_tag", ""+response.code());
                if(response.isSuccessful()){

                    PlaceList placeList = response.body();

                    placeArrayList.clear();
                    placeArrayList.addAll(placeList.results);
                    randomNumber.clear();

                    Log.i("MainFragment_tag", "data_size : " + placeArrayList.size());
                    // 중복되지 않는 랜덤 난수를 받아온다.
                    for (int i = 0; i<placeArrayList.size(); i++) {
                        randomNumber.add(i, random.nextInt(placeArrayList.size()));
                        for(int j=0; j<i; j++) {
                            if(randomNumber.get(i) == randomNumber.get(j)) {
                                i--;
                            }
                        }
                    }
                    // 처음의 상자정보가 없는 경우에는 상자 좌표를 10개 생성한다.
                    if(randomBoxArrayList.size() == 0) {
                        for(int i = 0; i<10; i++) {
                            RandomBox randomBox = new RandomBox(
                                    placeArrayList.get(randomNumber.get(i)).geometry.location.lat,
                                    placeArrayList.get(randomNumber.get(i)).geometry.location.lng
                            );
                            randomBoxArrayList.add(randomBox);
                        }
                    } else {
                        // 상자 정보가 10개가 아닌경우
                        for(int i = 0; i<placeArrayList.size(); i++) {
                            isDuplicate = false;
                            for(int j = 0; j<randomBoxArrayList.size(); j++) {
                                if(randomBoxArrayList.size() == 10) {
                                    i = 10;
                                    return;
                                }
                                // 중복된 좌표값이 있는지 확인
                                if(randomBoxArrayList.get(j).boxLat == placeArrayList.get(randomNumber.get(i)).geometry.location.lat
                                        && randomBoxArrayList.get(j).boxLng == placeArrayList.get(randomNumber.get(i)).geometry.location.lng) {
                                    isDuplicate = true;
                                }
                                // 이미 생성된 상자 좌표값과 랜덤생성된 좌표값이 중복된 값이 없으면 저장한다.
                                if(j == randomBoxArrayList.size()-1) {
                                    if(!isDuplicate) {
                                        RandomBox randomBox = new RandomBox(
                                                placeArrayList.get(randomNumber.get(i)).geometry.location.lat,
                                                placeArrayList.get(randomNumber.get(i)).geometry.location.lng
                                        );
                                        randomBoxArrayList.add(randomBox);
                                    }
                                }
                            }
                        }
                    }
//                    String json = new Gson().toJson(randomBoxArrayList);
                } else{

                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
            }
        });
    }

    // 좌표를 이용한 거리 계산
    public double DistanceByDegreeAndroid(double myLat, double myLng, double boxLat, double boxLng){
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(myLat);
        startPos.setLongitude(myLng);
        endPos.setLatitude(boxLat);
        endPos.setLongitude(boxLng);

        double distance = startPos.distanceTo(endPos);

        return distance;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();

        if (!checkLocationPermission()) {
            return;
        }


        sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        gson = new GsonBuilder().create();
        String values = sp.getString("exercise","");

        isCamer = false;
        isLocationReady = false;
        
        if (locationManager != null && locationListener != null) {
            // 위치 리스너를 다시 등록합니다.
            // 여기서 locationManager와 locationListener는 이전에 생성 및 초기화된 객체여야 합니다.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // 앱내 저장소에 값이 저장되어 있는 경우
        if(!values.equals("")){
            exercise = gson.fromJson(values, Exercise.class);
            calories = exercise.kcal;
            distance = exercise.distance;
            steps = exercise.steps;

            seconds = (int) (steps * 0.65);
            hour = seconds / 3600;
            seconds = seconds - (hour*3600);
            minutes = seconds / 60;


            setConvert();

            excerciseRecord();

            txtKcal.setText("" + calories);
            txtDistance.setText("" + distance);
            txtSteps.setText("" + steps);

            // 운동 정보가 있는경우
            if(steps != 0) {
                sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

                // 디바이스에 걸음 센서의 존재 여부 체크
                if (stepSensor == null) {

                }
                sensorManager.registerListener(MainFragment.this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
            // 운동 정보가 없는 경우 db를 조회한다.
            else {
                getRecord();
            }
        }
        // 저장되어 있는 값이 없을 때 db를 조회한다.
        else {
            getRecord();
        }



        // 걸음 센서 세팅
        // * 옵션
        // - TYPE_STEP_DETECTOR:  리턴 값이 무조건 1, 앱이 종료되면 다시 0부터 시작
        // - TYPE_STEP_COUNTER : 앱 종료와 관계없이 계속 기존의 값을 가지고 있다가 1씩 증가한 값을 리턴

        // 운동기록도 없고 버튼을 클릭하지 않으면 리스너를 할당하지 않는다.
        if(isStart != 1 && steps == 0) {
            return;
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // db에 있는 운동기록을 가져와 세팅한다.
    private void getRecord() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        ExerciseApi api = retrofit.create(ExerciseApi.class);

        sp = getContext().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<ExerciseRes> call = api.getRecord(token);
        call.enqueue(new Callback<ExerciseRes>() {
            @Override
            public void onResponse(Call<ExerciseRes> call, Response<ExerciseRes> response) {
                if(response.isSuccessful()){
                    ExerciseRes exerciseRes = response.body();

                    steps = exerciseRes.items.get(0).steps;
                    distance = exerciseRes.items.get(0).distance;
                    calories = exerciseRes.items.get(0).kcal;
                    txtSteps.setText("" + steps);
                    txtDistance.setText("" + distance);
                    txtKcal.setText("" + calories);

                    if(steps != 0) {
                        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

                        // 디바이스에 걸음 센서의 존재 여부 체크
                        if (stepSensor == null) {

                        }
                        sensorManager.registerListener(MainFragment.this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<ExerciseRes> call, Throwable t) {

            }
        });
    }

    public void onPause() {
        super.onPause();

        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        // 센서 리스너 해제
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 프래그먼트가 detached 될 때 위치 업데이트 리스너를 해제
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut(){
        String voice = "근처에 상자가 있습니다.";
        CharSequence text = voice;
        tts.setPitch((float)1.0); // 음성 톤 높이 지정
        tts.setSpeechRate((float)1.0); // 음성 속도 지정

        // 첫 번째 매개변수: 음성 출력을 할 텍스트
        // 두 번째 매개변수: 1. TextToSpeech.QUEUE_FLUSH - 진행중인 음성 출력을 끊고 이번 TTS의 음성 출력
        //                 2. TextToSpeech.QUEUE_ADD - 진행중인 음성 출력이 끝난 후에 이번 TTS의 음성 출력
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
    }
    @Override
    public void onDestroy() {
        if(tts!=null){ // 사용한 TTS객체 제거
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) { // OnInitListener를 통해서 TTS 초기화
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.KOREA); // TTS언어 한국어로 설정

            if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS", "This Language is not supported");
            }else{
                speakOut();// onInit에 음성출력할 텍스트를 넣어줌
            }
        }else{
            Log.e("TTS", "Initialization Failed!");
        }
    }

    Dialog dialog;
    private void showProgress(){
        dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(getActivity()));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissProgress(){
        dialog.dismiss();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if(event.values[0]==1.0f) {
                DecimalFormat df = new DecimalFormat("#.##");

                seconds = (int) (steps * 0.65);
                hour = seconds / 3600;
                seconds = seconds - (hour*3600);
                minutes = seconds / 60;

                steps++;
                calories = Double.parseDouble(df.format(calories + 0.04));
                distance = Double.parseDouble(df.format(distance + 0.001));

                txtSteps.setText("" + steps);
                txtKcal.setText("" + calories);
                txtDistance.setText("" + distance);

                setConvert();

            }

        }
    }

    private void setConvert() {
        if(hour < 10) {
            if(minutes < 10) {
                txtTime.setText("0" + hour + ":0" + minutes);
                time = txtTime.getText().toString().trim() + ":00";

                save();
                return;
            }
            txtTime.setText("0" + hour + ":" + minutes);
            time = txtTime.getText().toString().trim() + ":00";

            save();

        } else {
            if(minutes < 10) {
                txtTime.setText(hour + ":0" + minutes);
                time = txtTime.getText().toString().trim() + ":00";

                save();
                return;
            }
            txtTime.setText(hour + ":" + minutes);
            time = txtTime.getText().toString().trim() + ":00";
            save();
        }
    }

    private void save() {
        exercise = new Exercise(distance, calories, time, steps);
        editor = sp.edit();

        gson = new GsonBuilder().create();
        String value = gson.toJson(exercise);

        editor.putString("exercise", value);
        editor.apply();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 정확도 변경 시 처리
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // SensorManager 및 걸음 수 센서 초기화
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }
}