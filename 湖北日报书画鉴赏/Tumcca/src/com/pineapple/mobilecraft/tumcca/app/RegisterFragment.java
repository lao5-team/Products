package com.pineapple.mobilecraft.tumcca.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.mediator.IRegister;

/**
 * Created by yihao on 15/6/11.
 */
public class RegisterFragment extends DialogFragment implements IRegister{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register, null);

        addUsernameView();
        addPasswordView();
        return view;
    }

    @Override
    public void addUsernameView() {

    }

    @Override
    public void addPasswordView() {

    }

    @Override
    public void confirm() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public  void onResume()
    {
        super.onResume();
//        // Auto size the dialog based on it's contents
//        Dialog.SetLayout(LinearLayout.LayoutParams.WrapContent,
//                LinearLayout.LayoutParams.WrapContent);
//
//        // Make sure there is no background behind our view
//        Dialog.Window.SetBackgroundDrawable(new ColorDrawable(Color.Transparent));
//        setStyle(DialogFragmentStyle.NoFrame, Android.Resource.Style.Theme);
//        base.OnResume();
        setStyle(DialogFragment.STYLE_NO_FRAME|DialogFragment.STYLE_NO_TITLE, 0);
        //setStyle(DialogFragment., 0);
    }
}