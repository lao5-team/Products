package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.PictureInfo;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.ICalligraphyList;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.util.logic.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by yihao on 15/6/4.
 */
public class CalligraphyListFragment extends Fragment implements ICalligraphyList{

    public static interface OnBottomScrollListener{
        public void onBottom();
    }
    OnBottomScrollListener mBottomScrollListener = null;
    CopyOnWriteArrayList<WorksInfo> mWorksInfoList = new CopyOnWriteArrayList<WorksInfo>();
    Activity mContext;
    PictureAdapter mAdapter;
    DisplayImageOptions mImageOptionsWorks;
    DisplayImageOptions mImageOptionsAvatar;
    ImageLoader mImageLoader;
    HashMap<Integer, Profile> mMapProfile;
    public CalligraphyListFragment(){
        mMapProfile = new HashMap<Integer, Profile>();
        mAdapter = new PictureAdapter();
        mImageOptionsAvatar = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(Util.dip2px(DemoApplication.applicationContext, 5))).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();


        mImageOptionsWorks = new DisplayImageOptions.Builder()
                .displayer(new HalfRoundedBitmapDisplayer(Util.dip2px(DemoApplication.applicationContext, 5))).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        mImageLoader = ImageLoader.getInstance();
    }

    public void setBottomScrollListener(OnBottomScrollListener listener){
        mBottomScrollListener = listener;
    }

    public void setWorksList(final List<WorksInfo> worksInfoList){
        mWorksInfoList.clear();
        mWorksInfoList.addAll(worksInfoList);
        //mAdapter.notifyDataSetChanged();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<mWorksInfoList.size(); i++){
                    Profile profile = UserManager.getInstance().getUserProfile(worksInfoList.get(i).author);
                    mMapProfile.put(worksInfoList.get(i).author, profile);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("Tumcca", "addWorkList");
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        t.start();


    }

    public void addWorkList(final List<WorksInfo> worksInfoList){

        //mAdapter.notifyDataSetChanged();
        //Thread t = new Thread(new Runnable() {
            //@Override
            //public void run() {
                for (int i=0; i<worksInfoList.size(); i++){
                    Profile profile = UserManager.getInstance().getUserProfile(worksInfoList.get(i).author);
                    mMapProfile.put(worksInfoList.get(i).author, profile);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWorksInfoList.addAll(worksInfoList);
                        Log.v("Tumcca", "addWorkList");
                        mAdapter.notifyDataSetChanged();
                    }
                });
            //}
        //});
       // t.start();



    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calligraphy_list, container, false);
        StaggeredGridView listView = (StaggeredGridView)view.findViewById(R.id.list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //PictureDetailActivity.startActivity(mContext, mWorksInfoList.get(position -1).picInfo.id);
                CalligraphyDetailActivity.startActivity(mWorksInfoList.get(position), mContext);
            }
        });
        //listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true ,true));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        mImageLoader.resume();
                        //mAdapter.notifyDataSetChanged();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        mImageLoader.pause();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        //mImageLoader.pause();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(visibleItemCount+firstVisibleItem==totalItemCount){
                    //Log.e("log", "滑到底部");
                    if(null!=mBottomScrollListener){
                        mBottomScrollListener.onBottom();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void setCalligraphyList(List<Works> calligraphyList) {

    }

    @Override
    public void addCalligraphyListView() {

    }

    @Override
    public void openCalligraphy(Works calligraphy) {

    }


    private static class ViewHolder{
        ImageView imageView;
        TextView tvDesc;
        ImageView ivAuthor;
        TextView tvAuthorName;
        boolean isLayout = false;
    }

    private class PictureAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(null!=mWorksInfoList){
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
            if(convertView==null){
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_works, null);
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageView_picture);
                //viewHolder.imageView.setAlpha(0);
                viewHolder.tvDesc = (TextView)convertView.findViewById(R.id.textView_describe);
                viewHolder.ivAuthor = (ImageView)convertView.findViewById(R.id.imageView_avatar);
                viewHolder.tvAuthorName = (TextView)convertView.findViewById(R.id.textView_author);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            //return convertView;

            PictureInfo pictureInfo = mWorksInfoList.get(position).picInfo;
            //预先用图片的尺寸对imageView进行布局
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                        parent.getWidth()/2, parent.getWidth()*pictureInfo.height/(2*pictureInfo.width));
                viewHolder.imageView.setLayoutParams(param);
                viewHolder.isLayout = true;

            String imageUrl = PictureServer.getInstance().getPictureUrl(mWorksInfoList.get(position).picInfo.id);

            if(viewHolder.ivAuthor.getTag() == null||!viewHolder.imageView.getTag().equals(imageUrl)){
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

                String avatarUrl = UserServer.getInstance().getAvatarUrl(profile.avatar);
                if(viewHolder.ivAuthor.getTag() == null||!viewHolder.ivAuthor.getTag().equals(avatarUrl)){
                    mImageLoader.displayImage(avatarUrl, viewHolder.ivAuthor, mImageOptionsAvatar);
                    viewHolder.ivAuthor.setTag(avatarUrl);
                }

            }
            return convertView;
        }
    }
}