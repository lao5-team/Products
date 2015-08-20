package com.pineapple.mobilecraft.tumcca.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.HalfRoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.activity.PictureEditActivity2;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import com.pineapple.mobilecraft.util.logic.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yihao on 8/18/15.
 */
public class WorkCreateFragment extends BaseListFragment {
    List<WorkCreateItem> mItems = new ArrayList<WorkCreateItem>();
    //List<String> mUrls = new ArrayList<String>();
    DisplayImageOptions mImageOptionsWorks;
    TumccaService mService;

    public void setService(TumccaService service){
        mService = service;
    }

    public WorkCreateFragment(){
        setLayout(R.layout.fragment_works_create);

        setItemLoader(new ItemLoader() {
            @Override
            public List<ListItem> loadHead() {
                return Arrays.asList(mItems.toArray(new ListItem[0]));
            }

            @Override
            public List<ListItem> loadTail(int page) {
                return null;
            }
        });

        mImageOptionsWorks = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    public void setPhotos(List<String> urls){
        if(null!=urls){
            //mUrls.addAll(urls);
            for(String url:urls){
                WorkCreateItem item = new WorkCreateItem();
                item.id = mItems.size();
                item.imgLocalUrl = url;
                mItems.add(item);
            }
        }
    }

    private  class WorkCreateItem implements ListItem{
        String desc;
        String imgLocalUrl;
        int albumId;
        long id;
        WorkCreateFragment mFragment;

        public void editPic(){
            //拉起图片编辑页面
            Picture picture = new Picture(null, imgLocalUrl);
            List<Picture> pictures = new ArrayList<Picture>();
            pictures.add(picture);
            PictureEditActivity2.startActivity(getActivity(), 0, pictures, 0);
        }

        public void removeSelf(){
            //调用fragment删除自己
            WorkCreateFragment.this.removeItem(this);
        }


        @Override
        public void bindViewHolder(ListViewHolder viewHolder) {
            PhotoEditViewHolder vh = (PhotoEditViewHolder)viewHolder;
            String path = Uri.fromFile(new File(imgLocalUrl)).toString();
            ImageLoader.getInstance().displayImage(path, vh.mIvPic, mImageOptionsWorks);
            //vh.mIvPic
            vh.mTvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPic();
                }
            });

            vh.mIbDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeSelf();
                }
            });

            vh.mEtxDesc.addTextChangedListener(new TextWatcher() {
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
            });
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

        public TextView mTvEdit;
        public EditText mEtxDesc;
        public ImageView mIvPic;
        public ImageButton mIbDelete;
        public PhotoEditViewHolder(View itemView) {
            super(itemView);
            mTvEdit = (TextView) itemView.findViewById(R.id.textView_edit);
            mEtxDesc = (EditText) itemView.findViewById(R.id.editText_desc);
            mIvPic = (ImageView) itemView.findViewById(R.id.imageView_pic);
            mIbDelete = (ImageButton) itemView.findViewById(R.id.imageButton_delete);
        }
    }

    public void removeItem(WorkCreateItem item){
        remove(item.getId());
    }

    public void sumbit(){

        List<Picture> pictures = new ArrayList<Picture>();
        List<Works> workses = new ArrayList<Works>();
        for(int i=0; i<mItems.size(); i++){
            Picture picture =new Picture(null ,mItems.get(i).imgLocalUrl);
            Works works = new Works();
            works.title = mItems.get(i).desc;
            if(TextUtils.isEmpty(works.title)){
                Toast.makeText(getActivity(), "请填写图片的描述描述信息",Toast.LENGTH_SHORT).show();
                return;
            }
            works.albumId = 0;
            works.category = 1;
            pictures.add(picture);
            workses.add(works);

        }
        mService.uploadWorks(pictures, workses);
        getActivity().finish();
    }
}
