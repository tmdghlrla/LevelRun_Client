package com.qooke.levelrunproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class GpsTracker extends Service implements LocationListener {

    private final Context mContext;
    private Location location;
    private double latitude;
    private double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    private LocationManager locationManager;

    public GpsTracker(Context context) {
        this.mContext = context;
        initLocationListener();
        getLocation();
    }

    private void initLocationListener() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            // Check for location permissions
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        } catch (Exception e) {
            Log.d("@@@", "" + e.toString());
        }

        return location;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    @Override
    public void onLocationChanged(Location updatedLocation) {
        if (updatedLocation != null) {
            location = updatedLocation;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}