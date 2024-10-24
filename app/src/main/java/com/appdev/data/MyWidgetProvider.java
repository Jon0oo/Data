package com.appdev.data;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.text.DecimalFormat;

public class MyWidgetProvider extends AppWidgetProvider {









    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);



        // Calculate value
        double calculatedValue = calculate.calculateDataAllowedDataUsedDifference(context);



        remoteViews.setTextViewText(R.id.wertBisHeuteBenutztErlaubt2, String.valueOf(calculatedValue));
    }








    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);


        // Create a RemoteViews object for your widget layout
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);


        calculate calculate = new calculate();

        //calculating value
        double calculatedValue = calculate.calculateDataAllowedDataUsedDifference(context);

        //formatting value + changing from Mb to Gb by multiplying by 0.001


        //setting the desired font size depending on the Widget size


        if(calculatedValue >=0){
            remoteViews.setInt(R.id.wertBisHeuteBenutztErlaubt2, "setTextColor",
                    android.graphics.Color.rgb(19,174,75));
        }
        else {
            remoteViews.setInt(R.id.wertBisHeuteBenutztErlaubt2, "setTextColor",
                    android.graphics.Color.rgb(191,64,72));
            calculatedValue = Math.abs(calculatedValue);
        }




        DecimalFormat df2 = new DecimalFormat("#.#");
        String finalDouble = df2.format(calculatedValue * 0.001);






        remoteViews.setTextViewText(R.id.wertBisHeuteBenutztErlaubt2, finalDouble);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);



        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);








    }

}


