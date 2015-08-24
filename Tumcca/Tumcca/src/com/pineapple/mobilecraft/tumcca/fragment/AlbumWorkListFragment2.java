package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.activity.AlbumDetailActivity;
import com.pineapple.mobilecraft.tumcca.activity.AlbumsListActivity;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.widget.ExpandGridView;
import com.pineapple.mobilecraft.widget.TitleTabBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/3/15.
 */
public class AlbumWorkListFragment2 extends Fragment implements TitleTabBar.TitleTabClickListener {

    private class AlbumsAdapter extends BaseAdapter{

        @Override
        public void notifyDataSetChanged(){

                int listViewHeight = 0;
                int adaptCount = mAlbumsAdapter.getCount();
                for(int i=0;i<adaptCount;i=i+2){
                    View temp = mAlbumsAdapter.getView(i, null, mEGVAlbums);
                    temp.measure(0,0);
                    listViewHeight += temp.getMeasuredHeight();

                }
                ViewGroup.LayoutParams layoutParams = mEGVAlbums.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

                //int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
                //super.onMeasure(widthMeasureSpec, expandSpec);
                layoutParams.height = listViewHeight;
                mEGVAlbums.setLayoutParams(layoutParams);
                //mEGVAlbums.setSize(0, listViewHeight);
                Log.v(TumccaApplication.TAG, "BaseListFragment:applyListviewHeightWithChild:Height=" + mEGVAlbums.getMeasuredHeight());
                //mEGVAlbumView.
                super.notifyDataSetChanged();
        }

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
            View view = mActivity.getLayoutInflater().inflate(R.layout.item_album_preview, null);
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
    }

    public static final int MODE_LIKE = 0;
    public static final int MODE_COLLECT = 1;

    AlbumsAdapter mAlbumsAdapter;
    int mParentWidth = 0;
    private int mDataMode = MODE_LIKE;
    private Activity mActivity;
    private ViewPager mContentPager;
    //private WorkListFragment mWorkListFragment;
    private  ExpandGridView mEGVAlbums;
    private List<Album> mAlbumList = new ArrayList<Album>();
    private AlbumListFragment mAlbumListFragment = new AlbumListFragment();
    private TextView mTvAlbum, mTvWorks;
    private int mAuthorId = -1;
    public void setAuthorId(int authorId){
        mAuthorId = authorId;
    }

    public void setDataMode(int mode){
        mDataMode = mode;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albumwork2, container, false);

//        mBtnAlbum = (Button)view.findViewById(R.id.button_album);
//        mBtnAlbum.setSelected(true);
//        getChildFragmentManager().beginTransaction().replace(R.id.container, mAlbumListFragment).commit();
//
//        mBtnWork = (Button)view.findViewById(R.id.button_work);
//        mBtnWork.setSelected(false);
//        setupButtons();
        buildAlbumsView(view);
        buildWorksView(view);
        return view;
    }

    @Override
    public void callback(int index) {
        mContentPager.setCurrentItem(index);
//        titleTabBar.setTitleState(index);
    }

//    public void addWorksFragment(WorkListFragment fragment){
//        mWorkListFragment = fragment;
//    }
//
//    public void addAlbumsFragment(AlbumListFragment fragment){
//        mAlbumListFragment = fragment;
//    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        mActivity = activity;
    }

    /**
     *
     * @param view
     */
    private void buildAlbumsView(View view){
        /*TODO 加载4个专辑
        添加GridView
        添加AlbumAdapter
        指定高度
        添加See All按钮
        拉起 AlbumsListActivity
        */
//        mAlbumListFragment = new AlbumListFragment();
//        mAlbumListFragment.setAlbumLoader(new AlbumListFragment.AlbumListLoader() {
//            @Override
//            public List<Album> getInitialAlbums() {
//                if(mDataMode == MODE_LIKE){
//                    return WorksServer.getLikeAlbums(mAuthorId, 1, 4);
//
//                }
//                else if(mDataMode == MODE_COLLECT){
//                    return WorksServer.getCollectAlbums(mAuthorId, 1, 4);
//                }
//                return null;
//            }
//
//            @Override
//            public void loadHeadAlbums() {
//
//            }
//
//            @Override
//            public void loadTailAlbums(int page) {
//
//            }
//        });
        //getChildFragmentManager().beginTransaction().replace(R.id.container_albums, mAlbumListFragment).commit();

        mAlbumsAdapter = new AlbumsAdapter();
        mEGVAlbums = (ExpandGridView)view.findViewById(R.id.gridview_albums);
        mEGVAlbums.setAdapter(mAlbumsAdapter);

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                if(mDataMode == MODE_LIKE){
                    mAlbumList =  WorksServer.getLikeAlbums(mAuthorId, 1, 4);

                }
                else if(mDataMode == MODE_COLLECT){
                    mAlbumList =  WorksServer.getCollectAlbums(mAuthorId, 1, 4);

                }
                WorksServer.parseAlbumList(UserManager.getInstance().getCurrentToken(null), mAlbumList);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAlbumsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });


        mTvAlbum = (TextView) view.findViewById(R.id.textView_all_albums);
        mTvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumsListActivity.startActivity(getActivity(), mDataMode, mAuthorId);
            }
        });
    }

    private void buildWorksView(View view){
        /*TODO 加载4个作品
        添加{@link com.pineapple.mobilecraft.tumcca.fragment.AlbumListFragment}
        添加See All按钮*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("authorId", mAuthorId);
        outState.putInt("dataMode", mDataMode);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState){
        super.onViewStateRestored(savedInstanceState);
        if(null!=savedInstanceState){
            mAuthorId = savedInstanceState.getInt("authorId");
            mDataMode = savedInstanceState.getInt("dataMode");
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
                    Toast.makeText(mActivity, album.isLiked?"喜欢成功":"取消喜欢成功", Toast.LENGTH_SHORT).show();
                    mAlbumsAdapter.notifyDataSetChanged();
                }
                else{
                    UserManager.getInstance().requestLogin();

                }

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
                    Toast.makeText(mActivity, album.isCollected?"收藏成功":"取消收藏成功", Toast.LENGTH_SHORT).show();
                    mAlbumsAdapter.notifyDataSetChanged();
                }
                else
                {
                    UserManager.getInstance().requestLogin();
                    //Toast.makeText(mContext, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void bindAlbum(View view, final Album album){
        LinearLayout layoutImage = (LinearLayout)view.findViewById(R.id.layout_image);
        layoutImage.setClickable(true);
        layoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumDetailActivity.startActivity(mActivity, album.id, album.author, album.title);
            }
        });
    }
}