package com.pineapple.mobilecraft.tumcca.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.TextView;
import com.photoselector.ui.PhotoSelectorActivity;
import com.pineapple.mobilecraft.R;

/**
 * Created by jiankun on 2015/6/24.
 */
public class PhotoChoose extends DialogFragment implements View.OnClickListener {

    public static final int FROMCAMERA = 4;
    public static final int FROMGALLERY = 5;

    private TextView mTvFromCamera;
    private TextView mTvFromGallery;
    private TextView mTvTitle;
    private Uri mUri;

    public PhotoChoose(){
        //mUri = Uri.fromFile(new File(Utility.getTumccaImgPath(getActivity()) + "/" + "Temp.jpg"));
    }

    public void setUri(Uri uri){
        mUri = uri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        View root = inflater.inflate(R.layout.dialogfragment_photochoose, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        mTvFromCamera = (TextView) root.findViewById(R.id.fromCamera);
        mTvFromGallery = (TextView) root.findViewById(R.id.fromGallery);
        mTvTitle = (TextView)root.findViewById(R.id.textView_title);
        mTvTitle.setText("选择图片来源");
        mTvFromCamera.setOnClickListener(this);
        mTvFromGallery.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {

            int fullWidth = getDialog().getWindow().getAttributes().width;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                fullWidth = size.x;
            } else {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                fullWidth = display.getWidth();
            }

            final int padding = getResources().getDimensionPixelOffset(R.dimen.dialogfragment_marginleft);

            int w = fullWidth;
            int h = getDialog().getWindow().getAttributes().height;

            getDialog().getWindow().setLayout(w, h);

        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.fromCamera:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //((UserInfoActivity)getActivity()).mUri = Uri.fromFile(new File(Utility.getTumccaImgPath(getActivity()) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                getActivity().startActivityForResult(intent, FROMCAMERA);
                break;
            case R.id.fromGallery:
                intent = new Intent(getActivity(), PhotoSelectorActivity.class);
                intent.putExtra(PhotoSelectorActivity.KEY_MAX, 5);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().startActivityForResult(intent, FROMGALLERY);
                break;
        }

        dismiss();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == FROMGALLERY) {// selected image
            if (data != null && data.getStringExtra("photos") != null) {
                    Intent intent = new Intent(getActivity(), FacebookLikedWorksCreateActivity.class);
                    intent.putExtra("photos", data.getStringExtra("photos"));
                    startActivity(intent);
            }
        }

    }
}
