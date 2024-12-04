package com.appdev.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.util.Log;

public class DataTrack {
    private static final String PREFS_NAME = "DataTrackingPrefs";
    private static final String STORED_DATA_KEY = "storedDataUsage"; // Total usage over time
    private static final String CURRENT_DATA_KEY = "currentDataUsage"; // Usage since last reboot
    private static final String TAG = "DEBUG_DataTrack";

    private Context context;

    public DataTrack(Context context) {
        this.context = context;
    }

    // Track data usage since last reboot
    public long getDataUsageSinceLastReboot() {
        long currentRxBytes = TrafficStats.getMobileRxBytes();
        long currentTxBytes = TrafficStats.getMobileTxBytes();
        Log.d(TAG,"current Rx Bytes: " + currentRxBytes + "   current Tx Bytes: " + currentTxBytes );
        return currentRxBytes + currentTxBytes;


    }

    // Store current usage in SharedPreferences
    public void storeCurrentUsage() {
        long currentUsage = getDataUsageSinceLastReboot();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putLong(CURRENT_DATA_KEY, currentUsage).apply();

    }

    // Handle data on reboot
    public void handleReboot() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long currentUsage = prefs.getLong(CURRENT_DATA_KEY, 0);
        long totalUsage = prefs.getLong(STORED_DATA_KEY, 0);

        // Add current usage to total usage
        totalUsage += currentUsage;

        // Store updated total usage and reset current usage
        prefs.edit()
                .putLong(STORED_DATA_KEY, totalUsage)
                .putLong(CURRENT_DATA_KEY, 0) // Reset current usage
                .commit();
    }


    public long getTotalDataUsage() {
        long currentUsageSinceLastReboot = getDataUsageSinceLastReboot();
        long storedUsage = getStoredDataUsage();
        Log.d(TAG,"Total Data usage: " + String.valueOf((long) ((currentUsageSinceLastReboot + storedUsage)  / (1024.0 * 1024.0))));
        return (long) ((currentUsageSinceLastReboot + storedUsage)  / (1024.0 * 1024.0));
    }
    private long getStoredDataUsage() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getLong(STORED_DATA_KEY, 0);
    }

}
