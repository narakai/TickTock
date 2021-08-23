package com.martin.ads.ticktock.service;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.lockscreenmsg.LockScreenMessageActions;
import com.martin.ads.ticktock.model.NotifyTaskModel;
import com.martin.ads.ticktock.model.TimerModel;
import com.martin.ads.ticktock.service.camera.CameraEngine;
import com.martin.ads.ticktock.ui.DigitalClockActivity;
import com.martin.ads.ticktock.ui.StartingActivity;
import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.Logger;
import com.martin.ads.ticktock.utils.TimeRetriever;
import com.martin.ads.ticktock.utils.VibratorUtils;
import com.martin.ads.ticktock.utils.ringtone.RingTonePlayer;
import com.martin.ads.ticktock.utils.ringtone.RingtoneHardCode;

import java.io.File;
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

    private File fileToCheck = null;
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
            sendNotification();
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
                File parentDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TickTock/Monitor/"+dat.getDateTimeStr());
                parentDir.mkdirs();
                File outputFile = new File(parentDir.getAbsolutePath()+"/" + dat.getFullTimeStr()+".jpg");
                if(DigitalClockActivity.camera!=null){
                    CameraEngine.takePicture(outputFile.getAbsolutePath());
                    if(fileToCheck!=null && !fileToCheck.exists()){
                        //如果始终没有新照片，重启
                        Log.d(TAG, "processDateData: "+fileToCheck.exists()+ " in" + outputFile.getAbsolutePath());
                        DigitalClockActivity.requestRestart();
                        fileToCheck = null;
                    }else fileToCheck = outputFile;
                }
            }
        }

    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String hint) {
        Intent i=new Intent(this,StartingActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,0);
        Notification notification= null;

        // Android 8.0 (API 26) 适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "com.martin.ads.ticktock.service";
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "TickingService",
                    NotificationManager.IMPORTANCE_DEFAULT);
            //闪光灯
            channel.enableLights(true);
            //锁屏显示通知
            channel.setLockscreenVisibility(VISIBILITY_PUBLIC);
            //闪关灯的灯光颜色
            channel.setLightColor(Color.BLUE);
            //是否允许震动
            channel.enableVibration(false);
            //设置可绕过 请勿打扰模式
            channel.setBypassDnd(true);
            NotificationManager manager = getNotificationManager();
            assert manager != null;
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notification = builder
                    .setTicker(getResources().getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(hint+notifyTaskModels.size()+"个定时器正在运行")
                    .setContentIntent(pi)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .build();
            return notification;
        }

        Notification.Builder builder=new Notification.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder
                    .setTicker(getResources().getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(hint+notifyTaskModels.size()+"个定时器正在运行")
                    .setContentIntent(pi)
                    .build();
        }
        return notification;
    }

    private void updateNotification(){
        startForeground(1, getNotification(""));
    }

    private void sendNotification(){
        NotificationManager notificationManager = getNotificationManager();
        if(null != notificationManager){
            notificationManager.notify(2,getNotification("时间到，"));
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

    @Override
    public void onDestroy() {
        // 移除通知
        stopForeground(true);
        super.onDestroy();
    }
}
