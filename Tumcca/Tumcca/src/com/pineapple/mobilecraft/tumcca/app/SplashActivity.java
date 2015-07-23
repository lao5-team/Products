package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;

import java.util.List;

/**
 * Created by yihao on 15/5/20.
 */
public class SplashActivity extends Activity {
    int mCurrentIndex = 0;
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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //
                Looper.prepare();
                    String username = UserManager.getInstance().getCachedUsername();
                    String password = UserManager.getInstance().getCachedPassword();
                    if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)){
                        UserManager.getInstance().login(username, password);

                        List<Album> albumList = WorksServer.getMyAlbumList(UserManager.getInstance().getCurrentToken());
                        albumList.add(0, Album.DEFAULT_ALBUM);



                        for(Album album:albumList){
                            List<WorksInfo> worksInfoList;
                            worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(), album.id, 1, 20, 400);
                            album.worksInfoList = worksInfoList;
                            WorksManager.getInstance().putAlbumWorks(album.id, worksInfoList);
                        }
                        WorksManager.getInstance().setMyAlbumList(albumList);

                    }
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);



            }
        });
        t.start();
        final ImageView imageView = (ImageView)findViewById(R.id.imageView_splash);
        imageView.setImageResource(R.drawable.splash);
        imageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.splash));
//        final int imageIds[] = {R.drawable.splash_1, R.drawable.splash_2,R.drawable.splash_3, R.drawable.splash_4};
//
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageView.setImageResource(imageIds[++mCurrentIndex%imageIds.length]);
//                imageView.setAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash));
//            }
//        });

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