package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.etsy.android.grid.StaggeredGridView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Constants;
import com.pineapple.mobilecraft.tumcca.activity.AlbumsListActivity;
import com.pineapple.mobilecraft.tumcca.activity.WorksListActivity;
import com.pineapple.mobilecraft.tumcca.adapter.AlbumAdapter;
import com.pineapple.mobilecraft.tumcca.adapter.WorkAdapter;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.widget.ExpandGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/3/15.
 * Pinterset 风格的专辑作品列表 http://pan.baidu.com/s/1pJ1OYaZ#render-type=grid-view
 *
 * 因为layout结构是ScrollView套GridView和StaggeredGridView，所以要计算后者的高度{@link #applyListviewHeightWithChild}
 * 并且ScrollView内不能包含带有ListView的Fragment，也是因为高度计算有问题
 *
 * 另外，需要侦听喜欢和收藏事件，以及时刷新数据
 *
 */
public class PinlikeAlbumWorkListFragment extends Fragment {

    public static final int SAMPLE_COUNT = 2;  //样本显示个数
    public static final int MODE_LIKE = 0;
    public static final int MODE_COLLECT = 1;

    //data
    private int mDataMode = MODE_LIKE;
    private Activity mActivity;
    private int mAuthorId = -1;
    private List<Album> mAlbumList = new ArrayList<Album>();
    private List<WorksInfo> mWorkList = new ArrayList<WorksInfo>();

    //widgets
    private ExpandGridView mEGVAlbums;
    private AlbumAdapter mAlbumsAdapter;
    private TextView mTvSeeAllAlbums;
    private StaggeredGridView mSGVWorks;
    private WorkAdapter mWorkAdapter;
    private TextView mTvSeeAllWorks;

    public void setAuthorId(int authorId) {
        mAuthorId = authorId;
    }

    public void setDataMode(int mode) {
        mDataMode = mode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pinlike_albumworks, container, false);
        buildAlbumsView(view);
        buildWorksView(view);
        return view;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        listenAlbumsChange();
        listenWorksChange();
    }

    /**
     * @param view
     */
    private void buildAlbumsView(View view) {
        //添加GridView
        mEGVAlbums = (ExpandGridView) view.findViewById(R.id.gridview_albums);

        //添加Adapter
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                loadAlbums();
            }
        });
        mAlbumsAdapter = new AlbumAdapter(mActivity);
        mEGVAlbums.setAdapter(mAlbumsAdapter);

        //设置See All按钮
        mTvSeeAllAlbums = (TextView) view.findViewById(R.id.textView_all_albums);
        mTvSeeAllAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumsListActivity.startActivity(getActivity(), mDataMode, mAuthorId);
            }
        });
    }

    private void buildWorksView(View view) {

        //添加StaggeredGridView
        mSGVWorks = (StaggeredGridView) view.findViewById(R.id.gridview_works);

        //添加WorksAdapter
        mWorkAdapter = new WorkAdapter(mActivity);

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                loadWorks();
            }
        });
        mSGVWorks.setAdapter(mWorkAdapter);
        mWorkAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                applyListviewHeightWithChild();
            }
        });

        //添加See All按钮*/
        mTvSeeAllWorks = (TextView) view.findViewById(R.id.textView_all_works);
        mTvSeeAllWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorksListActivity.startActivity(getActivity(), mDataMode, mAuthorId);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("authorId", mAuthorId);
        outState.putInt("dataMode", mDataMode);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (null != savedInstanceState) {
            mAuthorId = savedInstanceState.getInt("authorId");
            mDataMode = savedInstanceState.getInt("dataMode");
        }
    }

    //设置ListView适应子数据的高度
    private void applyListviewHeightWithChild() {
        int listViewHeightLeft = 0;
        int listViewHeightRight = 0;
        int adaptCount = mWorkAdapter.getCount();
        for (int i = 0; i < adaptCount; i = i + 2) {
            View temp = mWorkAdapter.getView(i, null, mSGVWorks);
            temp.measure(0, 0);
            listViewHeightLeft += temp.getMeasuredHeight();

            if(i+1<adaptCount){
                temp = mWorkAdapter.getView(i+1, null, mSGVWorks);
                temp.measure(0, 0);
                listViewHeightRight += temp.getMeasuredHeight();
            }

        }
        ViewGroup.LayoutParams params = mSGVWorks.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = Math.max(listViewHeightLeft, listViewHeightRight);
        mSGVWorks.setLayoutParams(params);
    }

    private void loadAlbums(){
        if (mDataMode == MODE_LIKE) {
            mAlbumList = WorksServer.getLikeAlbums(mAuthorId, 1, SAMPLE_COUNT);

        } else if (mDataMode == MODE_COLLECT) {
            mAlbumList = WorksServer.getCollectAlbums(mAuthorId, 1, SAMPLE_COUNT);

        }
        WorksServer.parseAlbumList(UserManager.getInstance().getCurrentToken(null), mAlbumList);
        mAlbumsAdapter.setAlbumList(mAlbumList);
    }

    private void loadWorks(){
        if (mDataMode == MODE_LIKE) {
            mWorkList = WorksServer.getLikeWorks(
                    mAuthorId, 1, SAMPLE_COUNT, 400);

        } else if (mDataMode == MODE_COLLECT) {
            mWorkList = WorksServer.getCollectWorks(
                    mAuthorId, 1, SAMPLE_COUNT, 400);

        }
        mWorkAdapter.setWorks(mWorkList);
    }

    BroadcastReceiver mAlbumsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    loadAlbums();
                }
            });
        }
    };

    BroadcastReceiver mWorksChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    loadWorks();
                }
            });
        }
    };

    private void listenAlbumsChange(){
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_ALBUMS_CHANGE);
        mActivity.registerReceiver(mAlbumsChangeReceiver, intentFilter);
    }

    private void listenWorksChange(){
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_WORKS_CHANGE);
        mActivity.registerReceiver(mWorksChangeReceiver, intentFilter);
    }

    public void onDestroy(){
        super.onDestroy();
        mActivity.unregisterReceiver(mAlbumsChangeReceiver);
        mActivity.unregisterReceiver(mWorksChangeReceiver);
    }
}