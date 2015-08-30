package com.pineapple.mobilecraft.tumcca.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.activity.PictureEditActivity2;
import com.pineapple.mobilecraft.tumcca.activity.WorksCreateActivity2;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yihao on 8/18/15.
 *
 * facebook风格的图片创建列表
 * 输入参数，图库中的图片列表
 */
public class WorkCreateFragment extends BaseListFragment {
    List<WorkCreateItem> mWorkItems = new ArrayList<WorkCreateItem>();
    DisplayImageOptions mImageOptionsWorks;
    TumccaService mTumccaService;
    private Album mAlbum = Album.DEFAULT_ALBUM;
    private TextView mTvAlbumTitle;
    private ImageView mIvAlbumCover;
    private RelativeLayout mLayoutAlbum;
    boolean mIsBatch = false;
    private String mDescBatch = "";

    String []bigNum={"一","二","三","四","五","六","七","八","九"};

    public WorkCreateFragment(){
        setLayout(R.layout.fragment_works_create);

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

        mImageOptionsWorks = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public void buildView(View view){
        super.buildView(view);

        addAlbumView(view);
    }

    //添加图片
    public void addPictures(List<Picture> pictureList){
        if(null!=pictureList){
            for(Picture picture:pictureList){
                WorkCreateItem item = new WorkCreateItem();
                item.id = mWorkItems.size();
                item.picture = picture;
                mWorkItems.add(item);
            }
        }
    }

    //更新图片
    public void updatePicture(List<Picture> pictures){
        if(null!=pictures){
            for(WorkCreateItem item: mWorkItems){
                for(Picture picture:pictures){
                    if(item.picture.id == picture.id){
                        item.picture = picture;
                    }
                }
            }
            refresh();
        }
    }

    public void setService(TumccaService service){
        mTumccaService = service;
    }

    private void removeItem(WorkCreateItem item){
        mWorkItems.remove(item);
        clear();
    }

    public void submitWorks(){
        if(mWorkItems.isEmpty()){
            Toast.makeText(getActivity(), "请添加图片",Toast.LENGTH_SHORT).show();
            return;
        }

        List<Picture> pictures = new ArrayList<Picture>();
        List<Works> workses = new ArrayList<Works>();
        for(int i=0; i< mWorkItems.size(); i++){

            Works works = new Works();
            works.title = mWorkItems.get(i).desc;
            if(TextUtils.isEmpty(works.title)){
                Toast.makeText(getActivity(), "请填写图片说明",Toast.LENGTH_SHORT).show();
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
     *  添加专辑视图
     */
    private void addAlbumView(View view){
        mLayoutAlbum = (RelativeLayout)view.findViewById(R.id.layout_choose_album);
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
    private void showAlbumSelectDialog(){
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
     * @param album
     */
    private void setSelectedAlbum(Album album){
        mAlbum = album;
        mTvAlbumTitle.setText(album.title);
        List<Integer>cover = album.cover;
        if(null!=cover&&cover.size()>0){
            //Picasso.with(CalligraphyCreateActivity.this).load(PictureServer.getInstance().getPictureUrl(pictureInfo.id)).resize(48,48).centerCrop().into(mIvAlbumCover)
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(cover.get(0), 48, 1), mIvAlbumCover, mImageOptionsWorks);
        }

    }

    /**
     *
     */
    private  class WorkCreateItem implements ListItem{
        String desc;
        Picture picture;
        long id;
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
//                if(mIsBatch){
//                    String string = s.toString();
//                    desc = string.substring(0, string.length() - " 一".length());
//                }
//                else{
//                    desc = s.toString();
//                }
                desc = s.toString();

            }
        };

        public void editPic(){
            //拉起图片编辑页面
            //Picture picture = new Picture(null, imgLocalUrl);
            List<Picture> pictures = new ArrayList<Picture>();
            pictures.add(picture);
            PictureEditActivity2.startActivity(getActivity(), WorksCreateActivity2.REQ_PIC_EDIT, pictures, 0);
        }

        public void removeSelf(){
            //调用fragment删除自己
            WorkCreateFragment.this.removeItem(this);
        }


        @Override
        public void bindViewHolder(ListViewHolder viewHolder) {
            final PhotoEditViewHolder vh = (PhotoEditViewHolder)viewHolder;
            //显示图片
            String path = Uri.fromFile(new File(picture.localPath)).toString();
            ImageLoader.getInstance().displayImage(path, vh.mIvPic, mImageOptionsWorks);
            vh.mIvPic.setRotation(picture.rotArc);

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

            //绑定编辑描述

            vh.mEtxDesc.removeTextChangedListener((TextWatcher) vh.mEtxDesc.getTag());

            vh.mEtxDesc.addTextChangedListener(textWatcher);
            vh.mEtxDesc.setTag(textWatcher);
            if(mIsBatch){
                vh.mEtxDesc.setText(mDescBatch + " " + bigNum[mWorkItems.indexOf(WorkCreateItem.this)]);
            }
            else{
                vh.mEtxDesc.setText(desc);
            }


//            if(mIsBatch){
//                vh.mEtxDesc.setText(mDescBatch + " " + mWorkItems.indexOf(WorkCreateItem.this));
//            }

            //绑定批量图片说明
            if(mWorkItems.indexOf(WorkCreateItem.this)==0){
                vh.mCBxBatch.setVisibility(View.VISIBLE);
                vh.mCBxBatch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            mIsBatch = true;
                            mDescBatch = desc;
                            //applyBatch(mDescBatch);
                            refresh();
                        }
                        else{
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
                            // vh.mEtxDesc.set
                        }
                        return false;
                    }

                });
            }
            else{
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

    private static class PhotoEditViewHolder extends ListViewHolder{

        public TextView mTvPictureEdit;
        public EditText mEtxDesc;
        public ImageView mIvPic;
        public ImageButton mIbDelete;
        public CheckBox mCBxBatch;
        public PhotoEditViewHolder(View itemView) {
            super(itemView);
            mTvPictureEdit = (TextView) itemView.findViewById(R.id.textView_edit);
            mEtxDesc = (EditText) itemView.findViewById(R.id.editText_desc);
            mIvPic = (ImageView) itemView.findViewById(R.id.imageView_pic);
            mIbDelete = (ImageButton) itemView.findViewById(R.id.imageButton_delete);
            mCBxBatch = (CheckBox) itemView.findViewById(R.id.checkBox_batch);
        }
    }

//    private void applyBatch(String descBatch){
//        int index = 0;
//        for(WorkCreateItem item:mWorkItems){
//            item.desc = descBatch + " " + bigNum[index++];
//        }
//        refresh();
//    }
}
