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
import com.martin.ads.ticktock.lockscreenmsg.LockScreenMessageActions;
import com.martin.ads.ticktock.model.NotifyTaskModel;
import com.martin.ads.ticktock.model.TimerModel;
import com.martin.ads.ticktock.ui.StartingActivity;
import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.DateUtils;
import com.martin.ads.ticktock.utils.Logger;
import com.martin.ads.ticktock.utils.TimeRetriever;
import com.martin.ads.ticktock.utils.VibratorUtils;
import com.martin.ads.ticktock.utils.ringtone.RingTonePlayer;
import com.martin.ads.ticktock.utils.ringtone.RingtoneHardCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ads on 2016/6/14.
 */
public class TickingService extends Service {
    private static final String TAG = "TickingService";
    public final static int TICKING_ON=1;
    public final static int TICKING_OFF=3;
    private TickingBinder mBinder=new TickingBinder(TICKING_ON);
    private ArrayList<NotifyTaskModel> notifyTaskModels;
    private RingTonePlayer ringTonePlayer;
    public class TickingBinder extends Binder {
        private int tickingState;

        TickingBinder(int tickingState){this.tickingState=tickingState;}
        public int getTickingState(){return tickingState;}
        public void requestStop(){
            ringTonePlayer.release();
            stopSelf();
        }
        public boolean addTask(NotifyTaskModel notifyTaskModel){
            Logger.d(TAG, "addTask: "+notifyTaskModel.getTimerModel().getTimerTimeData().getTimeStr());
            if(!isTimerOn(notifyTaskModel.getTimerModel())){
                notifyTaskModels.add(notifyTaskModel);
                updateNotification();
                return true;
            }else return false;
        }
        public void removeTask(TimerModel timerModel){
            Logger.d(TAG, "removeTask: "+timerModel.getTimerTimeData().getTimeStr());
            for(NotifyTaskModel notifyTaskModel:notifyTaskModels){
                if(notifyTaskModel.getTimerModel().getUuid().equals(timerModel.getUuid())){
                    notifyTaskModels.remove(notifyTaskModel);
                    ringTonePlayer.stop();
                    break;
                }
            }
            updateNotification();
        }
        public void removeAllTask(){
            Logger.d(TAG, "removeAllTask: ");
            ringTonePlayer.stop();
            notifyTaskModels.clear();
            updateNotification();
        }

        public boolean isTimerOn(TimerModel timerModel){
            Logger.d(TAG, "check isTimerOn: "+timerModel.getTimerTimeData().getTimeStr());
            for(NotifyTaskModel notifyTaskModel:notifyTaskModels){
                if(notifyTaskModel.getTimerModel().getUuid().equals(timerModel.getUuid())){
                    return true;
                }
            }
            return false;
        }
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Logger.d(TAG, "onCreate: ");
        notifyTaskModels=new ArrayList<>();

        updateNotification();

        TimeRetriever.getInstance().addTimeUpdateCallback(new TimeRetriever.OnTimeUpdateCallback() {
            @Override
            public void onTimeUpdate(DateData dateData, TimeRetriever.STATE state) {
                switch (state){
                    case NEW_SECOND:
                        //Logger.d(TAG, "onTimeUpdate: inside service");
                        processDateData(dateData);
                        break;
                    case NEW_SECOND_HALF:
                    case NOTHING_SERIOUS:
                    default:
                        break;
                }
            }
        });

        ringTonePlayer=new RingTonePlayer(this);
    }

    private void processDateData(DateData dat) {
        DateData dateData=dat.copy();
        boolean shouldNotify=false;
        NotifyTaskModel notifyTaskModel=null;
        for(NotifyTaskModel task:notifyTaskModels){
            if(task.getNextNotifyDate().equalsInTime(dateData)){
                shouldNotify=true;
                notifyTaskModel=task;
                task.setLastNotifiedDate(dateData);
                task.setNextNotifyDate();
            }
        }
        if(shouldNotify){
            Intent stopIntent=new Intent();
            stopIntent.setAction(LockScreenMessageActions.TAG_STOP);
            sendBroadcast(stopIntent);
            Intent startIntent=new Intent();
            startIntent.setAction(LockScreenMessageActions.TAG_START);
            sendBroadcast(startIntent);
            if(notifyTaskModel.getTimerModel().isVibrate())
                VibratorUtils.vibrate(this);
            try {
                if(!notifyTaskModel.getTimerModel().getRingtoneUri().toString().equals(RingtoneHardCode.URI_SILENT.toString())){
                    ringTonePlayer.playRingtone(notifyTaskModel.getTimerModel().getRingtoneUri());
                }
            } catch (IOException e) {
                e.printStackTrace();
                //Cannot play ringtone
            }
            if(notifyTaskModel.getTimerModel().isMonitor()){
                //TODO:take picture
                Log.d(TAG, "processDateData: take pic");
            }
        }

    }

    private void updateNotification() {
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
                    .setContentText(notifyTaskModels.size()+"个定时器正在运行")
                    .setContentIntent(pi)
                    .build();
            startForeground(1,notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Logger.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }
}
