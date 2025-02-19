package com.appdev.data;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class OverlayHelper {
    private SharedViewModel2 SharedViewModel2;

    private static final String TAG = "DEBUG_OverlayHelper";
    private Context context;

    public OverlayHelper(Activity activity, SharedViewModel2 viewModel) {
        this.SharedViewModel2 = viewModel;
        this.context = activity;
    }

    /**
     * Displays the popup window with several buttons.
     */
    public void showPopupWindow(View anchorView) {
        // Start the pulse animation on the anchor view (e.g., a three-dot menu)
        pulseAnimation(anchorView);

        // Create a dialog for the popup window
        Dialog dialog = new Dialog(context);
        View popupView = dialog.getLayoutInflater().inflate(R.layout.popup_layout, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Initially set the popup view's visibility to GONE
        popupView.setVisibility(View.GONE);

        // Show the popup window at the desired location
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM | Gravity.END, 30, 230);

        // Find the buttons in the popup layout
        Button buttonReset = popupView.findViewById(R.id.ButtonReset);
        Button buttonExplain = popupView.findViewById(R.id.ButtonExplain);
        Button buttonUnlimitedData = popupView.findViewById(R.id.ButtonToggleUnlimited);
        Button buttonChangeLanguage = popupView.findViewById(R.id.ButtonChangeLanguage);

        // Setting the "on" or "off" text based on SharedPreferences
        SharedPreferences sharedPrefs = context.getSharedPreferences("UnlimitedFlag", Context.MODE_PRIVATE);
        SpannableString spannableOff;
        SpannableString spannableOn;

// Check the device's current language
        String currentLanguage = Locale.getDefault().getLanguage();

        if (currentLanguage.equals("de")) {  // If the language is German
            // German strings (longer)
            spannableOff = new SpannableString("   Unbegrenzte Datenvolumen aus   ");
            spannableOff.setSpan(new ForegroundColorSpan(Color.GREEN), 8, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  // "aus" part

            spannableOn = new SpannableString("   Unbegrenzte Datenvolumen an   ");
            spannableOn.setSpan(new ForegroundColorSpan(Color.RED), 8, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  // "an" part
        } else {  // For any other language, use the original (English) strings
            spannableOff = new SpannableString("   Turn On unlimited data   ");
            spannableOff.setSpan(new ForegroundColorSpan(Color.GREEN), 8, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  // "On" part

            spannableOn = new SpannableString("   Turn Off unlimited data   ");
            spannableOn.setSpan(new ForegroundColorSpan(Color.RED), 8, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  // "Off" part
        }

        Log.d(TAG, "value: " + sharedPrefs.getString("UnlimitedFlagValue2", "off"));
        if (sharedPrefs.getString("UnlimitedFlagValue2", "off").equals("off")) {
            Log.d(TAG, "flag is deactive at start");
            buttonUnlimitedData.setText(spannableOff);
        } else if (sharedPrefs.getString("UnlimitedFlagValue2", "off").equals("on")) {
            Log.d(TAG, "flag is active at startup");
            buttonUnlimitedData.setText(spannableOn);
        }


        // Animate the popup in
        popupView.setVisibility(View.VISIBLE);
        popupView.setAlpha(0f); // Start fully transparent
        popupView.setTranslationX(100); // Start slightly off the screen to the right

        // Animate to full opacity and original position
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(popupView, "alpha", 0f, 1f);
        ObjectAnimator slideInX = ObjectAnimator.ofFloat(popupView, "translationX", 100, 0);
        fadeIn.setDuration(250);
        slideInX.setDuration(250);
        fadeIn.start();
        slideInX.start();

        buttonChangeLanguage.setClickable(true);
        buttonChangeLanguage.setOnClickListener(v -> {
                    buttonReset.setClickable(false);

            SharedPreferences sharedPrefForLang = context.getSharedPreferences("SharedPrefsLang", Context.MODE_PRIVATE);
            Dialog dialogForLanguage = new Dialog(context);
            
            
            View popupViewForLanguage = null;
            if(sharedPrefForLang.getString("Lang","en").equals("en")) {
                popupViewForLanguage = dialog.getLayoutInflater().inflate(R.layout.popup_layout_language_english, null);
            }
            else if(sharedPrefForLang.getString("lang","en").equals("de")) {
                popupViewForLanguage = dialog.getLayoutInflater().inflate(R.layout.popup_layout_language_german, null);
            }

            
            final PopupWindow popupWindowForLanguage = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            popupWindowForLanguage.setOutsideTouchable(true);
            popupWindowForLanguage.setFocusable(true);

            // Initially set the popup view's visibility to GONE
            popupView.setVisibility(View.INVISIBLE);

            // Show the popup window at the desired location
            popupWindowForLanguage.showAtLocation(anchorView, Gravity.BOTTOM | Gravity.END, 30, 230);

            

            Button setGerman = (Button) popupViewForLanguage.findViewById(R.id.ButtonChangeLanguageDe);
            setGerman.setClickable(true);
            setGerman.setOnClickListener(v1 -> {
                setGerman.setClickable(false);
                sharedPrefForLang.edit().putString("lang", "de");
                showPopupWindow(anchorView);

            });


            Button setEnglish = (Button) popupViewForLanguage.findViewById(R.id.ButtonChangeLanguageEn);
            setEnglish.setClickable(true);
            setGerman.setOnClickListener(v1 -> {
                setEnglish.setClickable(false);
                sharedPrefForLang.edit().putString("lang", "en");
                showPopupWindow(anchorView);

            });





                });




// Reset button click listener
        buttonReset.setClickable(true);
        buttonReset.setOnClickListener(v -> {
                    buttonReset.setClickable(false);





            // Create a custom dialog for confirmation
            Dialog customDialog = new Dialog(v.getContext());
            customDialog.setContentView(R.layout.popup_confirmation_reset);
            customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Find the buttons in the custom dialog layout
            Button confirmButton = customDialog.findViewById(R.id.ButtonConfirm);
            Button cancelButton = customDialog.findViewById(R.id.ButtonCancel);

            // Confirm button: reset user data and restart the app
            confirmButton.setOnClickListener(view -> {
                SharedPreferences prefs = context.getSharedPreferences("calculate", Context.MODE_PRIVATE);
                prefs.edit().putString("installedMonth", null).apply();
                prefs.edit().putString("installedYear", null).apply();

                SharedPreferences prefs2 = context.getSharedPreferences("DataTrackingPrefs", Context.MODE_PRIVATE);
                prefs2.edit().putString("userInputDataUsage", null).apply();

                toggleButtons(buttonReset, buttonExplain, false);
                customDialog.dismiss();
                popupWindow.dismiss();

                Intent intent = new Intent(context, SplashScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

            // Cancel button: dismiss the confirmation dialog
            cancelButton.setOnClickListener(view -> customDialog.dismiss());
            customDialog.show();
        });

        // Explain button now calls the internal explanation method (if used from popup)
        buttonExplain.setOnClickListener(v -> {

            SharedPreferences sharedPrefForExplainFlag = context.getSharedPreferences("isExplaining", Context.MODE_PRIVATE);
            sharedPrefForExplainFlag.edit().putBoolean("isExplaining", true).apply();
            Log.d(TAG,"is Explaining is: " + sharedPrefForExplainFlag.getBoolean("isExplaining", false));


            Boolean isFirstStartup = false;
            showExplanation(isFirstStartup);
            toggleButtons(buttonReset, buttonExplain, false);
            popupWindow.dismiss();



        });

        // Unlimited data toggle button click listener
        buttonUnlimitedData.setOnClickListener(v -> {
            Log.d(TAG, "Pressed");
            Log.d(TAG, sharedPrefs.getString("UnlimitedFlagValue2", "off"));


            //turning the unlimited mode on
            if (sharedPrefs.getString("UnlimitedFlagValue2", "off").equals("off")) {
                sharedPrefs.edit().putString("UnlimitedFlagValue2", "on").apply();
                SharedViewModel3.setAnswer(1, "on");
                calculate.calculateDataAllowedDataUsedDifference(context);
                Log.d(TAG,"flag is: " + sharedPrefs.getString("UnlimitedFlagValue2", "off"));

                calculate calculate = new calculate();
                double calculatedValue = calculate.calculateDataAllowedDataUsedDifference(context);
                DecimalFormat df = new DecimalFormat("#.##");
                String calculatedValueDecimalFormat = df.format(calculatedValue * 0.001);
                SharedViewModel.setAnswer(1, calculatedValueDecimalFormat);


                SharedViewModel2.setAnswer(2, sharedPrefs.getString("UnlimitedFlagValue2", "off"));

                SharedPreferences sharedPref2 = context.getSharedPreferences("widgetKey", Context.MODE_PRIVATE);
                sharedPref2.edit().putString("widgetKey",calculatedValueDecimalFormat).apply();


                Log.d(TAG, "flag is now active");
                buttonUnlimitedData.setText(spannableOn);
                Boolean FlagUnlimited = true;
                UnlimitedDataHandler.handleFlagChange(FlagUnlimited);
                Log.d(TAG, String.valueOf(spannableOn));

                //turning the unlimited mode off
            } else if (sharedPrefs.getString("UnlimitedFlagValue2", "off").equals("on")) {
                sharedPrefs.edit().putString("UnlimitedFlagValue2", "off").apply();
                SharedViewModel3.setAnswer(1, "off");

                calculate.calculateDataAllowedDataUsedDifference(context);
                Log.d(TAG,"flag is: " + sharedPrefs.getString("UnlimitedFlagValue2", "off"));

                calculate calculate = new calculate();
                double calculatedValue = calculate.calculateDataAllowedDataUsedDifference(context);
                DecimalFormat df = new DecimalFormat("#.##");
                String calculatedValueDecimalFormat = df.format(calculatedValue * 0.001);
                SharedViewModel.setAnswer(1, calculatedValueDecimalFormat);

                SharedViewModel2.setAnswer(1, calculatedValueDecimalFormat);

                SharedPreferences sharedPref2 = context.getSharedPreferences("widgetKey", Context.MODE_PRIVATE);
                sharedPref2.edit().putString("widgetKey",calculatedValueDecimalFormat).apply();

                Boolean FlagUnlimited = false;
                Log.d(TAG, "flag is now deactive");
                buttonUnlimitedData.setText(spannableOff);
            }
        });

        // Optional: add any additional popup dismiss logic here.
        popupWindow.setOnDismissListener(() -> {
            // You can add an animation for the popup itself if needed.
        });
    }





    // Internal method used by the popup's "Explain" button.
    public void showExplanation(Boolean isFirstStartup) {
        SharedPreferences sharedPrefForExplain = context.getSharedPreferences("UnlimitedFlag", Context.MODE_PRIVATE);

        View rootView = ((Activity) context).findViewById(R.id.layout1);
        EditText EditTextRootView = (EditText) rootView.findViewById(R.id.WertMbProMonat);
        EditTextRootView.setFocusable(false);

        // Optionally change the status bar color in Day Mode.
        if (isDayMode()) {
            setStatusBarColor(Color.argb(0.8F, 0, 0, 0));
        }

        // Determine which overlay layout to use based on the flag.
        View dimmedOverlayDataPerMonth;
        if (sharedPrefForExplain.getString("UnlimitedFlagValue2", "off").equals("off")) {
            dimmedOverlayDataPerMonth = rootView.findViewById(R.id.dimOverlayDataPerMonth);
            Log.d(TAG, "Flag is off, layout set to basic");
        } else {
            dimmedOverlayDataPerMonth = rootView.findViewById(R.id.dimOverlayDataPerMonthUnlimited);
            Log.d(TAG, "Flag is on, layout set to unlimited");
        }

        // Update the overlay text if necessary.
        if (sharedPrefForExplain.getString("UnlimitedFlagValue2", "off").equals("off")) {
            TextView textViewOverlay = dimmedOverlayDataPerMonth.findViewById(R.id.cutoutTextView);
            SharedPreferences sharedPref = context.getSharedPreferences("calculate", Context.MODE_PRIVATE);
            float wertMbProMonatStartup = sharedPref.getFloat("wertMbProMonat", 0);
            int wertMbProMonatStartupInt = (int) wertMbProMonatStartup;
            textViewOverlay.setText(String.valueOf(wertMbProMonatStartupInt));
        }

        // Animate the overlay in.
        dimmedOverlayDataPerMonth.setVisibility(View.VISIBLE);
        dimmedOverlayDataPerMonth.setAlpha(0f);
        dimmedOverlayDataPerMonth.animate()
                .alpha(1f)
                .setDuration(300);
        Log.d(TAG, "set visibility to visible");



//        // Allow the overlay to be dismissed on click.
//        dimmedOverlayDataPerMonth.setOnClickListener(v1 -> {
//            dimmedOverlayDataPerMonth.animate()
//                    .alpha(0f)
//                    .setDuration(300)
//                    .withEndAction(() -> dimmedOverlayDataPerMonth.setVisibility(View.GONE));
//            if (isDayMode()) {
//                int startColor = ((Activity) context).getWindow().getStatusBarColor();
//                int endColor = Color.argb(0.8F, 0, 0, 0);
//                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
//                colorAnimation.setDuration(300);
//                colorAnimation.addUpdateListener(animator -> {
//                    int animatedColor = (int) animator.getAnimatedValue();
//                    ((Activity) context).getWindow().setStatusBarColor(animatedColor);
//                });
//                colorAnimation.start();
//            }
//        });






        // Setup the "next" button for the first explanation overlay.
        Button buttonNext = dimmedOverlayDataPerMonth.findViewById(R.id.buttonNext);

        buttonNext.setClickable(true);
        buttonNext.setOnClickListener(v2 -> {
            buttonNext.setClickable(false);


            // Fade out the first overlay.
            dimmedOverlayDataPerMonth.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> dimmedOverlayDataPerMonth.setVisibility(View.GONE));

            // Get the next overlay layout.
            View dimmedOverlayDataLeft;
            if (sharedPrefForExplain.getString("UnlimitedFlagValue2", "off").equals("off")) {
                dimmedOverlayDataLeft = rootView.findViewById(R.id.dimOverlayDataLeft);
            } else {
                dimmedOverlayDataLeft = rootView.findViewById(R.id.dimOverlayDataLeftUnlimited);
            }

            // Animate the next overlay in.

            dimmedOverlayDataLeft.setVisibility(View.VISIBLE);

            dimmedOverlayDataLeft.setAlpha(0f);
            TextView textViewDataLeftValueFromProgressBarSlide = dimmedOverlayDataLeft.findViewById(R.id.wertBisHeuteBenutztErlaubt2);
            dimmedOverlayDataLeft.setTranslationX(0f);
            textViewDataLeftValueFromProgressBarSlide.setTranslationX(0f);
            textViewDataLeftValueFromProgressBarSlide.setTranslationY(0f);
            dimmedOverlayDataLeft.animate()
                    .alpha(1f)
                    .setDuration(300);


            TextView textView = dimmedOverlayDataLeft.findViewById(R.id.wertBisHeuteBenutztErlaubt2);
            SharedPreferences sharedPref2 = context.getSharedPreferences("widgetKey", Context.MODE_PRIVATE);
            String sharedPrefValue = sharedPref2.getString("widgetKey", "0");
            // Update any text values from SharedPreferences.
            if (isFirstStartup){
                textView.setText("6.9");
            }
            else {
                textView.setText(sharedPrefValue);
            }

//

            // Setup the next button for this overlay.
            Button buttonNext2 = dimmedOverlayDataLeft.findViewById(R.id.buttonNext);
            buttonNext2.setClickable(true);
            buttonNext2.setOnClickListener(v3 -> {
                buttonNext2.setClickable(false);






                dimmedOverlayDataLeft.animate()
                                .alpha(0f)
                                .setDuration(300)
                                .withEndAction(() -> dimmedOverlayDataLeft.setVisibility(View.GONE));

                        // Show the progress bar overlay.
                        View dimmedOverlayProgressBar;
                        if (sharedPrefForExplain.getString("UnlimitedFlagValue2", "off").equals("off")) {
                            dimmedOverlayProgressBar = rootView.findViewById(R.id.dimOverlayProgressBar);
                        } else {
                            dimmedOverlayProgressBar = rootView.findViewById(R.id.dimOverlayProgressBarUnlimited);
                        }

                        dimmedOverlayProgressBar.setVisibility(View.VISIBLE);
                        dimmedOverlayProgressBar.setAlpha(0f);
                        dimmedOverlayProgressBar.animate()
                                .alpha(1f)
                                .setDuration(300);



                        SharedPreferences sharedPref3 = context.getSharedPreferences("FragmentLeftKey", Context.MODE_PRIVATE);





                        ConstraintLayout frameLayoutTopNew4 = dimmedOverlayProgressBar.findViewById(R.id.FrameLayoutTop3);
                        ConstraintLayout frameLayoutTopOld2 = dimmedOverlayProgressBar.findViewById(R.id.FrameLayoutTop2);
                        ConstraintLayout frameLayoutTop4 = dimmedOverlayProgressBar.findViewById(R.id.FrameLayoutTop);





                        progressBarSetterLeft(dimmedOverlayProgressBar, sharedPrefValue, isFirstStartup);

                        // Animate the progress bar elements.

                        frameLayoutTopOld2.setTranslationX(0f);
                        textViewDataLeftValueFromProgressBarSlide.setTranslationX(0f);
                        ObjectAnimator translateOutAnimator = ObjectAnimator.ofFloat(frameLayoutTopNew4, "translationX", 0f, (-frameLayoutTopNew4.getWidth() * 2));
                        translateOutAnimator.setDuration(100);
                        translateOutAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                frameLayoutTopNew4.setVisibility(View.VISIBLE);
                                frameLayoutTopNew4.setTranslationX(-frameLayoutTopNew4.getWidth());

                                ObjectAnimator translateOldOutAnimator = ObjectAnimator.ofFloat(frameLayoutTopOld2, "translationX", 0f, frameLayoutTopOld2.getWidth() + 50);
                                ObjectAnimator translateNewInAnimator = ObjectAnimator.ofFloat(frameLayoutTopNew4, "translationX", -frameLayoutTopNew4.getWidth(), 0f);
                                translateOldOutAnimator.setDuration(500);
                                translateNewInAnimator.setDuration(500);
                                translateOldOutAnimator.setInterpolator(new DecelerateInterpolator());
                                translateNewInAnimator.setInterpolator(new DecelerateInterpolator());
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.play(translateOldOutAnimator).with(translateNewInAnimator);
                                animatorSet.start();
                            }
                        });
                        translateOutAnimator.start();

//                // Allow the progress bar overlay to be dismissed on click.
//                dimmedOverlayProgressBar.setOnClickListener(v1 -> {
//                    dimmedOverlayProgressBar.animate()
//                            .alpha(0f)
//                            .setDuration(300)
//                            .withEndAction(() -> dimmedOverlayProgressBar.setVisibility(View.GONE));
//                });


                        //getting right fragment view and transitioning from progressBar to dayMonth layout
                        View dimmedOverlayDayMonth = rootView.findViewById(R.id.dimOverlayDayMonth);
                        Button buttonNextProgressBar = dimmedOverlayProgressBar.findViewById(R.id.buttonNext);




                    buttonNextProgressBar.setClickable(true);
                    buttonNextProgressBar.setOnClickListener(v4 -> {
                        buttonNextProgressBar.setClickable(false);


                        Boolean isFirstStartupExplanationAborted = false;
                        sharedPrefForExplain.edit().putBoolean("isFirstStartupExplanationAborted",isFirstStartupExplanationAborted).apply();



                            dimmedOverlayProgressBar.animate()
                                    .alpha(0f)
                                    .setDuration(300)
                                    .withEndAction(() -> dimmedOverlayProgressBar.setVisibility(View.GONE));


                            dimmedOverlayDayMonth.setVisibility(View.VISIBLE);

                            dimmedOverlayDayMonth.setAlpha(0f);
                            dimmedOverlayDayMonth.animate()
                                    .alpha(1f)
                                    .setDuration(300);
                            Button buttonFinish = (Button) dimmedOverlayDayMonth.findViewById(R.id.buttonNext3);
                            buttonFinish.setActivated(false);
                            buttonFinish.setVisibility(View.INVISIBLE);



                            //introducing constrained layouts
                            ConstraintLayout frameLayoutProgressBarLeft = dimmedOverlayDayMonth.findViewById(R.id.FrameLayoutProgressBarLeft);
                            ConstraintLayout frameLayoutDataLeft = dimmedOverlayDayMonth.findViewById(R.id.FrameLayoutDataLeft);
                            ConstraintLayout frameLayoutBlueBoxMonth = dimmedOverlayDayMonth.findViewById(R.id.FrameLayoutBlueBoxMonth);




                            dimmedOverlayDayMonth.post(new Runnable() {
                                @Override
                                public void run() {


                                    // First slide setup
                                    frameLayoutProgressBarLeft.setVisibility(View.VISIBLE);
                                    frameLayoutDataLeft.setVisibility(View.VISIBLE);

                                    // Set initial position (off-screen to the right)
                                    frameLayoutDataLeft.setTranslationX(frameLayoutDataLeft.getWidth());


                                    //Fill frameLayoutDataLeft with data
                                    // SharedPreferences sharedPref2 = context.getSharedPreferences("widgetKey", Context.MODE_PRIVATE);
                                    String sharedPrefValue = sharedPref2.getString("widgetKey", "0");
                                    TextView textView = dimmedOverlayDayMonth.findViewById(R.id.wertBisHeuteBenutztErlaubt2);
                                    textView.setText(sharedPrefValue);



                                    // Animate frameLayoutDataLeft into view
                                    frameLayoutDataLeft.animate()
                                            .translationX(0f)
                                            .setDuration(650)  // duration in milliseconds
                                            .setInterpolator(new AccelerateDecelerateInterpolator())
                                            .start();

                                    // Animate frameLayoutProgressBarLeft off-screen
                                    frameLayoutProgressBarLeft.animate()
                                            .translationX(-frameLayoutProgressBarLeft.getWidth())
                                            .setDuration(350)  // duration in milliseconds
                                            .setInterpolator(new AccelerateDecelerateInterpolator())
                                            .withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    frameLayoutProgressBarLeft.setVisibility(View.INVISIBLE);
                                                }
                                            })
                                            .start();
                                }
                            });

                            // Delay the second slide animation by 2000ms to ensure the first animation completes
                            // Disable the button at the beginning


                            dimmedOverlayDayMonth.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Show and set up the blue box
                                    frameLayoutBlueBoxMonth.setVisibility(View.VISIBLE);
                                    frameLayoutBlueBoxMonth.setTranslationX(frameLayoutBlueBoxMonth.getWidth());


                                    //TODO fill month with data
                                    TextView textViewMonth = frameLayoutBlueBoxMonth.findViewById(R.id.textViewMonth);
                                    TextView textViewDate = frameLayoutBlueBoxMonth.findViewById(R.id.textViewDate);
                                    ProgressBar progressForMonth = frameLayoutBlueBoxMonth.findViewById(R.id.ProgressBarMonth);
                                    TextView percentageTextView = frameLayoutBlueBoxMonth.findViewById(R.id.textViewPercentage); // Text above progress bar

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

                                    int width = progressForMonth.getWidth();
                                    Log.d(TAG, "ProgressBar width: " + width);

                                    if (width == 0) {
                                        Log.e(TAG, "Width is 0, skipping position adjustment.");
                                        return;
                                    }

                                    // Scale progress for range 0-100
                                    int cappedProgress = Math.min(progressForBar, 100);
                                    int progressWidth = (int) ((cappedProgress / 100.0) * width);

                                    Log.d(TAG, "Progress: " + progressForBar);
                                    Log.d(TAG, "CappedProgress: " + cappedProgress);
                                    Log.d(TAG, "ProgressWidth: " + progressWidth);

                                    // Adjust X position based on progress thresholds
                                    if (progressForBar >= 90) {
                                        textView.setX(progressForMonth.getX() + width - 185); // Stops at 90%
                                        Log.d(TAG, "-- progress >= 90 -- Locked X value.");
                                    } else if (progressForBar >= 60) {
                                        textView.setX(progressForMonth.getX() + progressWidth - 185); // Left side of indicator
                                        Log.d(TAG, "-- progress >= 60 -- Text left of indicator.");
                                    } else {
                                        textView.setX(progressForMonth.getX() + progressWidth + 30); // Right side of indicator
                                        Log.d(TAG, "-- progress < 60 -- Text right of indicator.");
                                    }


                                    textView.setY(progressForMonth.getY() + (progressForMonth.getHeight() - textView.getHeight()) / 2);




                                    //TODO fill month with data


                                    // Animate frameLayoutBlueBoxMonth (sliding in)
                                    frameLayoutBlueBoxMonth.animate()
                                            .translationX(0f)
                                            .setDuration(650)
                                            .setInterpolator(new AccelerateDecelerateInterpolator())
                                            .withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Once this animation completes, enable the button
                                                    Button buttonFinish = (Button) dimmedOverlayDayMonth.findViewById(R.id.buttonNext3);
                                                    buttonFinish.setVisibility(View.VISIBLE);
                                                    buttonFinish.setActivated(true);

                                                }
                                            })
                                            .start();

                                    // Animate frameLayoutDataLeft (sliding out)
                                    frameLayoutDataLeft.animate()
                                            .translationX(-frameLayoutDataLeft.getWidth())
                                            .setDuration(350)
                                            .setInterpolator(new AccelerateDecelerateInterpolator())
                                            .withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    frameLayoutDataLeft.setVisibility(View.INVISIBLE);
                                                }
                                            })
                                            .start();
                                }
                            }, 750);









                    //animating the transition from progress bar to data left to month day


                });
                // next explain slide here
                // Setup the next button for this overlay.





                Button buttonNext3 = dimmedOverlayDayMonth.findViewById(R.id.buttonNext3);

                buttonNext3.setClickable(true);
                buttonNext3.setOnClickListener(v4 -> {
                    buttonNext3.setClickable(false);

                    SharedPreferences sharedPrefForExplainFlag = context.getSharedPreferences("isExplaining", Context.MODE_PRIVATE);


                    sharedPrefForExplainFlag.edit().putBoolean("isExplaining", false).apply();
                    EditTextRootView.setFocusable(true);
                    EditTextRootView.setFocusableInTouchMode(true);


                        Log.d(TAG, "EditTextRootView is: " + EditTextRootView.getFocusable());


                    ConstraintLayout layoutTempForInvisibility2 = (ConstraintLayout) dimmedOverlayDayMonth.findViewById(R.id.FrameLayoutDataLeft);
                    layoutTempForInvisibility2.setVisibility(View.INVISIBLE);
                    ConstraintLayout layoutTempForInvisibility = (ConstraintLayout) dimmedOverlayDayMonth.findViewById(R.id.FrameLayoutBlueBoxMonth);
                    layoutTempForInvisibility.setVisibility(View.INVISIBLE);
                    dimmedOverlayDayMonth.animate()
                                .alpha(0f)
                                .setDuration(300)
                                .withEndAction(() -> dimmedOverlayDayMonth.setVisibility(View.GONE));

                });

            });
        });
    }

    /**
     * Animates two buttons simultaneously.
     */
    private void toggleButtons(View button1, View button2, boolean show) {
        float targetAlpha = show ? 1f : 0f;
        float targetTranslationY = show ? 0 : 100; // Adjust as needed for slide effect

        // Animate the first button
        ObjectAnimator fadeInOut1 = ObjectAnimator.ofFloat(button1, "alpha", targetAlpha);
        ObjectAnimator slide1 = ObjectAnimator.ofFloat(button1, "translationY", targetTranslationY);
        fadeInOut1.setDuration(300);
        slide1.setDuration(300);

        // Animate the second button
        ObjectAnimator fadeInOut2 = ObjectAnimator.ofFloat(button2, "alpha", targetAlpha);
        ObjectAnimator slide2 = ObjectAnimator.ofFloat(button2, "translationY", targetTranslationY);
        fadeInOut2.setDuration(300);

        if (show) {
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
        } else {
            fadeInOut1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    button1.setVisibility(View.GONE);
                    button2.setVisibility(View.GONE);
                }
            });
        }

        fadeInOut1.start();
        slide1.start();
        fadeInOut2.start();
        slide2.start();
    }

    /**
     * Checks if the device is in Day Mode (Light Mode).
     */
    private boolean isDayMode() {
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_NO;
    }

    /**
     * Sets the status bar color.
     */
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.setStatusBarColor(color);
        }
    }

    /**
     * Plays a pulse animation on the specified view.
     */
    private void pulseAnimation(View view) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.4f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.4f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.4f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.4f, 1.0f);

        scaleUpX.setDuration(150);
        scaleUpY.setDuration(150);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);

        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.play(scaleUpX).with(scaleUpY).before(scaleDownX).with(scaleDownY);
        pulseSet.start();
    }


    private void progressBarSetterLeft(View view, String sharedPrefValue, Boolean isFirstStartup){

        TextView textView2 = view.findViewById(R.id.wertBisHeuteBenutztErlaubt2);
        textView2.setTranslationX(0);
        textView2.setTranslationY(0);
        textView2.setText(sharedPrefValue);
        SharedPreferences sharedPref3 = context.getSharedPreferences("FragmentLeftKey", Context.MODE_PRIVATE);
        int calcValue;
        if(isFirstStartup){
            calcValue = 11;
        }
        else {calcValue = sharedPref3.getInt("ProgressInt", 0);
            Log.d(TAG,"-------------ProgressInt: "+ sharedPref3.getInt("ProgressInt", 0));
        }

        int absoluteValue = Math.abs(calcValue);
        ProgressBar progressBar = view.findViewById(R.id.ProgressBarData);
        progressBar.setProgress(absoluteValue);

        ConstraintLayout frameLayoutTopNew4 = view.findViewById(R.id.FrameLayoutTop3);
        ConstraintLayout frameLayoutTopOld2 = view.findViewById(R.id.FrameLayoutTop2);
        ConstraintLayout frameLayoutTop4 = view.findViewById(R.id.FrameLayoutTop);

        // Change the progress bar color based on the calculated value.
        if (calcValue < 0) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(191, 64, 72)));
            frameLayoutTop4.setBackgroundResource(R.drawable.rounded_corners_red);
        } else {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(19, 174, 75)));
            frameLayoutTop4.setBackgroundResource(R.drawable.rounded_corners_green);
        }

        TextView usageTextView = view.findViewById(R.id.usageTextView);
        usageTextView.setText(absoluteValue + "%");

        // Restore the text view's position from SharedPreferences.
        if(isFirstStartup){

            int absoluteValue2 = Math.abs(calcValue);
            progressBar.setProgress(absoluteValue2);
            usageTextView.setText(absoluteValue + "%");
            int progress = absoluteValue2;
            int cappedProgress = Math.min(progress, 20);
            int width = progressBar.getWidth();
            int progressWidth = (int) ((cappedProgress / 20.0) * width);
            if (progress >= 1000) {
                usageTextView.setX((progressBar.getX() + progressWidth - 230)); // Adjust this value as needed
                Log.d(TAG, "-- progress > 1000 -- set X value: " + (progressBar.getX() + progressWidth - 300));
            } else if (progress >= 100) {
                usageTextView.setX((progressBar.getX() + progressWidth - 200)); // Adjust this value as needed
                Log.d(TAG, "-- progress > 100 -- set X value: " + (progressBar.getX() + progressWidth - 200));
            } else if (cappedProgress > 10) {
                usageTextView.setX((progressBar.getX() + progressWidth - 170));
                Log.d(TAG, "-- bigger10 -- set X value: " + (progressBar.getX() + progressWidth - 170));
            } else {
                usageTextView.setX((progressBar.getX() + progressWidth + 70));
                Log.d(TAG, "-- smaller10 -- set X value: " + (progressBar.getX() + progressWidth + 70));
            }

            usageTextView.setY(progressBar.getY() + (progressBar.getHeight() - usageTextView.getHeight()) / 2);
        }
        else{
            usageTextView.setX(sharedPref3.getInt("textView.setXX", 0));
        }


        if(isFirstStartup){
            usageTextView.setY(450);
        }
        else{
            usageTextView.setY(sharedPref3.getInt("textView.setYY", 0));
        }




    }
}
