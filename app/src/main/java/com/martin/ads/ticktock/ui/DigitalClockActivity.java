package com.martin.ads.ticktock.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.martin.ads.ticktock.constant.Constant;
import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.utils.AnimationUtils;
import com.martin.ads.ticktock.utils.BatteryChangedReceiver;
import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.Logger;
import com.martin.ads.ticktock.utils.TimeRetriever;
import com.martin.ads.ticktock.utils.TouchHelper;
import com.martin.ads.ui.bubbleseekbar.BubbleSeekBar;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

/**
 * Created by Ads on 2018/1/29.
 */

public class DigitalClockActivity extends AppCompatActivity {
    private static final String TAG = "DigitalClockActivity";

    public static final String NORMAL_TIME_STR = "12:28";
    public static final String NORMAL_SECOND_STR = "28";
    public static final int TIME_SECOND_SIZE_RATIO = 4;
    private TextView dateText;
    private ArrayList<TextView> dateViews;

    private TextView batteryText;
    private TextView hourText;
    private TextView minuteText;
    private TextView secondText;
    private TextView colonText;
    private ArrayList<TextView> timeViews;
    private LinearLayout timeTextPanel;

    private ArrayList<TextView> allTextViews;

    private static final String FONT_PATH="fonts/NotoSansBuginese-Regular.ttf";
    private Typeface typeface;

    private BatteryChangedReceiver batteryChangedReceiver;

    private View touchPanel;
    private Constant.LightMode lightMode;

    private TouchHelper touchHelper;

    private int windowWidth;

    private LinearLayout settingView;

    private TimeRetriever timeRetriever;

    //160,160/4,35,16
    private float timeTextSize,dateTextSize,batteryTextSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG,"enter onCreate ");
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //旋转
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.content_digital_clock);

        //保持亮屏
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);

        touchHelper=new TouchHelper(this);

        initViews();

        initTimer();
    }

    private void initTimer() {
        timeRetriever=TimeRetriever.getInstance();
        timeRetriever.addTimeUpdateCallback(new TimeRetriever.OnTimeUpdateCallback() {
            @Override
            public void onTimeUpdate(DateData dateData, TimeRetriever.STATE state) {
                //oncreate 如果有两次，那就会被调用两次
                //Logger.d(TAG, "onTimeUpdate: "+state);
                if(state!= TimeRetriever.STATE.NOTHING_SERIOUS){
                    hourText.setText(dateData.get02Str(dateData.getHourOfDay()));
                    minuteText.setText(dateData.get02Str(dateData.getMinute()));
                    secondText.setText(dateData.get02Str(dateData.getSecond()));
                    dateText.setText(dateData.getDateStr()+"  "+ dateData.getDayOfWeekStr());
                    //TODO
                    int h=dateData.getHourOfDay();
                    if(h>=22 || h<=5)
                        switchToNightMode();
                    else switchToDayLightMode();
                }
                switch (state){
                    case NEW_SECOND:
                        colonText.setVisibility(View.VISIBLE);
                        break;
                    case NEW_SECOND_HALF:
                        colonText.setVisibility(View.INVISIBLE);
                        break;
                    case NOTHING_SERIOUS:
                    default:
                        break;
                }
            }
        });
        batteryChangedReceiver=new BatteryChangedReceiver();
        batteryChangedReceiver.setBatteryUpdateCallback(new BatteryChangedReceiver.BatteryUpdateCallback() {
            @Override
            public void onUpdate(String str) {
                //Logger.d(TAG, "onUpdate: "+str);
                batteryText.setText(str);
            }
        });
        registerReceiver(batteryChangedReceiver,getBatteryFilter());
    }

    ImageButton settingBtn;
    private void initViews() {
        settingBtn=findViewById(R.id.btn_settings);
        settingView=findViewById(R.id.setting_view);
        windowWidth=getResources().getDisplayMetrics().widthPixels;
        batteryText=findViewById(R.id.battery_text);
        dateText=findViewById(R.id.date_text);
        dateViews=new ArrayList<>();
        dateViews.add(dateText);
        hourText = findViewById(R.id.time_hour);
        colonText = findViewById(R.id.time_colon);
        colonText.setText(":");
        minuteText = findViewById(R.id.time_minute);
        secondText = findViewById(R.id.time_second);
        timeViews=new ArrayList<>();
        timeViews.add(hourText);
        timeViews.add(colonText);
        timeViews.add(minuteText);
        timeViews.add(secondText);
        timeTextPanel=findViewById(R.id.time_text_panel);
        typeface= Typeface.createFromAsset(getAssets(), FONT_PATH);

        touchPanel=findViewById(R.id.touch_panel);

        allTextViews=new ArrayList<>();
        allTextViews.addAll(dateViews);
        allTextViews.addAll(timeViews);
        allTextViews.add(batteryText);
        for(TextView textView:allTextViews){
            textView.setTextColor(Color.WHITE);
        }
        initTextSize();
        for(TextView textView:timeViews){
            textView.setTextSize(timeTextSize);
            textView.setTypeface(typeface);
        }
        secondText.setTextSize(timeTextSize/ TIME_SECOND_SIZE_RATIO);
        for(TextView textView:dateViews){
            textView.setTextSize(dateTextSize);
        }
        batteryText.setTextSize(batteryTextSize);
        //TODO:remove
        lightMode=Constant.LightMode.NIGHT;
        switchToDayLightMode();

        touchPanel.setClickable(true);
        touchHelper.setTouchCallback(new TouchHelper.TouchCallback() {
            @Override
            public void onClick() {
                //startActivity(new Intent(DigitalClockActivity.this, AddTimerActivity.class));

//                Intent intent = new Intent(DigitalClockActivity.this, MyService.class);
//                startService(intent); //启动
                //TODO:remove
//                if(bb) setAppBrightness(255);
//                else setAppBrightness(-1);
//                bb=!bb;
                if(settingView.getVisibility()==View.VISIBLE)
                    AnimationUtils.displayAnim(settingView,DigitalClockActivity.this,R.anim.push_out_to_left,View.GONE);
            }

            @Override
            public void onScroll(float distanceX, float distanceY) {

            }

            @Override
            public void onScaleBegin() {
//                TopToast.with(DigitalClockActivity.this)
//                        .setTitle("设置成功")
//                        .setMessage("1分20秒后提醒")
//                        .sneakSuccess();
//                TastyToast.makeText(getApplicationContext(), "已保存至SD卡", TastyToast.LENGTH_SHORT,
//                        TastyToast.SUCCESS);
                TastyToast.makeText(getApplicationContext(), "已保存至SD卡", TastyToast.LENGTH_SHORT,
                        TastyToast.INFO);
            }

            @Override
            public void onScale(float scaleFactor) {
//                   f1=f1+(scaleFactor-1)*30;
            }

            @Override
            public void onScaleEnd() {
            }
        });

        touchPanel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return touchHelper.handleTouchEvent(event);
            }
        });

        BubbleSeekBar bubbleSeekBar=findViewById(R.id.test_seek);
        bubbleSeekBar.getConfigBuilder()
                .hideBubble()
                .bubbleColor(ContextCompat.getColor(this,R.color.app_color))
                .secondTrackColor(ContextCompat.getColor(this,R.color.app_color))
                .trackSize(4)
                .touchToSeek()
                .min(50.0f)
                .max(500.0f)
                .progress(timeTextSize)
                .build();
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                for(TextView textView:timeViews){
                    textView.setTextSize(progressFloat);
                }
                secondText.setTextSize(progressFloat/ TIME_SECOND_SIZE_RATIO);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        BubbleSeekBar bubbleSeekBar2=findViewById(R.id.test_seek2);
        bubbleSeekBar2.getConfigBuilder()
                .hideBubble()
                .bubbleColor(ContextCompat.getColor(this,R.color.app_color))
                .secondTrackColor(ContextCompat.getColor(this,R.color.app_color))
                .trackSize(4)
                .touchToSeek()
                .min(5.0f)
                .max(200.0f)
                .progress(dateTextSize)
                .build();
        bubbleSeekBar2.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                for(TextView textView:dateViews){
                    textView.setTextSize(progressFloat);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        BubbleSeekBar bubbleSeekBar3=findViewById(R.id.test_seek3);
        bubbleSeekBar3.getConfigBuilder()
                .hideBubble()
                .bubbleColor(ContextCompat.getColor(this,R.color.app_color))
                .secondTrackColor(ContextCompat.getColor(this,R.color.app_color))
                .trackSize(4)
                .touchToSeek()
                .min(-1)
                .max(255)
                .progress(-1)
                .build();
        //bubbleSeekBar3.correctOffsetWhenContainerOnScrolling();
        bubbleSeekBar3.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                setAppBrightness(progress);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationUtils.displayAnim(settingView,DigitalClockActivity.this,R.anim.push_from_left,View.VISIBLE);
            }
        });
    }

    private void initTextSize(){
        int normalSize=60;
        hourText.setTextSize(normalSize);
        secondText.setTextSize(normalSize/TIME_SECOND_SIZE_RATIO);
        float size=hourText.getPaint().measureText(NORMAL_TIME_STR)
                +secondText.getPaint().measureText(NORMAL_SECOND_STR);
        timeTextSize=windowWidth/size*normalSize-2;
        dateTextSize=35;
        batteryTextSize=16;
    }
    private IntentFilter getBatteryFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        return filter;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(batteryChangedReceiver);
        super.onDestroy();
    }

    private void switchToNightMode(){
        if(lightMode==Constant.LightMode.DAYLIGHT){
            setTextAlpha(0.48f);
            lightMode=Constant.LightMode.NIGHT;
            //TODO clear log.d
            //setAppBrightness(-1);
        }
    }

    private void switchToDayLightMode(){
        if(lightMode==Constant.LightMode.NIGHT){
            setTextAlpha(1.0f);
            lightMode=Constant.LightMode.DAYLIGHT;
            //TODO
            //setAppBrightness(255);
        }
    }

    private void setTextAlpha(float alpha){
        for(TextView textView:allTextViews) {
            textView.setAlpha(alpha);
        }
        settingBtn.setAlpha(alpha);
    }

    public void setAppBrightness(int brightness) {
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }
}
