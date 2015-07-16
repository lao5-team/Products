package com.pineapple.mobilecraft.tumcca.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IHome;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;

import java.util.List;

/**
 * Created by yihao on 15/6/4.
 */
public class HomeActivity extends FragmentActivity implements IHome {

    private static final int WORKS_WIDTH = 400;
    private static final int PAGE_SIZE = 5;

    private Button mBtnLogin = null;
    private Button mBtnRegister = null;
    private ImageView mIVAccount = null;
    private CalligraphyListFragment mWorksListFragment;
    private int mCurrentPageIndex = 1;
    private Thread mDataThread = null;
    private Handler mDataHandler = null;
    private Object mLock = new Object();

    private boolean mIsLoadBottom = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        //final ActionBar mActionBar = getActionBar();

        //mActionBar.setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_home);
        String username = UserManager.getInstance().getCachedUsername();
        String password = UserManager.getInstance().getCachedPassword();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            UserManager.getInstance().login(username, password);
        }
        if (UserManager.getInstance().isLogin()) {
            int userId = UserManager.getInstance().getCurrentUserId();
            setTitle(UserManager.getInstance().getUserProfile(userId).pseudonym);
        } else {
            setTitle(getString(R.string.app_name));

        }
        addAccountView();

        mWorksListFragment = new CalligraphyListFragment();
        addWorkList(mWorksListFragment);

        mWorksListFragment.setBottomScrollListener(new CalligraphyListFragment.OnBottomScrollListener() {
            @Override
            public void onBottom() {

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mIsLoadBottom = true;
                        final List<WorksInfo> worksInfoList = WorksServer.getWorksInHome(mCurrentPageIndex, PAGE_SIZE, WORKS_WIDTH);
                        if(null!=worksInfoList&&worksInfoList.size() >0 ){
                            mCurrentPageIndex++;
                            mWorksListFragment.addWorkList(worksInfoList);
                        }
                        mIsLoadBottom = false;
                    }
                });
                if(!mIsLoadBottom){
                    Log.v("Tumcca", "onBottom");
                    t.start();
                }


            }
        });

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCurrentPageIndex = 1;
                final List<WorksInfo> worksInfoList = WorksServer.getWorksInHome(mCurrentPageIndex, PAGE_SIZE, WORKS_WIDTH);
                if(null!=worksInfoList&&worksInfoList.size() >0 ){
                    mCurrentPageIndex++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWorksListFragment.setWorksList(worksInfoList);

                        }
                    });
                }
            }
        }, new IntentFilter("upload_works"));

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
            layout.setVisibility(View.VISIBLE);
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
        UserManager userManager = UserManager.getInstance();
        int userid = userManager.getCurrentUserId();
        Profile profile = userManager.getUserProfile(userid);
        if (requestCode == LoginActivity.REQ_LOGIN && resultCode == RESULT_OK) {
            setTitle(profile.pseudonym);
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_account);
            layout.setVisibility(View.GONE);
        }
        if (requestCode == RegisterActivity.REQ_REGISTER && resultCode == RESULT_OK) {
            setTitle(profile.pseudonym);
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_account);
            layout.setVisibility(View.GONE);
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


    public void addWorkList(CalligraphyListFragment fragment) {
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