package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photoselector.model.PhotoModel;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.Constants;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.fragment.FacebookLikedWorksCreateFragment;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import com.pineapple.mobilecraft.tumcca.utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * facebook 风格的作品创建页面 http://pan.baidu.com/s/1sjoTI5R
 * Created by yihao on 8/18/15.
 */
public class FacebookLikedWorksCreateActivity extends FragmentActivity {
    public static final int REQ_PIC_EDIT = 1;

    /**
     * 图片列表Fragment
     */
    private FacebookLikedWorksCreateFragment mWorkCreateFragment;
    private TumccaService mService;
    private ServiceConnection mServiceConnection;
    private Uri mPhotoUri;
    public static void startActivity(Activity activity, List<Picture> pictures) {
        Intent intent = new Intent(activity, FacebookLikedWorksCreateActivity.class);
        intent.putExtra("pictures", new Gson().toJson(pictures));
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        final ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            addActionbar(actionBar);
        }

        setContentView(R.layout.activity_works_create2);

        //添加作品创建Fragment
        List<Picture> photos = new Gson().fromJson(getIntent().getStringExtra("pictures"), new TypeToken<List<Picture>>() {
        }.getType());
        Log.v(TumccaApplication.TAG, "onCreate");
        if (photos != null && !photos.isEmpty()) {
            addWorkView(photos);
        }

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((TumccaService.LocalService) service).getService();
                mWorkCreateFragment.bindTumccaService(mService);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, TumccaService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
//        if(null!=savedInstanceState){
//            mPhotoUri = savedInstanceState.getParcelable("photo_uri");
//
//        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //更新编辑过的图片
        if (requestCode == REQ_PIC_EDIT && resultCode == RESULT_OK) {
            List<Picture> pictures = new Gson().fromJson(data.getStringExtra("pictureList"),
                    new TypeToken<List<Picture>>() {
                    }.getType());
            if (null != pictures) {
                mWorkCreateFragment.updatePicture(pictures);
            }
        }
        if (requestCode == PhotoChoose.FROMCAMERA&&resultCode==RESULT_OK) {// selected image
            mPhotoUri = mWorkCreateFragment.getPhotoUri();
            String outPath = Utility.processImage(mPhotoUri.getPath(), Constants.MAX_PICTURE_WIDTH, Constants.MAX_PICTURE_HEIGHT, 0.0f, true);
            ArrayList<Picture> photoItems = new ArrayList<Picture>();
            photoItems.add(new Picture(null, outPath));
            mWorkCreateFragment.addPictures(photoItems);
        }
        if (requestCode == PhotoChoose.FROMGALLERY) {// selected image
            if (data != null && data.getStringExtra("photos") != null) {
                List<PhotoModel> photos = new Gson().fromJson(data.getStringExtra("photos"), new TypeToken<List<PhotoModel>>() {
                }.getType());
                List<Picture> pictures = new ArrayList<Picture>();
                for (PhotoModel model : photos) {
                    pictures.add(new Picture(null, model.getOriginalPath()));
                }
                mWorkCreateFragment.addPictures(pictures);
            }
        }
    }

    private void addActionbar(ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);

        View customActionBarView = getLayoutInflater().inflate(R.layout.actionbar_calligrahy_create, null);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.END;
        actionBar.setCustomView(customActionBarView, lp);

        //发布按钮事件绑定
        Button sumbButton = (Button) customActionBarView.findViewById(R.id.button_submit);
        sumbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorkCreateFragment.submitWorks();
            }
        });
    }

    /**
     * 添加作品Fragment
     */
    private void addWorkView(List<Picture> pictures) {
        Log.v(TumccaApplication.TAG, "addWorkView");
        if (null != pictures) {
            if((mWorkCreateFragment = (FacebookLikedWorksCreateFragment) getSupportFragmentManager().findFragmentByTag("works_create")) == null){
                mWorkCreateFragment = new FacebookLikedWorksCreateFragment();
                mWorkCreateFragment.setPictures(pictures);
                Log.v(TumccaApplication.TAG, "new Fragment");
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, mWorkCreateFragment, "works_create").commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        //getSupportFragmentManager().beginTransaction().remove(mWorkCreateFragment).commit();
//
//        outState.putParcelable("photo_uri", mPhotoUri = mWorkCreateFragment.getPhotoUri());
//
//        super.onSaveInstanceState(outState);
//    }

//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//
//        super.onRestoreInstanceState(savedInstanceState);
//        if(null!=savedInstanceState) {
//
//            mPhotoUri = savedInstanceState.getParcelable("photo_uri");
//        }
//        getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, mWorkCreateFragment).commit();
//
//
//    }

    @Override
    public void onDestroy(){
        Log.v(TumccaApplication.TAG, "onDestroy");
        unbindService(mServiceConnection);
        getSupportFragmentManager().beginTransaction().remove(mWorkCreateFragment).commitAllowingStateLoss();
        super.onDestroy();
     }

    @Override
    public void onResume(){
        super.onResume();
        //getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, mWorkCreateFragment).commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

}