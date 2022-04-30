package com.example.himalaya;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * 启动动画页面
 */
public class LaunchActivity extends Activity {
    private static int SPLASH_TIME_OUT = 5000;

    //Hooks
    View first,second,third,fourth,fifth,sixth;
    TextView a,slogan;

    //Animations
    Animation topAnimation,bottomAnimation,middleAnimation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);

        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        middleAnimation = AnimationUtils.loadAnimation(this,R.anim.middle_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //hooks
        first = findViewById(R.id.first_line);
        second = findViewById(R.id.third_line);
        third = findViewById(R.id.third_line);
        fourth = findViewById(R.id.fourth_line);
        fifth = findViewById(R.id.fifth_line);
        sixth = findViewById(R.id.sixth_line);

        a = findViewById(R.id.h);
        slogan = findViewById(R.id.tagLine);

        first.setAnimation(topAnimation);
        second.setAnimation(topAnimation);
        third.setAnimation(topAnimation);
        fourth.setAnimation(topAnimation);
        fifth.setAnimation(topAnimation);
        sixth.setAnimation(topAnimation);

        a.setAnimation(middleAnimation);
        slogan.setAnimation(bottomAnimation);

        //splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        },SPLASH_TIME_OUT);
    }
}
