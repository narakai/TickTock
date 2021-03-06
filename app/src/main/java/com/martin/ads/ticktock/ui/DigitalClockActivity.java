package com.martin.ads.ticktock.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.martin.ads.ticktock.constant.Constant;
import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.lockscreenmsg.MyService;
import com.martin.ads.ticktock.service.camera.CameraEngine;
import com.martin.ads.ticktock.utils.AnimationUtils;
import com.martin.ads.ticktock.utils.BatteryChangedReceiver;
import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.Logger;
import com.martin.ads.ticktock.utils.Lunar;
import com.martin.ads.ticktock.utils.TimeRetriever;
import com.martin.ads.ticktock.utils.TouchHelper;
import com.martin.ads.ui.bubbleseekbar.BubbleSeekBar;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.IOException;
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
    private SwitchButton showSecondBtn;

    private ImageButton settingBtn;

    private ArrayList<TextView> timeViews;
    private LinearLayout timeTextPanel;

    private ArrayList<TextView> allTextViews;
    private ArrayList<View> allViews;

    private static final String FONT_PATH="fonts/Milibus Sb.ttf";
    private Typeface typeface;

    private BatteryChangedReceiver batteryChangedReceiver;

    private View touchPanel;
    private Constant.LightMode lightMode;

    private TouchHelper touchHelper;

    private int windowWidth;

    private LinearLayout settingView;

    private TimeRetriever timeRetriever;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //160,160/4,35,16
    private float timeTextSize,dateTextSize,batteryTextSize;

    public static Camera camera = null;
    public static Activity instance = null;
    private SurfaceView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG,"enter onCreate ");
        instance = this;
        //??????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //??????
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.content_digital_clock);

        //????????????
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);

        touchHelper=new TouchHelper(this);

        windowWidth=getResources().getDisplayMetrics().widthPixels;

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        findViews();

        initViews();

        initTimer();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                if(camera==null) camera = CameraEngine.getFrontCamera();
                if(camera!=null){
                    try {
                        camera.setPreviewDisplay(cameraView.getHolder());
                        camera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cameraView.bringToFront();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                if (camera != null) {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });
    }

    private void findViews() {
        settingBtn=findViewById(R.id.btn_settings);
        settingView=findViewById(R.id.setting_view);
        batteryText=findViewById(R.id.battery_text);
        dateText=findViewById(R.id.date_text);
        hourText = findViewById(R.id.time_hour);
        colonText = findViewById(R.id.time_colon);
        minuteText = findViewById(R.id.time_minute);
        secondText = findViewById(R.id.time_second);
        timeTextPanel=findViewById(R.id.time_text_panel);
        touchPanel=findViewById(R.id.touch_panel);
        showSecondBtn=findViewById(R.id.btn_show_second);

        boolean showSecond=sharedPreferences.getBoolean("showSecond",false);
        showSecondBtn.setChecked(showSecond);
        if(showSecond){
            secondText.setVisibility(View.VISIBLE);
        }else secondText.setVisibility(View.GONE);
        showSecondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor=sharedPreferences.edit();
                editor.putBoolean("showSecond",showSecondBtn.isChecked());
                editor.apply();
                if(showSecondBtn.isChecked())
                    secondText.setVisibility(View.VISIBLE);
                else secondText.setVisibility(View.GONE);
            }
        });

        cameraView = findViewById(R.id.dummy_camera_view);
    }

    private void initTimer() {
        timeRetriever=TimeRetriever.getInstance();
        timeRetriever.addTimeUpdateCallback(new TimeRetriever.OnTimeUpdateCallback() {
            @Override
            public void onTimeUpdate(DateData dateData, TimeRetriever.STATE state) {
                //oncreate ??????????????????????????????????????????
                //Logger.d(TAG, "onTimeUpdate: "+state);
                if(state!= TimeRetriever.STATE.NOTHING_SERIOUS){
                    hourText.setText(dateData.get02Str(dateData.getHourOfDay()));
                    minuteText.setText(dateData.get02Str(dateData.getMinute()));
                    secondText.setText(dateData.get02Str(dateData.getSecond()));

                    boolean showLunar=dateData.getSecond()%6<3;
                    if(!showLunar) dateText.setText(dateData.getDateStr()+"  "+ dateData.getDayOfWeekStr());
                    else dateText.setText(Lunar.getInstance().getLunar(dateData.toCalendar(TimeRetriever.LOCALE))+"  "+ dateData.getDayOfWeekStr());
                    //TODO
//                    int h=dateData.getHourOfDay();
//                    if(h>=22 || h<=5)
//                        switchToNightMode();
//                    else switchToDayLightMode();
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

    private void initViews() {
        dateViews=new ArrayList<>();
        dateViews.add(dateText);
        colonText.setText(":");
        timeViews=new ArrayList<>();
        timeViews.add(hourText);
        timeViews.add(colonText);
        timeViews.add(minuteText);
        timeViews.add(secondText);
        typeface= Typeface.createFromAsset(getAssets(), FONT_PATH);
        allTextViews=new ArrayList<>();
        allTextViews.addAll(dateViews);
        allTextViews.addAll(timeViews);
        allTextViews.add(batteryText);
        for(TextView textView:allTextViews){
            textView.setTextColor(Color.WHITE);
        }
        allViews=new ArrayList<>();
        allViews.addAll(allTextViews);
        allViews.add(settingBtn);
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
                //Intent intent = new Intent(DigitalClockActivity.this, MyService.class);
                //startService(intent); //??????
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
//                TastyToast.makeText(getApplicationContext(), "????????????SD???", TastyToast.LENGTH_SHORT,
//                        TastyToast.INFO);
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
                .bubbleColor(ContextCompat.getColor(this,R.color.color_app))
                .secondTrackColor(ContextCompat.getColor(this,R.color.color_app))
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
                timeTextSize=progressFloat;
                editor=sharedPreferences.edit();
                editor.putFloat("timeTextSize",timeTextSize);
                editor.apply();
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
                .bubbleColor(ContextCompat.getColor(this,R.color.color_app))
                .secondTrackColor(ContextCompat.getColor(this,R.color.color_app))
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
                dateTextSize=progressFloat;
                editor=sharedPreferences.edit();
                editor.putFloat("dateTextSize",dateTextSize);
                editor.apply();
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
                .bubbleColor(ContextCompat.getColor(this,R.color.color_app))
                .secondTrackColor(ContextCompat.getColor(this,R.color.color_app))
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
        batteryTextSize=16;
        timeTextSize=sharedPreferences.getFloat("timeTextSize",-1);
        dateTextSize=sharedPreferences.getFloat("dateTextSize",-1);
        if(timeTextSize>0) return;
        int normalSize=60;
        hourText.setTextSize(normalSize);
        secondText.setTextSize(normalSize/TIME_SECOND_SIZE_RATIO);
        float size=hourText.getPaint().measureText(NORMAL_TIME_STR)
                +secondText.getPaint().measureText(NORMAL_SECOND_STR);
        timeTextSize=windowWidth/size*normalSize-2;
        dateTextSize=35;
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
            setAlpha(0.48f);
            lightMode=Constant.LightMode.NIGHT;
            //TODO clear log.d
            //setAppBrightness(-1);
        }
    }

    private void switchToDayLightMode(){
        if(lightMode==Constant.LightMode.NIGHT){
            setAlpha(1.0f);
            lightMode=Constant.LightMode.DAYLIGHT;
            //TODO
            //setAppBrightness(255);
        }
    }

    private void setAlpha(float alpha){
        for(View v:allViews) {
            v.setAlpha(alpha);
        }
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

    public static void requestRestart(){
        if(instance!=null && camera!=null){
            instance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = instance.getIntent();
                    instance.finish();
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    instance.startActivity(intent);
                }
            });
        }
    }
}
