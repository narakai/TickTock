package com.martin.ads.ticktock.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.ui.StartingActivity;
import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.TimeRetriever;

import java.util.Date;

/**
 * Created by Ads on 2016/6/14.
 */
public class TickingService extends Service {
    private static final String TAG = "TickingService";
    public final static int TICKING_ON=1;
    public final static int TICKING_OFF=3;
    private TickingBinder mBinder=new TickingBinder(TICKING_ON);
    public class TickingBinder extends Binder {
        private int tickingState;

        TickingBinder(int tickingState){this.tickingState=tickingState;}
        public int getTickingState(){return tickingState;}
        public void requestStop(){
            stopSelf();
        }
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        Intent i=new Intent(this,StartingActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,0);
        Notification.Builder builder=new Notification.Builder(this);
        Notification notification= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder
                    .setTicker(getResources().getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.app_name)+"正在运行")
                    .setContentIntent(pi)
                    .build();
            startForeground(1,notification);
        }

        TimeRetriever.getInstance().addTimeUpdateCallback(new TimeRetriever.OnTimeUpdateCallback() {
            @Override
            public void onTimeUpdate(DateData dateData, TimeRetriever.STATE state) {
                switch (state){
                    case NEW_SECOND:
                        Log.d(TAG, "onTimeUpdate: inside service");
                        break;
                    case NEW_SECOND_HALF:
                    case NOTHING_SERIOUS:
                    default:
                        break;
                }
            }
        });
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }
}
