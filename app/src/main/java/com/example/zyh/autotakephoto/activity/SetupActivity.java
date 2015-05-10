package com.example.zyh.autotakephoto.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.zyh.autotakephoto.R;
import com.example.zyh.autotakephoto.dao.DateDataDao;
import com.example.zyh.autotakephoto.dao.impl.DateDataDaoImpl;
import com.example.zyh.autotakephoto.model.UserInfo;

public class SetupActivity extends MTAActivity implements View.OnClickListener{

    public static void start(Context context) {
        context.startActivity(new Intent(context, SetupActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setup);

        init();
    }

    private void init() {
        TextView titleTv, backTv;
        Button logoutBtn, clearAllDataBtn;

        titleTv = (TextView)findViewById(R.id.action_bar_head_title);
        backTv = (TextView)findViewById(R.id.backTv);
        logoutBtn = (Button)findViewById(R.id.logoutBtn);
        clearAllDataBtn = (Button)findViewById(R.id.clearAllDataBtn);

        backTv.setVisibility(View.VISIBLE);
        findViewById(R.id.tv_quit).setVisibility(View.GONE);

        titleTv.setText(getString(R.string.actionbar_bottom_setup));

        backTv.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        clearAllDataBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backTv:
                finish();
                break;
            case R.id.logoutBtn:
                UserInfo.destroyUserInfo(SetupActivity.this);
                //LoginActivity 是 singleTask 模式，会直接销毁在它之上的活动
                startActivity(new Intent(SetupActivity.this, LoginActivity.class));
                break;
            case R.id.clearAllDataBtn:
                AlertDialog.Builder dialog = new AlertDialog.Builder(SetupActivity.this);
                dialog.setTitle("清除所有记录");
                dialog.setMessage("此操作不可逆");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DateDataDaoImpl impl = new DateDataDaoImpl(SetupActivity.this);
                        impl.deleteDateData(DateDataDao.DELETE_ALL, DateDataDao.DEFAULT_ID);
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                });
                dialog.show();
                break;
        }
    }
}
