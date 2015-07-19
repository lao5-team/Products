package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.graphics.Bitmap;
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
    public WorksListFragment(){
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onResume(){
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 解析数据
     * @param worksInfoList
     */
    private void parseWorks(final List<WorksInfo> worksInfoList){
        for (int i=0; i<worksInfoList.size(); i++){
            Profile profile = UserManager.getInstance().getUserProfile(worksInfoList.get(i).author);
            mMapProfile.put(worksInfoList.get(i).author, profile);
        }
    }

    @Override
    public void setScrollListener(OnScrollListener listener) {
        mScrollListener = listener;
    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     * 将数据添加到头部
     * @param worksInfoList
     */
    @Override
    public void addWorksHead(final List<WorksInfo> worksInfoList) {
        if(mWorksInfoList.size()>0){
            int topId = mWorksInfoList.get(0).id;
            int index = worksInfoList.size();
            for(int i=0; i<worksInfoList.size(); i++){
                if(topId==worksInfoList.get(i).id){
                    index = i;
                    break;
                }
            }
            if(worksInfoList.subList(0, index).size()==0){
                Toast.makeText(mContext, "当前没有新作品发布", Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(mContext, "当前有" + worksInfoList.subList(0, index).size() + "个新作品发布", Toast.LENGTH_SHORT).show();
                mWorksInfoList.addAll(0, worksInfoList.subList(0, index));
            }

        }
        else{
            mWorksInfoList.addAll(worksInfoList);
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                parseWorks(worksInfoList);
                if(null!=mContext){
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
        });
        thread.start();
    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     * @param worksInfoList
     */
    @Override
    public void addWorksTail(final List<WorksInfo> worksInfoList) {
        mWorksInfoList.addAll(worksInfoList);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                parseWorks(worksInfoList);
                if(null!=mContext){
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
        });
        thread.start();
    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     * 清空数据
     */
    public void clearWorks(){
        mWorksInfoList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addWorksListView(View view) {
        StaggeredGridView listView = (StaggeredGridView)view;
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
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        mImageLoader.pause();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(visibleItemCount+firstVisibleItem==totalItemCount){
                    if(null!= mScrollListener){
                        mScrollListener.onBottom();
                    }
                }
            }
        });
    }

    @Override
    public void openWorks(WorksInfo calligraphy) {
        CalligraphyDetailActivity.startActivity(calligraphy, mContext);
    }

//    /**
//     *
//     * @param worksInfoList
//     */
//    public void setWorksList(final List<WorksInfo> worksInfoList){
//        mWorksInfoList.clear();
//        mWorksInfoList.addAll(worksInfoList);
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i=0; i<mWorksInfoList.size(); i++){
//                    Profile profile = UserManager.getInstance().getUserProfile(worksInfoList.get(i).author);
//                    mMapProfile.put(worksInfoList.get(i).author, profile);
//                }
//                if(null!=mContext){
//                    mContext.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.v("Tumcca", "addWorkList");
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    });
//                }
//
//            }
//        });
//        t.start();
//
//
//    }

//    public void addWorkList(final List<WorksInfo> worksInfoList) {
//
//        for (int i = 0; i < worksInfoList.size(); i++) {
//            Profile profile = UserManager.getInstance().getUserProfile(worksInfoList.get(i).author);
//            mMapProfile.put(worksInfoList.get(i).author, profile);
//        }
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mWorksInfoList.addAll(worksInfoList);
//                Log.v("Tumcca", "addWorkList");
//                mAdapter.notifyDataSetChanged();
//            }
//        });
//
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calligraphy_list, container, false);
        StaggeredGridView listView = (StaggeredGridView)view.findViewById(R.id.list);
        addWorksListView(listView);


        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.button_normal_red);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!= mScrollListener){
                    mScrollListener.onTop();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }



    private static class ViewHolder{
        ImageView imageView;
        TextView tvDesc;
        ImageView ivAuthor;
        TextView tvAuthorName;
        boolean isLayout = false;
    }

    private class PictureAdapter extends BaseAdapter{
        public List<WorksInfo> mListWorks = new ArrayList<WorksInfo>();
        @Override
        public int getCount() {
            if(null!=mListWorks){
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
            if(convertView==null){
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_works, null);
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageView_picture);
                viewHolder.tvDesc = (TextView)convertView.findViewById(R.id.textView_describe);
                viewHolder.ivAuthor = (ImageView)convertView.findViewById(R.id.imageView_avatar);
                viewHolder.tvAuthorName = (TextView)convertView.findViewById(R.id.textView_author);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();

            PictureInfo pictureInfo = mWorksInfoList.get(position).picInfo;
            //预先用图片的尺寸对imageView进行布局
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                        parent.getWidth()/2, parent.getWidth()*pictureInfo.height/(2*pictureInfo.width));
                viewHolder.imageView.setLayoutParams(param);
                viewHolder.isLayout = true;

            String imageUrl = PictureServer.getInstance().getPictureUrl(mWorksInfoList.get(position).picInfo.id);

            if(viewHolder.imageView.getTag() == null||!viewHolder.imageView.getTag().equals(imageUrl)){
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
                                        for (int i=0; i<20; i++){
                                            final float value = 1.0f/20*i;
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
                                if(null!=view){
                                    view.setAlpha(0);
                                }
                            }
                        });
                viewHolder.imageView.setTag(imageUrl);
            }

            viewHolder.tvDesc.setText(mWorksInfoList.get(position).title);

            Profile profile = mMapProfile.get(mWorksInfoList.get(position).author);
            if(null!=profile){
                viewHolder.tvAuthorName.setText(profile.pseudonym);

                if(profile.avatar>PictureServer.INVALID_AVATAR_ID){
                    String avatarUrl = UserServer.getInstance().getAvatarUrl(profile.avatar);
                    if(viewHolder.ivAuthor.getTag() == null||!viewHolder.ivAuthor.getTag().equals(avatarUrl)){
                        mImageLoader.displayImage(avatarUrl, viewHolder.ivAuthor, mImageOptionsAvatar);
                        viewHolder.ivAuthor.setTag(avatarUrl);
                    }
                }
            }
            return convertView;
        }
    }
}