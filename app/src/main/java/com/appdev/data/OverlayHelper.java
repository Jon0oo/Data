package com.appdev.data;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class OverlayHelper {
    private static final String TAG = "DEBUG_OverlayHelper";
    private Context context;

    public OverlayHelper(Activity activity) {


        this.context = activity;
    }

    public void showPopupWindow(View anchorView) {
        // Start the pulse animation on the anchor view (the three dots)
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

        // Animate the popup in
        popupView.setVisibility(View.VISIBLE);
        popupView.setAlpha(0f); // Start fully transparent
        popupView.setTranslationX(100); // Start slightly off the screen to the right

        // Animate to full opacity and original position
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(popupView, "alpha", 0f, 1f);
        ObjectAnimator slideInX = ObjectAnimator.ofFloat(popupView, "translationX", 100, 0); // Slide in from the right

        fadeIn.setDuration(250);
        slideInX.setDuration(250);

        // Start the animations
        fadeIn.start();
        slideInX.start();

        // Find the buttons in the popup layout
        Button buttonReset = popupView.findViewById(R.id.ButtonReset);
        Button buttonExplain = popupView.findViewById(R.id.ButtonExplain);

        buttonReset.setOnClickListener(v -> {
            // Create a custom dialog for confirmation
            Dialog customDialog = new Dialog(v.getContext());
            customDialog.setContentView(R.layout.popup_confirmation_reset);
            customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Find the buttons in the custom dialog layout
            Button confirmButton = customDialog.findViewById(R.id.ButtonConfirm);
            Button cancelButton = customDialog.findViewById(R.id.ButtonCancel);

            // Set up the confirm button
            confirmButton.setOnClickListener(view -> {
                // Reset user inputted data
                SharedPreferences prefs = context.getSharedPreferences("calculate", Context.MODE_PRIVATE);
                prefs.edit().putString("installedMonth", null).apply();
                prefs.edit().putString("installedYear", null).apply();

                SharedPreferences prefs2 = context.getSharedPreferences("DataTrackingPrefs", Context.MODE_PRIVATE);
                prefs2.edit().putString("userInputDataUsage", null).apply();

                // Handle the reset action here
                toggleButtons(buttonReset, buttonExplain, false);

                // Dismiss dialogs
                customDialog.dismiss();
                popupWindow.dismiss();

                // Restart the app from the SplashScreen activity
                Intent intent = new Intent(context, SplashScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

            // Set up the cancel button
            cancelButton.setOnClickListener(view -> customDialog.dismiss());

            // Show the custom dialog
            customDialog.show();
        });

        buttonExplain.setOnClickListener(v -> {
            // Access the main activity's root view


            View rootView = ((Activity) context).findViewById(R.id.layout1);
            if (isDayMode()) {
                setStatusBarColor(Color.argb(0.8F, 0.0F, 0.0F, 0.0F)); // Replace with your color
            }

            // Access the included layout for the overlay
            View dimmedOverlayDataPerMonth = rootView.findViewById(R.id.dimOverlayDataPerMonth);
            TextView textViewOverlay = dimmedOverlayDataPerMonth.findViewById(R.id.cutoutTextView);

            // Get data from shared preferences
            SharedPreferences sharedPref = context.getSharedPreferences("calculate", Context.MODE_PRIVATE);
            float wertMbProMonatStartup = sharedPref.getFloat("wertMbProMonat", 0);
            int wertMbProMonatStartupInt = (int) wertMbProMonatStartup;

            textViewOverlay.setText(String.valueOf(wertMbProMonatStartupInt));

            // Set initial visibility and alpha
            dimmedOverlayDataPerMonth.setVisibility(View.VISIBLE);
            dimmedOverlayDataPerMonth.setAlpha(0f); // Start completely transparent

            // Fade in animation
            dimmedOverlayDataPerMonth.animate()
                    .alpha(1f) // Fade to fully visible
                    .setDuration(300);

            Log.d(TAG, "set visibility to visible");

            // Optionally toggle button states
            toggleButtons(buttonReset, buttonExplain, false);

            // Dismiss the popup window
            popupWindow.dismiss();

            // Close the dimmed overlay when clicking on it
            dimmedOverlayDataPerMonth.setOnClickListener(v1 -> {
                dimmedOverlayDataPerMonth.animate()
                        .alpha(0f) // Fade to transparent
                        .setDuration(300)
                        .withEndAction(() -> dimmedOverlayDataPerMonth.setVisibility(View.GONE)); // Set GONE at the end

                if (isDayMode()) {
                    // Get the current status bar color (starting color)
                    int startColor = ((Activity) context).getWindow().getStatusBarColor();

                    // Define the target color (end color)
                    int endColor = Color.argb(0.8F, 0.0F, 0.0F, 0.0F); // Replace with your target color

                    // Create a ValueAnimator to animate between the start and end color
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
                    colorAnimation.setDuration(300); // Duration for the animation (same as your fade duration)

                    // Add a listener to update the status bar color as the animation progresses
                    colorAnimation.addUpdateListener(animator -> {
                        int animatedColor = (int) animator.getAnimatedValue();
                        // Set the animated color to the status bar
                        ((Activity) context).getWindow().setStatusBarColor(animatedColor);
                    });

                    // Start the animation
                    colorAnimation.start();
                }

            });


            // Setup button for next overlay
            Button buttonNext = dimmedOverlayDataPerMonth.findViewById(R.id.buttonNext);
            buttonNext.setOnClickListener(v2 -> {

                //getting rid of the first explaining layout
                dimmedOverlayDataPerMonth.animate()
                        .alpha(0f) // Fade to transparent
                        .setDuration(300)
                        .withEndAction(() -> {
                            dimmedOverlayDataPerMonth.setVisibility(View.GONE);});


                //creating teh second explaining layout
                View dimmedOverlayDataLeft = rootView.findViewById(R.id.dimOverlayDataLeft);
                dimmedOverlayDataLeft.setVisibility(View.VISIBLE);
                dimmedOverlayDataLeft.setAlpha(0f); // Start fully transparent
                dimmedOverlayDataLeft.animate()
                        .alpha(1f)
                        .setDuration(300);
                            // Show the next overlay

                //let the nextbutton wiggle if pressed




                SharedPreferences sharedPref2 = context.getSharedPreferences("widgetKey", Context.MODE_PRIVATE);
                String sharedPrefValue = sharedPref2.getString("widgetKey", "0");
                TextView textView = dimmedOverlayDataLeft.findViewById(R.id.wertBisHeuteBenutztErlaubt2);
                textView.setText(sharedPrefValue);




                dimmedOverlayDataLeft.setOnClickListener(v1 -> {
                    dimmedOverlayDataLeft.animate()
                            .alpha(0f) // Fade to transparent
                            .setDuration(300)
                            .withEndAction(() -> dimmedOverlayDataLeft.setVisibility(View.GONE)); // Set GONE at the end
                });

                Button buttonNext2 = dimmedOverlayDataLeft.findViewById(R.id.buttonNext);
                buttonNext2.setOnClickListener(v3 -> {
                    dimmedOverlayDataLeft.animate()
                            .alpha(0f) // Fade to transparent
                            .setDuration(300)
                            .withEndAction(() -> {
                                dimmedOverlayDataLeft.setVisibility(View.GONE); // Set GONE at the end
                            });

                    View dimmedOverlayProgressBar = rootView.findViewById(R.id.dimOverlayProgressBar);
                    dimmedOverlayProgressBar.setVisibility(View.VISIBLE);
                    dimmedOverlayProgressBar.setAlpha(0f); // Start fully transparent
                    dimmedOverlayProgressBar.animate()
                            .alpha(1f)
                            .setDuration(300);

                    TextView textView2 = dimmedOverlayProgressBar.findViewById(R.id.wertBisHeuteBenutztErlaubt2);
                    textView2.setText(sharedPrefValue);
                    SharedPreferences sharedPref3 = context.getSharedPreferences("FragmentLeftKey", Context.MODE_PRIVATE);
                    int calcValue = sharedPref3.getInt("ProgressInt", 0);
                    int absoluteValue = Math.abs(calcValue);
                    ProgressBar progressBar = dimmedOverlayProgressBar.findViewById(R.id.ProgressBarData);
                    progressBar.setProgress(absoluteValue);

                    ConstraintLayout frameLayoutTopNew4 = dimmedOverlayProgressBar.findViewById(R.id.FrameLayoutTop3);
                    ConstraintLayout frameLayoutTopOld2 = dimmedOverlayProgressBar.findViewById(R.id.FrameLayoutTop2);
                    ConstraintLayout frameLayoutTop4 = dimmedOverlayProgressBar.findViewById(R.id.FrameLayoutTop);

                    // Set progress bar color based on calcValue
                    if (calcValue < 0) {
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(191, 64, 72)));
                        frameLayoutTop4.setBackgroundResource(R.drawable.rounded_corners_red);
                    } else {
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(19, 174, 75)));
                        frameLayoutTop4.setBackgroundResource(R.drawable.rounded_corners_green);
                    }

                    TextView usageTextView = dimmedOverlayProgressBar.findViewById(R.id.usageTextView);
                    usageTextView.setText(absoluteValue + "%");

                    // Restore text position from preferences
                    usageTextView.setX(sharedPref3.getInt("textView.setXX", 0));
                    usageTextView.setY(sharedPref3.getInt("textView.setYY", 0));

                    // Step 1: Animate frameLayoutTopNew4 out to the left
                    ObjectAnimator translateOutAnimator = ObjectAnimator.ofFloat(frameLayoutTopNew4, "translationX", 0f, (-frameLayoutTopNew4.getWidth() * 2));
                    translateOutAnimator.setDuration(100); // Duration for moving out

                    // Step 2: Animate frameLayoutTopOld2 and frameLayoutTopNew4 in after frameLayoutTopNew4 is moved out
                    translateOutAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Make frameLayoutTopNew4 visible and start off-screen to the left
                            frameLayoutTopNew4.setVisibility(View.VISIBLE);
                            frameLayoutTopNew4.setTranslationX(-frameLayoutTopNew4.getWidth());

                            // Step 3: Move frameLayoutTopOld2 and frameLayoutTopNew4 to the right
                            ObjectAnimator translateOldOutAnimator = ObjectAnimator.ofFloat(frameLayoutTopOld2, "translationX", 0f, frameLayoutTopOld2.getWidth() + 50); // Move further to the right
                            ObjectAnimator translateNewInAnimator = ObjectAnimator.ofFloat(frameLayoutTopNew4, "translationX", -frameLayoutTopNew4.getWidth(), 0f);

                            // Set the same duration for both animations
                            translateOldOutAnimator.setDuration(500);
                            translateNewInAnimator.setDuration(500);

                            // Use interpolator for smoother transitions
                            translateOldOutAnimator.setInterpolator(new DecelerateInterpolator());
                            translateNewInAnimator.setInterpolator(new DecelerateInterpolator());

                            // Start both animations simultaneously
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.play(translateOldOutAnimator).with(translateNewInAnimator);
                            animatorSet.start();
                        }
                    });

                    // Start the first animation
                    translateOutAnimator.start();

                    // Make dimmedOverlayProgressBar visible and handle click
                    dimmedOverlayProgressBar.setOnClickListener(v1 -> {
                        dimmedOverlayProgressBar.animate()
                                .alpha(0f) // Fade to transparent
                                .setDuration(300)
                                .withEndAction(() -> dimmedOverlayProgressBar.setVisibility(View.GONE)); // Set GONE at the end
                    });






                    // next explanation slide here




                });






            });
        });

        // Dismiss listener for the popup window
        popupWindow.setOnDismissListener(() -> {
            // Optionally, you can also add an animation for the popup itself if needed
        });
    }

    // Method to animate buttons in and out
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
    private boolean isDayMode() {
        // Check if the device is in Day Mode (Light Mode)
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_NO; // Day mode
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use the Window object to change the status bar color
            Window window = ((Activity) context).getWindow();
            window.setStatusBarColor(color);
        }
    }
    // Pulse animation method
    private void pulseAnimation(View view) {
        // Scale up
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.4f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.4f);

        // Scale down
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.4f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.4f, 1.0f);

        // Set duration for the scaling animations
        scaleUpX.setDuration(150);
        scaleUpY.setDuration(150);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);

        // Combine animations into a set
        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.play(scaleUpX).with(scaleUpY).before(scaleDownX).with(scaleDownY);

        // Start the pulse animation
        pulseSet.start();
    }
}
