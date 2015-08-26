package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photoselector.model.PhotoModel;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.fragment.WorkCreateFragment;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 8/18/15.
 */
public class WorksCreateActivity2 extends FragmentActivity {
    public static final int REQ_PIC_EDIT =1;

    /**
     * 图片列表Fragment
     */
    private WorkCreateFragment mWorkCreateFragment;
    private TumccaService mService;
    private ServiceConnection mServiceConnection;
    private Album mAlbum = Album.DEFAULT_ALBUM;
    private TextView mTvAlbumTitle;
    private ImageView mIvAlbumCover;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        final ActionBar actionBar = getActionBar();
        if(null!=actionBar){
            addActionbar(actionBar);
        }

        setContentView(R.layout.activity_works_create2);

        List<PhotoModel> photos = new Gson().fromJson(getIntent().getStringExtra("photos"), new TypeToken<List<PhotoModel>>(){}.getType());
        if(photos!=null&&!photos.isEmpty()){
            addWorkView(photos);
        }

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((TumccaService.LocalService)service).getService();
                mWorkCreateFragment.setService(mService);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, TumccaService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //更新编辑过的图片
        if (requestCode == REQ_PIC_EDIT && resultCode == RESULT_OK) {

            try {
                JSONArray jsonArray = new JSONArray(data.getStringExtra("pictureList"));
                List<Picture> pictures = new ArrayList<Picture>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    pictures.add(Picture.fromJSON(jsonArray.getJSONObject(i)));
                }
                mWorkCreateFragment.updatePicture(pictures);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addActionbar(ActionBar actionBar){
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

        Button sumbButton = (Button)customActionBarView.findViewById(R.id.button_submit);
        sumbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorkCreateFragment.submitWorks();
            }
        });
    }

    /**
     * 添加图片视图
     */
    private void addWorkView(List<PhotoModel> photos){
        List<Picture> pictures = new ArrayList<Picture>();
        for(PhotoModel photo:photos){
            //urls.add(photo.getOriginalPath());
            Picture picture = new Picture(pictures.size(), null, photo.getOriginalPath());
            pictures.add(picture);
        }
        mWorkCreateFragment = new WorkCreateFragment();
        mWorkCreateFragment.addPictures(pictures);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, mWorkCreateFragment).commit();
    }




}