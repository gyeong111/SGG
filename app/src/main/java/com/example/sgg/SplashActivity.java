package com.example.sgg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    Animation anim_FadeIn;
    Animation anim_FadeOut;
    ConstraintLayout constraintLayout;
    ImageView icon1, icon2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        constraintLayout=findViewById(R.id.constraintLayout);
        icon1=findViewById(R.id.icon1);
        icon2=findViewById(R.id.icon2);


        anim_FadeIn= AnimationUtils.loadAnimation(this,R.anim.anim_splash_fadein);
        anim_FadeOut= AnimationUtils.loadAnimation(this,R.anim.anim_splash_icon);

        anim_FadeIn.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(SaveSharedPreference.getUserId(SplashActivity.this).length() == 0) {
                    // call Login Activity
                    Intent intent = new Intent(SplashActivity.this, First.class);
                    startActivity(intent);
                } else {
                    // Call Next Activity
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("key", SaveSharedPreference.getUserId(getApplicationContext()));
                    startActivity(intent);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        icon1.startAnimation(anim_FadeOut);
        icon2.startAnimation(anim_FadeIn);
    }
}