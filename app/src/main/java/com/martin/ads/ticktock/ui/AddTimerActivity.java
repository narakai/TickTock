package com.martin.ads.ticktock.ui;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.martin.ads.ticktock.R;

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
        timePickerDialogHourMinuteSecond = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        Toast.makeText(AddTimerActivity.this,getDateToString(millseconds),Toast.LENGTH_LONG).show();
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
    }


    public String getDateToString(long time) {
        Date d = new Date(time);
        String ret=new SimpleDateFormat("HH:mm:ss").format(d);
        int hour= Integer.parseInt(ret.substring(0,2));
        int min= Integer.parseInt(ret.substring(3,5));
        int second= Integer.parseInt(ret.substring(6,ret.length()));
        return ret;
    }
}
