package com.appdev.data;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {


    //Tag for logging
    private static final String TAG = "DEBUG_MainActivityDebug";


    //for testing the update frequency of the data usage listener


    private PermissionHandler permissionHandler;



    @Override
            protected void onCreate( Bundle savedInstanceState) {

        permissionHandler = new PermissionHandler(this);
        permissionHandler.requestPermissionsSequentially();




        //stops the UI from going upwards if keyboard is opened
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        // hides the bar at the top
        Objects.requireNonNull(getSupportActionBar()).hide();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText MbProMonat;
        MbProMonat = (EditText) findViewById(R.id.WertMbProMonat);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String wertMbProMonatStartup = sharedPref.getString("wertMbProMonat", "0");
        MbProMonat.setText(wertMbProMonatStartup);
        Log.d(TAG, wertMbProMonatStartup);
        SharedPreferences.Editor editor = sharedPref.edit();
        Activity activity = this;

        SharedViewModel2 viewModel = new ViewModelProvider(this).get(SharedViewModel2.class);
        // introducing the on click listener for the three dots
        ImageView threeDots = findViewById(R.id.threeDots);
        threeDots.setOnClickListener(new View.OnClickListener() {

            OverlayHelper overlayHelper = new OverlayHelper(activity, viewModel);
             @Override
             public void onClick(View view) {
                 SharedPreferences sharedPrefsForExplain = getSharedPreferences("isExplaining", Context.MODE_PRIVATE);
                 Log.d(TAG,"isExplaining is: " + sharedPrefsForExplain.getBoolean("isExplaining", false));

                 if(!sharedPrefsForExplain.getBoolean("isExplaining", false)){

                    overlayHelper.showPopupWindow(view);

                        }
                   }
             });


        //storing the value in a seperate Shared Pref to get it to the calculate class
        SharedPreferences sharedPref3 = getSharedPreferences("calculate", MODE_PRIVATE);
        SharedPreferences.Editor editorForCalculate = sharedPref3.edit();

        if (wertMbProMonatStartup != null) {
            editorForCalculate.putFloat("wertMbProMonat", Float.parseFloat(wertMbProMonatStartup));
            editorForCalculate.apply();
        } else {
            // Handle the case when wertMbProMonatStartup is null
            Log.d(TAG, "wertMbProMonatStartup is null, not storing in shared preferences.");
        }

        calculate.storeCurrentMonthAndYear(this);


//----------------------------------testing--------------------------------------//







        //getting the calculated value from the calculate class

        Context context = this;
        calculate calculateTest = new calculate();
        double calculatedValueTest = calculateTest.calculateDataAllowedDataUsedDifference(context);

        Log.d(TAG, String.valueOf("Received this value from calculate method: " + calculatedValueTest));

        calculateTest.calculateDataAllowedDataUsedDifference(context);


//----------------------------------------testing-----------------------------------------//


        //Initialize the ViewModel
        SharedViewModel mainActivityViewModel = new ViewModelProvider(this).get(SharedViewModel.class);


        //Initialize the Pager Adapter to setup the Slider Pages
        ViewPager2 viewPager = findViewById(R.id.ViewPagerSlider);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new FragmentLeft());
        fragmentList.add(new FragmentMiddle());
        fragmentList.add(new FragmentRight());
        PagerAdapter2 adapter = new PagerAdapter2(this, fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);


        // introduce .xml objects to java



        //setting Answer of ViewModel to force update fragment middle at startup to display the right text
        SharedPreferences sharedPrefsForFragmentUpdate = getSharedPreferences("UnlimitedFlag",MODE_PRIVATE);
        SharedViewModel3.setAnswer(1, sharedPrefsForFragmentUpdate.getString("UnlimitedFlagValue2", "off"));
        calculate.calculateDataAllowedDataUsedDifference(context);



        double calculatedValueForFrag = calculate.calculateDataAllowedDataUsedDifference(context);
        DecimalFormat dfForFrag = new DecimalFormat("#.##");
        String calculatedValueDecimalFormatForFrag = dfForFrag.format(calculatedValueForFrag * 0.001);
        SharedViewModel.setAnswer(1, calculatedValueDecimalFormatForFrag);













        // starting the tracking service for mobile data
        Intent serviceIntent = new Intent(this, DataTrackingService.class);
        startService(serviceIntent);
        BroadcastReceiver dataUsageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intentTracking) {
                double currentUsage = intentTracking.getDoubleExtra("currentUsage", 0);
                double totalUsage = intentTracking.getDoubleExtra("totalUsage", 0);
                Log.d(TAG, "Service was called from MainActivity");
                Log.d(TAG, String.valueOf(totalUsage));


                //----------testing-----------------//


            }
        };




        SharedPreferences prefs = getSharedPreferences("FirstStartPrefs", MODE_PRIVATE);


        Button buttonDebug = (Button) findViewById(R.id.button2);
        buttonDebug.setOnClickListener(v -> {
            Log.d(TAG,"Debug button pressed!");
            prefs.edit().putBoolean("firstStart", true).apply();
        });

        SharedPreferences sharedPrefForExplain = context.getSharedPreferences("UnlimitedFlag", Context.MODE_PRIVATE);
        boolean isFirstStartupExplanationAborted = sharedPrefForExplain.getBoolean("isFirstStartupExplanationAborted",true);
        boolean isFirstStart = prefs.getBoolean("firstStart", true);
        Log.d(TAG,"isFirstStart: "+ isFirstStart + "isFristStartupExplanationAborted: "+ isFirstStartupExplanationAborted);
        if (isFirstStart || isFirstStartupExplanationAborted) {
            OverlayHelper overlayHelper = new OverlayHelper(this, viewModel);
            Boolean isFirstStartup = true;
            overlayHelper.showExplanation(isFirstStartup);
            prefs.edit().putBoolean("firstStart", false).apply();
        }





        //introduces shared preference early to set the wertMbProMonat value at startup


        // get current year month and day
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);


        //initialize TextWatcher to check if the user updated values. Calculation as consequence

        final String[] wertMbProMonat = {"0"};
        EditText finalMbProMonat = MbProMonat;
        SharedViewModel2 SharedViewModel2 = new ViewModelProvider(this).get(SharedViewModel2.class);
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


                    try {
                        editor.putString("wertMbProMonat", s.toString());

                        editor.apply();
                    } catch (NumberFormatException e) {
                        Log.d(TAG, "Invalid input for wertMbProMonat: " + s.toString());
                    }


                    try {
                        SharedPreferences.Editor editorCalc = getSharedPreferences("calculate", Context.MODE_PRIVATE).edit();
                        editorCalc.putFloat("wertMbProMonat", Float.parseFloat(s.toString()));
                        editorCalc.apply();
                    } catch (NumberFormatException e) {
                        Log.d(TAG, "Invalid input for wertMbProMonat: " + s.toString());
                    }





                }


            }

            @Override
            public void afterTextChanged(Editable s) {

                Context context = getApplicationContext();


                calculate calculate = new calculate();
                double calculatedValue = calculate.calculateDataAllowedDataUsedDifference(context);
                Log.d(TAG, "calculated Value equals: " + calculatedValue);

                DecimalFormat df = new DecimalFormat("#.##");
                String calculatedValueDecimalFormat = df.format(calculatedValue * 0.001);
                SharedViewModel.setAnswer(1, calculatedValueDecimalFormat);

                SharedViewModel2.setAnswer(1, calculatedValueDecimalFormat);


                SharedPreferences.Editor editor2 = getSharedPreferences("widgetKey", Context.MODE_PRIVATE).edit();
                editor2.putString("widgetKey", String.valueOf(calculatedValueDecimalFormat));
                editor2.apply();


                Intent intent = new Intent(context, MyWidgetProvider.class);
                intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MyWidgetProvider.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);


            }
        });


        // creates a persistent variable with SharedPreferences to store the Value the user entered at Mb per month

        calculate Calculate = new calculate();
        double calculatedValue = calculate.calculateDataAllowedDataUsedDifference(context);


        // creates value at startup to avoid having the user click the textbox to trigger the textwatcher

        DecimalFormat df = new DecimalFormat("#.##");
        String calculatedValueDecimalFormat = df.format(calculatedValue * 0.001);
        SharedViewModel.setAnswer(1, calculatedValueDecimalFormat);
        //SharedViewModel2.setAnswer(1, calculatedValueDecimalFormat);
        Log.d(TAG, "calculatedValueDecimalFormat: " + calculatedValueDecimalFormat);

        String calcvar2 = calculatedValueDecimalFormat;
        String calcvar3 = calcvar2.replace(",", ".");
        double divider1 = Double.parseDouble(wertMbProMonatStartup);
        double divider2 = Double.parseDouble(calcvar3);
        double calcvalue1 = divider2 / divider1;
        double calcvalue2 = calcvalue1 * 100;
        Log.d(TAG, "calcvalue2: " + calcvalue2);



        //calculating the percentage of how much the user is in the plus / minus depending on his monthly data limit
        double calcPercentage = (calculatedValue / (sharedPref3.getFloat("wertMbProMonat", 0) *1000)) * 100;
        Log.d(TAG, "calculatedValue: " + calculatedValue);
        Log.d(TAG, "WertMbProMonat: " + (sharedPref3.getFloat("wertMbProMonat", 0) *1000));
        Log.d(TAG, "calcPercentage: " + calcPercentage);



        Intent intent = new Intent(this, MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), MyWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);


        SharedPreferences sharedPref2 = this.getSharedPreferences("widgetKey", Context.MODE_PRIVATE);


    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHandler.handlePermissionResult(requestCode, permissions, grantResults);
    }









}