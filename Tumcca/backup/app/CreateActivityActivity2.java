package com.pineapple.mobilecraft.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.*;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.*;
import com.pineapple.mobilecraft.Constant;

import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.adapter.ActivityCategoryAdapter;
import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.data.ActivityData.ActivityBuilder;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.DianpingDao.ComplexBusiness;
import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.server.MyServerManager;
import com.pineapple.mobilecraft.manager.UserManager;
import com.pineapple.mobilecraft.mediator.IActivityCreateMediator;
import com.pineapple.mobilecraft.R;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;

public class CreateActivityActivity2 extends Activity {


	private ImageView mIvXuanchuan;

	/* request code */
	private static final int MEMBERS_REQUEST_CODE = 0;
	private static final int BUSINESS_REQUEST_CODE = 1;
	private static final int DATE_REQUEST_CODE = 2;
	private static final int LOCATION_REQUEST_CODE = 3;
	private static final int IMAGE_REQUEST_CODE = 4;
	private static final int CAMERA_REQUEST_CODE = 5;
	private static final int RESULT_REQUEST_CODE = 6;

	private String[] items = new String[] { "选择本地图片", "拍照" };
	private String IMAGE_AVATAR = Constant.CACHE_DIR + File.separator + "avatar.jpg";

	//for yxpj
	private String [] mCategories = {"翡翠", "和田玉", "蜜蜡", "祖母绿", "红蓝宝", "珍珠"};
	public static class IntentBuilder
	{
		Intent mIntent;
		/** 决定是用来创建一个活动还是用来处理一个活动
		 *  
		 * @param useType 可以是USE_CREATE,USE_EDIT
		 */
		public IntentBuilder(Intent intent)
		{
			if(null == intent)
				throw new IllegalArgumentException("IntentBuilder intent cannot be null!");
			mIntent = intent;
		}
		
		public void setUseType(int useType)
		{
			mIntent.putExtra("use", useType);
		}
		
		public void setActivityID(String activityID)
		{
			mIntent.putExtra("activity_id", activityID);
		}
		
		public int getUseType()
		{
			return mIntent.getIntExtra("use", USE_CREATE);
		}
		
		public String getActivityID()
		{
			return mIntent.getStringExtra("activity_id");
		}
	};

	//UI Controls
	private EditText mEtxTitle;
	private EditText mEtxContent;
	private Button mBtnSelectBusiness;
	private Button mBtnSelectFriends;
	private Button mBtnSelectDate;
	private Button mBtnSelectLocation;
	private Button mBtnOK;
	private Button mBtnCancel;
	private CheckBox mCBPayMe;
	private CheckBox mCBPayAA;
	private CheckBox mCBPayOther;
	private com.pineapple.mobilecraft.widget.ExpandGridView mGVType;
	//Data
	private ActivityData mActivityData;
	private ComplexBusiness mComplexBusiness;
	private Date mBeginDate;
	private int mUseType;

	
	public static final int USE_CREATE = 4;
	public static final int USE_EDIT = 5;
	boolean mIsTimeSet = false;
	
	private IActivityCreateMediator mMediator = new IActivityCreateMediator() {
		private ActivityBuilder mActivityBuilder = new ActivityBuilder();
	    
		
		@Override
		public void setTitle(String title) {
			mActivityBuilder.setTitle(title);
			
		}
		
		@Override
		public void setTime(Date date) {
			if(null != date)
			{
				mIsTimeSet = true;
				mActivityBuilder.setBeginTime(date);
			}
			
		}

		
		@Override
		public void setMembers(ArrayList<MyUser> users) {
			String usersString = "";
			ArrayList<String> names = new ArrayList<String>();
			for(MyUser user:users)
			{
				usersString += user.mName + " ";
				names.add(user.mName);
			}
			mBtnSelectFriends.setText(usersString);
			mActivityBuilder.setInviteUsers(names);
			
		}

		/**
		 * 设置地点描述信息
		 *
		 * @param text
		 */
		@Override
		public void setLocDesc(String text)
		{
			mActivityBuilder.setLocDesc(text);
		}

		/**
		 * @param x
		 * @param y
		 */
		@Override
		public void setLocCoord(float x, float y)
		{
			mActivityBuilder.setLocCoord(x, y);
		}

		@Override
		public void setImgUrl(String url) {
			mActivityBuilder.setImgUrl(url);
		}

		@Override
		public void setContent(String content) {
			mActivityBuilder.setContent(content);
		}

		
		@Override
		public void onOKClicked() {
			if(checkDataComplete())
			{
				final ProgressDialog pd = new ProgressDialog(CreateActivityActivity2.this);
				pd.setMessage("正在创建活动...");
				pd.show();
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						
						if(TumccaApplication.isDebug)
						{
							UserManager.getInstance().getCurrentUser().startActivity(mActivityData);
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									if (!CreateActivityActivity2.this.isFinishing())
										pd.dismiss();
									addActivityToCalendar(mActivityData);
									Toast.makeText(getApplicationContext(), "创建活动成功", Toast.LENGTH_SHORT).show();
									CreateActivityActivity2.this.finish();		
								}
							});
										
						}
						else
						{
							mMediator.setCreator(UserManager.getInstance().getCurrentUser().mName);
							if(null != (mActivityData = mMediator.createActivityData()))
							{
								//UserManager.getInstance().getCurrentUser().startActivity(mActivityData);
								MyServerManager.getInstance().createActivity2(mActivityData);
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										if (!CreateActivityActivity2.this.isFinishing())
											pd.dismiss();
										addActivityToCalendar(mActivityData);
										Toast.makeText(getApplicationContext(), "创建活动成功", Toast.LENGTH_SHORT).show();
										CreateActivityActivity2.this.finish();
									}
								});
							}				
						}
						
					}
				});
				thread.start();
			}
		}
		
		@Override
		public void onCancelClicked() {
			CreateActivityActivity2.this.finish();	
		}

		@Override
		public ActivityData createActivityData() {
			return mActivityBuilder.create();
		}

		@Override
		public void setType(String type) {
			mActivityBuilder.setType(type);
		}

		@Override
		public void setCreator(String username) {
			mActivityBuilder.setCreator(username);
		}
		
		private void addActivityToCalendar(ActivityData data)
		{
			try
			{
				String[] projection = new String[] { "_id", "name" };
				Uri calendars = Uri.parse("content://com.android.calendar/calendars");
				Cursor managedCursor = getContentResolver().query(calendars, projection,
						null, null, null);
				if (managedCursor.moveToFirst()) {
					String calName;
					String calId;
					int nameColumn = managedCursor.getColumnIndex("name");
					int idColumn = managedCursor.getColumnIndex("_id");
					do {
						calName = managedCursor.getString(nameColumn);
						calId = managedCursor.getString(idColumn);
					} while (managedCursor.moveToNext());
					
					long startMillis = 0; 
					long endMillis = 0;     
					Calendar beginTime = Calendar.getInstance();
					beginTime.set(2014, 11, 31, 10, 30);
					startMillis = beginTime.getTimeInMillis();
					Calendar endTime = Calendar.getInstance();
					endTime.set(2014, 11, 31, 11, 0);
					endMillis = endTime.getTimeInMillis();	
				ContentValues event = new ContentValues(); 
				event.put(Events.DTSTART, data.mBeginDate.getTime());
				event.put(Events.DTEND, data.mBeginDate.getTime() + 3600000);
				event.put(Events.CALENDAR_ID, calId); 	 
				event.put(Events.TITLE, data.mTitle); 
				event.put(Events.DESCRIPTION, data.mContent); 
				event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID().toString());
				Uri uri = getContentResolver().insert(Events.CONTENT_URI, event);
				long eventID = Long.parseLong(uri.getLastPathSegment());
				event = new ContentValues(); 
				event.put(Reminders.MINUTES, 15);
				event.put(Reminders.EVENT_ID, eventID);
				event.put(Reminders.METHOD, Reminders.METHOD_ALERT);
				uri = getContentResolver().insert(Reminders.CONTENT_URI, event);
				} 
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
		}
	};
	private ActivityCategoryAdapter mCategoryAdapter;

	private String mSelectType = "翡翠";



	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		initData();
		
		initUI();
		
	}
	
	public void initUI()
	{
		setContentView(R.layout.activity_newactivity2);
		mEtxTitle = (EditText)findViewById(R.id.editText_title);
		mEtxContent = (EditText)findViewById(R.id.editText_content);
		mBtnSelectBusiness = (Button)findViewById(R.id.button_select_business);
		mBtnSelectBusiness.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CreateActivityActivity2.this, CreateActivityActivity.class);
				startActivityForResult(intent, BUSINESS_REQUEST_CODE);
			}
		});
		
		
		mBtnSelectFriends = (Button)findViewById(R.id.button_select_friends);
		mBtnSelectFriends.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CreateActivityActivity2.this, ActivityMembersActivity.class);
//				if(mActivityData.mStatus == ActivityData.BEGIN)
//				{
//					intent.putExtra("state", ActivityData.BEGIN);
//				}
				if(null!=mActivityData && null!=mActivityData.mUsers)
				{
					intent.putExtra("members", mActivityData.mUsers);
				}
				startActivityForResult(intent, MEMBERS_REQUEST_CODE);
			}
		});
		
		mBtnSelectDate = (Button)findViewById(R.id.button_select_date);
		mBtnSelectDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CreateActivityActivity2.this, DateActivity.class);
				startActivityForResult(intent, DATE_REQUEST_CODE);
			}
		});
		
		
		mBtnOK = (Button)findViewById(R.id.button_confirm);
		mBtnCancel = (Button)findViewById(R.id.button_cancel);	
		
		
		if(mUseType == USE_CREATE)
		{
			if(TumccaApplication.isDebug)
			{
				mActivityData = ActivityData.createTestData();
				updateView();
			}
			
			mBtnOK.setText("发起聚会");
			mBtnOK.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mMediator.onOKClicked();
				}
			});
			
			mBtnCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mMediator.onCancelClicked();
				}
			});
		}

		//for yxpj
		mIvXuanchuan = (ImageView)findViewById(R.id.imageView_xuanchuan);
		mIvXuanchuan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		mGVType = (com.pineapple.mobilecraft.widget.ExpandGridView)findViewById(R.id.gridView_type);
		mGVType.setAdapter(mCategoryAdapter);
		mGVType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//mCategoryAdapter.notifyDataSetChanged();
				mCategoryAdapter.setSelectIndex(position);
				mCategoryAdapter.notifyDataSetChanged();
				mMediator.setType(mCategories[position]);
			}
		});




	}

	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	public void initData()
	{
		mActivityData = null;
		Intent intent =  getIntent();
		IntentBuilder ib = new IntentBuilder(intent);
		mUseType = ib.getUseType();
		if(mUseType == USE_EDIT)
		{
			mActivityData = MyServerManager.getInstance().getActivity(ib.getActivityID());
		}

		mCategoryAdapter = new ActivityCategoryAdapter(this);
		mCategoryAdapter.setData(mCategories);
		mCategoryAdapter.setSelectIndex(0);
		mMediator.setType(mCategories[0]);
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == RESULT_OK)
		{
			switch(requestCode)
			{
			case BUSINESS_REQUEST_CODE:
				mComplexBusiness = (ComplexBusiness) data.getSerializableExtra("business");
				mBtnSelectBusiness.setText(mComplexBusiness.mName);
				break;
				
			case MEMBERS_REQUEST_CODE:
				ArrayList<MyUser> users = (ArrayList<MyUser>) data.getSerializableExtra("members");
				mMediator.setMembers(users);
				break;
				
			case DATE_REQUEST_CODE:
				Date date = (Date)data.getSerializableExtra("date");
				mBtnSelectDate.setText(DateFormat.format(ActivityData.dataPattern, date));
				mMediator.setTime(date);
				break;
			}
		}

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
						Toast.makeText(CreateActivityActivity2.this, "未找到存储卡，无法存储照片！",
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
		
	}
	
	
	/**
	 * ActivityData 更新 View
	 * 
	 */
	private void updateView( )
	{
		if(null != mActivityData)
		{
			mEtxTitle.setText(mActivityData.mTitle);
			mEtxContent.setText(mActivityData.mContent);
			mBeginDate = mActivityData.mBeginDate;
			mMediator.setTime(mBeginDate);
			mBtnSelectDate.setText(DateFormat.format(ActivityData.dataPattern, mBeginDate));
			String users = "";
			for(String name:mActivityData.mInvitingUsers)
			{
				users += name + " ";
			}
			mBtnSelectFriends.setText(users);
		}
		
	}
	
	private boolean checkDataComplete()
	{
		String title;
		if(mEtxTitle.getEditableText().toString().length() != 0)
		{
			title = mEtxTitle.getEditableText().toString();
			mMediator.setTitle(title);
		}
		else
		{
			Toast.makeText(CreateActivityActivity2.this, "请填写聚会标题", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		String content;
		if(mEtxContent.getEditableText().toString().length() != 0)
		{
			content = mEtxContent.getEditableText().toString();
			mMediator.setContent(content);
		}
		else
		{
			Toast.makeText(CreateActivityActivity2.this, "请填写聚内容", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(!mIsTimeSet)
		{
			Toast.makeText(CreateActivityActivity2.this, "请选择聚会时间", Toast.LENGTH_SHORT).show();
			return false;			
		}
		return true;
	}

	private void showDialog() {
		String[] items = new String[] { "选择本地图片", "拍照" };
		new android.app.AlertDialog.Builder(this)
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

	/**
	 * 保存裁剪之后的图片数据
	 *
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			mIvXuanchuan.setImageDrawable(drawable);
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(IMAGE_AVATAR);
				photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						String imgUrl = BmobServerManager.getInstance()
								.uploadImage(new File(IMAGE_AVATAR));
						if (null != imgUrl) {
							mMediator.setImgUrl(imgUrl);
						} else {
							CreateActivityActivity2.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(CreateActivityActivity2.this, "头像修改失败",
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
				Toast.makeText(CreateActivityActivity2.this, "头像上传失败", Toast.LENGTH_SHORT)
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



}
