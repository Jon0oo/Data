package com.appdev.data;

import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


public class SplashScreen extends AppCompatActivity {

    private AnimatedVectorDrawable animation;

    private int currentImageIndex = 1;
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
            R.drawable.data_045,
            R.drawable.data_046,
            R.drawable.data_047,
            R.drawable.data_048,
            R.drawable.data_049,
            R.drawable.data_050,
            R.drawable.data_051,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Objects.requireNonNull(getSupportActionBar()).hide();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);


        ImageView animationview = (ImageView) findViewById(R.id.animationview);


        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Update the ImageView with the next image


                animationview.setBackgroundResource(imageResources[currentImageIndex]);

                currentImageIndex = (currentImageIndex + 1) % imageResources.length;

                // Repeat every second
                handler.postDelayed(this, 70);
            }

        };
        // Start the image switching loop
        handler.post(runnable);






        Handler splashHandleractivity = new Handler();
// Delay for 2 seconds (2000 milliseconds)
        splashHandleractivity.postDelayed(() -> {
            // Start the main activity
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the splash screen activity
        }, 1600); // Adjust the delay as needed
    }

}



