package com.martin.ads.ticktock.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Ads on 2018/1/29.
 */

public class DateData {
    private static final String TAG = "DateData";
    private static final String WEEKDAY_CN[]={"","周日","周一","周二","周三","周四","周五","周六"};

    private int year;
    private int month;
    private int dayOfMonth;
    private int hourOfDay;
    private int minute;
    private int second;
    private int milliSecond;
    private int dayOfWeek;
    private String dayOfWeekStr;

    public DateData() {
        year=0;
        month=0;
        dayOfMonth=0;
        hourOfDay=0;
        minute=0;
        second=0;
        milliSecond=0;
    }

    public DateData(int hourOfDay, int minute, int second) {
        this();
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.second = second;
    }

    //00:00:00
    public DateData setWithTimeStr(String timeStr) {
        int hour= Integer.parseInt(timeStr.substring(0,2));
        int minute= Integer.parseInt(timeStr.substring(3,5));
        int second= Integer.parseInt(timeStr.substring(6,timeStr.length()));
        this.hourOfDay=hour;
        this.minute=minute;
        this.second=second;
        return this;
    }

    public DateData(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int milliSecond, int dayOfWeek) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.second = second;
        this.milliSecond = milliSecond;
        this.dayOfWeek = dayOfWeek;
        if(dayOfWeek>=1 && dayOfWeek<=7)
            this.dayOfWeekStr=WEEKDAY_CN[dayOfWeek];
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void setMilliSecond(int milliSecond) {
        this.milliSecond = milliSecond;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        if(dayOfWeek>=1 && dayOfWeek<=7)
            this.dayOfWeekStr=WEEKDAY_CN[dayOfWeek];
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getMilliSecond() {
        return milliSecond;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDayOfWeekStr() {
        return dayOfWeekStr;
    }

    public String getDateStr(){
        return String.format("%04d-%02d-%02d",year,month,dayOfMonth);
    }

    public String getTimeStr(){
        return String.format("%02d:%02d:%02d",hourOfDay,minute,second);
    }

    public boolean equalsInTime(DateData dateData){
        return dateData.hourOfDay==hourOfDay
                && dateData.minute==minute
                && dateData.second==second;
    }

    public String get02Str(int val){
        return String.format("%02d",val);
    }

    public DateData copy(){
        return new DateData(
                year,
                month,
                dayOfMonth,
                hourOfDay,
                minute,
                second,
                milliSecond,
                dayOfWeek);
    }

    public boolean equalInHalfSecond(DateData dateData) {
        if(dateData==null) return false;
        if(dateData.year==year
            && dateData.month==month
            && dateData.dayOfMonth==dayOfMonth
            && dateData.hourOfDay==hourOfDay
            && dateData.minute==minute
            && dateData.second==second){
            boolean b1=MiscUtils.inRange(dateData.milliSecond,0,500);
            boolean b2=MiscUtils.inRange(milliSecond,0,500);
            return (b1 && b2) || (!b1 && !b2);
        }
        return false;
    }

    public Calendar toCalendar(String locale){
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone(locale));
        calendar.set(year,month-1,dayOfMonth,hourOfDay,minute,second);
        return calendar;
    }

    public DateData setWithCalender(Calendar cal){
        setYear(cal.get(Calendar.YEAR));
        setMonth(cal.get(Calendar.MONTH) + 1);
        setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
        setHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
        setMinute(cal.get(Calendar.MINUTE));
        setSecond(cal.get(Calendar.SECOND));
        setMilliSecond(cal.get(Calendar.MILLISECOND));
        setDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
        return this;
    }

    public String getSimpleTimeStr(){
        return String.format("%02d小时 %02d分钟 %02d秒",hourOfDay,minute,second);
    }
}
