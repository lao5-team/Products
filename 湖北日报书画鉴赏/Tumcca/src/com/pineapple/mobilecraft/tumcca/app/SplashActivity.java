package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;

/**
 * Created by yihao on 15/5/20.
 */
public class SplashActivity extends Activity {
    int mCurrentIndex = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();
        final ImageView imageView = (ImageView)findViewById(R.id.imageView_splash);
        imageView.setImageResource(R.drawable.splash_1);
        imageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.splash));
        final int imageIds[] = {R.drawable.splash_1, R.drawable.splash_2,R.drawable.splash_3, R.drawable.splash_4};

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(imageIds[++mCurrentIndex%imageIds.length]);
                imageView.setAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash));
            }
        });

        TextView tv_skip = (TextView)findViewById(R.id.textView_skip);
        tv_skip.setClickable(true);
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }
}