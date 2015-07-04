package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;

import java.util.List;

/**
 * Created by yihao on 15/7/4.
 */
public class CalligraphyListActivity extends FragmentActivity {

    public static void startActivity(Activity activity, int id){
        Intent intent = new Intent(activity, CalligraphyListActivity.class);
        intent.putExtra("id", id);
        activity.startActivity(intent);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<WorksInfo> worksInfoList = WorksManager.getInstance().getAlbumWorks(getIntent().getIntExtra("id", -1));
        setContentView(R.layout.activity_calligraphy_list);
        View view = findViewById(R.id.view);

        CalligraphyListFragment fragment = new CalligraphyListFragment();
        fragment.setWorksList(worksInfoList);
        getFragmentManager().beginTransaction().add(R.id.view, fragment).commit();

    }
}