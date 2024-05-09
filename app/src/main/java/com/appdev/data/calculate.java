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
                DaysOfMonth = 31;
                break;
            case 1:
                DaysOfMonth = 28;
                break;
            case 2:
                DaysOfMonth = 31;
                break;
            case 3:
                DaysOfMonth = 30;
                break;
            case 4:
                DaysOfMonth = 31;
                break;
            case 5:
                DaysOfMonth = 30;
                break;
            case 6:
                DaysOfMonth = 31;
                break;
            case 7:
                DaysOfMonth = 31;
                break;
            case 8:
                DaysOfMonth = 30;
                break;
            case 9:
                DaysOfMonth = 31;
                break;
            case 10:
                DaysOfMonth = 30;
                break;
            case 11:
                DaysOfMonth = 31;
                break;
        }
        int wertMbProMonatInt = 0;
        if (wertMbProMonat != null && wertMbProMonat.length() > 0) {

            wertMbProMonatInt = Integer.parseInt(wertMbProMonat);


        }

        double valueDataUsed = ((wertMbProMonatInt / DaysOfMonth) * currentDay);
        return valueDataUsed;

    }






}
