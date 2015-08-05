package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.widget.ExpandGridView;

import java.util.ArrayList;
import java.util.List;

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
    private AlbumsAdapter mAlbumsAdapter = new AlbumsAdapter();
    Activity mContext;
    TextView mTvAlbumCount;
    AlbumListLoader mAlbumsLoader;
    CircleProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    ImageLoader mImageLoader;
    boolean mScrollingIdle = false;
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
         * 取得数据后，要调用{@link #addWorksTail}
         */
        public void loadTailAlbums();
    }

    public void setAlbumLoader(AlbumListLoader loader){
        mAlbumsLoader = loader;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        mTvAlbumCount = (TextView)view.findViewById(R.id.textView_album_count);
        mProgressBar = (CircleProgressBar) view.findViewById(R.id.progressBar);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        mEGVAlbumView = (ExpandGridView)view.findViewById(R.id.gridview_albums);
        addAlbumListView(mEGVAlbumView);
        setupProgressBar();
        return view;
    }

    public void addAlbumListView(View view) {
        ExpandGridView gridView = (ExpandGridView) view;
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
                        mAlbumsLoader.loadTailAlbums();
                    }
                }
            }
        });
        if (null != mAlbumsLoader) {
            List<Album> initialAlbums = mAlbumsLoader.getInitialAlbums();
            if (null != initialAlbums) {
                mAlbumList.clear();
                mAlbumList.addAll(initialAlbums);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAlbumsAdapter.mAlbumList.clear();
                        mAlbumsAdapter.mAlbumList.addAll(mAlbumList);
                        mAlbumsAdapter.notifyDataSetChanged();
                    }
                });

            }
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mContext = activity;


    }

    private  void setupProgressBar(){
        mProgressBar.setShowArrow(true);
        if(null != mAlbumsLoader && mAlbumsLoader.getInitialAlbums()==null && mAlbumList.size()==0){
            mAlbumsLoader.loadHeadAlbums();
        }else{
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private class AlbumsAdapter extends BaseAdapter {

        private List<Album> mAlbumList = new ArrayList<Album>();
        @Override
        public int getCount() {
            return mAlbumList.size();
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
            View view = mContext.getLayoutInflater().inflate(R.layout.item_album_preview, null);
            AbsListView.LayoutParams param1 = new AbsListView.LayoutParams(
                    parent.getWidth()/2, (int)(parent.getWidth()*3.5/4));
            view.setLayoutParams(param1);
            TextView tvTitle = (TextView)view.findViewById(R.id.textView_album_title);
            Album album = mAlbumList.get(position);
            tvTitle.setText(album.title);
            ImageView imageView_0 = (ImageView)view.findViewById(R.id.imageView_0);
            ImageView imageView_1 = (ImageView)view.findViewById(R.id.imageView_1);
            ImageView imageView_2 = (ImageView)view.findViewById(R.id.imageView_2);
            ImageView imageView_3 = (ImageView)view.findViewById(R.id.imageView_3);
            int parent_width = parent.getWidth();
            if(parent_width < 100){
                parent_width = 800;
            }
            if(album.worksInfoList!=null){
                if(album.worksInfoList.size()>0){
                    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                            .displayer(new RoundedBitmapDisplayer(10)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(album.worksInfoList.get(0).picInfo.id, parent_width/2, 1), imageView_0, imageOptions);
                }
                if(album.worksInfoList.size() > 1) {
                    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                            .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(album.worksInfoList.get(1).picInfo.id, parent_width/6, 1), imageView_1, imageOptions);
                }
                if (album.worksInfoList.size()>2){
                    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                            .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(album.worksInfoList.get(2).picInfo.id, parent_width/6, 1), imageView_2, imageOptions);
                }
                if (album.worksInfoList.size()>3){
                    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                            .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(album.worksInfoList.get(3).picInfo.id, parent_width/6, 1), imageView_3, imageOptions);
                }
            }

            bindLikeCollect(view, album.id);
            return view;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mAlbumsAdapter.notifyDataSetChanged();
    }

    public void bindLikeCollect(View view, final int albumId){
        RelativeLayout layout_like = (RelativeLayout) view.findViewById(R.id.layout_like);
        layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(UserManager.getInstance().isLogin())
                {
                    int userId = UserManager.getInstance().getCurrentUserId();
                    boolean ret = WorksServer.likeAlbum(UserManager.getInstance().getCurrentToken(), String.valueOf(albumId), String.valueOf(userId));
                    if(ret)
                    {
//                        collectionImg.setImageDrawable(getResources().getDrawable(R.drawable.coolyou_post_collection_selected));
//                        Animation anim = AnimationUtils.loadAnimation(WorkDetailActivity.this, R.anim.coolyou_zan_scale);
//                        collectionImg.startAnimation(anim);

                    }
                }
                else
                {
                    Toast.makeText(mContext, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                }
            }
        });

        RelativeLayout layout_collect = (RelativeLayout) view.findViewById(R.id.layout_collect);
        layout_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(UserManager.getInstance().isLogin())
                {
                    int userId = UserManager.getInstance().getCurrentUserId();
                    boolean ret = WorksServer.collectAlbum(UserManager.getInstance().getCurrentToken(), String.valueOf(albumId), String.valueOf(userId));
                    if(ret)
                    {
//                        collectionImg.setImageDrawable(getResources().getDrawable(R.drawable.coolyou_post_collection_selected));
//                        Animation anim = AnimationUtils.loadAnimation(WorkDetailActivity.this, R.anim.coolyou_zan_scale);
//                        collectionImg.startAnimation(anim);

                    }
                }
                else
                {
                    Toast.makeText(mContext, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     * 将数据添加到头部
     *
     * @param albumList
     */
    public void addAlbumsHead(final List<Album> albumList) {
        if (mAlbumList.size() > 0) {
            int topId = mAlbumList.get(0).id;
            int index = albumList.size();
            for (int i = 0; i < albumList.size(); i++) {
                if (topId == albumList.get(i).id) {
                    index = i;
                    break;
                }
            }
            if (albumList.subList(0, index).size() == 0) {
                Toast.makeText(mContext, getString(R.string.there_is_no_new_works), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(mContext, getString(R.string.there_is_works, albumList.subList(0, index).size()), Toast.LENGTH_SHORT).show();
                mAlbumList.addAll(0, albumList.subList(0, index));
            }

        } else {
            mAlbumList.addAll(albumList);
        }
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mAlbumsAdapter.mAlbumList.clear();
                mAlbumsAdapter.mAlbumList.addAll(AlbumListFragment.this.mAlbumList);
                mAlbumsAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 将数据添加到底部，需要在主线程中调用
     *
     * @param worksInfoList
     */
    public void addAlbumsTail(final List<Album> albumList) {
        mAlbumList.addAll(albumList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlbumsAdapter.mAlbumList.clear();
                mAlbumsAdapter.mAlbumList.addAll(mAlbumList);
                mAlbumsAdapter.notifyDataSetChanged();
            }
        });
    }
}