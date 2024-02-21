package com.qooke.levelrunproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.adapter.WeatherAdapter;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PapagoApi;
import com.qooke.levelrunproject.api.WeatherApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Translate;
import com.qooke.levelrunproject.model.TranslateRes;
import com.qooke.levelrunproject.model.WeatherRes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WeatherActivity extends AppCompatActivity {
    TextView txtLocation, txtTemp, txtWeather, txtLevel;
    ImageView imgBack, imgWeather, imgAirLevel;
    double lat, lng;
    RecyclerView recyclerView;
    WeatherAdapter adapter;
    ArrayList<WeatherRes.Air> airArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        txtLocation = findViewById(R.id.txtLocation);
        imgWeather = findViewById(R.id.imgWeather);
        txtTemp = findViewById(R.id.txtTemp);
        imgBack = findViewById(R.id.imgBack);
        txtWeather = findViewById(R.id.txtWeather);
        imgAirLevel = findViewById(R.id.imgAirLevel);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(WeatherActivity.this));

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lat = getIntent().getDoubleExtra("lat" , 0);
        lng = getIntent().getDoubleExtra("lng" , 0);

        getWeatherData();
        getAirCondition();
        timelyWeatherData();
    }

    private void timelyWeatherData() {
        Retrofit retrofit = NetworkClient.getWeatherRetrofitClient(WeatherActivity.this);

        WeatherApi api = retrofit.create(WeatherApi.class);

        Call<WeatherRes> call = api.getTimelyWeather(
                lat,
                lng,
                Config.OPENWEATHERMAP_API_KEY,
                "metric",
                "kr");
        call.enqueue(new Callback<WeatherRes>() {
            @Override
            public void onResponse(Call<WeatherRes> call, Response<WeatherRes> response) {
                if(response.isSuccessful()){
                    WeatherRes weatherRes = response.body();

                    airArrayList.addAll(weatherRes.list);
                    adapter = new WeatherAdapter(WeatherActivity.this, airArrayList);
                    recyclerView.setAdapter(adapter);

                }else{

                }
            }

            @Override
            public void onFailure(Call<WeatherRes> call, Throwable t)
            {

            }
        });
    }

    private void getAirCondition() {
        // API 호출
        showProgress();
        Retrofit retrofit = NetworkClient.getWeatherRetrofitClient(WeatherActivity.this);
        WeatherApi api = retrofit.create(WeatherApi.class);

        Call<WeatherRes> call = api.getCondition(
                lat,
                lng,
                Config.OPENWEATHERMAP_API_KEY);
        call.enqueue(new Callback<WeatherRes>() {
            @Override
            public void onResponse(Call<WeatherRes> call, Response<WeatherRes> response) {
                dismissProgress();
                if(response.isSuccessful()){
                    WeatherRes weatherRes = response.body();
                    int airLevel = weatherRes.list.get(0).main.aqi;


                    switch (airLevel) {
                        case 1:
                            imgAirLevel.setImageResource(R.drawable.air_pollution1);
                            break;
                        case 2:
                            imgAirLevel.setImageResource(R.drawable.air_pollution2);
                            break;
                        case 3:
                            imgAirLevel.setImageResource(R.drawable.air_pollution3);
                            break;
                        case 4:
                            imgAirLevel.setImageResource(R.drawable.air_pollution4);
                            break;
                        default:
                            imgAirLevel.setImageResource(R.drawable.air_pollution5);
                    }



                } else{

                }
            }

            @Override
            public void onFailure(Call<WeatherRes> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    private void getWeatherData() {
        Retrofit retrofit = NetworkClient.getWeatherRetrofitClient(WeatherActivity.this);

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
                if(response.isSuccessful()){
                    WeatherRes weatherRes = response.body();

                    String weatherUrl = Config.WEATHER_IMAGE_URL + weatherRes.weather.get(0).icon + "@2x.png";

                    String min = "" + weatherRes.main.temp_min;
                    String max = "" + weatherRes.main.temp_max;
                    String temp = "" + weatherRes.main.temp;
                    String location = weatherRes.name;

                    if(location.contains("-")) {
                        location = location.replace("-", "");
                    }

                    getTranslatedData(location);
                    Log.i("MainFragment_tag", "location : " + location);

                    min = min.split("\\.")[0] + "." + min.split("\\.")[1].substring(0, 1);
                    max = max.split("\\.")[0] + "." + max.split("\\.")[1].substring(0, 1);
                    temp = temp.split("\\.")[0] + "." + temp.split("\\.")[1].substring(0, 1);


                    Glide.with(WeatherActivity.this).load(weatherUrl).into(imgWeather);
                    txtTemp.setText(temp + "°");
                    txtWeather.setText(weatherRes.weather.get(0).description);


                }else{

                }
            }

            @Override
            public void onFailure(Call<WeatherRes> call, Throwable t)
            {

            }
        });
    }

    private void getTranslatedData(String location) {
        // API 호출

        Retrofit retrofit = NetworkClient.getTranslateRetrofitClient(WeatherActivity.this);

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

    Dialog dialog;
    private void showProgress(){
        dialog = new Dialog(WeatherActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(WeatherActivity.this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissProgress(){
        dialog.dismiss();
    }
}