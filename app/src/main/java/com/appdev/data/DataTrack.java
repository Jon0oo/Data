package com.appdev.data;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.RemoteException;

public class DataTrack {

    private Context context;

    public DataTrack(Context context) {
        this.context = context;
    }

    public long getMobileDataUsage() {
        long totalBytes = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager =
                    (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);

            try {
                // Get mobile network stats
                NetworkStats networkStats = networkStatsManager.querySummary(
                        ConnectivityManager.TYPE_MOBILE,
                        "",
                        0,
                        System.currentTimeMillis());

                // Iterate through the stats to calculate total data usage
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                while (networkStats.hasNextBucket()) {
                    networkStats.getNextBucket(bucket);
                    totalBytes += bucket.getTxBytes() + bucket.getRxBytes(); // Transmit + Receive
                }
            } catch (SecurityException e) {
                // Handle exception (e.g., permission not granted)
                e.printStackTrace();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        return totalBytes; // Return total bytes used
    }
}
