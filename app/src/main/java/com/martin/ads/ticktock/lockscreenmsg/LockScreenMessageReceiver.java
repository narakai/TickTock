package com.martin.ads.ticktock.lockscreenmsg;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.martin.ads.ticktock.utils.Logger;

/**
 * 监听锁屏消息的广播接收器
 */
public class LockScreenMessageReceiver extends BroadcastReceiver {
    private static final String TAG = "LockScreenMsgReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "onReceive:收到了锁屏消息 ");
        String action = intent.getAction();
        if (action.equals(LockScreenMessageActions.TAG_START)) {
            //管理锁屏的一个服务
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (km.inKeyguardRestrictedInputMode()) {
                Logger.d(TAG,"onReceive:锁屏了 ");
                //判断是否锁屏
                Intent alarmIntent = new Intent(context, LockScreenMessageActivity.class);
                //在广播中启动Activity的context可能不是Activity对象，所以需要添加NEW_TASK的标志，否则启动时可能会报错。
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(alarmIntent); //启动显示锁屏消息的activity
            }
        }
    }
}
