package com.martin.ads.ticktock.lockscreenmsg;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.martin.ads.ticktock.R;
import com.sdsmdg.tastytoast.TastyToast;

/**
 * 锁屏消息内容的activity
 */
public class LockScreenMessageActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //四个标志位顾名思义，分别是锁屏状态下显示，解锁，保持屏幕长亮，打开屏幕。这样当Activity启动的时候，它会解锁并亮屏显示。
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏状态下显示
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
              //  | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕长亮
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); //打开屏幕
        //使用手机的背景
        Drawable wallPaper = WallpaperManager.getInstance(this).getDrawable();
        win.setBackgroundDrawable(wallPaper);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_message);
        LockScreenActivityManager.addActivity(this);
        mContext = this;
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //获取电源管理器对象
//        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        if (!pm.isScreenOn()) {
//            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
//                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
//            wl.acquire();  //点亮屏幕
//            wl.release();  //任务结束后释放
//        }
    }

    private void initView() {
//            //先解锁系统自带锁屏服务，放在锁屏界面里面
//            KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
//            keyguardManager.newKeyguardLock("").disableKeyguard(); //解锁
//            //点击进入消息对应的页面
//            mContext.startActivity(new Intent(mContext, DetailsActivity.class));
//            finish();
        findViewById(R.id.message_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                killSelf();
            }
        });
        findViewById(R.id.btn_notice_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killSelf();
            }
        });
        findViewById(R.id.btn_stop_timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TastyToast.makeText(getApplicationContext(), "这个功能还不存在", TastyToast.LENGTH_SHORT,
                        TastyToast.CONFUSING);
            }
        });
    }

    private void killSelf() {
        LockScreenActivityManager.removeActivity(this);
        finish();
    }
}
