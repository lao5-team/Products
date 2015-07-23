package com.pineapple.mobilecraft.tumcca.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.Toast;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.viewpagerindicator.TabPageIndicator;

import java.util.List;

/**
 * Created by yihao on 15/6/15.
 */
public class UserActivity extends FragmentActivity {
    private static final int WORKS_WIDTH = 400;
    private static final int PAGE_SIZE = 5;
    UserAlbumsFragment mUserAlbumsFragment;
    WorksListFragment mLikeCalligraphyFragment;
    boolean mIsTestMode = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        final ActionBar actionBar = getActionBar();
        if(null!=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mUserAlbumsFragment = new UserAlbumsFragment();
        mLikeCalligraphyFragment = new WorksListFragment();
        if(mIsTestMode) {
            UserManager.getInstance().login("999", "999");
        }
        int userId = UserManager.getInstance().getCurrentUserId();
        if(-1 == userId){
            Toast.makeText(this, "不存在此用户", Toast.LENGTH_SHORT).show();
            finish();
        }
        mUserAlbumsFragment.setUser(userId);
        final ActionBar mActionBar = getActionBar();
        setContentView(R.layout.activity_user);
        addTabView();

//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<Album> albumList = WorksServer.getMyAlbumList(UserManager.getInstance().getCurrentToken());
//                albumList.add(0, Album.DEFAULT_ALBUM);
//                for(Album album:albumList){
//                    List<WorksInfo> worksInfoList = WorksManager.getInstance().getAlbumWorks(album.id);
//                    if(worksInfoList.size()>0){
//                        album.sampleImageId = worksInfoList.get(0).picInfo.id;
//                    }
//                    worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(), album.id, PAGE_COUNT, PAGE_SIZE, WIDTH);
//                    album.worksInfoList = worksInfoList;
//
//
//                    WorksManager.getInstance().putAlbumWorks(album.id, worksInfoList);
//                }
//            }
//        });

    }

    public static void startActivity(Activity activity){
        activity.startActivity(new Intent(activity, UserActivity.class));

    }

    public void addTabView(){
        TabPageIndicator tabPageIndicator = (TabPageIndicator)findViewById(R.id.view_tab);
        ViewPager viewPager = (ViewPager)findViewById(R.id.view_viewPager);

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                if(i == 0){
                    return mUserAlbumsFragment;
                }
                else if(i == 2){
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<WorksInfo> worksInfoList = WorksServer.getLikeWorks(
                                    UserManager.getInstance().getCurrentToken(), 1, PAGE_SIZE, WORKS_WIDTH);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLikeCalligraphyFragment.addWorksHead(worksInfoList);
                                }
                            });
                        }
                    });
                    t.start();

                    return mLikeCalligraphyFragment;
                }
                else{
                    return TestFragment.newInstance(getResources().getStringArray(R.array.user_activity_tabs)[i]);
                }
            }

            @Override
            public int getCount() {
                return 6;
            }

            @Override
            public CharSequence getPageTitle(int position) {
               return getResources().getStringArray(R.array.user_activity_tabs)[position];
            }
        });

        tabPageIndicator.setViewPager(viewPager);
    }

    public void addUserView(){

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            //case R.id.account_settings:
                //UserInfoActivity.startActivity(UserActivity.this);
            //    break;
            // 其他省略...
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}