package com.pineapple.mobilecraft.tumcca.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.view.MyHorizontalScrollView;
import com.pineapple.mobilecraft.tumcca.view.MyScrollView;
import com.pineapple.mobilecraft.tumcca.view.MyVerticalScrollView;
import com.pineapple.mobilecraft.tumcca.view.ZoomImageView;
import com.sina.weibo.sdk.api.TextObject;

/**
 * Created by yihao on 15/6/16.
 */
public class PictureDetailActivity extends Activity {

    private final static int RADIUS = 150;
    private Rect mSrcRect = new Rect();
    private Point mPoint = new Point();
    private Bitmap resBitmap;
    private PopupWindow popupWindow;
    private MyScrollView mScrollView;
    Magnifier mMagnifier;
    private boolean mIsMagnifierMode = false;
    private ImageButton mIBMagnifier ;
    private ZoomImageView mZoomImageView;

    DisplayImageOptions mImageOptions;
    ImageLoader mImageLoader;

    public static void startActivity(Activity activity, int picId){
        Intent intent = new Intent(activity, PictureDetailActivity.class);
        intent.putExtra("id", picId);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_detail);
        mScrollView = (MyScrollView)findViewById(R.id.scrollView);
        //mScrollView.setOverScrollMode();
        final View view = findViewById(R.id.layout_picture);
        mIBMagnifier = (ImageButton)findViewById(R.id.imageButton_magnifier);
        mIBMagnifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsMagnifierMode = !mIsMagnifierMode;
                mScrollView.setMagnifierMode(mIsMagnifierMode);
            }
        });
        //resBitmap = ((BitmapDrawable)view.getBackground()).getBitmap();

//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                if((MotionEvent.ACTION_DOWN == action || MotionEvent.ACTION_MOVE == action)&&mIsMagnifierMode)
//                {
//                    int x = (int)event.getX();
//                    int y = (int)event.getY();// + mScrollView.getScrollY();
//                    Log.v("Tumcca", "x " + x + " y " + y);
//                    mSrcRect.set(x - RADIUS / 3, y - RADIUS / 3, x + RADIUS / 3, y + RADIUS / 3);
//                    mPoint.set((int)x, (int)y);
//                    if(mSrcRect.left < 0)
//                    {
//                        mSrcRect.offset(-mSrcRect.left, 0);
//                    }
//                    else
//                    if(mSrcRect.right > resBitmap.getWidth())
//                    {
//                        mSrcRect.offset(resBitmap.getWidth()-mSrcRect.right, 0);
//                    }
//                    if(mSrcRect.top < 0)
//                    {
//                        mSrcRect.offset(0, -mSrcRect.top);
//                    }
//                    else
//                    if(mSrcRect.bottom > resBitmap.getHeight())
//                    {
//                        mSrcRect.offset(0, resBitmap.getHeight()-mSrcRect.bottom);
//                    }
//
//                    popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x + 100, y);
//                    WindowManager windowManager = getWindowManager();
//                    Display display = windowManager.getDefaultDisplay();
//                    int screenWidth  = display.getWidth();
//                    int screenHeight = display.getHeight();
//                    if(screenHeight>screenWidth){
//                        popupWindow.update(x + 100,y - ((MyVerticalScrollView)mScrollView).getScrollY(), -1, -1);
//                    }
//                    else{
//                        popupWindow.update(x - ((MyHorizontalScrollView)mScrollView).getScrollX() + 100,y , -1, -1);
//                    }
//                    mMagnifier.invalidate();
//
//                }
//                if(MotionEvent.ACTION_UP == action)
//                {
//                    //mIsMagnifierMode = false;
//                    popupWindow.dismiss();
//                }
//                return true;
//            }
//        });
//
//        mMagnifier = new Magnifier(this);

        popupWindow = new PopupWindow(mMagnifier, RADIUS*2, RADIUS*2);

        mZoomImageView = (ZoomImageView)findViewById(R.id.zoom_view);

//        Picasso.with(this).load(PictureServer.getInstance().getPictureUrl(
//                getIntent().getIntExtra("id", -1))).into(mZoomImageView);

        if(null!=mZoomImageView){
            mImageOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.displayImage(PictureServer.getInstance().getPictureUrl(getIntent().getIntExtra("id", -1)), mZoomImageView, mImageOptions);
        }
        else{
            Toast.makeText(this, "图片缩放控件初始化失败", Toast.LENGTH_SHORT).show();
            finish();
        }

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
            rect = new Rect(0, 0, RADIUS *2, RADIUS *2);
        }
        protected void onDraw(Canvas canvas)
        {
            canvas.save();

            Path path = new Path();
            path.reset();
            canvas.clipPath(path, Region.Op.REPLACE);
            path.addCircle(RADIUS, RADIUS, RADIUS, Path.Direction.CW);
            canvas.clipPath(path, Region.Op.REPLACE);
            paint.setAlpha(255);
            canvas.drawBitmap(resBitmap, mSrcRect, rect, paint);
            canvas.drawCircle(rect.centerX(), rect.centerY(), RADIUS, paint);
        }
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event)
//    {
//        // TODO Auto-generated method stub
//        int action = event.getAction();
//        if(MotionEvent.ACTION_DOWN == action || MotionEvent.ACTION_MOVE == action)
//        {
//            float x = (float)event.getX();
//            float y = (float)event.getY();
//            System.out.println("X: " + x + ", Y: " + y);
//            mPoint.set((int)x, (int)y);
//            //对于越界处理 http://www.ligotop.com
//            if(mSrcRect.left < 0)
//            {
//                mSrcRect.offset(-mSrcRect.left, 0);
//            }
//            else
//            if(mSrcRect.right > resBitmap.getWidth())
//            {
//                mSrcRect.offset(resBitmap.getWidth()-mSrcRect.right, 0);
//            }
//            if(mSrcRect.top < 0)
//            {
//                mSrcRect.offset(0, -mSrcRect.top);
//            }
//            else
//            if(mSrcRect.bottom > resBitmap.getHeight())
//            {
//                mSrcRect.offset(0, resBitmap.getHeight()-mSrcRect.bottom);
//            }
//        }
//        return true;
//
//    }



}