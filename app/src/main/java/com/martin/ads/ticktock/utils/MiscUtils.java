package com.martin.ads.ticktock.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Ads on 2018/2/2.
 */

public class MiscUtils {
    public static boolean inRange(int val,int min,int max){
        return val>=min && val<max;
    }

    public static void startActivity(Context context,Class<?> cls){
        context.startActivity(new Intent(context,cls));
    }
}
