package com.pineapple.mobilecraft.tumcca.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.HalfRoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.activity.UserActivity;
import com.pineapple.mobilecraft.tumcca.activity.WorkDetailActivity;
import com.pineapple.mobilecraft.tumcca.data.PictureInfo;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.PictureManager;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.util.logic.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 8/25/15.
 */
public class WorkAdapter extends BaseAdapter {
    static final int CORNER_RADIUS = 5;
    List<WorksInfo> mWorksInfoList = new ArrayList<WorksInfo>();
    Activity mActivity;
    DisplayImageOptions mImageOptionsWorks;
    ImageLoader mImageLoader;

    public WorkAdapter(Activity activity) {
        mActivity = activity;

        mImageOptionsWorks = new DisplayImageOptions.Builder()
                .displayer(new HalfRoundedBitmapDisplayer(Util.dip2px(TumccaApplication.applicationContext,
                        CORNER_RADIUS))).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(false).build();
        mImageLoader = ImageLoader.getInstance();
    }

    public void setWorks(final List<WorksInfo> worksInfoList) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWorksInfoList.clear();
                mWorksInfoList.addAll(worksInfoList);
                notifyDataSetChanged();
            }
        });
    }

    private class ViewHolder {
        ImageView ivPic;
        TextView tvDesc;
        ImageView ivAuthor;
        TextView tvAuthorName;
        RelativeLayout rlWork;
        RelativeLayout rlAuthor;
        TextView tvLike;
        TextView tvComment;

        public void bindViews(View container, int pic, int desc, int author, int authorName,
                              int layWork, int layAuthor, int like, int comment) {
            ivPic = (ImageView) container.findViewById(pic);
            tvDesc = (TextView) container.findViewById(desc);
            ivAuthor = (ImageView) container.findViewById(author);
            tvAuthorName = (TextView) container.findViewById(authorName);
            rlWork = (RelativeLayout) container.findViewById(layWork);
            rlAuthor = (RelativeLayout) container.findViewById(layAuthor);
            tvLike = (TextView) container.findViewById(like);
            tvComment = (TextView) container.findViewById(comment);
        }

        public void bindWork(int position, ViewGroup parent) {
            PictureInfo pictureInfo = mWorksInfoList.get(position).picInfo;
            //预先用图片的尺寸对imageView进行布局
            int width = (parent.getWidth() - 60)/2;
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                    width, width * pictureInfo.height / ( pictureInfo.width));
            ivPic.setLayoutParams(param);

            String imageUrl = PictureServer.getInstance().getPictureUrl(mWorksInfoList.get(position).picInfo.id);

            if (ivPic.getTag() == null || !ivPic.getTag().equals(imageUrl)) {
                mImageLoader.displayImage(PictureServer.getInstance().getPictureUrl(
                                mWorksInfoList.get(position).picInfo.id), ivPic, mImageOptionsWorks,
                        new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                view.setAlpha(0);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //view.setAlpha(100);
                                        Animation alpha = AnimationUtils.loadAnimation(mActivity, R.anim.image_alpha);
                                        view.startAnimation(alpha);
                                        view.setAlpha(100);
                                    }
                                });
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        }, null);
                ivPic.setTag(imageUrl);
            }

            tvDesc.setText(mWorksInfoList.get(position).title);
            rlWork.setTag(new Integer(position));
            rlWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWorks(mWorksInfoList.get((Integer) v.getTag()));
                }
            });

            tvLike.setText(mWorksInfoList.get(position).likes + "");
            tvComment.setText(mWorksInfoList.get(position).comments + "");
        }

        public void bindAuthor(int position) {

            rlAuthor.setTag(new Integer(position));
            rlAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAuthor(mWorksInfoList.get((Integer) v.getTag()).author);
                }
            });

            Profile profile = mWorksInfoList.get(position).profile;//mMapProfile.get(mWorksInfoList.get(position).author);
            if (null != profile) {
                tvAuthorName.setText(profile.pseudonym);
                PictureManager.getInstance().displayAvatar(ivAuthor, profile.avatar, 16);
            }


        }
    }

    //public List<WorksInfo> mListWorks = new ArrayList<WorksInfo>();

    @Override
    public int getCount() {
        if (null != mWorksInfoList) {
            Log.v("Tumcca", "Home fragment size " + mWorksInfoList.size());
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
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_works, null);
            viewHolder.bindViews(convertView, R.id.imageView_picture, R.id.textView_describe, R.id.imageView_avatar,
                    R.id.textView_author, R.id.layout_work, R.id.layout_author, R.id.textView_like, R.id.textView_comment);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.bindWork(position, parent);
        viewHolder.bindAuthor(position);
        return convertView;
    }

    public void openWorks(WorksInfo calligraphy) {
        WorkDetailActivity.startActivity(calligraphy, mActivity);
    }

    public void openAuthor(int authorId) {
        UserActivity.startActivity(mActivity, authorId);
    }

//    private void parseWorks(final List<WorksInfo> worksInfoList) {
//        if(null!=worksInfoList){
//            for (int i = 0; i < worksInfoList.size(); i++) {
//                if (!mMapProfile.containsKey(worksInfoList.get(i).author)) {
//                    Profile profile = UserManager.getInstance().getUserProfile(worksInfoList.get(i).author);
//                    worksInfoList.get(i).profile = profile;
//                    mMapProfile.put(worksInfoList.get(i).author, profile);
//                }
//                else{
//                    worksInfoList.get(i).profile = mMapProfile.get(worksInfoList.get(i).author);
//                }
//
//            }
//        }
//
//    }
}
