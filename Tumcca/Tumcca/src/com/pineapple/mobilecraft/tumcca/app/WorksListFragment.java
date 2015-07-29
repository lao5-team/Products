package com.pineapple.mobilecraft.tumcca.app;

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
import android.widget.*;
import com.etsy.android.grid.StaggeredGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.HalfRoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.PictureInfo;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.Works;
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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by yihao on 15/6/4.
 */
public class WorksListFragment extends Fragment implements IWorksList {


    static final int CORNER_RADIUS = 5;
    OnScrollListener mScrollListener = null;
    List<WorksInfo> mWorksInfoList = new ArrayList<WorksInfo>();
    Activity mContext;
    PictureAdapter mAdapter;
    DisplayImageOptions mImageOptionsWorks;
    DisplayImageOptions mImageOptionsAvatar;
    ImageLoader mImageLoader;
    HashMap<Integer, Profile> mMapProfile;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TumccaService mService;
    WorkListLoader mWorksLoader = null;

    public static interface WorkListLoader{

        /**
         * 获取初始数据
         * @return
         */
        public  List<WorksInfo> getInitialWorks();
        /**
         * 取得数据后，要调用{@link #addWorksHead}
         */
        public void loadHeadWorks();

        /**
         * 取得数据后，要调用{@link #addWorksTail}
         */
        public void loadTailWorks();
    }

    public WorksListFragment() {
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

    public void setWorksLoader(WorkListLoader workListLoader){
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
        mAdapter.notifyDataSetChanged();
    }


//    public void setWorkList(final List<WorksInfo> worksInfoList){
//        mWorksInfoList.addAll(worksInfoList);
//        mAdapter.mListWorks.clear();
//        mAdapter.mListWorks.addAll(mWorksInfoList);
//
//    }

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
     *
     * @param worksInfoList
     */
    @Override
    public void addWorksTail(final List<WorksInfo> worksInfoList) {
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

    /**
     * 将数据添加到底部，需要在主线程中调用
     * 清空数据
     */
    public void clearWorks() {
        mWorksInfoList.clear();
        mAdapter.notifyDataSetChanged();
    }

    boolean mIdle = false;

    @Override
    public void addWorksListView(View view) {
        StaggeredGridView listView = (StaggeredGridView) view;
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openWorks(mWorksInfoList.get(position));
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        mImageLoader.resume();
                        mIdle = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        mImageLoader.pause();
                        mIdle = false;

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        mIdle = false;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount + firstVisibleItem == totalItemCount && mIdle) {
//                    if (null != mService) {
//                        // mScrollListener.onBottom();
//                        mIdle =false;
//                        mService.loadHomeTailList(new TumccaService.OnLoadFinished<WorksInfo>() {
//                            @Override
//                            public void onSuccess(List<WorksInfo> resultList) {
//                                addWorksTail(resultList);
//                            }
//
//                            @Override
//                            public void onFail(String message) {
//
//                            }
//                        });
//                    }
                    if(null!=mWorksLoader){
                        mIdle = false;
                        mWorksLoader.loadTailWorks();
                    }
                }
            }
        });
        if(null!=mWorksLoader){
            List<WorksInfo> worksInfoList= mWorksLoader.getInitialWorks();
            if(null!=worksInfoList){
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
        CalligraphyDetailActivity.startActivity(calligraphy, mContext);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calligraphy_list, container, false);
        StaggeredGridView listView = (StaggeredGridView) view.findViewById(R.id.list);
        addWorksListView(listView);


        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.button_normal_red);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if (null != mService) {
//                    //mScrollListener.onTop();
//                    mService.loadHomeHeadList(new TumccaService.OnLoadFinished<WorksInfo>() {
//                        @Override
//                        public void onSuccess(List<WorksInfo> resultList) {
//                            addWorksHead(resultList);
//                        }
//
//                        @Override
//                        public void onFail(String message) {
//
//                        }
//                    });
//                }
                if(null!=mWorksLoader){
                    mWorksLoader.loadHeadWorks();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }


    private static class ViewHolder {
        ImageView imageView;
        TextView tvDesc;
        ImageView ivAuthor;
        TextView tvAuthorName;
        boolean isLayout = false;
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
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView_picture);
                viewHolder.tvDesc = (TextView) convertView.findViewById(R.id.textView_describe);
                viewHolder.ivAuthor = (ImageView) convertView.findViewById(R.id.imageView_avatar);
                viewHolder.tvAuthorName = (TextView) convertView.findViewById(R.id.textView_author);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();

            PictureInfo pictureInfo = mWorksInfoList.get(position).picInfo;
            //预先用图片的尺寸对imageView进行布局
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                    parent.getWidth() / 2, parent.getWidth() * pictureInfo.height / (2 * pictureInfo.width));
            viewHolder.imageView.setLayoutParams(param);
            viewHolder.isLayout = true;

            String imageUrl = PictureServer.getInstance().getPictureUrl(mWorksInfoList.get(position).picInfo.id);

            if (viewHolder.imageView.getTag() == null || !viewHolder.imageView.getTag().equals(imageUrl)) {
                mImageLoader.displayImage(PictureServer.getInstance().getPictureUrl(
                                mWorksInfoList.get(position).picInfo.id), viewHolder.imageView, mImageOptionsWorks,
                        new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                view.setAlpha(0);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, final View view, Bitmap loadedImage) {

                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < 20; i++) {
                                            final float value = 1.0f / 20 * i;
                                            try {
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        view.setAlpha(value);
                                                        view.invalidate();
                                                    }
                                                });
                                                Thread.currentThread().sleep(10);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                t.start();

                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        }, new ImageLoadingProgressListener() {
                            @Override
                            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                                if (null != view) {
                                    view.setAlpha(0);
                                }
                            }
                        });
                viewHolder.imageView.setTag(imageUrl);
            }

            viewHolder.tvDesc.setText(mWorksInfoList.get(position).title);

            Profile profile = mWorksInfoList.get(position).profile;//mMapProfile.get(mWorksInfoList.get(position).author);
            if (null != profile) {
                viewHolder.tvAuthorName.setText(profile.pseudonym);

                String avatarUrl = "drawable://" + R.drawable.default_avatar;
                if (profile.avatar > PictureServer.INVALID_AVATAR_ID) {
                    avatarUrl = UserServer.getInstance().getAvatarUrl(profile.avatar);
                }

                if (viewHolder.ivAuthor.getTag() == null || !viewHolder.ivAuthor.getTag().equals(avatarUrl)) {
                    mImageLoader.displayImage(avatarUrl, viewHolder.ivAuthor, mImageOptionsAvatar);
                    viewHolder.ivAuthor.setTag(avatarUrl);
                }
            }
            return convertView;
        }
    }
}