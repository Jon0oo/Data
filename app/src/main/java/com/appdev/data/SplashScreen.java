package com.appdev.data;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import java.util.Objects;


public class SplashScreen extends AppCompatActivity {
    private ImageView animationview;
    private AnimatedVectorDrawable animation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Objects.requireNonNull(getSupportActionBar()).hide();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        Button testbutton = (Button) findViewById(R.id.testbutton);
        TextView testtext = (TextView) findViewById(R.id.testtext);
        animationview = (ImageView) findViewById(R.id.animationview);


        // Drawable d = getDrawable(R.drawable.logov3);
        //if (d instanceof AnimatedVectorDrawable) {
        //  AnimatedVectorDrawable avd  = (AnimatedVectorDrawable) d;

        //  testtext.setText("1");
        //  avd.start();

        // }

    }

        @Override
        protected void onStart() {
            super.onStart();

            Drawable d = animationview.getDrawable();
            if (d instanceof AnimatedVectorDrawable) {
                AnimatedVectorDrawable anim = (AnimatedVectorDrawable) d;
                anim.start();
                anim.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        anim.start();
                    }
                });
            }
        Handler splashHandler = new Handler();
// Delay for 2 seconds (2000 milliseconds)
        splashHandler.postDelayed(() -> {
            // Start the main activity
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the splash screen activity
        }, 2000); // Adjust the delay as needed
    }

}



