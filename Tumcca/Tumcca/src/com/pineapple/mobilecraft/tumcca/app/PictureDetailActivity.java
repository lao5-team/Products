package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import com.pineapple.mobilecraft.R;

/**
 * Created by yihao on 15/6/16.
 */
public class PictureDetailActivity extends Activity {

    private final static int EDGE = 150;
    private Rect mSrcRect = new Rect();
    private Point mPoint = new Point();
    private Bitmap resBitmap;
    private PopupWindow popupWindow;
    Magnifier mMagnifier;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_detail);

        final View view = findViewById(R.id.layout_picture);
        resBitmap = ((BitmapDrawable)view.getBackground()).getBitmap();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(MotionEvent.ACTION_DOWN == action || MotionEvent.ACTION_MOVE == action)
                {
                    int x = (int)event.getX();
                    int y = (int)event.getY();
                    System.out.println("X: " + x + ", Y: " + y);
//            mSrcRect.set((int)(x*760/480-EDGE), (int)(y*1030/840-EDGE),
//                    (int)(x*760/480+EDGE), (int)(y*1030/840+EDGE));
                    mSrcRect.set(x-EDGE/2, y-EDGE/2, x+EDGE/2, y+EDGE/2);
                    mPoint.set((int)x, (int)y);
                    //对于越界处理 http://www.ligotop.com
                    if(mSrcRect.left < 0)
                    {
                        mSrcRect.offset(-mSrcRect.left, 0);
                    }
                    else
                    if(mSrcRect.right > resBitmap.getWidth())
                    {
                        mSrcRect.offset(resBitmap.getWidth()-mSrcRect.right, 0);
                    }
                    if(mSrcRect.top < 0)
                    {
                        mSrcRect.offset(0, -mSrcRect.top);
                    }
                    else
                    if(mSrcRect.bottom > resBitmap.getHeight())
                    {
                        mSrcRect.offset(0, resBitmap.getHeight()-mSrcRect.bottom);
                    }
                    popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
                    mMagnifier.invalidate();

//            if((x<0) || (y<0))
//            {
//                popupWindow.dismiss();
//                layout.invalidate();
//                return
//                        true;
//            }
//            layout.removeCallbacks(showPopup);
//            layout.postDelayed(showPopup, 200);
//            magnifier.invalidate();
                }
//        else
//        if(MotionEvent.ACTION_UP == action)
//        {
//            layout.removeCallbacks(showPopup);
//            popup.dismiss();
//        }
//        layout.invalidate();
                return true;
            }
        });

        mMagnifier = new Magnifier(this);

        popupWindow = new PopupWindow(mMagnifier, 300, 300);
//        popupWindow.showAsDropDown(mMagnifier, 50, 50);
//        popupWindow.update();
    }

    public class Magnifier extends View
    {
        private Paint paint;
        private Rect rect;
        public Magnifier(Context context){
            super(context);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(0xff008000);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            rect = new Rect(0, 0, EDGE*2, EDGE*2);
        }
        protected void onDraw(Canvas canvas)
        {
            canvas.save();

            Path path = new Path();
            path.reset();
            canvas.clipPath(path, Region.Op.REPLACE);
            path.addCircle(EDGE, EDGE, EDGE, Path.Direction.CW);
            canvas.clipPath(path, Region.Op.REPLACE);
            paint.setAlpha(255);
            canvas.drawBitmap(resBitmap, mSrcRect, rect, paint);
            //canvas.drawCircle(rect.centerX(), rect.centerY(), EDGE, paint);
            //canvas.restore();
        }
    }


    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO Auto-generated method stub
        int action = event.getAction();
        if(MotionEvent.ACTION_DOWN == action || MotionEvent.ACTION_MOVE == action)
        {
            float x = (float)event.getX();
            float y = (float)event.getY();
            System.out.println("X: " + x + ", Y: " + y);
//            mSrcRect.set((int)(x*760/480-EDGE), (int)(y*1030/840-EDGE),
//                    (int)(x*760/480+EDGE), (int)(y*1030/840+EDGE));
            mPoint.set((int)x, (int)y);
            //对于越界处理 http://www.ligotop.com
            if(mSrcRect.left < 0)
            {
                mSrcRect.offset(-mSrcRect.left, 0);
            }
            else
            if(mSrcRect.right > resBitmap.getWidth())
            {
                mSrcRect.offset(resBitmap.getWidth()-mSrcRect.right, 0);
            }
            if(mSrcRect.top < 0)
            {
                mSrcRect.offset(0, -mSrcRect.top);
            }
            else
            if(mSrcRect.bottom > resBitmap.getHeight())
            {
                mSrcRect.offset(0, resBitmap.getHeight()-mSrcRect.bottom);
            }
//            if((x<0) || (y<0))
//            {
//                popupWindow.dismiss();
//                layout.invalidate();
//                return
//                        true;
//            }
//            layout.removeCallbacks(showPopup);
//            layout.postDelayed(showPopup, 200);
//            magnifier.invalidate();
        }
//        else
//        if(MotionEvent.ACTION_UP == action)
//        {
//            layout.removeCallbacks(showPopup);
//            popup.dismiss();
//        }
//        layout.invalidate();
        return true;
    }
}