package com.appdev.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class FragmentRight extends Fragment {
    private static final String TAG = "DEBUG_FragmentRight";
    private static final String PREFS_NAME = "FragmentRightKey";
    private static final String TEXT_VIEW_X_KEY = "textView.setXX";
    private static final String TEXT_VIEW_Y_KEY = "textView.setYY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider_right, container, false);
        TextView textViewMonth = view.findViewById(R.id.textViewMonth);
        TextView textViewDate = view.findViewById(R.id.textViewDate);
        ProgressBar progressForMonth = view.findViewById(R.id.ProgressBarMonth);
        TextView percentageTextView = view.findViewById(R.id.textViewPercentage); // Text above progress bar

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH); // 0-based, so no +1
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Get the month name instead of the number
        String monthName = new DateFormatSymbols().getMonths()[month];

        textViewMonth.setText(monthName);
        textViewDate.setText(day + "."); // Append a dot after the day

        // Calculate progress percentage
        float progressPercent = ((float) day / daysInMonth) * 100;
        int progressForBar = Math.round(progressPercent);

        progressForMonth.setProgress(progressForBar);
        percentageTextView.setText(progressForBar + "%");

        // Adjust the position dynamically after layout
        progressForMonth.post(() -> adjustTextPosition(progressForBar, percentageTextView, progressForMonth));

        return view;
    }




    private void adjustTextPosition(int progress, TextView textView, ProgressBar progressBar) {
        int width = progressBar.getWidth();
        Log.d(TAG, "ProgressBar width: " + width);

        if (width == 0) {
            Log.e(TAG, "Width is 0, skipping position adjustment.");
            return;
        }

        // Scale progress for range 0-100
        int cappedProgress = Math.min(progress, 100);
        int progressWidth = (int) ((cappedProgress / 100.0) * width);

        Log.d(TAG, "Progress: " + progress);
        Log.d(TAG, "CappedProgress: " + cappedProgress);
        Log.d(TAG, "ProgressWidth: " + progressWidth);

        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Adjust X position based on progress thresholds
        if (progress >= 90) {
            textView.setX(progressBar.getX() + width - 185); // Stops at 90%
            Log.d(TAG, "-- progress >= 90 -- Locked X value.");
        } else if (progress >= 60) {
            textView.setX(progressBar.getX() + progressWidth - 185); // Left side of indicator
            Log.d(TAG, "-- progress >= 60 -- Text left of indicator.");
        } else {
            textView.setX(progressBar.getX() + progressWidth + 30); // Right side of indicator
            Log.d(TAG, "-- progress < 60 -- Text right of indicator.");
        }


        textView.setY(progressBar.getY() + (progressBar.getHeight() - textView.getHeight()) / 2);

        Log.d(TAG, "-- Set Y value: " + progressBar.getY() + ((progressBar.getHeight() - textView.getHeight()) / 2));
        Log.d(TAG,"progressBar height: " + progressBar.getHeight());
        Log.d(TAG,"textView height: " + textView.getHeight());
        Log.d(TAG,"progressBar Y: " + progressBar.getY());

        // Save position to preferences
        editor.putInt(TEXT_VIEW_X_KEY, (int) textView.getX());
        editor.putInt(TEXT_VIEW_Y_KEY, (int) textView.getY());
        editor.apply();
    }
}
