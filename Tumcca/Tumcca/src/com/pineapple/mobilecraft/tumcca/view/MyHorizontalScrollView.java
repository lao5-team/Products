package com.pineapple.mobilecraft.tumcca.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by yihao on 15/6/19.
 */
public class MyHorizontalScrollView extends HorizontalScrollView implements MyScrollView{

    private boolean mIsMagnifierMode;

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
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
