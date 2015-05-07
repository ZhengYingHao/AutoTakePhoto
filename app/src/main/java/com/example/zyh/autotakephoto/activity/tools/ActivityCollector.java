package com.example.zyh.autotakephoto.activity.tools;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();
    public synchronized static void addActivity(Activity a) {
        activities.add(a);
    }
    public synchronized static void removeActivity(Activity a) {
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
