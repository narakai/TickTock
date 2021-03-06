package com.martin.ads.ticktock.ui;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.martin.ads.ticktock.model.TimerDatabaseHelper;
import com.martin.ads.ticktock.model.TimerModel;
import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.DateUtils;
import com.martin.ads.ticktock.utils.MiscUtils;
import com.martin.ads.ticktock.utils.TimeRetriever;
import com.martin.ads.ticktock.utils.ringtone.RingtoneHardCode;
import com.martin.ads.ticktock.utils.ringtone.RingtonePickerDialog;
import com.martin.ads.ticktock.utils.ringtone.RingtonePickerListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddTimerActivity extends AppCompatActivity implements View.OnClickListener {

    TimePickerDialog timePickerDialogHourMinuteSecond;

    private View timeDurationPanel;
    private View ringtonePanel;
    private View vibratePanel;
    private View monitorPanel;
    private TextView timeDurationText;
    private TextView ringText;
    private SwitchButton vibrateBtn;
    private SwitchButton monitorBtn;
    private ArrayList<View> allViews;

    private DateData durationData;
    private Uri currentRingtoneUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_timer);
        getSupportActionBar().setTitle(R.string.add_timer_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();
        initViews();
        initData();

    }

    private void initData() {
        durationData=new DateData(0,5,0);
        onDurationDataChanged();
        currentRingtoneUri=RingtoneHardCode.URI_SILENT;
    }

    private void findViews(){
        timeDurationPanel=findViewById(R.id.time_duration_panel);
        ringtonePanel=findViewById(R.id.ringtone_panel);
        vibratePanel=findViewById(R.id.vibrate_panel);
        monitorPanel=findViewById(R.id.monitor_panel);
        timeDurationText=findViewById(R.id.time_duration_text);
        vibrateBtn=findViewById(R.id.sb_vibrate);
        monitorBtn=findViewById(R.id.sb_monitor);
        ringText=findViewById(R.id.ring_text);
    }

    private void initViews(){
        allViews=new ArrayList<>();
        allViews.add(timeDurationPanel);
        allViews.add(ringtonePanel);
        allViews.add(vibratePanel);
        allViews.add(monitorPanel);
        for(View v:allViews) v.setOnClickListener(this);

        timePickerDialogHourMinuteSecond = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        Calendar addC=getDateDataFromMilSec(millseconds).toCalendar(TimeRetriever.LOCALE);
                        if(addC.get(Calendar.SECOND)==0 &&
                                addC.get(Calendar.HOUR_OF_DAY)==0 &&
                                addC.get(Calendar.MINUTE)==0){
                            TastyToast.makeText(getApplicationContext(),getResources().getString(R.string.time_duration_can_not_be_zero) , TastyToast.LENGTH_LONG,
                                    TastyToast.INFO);
                            return;
                        }
                        durationData.setWithCalender(addC);
                        onDurationDataChanged();

                        //TODO:?????????
                        if(false){
                            DateData dateData=DateUtils.getDateData().copy();
                            Calendar curCal=dateData.toCalendar(TimeRetriever.LOCALE);
                            String curTime="????????????"+DateUtils.calenderToFormatStr(curCal);
                            DateData newDateData=new DateData().setWithCalender(DateUtils.addHMSWithCalendar(curCal,addC));
                            String nxtTime="????????????"+newDateData.getTimeStr();
                            Toast.makeText(AddTimerActivity.this,curTime+" "+nxtTime ,Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setType(Type.HOURS_MINS_SECONDS)
                .setCancelStringId(getResources().getString(R.string.cancel))
                .setSureStringId(getResources().getString(R.string.confirm))
                .setTitleStringId("??????????????????")
                .setHourText("??????")
                .setMinuteText("??????")
                .setCyclic(true)
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorPrimary))
                .setWheelItemTextSize(12)
                .build();

        vibrateBtn.setChecked(true);
        monitorBtn.setChecked(true);
    }

    private void onDurationDataChanged() {
        timeDurationText.setText(durationData.getSimpleTimeStr());
    }

    public DateData getDateDataFromMilSec(long millSecond) {
        Date d = new Date(millSecond);
        String ret=new SimpleDateFormat("HH:mm:ss").format(d);
        return new DateData().setWithTimeStr(ret);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_timer, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                MiscUtils.startActivity(this, StartingActivity.class);
                return true;
            case R.id.save_timer:
                TimerDatabaseHelper.with(this).addTimer(
                        new TimerModel(
                                UUID.randomUUID().toString().replaceAll("-", ""),
                                durationData,currentRingtoneUri,vibrateBtn.isChecked(),monitorBtn.isChecked())
                );
                MiscUtils.startActivity(this, StartingActivity.class);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPickRingDialog(){
        RingtonePickerDialog.Builder ringtonePickerBuilder = new RingtonePickerDialog.Builder(getApplicationContext(), getSupportFragmentManager());
        ringtonePickerBuilder.setTitle(getResources().getString(R.string.select_ringtone));
        //ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_MUSIC);
        //ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        //ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NONE);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM);
        ringtonePickerBuilder.setPositiveButtonText(getResources().getString(R.string.confirm));
        ringtonePickerBuilder.setCancelButtonText(getResources().getString(R.string.cancel));
        ringtonePickerBuilder.setCurrentRingtoneUri(currentRingtoneUri);
        ringtonePickerBuilder.setPlaySampleWhileSelection(true);

        ringtonePickerBuilder.setListener(new RingtonePickerListener() {
            @Override
            public void OnRingtoneSelected(String ringtoneName, Uri ringtoneUri) {
                currentRingtoneUri=ringtoneUri;
                ringText.setText(ringtoneName);
            }
        });

        ringtonePickerBuilder.show();
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
            case R.id.monitor_panel:
                monitorBtn.setChecked(!monitorBtn.isChecked());
                break;
        }
    }
}
