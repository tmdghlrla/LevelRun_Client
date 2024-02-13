package com.qooke.levelrunproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.R;
import com.qooke.levelrunproject.WeatherActivity;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.WeatherRes;

import java.time.format.DateTimeParseException;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    Context context;
    ArrayList<WeatherRes.Air> airArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherRes.Air> airArrayList) {
        this.context = context;
        this.airArrayList = airArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_row, parent, false);
        return new WeatherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        WeatherRes.Air air = airArrayList.get(position);
        String time = air.dt_txt;
        String weatherUrl = Config.WEATHER_IMAGE_URL + air.weather.get(0).icon + "@2x.png";

        holder.txtTemp.setText("" + air.main.temp + "°");
        Glide.with(context).load(weatherUrl).into(holder.forecastItemWeatherIcon);
        holder.txtWeather.setText(air.weather.get(0).description);

        try {
            // 시간 문자열을 LocalDateTime으로 파싱합니다.
            LocalDateTime localTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 글로벌 시간대 (예: UTC)를 사용하여 시간을 가져옵니다.
            ZonedDateTime globalTime = ZonedDateTime.of(localTime, ZoneOffset.UTC);

            // 서울 시간대로 시간을 변환합니다.
            ZonedDateTime seoulTime = globalTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

            // 서울 시간을 형식화하여 출력합니다.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String seoulTimeString = seoulTime.format(formatter);

            // 서울 시간을 TextView에 설정합니다.
            holder.txtTime.setText(seoulTimeString);

        } catch (DateTimeParseException e) {
            // 파싱 오류가 발생한 경우 처리합니다.
            e.printStackTrace(); // 또는 다른 방법으로 예외 처리를 수행합니다.
        }

    }

    @Override
    public int getItemCount() {
        return airArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtTemp, txtWeather;
        ImageView forecastItemWeatherIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTemp = itemView.findViewById(R.id.txtTemp);
            txtWeather = itemView.findViewById(R.id.txtWeather);
            forecastItemWeatherIcon = itemView.findViewById(R.id.forecastItemWeatherIcon);
        }
    }
}
