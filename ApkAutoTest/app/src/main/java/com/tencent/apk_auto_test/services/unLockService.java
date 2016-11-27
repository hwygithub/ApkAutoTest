package com.tencent.apk_auto_test.services;

import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.util.Function;
import com.test.function.Operate;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

public class unLockService extends Service {
    private final static String TAG = "unLockService";
    private Operate mOperate;
    private Function mFunction;
    public static final int MSG_UNLOCK = 1;
    public static final int MSG_LOCK = 2;
    public static final int MAX_ERROR_TIMES = 10;
    WakeLock mWakelock;
    public static boolean mKeepScreenFlag = true;
    private PowerManager pm;
    private int mErrorTimes = 0;
    private Context mContext;

    @Override
    public void onCreate() {
        Log.e(TAG, "unLockService onCreate");
        super.onCreate();
        mContext = getApplicationContext();
        mOperate = new Operate(mContext);
        mFunction = new Function(mContext);
        // startForeBackgroud();
        pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "unLockService onDestroy");
        // mWakelock.release();
        mHandler.removeMessages(USER_ACTIVITY);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mWakelock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "SerialPortService");

        final Intent in = intent;
        if (in == null) {
            Log.e(TAG, "in  == null");
            // stopSelf();
            // return Service.START_NOT_STICKY;
        } else {
            new UnlockThread().start();
        }
        return flags;
    }

    private class UnlockThread extends Thread {
        @SuppressLint("NewApi")
        public void run() {
            Log.e(TAG, "UnlockThread start");
            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager.isKeyguardLocked()) {
                mFunction.unLockScreen();
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopSelf();
            } else {
                sendBroadcast(new Intent("com.apptest.cmcc_standard_autorun.action.runtest"));
            }
        }

        ;
    }

    ;

    private class LockThread extends Thread {
        public void run() {
            Log.e(TAG, "LockThread start");
            mFunction.LockScreen();
        }

        ;
    }

    ;

    @Override
    public IBinder onBind(Intent arg0) {
        Log.e(TAG, "unLockService onBind");
        return null;
    }

    protected static final int USER_ACTIVITY = 8;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case USER_ACTIVITY:
                    // linlong add for keep screen on in test
                    if (mKeepScreenFlag) {
                        pm.userActivity(SystemClock.uptimeMillis(), false);
                        mHandler.sendEmptyMessageDelayed(USER_ACTIVITY, 3000);
                    }
                    break;
            }

        }
    };

}
