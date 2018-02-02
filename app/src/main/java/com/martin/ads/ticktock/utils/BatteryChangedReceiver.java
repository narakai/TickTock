package com.martin.ads.ticktock.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by Ads on 2018/1/29.
 */

public class BatteryChangedReceiver extends BroadcastReceiver {
    private boolean charging;
    private BatteryUpdateCallback batteryUpdateCallback;

    public BatteryChangedReceiver() {
        super();
        charging=false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
            // 电池当前的电量, 它介于0和 EXTRA_SCALE之间
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            // 电池电量的最大值
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int percent=level*100/scale;

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    // 正在充电
                    charging=true;
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                case BatteryManager.BATTERY_STATUS_FULL:
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    // 没有充电
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    // 未知状态
                default:
                    charging=false;
                    break;
            }
            String str=(charging?"充电中：":"")+percent+"%";
            if(batteryUpdateCallback!=null)
                batteryUpdateCallback.onUpdate(str);
        }
    }

    public void setBatteryUpdateCallback(BatteryUpdateCallback batteryUpdateCallback) {
        this.batteryUpdateCallback = batteryUpdateCallback;
    }

    public interface BatteryUpdateCallback{
        void onUpdate(String str);
    }
}
