package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.adapter.AlbumAdapter;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.widget.ExpandGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/4/15.
 */
public class AlbumListFragment extends Fragment {

    private final int PAGE_COUNT = 1;
    private final int PAGE_SIZE = 20;
    private final int WIDTH = 400;
    private int mUserId = -1;
    private List<Album> mAlbumList = new ArrayList<Album>();
    private ExpandGridView mEGVAlbumView = null;
    private AlbumAdapter mAlbumsAdapter; // = new AlbumAdapter();
    Activity mContext;
    TextView mTvAlbumCount;
    AlbumListLoader mAlbumsLoader;
    CircleProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    ImageLoader mImageLoader;
    boolean mScrollingIdle = false;
    int mCurrentPage = 1;
    int mParentWidth = 0;

    BroadcastReceiver mReceiver;

    public static interface AlbumListLoader {

        /**
         * 获取初始数据
         *
         * @return
         */
        public List<Album> getInitialAlbums();

        /**
         * 取得数据后，要调用{@link #addAlbumsHead}
         */
        public void loadHeadAlbums();

        /**
         * 取得数据后，要调用{@link #addAlbumsTail}
         */
        public void loadTailAlbums(int page);
    }

    public void setAlbumLoader(AlbumListLoader loader) {
        mAlbumsLoader = loader;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        mTvAlbumCount = (TextView) view.findViewById(R.id.textView_album_count);
        mProgressBar = (CircleProgressBar) view.findViewById(R.id.progressBar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.button_normal_red);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != mAlbumsLoader) {
                    mAlbumsLoader.loadHeadAlbums();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mEGVAlbumView = (ExpandGridView) view.findViewById(R.id.gridview_albums);
        addAlbumListView(mEGVAlbumView);
        setupProgressBar();

//        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.MATCH_PARENT, View.MeasureSpec.EXACTLY);
//        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.EXACTLY);
//        view.measure(widthMeasureSpec, heightMeasureSpec);
        //ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        //layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //layoutParams.height = view.getMeasuredHeight() + mListViewHeight;
        //view.setLayoutParams(layoutParams);
        Log.v(TumccaApplication.TAG, "list view height " + mEGVAlbumView.getMeasuredHeight() + "");

        //int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        //super.onMeasure(widthMeasureSpec, expandSpec);
        //layoutParams.height = listViewHeight;
        return view;
    }

    public void addAlbumListView(View view) {
        ExpandGridView gridView = (ExpandGridView) view;
        mAlbumsAdapter = new AlbumAdapter(mContext);
        gridView.setAdapter(mAlbumsAdapter);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //mImageLoader.resume();
                        mScrollingIdle = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        //mImageLoader.pause();
                        mScrollingIdle = false;

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        //mScrollingIdle = false;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount + firstVisibleItem == totalItemCount && mScrollingIdle) {

                    if (null != mAlbumsLoader) {
                        mScrollingIdle = false;
                        mAlbumsLoader.loadTailAlbums(++mCurrentPage);
                    }
                }
            }
        });
        if (null != mAlbumsLoader) {
            List<Album> initialAlbums = mAlbumsLoader.getInitialAlbums();
            if (null != initialAlbums) {
                mAlbumList.clear();
                mAlbumList.addAll(initialAlbums);
                mAlbumsAdapter.setAlbumList(mAlbumList);
                mTvAlbumCount.setText(mContext.getString(R.string.you_have_albums, mAlbumList.size()));
            }
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mContext.registerReceiver(mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra("id", -1);
                removeAlbum(id);

            }
        }, new IntentFilter("remove_album"));


    }


    @Override
    public void onStart() {
        super.onStart();
        UserManager.getInstance().getCurrentToken(new UserManager.PostLoginTask() {
            @Override
            public void onLogin(String token) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TumccaApplication.TAG, "parse albums");
                        WorksServer.parseAlbumList(UserManager.getInstance().getCurrentToken(null), mAlbumList);
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAlbumsAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                });

            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void removeAlbum(long id) {
        for (Album album : mAlbumList) {
            if (album.id == id) {
                mAlbumList.remove(album);
                break;
            }
        }
        mAlbumsAdapter.setAlbumList(mAlbumList);

    }


    private void setupProgressBar() {
        mProgressBar.setShowArrow(true);
        if (null != mAlbumsLoader && mAlbumsLoader.getInitialAlbums() == null && mAlbumList.size() == 0) {
            mAlbumsLoader.loadHeadAlbums();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mAlbumsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Bundle bundle = new Bundle();
//        bundle.putInt("parentWidth", mParentWidth);
//        setArguments(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("parentWidth", mParentWidth);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (null != savedInstanceState) {
            mParentWidth = savedInstanceState.getInt("parentWidth");
        }
    }


    /**
     * 将数据添加到底部，需要在主线程中调用
     * 将数据添加到头部
     *
     * @param albumList
     */
    public void addAlbumsHead(final List<Album> albumList) {
        if (mAlbumList.size() > 0) {
            long topId = mAlbumList.get(0).id;
            int index = albumList.size();
            for (int i = 0; i < albumList.size(); i++) {
                if (topId == albumList.get(i).id) {
                    index = i;
                    break;
                }
            }
            if (albumList.subList(0, index).size() == 0) {
                //Toast.makeText(mContext, getString(R.string.there_is_no_new_works), Toast.LENGTH_SHORT).show();

            } else {
                //Toast.makeText(mContext, getString(R.string.there_is_works, albumList.subList(0, index).size()), Toast.LENGTH_SHORT).show();
                mAlbumList.addAll(0, albumList.subList(0, index));
            }

        } else {
            mAlbumList.addAll(albumList);
        }
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvAlbumCount.setText(mContext.getString(R.string.you_have_albums, mAlbumList.size()));
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mAlbumsAdapter.setAlbumList(mAlbumList);
            }
        });

        WorksServer.parseAlbumList(UserManager.getInstance().getCurrentToken(null), mAlbumList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlbumsAdapter.setAlbumList(mAlbumList);
            }
        });

    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     *
     * @param albumList
     */
    public void addAlbumsTail(final List<Album> albumList) {
        mAlbumList.addAll(albumList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvAlbumCount.setText(mContext.getString(R.string.you_have_albums, mAlbumList.size()));
                mAlbumsAdapter.setAlbumList(mAlbumList);
            }
        });

        WorksServer.parseAlbumList(UserManager.getInstance().getCurrentToken(null), mAlbumList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlbumsAdapter.setAlbumList(mAlbumList);
            }
        });
    }

    int mListViewHeight = 0;

    public void applyListviewHeightWithChild() {
        int listViewHeight = 0;
        int adaptCount = mAlbumsAdapter.getCount();
        for (int i = 0; i < adaptCount; i = i + 2) {
            View temp = mAlbumsAdapter.getView(i, null, mEGVAlbumView);
            temp.measure(0, 0);
            listViewHeight += temp.getMeasuredHeight();

        }
        ViewGroup.LayoutParams layoutParams = this.mEGVAlbumView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        //int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        //super.onMeasure(widthMeasureSpec, expandSpec);
        layoutParams.height = listViewHeight;
        mListViewHeight = listViewHeight;
        mEGVAlbumView.setLayoutParams(layoutParams);
        mEGVAlbumView.setSize(0, listViewHeight);
        Log.v(TumccaApplication.TAG, "BaseListFragment:applyListviewHeightWithChild:Height=" + mEGVAlbumView.getMeasuredHeight());
        //mEGVAlbumView.
    }
}