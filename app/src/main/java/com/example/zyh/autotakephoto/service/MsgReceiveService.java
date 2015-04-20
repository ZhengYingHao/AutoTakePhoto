package com.example.zyh.autotakephoto.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MsgReceiveService extends Service {
    public MsgReceiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
