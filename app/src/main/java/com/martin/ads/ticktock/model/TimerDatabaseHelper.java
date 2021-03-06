package com.martin.ads.ticktock.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.martin.ads.ticktock.utils.DateData;
import com.martin.ads.ticktock.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ads on 2017/3/16.
 */

public class TimerDatabaseHelper {
    private static final String TAG = "TimerDatabaseHelper";
    private Context context;
    private TimerDatabaseHelper(Context context) {
        this.context=context;
    }

    public void addTimer(TimerModel timerModel){
        try {
            JSONArray timerList=new JSONArray(getTimerListStr());
            JSONObject timerJSONObj=new JSONObject();
            timerJSONObj.put("uuid",timerModel.getUuid());
            timerJSONObj.put("timerTimeData",timerModel.getTimerTimeData().getTimeStr());
            timerJSONObj.put("ringtoneUri",timerModel.getRingtoneUri().toString());
            timerJSONObj.put("vibrate",timerModel.isVibrate());
            timerJSONObj.put("monitor",timerModel.isMonitor());
            Logger.d(TAG, "addTimer obj: "+timerJSONObj.toString());
            timerList.put(timerJSONObj);
            Logger.d(TAG, "after addTimer list: "+timerList.toString());
            saveTimerListStr(timerList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<TimerModel> getTimerList(){
        try {
            ArrayList<TimerModel> timerModels=new ArrayList<>();
            JSONArray timerList=new JSONArray(getTimerListStr());
            for(int i=0;i<timerList.length();i++){
                JSONObject curTimerJSONObj=timerList.getJSONObject(i);
                String uuid=curTimerJSONObj.getString("uuid");
                DateData timerTimeData=new DateData().setWithTimeStr(curTimerJSONObj.getString("timerTimeData"));
                Uri ringtoneUri=Uri.parse(curTimerJSONObj.getString("ringtoneUri"));
                boolean vibrate=Boolean.valueOf(curTimerJSONObj.getString("vibrate"));
                boolean monitor=Boolean.valueOf(curTimerJSONObj.getString("monitor"));
                timerModels.add(new TimerModel(uuid,timerTimeData,ringtoneUri,vibrate,monitor));
//                Log.d(TAG, "getTimerList: "+timerModels.get(timerModels.size()-1).toString());
            }
            return timerModels;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTimerListStr(){
        return FileUtils.loadStringFromFile(new File(context.getFilesDir().getAbsolutePath()
                ,"timer_list.json"));
    }

    public void saveTimerListStr(String timerListStr){
        FileUtils.saveStringToFile(new File(context.getFilesDir().getAbsolutePath()
                ,"timer_list.json"),timerListStr);
    }

    public static TimerDatabaseHelper with(Context context){
        return new TimerDatabaseHelper(context);
    }
}
