package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.etsy.android.grid.StaggeredGridView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.HalfRoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.activity.UserActivity;
import com.pineapple.mobilecraft.tumcca.activity.WorkDetailActivity;
import com.pineapple.mobilecraft.tumcca.adapter.WorkAdapter;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IWorksList;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import com.pineapple.mobilecraft.util.logic.Util;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yihao on 15/6/4.
 */
public class WorkListFragment extends Fragment implements IWorksList {
    public static final int MODE_LV_DRAG = 0;
    public static final int MODE_LV_FIXED = 1;

    static final int CORNER_RADIUS = 5;
    List<WorksInfo> mWorksInfoList = new ArrayList<WorksInfo>();
    int mCurrentPage = 1;
    int mListViewMode = MODE_LV_DRAG;
    boolean mIsEnd = false;

    Activity mContext;
    WorkAdapter mAdapter;
    DisplayImageOptions mImageOptionsWorks;
    ImageLoader mImageLoader;
    boolean mScrollingIdle = false;
    HashMap<Integer, Profile> mMapProfile;
    TumccaService mService;
    WorkListLoader mWorksLoader = null;

    CircleProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    BroadcastReceiver mReceiver;
    StaggeredGridView mListView;
    View mFooterProgressView;


    public void setListViewMode(int mode) {
        if (mode >= MODE_LV_DRAG && mode <= MODE_LV_FIXED) {
            mListViewMode = mode;
        }
    }

    public static interface WorkListLoader {

        /**
         * 获取初始数据
         *
         * @return
         */
        public List<WorksInfo> getInitialWorks();

        /**
         * 取得数据后，要调用{@link #addWorksHead}
         */
        public void loadHeadWorks();

        /**
         * 取得数据后，要调用{@link #addWorksTail}
         */
        public void loadTailWorks(final int page);
    }

    public WorkListFragment() {
        mMapProfile = new HashMap<Integer, Profile>();


        mImageOptionsWorks = new DisplayImageOptions.Builder()
                .displayer(new HalfRoundedBitmapDisplayer(Util.dip2px(TumccaApplication.applicationContext, CORNER_RADIUS))).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        mImageLoader = ImageLoader.getInstance();

    }

    public void setWorksLoader(WorkListLoader workListLoader) {
        mWorksLoader = workListLoader;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mAdapter = new WorkAdapter(mContext);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity().getApplicationContext();
        context.bindService(new Intent(context, TumccaService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((TumccaService.LocalService) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.registerReceiver(mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int id = intent.getIntExtra("id", -1);
                removeWork(id);
            }
        }, new IntentFilter("remove_work"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void removeWork(int id) {
        for (WorksInfo worksInfo : mWorksInfoList) {
            if (worksInfo.id == id) {
                mWorksInfoList.remove(worksInfo);
                break;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setupProgressBar();
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 将数据添加到底部，需要在主线程中调用
     * 将数据添加到头部
     *
     * @param worksInfoList
     */
    @Override
    public int addWorksHead(final List<WorksInfo> worksInfoList) {

        int currentLength = mWorksInfoList.size();
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWorksInfoList.size() > 0) {
                    int topId = mWorksInfoList.get(0).id;
                    int index = worksInfoList.size();
                    for (int i = 0; i < worksInfoList.size(); i++) {
                        if (topId == worksInfoList.get(i).id) {
                            index = i;
                            break;
                        }
                    }
                    if (worksInfoList.subList(0, index).size() != 0) {
                        mWorksInfoList.addAll(0, worksInfoList.subList(0, index));
                    }

                }
                else
                {
                    mWorksInfoList.addAll(worksInfoList);
                }
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mAdapter.setWorks(mWorksInfoList);
                mAdapter.notifyDataSetChanged();
            }
        });
        parseWorks(mWorksInfoList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setWorks(mWorksInfoList);
                mAdapter.notifyDataSetChanged();
            }
        });
        return (mWorksInfoList.size() - currentLength);
    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     *
     * @param worksInfoList
     */
    @Override
    public int addWorksTail(final List<WorksInfo> worksInfoList) {
        Log.v(TumccaApplication.TAG, "WorkListFragment addWorksTail");
        if(worksInfoList!=null&&worksInfoList.size()!=0){
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWorksInfoList.addAll(worksInfoList);
                    mAdapter.setWorks(mWorksInfoList);
                    mAdapter.notifyDataSetChanged();
                }
            });
            parseWorks(mWorksInfoList);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setWorks(mWorksInfoList);
                    mAdapter.notifyDataSetChanged();
                }
            });
            return worksInfoList.size();
        }
        else{
            setEnd(true);
            return 0;
        }

    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     * 清空数据
     */
    public void clearWorks() {
        mIsEnd = false;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWorksInfoList.clear();
                mAdapter.notifyDataSetChanged();
                if (null != mWorksLoader) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mWorksLoader.loadHeadWorks();
                }
            }
        });

    }

    @Override
    public void addWorksListView(View view) {
        mListView = (StaggeredGridView) view;
        mFooterProgressView = mContext.getLayoutInflater().inflate(R.layout.progressbar, null);
        mListView.addFooterView(mFooterProgressView);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        mImageLoader.resume();
                        mScrollingIdle = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        mImageLoader.pause();
                        mScrollingIdle = false;

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        mScrollingIdle = false;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.v(TumccaApplication.TAG, "onScroll " + visibleItemCount + " " + firstVisibleItem + " " + totalItemCount);

                if (visibleItemCount + firstVisibleItem == totalItemCount && !mIsEnd) {
                    if (null != mWorksLoader) {
                        mScrollingIdle = false;
                        Log.v(TumccaApplication.TAG, "loadTailWorks");
                        mWorksLoader.loadTailWorks(++mCurrentPage);
                    }
                }
            }
        });
        if (null != mWorksLoader) {
            final List<WorksInfo> worksInfoList = mWorksLoader.getInitialWorks();
            if (null != worksInfoList) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWorksInfoList.clear();
                        mWorksInfoList.addAll(worksInfoList);
                        mAdapter.setWorks(mWorksInfoList);
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }
        }

    }

    @Override
    public void openWorks(WorksInfo calligraphy) {
        WorkDetailActivity.startActivity(calligraphy, mContext);
    }

    @Override
    public void openAuthor(int authorId) {
        UserActivity.startActivity(mContext, authorId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(null!=savedInstanceState){
            restoreState(savedInstanceState);
        }

        View view = inflater.inflate(R.layout.fragment_work_list, container, false);
        mProgressBar = (CircleProgressBar) view.findViewById(R.id.progressBar);
        StaggeredGridView listView = (StaggeredGridView) view.findViewById(R.id.list);
        addWorksListView(listView);


        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.button_normal_red);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != mWorksLoader) {
                    mWorksLoader.loadHeadWorks();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        setupProgressBar();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("parentWidth", mParentWidth);
        //缓存Works列表
        JSONArray worksArray = new JSONArray();
        for(WorksInfo worksInfo:mWorksInfoList){
            worksArray.put(WorksInfo.toJSON(worksInfo));
        }
        outState.putString("works", worksArray.toString());

        //缓存当前页
        outState.putInt("currentPage", mCurrentPage);

        //缓存是否到底
        outState.getBoolean("isEnd", mIsEnd);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(null!=savedInstanceState){
            restoreState(savedInstanceState);
        }
//        if (null != savedInstanceState) {
//            try {
//                mWorksInfoList.reload();
//                JSONArray worksArray = new JSONArray(savedInstanceState.getString("works"));
//                for(int i=0; i<worksArray.length(); i++){
//                    WorksInfo worksInfo = WorksInfo.fromJSON(worksArray.getJSONObject(i));
//                    mWorksInfoList.add(worksInfo);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            mCurrentPage = savedInstanceState.getInt("currentPage");
//
//            mIsEnd = savedInstanceState.getBoolean("isEnd");
//        }
    }

    private void restoreState(Bundle savedInstanceState){
        Log.v(TumccaApplication.TAG, "restoreState");
        try {
            mWorksInfoList.clear();
            JSONArray worksArray = new JSONArray(savedInstanceState.getString("works"));
            for(int i=0; i<worksArray.length(); i++){
                WorksInfo worksInfo = WorksInfo.fromJSON(worksArray.getJSONObject(i));
                mWorksInfoList.add(worksInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mCurrentPage = savedInstanceState.getInt("currentPage");

        mIsEnd = savedInstanceState.getBoolean("isEnd");
    }

    private void setupProgressBar() {
        mProgressBar.setShowArrow(true);
        if (null != mWorksLoader && mWorksLoader.getInitialWorks() == null && mWorksInfoList.size() == 0) {
            mWorksLoader.loadHeadWorks();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }


    private void parseWorks(final List<WorksInfo> worksInfoList) {
        if (null != worksInfoList) {
            for (int i = 0; i < worksInfoList.size(); i++) {
                if (!mMapProfile.containsKey(worksInfoList.get(i).author)) {
                    Profile profile = UserManager.getInstance().getUserProfile(worksInfoList.get(i).author);
                    worksInfoList.get(i).profile = profile;
                    mMapProfile.put(worksInfoList.get(i).author, profile);
                } else {
                    worksInfoList.get(i).profile = mMapProfile.get(worksInfoList.get(i).author);
                }

            }
        }

    }


    private void setEnd(boolean isEnd) {
        mIsEnd = isEnd;
        if (mIsEnd) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListView.removeFooterView(mFooterProgressView);
                }
            });
        }
    }

//    public void applyListviewHeightWithChild(){
//        int listViewHeightLeft = 0;
//        int listViewHeightRight = 0;
//        int adaptCount = mAdapter.getCount();
//        for(int i=0;i<adaptCount;i=i+2){
//            View viewLeft = mAdapter.getView(i, null, null);
//            viewLeft.measure(0,0);
//            listViewHeightLeft += viewLeft.getMeasuredHeight();
//            Log.v(TumccaApplication.TAG, "BaseListFragment:applyListviewHeightWithChild:HeightLeft=" + listViewHeightLeft);
//
//            if((i+1)<mAdapter.getCount()){
//                View viewRight = mAdapter.getView(i, null, null);
//                viewRight.measure(0,0);
//                listViewHeightRight += viewRight.getMeasuredHeight();
//                Log.v(TumccaApplication.TAG, "BaseListFragment:applyListviewHeightWithChild:HeightRight=" + listViewHeightLeft);
//            }
//        }
//        ViewGroup.LayoutParams layoutParams = this.mListView.getLayoutParams();
//        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//
//        //int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
//        //super.onMeasure(widthMeasureSpec, expandSpec);
//        layoutParams.height = Math.max(listViewHeightLeft, listViewHeightRight);
//        mListView.setLayoutParams(layoutParams);
//    }

}