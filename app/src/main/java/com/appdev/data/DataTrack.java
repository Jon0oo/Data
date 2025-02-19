package com.appdev.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class DataTrack {
    private static final String PREFS_NAME = "DataTrackingPrefs";
    // Baseline data usage entered on first install (from Settings app)
    private static final String STORED_DATA_KEY = "storedDataUsage";
    // Usage measured from TrafficStats since the last reboot (mobile data only)
    private static final String CURRENT_DATA_KEY = "currentDataUsage";
    // Offset used to mark the beginning of the current month (so that previous month’s usage is not counted)
    private static final String MONTHLY_OFFSET_KEY = "monthlyOffset";
    // The month (0-based, per Calendar.MONTH) in which the baseline/offset was last set
    private static final String LAST_MONTH_KEY = "lastMonth";
    private static final String TAG = "DEBUG_DataTrack";

    private Context context;

    public DataTrack(Context context) {
        this.context = context;
    }

    /**
     * Initializes the baseline usage for the current month.
     * This should be called once (for example, during first install) when the user enters their current
     * data usage from the Settings app. This baseline is used to compute cumulative usage.
     *
     * @param baseline The baseline data usage (in bytes) entered by the user.
     */
    public void initializeBaseline(long baseline) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Only initialize if no baseline is already set
        if (prefs.getLong(STORED_DATA_KEY, 0) == 0) {
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(STORED_DATA_KEY, baseline)
                    .putInt(LAST_MONTH_KEY, currentMonth)
                    .apply();

            // Set the monthly offset as the current cumulative usage (baseline + usage since reboot)
            long initialTotalUsage = getDataUsageSinceLastReboot() + baseline;
            prefs.edit().putLong(MONTHLY_OFFSET_KEY, initialTotalUsage).apply();
            Log.d(TAG, "Baseline initialized: baseline=" + baseline +
                    ", monthly offset=" + initialTotalUsage +
                    ", month=" + currentMonth);
        } else {
            Log.d(TAG, "Baseline already initialized.");
        }
    }

    /**
     * Checks whether the device is connected via mobile data.
     * Uses a modern API (for API 23+) and falls back to the older API for pre-23 devices.
     *
     * @return true if mobile data is connected; false otherwise.
     */
    private boolean isMobileDataConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                    return capabilities != null &&
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
                return false;
            } else {
                // Deprecated in API 29, but still works for older devices
                NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                return mobileInfo != null && mobileInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * Returns the mobile data usage since the last reboot.
     * If mobile data is not connected, returns the last stored usage value.
     *
     * @return Mobile data usage (in bytes) since the last reboot.
     */
    public long getDataUsageSinceLastReboot() {
        if (!isMobileDataConnected()) {
            Log.d(TAG, "Mobile data not connected; returning stored current usage.");
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            return prefs.getLong(CURRENT_DATA_KEY, 0);
        }
        long rxBytes = TrafficStats.getMobileRxBytes();
        long txBytes = TrafficStats.getMobileTxBytes();
        long currentUsage = rxBytes + txBytes;
        Log.d(TAG, "Mobile data usage since reboot: Rx=" + rxBytes + " bytes, Tx=" + txBytes + " bytes");
        return currentUsage;
    }

    /**
     * Stores the current mobile data usage (since reboot) in SharedPreferences.
     * Updates only if mobile data is currently connected.
     */
    public void storeCurrentUsage() {
        if (!isMobileDataConnected()) {
            Log.d(TAG, "Mobile data not connected; skipping update of current usage.");
            return;
        }
        long currentUsage = getDataUsageSinceLastReboot();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putLong(CURRENT_DATA_KEY, currentUsage).apply();
        Log.d(TAG, "Stored current usage: " + currentUsage + " bytes");
    }

    /**
     * Handles the actions to take on device reboot.
     * This method should be called by a BootReceiver. It adds the data usage (recorded prior to reboot)
     * into the monthly offset and then resets the CURRENT_DATA_KEY.
     */
    public void handleReboot() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long usageBeforeReboot = prefs.getLong(CURRENT_DATA_KEY, 0);
        long currentOffset = prefs.getLong(MONTHLY_OFFSET_KEY, 0);

        // Add the usage before reboot to the monthly offset, then reset CURRENT_DATA_KEY.
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(MONTHLY_OFFSET_KEY, currentOffset + usageBeforeReboot)
                .putLong(CURRENT_DATA_KEY, 0)
                .apply();

        Log.d(TAG, "Reboot handled: added " + usageBeforeReboot +
                " bytes to monthly offset (new offset=" + (currentOffset + usageBeforeReboot) + ").");
    }

    /**
     * Handles monthly reset logic. This should be called before calculating the current month's usage.
     * If a new month is detected (or on first run), it resets the monthly offset so that data usage
     * from previous months is excluded.
     */
    private void handleMonthlyReset() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int storedMonth = prefs.getInt(LAST_MONTH_KEY, -1);

        long cumulativeUsage = getDataUsageSinceLastReboot() + getStoredDataUsage();
        if (storedMonth == -1 || storedMonth != currentMonth) {
            // New month detected or first-time run: update the month and reset monthly offset.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(LAST_MONTH_KEY, currentMonth)
                    .putLong(MONTHLY_OFFSET_KEY, cumulativeUsage)
                    .apply();
            Log.d(TAG, "Monthly reset: New month (" + currentMonth + ") detected. " +
                    "Monthly offset set to cumulative usage (" + cumulativeUsage + " bytes).");
        }
    }

    /**
     * Returns the overall total data usage (baseline + data since reboot) in megabytes.
     * Note: Uses integer division for MB.
     *
     * @return Total data usage in MB.
     */
    public long getTotalDataUsage() {
        long usageSinceReboot = getDataUsageSinceLastReboot();
        long baseline = getStoredDataUsage();
        long totalUsageBytes = usageSinceReboot + baseline;
        Log.d(TAG, "Total usage: " + totalUsageBytes + " bytes ("
                + (totalUsageBytes / (1024.0 * 1024.0)) + " MB)");
        return totalUsageBytes / (1024 * 1024); // MB (integer value)
    }

    /**
     * Returns the current month's data usage in megabytes.
     * This is calculated by subtracting the monthly offset (the cumulative usage at the month’s start)
     * from the current cumulative usage.
     *
     * @return Current month's data usage in MB.
     */
    public long getDataUsageForCurrentMonth() {
        // Ensure the monthly offset is up to date
        handleMonthlyReset();

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long currentCumulativeUsage = getDataUsageSinceLastReboot() + getStoredDataUsage();
        long monthlyOffset = prefs.getLong(MONTHLY_OFFSET_KEY, 0);
        long usageThisMonth = currentCumulativeUsage - monthlyOffset;
        Log.d(TAG, "Current month usage: " + usageThisMonth + " bytes ("
                + (usageThisMonth / (1024.0 * 1024.0)) + " MB)");
        return usageThisMonth / (1024 * 1024); // MB (integer value)
    }

    /**
     * Retrieves the stored baseline data usage (user-entered value) from SharedPreferences.
     *
     * @return The baseline usage (in bytes).
     */
    private long getStoredDataUsage() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long storedBaseline = prefs.getLong(STORED_DATA_KEY, 0);
        Log.d(TAG, "Stored baseline usage: " + storedBaseline + " bytes");
        return storedBaseline;
    }
}
