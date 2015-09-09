package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Constants;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.UserServer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/12/15.
 *  创建UserListFragment过程
 *  设置模式，显示关注者或者粉丝
 *  根据模式，读取当前用户关注者数量，或者粉丝数量
 *  载入第一页数据。
 *  根据用户操作加载数据。
 *
 */
public class UserListFragment extends BaseListFragment {

    /**
     * 显示关注者数量
     */
    public static int MODE_FOLLOWING = 0;

    /**
     * 显示粉丝数量
     */
    public static int MODE_FOLLOWER = 1;

    int mMode = MODE_FOLLOWING;
    long mUserId = -1;
    TextView mTvCount;
    Activity mActivity;
    int mCount = 0;

    BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reload();
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    if(mMode==MODE_FOLLOWING){
                        setCount(UserServer.getInstance().getAuthorFollowing(mUserId));
                    }
                    if(mMode==MODE_FOLLOWER){
                        setCount(UserServer.getInstance().getAuthorFollowers(mUserId));

                    }
                }
            });
        }
    };

    public UserListFragment(){
        super();
        setLayout(R.layout.fragment_user_list);
        setMode(BaseListFragment.MODE_PULL_DRAG);
    }

    public void setUserId(Long id){
        mUserId = id;
    }

    //设置用户模式
    public void setUserMode(int mode)
    {
        if(mode<=MODE_FOLLOWER&&mode>=MODE_FOLLOWING){
            mMode = mode;
            applyMode();
        }
    }

    private void applyMode(){
        if(mMode == MODE_FOLLOWING){
            setItemLoader(new ItemLoader() {
                @Override
                public List<ListItem> loadHead() {
                    return Arrays.asList(UserManager.getInstance().getUserFollowings(mUserId, 1, 5).toArray(new ListItem[0]));
                }

                @Override
                public List<ListItem> loadTail(int page) {
                    return Arrays.asList(UserManager.getInstance().getUserFollowings(mUserId, page, 5).toArray(new ListItem[0]));
                }
            });
        }
        else if(mMode == MODE_FOLLOWER){
            setItemLoader(new ItemLoader() {
                @Override
                public List<ListItem> loadHead() {
                    return Arrays.asList(UserManager.getInstance().getUserFollowers(mUserId, 1, 5).toArray(new ListItem[0]));

                }

                @Override
                public List<ListItem> loadTail(int page) {
                    return Arrays.asList(UserManager.getInstance().getUserFollowers(mUserId, 1, 5).toArray(new ListItem[0]));
                }
            });

        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mActivity = activity;
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                if(mMode==MODE_FOLLOWING){
                    setCount(UserServer.getInstance().getAuthorFollowing(mUserId));
                }
                if(mMode==MODE_FOLLOWER){
                    setCount(UserServer.getInstance().getAuthorFollowers(mUserId));

                }
            }
        });

        UserManager.getInstance().getCurrentToken(new UserManager.PostLoginTask() {
            @Override
            public void onLogin(String token) {
                reload();
            }

            @Override
            public void onCancel() {

            }
        });

        listenUserChanges();
    }

    @Override
    protected void buildView(View view, Bundle savedInstanceState){
        super.buildView(view, savedInstanceState);
        mTvCount = (TextView) view.findViewById(R.id.textView_count);
        if(null!=savedInstanceState){
            setCount(savedInstanceState.getInt("count"));
        }
    }

//    @Override
//    public void onViewStateRestored(Bundle state){
//        super.onViewStateRestored(state);
//
//    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putInt("count", mCount);
    }
    /**
     * 设置关注或者粉丝的数量
     * @param count
     */
    private void setCount(final int count){
        mCount = count;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvCount.setText("共有"+count+"个用户");
            }
        });
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onViewStateRestored (Bundle savedInstanceState){
        super.onViewStateRestored(savedInstanceState);
    }

    private void listenUserChanges(){
        mActivity.registerReceiver(mUserChangeReceiver, new IntentFilter(Constants.ACTION_USERS_CHANGE));
    }

    public void onDestroy(){
        super.onDestroy();
        mActivity.unregisterReceiver(mUserChangeReceiver);
    }


}