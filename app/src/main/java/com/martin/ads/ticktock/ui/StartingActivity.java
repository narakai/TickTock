package com.martin.ads.ticktock.ui;

import android.content.ComponentName;
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

import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.adapter.ListAdapter;
import com.martin.ads.ticktock.model.FileUtils;
import com.martin.ads.ticktock.model.TimerDatabaseHelper;
import com.martin.ads.ticktock.model.TimerModel;
import com.martin.ads.ticktock.service.TickingService;
import com.martin.ads.ticktock.utils.MiscUtils;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

public class StartingActivity extends AppCompatActivity {

    private RecyclerView timerList;
    private LinearLayout emptyView;
    private TickingService.TickingBinder tickingBinder;

    private ArrayList<TimerModel> timerModels;
    private Intent startIntent;

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            tickingBinder=(TickingService.TickingBinder)service;
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
        ListAdapter adapter = new ListAdapter(this,timerModels);
        adapter.notifyDataSetChanged();
        timerList.setAdapter(adapter);
        adapter.setTimerStateListener(new ListAdapter.TimerStateListener() {
            @Override
            public void onTimerStateChanged(TimerModel timerModel, boolean isChecked) {
                Log.d("lalala", "onTimerStateChanged: "+timerModel.toString()+" "+isChecked);
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

                return true;
            case R.id.clear_list:
                TimerDatabaseHelper.with(this).saveTimerListStr(FileUtils.EMPTY_FILE_STR);
                reStartActivity();
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
}
