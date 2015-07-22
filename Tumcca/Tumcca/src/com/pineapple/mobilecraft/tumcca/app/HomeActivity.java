package com.pineapple.mobilecraft.tumcca.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.mediator.IHome;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.util.logic.Util;

import java.util.List;

/**
 * Created by yihao on 15/6/4.
 */
public class HomeActivity extends FragmentActivity implements IHome {

    private static final int WORKS_WIDTH = 400;
    private static final int PAGE_SIZE = 5;

    private RelativeLayout mLayoutLogin;
    private RelativeLayout mLayoutProfile;
    private ImageView mIvAvatar;
    private TextView mTvPseudonym;

    private Button mBtnLogin = null;
    private Button mBtnRegister = null;
    private ImageView mIVAccount = null;
    private WorksListFragment mWorksListFragment;
    private int mCurrentPageIndex = 1;
    private Thread mDataThread = null;
    private Handler mDataHandler = null;
    private Object mLock = new Object();

    private boolean mIsLoadBottom = false;
    DisplayImageOptions mImageOptions;

    private Profile mProfile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageOptions = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(Util.dip2px(DemoApplication.applicationContext, 18)))
                .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565).build();

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            View customActionBarView = getLayoutInflater().inflate(R.layout.actionbar_home, null);
            addActionbarView(actionBar, customActionBarView);
        }


        setContentView(R.layout.activity_home);



        mWorksListFragment = new WorksListFragment();
        addWorkList(mWorksListFragment);

        mWorksListFragment.setScrollListener(new WorksListFragment.OnScrollListener() {

            @Override
            public void onTop(){
                final List<WorksInfo> worksInfoList = WorksServer.getWorksInHome(1, PAGE_SIZE, WORKS_WIDTH);
                if(null!=worksInfoList&&worksInfoList.size() >0 ){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mWorksListFragment.addWorksHead(worksInfoList);

                        }
                    });
                }
            }

            @Override
            public void onBottom() {

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mIsLoadBottom = true;
                        final List<WorksInfo> worksInfoList = WorksServer.getWorksInHome(mCurrentPageIndex, PAGE_SIZE, WORKS_WIDTH);
                        if (null != worksInfoList && worksInfoList.size() > 0) {
                            mCurrentPageIndex++;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mWorksListFragment.addWorksTail(worksInfoList);
                                }
                            });
                        }
                        mIsLoadBottom = false;
                    }
                });
                if (!mIsLoadBottom) {
                    Log.v("Tumcca", "onBottom");
                    t.start();
                }


            }
        });

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final List<WorksInfo> worksInfoList = WorksServer.getWorksInHome(1, PAGE_SIZE, WORKS_WIDTH);
                if(null!=worksInfoList&&worksInfoList.size() >0 ){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mWorksListFragment.addWorksHead(worksInfoList);

                        }
                    });
                }
            }
        }, new IntentFilter("upload_works"));

        int userId;
        if(-1!=(userId = UserManager.getInstance().getCurrentUserId())){
            mProfile = UserManager.getInstance().getUserProfile(userId);
            displayActionbar(1);
        }
        else{
            displayActionbar(0);
        }


    }

    public void addActionbarView(ActionBar actionBar, View view) {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.START;
        actionBar.setCustomView(view, lp);

        mLayoutProfile = (RelativeLayout) view.findViewById(R.id.layout_profile);
        mLayoutProfile.setClickable(true);
        mLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.startActivity(HomeActivity.this);
            }
        });
        mIvAvatar = (ImageView) view.findViewById(R.id.imageView_avatar);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage("drawable://" + R.drawable.icon, mIvAvatar, mImageOptions);
        mTvPseudonym = (TextView) view.findViewById(R.id.textView_user);
        mLayoutLogin = (RelativeLayout) view.findViewById(R.id.layout_login);
        mLayoutLogin.setClickable(true);
        mLayoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startActivity(HomeActivity.this);
            }
        });

    }

    /**
     * 0 login 1 profile
     */
    private void displayActionbar(int index){
        if(index==0){
            mLayoutProfile.setVisibility(View.GONE);
            mLayoutLogin.setVisibility(View.VISIBLE);
            mLayoutLogin.setClickable(true);
            mLayoutLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginActivity.startActivity(HomeActivity.this);
                }
            });
        }
        else if(index==1){
            mLayoutProfile.setVisibility(View.VISIBLE);
            mLayoutLogin.setVisibility(View.GONE);
            mProfile = UserManager.getInstance().getUserProfile(UserManager.getInstance().getCurrentUserId());
            if (mProfile.avatar > 0) {
                ImageLoader.getInstance().displayImage(UserServer.getInstance().getAvatarUrl(mProfile.avatar), mIvAvatar, mImageOptions);
            }
            mLayoutProfile.setClickable(true);
            mLayoutProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfoActivity.startActivity(HomeActivity.this);
                }
            });
            mTvPseudonym.setText(mProfile.pseudonym);
        }
    }

    @Override
    public void addHotTab() {

    }

    @Override
    public void addTrendsTab() {

    }

    @Override
    public void onTabSelect() {

    }

    @Override
    public void addAccountView() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_account);
        if(UserManager.getInstance().isLogin())
        {
            layout.setVisibility(View.GONE);
        }
        else
        {
            layout.setVisibility(View.GONE);
        }

        mBtnLogin = (Button) findViewById(R.id.button_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startActivity(HomeActivity.this);
            }
        });

        mBtnRegister = (Button) findViewById(R.id.button_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.startActivity(HomeActivity.this);
            }
        });

        mIVAccount = (ImageView) findViewById(R.id.imageView_account);
        mIVAccount.setClickable(true);
        mIVAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserActivity.startActivity(HomeActivity.this);

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LoginActivity.REQ_LOGIN && resultCode == RESULT_OK) {
            displayActionbar(1);

            List<Album> albumList = WorksServer.getMyAlbumList(UserManager.getInstance().getCurrentToken());
            albumList.add(0, Album.DEFAULT_ALBUM);

            for(Album album:albumList){
                List<WorksInfo> worksInfoList;
                worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(), album.id, 1, 20, 400);
                album.worksInfoList = worksInfoList;
                WorksManager.getInstance().putAlbumWorks(album.id, worksInfoList);
            }
            WorksManager.getInstance().setMyAlbumList(albumList);

        }
        if (requestCode == RegisterActivity.REQ_REGISTER && resultCode == RESULT_OK) {
            displayActionbar(1);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        /*
         * 将actionBar的HomeButtonEnabled设为ture，
         *
         * 将会执行此case
         */
            case R.id.account:
                UserActivity.startActivity(HomeActivity.this);
                break;
            case R.id.add:
                CalligraphyCreateActivity.startActivity(HomeActivity.this);
                break;
            // 其他省略...
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addWorkList(WorksListFragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.layout_works, mWorksListFragment).commit();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            View view = getLayoutInflater().inflate(R.layout.dialog_exit, null);
            AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //
                    System.exit(0);
                    //finish();
                }
            });
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        return false;
    }


}