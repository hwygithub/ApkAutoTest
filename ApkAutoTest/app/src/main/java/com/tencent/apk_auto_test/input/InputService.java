package com.tencent.apk_auto_test.input;

import com.tencent.apk_auto_test.input.IInputMethodService;
import com.tencent.apk_auto_test.data.StaticData;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

public class InputService extends Service {
    public static final String KEY_MSG = "key_msg";
    public static final String KEY_MODE = "key_mode";
    public static IInputMethodService inputMethod;

    private static final String TAG = "InputService";
    private static final int MSG_RECONNECT = 11;
    private static final int MSG_INPUT = 12;
    private String mText;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            inputMethod = IInputMethodService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            inputMethod = null;
        }
    };

    private void initAidlServer(Context context) {
        context.bindService(new Intent().setComponent(new ComponentName(
                        "com.tencent.apk_auto_test", "com.tencent.apk_auto_test.input.IService")),
                serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mHandler.sendEmptyMessage(MSG_INPUT);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            inputMethod.setBackUserInputIfNeed();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    Handler mHandler = new Handler() {
        int reCount = 0;

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_INPUT:
                    if (inputMethod == null) {
                        Log.e(TAG, "inputMethod null");
                        mHandler.sendEmptyMessage(MSG_RECONNECT);
                    }
                    //inputString(mText);

                    break;
                case MSG_RECONNECT:
                    if (null == inputMethod) {
                        reCount++;
                        try {
                            //inputMethod.setUpInputMethodIfNeed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        initAidlServer(getApplicationContext());
                        Log.e(TAG, "reconnect time " + reCount);
                        if (reCount == 5)
                            break;
                        this.sendEmptyMessageDelayed(MSG_RECONNECT, 2000);
                    } else {
                        //inputString(mText);
                        Log.e(TAG, "reconnect success ");
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    public void inputString(final String msg) {
        Thread mThread1 = new Thread(new Runnable() {
            public void run() {
                try {
                    Log.e(TAG, "input " + msg);
                    setTextMethod(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mThread1.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean setTextMethod(String str) throws Exception {
        StaticData.isRunningInputService = true;
        boolean b1 = inputMethod.clearText();
        boolean b2 = inputMethod.setText(str, 0);
        return b1 && b2;
    }

}
