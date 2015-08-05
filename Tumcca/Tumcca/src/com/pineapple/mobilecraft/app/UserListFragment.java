package com.pineapple.mobilecraft.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pineapple.mobilecraft.R;

/**
 * Created by yihao on 8/3/15.
 */
public class UserListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_userlist, container, false);
    }
}