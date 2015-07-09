package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.PictureInfo;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.ICalligraphyList;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.widget.waterfall.MultiColumnPullToRefreshListView;
import com.pineapple.mobilecraft.widget.waterfall.internal.PLA_AbsListView;
import com.pineapple.mobilecraft.widget.waterfall.internal.PLA_AdapterView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yihao on 15/6/4.
 */
public class CalligraphyListFragment extends Fragment implements ICalligraphyList{

    public static interface OnBottomScrollListener{
        public void onBottom();
    }
    OnBottomScrollListener mBottomScrollListener = null;
    List<WorksInfo> mWorksInfoList = new ArrayList<WorksInfo>();
    Activity mContext;
    PictureAdapter mAdapter;
    DisplayImageOptions mImageOptions;
    ImageLoader mImageLoader;
    HashMap<Integer, Profile> mMapProfile;
    public CalligraphyListFragment(){
        mMapProfile = new HashMap<Integer, Profile>();
        mAdapter = new PictureAdapter();
        mImageOptions = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(10)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        mImageLoader = ImageLoader.getInstance();
    }

    public void setBottomScrollListener(OnBottomScrollListener listener){
        mBottomScrollListener = listener;
    }

    public void setWorksList(final List<WorksInfo> worksInfoList){
        mWorksInfoList = worksInfoList;
        mAdapter.notifyDataSetChanged();
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
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        t.start();


    }

    public void addWorkList(final List<WorksInfo> worksInfoList){
        mWorksInfoList.addAll(worksInfoList);
        mAdapter.notifyDataSetChanged();
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
        MultiColumnPullToRefreshListView listView = (MultiColumnPullToRefreshListView)view.findViewById(R.id.list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                //PictureDetailActivity.startActivity(mContext, mWorksInfoList.get(position -1).picInfo.id);
                CalligraphyDetailActivity.startActivity(mWorksInfoList.get(position -1), mContext);
            }
        });
        //listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true ,true));
        listView.setOnScrollListener(new PLA_AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        mImageLoader.resume();
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
            public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
            //if(convertView==null){
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_works, null);
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageView_picture);
                viewHolder.tvDesc = (TextView)convertView.findViewById(R.id.textView_describe);
                viewHolder.ivAuthor = (ImageView)convertView.findViewById(R.id.imageView_avatar);
                viewHolder.tvAuthorName = (TextView)convertView.findViewById(R.id.textView_author);
                convertView.setTag(viewHolder);
            //}
            viewHolder = (ViewHolder) convertView.getTag();
            PictureInfo pictureInfo = mWorksInfoList.get(position).picInfo;
            //预先用图片的尺寸对imageView进行布局
            if(!viewHolder.isLayout){
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                        parent.getWidth()/2, parent.getWidth()*pictureInfo.height/(2*pictureInfo.width));
                viewHolder.imageView.setLayoutParams(param);
                viewHolder.isLayout = true;
            }

            mImageLoader.displayImage(PictureServer.getInstance().getPictureUrl(
                    mWorksInfoList.get(position).picInfo.id), viewHolder.imageView, mImageOptions);

            viewHolder.tvDesc.setText(mWorksInfoList.get(position).title);

            Profile profile = mMapProfile.get(mWorksInfoList.get(position).author);
            if(null!=profile){
                viewHolder.tvAuthorName.setText(profile.pseudonym);

                mImageLoader.displayImage(UserServer.getInstance().getAvatarUrl(profile.avatar), viewHolder.ivAuthor, mImageOptions);

            }
            return convertView;
        }
    }
}