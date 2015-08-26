package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.etsy.android.grid.StaggeredGridView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.activity.AlbumsListActivity;
import com.pineapple.mobilecraft.tumcca.activity.WorksListActivity;
import com.pineapple.mobilecraft.tumcca.adapter.AlbumAdapter;
import com.pineapple.mobilecraft.tumcca.adapter.WorkAdapter;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.widget.ExpandGridView;
import com.pineapple.mobilecraft.widget.TitleTabBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/3/15.
 */
public class AlbumWorkListFragment2 extends Fragment implements TitleTabBar.TitleTabClickListener {

    public static final int MODE_LIKE = 0;
    public static final int MODE_COLLECT = 1;


    int mParentWidth = 0;
    private int mDataMode = MODE_LIKE;
    private Activity mActivity;
    private ViewPager mContentPager;
    //private WorkListFragment mWorkListFragment;
    private int mAuthorId = -1;
    private ExpandGridView mEGVAlbums;
    private List<Album> mAlbumList = new ArrayList<Album>();
    AlbumAdapter mAlbumsAdapter;

    private StaggeredGridView mSGVWorks;
    private List<WorksInfo> mWorkList = new ArrayList<WorksInfo>();
    private WorkAdapter mWorkAdapter;
    private TextView mTvAlbum, mTvWorks;

    public void setAuthorId(int authorId) {
        mAuthorId = authorId;
    }

    public void setDataMode(int mode) {
        mDataMode = mode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albumwork2, container, false);
        buildAlbumsView(view);
        buildWorksView(view);
        return view;
    }

    @Override
    public void callback(int index) {
        mContentPager.setCurrentItem(index);
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    /**
     * @param view
     */
    private void buildAlbumsView(View view) {
        /*TODO 加载4个专辑
        添加GridView
        添加AlbumAdapter
        指定高度
        添加See All按钮
        拉起 AlbumsListActivity
        */

        mAlbumsAdapter = new AlbumAdapter(mActivity);
        mEGVAlbums = (ExpandGridView) view.findViewById(R.id.gridview_albums);
        mEGVAlbums.setAdapter(mAlbumsAdapter);

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                if (mDataMode == MODE_LIKE) {
                    mAlbumList = WorksServer.getLikeAlbums(mAuthorId, 1, 4);

                } else if (mDataMode == MODE_COLLECT) {
                    mAlbumList = WorksServer.getCollectAlbums(mAuthorId, 1, 4);

                }
                WorksServer.parseAlbumList(UserManager.getInstance().getCurrentToken(null), mAlbumList);
                mAlbumsAdapter.setAlbumList(mAlbumList);

            }
        });


        mTvAlbum = (TextView) view.findViewById(R.id.textView_all_albums);
        mTvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumsListActivity.startActivity(getActivity(), mDataMode, mAuthorId);
            }
        });
    }

    private void buildWorksView(View view) {
        /*TODO 加载4个作品*/
        //添加StaggeredGridView
        mSGVWorks = (StaggeredGridView) view.findViewById(R.id.gridview_works);

        //添加WorksAdapter
        mWorkAdapter = new WorkAdapter(mActivity);

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                if (mDataMode == MODE_LIKE) {
                    mWorkList = WorksServer.getLikeWorks(
                            mAuthorId, 1, 4, 400);

                } else if (mDataMode == MODE_COLLECT) {
                    mWorkList = WorksServer.getCollectWorks(
                            mAuthorId, 1, 4, 400);

                }
                mWorkAdapter.setWorks(mWorkList);
                //applyListviewHeightWithChild();

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
        //指定高度
        //添加See All按钮*/
        mTvWorks = (TextView) view.findViewById(R.id.textView_all_works);
        mTvWorks.setOnClickListener(new View.OnClickListener() {
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

    public void applyListviewHeightWithChild() {
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


}