package com.martin.ads.ticktock.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Ads on 2018/2/6.
 */

public class VibratorUtils {
    public static void vibrate(Context context){
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if(vibrator!=null)
            vibrator.vibrate(new long[]{0, 200, 100, 200,100,200}, -1);
    }
}
