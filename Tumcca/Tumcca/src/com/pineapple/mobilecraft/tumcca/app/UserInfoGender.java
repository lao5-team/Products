package com.pineapple.mobilecraft.tumcca.app;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import com.pineapple.mobilecraft.R;

/**
 * Created by yihao on 15/6/17.
 */
public class UserInfoGender extends DialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setStyle(android.app.DialogFragment.STYLE_NO_TITLE, R.style.my_dialog_activity_style);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null)
        {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        View root = inflater.inflate(R.layout.dialogfragment_userinfogender, container, false);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {

            getDialog().setCanceledOnTouchOutside(true);
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

}