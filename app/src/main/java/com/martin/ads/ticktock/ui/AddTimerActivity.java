package com.martin.ads.ticktock.ui;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.DateUtils;
import com.martin.ads.ticktock.utils.TimeRetriever;
import com.martin.ads.ui.switchbutton.SwitchButton;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTimerActivity extends AppCompatActivity {

    TimePickerDialog timePickerDialogHourMinuteSecond;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_timer);
        getSupportActionBar().setTitle(R.string.add_timer_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        timePickerDialogHourMinuteSecond = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        DateData dateData=DateUtils.getDateData().copy();
                        Calendar curCal=dateData.toCalendar(TimeRetriever.LOCALE);
                        Calendar addC=getDateDataFromMilSec(millseconds).toCalendar(TimeRetriever.LOCALE);
                        String curTime="当前时间"+DateUtils.calenderToFormatStr(curCal);
                        DateData newDateData=new DateData().setWithCalender(DateUtils.addHMSWithCalendar(curCal,addC));
                        String nxtTime="下次提醒"+newDateData.getTimeStr();
                        Toast.makeText(AddTimerActivity.this,curTime+" "+nxtTime ,Toast.LENGTH_LONG).show();
                    }
                })
                .setType(Type.HOURS_MINS_SECONDS)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("设置提醒间隔")
                .setHourText("小时")
                .setMinuteText("分钟")
                .setCyclic(false)
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorPrimary))
                .setWheelItemTextSize(12)
                .build();


        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialogHourMinuteSecond.show(getSupportFragmentManager(), "all");
            }
        });

        SwitchButton switchButton = findViewById(R.id.vibrate_switch_btn);
        switchButton.setChecked(true);
        switchButton.setShadowEffect(true);//disable shadow effect
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                TastyToast.makeText(getApplicationContext(), "已保存至SD卡"+isChecked, TastyToast.LENGTH_SHORT,
                        TastyToast.SUCCESS);
            }
        });
    }


    public DateData getDateDataFromMilSec(long millSecond) {
        Date d = new Date(millSecond);
        String ret=new SimpleDateFormat("HH:mm:ss").format(d);
        int hour= Integer.parseInt(ret.substring(0,2));
        int minute= Integer.parseInt(ret.substring(3,5));
        int second= Integer.parseInt(ret.substring(6,ret.length()));
        return new DateData(hour,minute,second);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_timer, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save_timer:
                //save
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
