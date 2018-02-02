package com.martin.ads.ticktock.utils;

import android.util.Log;

import com.martin.ads.ticktock.constant.Constant;

/**
 * Created by Ads on 2018/1/29.
 */

public class Logger {
    public static void d(String tag, String msg) {
        if(Constant.DEBUG)
            Log.d(tag,msg);
    }
}
