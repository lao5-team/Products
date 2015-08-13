package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.fragment.AlbumListFragment;
import com.pineapple.mobilecraft.tumcca.fragment.AlbumWorkListFragment;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.fragment.UserListFragment;
import com.pineapple.mobilecraft.tumcca.fragment.WorkListFragment;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.util.logic.Util;
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
    ImageView mIvAvatar;
    TextView mTvFollow;
    TextView mTvPseudonym;
    DisplayImageOptions mImageOptions;
    private RelativeLayout mLayoutProfile;

    private static final int REQ_USERINFO = 2;

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

//            actionBar.setDisplayOptions(
//                    ActionBar.DISPLAY_SHOW_CUSTOM,
//                    ActionBar.DISPLAY_SHOW_CUSTOM);
//            View customActionBarView = getLayoutInflater().inflate(R.layout.actionbar_user, null);
//            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT);
//            lp.gravity = Gravity.END;
//            actionBar.setCustomView(customActionBarView, lp);



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
        mFollowingFragment.setUserMode(UserListFragment.MODE_FOLLOWING);
        mFollowingFragment.setUserId((long)mAuthorId);

        if (mIsTestMode) {
            UserManager.getInstance().login("999", "999");
        }

        //mUserAlbumsFragment.setUser(mAuthorId);
        setContentView(R.layout.activity_user);

        addUserView();

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

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
        mImageOptions  = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(Util.dip2px(TumccaApplication.applicationContext, 24))).
                        cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        mIvAvatar = (ImageView)findViewById(R.id.imageView_avatar);
        imageLoader.displayImage("drawable://" + R.drawable.default_avatar, mIvAvatar, mImageOptions);
        mTvPseudonym = (TextView)findViewById(R.id.textView_user);
        mTvFollow = (TextView)findViewById(R.id.textView_follow);
        mLayoutProfile = (RelativeLayout)findViewById(R.id.layout_profile);

        bindUserActions();
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
                        fragment.addAlbumsHead(albumList);
                    }
                });
            }

            @Override
            public void loadTailAlbums(int page) {
                List<Album> albumList = WorksServer.getAuthorAlbumList(mAuthorId);
                fragment.addAlbumsHead(albumList);
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
            public void loadTailWorks(final int page) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final List<WorksInfo> worksInfoList = WorksServer.getCollectWorks(
                                mAuthorId, page, PAGE_SIZE, WORKS_WIDTH);
                        workListFragment.addWorksHead(worksInfoList);
                    }
                });

            }
        });

        fragment.addWorksFragment(workListFragment);

        final AlbumListFragment albumListFragment = new AlbumListFragment();
        albumListFragment.setAlbumLoader(new AlbumListFragment.AlbumListLoader() {
            @Override
            public List<Album> getInitialAlbums() {
                return null;
            }

            @Override
            public void loadHeadAlbums() {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final List<Album> albumList = WorksServer.getLikeAlbums(
                                mAuthorId, 1, PAGE_SIZE);
                        albumListFragment.addAlbumsHead(albumList);

                    }
                });
            }

            @Override
            public void loadTailAlbums(int page) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final List<Album> albumList = WorksServer.getLikeAlbums(
                                mAuthorId, 1, PAGE_SIZE);
                        albumListFragment.addAlbumsTail(albumList);
                    }
                });
            }
        });

        fragment.addAlbumsFragment(albumListFragment);
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
            public void loadTailWorks(final int page) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final List<WorksInfo> worksInfoList = WorksServer.getCollectWorks(
                                mAuthorId, page, PAGE_SIZE, WORKS_WIDTH);
                        workListFragment.addWorksHead(worksInfoList);
                    }
                });
            }
        });
        fragment.addWorksFragment(workListFragment);

        final AlbumListFragment albumListFragment = new AlbumListFragment();
        albumListFragment.setAlbumLoader(new AlbumListFragment.AlbumListLoader() {
            @Override
            public List<Album> getInitialAlbums() {
                return null;
            }

            @Override
            public void loadHeadAlbums() {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final List<Album> albumList = WorksServer.getCollectAlbums(
                                mAuthorId, 1, PAGE_SIZE);
                        albumListFragment.addAlbumsHead(albumList);

                    }
                });
            }

            @Override
            public void loadTailAlbums(int page) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final List<Album> albumList = WorksServer.getCollectAlbums(
                                mAuthorId, 1, PAGE_SIZE);
                        albumListFragment.addAlbumsTail(albumList);
                    }
                });
            }
        });

        fragment.addAlbumsFragment(albumListFragment);
    }

    public void addFollowingFragment(UserListFragment fragment) {

    }

    public void addFollowerFragment(UserListFragment fragment) {

    }
    boolean mIsFollowed = false;
    private void bindUserActions(){
        mLayoutProfile.setClickable(true);
        mLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.startActivity(UserActivity.this, REQ_USERINFO);
            }
        });

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                final Profile profile = UserManager.getInstance().getUserProfile(mAuthorId);
                if (profile.avatar > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageLoader.getInstance().displayImage(UserServer.getInstance().getAvatarUrl(profile.avatar), mIvAvatar, mImageOptions);
                        }
                    });
                }
                mTvPseudonym.setText(profile.pseudonym);
                if(mAuthorId == UserManager.getInstance().getCurrentUserId()){
                    mTvFollow.setVisibility(View.GONE);
                }else{
                    Long[] ids = {new Long(mAuthorId)};
                    Boolean[] isFollowed = UserServer.getInstance().isUsersFollowed(UserManager.getInstance().getCurrentToken(null), ids);
                    if(isFollowed.length>0){
                        mIsFollowed = isFollowed[0];
                    }
                    if (mIsFollowed) {
                        mTvFollow.setText("取消关注");

                    } else {
                        mTvFollow.setText("关   注");
                    }
                }

            }
        });

        mTvFollow.setClickable(true);
        mTvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null==UserManager.getInstance().getCurrentToken(new UserManager.PostLoginTask() {
                    @Override
                    public void onLogin(String token) {

                        if (mIsFollowed) {
                            UserServer.getInstance().cancelfollowUser(UserManager.getInstance().getCurrentToken(null), mAuthorId);
                        } else {
                            UserServer.getInstance().followUser(UserManager.getInstance().getCurrentToken(null), UserManager.getInstance().getCurrentUserId(), mAuthorId);
                        }
                        mIsFollowed = !mIsFollowed;
                        if (mIsFollowed) {
                            mTvFollow.setText("取消关注");

                        } else {
                            mTvFollow.setText("关   注");
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                })){
                    UserManager.getInstance().requestLogin();
                }
            }
        });

    }


}