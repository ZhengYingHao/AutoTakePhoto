package com.example.zyh.autotakephoto.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.zyh.autotakephoto.activity.tools.ActivityCollector;
import com.ta.TAActivity;

public class MTAActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MTAA", "on destroy??");
        ActivityCollector.removeActivity(this);
    }
}
