package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.Toast;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.fragment.AlbumListFragment;
import com.pineapple.mobilecraft.tumcca.fragment.AlbumWorkListFragment;
import com.pineapple.mobilecraft.app.UserListFragment;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.fragment.WorkListFragment;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.viewpagerindicator.TabPageIndicator;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 15/6/15.
 */
public class UserActivity extends FragmentActivity {
    private static final int WORKS_WIDTH = 400;
    private static final int PAGE_SIZE = 5;
    private static final int ALBUM_PAGE_SIZE = 20;
    AlbumListFragment mUserAlbumsFragment;
    //WorksListFragment mLikeCalligraphyFragment;
    AlbumWorkListFragment mLikesFragment;
    AlbumWorkListFragment mCollectFragment;
    UserListFragment mFollowingFragment;
    UserListFragment mFollowerFragment;
    boolean mIsTestMode = false;
    int mAuthorId = -1;

    public static void startActivity(Activity activity, int id) {
        Intent intent = new Intent(activity, UserActivity.class);
        intent.putExtra("authorId", id);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        final ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAuthorId = getIntent().getIntExtra("authorId", -1);
        if (-1 == mAuthorId) {
            Toast.makeText(this, "不存在此用户", Toast.LENGTH_SHORT).show();
            finish();
        }
        mUserAlbumsFragment = new AlbumListFragment();
        mLikesFragment = new AlbumWorkListFragment();
        mCollectFragment = new AlbumWorkListFragment();
        mFollowerFragment = new UserListFragment();
        mFollowingFragment = new UserListFragment();


        if (mIsTestMode) {
            UserManager.getInstance().login("999", "999");
        }

        //mUserAlbumsFragment.setUser(mAuthorId);
        setContentView(R.layout.activity_user);
        addTabView();

        addUserAlbumFragment(mUserAlbumsFragment);

        addLikesFragment(mLikesFragment);

        addCollectFragment(mCollectFragment);

        addFollowingFragment(mFollowingFragment);

        addFollowerFragment(mFollowerFragment);
    }



    public void addTabView() {
        TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.view_tab);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_viewPager);

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                if (i == 0) {
                    return mUserAlbumsFragment;
                } else if (i == 1) {
                    return mLikesFragment;
                } else if (i == 2) {
                    return mCollectFragment;
                } else if (i == 3) {
                    return mFollowingFragment;
                } else if (i == 4) {
                    return mFollowerFragment;
                } else {
                    return TestFragment.newInstance(getResources().getStringArray(R.array.user_activity_tabs)[i]);
                }
            }

            @Override
            public int getCount() {
                return getResources().getStringArray(R.array.user_activity_tabs).length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getResources().getStringArray(R.array.user_activity_tabs)[position];
            }
        });

        tabPageIndicator.setViewPager(viewPager);
    }

    public void addUserView() {

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

    public void addUserAlbumFragment(final AlbumListFragment fragment) {
        fragment.setAlbumLoader(new AlbumListFragment.AlbumListLoader() {
            @Override
            public List<Album> getInitialAlbums() {
                return null;
            }

            @Override
            public void loadHeadAlbums() {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        List<Album> albumList = WorksServer.getAuthorAlbumList(mAuthorId);
                        for (Album album : albumList) {
                            List<WorksInfo> worksInfoList = WorksManager.getInstance().getAlbumWorks(album.id);
                            if (worksInfoList.size() > 0) {
                                album.worksInfoList = worksInfoList;
                            } else {
                                worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(), album.id, 1, ALBUM_PAGE_SIZE, 400);
                                album.worksInfoList = worksInfoList;
                                WorksManager.getInstance().putAlbumWorks(album.id, worksInfoList);
                            }

                        }
                        fragment.addAlbumsHead(albumList);

                    }
                });
            }

            @Override
            public void loadTailAlbums() {

            }
        });
    }

    public void addLikesFragment(AlbumWorkListFragment fragment) {
        final WorkListFragment workListFragment = new WorkListFragment();


        workListFragment.setWorksLoader(new WorkListFragment.WorkListLoader() {
            @Override
            public List<WorksInfo> getInitialWorks() {
                return null;
            }

            @Override
            public void loadHeadWorks() {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final List<WorksInfo> worksInfoList = WorksServer.getLikeWorks(
                                mAuthorId, 1, PAGE_SIZE, WORKS_WIDTH);
                        workListFragment.addWorksHead(worksInfoList);

                    }
                });
            }

            @Override
            public void loadTailWorks() {

            }
        });

        fragment.addWorksFragment(workListFragment);
    }

    public void addCollectFragment(AlbumWorkListFragment fragment) {
        final WorkListFragment workListFragment = new WorkListFragment();

        workListFragment.setWorksLoader(new WorkListFragment.WorkListLoader() {
            @Override
            public List<WorksInfo> getInitialWorks() {
                return null;
            }

            @Override
            public void loadHeadWorks() {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final List<WorksInfo> worksInfoList = WorksServer.getCollectWorks(
                                mAuthorId, 1, PAGE_SIZE, WORKS_WIDTH);
                        workListFragment.addWorksHead(worksInfoList);
                    }
                });


            }

            @Override
            public void loadTailWorks() {

            }
        });
        fragment.addWorksFragment(workListFragment);
    }

    public void addFollowingFragment(UserListFragment fragment) {

    }

    public void addFollowerFragment(UserListFragment fragment) {

    }


}