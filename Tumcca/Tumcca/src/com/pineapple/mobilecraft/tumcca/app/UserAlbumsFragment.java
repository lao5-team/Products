package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.widget.ExpandGridView;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by yihao on 15/7/2.
 */
public class UserAlbumsFragment extends Fragment {
    private final int PAGE_COUNT = 1;
    private final int PAGE_SIZE = 20;
    private final int WIDTH = 400;
    private int mUserId = -1;
    private List<Album> mAlbumList = new ArrayList<Album>();
    private ExpandGridView mEGVAlbumView = null;
    private AlbumsAdapter mAlbumsAdapter = new AlbumsAdapter();
    Activity mContext;
    TextView mTvAlbumCount;
    public UserAlbumsFragment(){
        Log.v("Tumcca", "UserAlbumsFragment");
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Album> albumList = WorksServer.getMyAlbumList(UserManager.getInstance().getCurrentToken());
                mAlbumList.addAll(albumList);
                mAlbumList.add(0, Album.DEFAULT_ALBUM);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addCountView(mTvAlbumCount);
                    }
                });
                try {
                    for (Album album : mAlbumList) {
                        List<WorksInfo> worksInfoList = WorksManager.getInstance().getAlbumWorks(album.id);
                        if (worksInfoList.size() > 0) {
                            album.worksInfoList = worksInfoList;
                        } else {
                            worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(), album.id, PAGE_COUNT, PAGE_SIZE, WIDTH);
                            album.worksInfoList = worksInfoList;
                            WorksManager.getInstance().putAlbumWorks(album.id, worksInfoList);
                        }

                    }
                }catch (ConcurrentModificationException e){
                    e.printStackTrace();
                }
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAlbumsAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
        t.start();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mAlbumList.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, null);
        mEGVAlbumView = (ExpandGridView)view.findViewById(R.id.gridview_albums);
        addAlbumsView(mEGVAlbumView);
        mTvAlbumCount = (TextView)view.findViewById(R.id.textView_album_count);
        addCountView(mTvAlbumCount);

        return view;
    }

    public void setUser(int userId){
        mUserId = userId;


    }



    public void addCountView(TextView countView){

        countView.setText("您有" + mAlbumList.size() + "个专辑");
    }

    public void addAlbumsView(ExpandGridView albumsView){
        albumsView.setAdapter(mAlbumsAdapter);
        albumsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = mAlbumList.get(position);
                AlbumCalligraphyListActivity.startActivity(mContext, album.id, album.title);
            }
        });
    }

    private class AlbumsAdapter extends BaseAdapter{

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return mAlbumList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return null;
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mContext.getLayoutInflater().inflate(R.layout.item_preview_album, null);
            AbsListView.LayoutParams param1 = new AbsListView.LayoutParams(
                                parent.getWidth()/2, parent.getWidth()*2/3);
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
            return view;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mAlbumsAdapter.notifyDataSetChanged();
    }

}
