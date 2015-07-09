package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;

import java.util.List;

/**
 * Created by yihao on 15/7/4.
 */
public class AlbumCalligraphyListActivity extends FragmentActivity {

    private ImageView mIvBack;
    private TextView mTvTitle;
    public static void startActivity(Activity activity, int id, String albumName){
        Intent intent = new Intent(activity, AlbumCalligraphyListActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("albumName", albumName);
        activity.startActivity(intent);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<WorksInfo> worksInfoList = WorksManager.getInstance().getAlbumWorks(getIntent().getIntExtra("id", -1));
        setContentView(R.layout.activity_album_calligraphy_list);
        mIvBack = (ImageView)findViewById(R.id.imageView_back);
        mTvTitle = (TextView)findViewById(R.id.textView_title);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTvTitle.setText(getIntent().getStringExtra("albumName"));
        CalligraphyListFragment fragment = new CalligraphyListFragment();
        fragment.setWorksList(worksInfoList);
        getFragmentManager().beginTransaction().add(R.id.layout_container, fragment).commit();

    }
}