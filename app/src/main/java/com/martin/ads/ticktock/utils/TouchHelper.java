package com.martin.ads.ticktock.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Created by Ads on 2018/1/29.
 */

public class TouchHelper {
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context context;
    private TouchCallback touchCallback;

    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 0.2f;

    public TouchHelper(Context context) {
        this.context = context;
        init();
    }

    private void init(){
        gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(touchCallback!=null)
                    touchCallback.onClick();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float dx=distanceX / sDensity * sDamping;
                float dy=distanceY / sDensity * sDamping;
                if(touchCallback!=null)
                    touchCallback.onScroll(dx,dy);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        scaleGestureDetector=new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor=detector.getScaleFactor();
                if(touchCallback!=null)
                    touchCallback.onScale(scaleFactor);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                //return true to enter onScale()
                if(touchCallback!=null)
                    touchCallback.onScaleBegin();;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                if(touchCallback!=null)
                    touchCallback.onScaleEnd();
            }
        });
    }

    public boolean handleTouchEvent(MotionEvent event) {
        //int action = event.getActionMasked();
        //也可以通过event.getPointerCount()来判断是双指缩放还是单指触控
        boolean ret=scaleGestureDetector.onTouchEvent(event);
        if (!scaleGestureDetector.isInProgress()){
            ret=gestureDetector.onTouchEvent(event);
        }
        return ret;
    }

    public void setTouchCallback(TouchCallback touchCallback) {
        this.touchCallback = touchCallback;
    }

    public interface TouchCallback{
        void onClick();
        void onScroll(float distanceX, float distanceY);
        void onScaleBegin();
        void onScale(float scaleFactor);
        void onScaleEnd();
    }
}
