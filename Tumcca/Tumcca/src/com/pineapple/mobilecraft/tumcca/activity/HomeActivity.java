package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.photoselector.model.PhotoModel;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Constants;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.fragment.WorkListFragment;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.mediator.IHome;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import com.pineapple.mobilecraft.tumcca.utility.Utility;
import com.pineapple.mobilecraft.util.logic.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/6/4.
 */
public class HomeActivity extends FragmentActivity implements IHome {

    private static final int REQ_REG = 0;
    private static final int REQ_LOGIN = 1;
    private static final int REQ_USERINFO = 2;
    private static final int WORKS_WIDTH = 400;
    private static final int PAGE_SIZE = 5;

    private RelativeLayout mLayoutLogin;
    private RelativeLayout mLayoutProfile;
    private ImageView mIvAvatar;
    private TextView mTvPseudonym;

    private ImageView mIVAccount = null;
    private WorkListFragment mWorksListFragment;

    DisplayImageOptions mImageOptions;

    private Profile mProfile;
    private TumccaService mService = null;
    private Uri mUri;
    private ServiceConnection mServiceConntection;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null!=savedInstanceState){
            mUri = savedInstanceState.getParcelable("photo_uri");

        }
        mImageOptions = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(Util.dip2px(TumccaApplication.applicationContext, 18)))
                .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565).build();

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(0xffffff  ));
            View customActionBarView = getLayoutInflater().inflate(R.layout.actionbar_home, null);
            addActionbarView(actionBar, customActionBarView);

        }


        setContentView(R.layout.activity_home);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mService.loadHomeHeadList(new TumccaService.OnLoadFinished<WorksInfo>() {
                    @Override
                    public void onSuccess(List<WorksInfo> resultList) {
                        mWorksListFragment.addWorksHead(resultList);
                    }

                    @Override
                    public void onFail(String message) {

                    }
                });
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

        bindService(new Intent(this, TumccaService.class), mServiceConntection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((TumccaService.LocalService)service).getService();
                mWorksListFragment = new WorkListFragment();
                //Log.v("Tumcca", "Home size " + mService.getHomeWorkList().size());
                final List<WorksInfo> worksInfoList = mService.getHomeWorkList();
                mWorksListFragment.setWorksLoader(new WorkListFragment.WorkListLoader() {
                    @Override
                    public List<WorksInfo> getInitialWorks() {
                        return worksInfoList;
                    }

                    @Override
                    public void loadHeadWorks() {
                        mService.loadHomeHeadList(new TumccaService.OnLoadFinished<WorksInfo>() {
                            @Override
                            public void onSuccess(List<WorksInfo> resultList) {
                                int lengthAdded = mWorksListFragment.addWorksHead(resultList);
                                if(lengthAdded==0){
                                    //Toast.makeText(mContext, getString(R.string.there_is_no_new_works), Toast.LENGTH_SHORT).show();
                                    showMessage(getString(R.string.there_is_no_new_works));
                                }
                                else{
                                    //Toast.makeText(mContext, getString(R.string.there_is_works, worksInfoList.subList(0, index).size()), Toast.LENGTH_SHORT).show();
                                    showMessage(getString(R.string.there_is_works, lengthAdded));
                                }
                            }

                            @Override
                            public void onFail(String message) {

                            }
                        });
                    }

                    @Override
                    public void loadTailWorks(int page) {
                        mService.loadHomeTailList(new TumccaService.OnLoadFinished<WorksInfo>() {
                            @Override
                            public void onSuccess(List<WorksInfo> resultList) {
                                mWorksListFragment.addWorksTail(resultList);
                            }

                            @Override
                            public void onFail(String message) {

                            }
                        });
                    }
                });
                addWorkList(mWorksListFragment);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;

            }
        }, Context.BIND_AUTO_CREATE);

        //startWorkCreating();
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
                UserInfoActivity.startActivity(HomeActivity.this, UserManager.getInstance().getCurrentUserId(), REQ_USERINFO);
            }
        });
        mIvAvatar = (ImageView) view.findViewById(R.id.imageView_avatar);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage("drawable://" + R.drawable.default_avatar, mIvAvatar, mImageOptions);
        mTvPseudonym = (TextView) view.findViewById(R.id.textView_user);

        mLayoutLogin = (RelativeLayout)view.findViewById(R.id.layout_login);
        ImageButton btnLogin = (ImageButton) view.findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startActivity(HomeActivity.this, REQ_LOGIN);
            }
        });

    }

    /**
     * 0 sign_in 1 user_home
     */
    private void displayActionbar(int index){
        if(index==0){
            mLayoutProfile.setVisibility(View.GONE);
            mLayoutLogin.setVisibility(View.VISIBLE);
            mLayoutLogin.setClickable(true);
            mLayoutLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginActivity.startActivity(HomeActivity.this, REQ_LOGIN);
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
                    UserActivity.startActivity(HomeActivity.this, UserManager.getInstance().getCurrentUserId());
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
//        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_account);
//        if(UserManager.getInstance().isLogin())
//        {
//            layout.setVisibility(View.GONE);
//        }
//        else
//        {
//            layout.setVisibility(View.GONE);
//        }
//
//
//        mIVAccount = (ImageView) findViewById(R.id.imageView_account);
//        mIVAccount.setClickable(true);
//        mIVAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UserActivity.startActivity(HomeActivity.this);
//
//            }
//        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_LOGIN && resultCode == RESULT_OK) {

            displayActionbar(1);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Album> albumList = WorksServer.getMyAlbumList(UserManager.getInstance().getCurrentToken(null));
                    //albumList.add(0, Album.DEFAULT_ALBUM);

                    for(Album album:albumList){
                        List<WorksInfo> worksInfoList;
                        worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(null), album.id, UserManager.getInstance().getCurrentUserId(), 1, 20, 400);
                        album.worksInfoList = worksInfoList;
                        WorksManager.getInstance().putAlbumWorks(album.id, worksInfoList);
                    }
                    WorksManager.getInstance().setMyAlbumList(albumList);
                }
            });
            thread.start();


        }
        if(requestCode == REQ_USERINFO&&resultCode == UserInfoActivity.RESULT_LOGOUT){
            if(!UserManager.getInstance().isLogin()){
                displayActionbar(0);
            }
        }
        if (requestCode == PhotoChoose.FROMCAMERA&&resultCode==RESULT_OK) {// selected image
            String outPath = Utility.processImage(mUri.getPath(), Constants.MAX_PICTURE_WIDTH, Constants.MAX_PICTURE_HEIGHT, 0.0f, true);
            List<Picture> pictures = new ArrayList<Picture>();
            pictures.add(new Picture(null, outPath));
            FacebookLikedWorksCreateActivity.startActivity(this, pictures);
        }
        if (requestCode == PhotoChoose.FROMGALLERY) {// selected image
            if (data != null && data.getStringExtra("photos") != null) {
                List<PhotoModel> models = new Gson().fromJson(data.getStringExtra("photos"),
                        new TypeToken<List<PhotoModel>>(){}.getType());
                if(null != models){
                    List<Picture> pictures = new ArrayList<Picture>();
                    for(PhotoModel model:models){
                        pictures.add(new Picture(null, model.getOriginalPath()));
                    }
                    FacebookLikedWorksCreateActivity.startActivity(this, pictures);
                }
                else{
                    Toast.makeText(this, "图库返回结果异常", Toast.LENGTH_SHORT).show();
                }
            }
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
            case R.id.search:
                Intent intent = new Intent(this, WorksSearchActivity.class);
                startActivity(intent);
                break;
//            case R.id.account:
//                if(null==UserManager.getInstance().getCurrentToken(new UserManager.PostLoginTask() {
//                    @Override
//                    public void onLogin(String token) {
//                        UserActivity.startActivity(HomeActivity.this, UserManager.getInstance().getCurrentUserId());
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//
//                })){
//                    UserManager.getInstance().requestLogin();
//                }
//
//                break;
            case R.id.add:

                if(null==UserManager.getInstance().getCurrentToken(new UserManager.PostLoginTask() {
                    @Override
                    public void onLogin(String token) {
                        //WorksCreateActivity.startActivity(HomeActivity.this);
                        startWorkCreating();
                    }

                    @Override
                    public void onCancel() {

                    }
                }))
                {
                    UserManager.getInstance().requestLogin();
                }
                break;
            // 其他省略...
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addWorkList(WorkListFragment fragment) {
        if(null!=fragment){
            //if(getSupportFragmentManager().)
            //Log.v(TumccaApplication.TAG, (null==getSupportFragmentManager().findFragmentByTag("worksFragment"))+"");
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.layout_works, fragment, "worksFragment").commitAllowingStateLoss();
        }
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
                    if(null!=mService){
                        mService.quit();
                        mService = null;
                    }
                    System.exit(0);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    finish();
                    //android.os.Process.killProcess(android.os.Process.myPid());
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

    @Override
    public void finishActivityFromChild (Activity child, int requestCode){
        if(requestCode == REQ_USERINFO){
            if(!UserManager.getInstance().isLogin())
                displayActionbar(0);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        int userId;
        if(-1!=(userId = UserManager.getInstance().getCurrentUserId())){
            mProfile = UserManager.getInstance().getUserProfile(userId);
            displayActionbar(1);
        }
        else{
            displayActionbar(0);
        }

        addWorkList(mWorksListFragment);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(mServiceConntection);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("photo_uri", mUri);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(null!=savedInstanceState) {

            mUri = savedInstanceState.getParcelable("photo_uri");
        }
    }

    private void showMessage(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startWorkCreating(){
        PhotoChoose photoChoose = new PhotoChoose();
        mUri = Utility.createPhotoUri(this);//Uri.fromFile(new File(Utility.getTumccaImgPath(HomeActivity.this) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
        photoChoose.setUri(mUri);
        photoChoose.show(getSupportFragmentManager(), "WorksPhotoChoose");
    }


}