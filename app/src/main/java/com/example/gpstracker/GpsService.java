package com.example.gpstracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GpsService extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "GpsService";
    private LocationListener locationListener;
    private LocationManager locationManager;

    // in ms
    private final int LOCATION_INTERVAL = 30 * 60 * 1000;
    private final int LOCATION_DISTANCE = 0;

    //@androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class LocationListener implements android.location.LocationListener
    {
        private final String TAG = "LocationListener";
        private Location lastLocation;

        public LocationListener(String provider)
        {
            lastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            lastLocation = location;
            Log.i(TAG, "LocationChanged: "+location);

            // HTTP call
            HttpPostTask poster = new HttpPostTask(null, lastLocation);
            poster.execute();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + status);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        startForeground(12345678, getNotification());
        startTracking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listeners, ignore", ex);
            }
        }
    }

    private void initLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void startTracking() {
        initLocationManager();
        locationListener = new LocationListener(LocationManager.GPS_PROVIDER);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListener);

        } catch (java.lang.SecurityException ex) {
             Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
             Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    public void stopTracking() {
        this.onDestroy();
    }

    private Notification getNotification() {
        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01");

        return builder.build();
    }

    public class LocationServiceBinder extends Binder {
        public GpsService getService() {
            return GpsService.this;
        }
    }
}
