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
        dateData.setWithCalender(cal);
    }

    public static String calenderToFormatStr(Calendar calendar){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String rt = sdf.format(calendar.getTime());
        return rt;
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
