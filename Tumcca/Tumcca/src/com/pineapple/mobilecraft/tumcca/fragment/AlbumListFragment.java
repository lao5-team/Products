package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.domain.User;
import com.pineapple.mobilecraft.tumcca.activity.AlbumDetailActivity;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.widget.ExpandGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/4/15.
 *
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
         * 取得数据后，要调用{@link #addTail}
         */
        public void loadTailAlbums(int page);
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
                setAdapter();

            }
        }
    }


    @Override
    public void onAttach(Activity activity){
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
    public void onStart(){
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
    public void onDetach(){
        super.onDetach();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void removeAlbum(long id){
        for(Album album:mAlbumList){
            if(album.id == id){
                mAlbumList.remove(album);
                break;
            }
        }
        setAdapter();

    }

    private void setAdapter(){
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlbumsAdapter.mAlbumList.clear();
                mAlbumsAdapter.mAlbumList.addAll(mAlbumList);
                mAlbumsAdapter.notifyDataSetChanged();
            }
        });
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
            if(mParentWidth==0){
                mParentWidth = parent.getWidth();
            }
            View view = mContext.getLayoutInflater().inflate(R.layout.item_album_preview, null);
            AbsListView.LayoutParams param1 = new AbsListView.LayoutParams(
                    mParentWidth/2, (int)(mParentWidth*3.5/4));
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
            if(album.cover!=null){
                if(album.cover.size()>0){
                    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                            .displayer(new RoundedBitmapDisplayer(10)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(album.cover.get(0), parent_width/2, 1), imageView_0, imageOptions);
                }
                if(album.cover.size() > 1) {
                    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                            .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(album.cover.get(1), parent_width/6, 1), imageView_1, imageOptions);
                }
                if (album.cover.size()>2){
                    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                            .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(album.cover.get(2), parent_width/6, 1), imageView_2, imageOptions);
                }
                if (album.cover.size()>3){
                    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                            .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(album.cover.get(3), parent_width/6, 1), imageView_3, imageOptions);
                }
            }

            bindLikeCollect(view, album);

            bindAlbum(view, album);
            return view;
        }

        public void setData(List<Album> albums){
            mAlbumList.clear();
            mAlbumList.addAll(albums);
            notifyDataSetChanged();
        }
    }

    private void bindAlbum(View view, final Album album){
        LinearLayout layoutImage = (LinearLayout)view.findViewById(R.id.layout_image);
        layoutImage.setClickable(true);
        layoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumDetailActivity.startActivity(mContext, album.id, album.author, album.title);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
//        if(null!=getArguments()){
//            mParentWidth = getArguments().getInt("parentWidth");
//        }
        mAlbumsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        super.onPause();
//        Bundle bundle = new Bundle();
//        bundle.putInt("parentWidth", mParentWidth);
//        setArguments(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("parentWidth", mParentWidth);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState){
        super.onViewStateRestored(savedInstanceState);
        if(null!=savedInstanceState){
            mParentWidth = savedInstanceState.getInt("parentWidth");
        }
    }


    public void bindLikeCollect(View view, final Album album){
        RelativeLayout layout_like = (RelativeLayout) view.findViewById(R.id.layout_like);
        if(album.author == UserManager.getInstance().getCurrentUserId()){
            layout_like.setVisibility(View.GONE);
        }
        final TextView tvLike = (TextView)view.findViewById(R.id.textView_like);
        if(album.isLiked){
            tvLike.setText("取消喜欢");
        }
        else{
            tvLike.setText("喜欢");
        }
        layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(null!=UserManager.getInstance().getCurrentToken(null)){
                    if(album.isLiked){
                        WorksServer.dislikeAlbum(UserManager.getInstance().getCurrentToken(null), album.id);
                    }
                    else{
                        int userId = UserManager.getInstance().getCurrentUserId();
                        boolean ret = WorksServer.likeAlbum(UserManager.getInstance().getCurrentToken(null), album.id, userId);
                    }
                    album.isLiked = !album.isLiked;
                    Toast.makeText(mContext, album.isLiked?"喜欢成功":"取消喜欢成功", Toast.LENGTH_SHORT).show();
                    AlbumListFragment.this.mAlbumsAdapter.notifyDataSetChanged();
                }
                else{
                    UserManager.getInstance().requestLogin();

                }
//                if(UserManager.getInstance().isLogin())
//                {
//
//                }
//                else
//                {
//                    Toast.makeText(mContext, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
//                }

            }
        });

        RelativeLayout layout_collect = (RelativeLayout) view.findViewById(R.id.layout_collect);
        if(album.author == UserManager.getInstance().getCurrentUserId()){
            layout_collect.setVisibility(View.GONE);
        }
        TextView tvCollect = (TextView)view.findViewById(R.id.textView_collect);
        if(album.isCollected){
            tvCollect.setText("取消收藏");
        }
        else{
            tvCollect.setText("收藏");
        }
        layout_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(null!=UserManager.getInstance().getCurrentToken(null))
                {
                    if(album.isCollected){
                        WorksServer.discollectAlbum(UserManager.getInstance().getCurrentToken(null), album.id);

                    }
                    else
                    {
                        int userId = UserManager.getInstance().getCurrentUserId();
                        boolean ret = WorksServer.collectAlbum(UserManager.getInstance().getCurrentToken(null), album.id, userId);
                    }
                    album.isCollected = !album.isCollected;
                    Toast.makeText(mContext, album.isCollected?"收藏成功":"取消收藏成功", Toast.LENGTH_SHORT).show();
                    AlbumListFragment.this.mAlbumsAdapter.notifyDataSetChanged();
                }
                else
                {
                    UserManager.getInstance().requestLogin();
                    //Toast.makeText(mContext, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
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
                mTvAlbumCount.setText(getString(R.string.you_have_albums, mAlbumList.size()));
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                setAdapter();
            }
        });

        WorksServer.parseAlbumList(UserManager.getInstance().getCurrentToken(null), mAlbumList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlbumsAdapter.setData(mAlbumList);
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
                mTvAlbumCount.setText(getString(R.string.you_have_albums,mAlbumList.size()));
                mAlbumsAdapter.setData(mAlbumList);
            }
        });

        WorksServer.parseAlbumList(UserManager.getInstance().getCurrentToken(null), mAlbumList);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlbumsAdapter.setData(mAlbumList);
            }
        });
    }
}