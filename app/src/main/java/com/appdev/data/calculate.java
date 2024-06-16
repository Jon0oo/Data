package com.appdev.data;


import java.util.Calendar;
import java.util.TimeZone;

public class  calculate {

    public static double calculateDataUsage(String wertMbProMonat) {

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
        int wertMbProMonatInt = 0;
        if (wertMbProMonat != null && wertMbProMonat.length() > 0) {

            wertMbProMonatInt = Integer.parseInt(wertMbProMonat);


        }

        double valueDataUsed = ((wertMbProMonatInt / DaysOfMonth) * currentDay *1000);
        return valueDataUsed;

    }






}
