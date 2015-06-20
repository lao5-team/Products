package com.pineapple.mobilecraft.tumcca.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by yihao on 15/6/18.
 */
public class MyVerticalScrollView extends ScrollView implements MyScrollView{

    private boolean mIsMagnifierMode;
    public MyVerticalScrollView(Context context){
        super(context);
    }

    public MyVerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //super.onInterceptTouchEvent(ev);
        if(mIsMagnifierMode){
            return  false;
        }
        else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public void setMagnifierMode(boolean mode) {
        mIsMagnifierMode = mode;
    }
}
