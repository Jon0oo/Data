package com.appdev.data;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
        // Reset the current image index for new animation
        currentImageIndex = 0;

        // Animation logic
        CountDownTimer timer = new CountDownTimer(SPLASH_DURATION, ANIMATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                animationView.setBackgroundResource(imageResources[currentImageIndex]);
                currentImageIndex = (currentImageIndex + 1) % imageResources.length;
            }

            @Override
            public void onFinish() {
                // After the animation duration, check for permissions
                checkPermissionsAndProceed();
            }
        }.start();
    }

    private void checkPermissionsAndProceed() {
        AskPerm askPerm = new AskPerm(SplashScreen.this);
        if (askPerm.checkPermission()) {
            startMainActivity(); // Permission granted, start MainActivity
        } else {
            // Show the permission dialog
            showPermissionDialog(askPerm);
        }
    }

    private void showPermissionDialog(AskPerm askPerm) {
        askPerm.showCustomDialog(new AskPerm.PermissionDialogListener() {
            @Override
            public void onPermissionGranted() {
                startMainActivity();
            }

            @Override
            public void onPermissionDenied() {
                // Keep the splash screen open if permission is denied
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the splash screen shows for 2 seconds every time
        ImageView animationView = findViewById(R.id.animationview);
        startAnimation(animationView); // Restart animation regardless of permission
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Close SplashScreen
    }
}
