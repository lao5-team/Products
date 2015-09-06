package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.utility.Utility;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 7/22/15.
 */
public class PictureEditActivity extends Activity implements GestureDetector.OnGestureListener {


    DisplayImageOptions mImageOptionsWorks;

    private List<Picture> mPictureList = new ArrayList<Picture>();

    ViewFlipper mVF;
    TextView mTvPictureIndex;
    ImageView mIvDelete;
    RelativeLayout mLayoutRotate;
    Button mBtnOK;
    private GestureDetector mDetector;

    public static void startActivity(Activity activity, int requestCode, List<Picture> pictureList, int index){
        Intent intent = new Intent(activity, PictureEditActivity.class);
        JSONArray jsonArray = new JSONArray();
        for(Picture picture:pictureList){
            jsonArray.put(Picture.toJSON(picture));
        }
        intent.putExtra("pictureList", jsonArray.toString());
        intent.putExtra("index", index);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        mImageOptionsWorks = new DisplayImageOptions.Builder()
                .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        final ActionBar actionBar = getActionBar();
        if(null!=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM);
            View customActionBarView = getLayoutInflater().inflate(R.layout.actionbar_picture_edit, null);
            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.END;
            actionBar.setCustomView(customActionBarView, lp);
        }
        setContentView(R.layout.activity_picture_edit);
        mVF = (ViewFlipper)findViewById(R.id.viewFlipper);
        mTvPictureIndex = (TextView)findViewById(R.id.textView_pic_index);
        mIvDelete = (ImageView)findViewById(R.id.imageView_delete);
        mIvDelete.setClickable(true);
        mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePicture();
            }
        });
        mLayoutRotate = (RelativeLayout)findViewById(R.id.layout_rotate);
        mLayoutRotate.setClickable(true);
        mLayoutRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotatePicture();
            }
        });
        mBtnOK = (Button)findViewById(R.id.button_OK);
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmEdit();
            }
        });
        loadData(getIntent());
        addViewflipper(mVF, mPictureList);
        mTvPictureIndex.setText((mVF.getDisplayedChild() + 1) + "/" + mPictureList.size());

        mDetector = new GestureDetector(this);
    }

    private void deletePicture() {
        final int index = mVF.getDisplayedChild();
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage(getString(R.string.remove_picture_confirm))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPictureList.remove(index);
                        mVF.removeViewAt(index);
                        mTvPictureIndex.setText((mVF.getDisplayedChild()+1) + "/" + mPictureList.size());
                        confirmEdit();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void confirmEdit() {
        Intent intent = new Intent();
        JSONArray jsonArray = new JSONArray();
        for(Picture picture:mPictureList){
            jsonArray.put(Picture.toJSON(picture));
        }
        intent.putExtra("pictureList", jsonArray.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void rotatePicture() {
        int index = mVF.getDisplayedChild();
        Picture picture = mPictureList.get(index);
        picture.localPath = Utility.rotateImage(picture.localPath, 90);
        String path = "file://" + Uri.fromFile(new File(picture.localPath)).getPath();
        ImageLoader.getInstance().displayImage(path, (ImageView) mVF.getCurrentView().findViewById(R.id.imageView), mImageOptionsWorks);
    }

    private void loadData(Intent intent){
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(intent.getStringExtra("pictureList"));
            for(int i=0; i<jsonArray.length(); i++){
                mPictureList.add(Picture.fromJSON(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addViewflipper(ViewFlipper viewFlipper, List<Picture> pictureList){
        for(Picture picture:pictureList){
            View view = getLayoutInflater().inflate(R.layout.fliper_pic, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //ivPic.setLayoutParams(params);
            String path = Uri.fromFile(new File(picture.localPath)).toString();
            ImageLoader.getInstance().displayImage(path, imageView, mImageOptionsWorks);
            viewFlipper.addView(view);
        }

        viewFlipper.setDisplayedChild(getIntent().getIntExtra("index", 0));

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


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > 120) {
            this.mVF.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right));
            this.mVF.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left));
            if(this.mVF.getDisplayedChild()<(mPictureList.size()-1)){
                mTvPictureIndex.setText((mVF.getDisplayedChild()+2) + "/" + mPictureList.size());
                this.mVF.showNext();
            }
            return true;
        } else if (e1.getX() - e2.getX() < -120) {
            this.mVF.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left));
            this.mVF.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_to_right));
            if(this.mVF.getDisplayedChild()>0){
                mTvPictureIndex.setText((mVF.getDisplayedChild()) + "/" + mPictureList.size());
                this.mVF.showPrevious();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mDetector.onTouchEvent(event);
    }
}