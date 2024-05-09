package com.appdev.data;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsetsAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.window.SplashScreenView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

            @Override
            protected void onCreate(@Nullable Bundle savedInstanceState) {
                Objects.requireNonNull(getSupportActionBar()).hide();
                // hides the bar at the top





                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);










        TextView WertAllowedDataUsage = (TextView) findViewById(R.id.wertBisHeuteBenutztErlaubt);
        EditText MbProMonat = (EditText) findViewById(R.id.WertMbProMonat);
// introduce .xml objects to java

        WertAllowedDataUsage.setText("0");

// starts the app with zero data usage left


        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String wertMbProMonatStartup = sharedPref.getString("wertMbProMonat", "");
        MbProMonat.setText(wertMbProMonatStartup);


//introduces shared preference early to set the wertMbProMonat value at startup



        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
// get current year month and day









        SharedPreferences.Editor editor = sharedPref.edit();


        final String[] wertMbProMonat = {"0"};
        MbProMonat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null && s.length() > 0) {
                    // The string is empty



                }





            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s != null && s.length() > 0) {
                    // The string is empty

                wertMbProMonat[0] = s.toString();


                editor.putString("wertMbProMonat", s.toString());
                editor.apply();

            }



            }

            @Override
            public void afterTextChanged(Editable s) {

                calculate Calculate = new calculate();
                double calculatedValue = calculate.calculateDataUsage(sharedPref.getString("wertMbProMonat", null));

                DecimalFormat df = new DecimalFormat("#");
                String calculatedValueDecimalFormat = df.format(calculatedValue);
                WertAllowedDataUsage.setText(calculatedValueDecimalFormat);




                //calculate Calculate = new calculate();
                //double calculatedValue = calculate.calculateDataUsage(sharedPref.getString("wertMbProMonat", null));
                //WertAllowedDataUsage.setText((calculatedValue) + "Mb");


            }
        });

// creates a persistent variable with SharedPreferences to store the Value the user entered at Mb per month



        calculate Calculate = new calculate();
        double calculatedValue = calculate.calculateDataUsage(sharedPref.getString("wertMbProMonat", null));

        DecimalFormat df = new DecimalFormat("#");
        String calculatedValueDecimalFormat = df.format(calculatedValue);
        WertAllowedDataUsage.setText(calculatedValueDecimalFormat);

// uses calculateDataUsage method, stores the value in formatting without decimals at the end to the allowed data use box








    }
    }