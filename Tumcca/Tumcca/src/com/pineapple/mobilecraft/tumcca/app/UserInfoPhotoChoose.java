package com.pineapple.mobilecraft.tumcca.app;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Utility.Utility;
import org.w3c.dom.Text;

import java.io.File;

/**
 * Created by jiankun on 2015/6/24.
 */
public class UserInfoPhotoChoose extends DialogFragment implements View.OnClickListener {



    private TextView mTvFromCamera;
    private TextView mTvFromGallery;



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
        switch (view.getId()) {
            case R.id.fromCamera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ((UserInfoActivity)getActivity()).mUri = Uri.fromFile(new File(Utility.getTumccaImgPath(getActivity()) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, ((UserInfoActivity) getActivity()).mUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                getActivity().startActivityForResult(intent, UserInfoActivity.FROMCAMERA);
                break;
            case R.id.fromGallery:

                break;
        }
    }
}
