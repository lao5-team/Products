package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.PictureInfo;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/7/3.
 * 用于创建作品时选择专辑
 */
public class AlbumSelectFragment extends DialogFragment {
    //ImageView mImageView;
    ListView mAlbumListView;
    AlbumCreateFragment mAlbumCreateFragment;
    AlbumAdapter mAlbumAdapter;
    List<Album> mAlbumList = new ArrayList<Album>();
    OnAlbumSelectListener mListener;
    Activity mContext = null;
    private final int PAGE_COUNT = 1;
    private final int PAGE_SIZE = 20;
    private final int WIDTH = 400;
    public interface OnAlbumSelectListener{
        public void onAlbumSelect(Album album);
    }

    public AlbumSelectFragment(){
        mAlbumCreateFragment = new AlbumCreateFragment();
        mAlbumAdapter = new AlbumAdapter();
        mAlbumCreateFragment.setAlbumCreateListener(new AlbumCreateFragment.OnAlbumCreateListener() {
            @Override
            public void onResult(boolean result) {
                loadMyAlbums();
            }
        });


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setStyle(DialogFragment.STYLE_NORMAL, R.style.my_dialog_activity_style);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        this.getDialog().setTitle("选择专辑");

    }

    public void setAlbumList(List<Album> albumList){
        if(null!=albumList){
            mAlbumList = albumList;
        }
        mAlbumAdapter.notifyDataSetChanged();
    }

    public void setAlbumSelectListener(OnAlbumSelectListener listener){
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_album_select, null);

        RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.layout_create);
        layout.setClickable(true);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbumCreate();
            }
        });
        mAlbumListView = (ListView)view.findViewById(R.id.listView_albums);
        mAlbumListView.setAdapter(mAlbumAdapter);
        mAlbumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mListener !=null){
                    if(position == 0){
                        mListener.onAlbumSelect(Album.DEFAULT_ALBUM);
                    }
                    else{
                        mListener.onAlbumSelect(mAlbumList.get(position));
                    }
                }

                getFragmentManager().beginTransaction().remove(AlbumSelectFragment.this).commit();
            }
        });
        loadMyAlbums();

        return view;
    }

    private void loadMyAlbums(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                mAlbumList = WorksManager.getInstance().getMyAlbumList();   // WorksServer.getMyAlbumList(UserManager.getInstance().getCurrentToken());

                //mAlbumList.add(0, Album.DEFAULT_ALBUM);

//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAlbumAdapter.notifyDataSetChanged();
//                    }
//                });

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAlbumAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
        t.start();
    }

    private void showAlbumCreate(){
        mAlbumCreateFragment.show(getFragmentManager(), "create_album");
    }

    private class AlbumAdapter extends BaseAdapter{


        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            //注意有个默认专辑
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
            View view = mContext.getLayoutInflater().inflate(R.layout.item_album, null);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageView_album_sample);

            TextView textView = (TextView)view.findViewById(R.id.textView_album_title);
//            if(position == 0){
//                textView.setText(mContext.getString(R.string.default_album));
//            }
//            else{
                textView.setText(mAlbumList.get(position).title);
 //           }

//            List<WorksInfo> worksInfoList = mAlbumList.get(position).worksInfoList;
//            if(null!=worksInfoList&&worksInfoList.size()>0){
//                PictureInfo pictureInfo = worksInfoList.get(0).picInfo;
//                if(null!=pictureInfo){
//                    //Picasso.with(mContext).load(PictureServer.getInstance().getPictureUrl(pictureInfo.id)).resize(48,48).centerCrop().into(imageView);
//                    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//                            .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
//                            .build();
//                    ImageLoader imageLoader = ImageLoader.getInstance();
//                    imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(pictureInfo.id, 48, 1), imageView, imageOptions);
//
//                }
//            }
            List<Integer> cover = mAlbumList.get(position).cover;
            if(null!=cover&&cover.size()>0){
                DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(cover.get(0), 48, 1), imageView, imageOptions);

            }
           return view;
        }
    }
}