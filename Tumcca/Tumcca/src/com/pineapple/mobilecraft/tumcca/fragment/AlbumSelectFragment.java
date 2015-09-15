package com.pineapple.mobilecraft.tumcca.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Constants;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/7/3.
 * 用于创建作品时选择专辑，默认专辑放在首位
 * 用户可以调用{@link AlbumCreateFragment}来创建专辑
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        this.getDialog().setTitle(mContext.getString(R.string.select_albums));

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

        RelativeLayout layoutCreate = (RelativeLayout)view.findViewById(R.id.layout_create);
        layoutCreate.setClickable(true);
        layoutCreate.setOnClickListener(new View.OnClickListener() {
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
                mAlbumList = WorksManager.getInstance().getMyAlbumList();
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
        mAlbumCreateFragment.show(getChildFragmentManager(), "create_album");
    }

    private class AlbumAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            //注意有个默认专辑
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
            View view = mContext.getLayoutInflater().inflate(R.layout.item_album, null);
            ImageView ivCover = (ImageView)view.findViewById(R.id.imageView_album_sample);

            TextView tvName = (TextView)view.findViewById(R.id.textView_album_name);

            tvName.setText(mAlbumList.get(position).title);

            List<Integer> cover = mAlbumList.get(position).cover;
            if(null!=cover&&cover.size()>0){
                DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(Constants.IMAGE_ROUNDED_CORNER_RADIUS)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(cover.get(0), 48, 1), ivCover, imageOptions);

            }
           return view;
        }
    }
}