package com.photoselector.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.photoselector.R;
import com.photoselector.model.PhotoModel;

import java.util.concurrent.Executors;

/**
 * @author Aizaz AZ
 *
 */

public class PhotoItem extends LinearLayout implements OnCheckedChangeListener,
		OnLongClickListener {


	static DisplayImageOptions options;
	private ImageView ivPhoto;
	private CheckBox cbPhoto;
	private onPhotoItemCheckedListener listener;
	private PhotoModel photo;
	private boolean isCheckAll;
	private onItemClickListener l;
	private int position;
	private AsyncTask mTask;

	private PhotoItem(Context context) {
		super(context);
	}

	public PhotoItem(Context context, onPhotoItemCheckedListener listener) {
		this(context);
		LayoutInflater.from(context).inflate(R.layout.layout_photoitem, this,
				true);
		this.listener = listener;

		setOnLongClickListener(this);

		ivPhoto = (ImageView) findViewById(R.id.iv_photo_lpsi);
		cbPhoto = (CheckBox) findViewById(R.id.cb_photo_lpsi);

		cbPhoto.setOnCheckedChangeListener(this); // CheckBoxѡ��״̬�ı������
		BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
		factoryOptions.inSampleSize = 8;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_picture_loading)
				.showImageOnFail(R.drawable.ic_picture_loadfailed)
				.cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.resetViewBeforeLoading(true).considerExifParams(false).decodingOptions(factoryOptions)
				.bitmapConfig(Bitmap.Config.RGB_565).build();


	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (!isCheckAll) {
			listener.onCheckedChanged(photo, buttonView, isChecked); // ����������ص�����
		}
		// ��ͼƬ�䰵���߱���
		if (isChecked) {
			setDrawingable();
			ivPhoto.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		} else {
			ivPhoto.clearColorFilter();
		}
		photo.setChecked(isChecked);
		setIndex(photo.getIndex());
	}

	/** ����·���µ�ͼƬ��Ӧ������ͼ */
	public void setImageDrawable(final PhotoModel photo) {
		if(null!=this.photo){
			this.photo.photoView = null;
		}
		this.photo = photo;
		// You may need this setting form some custom ROM(s)
		/*
		 * new Handler().postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { ImageLoader.getInstance().displayImage(
		 * "file://" + photo.getOriginalPath(), ivPhoto); } }, new
		 * Random().nextInt(10));
		 */
//		Executors.newSingleThreadExecutor().submit(new Runnable() {
//			@Override
//			public void run() {
		if(mTask!=null){
			mTask.cancel(true);
		}
		AsyncTask<String, String, BitmapFactory.Options> task = new AsyncTask<String, String, BitmapFactory.Options>() {
			@Override
			protected BitmapFactory.Options doInBackground(String... params) {
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				bitmapOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(photo.getOriginalPath(), bitmapOptions);
				options.getDecodingOptions().inSampleSize = Math.max(bitmapOptions.outWidth/200, bitmapOptions.outHeight/200);
				return bitmapOptions;
			}

			@Override
			protected void onPostExecute(BitmapFactory.Options result) {
				//之前没有图片链接
				if(null==ivPhoto.getTag()){
					ImageLoader.getInstance().displayImage(
							"file://" + photo.getOriginalPath(), ivPhoto, options);
					ivPhoto.setTag(photo.getOriginalPath());
				}
				//之前的图片链接和现在不一致
				else if(!ivPhoto.getTag().equals(photo.getOriginalPath())){
					if(null!=ivPhoto.getDrawable()){
						Bitmap bitmap = ((BitmapDrawable)ivPhoto.getDrawable()).getBitmap();
						if(bitmap!=null&&!bitmap.isRecycled()){
							//ivPhoto.setImageDrawable(null);
							//bitmap.recycle();
						}
					}

					ImageLoader.getInstance().displayImage(
							"file://" + photo.getOriginalPath(), ivPhoto, options);
					ivPhoto.setTag(photo.getOriginalPath());


				}
			}
		};

		task.execute("");
		mTask = task;


//			}
//		});


		photo.photoView = this;

	}

	private void setDrawingable() {
		ivPhoto.setDrawingCacheEnabled(true);
		ivPhoto.buildDrawingCache();
	}

	@Override
	public void setSelected(boolean selected) {
		if (photo == null) {
			return;
		}
		isCheckAll = true;
		cbPhoto.setChecked(selected);
//		if(){
//
//		}
//		cbPhoto.setText(photo.getIndex() + "");
		isCheckAll = false;


	}

	public void setOnClickListener(onItemClickListener l, int position) {
		this.l = l;
		this.position = position;
	}

	// @Override
	// public void
	// onClick(View v) {
	// if (l != null)
	// l.onItemClick(position);
	// }

	/** ͼƬItemѡ���¼������� */
	public static interface onPhotoItemCheckedListener {
		public void onCheckedChanged(PhotoModel photoModel,
				CompoundButton buttonView, boolean isChecked);
	}

	/** ͼƬ����¼� */
	public interface onItemClickListener {
		public void onItemClick(int position);
	}

	@Override
	public boolean onLongClick(View v) {
		if (l != null)
			l.onItemClick(position);
		return true;
	}

	public void setIndex(int index){
		if(index !=-1){
			cbPhoto.setText(index + "");
		}
		else{
			cbPhoto.setText("");
		}

	}

	public void setEnable(boolean enable){
		if(enable){
			cbPhoto.setVisibility(View.VISIBLE);
		}
		else{
			cbPhoto.setVisibility(View.GONE);
		}
	}

}
