package com.appdev.data;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

            @Override
            protected void onCreate( Bundle savedInstanceState) {



                //stops the UI from going upwards if keyboard is opened

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


                // hides the bar at the top

                Objects.requireNonNull(getSupportActionBar()).hide();



                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);


                //Initialize the ViewModel

                SharedViewModel mainActivityViewModel = new ViewModelProvider(this).get(SharedViewModel.class);




                //Initialize the Pager Adapter to setup the Slider Pages

                ViewPager2 viewPager = findViewById(R.id.ViewPagerSlider); // Replace with your actual ID
                List<Fragment> fragmentList = new ArrayList<>();
                fragmentList.add(new FragmentLeft());
                fragmentList.add(new FragmentMiddle());
                fragmentList.add(new FragmentRight());
                PagerAdapter2 adapter = new PagerAdapter2(this, fragmentList);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(1, false);





                // introduce .xml objects to java

                EditText MbProMonat = (EditText) findViewById(R.id.WertMbProMonat);


















                //introduces shared preference early to set the wertMbProMonat value at startup

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                String wertMbProMonatStartup = sharedPref.getString("wertMbProMonat", "0");
                MbProMonat.setText(wertMbProMonatStartup);



                // get current year month and day

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);





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

                        DecimalFormat df = new DecimalFormat("#.##");
                        String calculatedValueDecimalFormat = df.format(calculatedValue * 0.001);
                        SharedViewModel.setAnswer(1,calculatedValueDecimalFormat);











                    }
                });




                // creates a persistent variable with SharedPreferences to store the Value the user entered at Mb per month

                calculate Calculate = new calculate();
                double calculatedValue = calculate.calculateDataUsage(sharedPref.getString("wertMbProMonat", null));




                // creates value at startup to avoid having the user click the textbox to trigger the textwatcher

                DecimalFormat df = new DecimalFormat("#.##");
                String calculatedValueDecimalFormat = df.format(calculatedValue * 0.001);
                SharedViewModel.setAnswer(1,calculatedValueDecimalFormat);

                String calcvar2 = calculatedValueDecimalFormat;
                String calcvar3 = calcvar2.replace("," , ".");
                double divider1 = Double.parseDouble(wertMbProMonatStartup);
                double divider2 = Double.parseDouble(calcvar3);
                double calcvalue1 = divider2 / divider1;
                double calcvalue2 = calcvalue1 * 100;



                SharedViewModel2.setAnswer(1, String.valueOf(calcvalue2));


            }
    }