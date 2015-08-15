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
import com.pineapple.mobilecraft.tumcca.fragment.WorkListFragment;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 15/7/4.
 */
public class AlbumDetailActivity extends FragmentActivity {

    private TextView mTvTitle;
    private long mId;
    private long mAuthorId;
    public static void startActivity(Activity activity, long id, long authorId, String albumName){
        Intent intent = new Intent(activity, AlbumDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("author", authorId);
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
        //final List<WorksInfo> worksInfoList = WorksManager.getInstance().getAlbumWorks(getIntent().getLongExtra("id", -1));
        setContentView(R.layout.activity_album_calligraphy_list);


        final WorkListFragment fragment = new WorkListFragment();
        fragment.setWorksLoader(new WorkListFragment.WorkListLoader() {
            @Override
            public List<WorksInfo> getInitialWorks() {
                return null;
            }

            @Override
            public void loadHeadWorks() {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        List<WorksInfo> worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(null),
                                mId = getIntent().getLongExtra("id", -1), mAuthorId = getIntent().getLongExtra("author", -1), 1, 5, 400);
                        fragment.addWorksHead(worksInfoList);
                    }
                });


            }

            @Override
            public void loadTailWorks(final int page) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        List<WorksInfo> worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(null),
                                getIntent().getLongExtra("id", -1), getIntent().getLongExtra("author", -1), page, 5, 400);
                        if(worksInfoList.size()>0){
                            fragment.addWorksTail(worksInfoList);
                        }
                        else{
                            fragment.setEnd(true);
                        }


                    }
                });
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.layout_container, fragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.album_detail_menu, menu);
        if(mAuthorId!=UserManager.getInstance().getCurrentUserId()){
            menu.removeItem(R.id.menu_delete);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.menu_delete:
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("确定删除此专辑？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Executors.newSingleThreadExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                WorksServer.removeAlbum(UserManager.getInstance().getCurrentToken(null), mId);
                                Intent intent = new Intent();
                                intent.setAction("remove_album");
                                intent.putExtra("id", mId);
                                sendBroadcast(intent);
                                finish();
                            }
                        });
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                dialog.show();


        }
        return super.onOptionsItemSelected(item);
    }
}