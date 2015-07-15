package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Created by yihao on 15/7/14.
 */

public class HalfRoundedBitmapDisplayer implements BitmapDisplayer {

    protected final int cornerRadius;
    protected final int margin;


    public HalfRoundedBitmapDisplayer(int cornerRadiusPixels) {
        this(cornerRadiusPixels, 0);
    }

    public HalfRoundedBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
        this.cornerRadius = cornerRadiusPixels;
        this.margin = marginPixels;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        imageAware.setImageDrawable(new HalfRoundedBitmapDrawable(bitmap, cornerRadius, margin, imageAware.getScaleType()));
    }

    public static class HalfRoundedBitmapDrawable extends RoundedBitmapDisplayer.RoundedDrawable {

        public HalfRoundedBitmapDrawable(Bitmap bitmap, int cornerRadius, int margin, ViewScaleType scaleType) {
            super(bitmap, cornerRadius, margin, scaleType);
        }

        @Override
        public void draw(Canvas canvas) {
            float diameter = cornerRadius* 2;
            Path path = new Path();
            //移动到D点，向上画到A1
            path.moveTo(mRect.left, mRect.bottom);
            path.lineTo(mRect.left, mRect.top + diameter);

            //A1绘制一个圆弧到A2
            RectF rectF = new RectF(mRect.left, mRect.top, mRect.left + diameter, mRect.top + diameter);
            path.arcTo(rectF, 180, 90);

            //A2绘制到B1
            path.lineTo(mRect.right - diameter, mRect.top);

            //B1绘制圆弧到B2
            rectF = new RectF(mRect.right - diameter, mRect.top, mRect.right, mRect.top + diameter);
            path.arcTo(rectF, 270, 90);

            //B2绘制直线到C
            path.lineTo(mRect.right, mRect.bottom);

            //C绘制直线到D
            path.lineTo(mRect.left, mRect.bottom);
            canvas.drawPath(path, paint);
        }
    }

}