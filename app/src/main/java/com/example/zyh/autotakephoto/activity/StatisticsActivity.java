package com.example.zyh.autotakephoto.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zyh.autotakephoto.R;
import com.example.zyh.autotakephoto.adapter.DateArrayAdapter;
import com.example.zyh.autotakephoto.dao.impl.DateDataDaoImpl;


public class StatisticsActivity extends MTAActivity implements View.OnClickListener{

    private ListView listView;
    private DateDataDaoImpl impl;
    private DownloadDateDataHandler handler;

    public static void start(Context context) {
        context.startActivity(new Intent(context, StatisticsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_statistics);

        init();

        handler = new DownloadDateDataHandler();

        impl = new DateDataDaoImpl(StatisticsActivity.this);
        impl.downloadDateData(new DateDataDaoImpl.OnFinishGetDateData() {
            @Override
            public void onFinish() {
                handler.sendEmptyMessage(DownloadDateDataHandler.DOWNLOAD_SUCCESS);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init() {
        TextView titleTv, backTv;

        titleTv = (TextView)findViewById(R.id.action_bar_head_title);
        backTv = (TextView)findViewById(R.id.backTv);
        listView = (ListView)findViewById(R.id.dateList);

        backTv.setVisibility(View.VISIBLE);
        findViewById(R.id.tv_quit).setVisibility(View.GONE);

        titleTv.setText(getString(R.string.actionbar_bottom_statistics));

        backTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backTv:
                finish();
                break;
        }
    }

    /**
     * 就系因为有里够也，当删除活动崩溃后还能返回到Main，而不是整个程序死亡！
     */
    class DownloadDateDataHandler extends Handler {
//        public static final int DOWNLOAD_FAIL = 0;
        public static final int DOWNLOAD_SUCCESS = 1;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DateArrayAdapter dateArrayAdapter =
                    new DateArrayAdapter(StatisticsActivity.this,
                            R.layout.adapter_date, impl.getDateData());
            listView.setAdapter(dateArrayAdapter);
        }
    }

}
