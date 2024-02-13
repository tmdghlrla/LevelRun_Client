package com.qooke.levelrunproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qooke.levelrunproject.R;
import com.qooke.levelrunproject.model.WeatherRes;

import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        // todo: timezon 해결
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime nowSeoul = nowUtc.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

        WeatherRes.Air air = airArrayList.get(position);
        String time = air.dt_txt;

    }

    @Override
    public int getItemCount() {
        return airArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtTemp, txtWeather;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTemp = itemView.findViewById(R.id.txtTemp);
            txtWeather = itemView.findViewById(R.id.txtWeather);
        }
    }
}
