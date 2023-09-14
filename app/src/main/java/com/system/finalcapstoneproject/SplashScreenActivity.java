package com.system.finalcapstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000; //3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logoImageView = findViewById(R.id.logo_container);


        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1500); // 1.5 seconds
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent homeIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });


        logoImageView.setAnimation(fadeInAnimation);
    }
}
