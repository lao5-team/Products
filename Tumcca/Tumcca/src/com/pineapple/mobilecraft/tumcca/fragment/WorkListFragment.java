package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.activity.WorkDetailActivity;
import com.pineapple.mobilecraft.tumcca.activity.UserActivity;
import com.pineapple.mobilecraft.tumcca.data.PictureInfo;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IWorksList;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
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
    OnScrollListener mScrollListener = null;
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
        public void loadTailWorks(int page);
    }

    public WorkListFragment() {
        mMapProfile = new HashMap<Integer, Profile>();
        mAdapter = new PictureAdapter();
        mImageOptionsAvatar = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(Util.dip2px(DemoApplication.applicationContext, CORNER_RADIUS))).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        mImageOptionsWorks = new DisplayImageOptions.Builder()
                .displayer(new HalfRoundedBitmapDisplayer(Util.dip2px(DemoApplication.applicationContext, CORNER_RADIUS))).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
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
    public void addWorksHead(final List<WorksInfo> worksInfoList) {
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
                Toast.makeText(mContext, getString(R.string.there_is_no_new_works), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(mContext, getString(R.string.there_is_works, worksInfoList.subList(0, index).size()), Toast.LENGTH_SHORT).show();
                mWorksInfoList.addAll(0, worksInfoList.subList(0, index));
            }

        } else {
            mWorksInfoList.addAll(worksInfoList);
        }
        parseWorks(mWorksInfoList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mAdapter.mListWorks.clear();
                mAdapter.mListWorks.addAll(mWorksInfoList);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     *
     * @param worksInfoList
     */
    @Override
    public void addWorksTail(final List<WorksInfo> worksInfoList) {
        mWorksInfoList.addAll(worksInfoList);
        parseWorks(mWorksInfoList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.mListWorks.clear();
                mAdapter.mListWorks.addAll(mWorksInfoList);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     * 清空数据
     */
    public void clearWorks() {
        mWorksInfoList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addWorksListView(View view) {
        StaggeredGridView listView = (StaggeredGridView) view;
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                if (visibleItemCount + firstVisibleItem == totalItemCount && mScrollingIdle) {

                    if (null != mWorksLoader) {
                        mScrollingIdle = false;
                        mWorksLoader.loadTailWorks(++mCurrentPage);
                    }
                }
            }
        });
        if (null != mWorksLoader) {
            List<WorksInfo> worksInfoList = mWorksLoader.getInitialWorks();
            if (null != worksInfoList) {
                mWorksInfoList.clear();
                mWorksInfoList.addAll(worksInfoList);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.mListWorks.clear();
                        mAdapter.mListWorks.addAll(mWorksInfoList);
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
        View view = inflater.inflate(R.layout.fragment_calligraphy_list, container, false);
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


    private  void setupProgressBar(){
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

        public void bindViews(View container, int pic, int desc, int author, int authorName,
                              int layWork, int layAuthor){
            ivPic = (ImageView) container.findViewById(pic);
            tvDesc = (TextView) container.findViewById(desc);
            ivAuthor = (ImageView) container.findViewById(author);
            tvAuthorName = (TextView) container.findViewById(authorName);
            rlWork = (RelativeLayout) container.findViewById(layWork);
            rlAuthor = (RelativeLayout) container.findViewById(layAuthor);
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

                String avatarUrl = "drawable://" + R.drawable.default_avatar;
                if (profile.avatar > PictureServer.INVALID_AVATAR_ID) {
                    avatarUrl = UserServer.getInstance().getAvatarUrl(profile.avatar);
                }

                if (ivAuthor.getTag() == null || !ivAuthor.getTag().equals(avatarUrl)) {
                    mImageLoader.displayImage(avatarUrl, ivAuthor, mImageOptionsAvatar);
                    ivAuthor.setTag(avatarUrl);
                }
            }


        }
    }

    private class PictureAdapter extends BaseAdapter {
        public List<WorksInfo> mListWorks = new ArrayList<WorksInfo>();

        @Override
        public int getCount() {
            if (null != mListWorks) {
                Log.v("Tumcca", "Home fragment size " + mListWorks.size());
                return mListWorks.size();
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
                        R.id.textView_author, R.id.layout_work, R.id.layout_author);
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
}