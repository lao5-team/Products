package com.pineapple.mobilecraft.tumcca.app;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;

/**
 * Created by liujiankun007 on 2015/6/16.
 */
public class UserInfoForte extends DialogFragment implements View.OnClickListener {

    private TextView tvTitle;
    private EditText etContent;
    private TextView tvSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null)
        {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        View root = inflater.inflate(R.layout.dialogfragment_userinfo, container, false);
        tvTitle = (TextView)root.findViewById(R.id.tvTitle);
        etContent = (EditText)root.findViewById(R.id.etContent);
        tvSave = (TextView)root.findViewById(R.id.tvSave);
        tvTitle.setText("更改您的专长");
        return root;
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
        switch (view.getId())
        {
            case R.id.tvSave:

                break;
        }
    }
}
