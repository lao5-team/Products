package com.pineapple.mobilecraft.tumcca.app;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.service.MessageService;
import com.pineapple.mobilecraft.tumcca.Utility.Utility;
import com.pineapple.mobilecraft.tumcca.data.*;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.mediator.ICalligraphyCreate;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import com.pineapple.mobilecraft.util.logic.ImgsActivity;
import com.squareup.picasso.Picasso;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/3/12.
 */
public class CalligraphyCreateActivity extends FragmentActivity implements ICalligraphyCreate {
    public static final int CROP_REQUEST_CODE = 2;

    private ArrayList<String> mImgFiles = new ArrayList<String>();

    private String mDescription;
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


    public static void startActivity(Activity activity){
        Intent intent = new Intent(activity, CalligraphyCreateActivity.class);
        //fragment.startActivityForResult(intent, TreasuresEntryFragment.REQUEST_CREATE_TREASURE);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mIsTestMode)
        {
            UserManager.getInstance().login("999", "999");
            UserServer.getInstance().uploadProfile(UserManager.getInstance().getCurrentToken(), Profile.createTestProfile());


        }
        if(!UserManager.getInstance().isLogin()){
            Toast.makeText(CalligraphyCreateActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
        }
        Profile profile = UserServer.getInstance().getCurrentUserProfile(UserManager.getInstance().getCurrentToken());
        if(profile == Profile.NULL){
            Toast.makeText(CalligraphyCreateActivity.this, "请先完成用户信息填写", Toast.LENGTH_SHORT).show();
            finish();
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

        mIvBack = (ImageView)findViewById(R.id.imageView_back);
        mIvBack.setClickable(true);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listenConnection();

        bindService(new Intent(this, TumccaService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((TumccaService.LocalService)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

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
    }


    @Override
    public void addPictureChooseView(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAvatarChoose = new AvatarChoose();
                mUri = Uri.fromFile(new File(Utility.getTumccaImgPath(CalligraphyCreateActivity.this) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                mAvatarChoose.setUri(mUri);
                mAvatarChoose.show(getSupportFragmentManager(), "WorksPhotoChoose");

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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Works works = new Works();
                works.title = mDescription;
                works.category = 1;
                works.albumId = mAlbum.id;
                mService.uploadWorks(mListPicture, works);
//                int id = WorksServer.uploadWorks(token, works);
//                List<WorksInfo> worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(), mAlbum.id, 1, 20, 400);
//                WorksManager.getInstance().putAlbumWorks(mAlbum.id, worksInfoList);
                //showResult(id!=WorksServer.INVALID_WORKS_ID);
            }
        });
        t.start();
        finish();
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
//            Bundle bundle = data.getExtras();
//            mImgFiles = bundle.getStringArrayList("files");
//            for(String path:mImgFiles){
//                mListPicture.add(new Picture(null, path));
//            }
//            BaseAdapter adapter = (BaseAdapter)mGVPictures.getAdapter();
//
//            mGVPictures.requestFocus();
//            mGVPictures.invalidate();
//            adapter.notifyDataSetChanged();
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null,
                    null, null, null);
            cursor.moveToFirst();
            String imgNo = cursor.getString(0); // 图片编号
            String imgPath = cursor.getString(1); // 图片文件路径
            String imgSize = cursor.getString(2); // 图片大小
            String imgName = cursor.getString(3); // 图片文件名
            cursor.close();
            addPicture(new Picture(null, imgPath));
//            mListPicture.add(new Picture(null, imgPath));
//            PictureAdapter adapter = (PictureAdapter)mGVPictures.getAdapter();
//            mGVPictures.requestFocus();
//            mGVPictures.invalidate();
//            adapter.notifyDataSetChanged();

        }
        if(requestCode == AvatarChoose.FROMCAMERA&&resultCode == RESULT_OK){

            addPicture(new Picture(null, mUri.getPath()));
//            mListPicture.add();
//            PictureAdapter adapter = (PictureAdapter)mGVPictures.getAdapter();
//            mGVPictures.requestFocus();
//            mGVPictures.invalidate();
//            adapter.notifyDataSetChanged();
            //startPhotoZoom(mUri);
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
    }

    private void addPicture(Picture picture){
        if(mListPicture.size()==0){
            mListPicture.add(picture);
        }else {
            mListPicture.set(0, picture);
        }
        PictureAdapter adapter = (PictureAdapter)mGVPictures.getAdapter();
        mGVPictures.requestFocus();
        mGVPictures.invalidate();
        adapter.notifyDataSetChanged();
    }

    private void showResult(final boolean result){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(result){
                    Toast.makeText(CalligraphyCreateActivity.this, "作品发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{

                    Toast.makeText(CalligraphyCreateActivity.this, "作品发布失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        if(-1!=album.sampleImageId){
                //Picasso.with(CalligraphyCreateActivity.this).load(PictureServer.getInstance().getPictureUrl(pictureInfo.id)).resize(48,48).centerCrop().into(mIvAlbumSample);
                DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(5)).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(PictureServer.getInstance().getPictureUrl(album.sampleImageId, 48, 1), mIvAlbumSample, imageOptions);
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
        public View getView(int position, View convertView, ViewGroup parent) {
            /*
            获取GridView宽度，item宽度为gridview宽度除以列数。高度和宽度相等
            * */
            GridView gridView = mGVPictures;
            int item_width = gridView.getWidth() / 3;

            LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_picture, null);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    item_width, item_width);
            layout.setLayoutParams(param);
            if (position == 0) {
                ImageView imageView = (ImageView) layout.findViewById(R.id.imageView_picture);
                imageView.setImageResource(R.drawable.add_photo);
                addPictureChooseView(imageView);

            } else {
                ImageView imageView = (ImageView) layout.findViewById(R.id.imageView_picture);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mListPicture.get(position - 1).localPath, options);

                int inSampleSize = options.outHeight*options.outHeight/(item_width*item_width);
                options = new  BitmapFactory.Options();
                options.inSampleSize = inSampleSize;
                //imageView.setImageBitmap(BitmapFactory.decodeFile(mListPicture.get(position - 1).localPath, options));

                //imageView.setImageDrawable(new BitmapDrawable());
                Picasso.with(CalligraphyCreateActivity.this).load(new File(mListPicture.get(position - 1).localPath)).resize(item_width, item_width).centerCrop().into(imageView);
            }
            return layout;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
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


}