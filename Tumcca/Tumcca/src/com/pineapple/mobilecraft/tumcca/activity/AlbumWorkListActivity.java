package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.fragment.WorkListFragment;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;

import java.util.List;

/**
 * Created by yihao on 15/7/4.
 */
public class AlbumWorkListActivity extends FragmentActivity {

    private TextView mTvTitle;
    public static void startActivity(Activity activity, long id, String albumName){
        Intent intent = new Intent(activity, AlbumWorkListActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("albumName", albumName);
        activity.startActivity(intent);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        final ActionBar actionBar = getActionBar();
        if(null!=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra("albumName"));
        }
        final List<WorksInfo> worksInfoList = WorksManager.getInstance().getAlbumWorks(getIntent().getIntExtra("id", -1));
        setContentView(R.layout.activity_album_calligraphy_list);


        WorkListFragment fragment = new WorkListFragment();
        fragment.setWorksLoader(new WorkListFragment.WorkListLoader() {
            @Override
            public List<WorksInfo> getInitialWorks() {
                return worksInfoList;
            }

            @Override
            public void loadHeadWorks() {

            }

            @Override
            public void loadTailWorks(int page) {

            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.layout_container, fragment).commit();

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
}