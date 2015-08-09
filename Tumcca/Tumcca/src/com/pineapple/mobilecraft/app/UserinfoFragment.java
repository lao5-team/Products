package com.pineapple.mobilecraft.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.Constant;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.server.MyServerManager;
import com.pineapple.mobilecraft.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author yh
 * 传入用户id,显示该用户信息
 * 
 */
public class UserinfoFragment extends Fragment {
	
	/* request code */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private String IMAGE_AVATAR = Constant.CACHE_DIR + File.separator + "avatar.jpg";
	/*data*/
	private MyUser mUser;
	
	/*UI Widget*/;
	private ImageView mImgAvatar;
	private TextView mTvName;
	private Button mBtnLogout;
	private TextView mTvDoing;
	private TextView mTvFinish;
	
	public UserinfoFragment(MyUser user)
	{
		//Assert.assertNotNull(user);
		if(null == user&&null!=getActivity())
		{
			Toast.makeText(getActivity(), "No current user, do you test in local mode?", Toast.LENGTH_SHORT).show();
			return;
		}
		
		mUser = user;
		//Log.v(DemoApplication.TAG, String.format("Userinfo is %s", MyUser.toJSON(user).toString()));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_userinfo, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		initUI();
	}
	
	
	public void initData()
	{
		
	}
	
	public void initUI()
	{
		mTvName = (TextView)getView().findViewById(R.id.textView_treasure_name);
		mTvName.setText(mUser.mName);
		mImgAvatar = (ImageView)getView().findViewById(R.id.imageView_avatar);
		if(null!=mUser.mImgUrl&&mUser.mImgUrl.length()>0)
		{
			Picasso.with(getActivity()).load(mUser.mImgUrl).into(mImgAvatar);
		}
		
		mImgAvatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		
		mTvDoing = (TextView)getView().findViewById(R.id.textView_doing_activity);
		mTvDoing.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ActivityListActivity.class);
				intent.putExtra("user_id", mUser.mID);
				intent.putExtra("type", ActivityListActivity.TYPE_DOING);
				getActivity().startActivity(intent);
			}
		});
		mTvFinish = (TextView)getView().findViewById(R.id.textView_finish_activity);
		mTvFinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ActivityListActivity.class);
				intent.putExtra("user_id", mUser.mID);
				intent.putExtra("type", ActivityListActivity.TYPE_FINISH);
				getActivity().startActivity(intent);				
			}
		});
		
		mBtnLogout = (Button)getView().findViewById(R.id.button_logout);
		mBtnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TumccaApplication.getInstance().logout();
				// 重新显示登陆页面
				((EntryActivity) getActivity()).finish();
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
		});
	}
	
	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(getActivity())
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (hasSdcard()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(IMAGE_AVATAR)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消时候
		if (resultCode != Activity.RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (hasSdcard()) {
					File tempFile = new File(IMAGE_AVATAR);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			mImgAvatar.setImageDrawable(drawable);
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(IMAGE_AVATAR);
				photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						String imgUrl = MyServerManager.getInstance()
								.uploadImage(new File(IMAGE_AVATAR));
						if (null != imgUrl) {
							mUser.mImgUrl = imgUrl;
							if (MyServerManager.getInstance().updateUserInfo(
									mUser)) {
								getActivity().runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(getActivity(), "头像修改成功",
												Toast.LENGTH_SHORT).show();
									}
								});
							} else {
								getActivity().runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(getActivity(), "头像修改失败",
												Toast.LENGTH_SHORT).show();
									}
								});
							}
						} else {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(getActivity(), "头像修改失败",
											Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				});
				t.start();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getActivity(), "头像上传失败", Toast.LENGTH_SHORT)
						.show();
			}

		}
	}
	
	public  boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
}
