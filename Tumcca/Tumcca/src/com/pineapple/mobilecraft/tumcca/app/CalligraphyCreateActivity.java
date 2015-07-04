package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.app.TreasuresEntryFragment;
import com.pineapple.mobilecraft.domain.User;
import com.pineapple.mobilecraft.tumcca.Utility.Utility;
import com.pineapple.mobilecraft.tumcca.data.*;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.mediator.ICalligraphyCreate;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
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
    private PhotoChoose mPhotoChoose;
    private Uri mUri;
    private boolean mIsTestMode = false;

    private Album mAlbum = null;
    private TextView mTvAlbumTitle;
    private ImageView mIvAlbumSample;



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
            Profile profile = UserServer.getInstance().getProfile(UserManager.getInstance().getCurrentToken());
            if(profile == Profile.NULL){
                Toast.makeText(CalligraphyCreateActivity.this, "请先完成用户信息填写", Toast.LENGTH_SHORT).show();
            }

        }
        ImgsActivity.ImagesReceiver = CalligraphyCreateActivity.class;
        mPictureAdapter = new PictureAdapter();
        mPhotoChoose = new PhotoChoose();
        setContentView(R.layout.activity_create_work);
        mGVPictures = (GridView)findViewById(R.id.gridView_picture);
        addPictureDisplayView(mGVPictures);

        EditText etxDesc = (EditText)findViewById(R.id.editText_description);
        addDescribeView(etxDesc);

        Button btnSubmit = (Button)findViewById(R.id.button_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
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
                mPhotoChoose = new PhotoChoose();
                mUri = Uri.fromFile(new File(Utility.getTumccaImgPath(CalligraphyCreateActivity.this) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                mPhotoChoose.setUri(mUri);
                mPhotoChoose.show(getSupportFragmentManager(), "WorksPhotoChoose");

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
        dialog.setTitle("正在发布作品");
        dialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String token = UserManager.getInstance().getCurrentToken();
                int pictureId = -1;
                Works works = new Works();
                for(Picture picture:mListPicture){
                    pictureId = PictureServer.getInstance().uploadPicture(token, new File(picture.localPath));
                    if(PictureServer.INVALID_PICTURE_ID!=pictureId){
                        works.pictures.add(pictureId);
                    }
                }
                works.description = mDescription;
                if(mIsTestMode){
                    works.category = 1;
                    //works.albumId = 0;
                    //works.title = "test";
                }
                works.category = 1;
                if(null!=mAlbum){
                    works.albumId = mAlbum.id;
                }
                else{
                    works.albumId = 0;
                }
                int id = WorksServer.uploadWorks(token, works);

                showResult(id!=WorksServer.INVALID_WORKS_ID);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        });
        t.start();
    }

    @Override
    public void returnBack() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PhotoChoose.FROMGALLERY&&resultCode == RESULT_OK)
        {
            Bundle bundle = data.getExtras();
            mImgFiles = bundle.getStringArrayList("files");
            for(String path:mImgFiles){
                mListPicture.add(new Picture(null, path));
            }
            BaseAdapter adapter = (BaseAdapter)mGVPictures.getAdapter();

            mGVPictures.requestFocus();
            mGVPictures.invalidate();
            adapter.notifyDataSetChanged();

        }
        if(requestCode == PhotoChoose.FROMCAMERA&&resultCode == RESULT_OK){

            mListPicture.add(new Picture(null, mUri.getPath()));
            PictureAdapter adapter = (PictureAdapter)mGVPictures.getAdapter();
            mGVPictures.requestFocus();
            mGVPictures.invalidate();
            adapter.notifyDataSetChanged();
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

    private void showResult(final boolean result){
        List<WorksInfo> worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(), mAlbum.id, 1, 20, 1);
        WorksManager.getInstance().putAlbumWorks(mAlbum.id, worksInfoList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(result){
                    Toast.makeText(CalligraphyCreateActivity.this, "作品发布成功", Toast.LENGTH_SHORT).show();
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
                mTvAlbumTitle.setText(mAlbum.title);
                if(null!=mAlbum.worksInfoList&&mAlbum.worksInfoList.size()>0){
                    PictureInfo pictureInfo = mAlbum.worksInfoList.get(0).picInfo;
                    if(pictureInfo!=null){
                        Picasso.with(CalligraphyCreateActivity.this).load(PictureServer.getInstance().getPictureUrl(pictureInfo.id)).resize(48,48).centerCrop().into(mIvAlbumSample);
                    }
                }
            }
        });
        //getFragmentManager().beginTransaction().add(fragment, "album_select").commit();
        fragment.show(getFragmentManager(), "album_select");

    }

    private class PictureAdapter extends BaseAdapter{

        /**
         * Indicates whether all the items in this adapter are enabled. If the
         * value returned by this method changes over time, there is no guarantee
         * it will take effect.  If true, it means all items are selectable and
         * clickable (there is no separator.)
         *
         * @return True if all items are enabled, false otherwise.
         * @see #isEnabled(int)
         */
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        /**
         * Returns true if the item at the specified position is not a separator.
         * (A separator is a non-selectable, non-clickable item).
         * <p/>
         * The result is unspecified if position is invalid. An {@link ArrayIndexOutOfBoundsException}
         * should be thrown in that case for fast failure.
         *
         * @param position Index of the item
         * @return True if the item is not a separator
         * @see #areAllItemsEnabled()
         */
        @Override
        public boolean isEnabled(int position) {
            return false;
        }



        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return mListPicture.size() + 1;
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return null;
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * Indicates whether the item ids are stable across changes to the
         * underlying data.
         *
         * @return True if the same id always refers to the same object.
         */
        @Override
        public boolean hasStableIds() {
            return false;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
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

        /**
         * Get the type of View that will be created by {@link #getView} for the specified item.
         *
         * @param position The position of the item within the adapter's data set whose view type we
         *                 want.
         * @return An integer representing the type of View. Two views should share the same type if one
         * can be converted to the other in {@link #getView}. Note: Integers must be in the
         * range 0 to {@link #getViewTypeCount} - 1. {@link #IGNORE_ITEM_VIEW_TYPE} can
         * also be returned.
         * @see #IGNORE_ITEM_VIEW_TYPE
         */
        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return 0;
            }
            else{
                return 1;
            }
        }

        /**
         * <p>
         * Returns the number of types of Views that will be created by
         * {@link #getView}. Each type represents a set of views that can be
         * converted in {@link #getView}. If the adapter always returns the same
         * type of View for all items, this method should return 1.
         * </p>
         * <p>
         * This method will only be called when when the adapter is set on the
         * the {@link android.widget.AdapterView}.
         * </p>
         *
         * @return The number of types of Views that will be created by this adapter
         */
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        /**
         * @return true if this adapter doesn't contain any data.  This is used to determine
         * whether the empty view should be displayed.  A typical implementation will return
         * getCount() == 0 but since getCount() includes the headers and footers, specialized
         * adapters might want a different behavior.
         */
        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}