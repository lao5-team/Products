package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.fragment.AlbumListFragment;
import com.pineapple.mobilecraft.tumcca.fragment.WorkListFragment;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 15/7/4.
 *
 * 用于 See All功能
 */
public class WorksListActivity extends FragmentActivity {

    private TextView mTvTitle;
    private long mId;

    public static final int MODE_LIKE = 0;
    public static final int MODE_COLLECT = 1;

    private int mDataMode = MODE_LIKE;
    private WorkListFragment mWorksFragment;
    private int mAuthorId = 0;
//    public static void startActivity(Activity activity, long id, long authorId, String albumName){
//        Intent intent = new Intent(activity, WorksListActivity.class);
//        intent.putExtra("id", id);
//        intent.putExtra("author", authorId);
//        intent.putExtra("albumName", albumName);
//        activity.startActivity(intent);
//
//    }

    public static void startActivity(Activity activity, int mode, int authorId){
        Intent intent = new Intent(activity, WorksListActivity.class);
        intent.putExtra("data_mode", mode);
        intent.putExtra("authorId", authorId);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);



//        final ActionBar actionBar = getActionBar();
//        if(null!=actionBar){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle(getIntent().getStringExtra("albumName"));
//        }
        //final List<WorksInfo> worksInfoList = WorksManager.getInstance().getAlbumWorks(getIntent().getLongExtra("id", -1));
        setContentView(R.layout.activity_album_calligraphy_list);
        mDataMode = getIntent().getIntExtra("data_mode", MODE_COLLECT);
        mAuthorId = getIntent().getIntExtra("authorId", 0);

        mWorksFragment = new WorkListFragment();
        mWorksFragment.setWorksLoader(new WorkListFragment.WorkListLoader() {
            @Override
            public List<WorksInfo> getInitialWorks() {
                List<WorksInfo> worksInfoList = new ArrayList<WorksInfo>();
                if(mDataMode == MODE_LIKE){
                    worksInfoList = WorksServer.getLikeWorks(mAuthorId, 1, 5, 400);
                }
                else if(mDataMode == MODE_COLLECT){
                    worksInfoList = WorksServer.getCollectWorks(mAuthorId, 1, 5, 400);
                }
                return worksInfoList;
            }

            @Override
            public void loadHeadWorks() {
                List<WorksInfo> worksInfoList = new ArrayList<WorksInfo>();;
                if(mDataMode == MODE_LIKE){
                    worksInfoList = WorksServer.getLikeWorks(mAuthorId, 1, 5, 400);
                }
                else if(mDataMode == MODE_COLLECT){
                    worksInfoList = WorksServer.getCollectWorks(mAuthorId, 1, 5, 400);
                }
                mWorksFragment.addWorksHead(worksInfoList);
            }

            @Override
            public void loadTailWorks(int page) {
                List<WorksInfo> worksInfoList = new ArrayList<WorksInfo>();;
                if(mDataMode == MODE_LIKE){
                    worksInfoList = WorksServer.getLikeWorks(mAuthorId, 1, page, 400);
                }
                else if(mDataMode == MODE_COLLECT){
                    worksInfoList = WorksServer.getCollectWorks(mAuthorId, 1, page, 400);
                }
                mWorksFragment.addWorksHead(worksInfoList);
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.layout_container, mWorksFragment).commit();

    }

}