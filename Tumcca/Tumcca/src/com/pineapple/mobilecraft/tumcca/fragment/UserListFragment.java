package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.domain.User;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.UserServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/12/15.
 * 创建UserListFragment过程
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

    public UserListFragment(){
        super();
        setLayout(R.layout.fragment_user_list);
        setMode(BaseListFragment.MODE_FIXED_HEIGHT);
    }

    public void setUserId(Long id){
        mUserId = id;
    }

    public void setUsersMode(int mode)
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
                    return null;
                }

                @Override
                public List<ListItem> loadTail(int page) {
                    return null;
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
            }
        });
    }



    @Override
    public void onResume(){
        super.onResume();

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
//        mTvCount = (TextView) view.findViewById(R.id.textView_count);
//        return view;
//    }

    @Override
    protected void buildView(View view){
        super.buildView(view);
        mTvCount = (TextView) view.findViewById(R.id.textView_count);
    }

    /**
     * 设置关注或者粉丝的数量
     * @param count
     */
    private void setCount(final int count){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvCount.setText("共有"+count+"个用户");
            }
        });
    }


}