package com.appdev.data;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

public class  calculate {

    Context context;
    public static final String TAG = "DEBUG_calculate";
    public static double calculateDataUsageAllowed(String wertMbProMonat) {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        double DaysOfMonth = 0;
        switch (currentMonth) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                DaysOfMonth = 31;
                break;
            case 1:
                DaysOfMonth = 28;
                break;
            case 3:
            case 10:
            case 5:
            case 8:
                DaysOfMonth = 30;
                break;
        }
        double wertMbProMonatInt = 0;
        if (wertMbProMonat != null && wertMbProMonat.length() > 0) {

            wertMbProMonatInt = Double.parseDouble(wertMbProMonat);


        }

        double valueDataUsed = ((wertMbProMonatInt / DaysOfMonth) * currentDay *1000);
//        Log.d("DEBUG_ALLOWED", String.valueOf(valueDataUsed));
//        Log.d("DEBUG_ALLOWED", String.valueOf(wertMbProMonatInt));
//        Log.d("DEBUG_ALLOWED", String.valueOf(DaysOfMonth));
//        Log.d("DEBUG_ALLOWED", String.valueOf(currentDay));
        return valueDataUsed;

    }
    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1; // MONTH is zero-based (0 = January)
    }
    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static void storeCurrentMonthAndYear(Context context) {
        SharedPreferences prefsForMonthYear = context.getSharedPreferences("calculate", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsForMonthYear.edit();

        // Check if the month and year are already stored
        if (!prefsForMonthYear.contains("installedMonth") && !prefsForMonthYear.contains("installedYear")) {
            int currentMonth = getCurrentMonth();
            int currentYear = getCurrentYear();
            editor.putInt("installedMonth", currentMonth);
            editor.putInt("installedYear", currentYear);
            Log.d(TAG, String.valueOf("current Month got stored as: " + currentMonth));
            Log.d(TAG, String.valueOf("current Year got stored as: " + currentYear));
            editor.apply();
        }
    }


    public static double calculateDataUsageLeft(Context context) {





        DataTrack dataTrack = new DataTrack(context);








        float calculatedRemainingData = 0;

        //getting the user entered data limit per month
        SharedPreferences prefsForMonthYear = context.getSharedPreferences("calculate", MODE_PRIVATE);
        float storingUserDataLimit = prefsForMonthYear.getFloat("wertMbProMonat", 0) * 1000;
        Log.d(TAG, "Data limit per month: " + storingUserDataLimit);

        //getting the usage value before the first install
        SharedPreferences prefsForTracking = context.getSharedPreferences("DataTrackingPrefs", MODE_PRIVATE);
        float calculatedValueBeforeTracking = prefsForTracking.getFloat("totalDataUsageBeforeTracking", 0) ;
        Log.d(TAG, String.valueOf("Calculated value of data usage before tracking started: " +calculatedValueBeforeTracking));

        // getting the total data usage
        float totalDataUsage = dataTrack.getTotalDataUsage();
        Log.d(TAG, String.valueOf("Total Data usage from tracking: " + totalDataUsage));

        //checking if the calc should be done for install month
        if (getCurrentMonth() == prefsForMonthYear.getInt("installedMonth", 0) && getCurrentYear() == prefsForMonthYear.getInt("installedYear", 0)){




            calculatedRemainingData = storingUserDataLimit - calculatedValueBeforeTracking - totalDataUsage;
            Log.d(TAG, "calculated remaining data: " + String.valueOf(calculatedRemainingData));

        }
        else {
            //when month and year do not match

            calculatedRemainingData = storingUserDataLimit - totalDataUsage;

        }
        return calculatedRemainingData;
    }


    public static double calculateDataAllowedDataUsedDifference(Context context) {
        // Getting the user-entered data limit per month
        SharedPreferences prefsForMonthYear = context.getSharedPreferences("calculate", MODE_PRIVATE);
        float storingUserDataLimit = prefsForMonthYear.getFloat("wertMbProMonat", 0) * 1000;
        Log.d(TAG, "Data limit per month: " + storingUserDataLimit);

        // Calculate the allowed data usage
        double dataUsageLeft = calculateDataUsageLeft(context);
        Log.d(TAG, "dataUsageLeft: " + dataUsageLeft);
        double alreadyUsed = storingUserDataLimit - dataUsageLeft;
        double alreadyAllowed = calculateDataUsageAllowed(String.valueOf(storingUserDataLimit ))  *0.001;

        double calculateDataAllowedDataUsedDifferenceVar =  alreadyAllowed - alreadyUsed;


        Log.d(TAG, "The calculated allowed data usage: " + alreadyAllowed);
        Log.d(TAG, "Minus the stored user data limit: " + alreadyUsed);
//        Log.d(TAG, "Minus this too: " + calculateDataUsageLeft(context));
        Log.d(TAG, "Equals: " + calculateDataAllowedDataUsedDifferenceVar);
        return calculateDataAllowedDataUsedDifferenceVar;
    }

}
