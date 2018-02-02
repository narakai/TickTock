package com.martin.ads.ticktock.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.adapter.ListAdapter;
import com.martin.ads.ticktock.utils.MiscUtils;

public class StartingActivity extends AppCompatActivity {

    private RecyclerView timerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_starting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MiscUtils.startActivity(StartingActivity.this, AddTimerActivity.class);
            }
        });

        initViews();
    }

    private void initViews() {
        timerList=findViewById(R.id.xrv_list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        timerList.setLayoutManager(mLayoutManager);
        // 需加，不然滑动不流畅
        timerList.setNestedScrollingEnabled(false);
        timerList.setHasFixedSize(false);
        final ListAdapter adapter = new ListAdapter(this);
        adapter.notifyDataSetChanged();
        timerList.setAdapter(adapter);
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
                return true;
            case R.id.action_help:
                return true;
            case R.id.full_screen:
                MiscUtils.startActivity(StartingActivity.this,DigitalClockActivity.class);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
