package com.appdev.data;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
            R.drawable.data_028,
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
            R.drawable.data_051
    };

    private static final long SPLASH_DURATION = 1700; // Duration for splash screen
    private static final long ANIMATION_INTERVAL = 50; // Interval for animation frames
    private CountDownTimer timer;
    private boolean isDialogShown = false;
    private static final String TAG = "DEBUG_SplashScreen";
    private boolean isMainActivityStarted = false; // Flag to track if MainActivity has been started

    // Member variable for the user value dialog reference
    private Dialog userValueDialog;

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
        Log.d(TAG, "Animation started");
        currentImageIndex = 0;

        timer = new CountDownTimer(SPLASH_DURATION, ANIMATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                animationView.setBackgroundResource(imageResources[currentImageIndex]);
                currentImageIndex = (currentImageIndex + 1) % imageResources.length;
            }

            @Override
            public void onFinish() {
                checkPermissionsAndProceed();
            }
        }.start();
    }

    private void checkPermissionsAndProceed() {
        // Prevent calling the method again if MainActivity is already started
        if (isMainActivityStarted) return;

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
            isDialogShown = true;
            askPerm.showCustomDialog(new AskPerm.PermissionDialogListener() {
                @Override
                public void onPermissionGranted() {
                    // Permission granted, start MainActivity
                    startMainActivity();
                }

                @Override
                public void onPermissionDenied() {
                    // Reset the flag when dialog is closed
                    isDialogShown = false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensure the MainActivity is not started again
        if (!isMainActivityStarted) {
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
    }

    @Override
    protected void onDestroy() {
        // Cancel timer to prevent leaks
        if (timer != null) {
            timer.cancel();
        }
        // Dismiss the dialog if it's still showing
        if (userValueDialog != null && userValueDialog.isShowing()) {
            userValueDialog.dismiss();
        }
        isDialogShown = false;
        super.onDestroy();
    }

    private void startMainActivity() {
        if (isMainActivityStarted) return; // Avoid multiple starts

        isMainActivityStarted = true; // Set flag to true

        SharedPreferences prefs = getSharedPreferences("DataTrackingPrefs", MODE_PRIVATE);
        float storedDataUsage = prefs.getFloat("userInputDataUsage", 0);

        Handler handler = new Handler();

        if (storedDataUsage == 0) {
            showUserValueDialog();
        } else {
            // Delay the transition to MainActivity
            handler.postDelayed(() -> {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                Log.d(TAG, "MainActivity started from startMainActivity Method");
                finish();
            }, SPLASH_DURATION - 600);
        }
    }

    // Method to show the user value dialog
    private void showUserValueDialog() {
        if (isDialogShown || isFinishing()) return;

        isDialogShown = true;
        userValueDialog = new Dialog(this);
        userValueDialog.setContentView(R.layout.get_current_data_usage);

        EditText input = userValueDialog.findViewById(R.id.TextInputField);
        Button buttonSettings = userValueDialog.findViewById(R.id.btn_to_settings);
        Button buttonConfirm = userValueDialog.findViewById(R.id.btn_confirm);

        buttonSettings.setOnClickListener(v -> {
            Log.d(TAG, "Click recognized");
            Intent intent = new Intent(android.provider.Settings.ACTION_DATA_USAGE_SETTINGS);
            SplashScreen.this.startActivity(intent);
        });

        buttonConfirm.setOnClickListener(v -> {
            String userInputtedValue = input.getText().toString();
            SharedPreferences prefs = SplashScreen.this.getSharedPreferences("DataTrackingPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            DataTrack dataTrack = new DataTrack(getApplicationContext());
            float totalDataUsage = dataTrack.getTotalDataUsage();
            float totalDataUsageBeforeTracking = (float) ((Float.parseFloat(userInputtedValue) * 1000) - totalDataUsage);

            try {
                editor.putFloat("userInputDataUsage", Float.parseFloat(userInputtedValue));
                editor.putFloat("totalDataUsageBeforeTracking", totalDataUsageBeforeTracking);
                editor.apply();
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }

            // Dismiss the dialog before starting MainActivity
            if (userValueDialog != null && userValueDialog.isShowing()) {
                userValueDialog.dismiss();
            }

            isMainActivityStarted = true;
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            Log.d(TAG, "MainActivity started from Button confirm user data from settings");
            finish();
        });

        userValueDialog.setOnDismissListener(dialogInterface -> isDialogShown = false);
        userValueDialog.show();
    }
}
