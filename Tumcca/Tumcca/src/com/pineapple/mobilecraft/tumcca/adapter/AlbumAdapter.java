package com.pineapple.mobilecraft.tumcca.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Constants;
import com.pineapple.mobilecraft.tumcca.activity.AlbumDetailActivity;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/24/15.
 */
public class AlbumAdapter extends BaseAdapter{
    private List<Album> mAlbumList = new ArrayList<Album>();
    int mParentWidth = 0;

    private Activity mActivity;

    public AlbumAdapter(Activity activity){
        if(null==activity){
            throw new NullPointerException("activiy is null");
        }
        mActivity = activity;
    }

    public void setAlbumList(final List<Album> albumList){
        if(null==albumList){
            throw new NullPointerException("setAlbumList is null");
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlbumList.clear();
                mAlbumList.addAll(albumList);
                notifyDataSetChanged();
            }
        });
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
        TextView tvTitle = (TextView)view.findViewById(R.id.textView_album_name);
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

        bindDelete(view, album);
        return view;
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

    private void bindLikeCollect(View view, final Album album){
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
                    notifyDataSetChanged();
                    mActivity.sendBroadcast(new Intent(Constants.ACTION_ALBUMS_CHANGE));
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
                    mActivity.sendBroadcast(new Intent(Constants.ACTION_ALBUMS_CHANGE));
                    notifyDataSetChanged();
                }
                else
                {
                    UserManager.getInstance().requestLogin();
                    //Toast.makeText(mContext, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void bindDelete(View view, final Album album){
        LinearLayout layoutImage = (LinearLayout)view.findViewById(R.id.layout_image);

        layoutImage.setLongClickable(true);
        if(album.author == UserManager.getInstance().getCurrentUserId()&&album.id!=Album.DEFAULT_ID){
            layoutImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final View viewDialog = mActivity.getLayoutInflater().inflate(R.layout.dialog_album, null);
                    final AlertDialog dialog = new AlertDialog.Builder(mActivity).setView(viewDialog).create();
                    viewDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Executors.newSingleThreadExecutor().submit(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    WorksServer.removeAlbum(UserManager.getInstance().getCurrentToken(null), album.id);
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent();
                                            intent.setAction("remove_album");
                                            intent.putExtra("id", album.id);
                                            mActivity.sendBroadcast(intent);
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            });

                        }
                    });
                    dialog.show();
                    return false;
                }
            });
        }
    }
}
