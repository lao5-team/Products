package com.pineapple.mobilecraft.tumcca.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Calligraphy;
import com.pineapple.mobilecraft.tumcca.mediator.ICalligraphyList;

import java.util.List;

/**
 * Created by yihao on 15/6/4.
 */
public class CalligraphyListFragment extends Fragment implements ICalligraphyList{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calligraphy_list, container, false);
    }

    @Override
    public void setCalligraphyList(List<Calligraphy> calligraphyList) {

    }

    @Override
    public void addCalligraphyListView() {

    }

    @Override
    public void openCalligraphy(Calligraphy calligraphy) {

    }
}