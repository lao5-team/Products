package com.pineapple.mobilecraft.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by yihao on 9/2/15.
 */
public class RotateImageView extends ImageView {

    public RotateImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
//        if(getRotation()>0){
////            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
////            params.height = getMeasuredWidth();
////            params.width = getMeasuredHeight();
////            setLayoutParams(params);
//            layout(0,0,400,640);
//            super.onMeasure(View.MeasureSpec.makeMeasureSpec(400, MeasureSpec.EXACTLY),
//                    View.MeasureSpec.makeMeasureSpec(640, MeasureSpec.EXACTLY));
//        }
//        else{
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        }
//
//    }

//    @Override
//    public ViewGroup.LayoutParams getLayoutParams(){
//        if(getRotation()>0){
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) super.getLayoutParams();
//            params.height = 640;
//            params.width = 400;
//            return params;
//        }
//        else{
//            return super.getLayoutParams();
//        }
//
//    }

    @Override
    public void setRotation(float rotation){
        super.setRotation(rotation);
        //getParent().
        if(rotation>0){
            if (getRotation() > 0) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                int height = params.height;
                params.height = 720;
                params.width = params.height*getDrawable().getBounds().height()/getDrawable().getBounds().width();

                measure(View.MeasureSpec.makeMeasureSpec(params.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(params.height, View.MeasureSpec.EXACTLY));
                setLayoutParams(params);
                setMeasuredDimension(params.width, params.height);

                //vh.mIvPic.measure();
            }
//            setLayoutParams(params);
//
//            measure(View.MeasureSpec.makeMeasureSpec(400, MeasureSpec.EXACTLY),
//                    View.MeasureSpec.makeMeasureSpec(640, MeasureSpec.EXACTLY));
//
//            requestLayout();
        }
    }
}
