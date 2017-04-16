package com.tencent.apk_auto_test.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tencent.apk_auto_test.MainActivity;
import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.util.Function;
import com.tencent.apk_auto_test.util.UINodeOperate;
import com.tencent.apk_auto_test.util.UIOperate;
import com.test.function.Assert;
import com.test.function.Operate;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by veehou on 2017/4/16.23:49
 */

public class TestTask extends Service {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private TelephonyManager teleMgr;
    private UINodeOperate mNodeOperate;
    private UIOperate mUIOperate;
    private Function mFunction;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (null == StaticData.chooseArray) {
            onDestroy();
            return;
        }

        runCaseList();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // StartForeground
        Notification notification = new Notification(R.drawable.icon,
                getText(R.string.app_name), System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                getText(R.string.txt_running), pendingIntent);
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    /**
     * 开始执行task前确保数据和对象的初始化
     *
     * @return
     */
    private boolean runBeforeTask() {
        mContext = getApplicationContext();
        mNodeOperate = new UINodeOperate(mContext);
        mUIOperate = new UIOperate(mContext);
        mFunction = new Function(mContext);

        int length = 0;
        for (int i = 0; i < StaticData.chooseArray.length; i++) {
            if (StaticData.chooseArray[i]) {
                length++;
            }
        }
        StaticData.mBar.setMax(length);

        mFunction.initInputMethod();
        mFunction.delFolder(new File("/sdcard/tencent-test"));

        return true;
    }

    /**
     * 执行用例集
     *
     * @param caseArrayList
     */
    public void runCaseList(ArrayList<TestCase> caseArrayList) {
        if (!runBeforeTask()) {
            Log.e(TAG, "before task run,init data or object error ");
        }

    }
}
