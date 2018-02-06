package com.martin.ads.ticktock.ui;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.adapter.ListAdapter;
import com.martin.ads.ticktock.model.FileUtils;
import com.martin.ads.ticktock.model.NotifyTaskModel;
import com.martin.ads.ticktock.model.TimerDatabaseHelper;
import com.martin.ads.ticktock.model.TimerModel;
import com.martin.ads.ticktock.service.TickingService;
import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.DateUtils;
import com.martin.ads.ticktock.utils.DialogUtil;
import com.martin.ads.ticktock.utils.Logger;
import com.martin.ads.ticktock.utils.MiscUtils;
import com.martin.ads.ticktock.utils.TimeRetriever;
import com.martin.ads.ui.toptoast.TopToast;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Calendar;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;

public class StartingActivity extends AppCompatActivity {

    private RecyclerView timerList;
    private LinearLayout emptyView;
    private TickingService.TickingBinder tickingBinder;

    private ArrayList<TimerModel> timerModels;
    private Intent startIntent;
    private ListAdapter adapter;
    private boolean createFinish=false;

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            tickingBinder=(TickingService.TickingBinder)service;
            if(timerModels!=null){
                for(TimerModel timerModel:timerModels){
                    if(tickingBinder.isTimerOn(timerModel)){
                        timerModel.setOn(true);
                    }
                }
                adapter.notifyDataSetChanged();
                createFinish=true;
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_starting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startIntent=new Intent(this,TickingService.class);
        bindService(startIntent,connection,BIND_AUTO_CREATE);
        startService(startIntent);

        FloatingActionButton fab =findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:on data changed
                MiscUtils.startActivity(StartingActivity.this, AddTimerActivity.class);
                finish();
            }
        });

        timerModels= TimerDatabaseHelper.with(this).getTimerList();
        initViews();
        checkTimerSize();
    }

    private void checkTimerSize() {
        if(timerModels.size()==0){
            emptyView.setVisibility(View.VISIBLE);
            timerList.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.GONE);
            timerList.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        timerList=findViewById(R.id.timer_list);
        emptyView=findViewById(R.id.empty_list_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        timerList.setLayoutManager(mLayoutManager);
        timerList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        // 需加，不然滑动不流畅
        timerList.setNestedScrollingEnabled(false);
        timerList.setHasFixedSize(false);
        adapter = new ListAdapter(this,timerModels);
        adapter.notifyDataSetChanged();
        timerList.setAdapter(adapter);
        adapter.setTimerStateListener(new ListAdapter.TimerStateListener() {
            @Override
            public void onTimerStateChanged(TimerModel timerModel, boolean isChecked) {
                if(!createFinish) return;
                if(isChecked){
//                    if(false){
//                        Calendar addC=timerModel.getTimerTimeData().copy().toCalendar(TimeRetriever.LOCALE);
//                        DateData dateData= DateUtils.getDateData().copy();
//                        Calendar curCal=dateData.toCalendar(TimeRetriever.LOCALE);
//                        String curTime="当前时间"+DateUtils.calenderToFormatStr(curCal);
//                        DateData newDateData=new DateData().setWithCalender(DateUtils.addHMSWithCalendar(curCal,addC));
//                        String nxtTime="下次提醒"+newDateData.getTimeStr();
//                    }
                    NotifyTaskModel notifyTaskModel=new NotifyTaskModel();
                    notifyTaskModel.setTimerModel(timerModel);
                    notifyTaskModel.setLastNotifiedDate(DateUtils.getDateData().copy());
                    notifyTaskModel.setNextNotifyDate();
                    boolean ret=tickingBinder.addTask(notifyTaskModel);
                    if(ret)
                        TopToast.with(StartingActivity.this)
                            .setTitle(timerModel.getTimerTimeData().getSimpleTimeStr()+"后提醒")
                            //.setMessage(timerModel.getTimerTimeData().getSimpleTimeStr()+"后提醒")
                            .sneakSuccess();
                }else{
                    tickingBinder.removeTask(timerModel);
                }
                Logger.d("lalala", "onTimerStateChanged: "+timerModel.toString()+" "+isChecked);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_starting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                TastyToast.makeText(getApplicationContext(), "这个功能还不存在", TastyToast.LENGTH_SHORT,
                        TastyToast.CONFUSING);
                return true;
            case R.id.action_help:
                showDonateDialog();
                return true;
            case R.id.clear_list:
                TimerDatabaseHelper.with(this).saveTimerListStr(FileUtils.EMPTY_FILE_STR);
                //reStartActivity();
                timerModels= TimerDatabaseHelper.with(this).getTimerList();
                adapter.notifyDataSetChanged();
                tickingBinder.removeAllTask();
                checkTimerSize();
                return true;
            case R.id.exit_app:
                tickingBinder.requestStop();
                finish();
                return true;
            case R.id.full_screen:
                MiscUtils.startActivity(StartingActivity.this,DigitalClockActivity.class);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reStartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void showDonateDialog() {
        String htmlFileName =  "help_ch.html";
        DialogUtil.showCustomDialogWithTwoAction(this, getSupportFragmentManager(), getString(R.string.help),htmlFileName, "help",
                getString(R.string.close),null,
                getString(R.string.offer_donation),new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(AlipayZeroSdk.hasInstalledAlipayClient(StartingActivity.this)){
                            String auth="FKX08947EWPXCXJDX6YQ25";
                                         //aex07094cljuqa36ku7ml36
                            AlipayZeroSdk.startAlipayClient(StartingActivity.this, auth);
                        }else{
                            //Toast.makeText(StartingActivity.this,getString(R.string.support_exception_for_alipay), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
