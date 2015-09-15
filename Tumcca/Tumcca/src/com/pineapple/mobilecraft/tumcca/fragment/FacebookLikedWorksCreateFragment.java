package com.pineapple.mobilecraft.tumcca.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.activity.PhotoChoose;
import com.pineapple.mobilecraft.tumcca.activity.PictureEditActivity2;
import com.pineapple.mobilecraft.tumcca.activity.FacebookLikedWorksCreateActivity;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import com.pineapple.mobilecraft.tumcca.utility.Utility;
import com.pineapple.mobilecraft.widget.RotateImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yihao on 8/18/15.
 * <p/>
 * facebook风格的图片创建列表
 * 输入参数，图库中的图片列表
 * 比较纠结的地方是：
 * 1 图片进行旋转后，尺寸要重新计算，否则会盖住其他控件。
 * 2 ConvertView会导致EditText关联的数据混乱。
 * 3 批量添加图片描述的逻辑比较繁琐。
 */
public class FacebookLikedWorksCreateFragment extends BaseListFragment {
    //widgets
    private TextView mTvAlbumTitle;
    private ImageView mIvAlbumCover;
    private RelativeLayout mLayoutAlbum;
    private TextView mTvAddPicture;

    //data
    private List<WorkCreateItem> mWorkItems = new ArrayList<WorkCreateItem>();
    private static DisplayImageOptions mImageOptionsWorks;
    private TumccaService mTumccaService;

    //默认专辑
    private Album mAlbum = Album.DEFAULT_ALBUM;

    boolean mIsBatch = false;

    private String mDescBatch = "";  //批量处理使用的图片说明

    private Uri mPhotoTakenUri;   //拍照使用的uri

    private String[] bigNum = {"一", "二", "三", "四", "五", "六", "七", "八", "九"}; //数字对应的中文

    public FacebookLikedWorksCreateFragment() {
        Log.v(TumccaApplication.TAG, "WorksCreateFragment id " + this.hashCode());
        setLayout(R.layout.fragment_works_create);

        if(mItemLoader == null){
            setItemLoader(new ItemLoader() {
                @Override
                public List<ListItem> loadHead() {
                    return Arrays.asList(mWorkItems.toArray(new ListItem[0]));
                }

                @Override
                public List<ListItem> loadTail(int page) {
                    return null;
                }
            });
        }

        if(null == mImageOptionsWorks){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            mImageOptionsWorks = new DisplayImageOptions.Builder()
                    .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(false).decodingOptions(options).build();
        }
    }

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Log.v(TumccaApplication.TAG, "FacebookLikedWorksCreateFragment onCreate  " + this.hashCode());


    }

    @Override
    public void buildView(View view, Bundle savedInstanceState) {
        if(null!=savedInstanceState){
            mWorkItems = new Gson().fromJson(savedInstanceState.getString("works_items"), new TypeToken<List<WorkCreateItem>>(){}.getType());
        }
        addAlbumView(view);

        addAddPhotoView(view);

        super.buildView(view, savedInstanceState);

    }

    public Uri getPhotoUri() {
        return mPhotoTakenUri;
    }

    //设置图片
    public void setPictures(List<Picture> pictureList) {
        if (null != pictureList) {
            mWorkItems.clear();
            for (Picture picture : pictureList) {
                WorkCreateItem item = new WorkCreateItem();
                item.id = mWorkItems.size();
                item.picture = picture;
                mWorkItems.add(item);
            }
        }
    }

    //添加图片
    public void addPictures(List<Picture> pictureList) {
        if (null != pictureList) {
            for (Picture picture : pictureList) {
                WorkCreateItem item = new WorkCreateItem();
                item.id = mWorkItems.size();
                item.picture = picture;
                mWorkItems.add(item);
            }
            reload();
        }
    }

    //更新图片
    public void updatePicture(List<Picture> pictures) {
        if (null != pictures) {
            for (WorkCreateItem item : mWorkItems) {
                for (Picture picture : pictures) {
                    if (item.picture.id == picture.id) {
                        item.picture = picture;
                    }
                }
            }
            refresh();
        }
    }

    public void bindTumccaService(TumccaService service) {
        mTumccaService = service;
    }

    //发布作品
    public void submitWorks() {
        if (mWorkItems.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.please_add_picture), Toast.LENGTH_SHORT).show();
            return;
        }

        List<Picture> pictures = new ArrayList<Picture>();
        List<Works> workses = new ArrayList<Works>();
        for (int i = 0; i < mWorkItems.size(); i++) {

            Works works = new Works();
            works.title = mWorkItems.get(i).desc;
            if (TextUtils.isEmpty(works.title)) {
                Toast.makeText(getActivity(), getString(R.string.please_enter_description), Toast.LENGTH_SHORT).show();
                return;
            }
            works.albumId = mAlbum.id;
            works.category = 1;
            pictures.add(mWorkItems.get(i).picture);
            workses.add(works);
        }
        mTumccaService.uploadWorks(pictures, workses);
        getActivity().finish();
    }

    /**
     * 添加专辑视图
     */
    private void addAlbumView(View view) {
        mLayoutAlbum = (RelativeLayout) view.findViewById(R.id.layout_choose_album);
        mLayoutAlbum.setClickable(true);
        mLayoutAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbumSelectDialog();
            }
        });

        mIvAlbumCover = (ImageView) view.findViewById(R.id.imageView_album);
        mTvAlbumTitle = (TextView) view.findViewById(R.id.textView_album_name);

        setSelectedAlbum(WorksManager.getInstance().getLatestAlbum());
    }

    /**
     * 弹出专辑选择对话框
     */
    private void showAlbumSelectDialog() {
        AlbumSelectFragment fragment = new AlbumSelectFragment();
        fragment.setAlbumSelectListener(new AlbumSelectFragment.OnAlbumSelectListener() {
            @Override
            public void onAlbumSelect(Album album) {
                WorksManager.getInstance().setLatestAlbum(album);
                setSelectedAlbum(album);
            }
        });
        fragment.show(getChildFragmentManager(), "album_select");

    }

    /**
     * 专辑被选择时做的处理
     *
     * @param album
     */
    private void setSelectedAlbum(Album album) {
        mAlbum = album;
        mTvAlbumTitle.setText(album.title);
        List<Integer> cover = album.cover;
        if (null != cover && cover.size() > 0) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(cover.get(0), 48, 1), mIvAlbumCover, mImageOptionsWorks);
        }

    }

    //添加图片视图
    private void addAddPhotoView(View view) {
        mTvAddPicture = (TextView) view.findViewById(R.id.textView_add_picture);
        mTvAddPicture.setClickable(true);
        mTvAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoTakenUri = Utility.createPhotoUri(mActivity);
                PhotoChoose photoChoose = new PhotoChoose();
                photoChoose.setUri(mPhotoTakenUri);
                photoChoose.show(getChildFragmentManager(), "WorksPhotoChoose");
            }
        });
    }

    //将批量描述应用到所有作品上
    private void applyBatch(){
        for (WorkCreateItem item:mWorkItems){
            item.desc = mDescBatch  + " " + bigNum[mWorkItems.indexOf(item)];
        }
    }

    //移除作品
    private void removeItem(WorkCreateItem item) {
        mWorkItems.remove(item);
        reload();
    }

    private static class PhotoEditViewHolder extends ListViewHolder {

        public TextView mTvPictureEdit;
        public EditText mEtxDesc;
        public RotateImageView mIvPic;
        public ImageButton mIbDelete;
        public CheckBox mCBxBatch;
        public RelativeLayout mLayout;

        public PhotoEditViewHolder(View itemView) {
            super(itemView);
            mTvPictureEdit = (TextView) itemView.findViewById(R.id.textView_edit);
            mEtxDesc = (EditText) itemView.findViewById(R.id.editText_desc);
            mIvPic = (RotateImageView) itemView.findViewById(R.id.imageView_pic);
            mIbDelete = (ImageButton) itemView.findViewById(R.id.imageButton_delete);
            mCBxBatch = (CheckBox) itemView.findViewById(R.id.checkBox_batch);
            mLayout = (RelativeLayout) itemView;
        }
    }

    /**
     *
     */
    private class WorkCreateItem implements ListItem {
        String desc;
        Picture picture;
        long id;
        int width = 0;
        int height = 0;

        //String index = "";
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                desc = s.toString();
            }
        };

        public void editPic() {
            //拉起图片编辑页面
            List<Picture> pictures = new ArrayList<Picture>();
            pictures.add(picture);
            PictureEditActivity2.startActivity(getActivity(), FacebookLikedWorksCreateActivity.REQ_PIC_EDIT, pictures, 0);
        }

        public void removeSelf() {
            //调用fragment删除自己
            FacebookLikedWorksCreateFragment.this.removeItem(this);
        }


        @Override
        public void bindViewHolder(ListViewHolder viewHolder) {

            if(mImageOptionsWorks == null){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                mImageOptionsWorks = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                        .considerExifParams(false).decodingOptions(options).resetViewBeforeLoading(true).build();
            }

            final PhotoEditViewHolder vh = (PhotoEditViewHolder) viewHolder;
            //显示图片
            String path = "file://" + Uri.fromFile(new File(picture.localPath)).getPath();
            RoundedBitmapDisplayer displayer = (RoundedBitmapDisplayer) mImageOptionsWorks.getDisplayer();
            displayer.setRotation(picture.rotArc);
            ImageLoader.getInstance().displayImage(path, vh.mIvPic, mImageOptionsWorks, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (Math.abs(picture.rotArc - 0) < 0.000001) {
                        height = vh.mIvPic.getMeasuredHeight();
                        width = vh.mIvPic.getMeasuredWidth();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            if (width != 0 && height != 0) {
                if (((int) picture.rotArc) % 180 == 90) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vh.mIvPic.getLayoutParams();
                    params.height = width;//vh.mLayout.getMeasuredWidth();
                    params.width = height;//params.height*vh.mIvPic.getDrawable().getBounds().height()/vh.mIvPic.getDrawable().getBounds().width();
                    vh.mIvPic.setLayoutParams(params);
                } else if (((int) picture.rotArc) % 180 == 0) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vh.mIvPic.getLayoutParams();
                    params.height = height;
                    params.width = width;
                    vh.mIvPic.setLayoutParams(params);
                }
            }

            //绑定图片编辑
            vh.mTvPictureEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPic();
                }
            });

            //绑定删除
            vh.mIbDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeSelf();
                }
            });

            //移除之前的图片说明，绑定现在的
            vh.mEtxDesc.removeTextChangedListener((TextWatcher) vh.mEtxDesc.getTag());
            vh.mEtxDesc.addTextChangedListener(textWatcher);
            vh.mEtxDesc.setTag(textWatcher);
            vh.mEtxDesc.setText(desc);

            //绑定批量图片说明
            if (mWorkItems.indexOf(WorkCreateItem.this) == 0) {
                vh.mCBxBatch.setVisibility(View.VISIBLE);
                vh.mCBxBatch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mIsBatch = true;
                            mDescBatch = desc;
                            applyBatch();
                            refresh();
                        } else {
                            mIsBatch = false;
                            desc = mDescBatch;
                            refresh();
                        }
                    }
                });

                vh.mEtxDesc.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (vh.mCBxBatch.isChecked()) {
                            vh.mCBxBatch.setChecked(false);
                        }
                        return false;
                    }

                });
            } else {
                vh.mCBxBatch.setVisibility(View.GONE);
            }

        }

        @Override
        public ListViewHolder getViewHolder(LayoutInflater inflater) {
            return new PhotoEditViewHolder(inflater.inflate(R.layout.item_picture_edit, null));
        }

        @Override
        public long getId() {
            return id;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putString("works_items", new Gson().toJson(mWorkItems));
    }

    @Override
    public void onResume(){
//        if(mItemLoader == null){
//            setItemLoader(new ItemLoader() {
//                @Override
//                public List<ListItem> loadHead() {
//                    return Arrays.asList(mWorkItems.toArray(new ListItem[0]));
//                }
//
//                @Override
//                public List<ListItem> loadTail(int page) {
//                    return null;
//                }
//            });
//        }
//
//
//        if(null == mImageOptionsWorks){
//            mImageOptionsWorks = new DisplayImageOptions.Builder()
//                    .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
//                    .considerExifParams(false).build();
//        }
        super.onResume();

    }

    @Override
    public void onDestroy(){
        mWorkItems.clear();
        reload();
        super.onDestroy();
    }
}
