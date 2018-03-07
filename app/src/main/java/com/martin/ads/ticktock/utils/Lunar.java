package com.martin.ads.ticktock.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Lunar {
    public static Lunar lunarInstance=new Lunar();
    static SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    static final String[] chineseNumber = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊"};
    static final long[] lunarInfo = new long[]{(long) 19416, (long) 19168, (long) 42352, (long) 21717, (long) 53856, (long) 55632, (long) 91476, (long) 22176, (long) 39632, (long) 21970, (long) 19168, (long) 42422, (long) 42192, (long) 53840, (long) 119381, (long) 46400, (long) 54944, (long) 44450, (long) 38320, (long) 84343, (long) 18800, (long) 42160, (long) 46261, (long) 27216, (long) 27968, (long) 109396, (long) 11104, (long) 38256, (long) 21234, (long) 18800, (long) 25958, (long) 54432, (long) 59984, (long) 28309, (long) 23248, (long) 11104, (long) 100067, (long) 37600, (long) 116951, (long) 51536, (long) 54432, (long) 120998, (long) 46416, (long) 22176, (long) 107956, (long) 9680, (long) 37584, (long) 53938, (long) 43344, (long) 46423, (long) 27808, (long) 46416, (long) 86869, (long) 19872, (long) 42448, (long) 83315, (long) 21200, (long) 43432, (long) 59728, (long) 27296, (long) 44710, (long) 43856, (long) 19296, (long) 43748, (long) 42352, (long) 21088, (long) 62051, (long) 55632, (long) 23383, (long) 22176, (long) 38608, (long) 19925, (long) 19152, (long) 42192, (long) 54484, (long) 53840, (long) 54616, (long) 46400, (long) 46496, (long) 103846, (long) 38320, (long) 18864, (long) 43380, (long) 42160, (long) 45690, (long) 27216, (long) 27968, (long) 44870, (long) 43872, (long) 38256, (long) 19189, (long) 18800, (long) 25776, (long) 29859, (long) 59984, (long) 27480, (long) 21952, (long) 43872, (long) 38613, (long) 37600, (long) 51552, (long) 55636, (long) 54432, (long) 55888, (long) 30034, (long) 22176, (long) 43959, (long) 9680, (long) 37584, (long) 51893, (long) 43344, (long) 46240, (long) 47780, (long) 44368, (long) 21977, (long) 19360, (long) 42416, (long) 86390, (long) 21168, (long) 43312, (long) 31060, (long) 27296, (long) 44368, (long) 23378, (long) 19296, (long) 42726, (long) 42208, (long) 53856, (long) 60005, (long) 54576, (long) 23200, (long) 30371, (long) 38608, (long) 19415, (long) 19152, (long) 42192, (long) 118966, (long) 53840, (long) 54560, (long) 56645, (long) 46496, (long) 22224, (long) 21938, (long) 18864, (long) 42359, (long) 42160, (long) 43600, (long) 111189, (long) 27936, (long) 44448};
    int day;
    boolean leap;
    int month;
    int year;

    private Lunar(){}

    private static final String cyclicalm(int i) {
        return new StringBuffer().append(new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"}[i % 10]).append(new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"}[i % 12]).toString();
    }

    public static String getChinaDayString(int i) {
        int i2 = i % 10 == 0 ? 9 : (i % 10) - 1;
        if (i > 30) {
            return "";
        }
        if (i == 10) {
            return "初十";
        }
        return new StringBuffer().append(new String[]{"初", "十", "廿", "三"}[i / 10]).append(chineseNumber[i2]).toString();
    }

    private static final int leapDays(int i) {
        return leapMonth(i) != 0 ?
                (lunarInfo[i + -1900] & ((long) 65536)) != 0 ? 30 : 29 : 0;
    }

    private static final int leapMonth(int i) {
        return (int) (lunarInfo[i - 1900] & ((long) 15));
    }

    private static final int monthDays(int i, int i2) {
        return (lunarInfo[i + -1900] & ((long) (65536 >> i2))) == ((long) 0) ? 29 : 30;
    }

    private static final int yearDays(int i) {
        int i2 = 348;
        for (int i3 = 32768; i3 > 8; i3 >>= 1) {
            if ((lunarInfo[i - 1900] & ((long) i3)) != ((long) 0)) {
                i2++;
            }
        }
        return i2 + leapDays(i);
    }

    public final String animalsYear() {
        return new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"}[(this.year - 4) % 12];
    }

    public final String cyclical() {
        return cyclicalm((this.year - 1900) + 36);
    }

    public Lunar getLunar(Calendar calendar) {
        Date date = (Date) null;
        try {
            date = chineseDateFormat.parse("1900年1月31日");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int time = (int) ((calendar.getTime().getTime() - date.getTime()) / 86400000);
        int i = 14;
        int i2 = 1900;
        int i3 = 0;
        while (i2 < 2050 && time > 0) {
            i3 = yearDays(i2);
            time -= i3;
            i += 12;
            i2++;
        }
        if (time < 0) {
            time += i3;
            i2--;
            i -= 12;
        }
        this.year = i2;
        int leapMonth = leapMonth(i2);
        this.leap = false;
        int i4 = 0;
        i3 = i;
        i2 = time;
        time = 1;
        while (time < 13 && i2 > 0) {
            if (leapMonth <= 0 || time != leapMonth + 1 || this.leap) {
                i = monthDays(this.year, time);
            } else {
                time--;
                this.leap = true;
                i = leapDays(this.year);
            }
            i4 = i2 - i;
            if (this.leap && time == leapMonth + 1) {
                this.leap = false;
            }
            time++;
            i3 = !this.leap ? i3 + 1 : i3;
            i2 = i4;
            i4 = i;
        }
        if (i2 != 0 || leapMonth <= 0 || time != leapMonth + 1) {
            i = time;
        } else if (this.leap) {
            this.leap = false;
            i = time;
        } else {
            this.leap = true;
            i = i3 - 1;
            i = time - 1;
        }
        if (i2 < 0) {
            i2 += i4;
            i--;
        }
        this.month = i;
        this.day = i2 + 1;
        return this;
    }

    public String toString() {
        return new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(animalsYear()).append("年").toString()).append(this.leap ? "闰" : "").toString()).append(chineseNumber[this.month - 1]).toString()).append("月").toString()).append(getChinaDayString(this.day)).toString();
    }

    public static Lunar getInstance(){
        return lunarInstance;
    }
}
