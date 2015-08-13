package com.pineapple.mobilecraft.tumcca.activity;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.fragment.AlbumSelectFragment;
import com.pineapple.mobilecraft.tumcca.utility.PrefsCache;
import com.pineapple.mobilecraft.tumcca.utility.Utility;
import com.pineapple.mobilecraft.tumcca.data.*;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.mediator.ICalligraphyCreate;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import com.pineapple.mobilecraft.util.logic.ImgsActivity;
import com.pineapple.mobilecraft.util.logic.Util;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/3/12.
 */
public class CalligraphyCreateActivity extends FragmentActivity implements ICalligraphyCreate {
    public static final int CROP_REQUEST_CODE = 2;
    public static final int MAX_IMAGE_COUNT =5;
    public static int REQ_PIC_EDIT = 6;
    private ArrayList<String> mImgFiles = new ArrayList<String>();

    private String mDescription = "";
    private PictureAdapter mPictureAdapter;
    private List<Picture> mListPicture = new ArrayList<Picture>();
    private GridView mGVPictures;
    private RelativeLayout mLayoutAlbum;
    private AvatarChoose mAvatarChoose;
    private Uri mUri;
    private boolean mIsTestMode = false;

    private Album mAlbum = Album.DEFAULT_ALBUM;
    private TextView mTvAlbumTitle;
    private ImageView mIvAlbumSample;
    private ImageView mIvBack;
    private Button mBtnSubmit;
    private TumccaService mService;
    DisplayImageOptions mImageOptionsWorks;
    PrefsCache mPrefsCache;
    ServiceConnection mServiceConnection;

    public static void startActivity(Activity activity){
        Intent intent = new Intent(activity, CalligraphyCreateActivity.class);
        //fragment.startActivityForResult(intent, TreasuresEntryFragment.REQUEST_CREATE_TREASURE);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        mImageOptionsWorks = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(Util.dip2px(TumccaApplication.applicationContext, 5))).cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        final ActionBar actionBar = getActionBar();
        if(null!=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM);
            View customActionBarView = getLayoutInflater().inflate(R.layout.actionbar_calligrahy_create, null);
            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.END;
            actionBar.setCustomView(customActionBarView, lp);
        }


        if(mIsTestMode)
        {
            UserManager.getInstance().login("999", "999");
            UserServer.getInstance().uploadProfile(UserManager.getInstance().getCurrentToken(null), Profile.createTestProfile());
        }

        ImgsActivity.ImagesReceiver = CalligraphyCreateActivity.class;
        mPictureAdapter = new PictureAdapter();
        mAvatarChoose = new AvatarChoose();
        setContentView(R.layout.activity_create_work);
        mGVPictures = (GridView)findViewById(R.id.gridView_picture);
        addPictureDisplayView(mGVPictures);

        EditText etxDesc = (EditText)findViewById(R.id.editText_description);
        addDescribeView(etxDesc);

        mBtnSubmit = (Button)findViewById(R.id.button_submit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        mLayoutAlbum = (RelativeLayout)findViewById(R.id.layout_album);
        mLayoutAlbum.setClickable(true);
        mLayoutAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbumSelect();
            }
        });
        mTvAlbumTitle = (TextView)findViewById(R.id.textView_album_title);
        mIvAlbumSample = (ImageView)findViewById(R.id.imageView_album);

        Album album = WorksManager.getInstance().getLatestAlbum();
        if(album!=null){
            mAlbum = album;
            displaySelectedAlbum(album);
        }


        listenConnection();

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((TumccaService.LocalService)service).getService();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, TumccaService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

//        if(!UserManager.getInstance().isLogin()){
//            Toast.makeText(CalligraphyCreateActivity.this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
//            finish();
//        }

        UserManager.getInstance().getCurrentToken(new UserManager.PostLoginTask() {
            @Override
            public void onLogin(String token) {
                Profile profile = UserServer.getInstance().getCurrentUserProfile(token);
                if(profile == Profile.NULL){
                    Toast.makeText(CalligraphyCreateActivity.this, getString(R.string.please_complete_profile), Toast.LENGTH_SHORT).show();
                    finish();
                }
                //TODO load albums
            }

            @Override
            public void onCancel() {
                finish();
            }

        });


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    private void listenConnection() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        ConnectivityManager mConnectivityManager = (ConnectivityManager)CalligraphyCreateActivity.this
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                        if((mNetworkInfo == null||!mNetworkInfo.isAvailable())){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLayoutAlbum.setClickable(false);
                                    mBtnSubmit.setEnabled(false);
                                }
                            });
                        }
                        else if(null!=mNetworkInfo&&mNetworkInfo.isAvailable()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLayoutAlbum.setClickable(true);
                                    mBtnSubmit.setEnabled(true);
                                }
                            });
                        }
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        });
        t.start();

    }


    @Override
    public void addDescribeView(View view) {
        EditText editText = (EditText)view;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDescription = s.toString();
            }
        });
        editText.requestFocus();
    }


    @Override
    public void addPictureChooseView(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListPicture.size()==5){
                    Toast.makeText(CalligraphyCreateActivity.this, getString(R.string.restrict_upload_pictures, MAX_IMAGE_COUNT), Toast.LENGTH_SHORT).show();

                }
                else{
                    mAvatarChoose = new AvatarChoose();
                    mUri = Uri.fromFile(new File(Utility.getTumccaImgPath(CalligraphyCreateActivity.this) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                    mAvatarChoose.setUri(mUri);
                    mAvatarChoose.show(getSupportFragmentManager(), "WorksPhotoChoose");
                }


            }
        });
    }

    @Override
    public void addPictureDisplayView(View view) {
        GridView gridView = (GridView)view;
        gridView.setAdapter(mPictureAdapter);
    }

    @Override
    public void setPictures(List<Picture> pictureList) {

    }

    @Override
    public void submit() {
        final ProgressDialog dialog = new ProgressDialog(this);
        if (TextUtils.isEmpty(mDescription)) {
            Toast.makeText(CalligraphyCreateActivity.this, getString(R.string.please_enter_description), Toast.LENGTH_SHORT).show();
        }else if(mListPicture.size()==0){
            Toast.makeText(CalligraphyCreateActivity.this, getString(R.string.please_select_picture), Toast.LENGTH_SHORT).show();
        }
        else {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Works works = new Works();
                    works.title = mDescription;
                    works.category = 1;
                    works.albumId = mAlbum.id;
                    mService.uploadWorks(mListPicture, works);
                }
            });
            t.start();
            finish();
        }

    }

    @Override
    public void returnBack() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AvatarChoose.FROMGALLERY&&resultCode == RESULT_OK)
        {

            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null,
                    null, null, null);
            cursor.moveToFirst();
            String imgNo = cursor.getString(0); // 图片编号
            String imgPath = cursor.getString(1); // 图片文件路径
            String imgSize = cursor.getString(2); // 图片大小
            String imgName = cursor.getString(3); // 图片文件名
            cursor.close();
            String outPath = Utility.processImage(imgPath, 1080, 1920, true);
            addPicture(new Picture(null, outPath));

        }
        if(requestCode == AvatarChoose.FROMCAMERA&&resultCode == RESULT_OK){
            String outPath = Utility.processImage(mUri.getPath(), 1080, 1920, true);
            addPicture(new Picture(null, outPath));

        }
        if(requestCode == CROP_REQUEST_CODE){
            Bundle extras = data.getExtras();
            if(extras != null ) {
                Bitmap photo = extras.getParcelable("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                //将流写入文件或者直接使用
                FileOutputStream fos = null;
                try {
                    String localPath = Utility.getTumccaImgPath(CalligraphyCreateActivity.this) + "/" + mListPicture.size() + ".jpg";
                    fos = new FileOutputStream(localPath);
                    try {
                        fos.write(stream.toByteArray());
                        fos.close();
                        mListPicture.add(new Picture(null, localPath));
                        PictureAdapter adapter = (PictureAdapter)mGVPictures.getAdapter();

                        mGVPictures.requestFocus();
                        mGVPictures.invalidate();
                        adapter.notifyDataSetChanged();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }
        if(requestCode == REQ_PIC_EDIT&&resultCode == RESULT_OK){

            try {
                JSONArray jsonArray = new JSONArray(data.getStringExtra("pictureList"));
                mListPicture.clear();
                for(int i=0; i<jsonArray.length(); i++){
                    mListPicture.add(Picture.fromJSON(jsonArray.getJSONObject(i)));
                }
                mPictureAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void addPicture(Picture picture){
            if(!UserManager.getInstance().isUserEditPicture()){
                //Toast.makeText(this, "点击图片可以进行编辑", Toast.LENGTH_SHORT).show();
                showTips();
                UserManager.getInstance().recordUserPictureEdit();
            }
        if(mListPicture.size()<MAX_IMAGE_COUNT){
            mListPicture.add(picture);
            PictureAdapter adapter = (PictureAdapter)mGVPictures.getAdapter();
            mGVPictures.requestFocus();
            mGVPictures.invalidate();
            adapter.notifyDataSetChanged();
        }

    }



    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", true);
        File capturePath = new File(Utility.getTumccaImgPath(this) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        Uri cropUri = Uri.fromFile(capturePath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private void showAlbumSelect(){
        AlbumSelectFragment fragment = new AlbumSelectFragment();
        fragment.setAlbumSelectListener(new AlbumSelectFragment.OnAlbumSelectListener() {
            @Override
            public void onAlbumSelect(Album album) {
                mAlbum = album;
                WorksManager.getInstance().setLatestAlbum(album);
                displaySelectedAlbum(album);
            }
        });
        fragment.show(getFragmentManager(), "album_select");

    }

    private void displaySelectedAlbum(Album album){
        mTvAlbumTitle.setText(album.title);
        List<Integer>cover = album.cover;
        if(null!=cover&&cover.size()>0){
                //Picasso.with(CalligraphyCreateActivity.this).load(PictureServer.getInstance().getPictureUrl(pictureInfo.id)).resize(48,48).centerCrop().into(mIvAlbumSample);
                DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(cover.get(0), 48, 1), mIvAlbumSample, imageOptions);
        }
    }

    private class PictureAdapter extends BaseAdapter{

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            return mListPicture.size() + 1;
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
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            /*
            获取GridView宽度，item宽度为gridview宽度除以列数。高度和宽度相等
            * */
            GridView gridView = mGVPictures;
            int item_width = gridView.getWidth() / 3;


            if (position == getCount() - 1) {
                LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_add_picture, null);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                        item_width, item_width);
                layout.setLayoutParams(param);
                ImageView imageView = (ImageView) layout.findViewById(R.id.imageView_picture);
                addPictureChooseView(imageView);
                return layout;
            } else {
                LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_picture, null);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                        item_width, item_width);
                layout.setLayoutParams(param);
                ImageView imageView = (ImageView) layout.findViewById(R.id.imageView_picture);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mListPicture.get(position).localPath, options);

                int inSampleSize = options.outHeight * options.outHeight / (item_width * item_width);
                options = new BitmapFactory.Options();
                options.inSampleSize = inSampleSize;
                String path = Uri.fromFile(new File(mListPicture.get(position).localPath)).toString();
                ImageLoader.getInstance().displayImage(path, imageView, mImageOptionsWorks);
                imageView.setClickable(true);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PictureEditActivity.startActivity(CalligraphyCreateActivity.this, REQ_PIC_EDIT, mListPicture, position);
                    }
                });
                return layout;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position == getCount()-1){
                return 0;
            }
            else{
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTips(){
        View view = getLayoutInflater().inflate(R.layout.popup_tips, null);
        PopupWindow window = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        window.setFocusable(true);
        window.setTouchable(true);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //window.setContentView();
        //window.showAtLocation(mBtnSubmit, Gravity.NO_GRAVITY, 0, 0);
        window.showAsDropDown(mGVPictures, 0, -mGVPictures.getHeight() + Util.dip2px(this, 105));
    }


}