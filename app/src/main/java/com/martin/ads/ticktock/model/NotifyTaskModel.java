package com.martin.ads.ticktock.model;

import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.DateUtils;
import com.martin.ads.ticktock.utils.Logger;
import com.martin.ads.ticktock.utils.TimeRetriever;

import java.util.Calendar;

/**
 * Created by Ads on 2018/2/6.
 */

public class NotifyTaskModel {
    private DateData lastNotifiedDate;
    private TimerModel timerModel;
    private DateData nextNotifyDate;

    public NotifyTaskModel() {
        nextNotifyDate=new DateData();
    }

    public NotifyTaskModel(DateData lastNotifiedDate, TimerModel timerModel, DateData nextNotifyDate) {
        this.lastNotifiedDate = lastNotifiedDate;
        this.timerModel = timerModel;
        this.nextNotifyDate = nextNotifyDate;
    }

    public DateData getLastNotifiedDate() {
        return lastNotifiedDate;
    }

    public void setLastNotifiedDate(DateData lastNotifiedDate) {
        this.lastNotifiedDate = lastNotifiedDate;
    }

    public TimerModel getTimerModel() {
        return timerModel;
    }

    public void setTimerModel(TimerModel timerModel) {
        this.timerModel = timerModel;
    }

    public DateData getNextNotifyDate() {
        return nextNotifyDate;
    }

    public void setNextNotifyDate() {
        Calendar addC=timerModel.getTimerTimeData().copy().toCalendar(TimeRetriever.LOCALE);
        Calendar curCal=lastNotifiedDate.toCalendar(TimeRetriever.LOCALE);
        nextNotifyDate.setWithCalender(DateUtils.addHMSWithCalendar(curCal,addC)) ;
        Logger.d("lalala","Pre："+lastNotifiedDate.getTimeStr()+" Nxt:"+nextNotifyDate.getTimeStr());
    }
}
