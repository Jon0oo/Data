package com.appdev.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

public class calculate {

    static Context context;
    public static final String TAG = "DEBUG_Calculate";

    public static double calculateDataUsageAllowed(String wertMbProMonat) {
        if (wertMbProMonat == null || wertMbProMonat.isEmpty()) {
            Log.e(TAG, "Error: wertMbProMonat is null or empty.");
            return 0;
        }

        double wertMbProMonatInt;
        try {
            wertMbProMonatInt = Double.parseDouble(wertMbProMonat);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid number format for wertMbProMonat: " + wertMbProMonat, e);
            return 0;
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH); // 0-based (Jan = 0, Dec = 11)

        double daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // Handles leap years correctly

        if (daysOfMonth == 0) {
            Log.e(TAG, "Error: Days in month is 0, calculation aborted.");
            return 0;
        }

        return (wertMbProMonatInt / daysOfMonth) * currentDay * 1000;
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1; // Convert to 1-based (Jan = 1, Dec = 12)
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static void storeCurrentMonthAndYear(Context context) {
        SharedPreferences prefsForMonthYear = context.getSharedPreferences("calculate", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsForMonthYear.edit();

        if (!prefsForMonthYear.contains("installedMonth") || !prefsForMonthYear.contains("installedYear")) {
            int currentMonth = getCurrentMonth();
            int currentYear = getCurrentYear();
            editor.putInt("installedMonth", currentMonth);
            editor.putInt("installedYear", currentYear);
            editor.apply();
            Log.d(TAG, "Stored current Month: " + currentMonth + ", Year: " + currentYear);
        }
    }

    public static double calculateDataUsageLeft(Context context) {
        DataTrack dataTrack = new DataTrack(context);
        SharedPreferences prefsForMonthYear = context.getSharedPreferences("calculate", MODE_PRIVATE);
        SharedPreferences prefsForTracking = context.getSharedPreferences("DataTrackingPrefs", MODE_PRIVATE);
        SharedPreferences sharedPrefs = context.getSharedPreferences("UnlimitedFlag", Context.MODE_PRIVATE);

        // Check if unlimited flag is "on"
        boolean isUnlimited = sharedPrefs.getString("UnlimitedFlagValue2", "off").equals("off");

        // Get user-defined data limit
        float storingUserDataLimit = prefsForMonthYear.getFloat("wertMbProMonat", 0) * 1000;
        float calculatedValueBeforeTracking = prefsForTracking.getFloat("totalDataUsageBeforeTracking", 0);
        float totalDataUsage = dataTrack.getTotalDataUsage();

        if (totalDataUsage < 0) {
            Log.e(TAG, "Error: totalDataUsage is negative. Resetting to 0.");
            totalDataUsage = 0;
        }



        // Normal calculation (when unlimited mode is OFF)
        int installedMonth = prefsForMonthYear.getInt("installedMonth", 0);
        int installedYear = prefsForMonthYear.getInt("installedYear", 0);

        if (getCurrentMonth() == installedMonth && getCurrentYear() == installedYear) {
            return storingUserDataLimit - calculatedValueBeforeTracking - totalDataUsage;
        } else {
            return storingUserDataLimit - totalDataUsage;
        }
    }


    public static double calculateDataAllowedDataUsedDifference(Context context) {

        SharedPreferences prefsForMonthYear = context.getSharedPreferences("calculate", MODE_PRIVATE);
        float storingUserDataLimit = prefsForMonthYear.getFloat("wertMbProMonat", 0) * 1000;

        double dataUsageLeft = calculateDataUsageLeft(context);
        double alreadyUsed = storingUserDataLimit - dataUsageLeft;
        double alreadyAllowed = calculateDataUsageAllowed(String.valueOf(storingUserDataLimit)) / 1000; // Convert to MB

        double difference = alreadyAllowed - alreadyUsed;

        Log.d(TAG, "Allowed Data Usage: " + alreadyAllowed);
        Log.d(TAG, "Used Data: " + alreadyUsed);
        Log.d(TAG, "Difference: " + difference);


        SharedPreferences sharedPrefs = context.getSharedPreferences("UnlimitedFlag", Context.MODE_PRIVATE);

        // Check if unlimited flag is "on"
        String isUnlimitedString = sharedPrefs.getString("UnlimitedFlagValue2", "off");


        Log.d(TAG,"isUnlimitedString is: " + isUnlimitedString);

        if (isUnlimitedString == "on") {
            Log.d(TAG,"returned from if clause:" + alreadyUsed);
            return alreadyUsed;
        }


        else {
            Log.d(TAG,"returned from else clause:" + difference);
            return difference;
        }
    }
}
