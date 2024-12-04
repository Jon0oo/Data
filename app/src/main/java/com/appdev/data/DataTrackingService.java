package com.appdev.data;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class DataTrackingService extends Service {

    private static final long CHECK_INTERVAL = 600000; // Check every 10 minutes
    private static final String TAG = "DEBUG_DataTrackingService";
    private static final String CHANNEL_ID = "DataTrackingServiceChannel";

    private CountDownTimer timer;
    private DataTrack dataTrack;

    @Override
    public void onCreate() {
        super.onCreate();
        dataTrack = new DataTrack(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Data tracking service got start command");
        startForeground(1, createNotification());
        startTracking();
        return START_STICKY;
    }

    private Notification createNotification() {
        // Determine if the device is in night mode
        boolean isNightMode = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;

        // Set the appropriate icon based on the mode
        int notificationIcon = isNightMode ? R.drawable.logo_notification_night : R.drawable.logo_notification_day;

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("DATA is running")
                .setSmallIcon(notificationIcon) // Use the appropriate icon
                .setOngoing(true) // Make the notification non-removable
                .build();
    }


    // used to check if the mobile data is enabled, to avoid tracking while off because of wrong values
    private boolean isMobileDataEnabled() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For Android Oreo and above
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            } else {
                // For Android Nougat and below
                NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                return mobileNetwork != null && mobileNetwork.isConnected();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking mobile data state", e);
            return false;
        }
    }




    private void startTracking() {
        timer = new CountDownTimer(Long.MAX_VALUE, CHECK_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isMobileDataEnabled()) {
                    // Store current data usage in SharedPreferences
                    dataTrack.storeCurrentUsage();
                    long currentUsage = dataTrack.getDataUsageSinceLastReboot();
                    Log.d(TAG, String.format("Current usage since last reboot: %.2f MB", currentUsage / (1024.0 * 1024.0)));
                } else {
                    Log.d(TAG, "Mobile data is disabled. Skipping data usage update.");
                }
            }

            @Override
            public void onFinish() {
                // This won't be called since we're using Long.MAX_VALUE
            }
        }.start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel(); // Cancel the timer to avoid leaks
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // We don't need to bind this service
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Data Tracking Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
