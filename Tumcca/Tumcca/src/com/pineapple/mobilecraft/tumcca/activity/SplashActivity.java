package com.pineapple.mobilecraft.tumcca.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;

/**
 * Created by yihao on 15/5/20.
 */
public class SplashActivity extends Activity {
    int mCurrentIndex = 0;
    TumccaService mService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String test = null;
        try{
            Log.v("Tumcca", test.toString());

        }
        catch (NullPointerException exp){
            exp.printStackTrace();
        }
        setContentView(R.layout.activity_splash);

        final ImageView imageView = (ImageView)findViewById(R.id.imageView_splash);
        imageView.setImageResource(R.drawable.splash);
        imageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.splash));

        TextView tv_skip = (TextView)findViewById(R.id.textView_skip);
        tv_skip.setClickable(true);
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        bindService(new Intent(this, TumccaService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((TumccaService.LocalService)service).getService();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        Looper.prepare();
                        mService.preloadApp();

                        try {
                            Thread.currentThread().sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);



                    }
                });
                t.start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;

            }
        }, Context.BIND_AUTO_CREATE);

    }
}