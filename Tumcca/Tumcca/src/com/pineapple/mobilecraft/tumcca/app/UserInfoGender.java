package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;

/**
 * Created by yihao on 15/6/17.
 */
public class UserInfoGender extends DialogFragment implements View.OnClickListener {

    private TextView tvSave;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private int isMale =1;
    private RadioGroup radioGroup;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setStyle(android.app.DialogFragment.STYLE_NO_TITLE, R.style.my_dialog_activity_style);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHandler = ((UserInfoActivity)activity).mHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null)
        {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        View root = inflater.inflate(R.layout.dialogfragment_userinfogender, container, false);
        tvSave = (TextView)root.findViewById(R.id.tvSave);
        rbMale = (RadioButton)root.findViewById(R.id.rbMale);
        rbFemale = (RadioButton)root.findViewById(R.id.rbFemale);
        radioGroup = (RadioGroup)root.findViewById(R.id.radioGroup);
//        rbMale.setOnClickListener(new );
        tvSave.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbMale)
                {
                    isMale = 1;
                }
                else
                {
                    isMale = 0;
                }
            }
        });
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

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tvSave:
                Message msg = mHandler.obtainMessage(UserInfoActivity.MSG_CHANGE_SEX);
                Bundle bundle = new Bundle();
                bundle.putInt("gender", isMale);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
                this.dismiss();
                break;
        }
    }
}