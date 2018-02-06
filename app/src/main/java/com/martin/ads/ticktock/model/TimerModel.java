package com.martin.ads.ticktock.model;

import android.net.Uri;

import com.martin.ads.ticktock.utils.DateData;

/**
 * Created by Ads on 2018/2/2.
 */

public class TimerModel {
    private String uuid;
    private DateData timerTimeData;
    private Uri ringtoneUri;
    private boolean vibrate;
    private boolean on;
    public TimerModel(String uuid, DateData timerTimeData, Uri ringtoneUri, boolean vibrate) {
        this.uuid = uuid;
        this.timerTimeData = timerTimeData;
        this.ringtoneUri = ringtoneUri;
        this.vibrate = vibrate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public DateData getTimerTimeData() {
        return timerTimeData;
    }

    public void setTimerTimeData(DateData timerTimeData) {
        this.timerTimeData = timerTimeData;
    }

    public Uri getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(Uri ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public String toString() {
        return  " Time: "+timerTimeData.getTimeStr()+"\n"+
                " Uri: "+ringtoneUri.toString()+"\n"+
                " Vibrate: "+vibrate+"\n"+
                " uuid: "+uuid+"\n";
    }
}
