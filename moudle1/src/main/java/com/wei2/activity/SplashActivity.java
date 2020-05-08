package com.wei2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.RelativeLayout;

import com.wei2.bikenavi.R;
import com.wei2.utils.PrefUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        RelativeLayout rl_root = findViewById(R.id.rl_root);
        //旋转动画
//        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setDuration(1600);
        //缩放动画
//        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAnimation.setDuration(1000);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);

        AnimationSet set = new AnimationSet(true);
//        set.addAnimation(rotateAnimation);
//        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);
        rl_root.startAnimation(set);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               boolean isFirstEnter =  PrefUtils.getBoolean(SplashActivity.this, "is_first_enter", true);
               Intent intent;
                if (isFirstEnter) {
                    intent = new Intent(getApplicationContext(), GuideActivity.class);
                }else{
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
