package com.martin.ads.ticktock.utils;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ads on 2018/2/2.
 */

public class TimeRetriever {
    private static final String TAG = "TimeRetriever";
    public enum STATE{
        //进入0.0,0.5,其他
        NEW_SECOND,NEW_SECOND_HALF,NOTHING_SERIOUS
    }
    private static TimeRetriever timeRetrieverInstance=null;
    private static boolean stop;

    private Handler handler;
    private Runnable updateTimeTask;
    private ArrayList<OnTimeUpdateCallback> onTimeUpdateCallbacks;
    private DateData oldDateData;
    public static final String LOCALE="GMT+8";

    //运行在UI线程中
    private TimeRetriever() {
        handler=new Handler();
        onTimeUpdateCallbacks=new ArrayList<>();
        updateTimeTask=new Runnable() {
            @Override
            public void run() {
                DateUtils.updateTime(LOCALE);
                DateData dateData =DateUtils.getDateData();
                STATE state=STATE.NOTHING_SERIOUS;
                if(!dateData.equalInHalfSecond(oldDateData)){
                    if(MiscUtils.inRange(dateData.getMilliSecond(),0,500))
                        state=STATE.NEW_SECOND;
                    else state=STATE.NEW_SECOND_HALF;
                }
                for(OnTimeUpdateCallback onTimeUpdateCallback:onTimeUpdateCallbacks)
                    onTimeUpdateCallback.onTimeUpdate(dateData,state);
                oldDateData=dateData.copy();
                if(!stop)
                    handler.postDelayed(this,31);
            }
        };
        handler.post(updateTimeTask);
    }

    public interface OnTimeUpdateCallback{
        void onTimeUpdate(DateData dateData,STATE state);
    }

    public static TimeRetriever getInstance(){
        if(timeRetrieverInstance==null){
            stop=false;
            timeRetrieverInstance=new TimeRetriever();
        }
        return timeRetrieverInstance;
    }

    public static void requestStop() {
        stop=true;
        timeRetrieverInstance=null;
    }

    public void addTimeUpdateCallback(@NonNull OnTimeUpdateCallback onTimeUpdateCallback){
        onTimeUpdateCallbacks.add(onTimeUpdateCallback);
    }
}
