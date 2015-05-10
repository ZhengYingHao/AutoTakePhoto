package com.example.zyh.autotakephoto.activity.tools;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();
    public synchronized static void addActivity(Activity a) {
        Log.i("Collector", "add activity: " + a.toString());
        activities.add(a);
    }

    public static void removeActivity(Activity a) {
        Log.i("Collector", "remove activity: " + a.toString());
        activities.remove(a);
    }

    public synchronized static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
