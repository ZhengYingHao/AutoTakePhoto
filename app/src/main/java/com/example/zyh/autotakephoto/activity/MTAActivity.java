package com.example.zyh.autotakephoto.activity;

import android.os.Bundle;

import com.example.zyh.autotakephoto.activity.tools.ActivityCollector;
import com.ta.TAActivity;

public class MTAActivity extends TAActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
