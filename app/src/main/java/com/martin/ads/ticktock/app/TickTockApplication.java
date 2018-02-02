package com.martin.ads.ticktock.app;

import android.app.Application;
import android.util.Log;

import com.martin.ads.ticktock.utils.TimeRetriever;

/**
 * Created by Ads on 2018/2/2.
 */

public class TickTockApplication extends Application{
    private static TickTockApplication tickTockApplication;

    public static TickTockApplication getInstance() {
        return tickTockApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TimeRetriever.getInstance();
        tickTockApplication = this;
    }

    @Override
    public void onTerminate() {
        TimeRetriever.requestStop();
        super.onTerminate();
    }
}
