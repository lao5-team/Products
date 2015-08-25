package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.etsy.android.grid.StaggeredGridView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.HalfRoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.activity.WorkDetailActivity;
import com.pineapple.mobilecraft.tumcca.activity.UserActivity;
import com.pineapple.mobilecraft.tumcca.data.PictureInfo;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.PictureManager;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IWorksList;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import com.pineapple.mobilecraft.util.logic.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yihao on 15/6/4.
 */
public class WorkListFragment extends Fragment implements IWorksList {
    static final int CORNER_RADIUS = 5;
    List<WorksInfo> mWorksInfoList = new ArrayList<WorksInfo>();
    Activity mContext;
    PictureAdapter mAdapter;
    DisplayImageOptions mImageOptionsWorks;
    DisplayImageOptions mImageOptionsAvatar;
    ImageLoader mImageLoader;
    boolean mScrollingIdle = false;
    HashMap<Integer, Profile> mMapProfile;

    TumccaService mService;
    WorkListLoader mWorksLoader = null;

    CircleProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int mCurrentPage = 1;
    BroadcastReceiver mReceiver;
    StaggeredGridView mListView;
    View mFooterProgressView;
    int mListViewMode = MODE_LV_DRAG;
    public static final int MODE_LV_DRAG = 0;
    public static final int MODE_LV_FIXED = 1;

    public void setListViewMode(int mode){
        if(mode>=MODE_LV_DRAG&&mode<=MODE_LV_FIXED){
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
        mAdapter = new PictureAdapter();

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
    public void onAttach(Activity activity){
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
    public void onDetach(){
        super.onDetach();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void removeWork(int id){
        for(WorksInfo worksInfo:mWorksInfoList){
            if(worksInfo.id == id){
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
                    if (worksInfoList.subList(0, index).size() == 0) {
                        //Toast.makeText(mContext, getString(R.string.there_is_no_new_works), Toast.LENGTH_SHORT).show();

                    } else {
                        //Toast.makeText(mContext, getString(R.string.there_is_works, worksInfoList.subList(0, index).size()), Toast.LENGTH_SHORT).show();
                        mWorksInfoList.addAll(0, worksInfoList.subList(0, index));

                        ///mWorksInfoList.addAll(worksInfoList.subList(0, index));
                    }

                } else {
                    mWorksInfoList.addAll(worksInfoList);
                }
                //mWorksInfoList.addAll(worksInfoList);                    if(mLoadMode == MODE_FIXED_HEIGHT){
                //applyListviewHeightWithChild();
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }
        });
        parseWorks(mWorksInfoList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
        return (mWorksInfoList.size()-currentLength);
    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     *
     * @param worksInfoList
     */
    @Override
    public int addWorksTail(final List<WorksInfo> worksInfoList) {
        Log.v(TumccaApplication.TAG, "WorkListFragment addWorksTail");


        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWorksInfoList.addAll(worksInfoList);
//                if(mListViewMode == MODE_LV_FIXED){
//                    applyListviewHeightWithChild();
//
//                }
                mAdapter.notifyDataSetChanged();
            }
        });
        parseWorks(mWorksInfoList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
        return worksInfoList.size();
    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     * 清空数据
     */
    public void clearWorks() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWorksInfoList.clear();
                mAdapter.notifyDataSetChanged();
                if(null!=mWorksLoader){
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

                if (visibleItemCount + firstVisibleItem == totalItemCount&&!mIsEnd) {
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


    private void setupProgressBar(){
        mProgressBar.setShowArrow(true);
        if(null != mWorksLoader && mWorksLoader.getInitialWorks()==null && mWorksInfoList.size()==0){
            mWorksLoader.loadHeadWorks();
        }else{
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }


    private  class ViewHolder {
        ImageView ivPic;
        TextView tvDesc;
        ImageView ivAuthor;
        TextView tvAuthorName;
        RelativeLayout rlWork;
        RelativeLayout rlAuthor;
        TextView tvLike;
        TextView tvComment;

        public void bindViews(View container, int pic, int desc, int author, int authorName,
                              int layWork, int layAuthor, int like, int comment){
            ivPic = (ImageView) container.findViewById(pic);
            tvDesc = (TextView) container.findViewById(desc);
            ivAuthor = (ImageView) container.findViewById(author);
            tvAuthorName = (TextView) container.findViewById(authorName);
            rlWork = (RelativeLayout) container.findViewById(layWork);
            rlAuthor = (RelativeLayout) container.findViewById(layAuthor);
            tvLike = (TextView)container.findViewById(like);
            tvComment = (TextView)container.findViewById(comment);
        }

        public void bindWork(int position, ViewGroup parent){
            PictureInfo pictureInfo = mWorksInfoList.get(position).picInfo;
            //预先用图片的尺寸对imageView进行布局
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                    parent.getWidth() / 2, parent.getWidth() * pictureInfo.height / (2 * pictureInfo.width));
            ivPic.setLayoutParams(param);

            String imageUrl = PictureServer.getInstance().getPictureUrl(mWorksInfoList.get(position).picInfo.id);

            if (ivPic.getTag() == null || !ivPic.getTag().equals(imageUrl)) {
                mImageLoader.displayImage(PictureServer.getInstance().getPictureUrl(
                                mWorksInfoList.get(position).picInfo.id), ivPic, mImageOptionsWorks,
                        new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                view.setAlpha(0);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.setAlpha(100);
                                        Animation alpha = AnimationUtils.loadAnimation(mContext, R.anim.image_alpha);
                                        view.startAnimation(alpha);
                                    }
                                });
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        }, null);
                ivPic.setTag(imageUrl);
            }

            tvDesc.setText(mWorksInfoList.get(position).title);
            rlWork.setTag(new Integer(position));
            rlWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWorks(mWorksInfoList.get((Integer) v.getTag()));
                }
            });

            tvLike.setText(mWorksInfoList.get(position).likes+"");
            tvComment.setText(mWorksInfoList.get(position).comments+"");
        }

        public void bindAuthor(int position){

            rlAuthor.setTag(new Integer(position));
            rlAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAuthor(mWorksInfoList.get((Integer) v.getTag()).author);
                }
            });

            Profile profile = mWorksInfoList.get(position).profile;//mMapProfile.get(mWorksInfoList.get(position).author);
            if (null != profile) {
                tvAuthorName.setText(profile.pseudonym);
                PictureManager.getInstance().displayAvatar(ivAuthor, profile.avatar, 16);
            }


        }
    }

    private class PictureAdapter extends BaseAdapter {
        //public List<WorksInfo> mListWorks = new ArrayList<WorksInfo>();

        @Override
        public int getCount() {
            if (null != mWorksInfoList) {
                Log.v("Tumcca", "Home fragment size " + mWorksInfoList.size());
                return mWorksInfoList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_works, null);
                viewHolder.bindViews(convertView, R.id.imageView_picture, R.id.textView_describe, R.id.imageView_avatar,
                        R.id.textView_author, R.id.layout_work, R.id.layout_author, R.id.textView_like, R.id.textView_comment);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.bindWork(position, parent);
            viewHolder.bindAuthor(position);
            return convertView;
        }
    }

    private void parseWorks(final List<WorksInfo> worksInfoList) {
        if(null!=worksInfoList){
            for (int i = 0; i < worksInfoList.size(); i++) {
                if (!mMapProfile.containsKey(worksInfoList.get(i).author)) {
                    Profile profile = UserManager.getInstance().getUserProfile(worksInfoList.get(i).author);
                    worksInfoList.get(i).profile = profile;
                    mMapProfile.put(worksInfoList.get(i).author, profile);
                }
                else{
                    worksInfoList.get(i).profile = mMapProfile.get(worksInfoList.get(i).author);
                }

            }
        }

    }

    boolean mIsEnd = false;
    public void setEnd(boolean isEnd){
        mIsEnd = isEnd;
        if(mIsEnd){
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