package com.pineapple.mobilecraft.tumcca.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
import com.pineapple.mobilecraft.tumcca.mediator.IMyScrollViewListener;

/**
 * Created by jiankun on 2015/7/15.
 */
public class ObservableScrollView extends ScrollView {

    private IMyScrollViewListener myScrollViewListener = null;

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context) {
        super(context);
    }

    public void setMyScrollViewListener(IMyScrollViewListener scrollViewListener)
    {
        this.myScrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(myScrollViewListener != null)
        {
            myScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
}

