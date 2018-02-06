package com.martin.ads.ticktock.model;

import android.net.Uri;

import com.martin.ads.ticktock.utils.DateData;

/**
 * Created by Ads on 2018/2/2.
 */

public class TimerModel {
    private DateData timerTimeData;
    private Uri ringtoneUri;
    private boolean vibrate;

    public TimerModel(DateData timerTimeData, Uri ringtoneUri, boolean vibrate) {
        this.timerTimeData = timerTimeData;
        this.ringtoneUri = ringtoneUri;
        this.vibrate = vibrate;
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

    @Override
    public String toString() {
        return  " Time: "+timerTimeData.getTimeStr()+"\n"+
                " Uri: "+ringtoneUri.toString()+"\n"+
                " Vibrate: "+vibrate;
    }
}
