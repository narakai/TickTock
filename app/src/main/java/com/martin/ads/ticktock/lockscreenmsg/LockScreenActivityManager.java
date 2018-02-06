package com.martin.ads.ticktock.lockscreenmsg;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/4/13.
 */
public class LockScreenActivityManager {

        public static List<Activity> activities = new ArrayList<Activity>();
        public static void addActivity(Activity activity) {
            activities.add(activity);
        }

        public static void removeActivity(Activity activity) {
            activities.remove(activity);
        }

        public static void finishAll() {
            for (Activity activity : activities) {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
            activities.clear();
        }

}
