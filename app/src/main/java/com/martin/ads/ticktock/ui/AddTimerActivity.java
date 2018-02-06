package com.martin.ads.ticktock.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.kyleduo.switchbutton.SwitchButton;
import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.DateUtils;
import com.martin.ads.ticktock.utils.TimeRetriever;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddTimerActivity extends AppCompatActivity implements View.OnClickListener {

    TimePickerDialog timePickerDialogHourMinuteSecond;

    private View timeDurationPanel;
    private View ringtonePanel;
    private View vibratePanel;
    private TextView timeDurationText;
    private SwitchButton vibrateBtn;
    private ArrayList<View> allViews;

    private DateData durationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_timer);
        getSupportActionBar().setTitle(R.string.add_timer_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();
        initViews();
    }

    private void findViews(){
        timeDurationPanel=findViewById(R.id.time_duration_panel);
        ringtonePanel=findViewById(R.id.ringtone_panel);
        vibratePanel=findViewById(R.id.vibrate_panel);
        timeDurationText=findViewById(R.id.time_duration_text);
        vibrateBtn=findViewById(R.id.sb_vibrate);
    }

    private void initViews(){
        allViews=new ArrayList<>();
        allViews.add(timeDurationPanel);
        allViews.add(ringtonePanel);
        allViews.add(vibratePanel);
        for(View v:allViews) v.setOnClickListener(this);

        durationData=new DateData(0,5,0);
        onDurationDataChanged();

        timePickerDialogHourMinuteSecond = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        Calendar addC=getDateDataFromMilSec(millseconds).toCalendar(TimeRetriever.LOCALE);
                        durationData.setWithCalender(addC);
                        onDurationDataChanged();

                        //TODO:开启时
                        if(true){
                            DateData dateData=DateUtils.getDateData().copy();
                            Calendar curCal=dateData.toCalendar(TimeRetriever.LOCALE);
                            String curTime="当前时间"+DateUtils.calenderToFormatStr(curCal);
                            DateData newDateData=new DateData().setWithCalender(DateUtils.addHMSWithCalendar(curCal,addC));
                            String nxtTime="下次提醒"+newDateData.getTimeStr();
                            Toast.makeText(AddTimerActivity.this,curTime+" "+nxtTime ,Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setType(Type.HOURS_MINS_SECONDS)
                .setCancelStringId(getResources().getString(R.string.cancel))
                .setSureStringId(getResources().getString(R.string.confirm))
                .setTitleStringId("设置提醒间隔")
                .setHourText("小时")
                .setMinuteText("分钟")
                .setCyclic(true)
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorPrimary))
                .setWheelItemTextSize(12)
                .build();

        vibrateBtn.setChecked(true);
    }

    private void onDurationDataChanged() {
        timeDurationText.setText(durationData.getTimeStr());
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
                //TODO:save
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPickRingDialog() {
        String[] ringList = new String[]{"Morning","卡农","空灵","天籁森林","唯美","温暖早晨"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(ringList, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                SharedPreferences.Editor editor = getSharedPreferences("ringCode", MODE_PRIVATE).edit();
//                editor.putInt("key_ring", which + 1);
//                editor.apply();
//                playRing(which + 1);
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                SharedPreferences.Editor editor = getSharedPreferences("ringCode", MODE_PRIVATE).edit();
//                editor.putInt("key_ring", which + 1);
//                editor.apply();
//                if (player.isPlaying()){
//                    player.stop();
//                    player.release();
//                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (player != null && player.isPlaying()) {
//                    player.stop();
//                    player.release();
//                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.time_duration_panel:
                timePickerDialogHourMinuteSecond.show(getSupportFragmentManager(), "all");
                break;
            case R.id.ringtone_panel:
                showPickRingDialog();
                break;
            case R.id.vibrate_panel:
                vibrateBtn.setChecked(!vibrateBtn.isChecked());
                break;
        }
    }
}
