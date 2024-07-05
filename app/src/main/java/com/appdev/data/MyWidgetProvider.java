package com.appdev.data;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.appdev.data.R;

import java.text.DecimalFormat;

public class MyWidgetProvider extends AppWidgetProvider {









    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);



        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        //introduce Shared Preferenecs instance to get the users "data per month" value
        SharedPreferences sharedPref = context.getSharedPreferences("widgetKey", Context.MODE_PRIVATE);
        String sharedPrefValue = sharedPref.getString("widgetKey", "0");

        //calls calculate function to calculate the ""data allowed" value locally in the widget to avoid having to open the app for the corrent value to be displayed
        calculate Calculate = new calculate();

        //calculating value
        double calculatedValue = calculate.calculateDataUsage(sharedPrefValue);

        //formatting value + changing from Mb to Gb by multiplying by 0.001
        DecimalFormat df2 = new DecimalFormat("#.#");
        String finalDouble = df2.format(calculatedValue * 0.001);



        remoteViews.setTextViewText(R.id.wertBisHeuteBenutztErlaubt2, finalDouble);

    }







    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);


        // Create a RemoteViews object for your widget layout
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        //introduce Shared Preferenecs instance to get the users "data per month" value
        SharedPreferences sharedPref = context.getSharedPreferences("widgetKey", Context.MODE_PRIVATE);
        String sharedPrefValue = sharedPref.getString("widgetKey", "0");

        //calls calculate function to calculate the ""data allowed" value locally in the widget to avoid having to open the app for the corrent value to be displayed
        calculate Calculate = new calculate();

        //calculating value
        double calculatedValue = calculate.calculateDataUsage(sharedPrefValue);

        //formatting value + changing from Mb to Gb by multiplying by 0.001
        DecimalFormat df2 = new DecimalFormat("#.#");
        String finalDouble = df2.format(calculatedValue * 0.001);

        //setting the desired font size depending on the Widget size











        remoteViews.setTextViewText(R.id.wertBisHeuteBenutztErlaubt2, finalDouble);

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);






    }

}


