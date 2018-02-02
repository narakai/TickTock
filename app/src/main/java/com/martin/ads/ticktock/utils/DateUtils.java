package com.martin.ads.ticktock.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Ads on 2018/1/29.
 */

public class DateUtils {
    private static DateData dateData =null;
    static {
        dateData =new DateData();
    }

    public static void updateTime(String locale){
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(locale));
        cal.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        dateData.setYear(cal.get(Calendar.YEAR));
        dateData.setMonth(cal.get(Calendar.MONTH) + 1);
        dateData.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
        dateData.setHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
        dateData.setMinute(cal.get(Calendar.MINUTE));
        dateData.setSecond(cal.get(Calendar.SECOND));
        dateData.setMilliSecond(cal.get(Calendar.MILLISECOND));
        dateData.setDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
    }

    @Deprecated
    public static String getLocalDatetimeStringOld(String local) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(local));
        String rt = sdf.format(calendar.getTime());
        return rt;
    }

    public static DateData getDateData() {
        return dateData;
    }
}
