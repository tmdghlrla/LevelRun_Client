package com.qooke.levelrunproject;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.hardware.SensorListener;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


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
import com.qooke.levelrunproject.api.BoxApi;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PapagoApi;
import com.qooke.levelrunproject.api.PlaceApi;
import com.qooke.levelrunproject.api.WeatherApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Place;
import com.qooke.levelrunproject.model.PlaceList;
import com.qooke.levelrunproject.model.RandomBox;
import com.qooke.levelrunproject.model.Res;
import com.qooke.levelrunproject.model.Translate;
import com.qooke.levelrunproject.model.TranslateRes;
import com.qooke.levelrunproject.model.WeatherRes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class MainFragment extends Fragment implements SensorEventListener, TextToSpeech.OnInitListener {

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
    private ToggleButton toggleButton;
    private TextView txtDate;
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
    ImageView imgWeather;
    ImageView imgLoading;
    TextView txtKal;
    TextView txtTime;
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
    String weatherUrl = "";
    boolean isDuplicate = false;
    boolean isCamer = false;
    public TextToSpeech tts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        toggleButton = rootView.findViewById(R.id.toggleButton);
        txtDate = rootView.findViewById(R.id.txtDate);
        stepsValueTextView = rootView.findViewById(R.id.stepsValue);
        txtKal = rootView.findViewById(R.id.txtKal);
        txtTime = rootView.findViewById(R.id.txtTime);
        distanceValueTextView = rootView.findViewById(R.id.distanceValue);
        imgWeather = rootView.findViewById(R.id.imgWeather);
        imgLoading = rootView.findViewById(R.id.imgLoading);
        txtDetail = rootView.findViewById(R.id.txtDetail);
        txtWeather = rootView.findViewById(R.id.txtWeather);

        txtLocation = rootView.findViewById(R.id.txtLocation);

        Glide.with(this).asGif().load(R.drawable.loading_run).into(imgLoading);

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
                        toggleButton.setText(""+ randomBoxArrayList.size());
                        imgLoading.setVisibility(View.GONE);

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
        if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getActivity().finish();
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

    private void getBox() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        BoxApi api = retrofit.create(BoxApi.class);

        SharedPreferences sp = getContext().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
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
        showProgress();
        Retrofit retrofit = NetworkClient.getWeatherRetrofitClient(getActivity());

        WeatherApi api = retrofit.create(WeatherApi.class);

        Call<WeatherRes> call = api.getWeather(
                lat,
                lng,
                Config.OPENWEATHERMAP_API_KEY,
                "metric",
                "kr");
        Log.i("MainFragment_tag", "test");
        call.enqueue(new Callback<WeatherRes>() {
            @Override
            public void onResponse(Call<WeatherRes> call, Response<WeatherRes> response) {
                dismissProgress();
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
                dismissProgress();
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
        isCamer = false;
        isLocationReady = false;
        if (locationManager != null && locationListener != null) {
            // 위치 리스너를 다시 등록합니다.
            // 여기서 locationManager와 locationListener는 이전에 생성 및 초기화된 객체여야 합니다.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    public void onPause() {
        super.onPause();
        if (isSensorAvailable) {
            sensorManager.unregisterListener((SensorListener) getActivity());
        }
        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        preferences.edit().putInt("initialStepCount", stepCount).apply();

        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
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

    private void updateStepsTextView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        txtKal.setText(String.valueOf(calories));
        txtTime.setText(time);
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
}