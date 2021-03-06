package com.pineapple.mobilecraft.tumcca.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.pineapple.mobilecraft.R;

/**
 * Created by liujiankun007 on 2015/6/16.
 */
public class UserInfoForte extends DialogFragment implements View.OnClickListener {

    private TextView tvTitle;
    private EditText etContent;
    private TextView tvSave;
    private Handler mHandler;
    public void setForte(String forte){
        Bundle bundle = new Bundle();
        bundle.putString("forte", forte);
        setArguments(bundle);
    }

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
        etContent.setText(getArguments().getString("forte"));
        tvSave = (TextView)root.findViewById(R.id.tvSave);
        tvTitle.setText("更改您的专长");
        tvSave.setOnClickListener(this);
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHandler = ((UserInfoActivity)activity).mHandler;
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
                if(TextUtils.isEmpty(etContent.getText()))
                {
                    Toast.makeText(getActivity(), "专长不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    String editPseudonym = etContent.getText().toString();
                    Message msg = mHandler.obtainMessage(UserInfoActivity.MSG_CHANGE_FORTE);
                    Bundle bundle = new Bundle();
                    bundle.putString("forte", editPseudonym);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    this.dismiss();
                }
                break;
        }
    }
}
