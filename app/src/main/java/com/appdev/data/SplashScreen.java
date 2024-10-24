package com.appdev.data;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    private int currentImageIndex = 0;
    private int[] imageResources = {
            R.drawable.data_000,
            R.drawable.data_001,
            R.drawable.data_002,
            R.drawable.data_003,
            R.drawable.data_004,
            R.drawable.data_005,
            R.drawable.data_006,
            R.drawable.data_007,
            R.drawable.data_008,
            R.drawable.data_009,
            R.drawable.data_010,
            R.drawable.data_011,
            R.drawable.data_012,
            R.drawable.data_013,
            R.drawable.data_014,
            R.drawable.data_015,
            R.drawable.data_016,
            R.drawable.data_017,
            R.drawable.data_018,
            R.drawable.data_019,
            R.drawable.data_020,
            R.drawable.data_021,
            R.drawable.data_022,
            R.drawable.data_023,
            R.drawable.data_024,
            R.drawable.data_025,
            R.drawable.data_026,
            R.drawable.data_027,
            R.drawable.data_029,
            R.drawable.data_030,
            R.drawable.data_031,
            R.drawable.data_032,
            R.drawable.data_033,
            R.drawable.data_034,
            R.drawable.data_035,
            R.drawable.data_036,
            R.drawable.data_037,
            R.drawable.data_038,
            R.drawable.data_039,
            R.drawable.data_040,
            R.drawable.data_041,
            R.drawable.data_042,
            R.drawable.data_043,
            R.drawable.data_044,
            R.drawable.data_045,
            R.drawable.data_046,
            R.drawable.data_047,
            R.drawable.data_048,
            R.drawable.data_049,
            R.drawable.data_050,
            R.drawable.data_051,
    };

    private static final long SPLASH_DURATION = 1700; // Duration for splash screen
    private static final long ANIMATION_INTERVAL = 50; // Interval for animation frames
    private CountDownTimer timer;
    private boolean isDialogShown = false;
    Context context;
    private static final String TAG = "DEBUG_SplashScreen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        // Initialize the animation view
        ImageView animationView = findViewById(R.id.animationview);





        // Start the animation
        startAnimation(animationView);
    }

    private void startAnimation(ImageView animationView) {
        Log.d(TAG, "Animation started"); // Add this log
        currentImageIndex = 0;

        // Animation logic
        timer = new CountDownTimer(SPLASH_DURATION, ANIMATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                animationView.setBackgroundResource(imageResources[currentImageIndex]);
//                Log.d(TAG, "Current image index: " + currentImageIndex); // Log current index
                currentImageIndex = (currentImageIndex + 1) % imageResources.length;
            }

            @Override
            public void onFinish() {
//                Log.d(TAG, "Animation finished"); // Log when finished
                checkPermissionsAndProceed();
            }
        }.start();
    }


    private void checkPermissionsAndProceed() {
        AskPerm askPerm = new AskPerm(SplashScreen.this);
        if (askPerm.checkPermission()) {
            startMainActivity(); // Permission granted, start MainActivity
        } else {
            // Show the permission dialog only if not finishing
            if (!isFinishing()) {
                showPermissionDialog(askPerm);
            }
        }
    }

    private void showPermissionDialog(AskPerm askPerm) {
        if (!isFinishing() && !isDialogShown) {
            isDialogShown = true; // Set flag to true
            askPerm.showCustomDialog(new AskPerm.PermissionDialogListener() {
                @Override
                public void onPermissionGranted() {
                    //asking for permission to avoid asking 2 permissions at once

                    startMainActivity();
                }

                @Override
                public void onPermissionDenied() {
                    // Keep the splash screen open if permission is denied
                    isDialogShown = false; // Reset flag when dialog is closed
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Check if permissions are granted when returning from settings
        AskPerm askPerm = new AskPerm(SplashScreen.this);

        if (askPerm.checkPermission()) {
            startMainActivity(); // Permission granted, start MainActivity
        } else {
            // Show permission dialog again if not granted
            showPermissionDialog(askPerm);

            // Avoid restarting animation if already running
            if (currentImageIndex == 0) {
                ImageView animationView = findViewById(R.id.animationview);
                startAnimation(animationView);
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel(); // Cancel the timer to prevent leaks
        }
        isDialogShown = false; // Reset dialog shown flag
    }



    private void showUserValueDialog() {
        if (isDialogShown || isFinishing()) return; // Check if dialog is already shown or activity is finishing

        isDialogShown = true; // Set flag to true
        // Create a Dialog instance
        Dialog dialog = new Dialog(this);

        // Set the custom layout
        dialog.setContentView(R.layout.get_current_data_usage);

        // Initialize views

        EditText input = (EditText) dialog.findViewById(R.id.TextInputField);
        Button buttonSettings = (Button) dialog.findViewById(R.id.btn_to_settings);
        Button buttonConfirm = (Button) dialog.findViewById(R.id.btn_confirm);

        // Set a click listener on the settings button
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Click recognized");
                Intent intent = new Intent(android.provider.Settings.ACTION_DATA_USAGE_SETTINGS);

                SplashScreen.this.startActivity(intent);
            }
        });
        // Set a click listener on the confirm button
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInputtedValue = String.valueOf(input.getText());
                SharedPreferences prefs = SplashScreen.this.getSharedPreferences("DataTrackingPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                DataTrack dataTrack = new DataTrack(getApplicationContext());
                float totalDataUsage = dataTrack.getTotalDataUsage();
                float totalDataUsageBeforeTracking = (float) ((Float.parseFloat(userInputtedValue) * 1000) - totalDataUsage);
                Log.d(TAG, "totalDataUsageBeforeTracking: " + ((Float.parseFloat(userInputtedValue) * 1000) - totalDataUsage));

                try {
                    editor.putFloat("userInputDataUsage", Float.parseFloat(userInputtedValue)); // For a String value
                    editor.putFloat("totalDataUsageBeforeTracking", totalDataUsageBeforeTracking); // For a String value
                    editor.apply();
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
                startMainActivity();
            }
        });
        dialog.setOnDismissListener(dialogInterface -> isDialogShown = false); // Reset flag when dialog is dismissed
        // Show the dialog
        dialog.show();
    }

    private void startMainActivity() {
        SharedPreferences prefs = getSharedPreferences("DataTrackingPrefs", MODE_PRIVATE);
        float storedDataUsage = prefs.getFloat("userInputDataUsage", 0);

        // Use a handler to delay the transition to MainActivity
        Handler handler = new Handler();

        if (storedDataUsage == 0) {
            showUserValueDialog();
        } else {
            // Delay the transition for 1.7 seconds (or however long your animation lasts)
            handler.postDelayed(() -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish(); // Close SplashScreen
            }, SPLASH_DURATION - 600); // Change to your splash duration
        }
    }



}

