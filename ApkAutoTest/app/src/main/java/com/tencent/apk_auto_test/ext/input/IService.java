package com.tencent.apk_auto_test.ext.input;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class IService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return new InputMethod(this);
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
