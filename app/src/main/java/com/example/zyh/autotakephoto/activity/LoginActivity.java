package com.example.zyh.autotakephoto.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zyh.autotakephoto.HttpUtil;
import com.example.zyh.autotakephoto.R;
import com.example.zyh.autotakephoto.model.UserInfo;
import com.example.zyh.autotakephoto.broadcast.receiver.NetStateReceiver;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.AsyncHttpResponseHandler;
import com.ta.util.http.RequestParams;

public class LoginActivity extends MTAActivity implements View.OnClickListener{

    public static final String TAG = "LoginActivity";

    private EditText userNameText, passwordText;
    private Button loginBtn, registerBtn;
    private TextView hintTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        init();

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        UserInfo.initUserInfo(this);

        if (hasLogin()) {
            MainActivity.start(LoginActivity.this);
        }
    }

    private void init() {
        userNameText = (EditText)findViewById(R.id.userName);
        passwordText = (EditText)findViewById(R.id.password);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        hintTv = (TextView)findViewById(R.id.hintTv);

        //初始化网络状态
        NetStateReceiver.initNetworkState(LoginActivity.this);
    }

    @Override
    public void onClick(View v) {
        if (NetStateReceiver.networkInfo == null || !NetStateReceiver.networkInfo.isAvailable()) {
            Toast.makeText(LoginActivity.this, "未连接网络", Toast.LENGTH_LONG).show();
            return ;
        }
        String name = userNameText.getText().toString();
        String pd = passwordText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pd)) {
            hintTv.setVisibility(View.VISIBLE);
            return ;
        }

        RequestParams params = new RequestParams();
        params.put(UserInfo.NAME, name);
        params.put(UserInfo.PASSWORD, pd);

        AsyncHttpClient client;
        switch (v.getId()) {
            case R.id.loginBtn:
                client = new AsyncHttpClient();
                client.post(getString(R.string.checkUserUrl), params,
                        new UserDataAsyncHttpResponseHandler(name));
                break;
            case R.id.registerBtn:
                client = new AsyncHttpClient();
                client.post(getString(R.string.registerUrl), params,
                        new UserDataAsyncHttpResponseHandler(name));
                break;
        }
    }

    /**
     * 用户信息不为空则已登录。
     */
    private boolean hasLogin() {
        return !UserInfo.getUserInfo().getName().equals("");
    }


    /**
     * 异步处理从网站返回的信息。
     */
    private class UserDataAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
        private String name;
        private ProgressBar bar;
        public UserDataAsyncHttpResponseHandler(String name) {
            super();
            this.name = name;
        }

        @Override
        public void onStart() {
            super.onStart();
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            params.height = 100;
            params.width = 100;
            bar = new ProgressBar(LoginActivity.this);
            bar.setBackgroundColor(Color.TRANSPARENT);
            getWindowManager().addView(bar, params);
        }

        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);
            Log.i(TAG, content);
            getWindowManager().removeView(bar);
            if (!HttpUtil.WRONG_USER_NAME_OR_PASSWORD_INFO.equals(content)) {
                UserInfo.storageInSharedPreference(LoginActivity.this, name, content);

                hintTv.setVisibility(View.INVISIBLE);
                Log.i(TAG, "ready to MainActivity.");
                MainActivity.start(LoginActivity.this);
                Log.i(TAG, "跳转到 MainActivity.");
            } else {
                hintTv.setVisibility(View.VISIBLE);
            }
        }
    }


}
