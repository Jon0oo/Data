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
import androidx.lifecycle.ViewModelProvider;

public class FragmentLeft extends Fragment {

    private SharedViewModel2 viewModel;
    private static final String TAG = "DEBUG_FragmentLeft";
    private static final String PREFS_NAME = "FragmentLeftKey";
    private static final String PROGRESS_INT_KEY = "ProgressInt";
    private static final String TEXT_VIEW_X_KEY = "textView.setXX";
    private static final String TEXT_VIEW_Y_KEY = "textView.setYY";
    //TODO update progressbar if unlimited mode changes
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider_left, container, false);
        ProgressBar progressBar = view.findViewById(R.id.ProgressBarData);
        TextView usageTextView = view.findViewById(R.id.usageTextView);
        ConstraintLayout frameLayoutTop2 = view.findViewById(R.id.FrameLayoutTop2);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel2.class);
        usageTextView.setText("0%"); // Default text

        final boolean[] isInitialized = {false};

        viewModel.getAnswer(1).observe(getViewLifecycleOwner(), calcValueFromViewModel -> {
            SharedPreferences sharedPrefs = getActivity().getSharedPreferences("UnlimitedFlag", Context.MODE_PRIVATE);
            String isFlagOn = sharedPrefs.getString("UnlimitedFlagValue2","off");
              if (!isInitialized[0]) {
                    isInitialized[0] = true;
                    return; // Skip the first update
              }
              if (calcValueFromViewModel != null) {
                   updateProgress(isFlagOn, calcValueFromViewModel, progressBar, usageTextView, frameLayoutTop2);
               } else {
                   Log.d(TAG, "answer2 is empty");
               }



        });



        viewModel.getAnswer(2).observe(getViewLifecycleOwner(), isFlagOn -> {




            String calcValueFromViewModelFromFlag = "0";
            updateProgress(isFlagOn,calcValueFromViewModelFromFlag, progressBar, usageTextView, frameLayoutTop2);


        });




        loadInitialProgress(progressBar, usageTextView, frameLayoutTop2);
        return view;
    }

    private void updateProgress(String isFlagOn, String calcValueFromViewModel, ProgressBar progressBar, TextView usageTextView, ConstraintLayout frameLayoutTop2) {
        int calcValue = parseAnswer(calcValueFromViewModel);
        if (calcValue == Integer.MIN_VALUE) return; // Error in parsing


        if (isFlagOn.equals("on")){
            calcValue = 0;
        }

            int absoluteValue = Math.abs(calcValue);
            progressBar.setProgress(absoluteValue);
            updateProgressBarColor(calcValue, progressBar, frameLayoutTop2);
            usageTextView.setText(absoluteValue + "%");
            Log.d(TAG, "text on textView: " + (absoluteValue + "%"));

            saveProgressToPreferences(calcValue);
            adjustTextPosition(absoluteValue, usageTextView, progressBar);
        }


    private void loadInitialProgress(ProgressBar progressBar, TextView usageTextView, ConstraintLayout frameLayoutTop2) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int calcValue = sharedPref.getInt(PROGRESS_INT_KEY, 0);
        int absoluteValue = Math.abs(calcValue);

        progressBar.setProgress(absoluteValue);
        updateProgressBarColor(calcValue, progressBar, frameLayoutTop2);
        usageTextView.setText(absoluteValue + "%");
        Log.d(TAG, "ProgressInt: " + (sharedPref.getInt(PROGRESS_INT_KEY, 0)));

        // Restore text position from preferences
        usageTextView.setX(sharedPref.getInt(TEXT_VIEW_X_KEY, 0));
        usageTextView.setY(sharedPref.getInt(TEXT_VIEW_Y_KEY, 0));
    }

    private int parseAnswer(String answer2) {
        try {
            answer2 = answer2.replace(",", "."); // Replace comma with dot
            return Math.round(Float.parseFloat(answer2));
        } catch (NumberFormatException | NullPointerException e) {
            Log.e(TAG, "Error parsing answer2: " + e.getMessage());
            return Integer.MIN_VALUE; // Indicate parsing error
        }
    }

    private void updateProgressBarColor(int calcValue, ProgressBar progressBar, ConstraintLayout frameLayoutTop2) {
        if (calcValue < 0) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(191, 64, 72)));
            frameLayoutTop2.setBackgroundResource(R.drawable.rounded_corners_red);
        } else {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(19, 174, 75)));
            frameLayoutTop2.setBackgroundResource(R.drawable.rounded_corners_green);
        }
    }

    private void saveProgressToPreferences(int calcValue) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(PROGRESS_INT_KEY, calcValue);
        editor.apply();
    }

    private void adjustTextPosition(int progress, TextView textView, ProgressBar progressBar) {
        int width = progressBar.getWidth();
        Log.d(TAG, "width: " + width);

        int cappedProgress = Math.min(progress, 20);
        int progressWidth = (int) ((cappedProgress / 20.0) * width);

        Log.d(TAG, "progress: " + progress);
        Log.d(TAG, "cappedProgress: " + cappedProgress);
        Log.d(TAG, "progressWidth: " + progressWidth);

        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Adjust X position based on progress thresholds
        if (progress >= 1000) {
            textView.setX((progressBar.getX() + progressWidth - 230)); // Adjust this value as needed
            Log.d(TAG, "-- progress > 1000 -- set X value: " + (progressBar.getX() + progressWidth - 300));
        } else if (progress >= 100) {
            textView.setX((progressBar.getX() + progressWidth - 200)); // Adjust this value as needed
            Log.d(TAG, "-- progress > 100 -- set X value: " + (progressBar.getX() + progressWidth - 200));
        } else if (cappedProgress > 10) {
            textView.setX((progressBar.getX() + progressWidth - 170));
            Log.d(TAG, "-- bigger10 -- set X value: " + (progressBar.getX() + progressWidth - 170));
        } else {
            textView.setX((progressBar.getX() + progressWidth + 70));
            Log.d(TAG, "-- smaller10 -- set X value: " + (progressBar.getX() + progressWidth + 70));
        }

        textView.setY(progressBar.getY() + (progressBar.getHeight() - textView.getHeight()) / 2);
        Log.d(TAG, "-- set Y value: " + (progressBar.getY() + (progressBar.getHeight() - textView.getHeight()) / 2));

        editor.putInt(TEXT_VIEW_X_KEY, (int) textView.getX());
        editor.putInt(TEXT_VIEW_Y_KEY, (int) textView.getY());
        editor.apply();
    }

}
