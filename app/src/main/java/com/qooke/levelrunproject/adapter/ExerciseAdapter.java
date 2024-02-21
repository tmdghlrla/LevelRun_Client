package com.qooke.levelrunproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qooke.levelrunproject.R;
import com.qooke.levelrunproject.model.Exercise;

import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    Context context;
    ArrayList<Exercise> exerciseArrayList;

    public ExerciseAdapter(Context context, ArrayList<Exercise> exerciseArrayList) {
        this.context = context;
        this.exerciseArrayList = exerciseArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.ViewHolder holder, int position) {
        Exercise exercise = exerciseArrayList.get(position);
        holder.txtDate.setText(exercise.createdAt);
        holder.txtSteps.setText("" + exercise.steps);
        holder.txtKcal.setText("" + exercise.kcal);

        int seconds = exercise.seconds;
        int hour = seconds / 3600;
        seconds = seconds - (hour*3600);
        int minutes = seconds / 60;

        String time = setConvert(seconds, minutes, hour);

        holder.txtTime.setText(time);
        holder.txtDistance.setText("" + exercise.distance);
    }

    @Override
    public int getItemCount() {
        return exerciseArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtSteps, txtKcal, txtTime, txtDistance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtSteps = itemView.findViewById(R.id.txtDistance);
            txtKcal = itemView.findViewById(R.id.txtKcal);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDistance = itemView.findViewById(R.id.txtDistance);
        }
    }

    private String setConvert(int seconds, int minutes, int hour) {
        String time;
        if(hour < 10) {
            if(minutes < 10) {
                time = "0" + hour + ":0" + minutes;
                return time;
            }
            time = "0" + hour + ":" + minutes;

            return time;
        } else {
            if(minutes < 10) {
                time = hour + ":0" + minutes;

                return time;
            }
            time = hour + ":" + minutes;

            return time;
        }
    }
}
