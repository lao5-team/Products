package com.pineapple.mobilecraft.tumcca.mediator;

import com.pineapple.mobilecraft.tumcca.view.ObservableScrollView;

/**
 * Created by jiankun on 2015/7/15.
 */
public interface IMyScrollViewListener {
    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
}
