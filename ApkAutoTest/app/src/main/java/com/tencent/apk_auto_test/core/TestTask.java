package com.tencent.apk_auto_test.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.ext.UIActionBox;
import com.tencent.apk_auto_test.ext.UIImageActionBox;
import com.tencent.apk_auto_test.ext.UINodeActionBox;
import com.tencent.apk_auto_test.ui.HelpActivity;
import com.tencent.apk_auto_test.ui.MainActivity;
import com.tencent.apk_auto_test.util.Function;
import com.tencent.apk_auto_test.util.TimeUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by veehou on 2017/4/16.23:49
 */

public abstract class TestTask extends Service {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private TestTips mTips;
    private Method method;
    private TestTask mTestTask;

    public int testNumber = 0;

    public UIActionBox mBox;
    public UINodeActionBox mNodeBox;
    public UIImageActionBox mImageBox;
    public TestMonitor monitor;
    public Function mFunction;

    public String mRunFileName;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "TestTask is destroy");
        stopForeground(true);
        super.onDestroy();
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

        if (null == StaticData.chooseArray) {
            onDestroy();
        } else {
            runBeforeTask();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 开始执行task前确保数据和对象的初始化
     *
     * @return
     */
    private void runBeforeTask() {
        mContext = getApplicationContext();
        mNodeBox = new UINodeActionBox(mContext);
        mBox = new UIActionBox(mContext);
        mImageBox = new UIImageActionBox(mContext);
        mFunction = new Function(mContext);
        mTips = new TestTips(mContext);
        mTestTask = this;
        monitor = new TestMonitor(getTaskSimpleName(), mNodeBox, mImageBox);

        int length = 0;
        for (int i = 0; i < StaticData.chooseArray.length; i++) {
            if (StaticData.chooseArray[i]) {
                length++;
            }
        }

        StaticData.mBar.setMax(length);
        StaticData.mBar.setProgress(0);
        mTips.initTips(new StopClickListener());
        // start time
        StaticData.testStartTime = TimeUtil.getCurrentTime();

        mFunction.initInputMethod();
        mFunction.delFolder(new File("/sdcard/tencent-test"));

        int number = StaticData.runList.get(testNumber).runCaseNumber;
        int time = StaticData.runList.get(testNumber).runNumber;
        startRunCase(number, time, new MyTestTaskController());
    }

    private class StopClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mTips.removeTips();
            TestManager testManager = new TestManager(mContext);
            testManager.stopTest();
        }
    }

    /**
     * 用例执行入口
     * 优化代码结构：去除switch方式判断执行用例，采用反射方式动态获取类名后执行
     *
     * @param caseNumber test case name
     * @param caseTime   test case time
     */
    private void startRunCase(final int caseNumber, final int caseTime, final MyTestTaskController taskController) {
        //2、根据用例number执行拼接后指定的用例·
        String caseMethod = getTaskSimpleName() + "_" + caseNumber;
        TestResultPrinter mPrinter = TestResultPrinter.getInstance();
        mPrinter.printInfo("[startRunCase]:" + caseMethod);
        //getDeclaredMethod 能获取所有方法,getMethod 只能获取public 方法
        try {
            method = getClass().getDeclaredMethod(caseMethod, int.class);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "case number not found");
            e.printStackTrace();
        }

        Thread currentThread = new Thread(new Runnable() {

            @Override
            public void run() {
                //悬浮窗显示状态
                mTips.updateTips("case:" + (testNumber++ + 1) + " \t" + StaticData.chooseListText[caseNumber]);
                //初始化参数
                mRunFileName = getTaskSimpleName() + caseNumber + "-" + TimeUtil.getCurrentTimeSecond();
                //冷启动手Q
                if (caseNumber != 0)
                    _InitQQ();
                try {
                    StaticData.caseStartTime = TimeUtil.getCurrentTime();
                    method.invoke(mTestTask, caseTime);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                mBox.sendKey(KeyEvent.KEYCODE_HOME, 1000);
                taskController.onCaseRunFinished();
            }
        });
        currentThread.setName("apk_auto_test run thread: " + getTaskSimpleName() + "-" + caseNumber);
        currentThread.start();
    }

    private class MyTestTaskController implements TestTaskController {

        @Override
        public void onCaseRunFinished() {
            Log.v(TAG, testNumber + " case run finished");
            if (null == StaticData.runList) {
                return;
            }
            if (testNumber == StaticData.runList.size()) {
                if (StaticData.runState.equals("circle")) {
                    testNumber = 0;
                    int caseNumber = StaticData.caseNumber = StaticData.runList.get(testNumber).runCaseNumber;
                    int caseTime = StaticData.runList.get(testNumber).runNumber;
                    startRunCase(caseNumber, caseTime, this);
                } else {
                    StaticData.testFinishEvent = getResources().getString(
                            R.string.txt_finish_case);
                    onTaskFinished();
                }
            } else {
                int caseNumber = StaticData.caseNumber = StaticData.runList.get(testNumber).runCaseNumber;
                int caseTime = StaticData.runList.get(testNumber).runNumber;
                startRunCase(caseNumber, caseTime, this);
            }
        }

        @Override
        public void onTaskFinished() {
            String testTime = TimeUtil.getPassTimeString(StaticData.testStartTime, TimeUtil.getCurrentTime());
            // After test,send sms  ---remove in 2016/12/21
            stopSelf();
            // return to the main activity
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(mContext, HelpActivity.class);
            startActivity(intent);

            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public abstract String getTaskSimpleName();

    /****************
     * 代码复用
     ******************/
    //［_InitQQ］杀手Q进程还原状态、启动手Q
    public void _InitQQ() {
        //杀手Q进程还原状态
        mFunction.killAppByPackageName("com.tencent.mobileqq");
        mBox.sleep(2000);
        //热启动手Q
        try {
            mBox.openApp("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity");
        } catch (Exception e) {
            Log.e(TAG, "start test app activity error!");
            return;
        }
        mBox.sleep(12000);
    }

    //［_OpenActionTab］进入群AIO打开动作面板
    public void _OpenActionTab() {
        //点击搜索栏
        mNodeBox.clickOnText("搜索", 1000);
        //输入群，点击进入
        mFunction.inputText("546479585", 2000);
        //点击测试群
        mNodeBox.clickOnText("测试号集中营", 1000);
        //点击AIO输入输入框上方的中间部分区域
        mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);
    }

    //［_OpenC2CActionTab］进入测试号AIO打开面板
    public void _OpenC2CActionTab() {
        //点击搜索栏
        mNodeBox.clickOnText("搜索", 1000);
        //输入群，点击进入
        mFunction.inputText("1220232584", 2000);
        //点击测试号
        mNodeBox.clickOnTextContain("我的好友", 2000);
        //点击AIO输入输入框上方的中间部分区域
        mNodeBox.clickOnResourceIdOffset("inputBar", 1000, 0, 1, -100);
    }

    //[_OpenChangeClothesWeb] 通过抽屉页进入换装页
    public void _OpenChangeClothesWeb() {
        //进入抽屉页
        mNodeBox.clickOnResourceId("conversation_head", 3000, 0);
        //点击抽屉页厘米秀小人
        mNodeBox.clickOnResourceIdOffset("nightmode", 4000, 0, 0, 200);
        //点击切换到换装页
        mImageBox.clickOnImage("tab_web_change", 4000);
    }

    //[_AIOOpenChangeClothesWeb] 通过AIO进入换装页
    public void _AIOOpenChangeClothesWeb() {
        //［_OpenC2CActionTab］进入测试号AIO打开面板
        _OpenC2CActionTab();
        //点击换装入口
        mNodeBox.clickOnResourceId("btn_more_apollo", 8000, 0);
    }
}
