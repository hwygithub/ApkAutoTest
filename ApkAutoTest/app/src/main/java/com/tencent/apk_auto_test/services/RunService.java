package com.tencent.apk_auto_test.services;

import java.io.File;
import java.util.Date;

import com.tencent.apk_auto_test.HelpActivity;
import com.tencent.apk_auto_test.MainActivity;
import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.util.Function;
import com.tencent.apk_auto_test.util.Time;
import com.tencent.apk_auto_test.util.UINodeOperate;
import com.tencent.apk_auto_test.util.UIOperate;
import com.tencent.apk_auto_test.receiver.BatteryReceiver;
import com.tencent.apk_auto_test.receiver.ScreenActionReceiver;
import com.tencent.apk_auto_test.receiver.ShutDownReceiver;
import com.test.function.Assert;
import com.test.function.Operate;
import com.test.function.Show;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class RunService extends Service {
    // static
    protected static final int END_TEST = 0;
    protected static final int START_TEST = 1;
    protected static final int RUN_TEST = 2;
    protected static final int START_SCHEME = 3;
    protected static final int END_SCHEME = 4;
    protected static final int RUN_SCHEME = 5;
    protected static final int PRINT_LOG = 70;

    protected static final String EXCEL_DIRECTORY = "MpBatteryAutoTest";

    private String TAG = "RunService";

    // class
    private Operate mOperate;
    private Assert mAssert;
    private Context mContext;
    private Show mShow;
    private Function mFunction;
    private BatteryReceiver batteryReceiver;
    private ShutDownReceiver mShutDownReceiver;
    private RunTestBroadcastReceiver mReceiver;
    private ScreenActionReceiver mActionReceiver;
    private EventHandler mEventHandler;
    private TestHandler mHandler;
    private TelephonyManager teleMgr;
    private UINodeOperate mNodeOperate;
    private UIOperate mUIOperate;

    // data
    public static int testNumber;
    private int schemeNumber;

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

        setClass();
        setData();
        setReceivers();

    }

    private void setClass() {
        mHandler = new TestHandler();
        mContext = getApplicationContext();
        mOperate = new Operate(mContext);
        mShow = new Show(mContext);
        mAssert = new Assert(mContext);
        mFunction = new Function(mContext, mHandler);
        mEventHandler = new EventHandler();
        mNodeOperate = new UINodeOperate(mContext);
        mUIOperate = new UIOperate(mContext);
    }

    @Override
    public void onStart(Intent intent, int arg1) {
        super.onStart(intent, arg1);
        // StartForeground
        Notification notification = new Notification(R.drawable.icon,
                getText(R.string.app_name), System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                getText(R.string.txt_running), pendingIntent);
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    private void setData() {
        int length = 0;
        for (int i = 0; i < StaticData.chooseArray.length; i++) {
            if (StaticData.chooseArray[i]) {
                length++;
            }
        }
        StaticData.mBar.setMax(length);

        mFunction.initInputMethod();
        mFunction.delFolder(new File("/sdcard/tencent-test"));
    }

    private void setReceivers() {
        batteryReceiver = new BatteryReceiver(mContext, mHandler);
        batteryReceiver.setName("Log_");
        mContext.registerReceiver(batteryReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        mShutDownReceiver = new ShutDownReceiver();
        mContext.registerReceiver(mShutDownReceiver, new IntentFilter(
                Intent.ACTION_SHUTDOWN));
    }

    class StopClickListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            mShow.removeView();
            stopSelf();
            mFunction.stopPackage(mContext.getPackageName());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == mHandler) {
            return START_STICKY_COMPATIBILITY;
        }
        mHandler.sendEmptyMessageDelayed(START_SCHEME, 2000);
        return super.onStartCommand(intent, flags, startId);
    }

    class TestHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // Scheme before test
                case START_SCHEME:
                    schemeNumber = 0;
                    // public scheme
                    // close auto round
                    mOperate.setAutoRotation(0);
                    // set the screen off time:30 minutes
                    mOperate.setScreenOffTime(StaticData.testScreenOffTime);
                    // Set pure background
                    mFunction.setPureBackgroundEnable(false);
                    // Set PermissionIntercept
                    // mFunction.setPermissionInterceptEnable(false);
                    // Set the float window
                    mFunction.setFloatWindowState(true, getPackageName());
                    // Set the change state confirm mode
                    mFunction.setChangeGprsStateConfirmMode(0);
                    mFunction.setChangeWifiStateConfirmMode(0);
                    mFunction.setSwitchToGprsStateConfirmMode(0);
                    // Set the traffic alert
                    mFunction.setTrafficAlert(false, false);
                    // Set the update window
                    mShow.addView(new StopClickListener());

                    mHandler.sendEmptyMessage(RUN_SCHEME);
                    break;
                case RUN_SCHEME:
                    if (schemeNumber == StaticData.chooseArray.length) {
                        mHandler.sendEmptyMessage(END_SCHEME);
                    } else {
                        if (StaticData.chooseArray[schemeNumber])
                            startScheme(schemeNumber);
                        else {
                            schemeNumber++;
                            mHandler.sendEmptyMessage(RUN_SCHEME);
                        }
                    }
                    break;
                case END_SCHEME:
                    mHandler.sendEmptyMessageDelayed(START_TEST, 2000);
                    break;
                case START_TEST:
                    StaticData.mBar.setProgress(0);
                    // init
                    testNumber = 0;
                    // set the Dirctory
                    // StaticData.mDealer.setDirctory(EXCEL_DIRECTORY);
                    // write start log
                    teleMgr = (TelephonyManager) mContext
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    batteryReceiver.printStartLevel();
                    // start time
                    StaticData.testStartTime = Time.getCurrentTime();
                    // start run
                    mHandler.sendEmptyMessage(RUN_TEST);
                    break;
                case RUN_TEST:
                    if (null == StaticData.runList) {
                        return;
                    }
                    if (testNumber == StaticData.runList.size()) {
                        if (StaticData.runState.equals("circle")) {
                            testNumber = 0;
                            mHandler.sendEmptyMessageDelayed(RUN_TEST, 3000);
                        } else {
                            StaticData.testFinishEvent = getResources().getString(
                                    R.string.txt_finish_case);
                            mHandler.sendEmptyMessageDelayed(END_TEST, 3000);
                        }
                    } else {
                        StaticData.runList.get(testNumber);
                        StaticData.caseNumber = StaticData.runList.get(testNumber).runCaseNumber;
                        int caseTime = StaticData.runList.get(testNumber).runNumber;
                        // start
                        StaticData.caseStartTime = Time.getCurrentTime();
                        startRunCase(StaticData.caseNumber, caseTime);
                    }
                    break;
                case END_TEST:
                    String testTime = Time.getPassTimeString(
                            StaticData.testStartTime, Time.getCurrentTime());
                    // write log
                    BatteryReceiver.writeLog(Time.getCurrentTimeSecond(), 4, 1);
                    BatteryReceiver.writeLog(testTime, 5, 1);
                    BatteryReceiver.writeLog(StaticData.testFinishEvent, 6, 1);
                    // After test,send sms
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("AutoRun Test is Finished!");
                    buffer.append("\nimei:" + teleMgr.getDeviceId());
                    mFunction.sendSmsAPI(StaticData.testEndSendNumber,
                            buffer.toString());
                    // end the test
                    sendNotification();
                    stopSelf();
                    // return to the main activity
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, HelpActivity.class);
                    startActivity(intent);

                    android.os.Process.killProcess(android.os.Process.myPid());

                    break;
            }
        }
    }

    ;

    class EventHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PRINT_LOG:
                    batteryReceiver.printLog(testNumber);
                    break;
            }
        }

    }

    ;

    /**
     * 运行用例前可以进行环境的预置，如无则自动跳过
     *
     * @param schemeNumber
     */
    private void startScheme(int schemeNumber) {
        switch (schemeNumber) {
            default:
                // default run
                StaticData.mBar.incrementProgressBy(1);
                mHandler.sendEmptyMessage(RUN_SCHEME);
                break;
        }
        this.schemeNumber++;

    }

    /**
     * @param caseNumber test case name
     * @param caseTime   test case time
     */
    private void startRunCase(int caseNumber, int caseTime) {
        switch (caseNumber) {
            case 1:
                CSMT_1(caseTime);
                break;
            case 2:
                CSMT_2(caseTime);
                break;
            case 3:
                CSMT_3(caseTime);
                break;
            case 4:
                CSMT_4(caseTime);
                break;
            case 5:
                CSMT_5(caseTime);
                break;
            case 6:
                CSMT_6(caseTime);
                break;
            default:
                return;
        }
        testNumber++;
    }

    // 群单人动作交叉发送
    private void CSMT_1(final int caseTime) {
        mShow.updateState("case: " + (testNumber + 1) + "\n" + "群单人动作交叉发送");
        Thread currentThread = new Thread(new Runnable() {

            @Override
            public void run() {

                //初始化参数
                String fileName = "CSMT-1-" + Time.getCurrentTimeSecond();
                //杀手Q进程还原状态
                mFunction.killAppByPackageName("com.tencent.mobileqq");
                mOperate.sleep(3000);
                //热启动手Q
                try {
                    mOperate.startActivity("com.tencent.mobileqq",
                            "com.tencent.mobileqq.activity.SplashActivity");
                } catch (Exception e) {
                    Log.e(TAG, "start test app activity error!");
                    return;
                }
                mOperate.sleep(5000);
                //点击搜索栏
                mNodeOperate.clickOnText("搜索", 3000);
                //输入群，点击进入
                mFunction.inputText("546479585", 2000);
                //点击搜索栏
                mNodeOperate.clickOnText("测试号集中营", 3000);
                //点击AIO输入输入框上方的中间部分区域
                mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                        -100);
                //点击发送并播放厘米秀动作，交叉互相打断播放
                int count = 0;
                for (int i = 0; i < caseTime; i++) {
                    //循环交互点击
                    //如果没有点击成功判断面板是否隐藏了
                    //通过厘米秀面板的特别动作id点击
                    if (!mNodeOperate.clickOnResouceId("avatar_item_imageview", 2000, 0)) {
                        mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                                -100);
                    }
                    mFunction.inputText("群单人交叉发送:" + count, 2000);
                    mNodeOperate.clickOnResouceId("avatar_item_imageview", 2000, 1);
                    //每轮查询可用内存和进程内存情况,并保存到终端存储
                    mFunction.saveMem("com.tencent.mobileqq", fileName);
                }
                mOperate.sleep(2000);

                mUIOperate.sendKey(KeyEvent.KEYCODE_HOME, 2000);

                // 测试用例结束，打印日志
                mEventHandler.sendEmptyMessage(PRINT_LOG);
            }
        });
        currentThread.start();
    }

    // 群双人动作交叉发送
    private void CSMT_2(final int caseTime) {
        mShow.updateState("case: " + (testNumber + 1) + "\n" + "群双人动作交叉发送");
        Thread currentThread = new Thread(new Runnable() {

            @Override
            public void run() {

                //初始化参数
                String fileName = "CSMT-2-" + Time.getCurrentTimeSecond();
                //杀手Q进程还原状态
                mFunction.killAppByPackageName("com.tencent.mobileqq");
                mOperate.sleep(3000);
                //热启动手Q
                try {
                    mOperate.startActivity("com.tencent.mobileqq",
                            "com.tencent.mobileqq.activity.SplashActivity");
                } catch (Exception e) {
                    Log.e(TAG, "start test app activity error!");
                    return;
                }
                mOperate.sleep(5000);
                //点击搜索栏
                mNodeOperate.clickOnText("搜索", 3000);
                //输入群，点击进入
                mFunction.inputText("546479585", 2000);
                //点击搜索栏
                mNodeOperate.clickOnText("测试号集中营", 3000);
                //点击AIO输入输入框上方的中间部分区域
                mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                        -100);
                //点击tab切换到双人动作
                mNodeOperate.clickOnResouceId("tabView", 2000, 1);
                //点击发送并播放厘米秀动作，交叉互相打断播放
                int count = 0;
                for (int i = 0; i < caseTime; i++) {
                    //循环交互点击
                    //如果没有点击成功判断面板是否隐藏了
                    //通过厘米秀面板的特别动作id点击
                    if (!mNodeOperate.clickOnResouceId("avatar_item_imageview", 2000, 0)) {
                        mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                                -100);
                    }
                    mNodeOperate.clickOnTextContain("大群主", 1000);
                    mFunction.inputText("群双人交叉发送:" + count, 2000);
                    mNodeOperate.clickOnResouceId("avatar_item_imageview", 2000, 1);
                    mNodeOperate.clickOnTextContain("大群主", 2000);
                    //每轮查询可用内存和进程内存情况,并保存到终端存储
                    mFunction.saveMem("com.tencent.mobileqq", fileName);
                }
                mOperate.sleep(2000);
                mUIOperate.sendKey(KeyEvent.KEYCODE_HOME, 2000);
                // 测试用例结束，打印日志
                mEventHandler.sendEmptyMessage(PRINT_LOG);
            }
        });
        currentThread.start();
    }

    // 弹幕动作发送
    private void CSMT_3(final int caseTime) {
        mShow.updateState("case: " + (testNumber + 1) + "\n" + "弹幕动作发送");
        Thread currentThread = new Thread(new Runnable() {

            @Override
            public void run() {

                //初始化参数
                String fileName = "CSMT-3-" + Time.getCurrentTimeSecond();
                //杀手Q进程还原状态
                mFunction.killAppByPackageName("com.tencent.mobileqq");
                mOperate.sleep(3000);
                //热启动手Q
                try {
                    mOperate.startActivity("com.tencent.mobileqq",
                            "com.tencent.mobileqq.activity.SplashActivity");
                } catch (Exception e) {
                    Log.e(TAG, "start test app activity error!");
                    return;
                }
                mOperate.sleep(5000);
                //点击搜索栏
                mNodeOperate.clickOnText("搜索", 3000);
                //输入群，点击进入
                mFunction.inputText("546479585", 2000);
                //点击搜索栏
                mNodeOperate.clickOnText("测试号集中营", 3000);
                //点击AIO输入输入框上方的中间部分区域
                mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                        -100);
                //点击tab切换到弹幕动作
                mNodeOperate.clickOnResouceId("tabView", 2000, 2);
                int count = 0;
                for (int i = 0; i < caseTime; i++) {
                    //循环交互点击
                    //如果没有点击成功判断面板是否隐藏了
                    //通过厘米秀面板的特别动作id点击
                    if (!mNodeOperate.clickOnResouceId("avatar_item_imageview", 1000, 0)) {
                        mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                                -100);
                    }
                    mNodeOperate.clickOnTextContain("大群主", 1000);
                    mFunction.inputText("弹幕发送:" + count, 2000);

                    //每轮查询可用内存和进程内存情况,并保存到终端存储
                    mFunction.saveMem("com.tencent.mobileqq", fileName);
                }
                mOperate.sleep(2000);
                mUIOperate.sendKey(KeyEvent.KEYCODE_HOME, 2000);

                // 测试用例结束，打印日志
                mEventHandler.sendEmptyMessage(PRINT_LOG);
            }

        });
        currentThread.start();
    }

    // 群和C2C切换发送
    private void CSMT_4(final int caseTime) {
        mShow.updateState("case: " + (testNumber + 1) + "\n" + "群和C2C切换发送");
        Thread currentThread = new Thread(new Runnable() {

            @Override
            public void run() {

                //初始化参数
                String fileName = "CSMT-4-" + Time.getCurrentTimeSecond();
                //杀手Q进程还原状态
                mFunction.killAppByPackageName("com.tencent.mobileqq");
                mOperate.sleep(3000);
                //热启动手Q
                try {
                    mOperate.startActivity("com.tencent.mobileqq",
                            "com.tencent.mobileqq.activity.SplashActivity");
                } catch (Exception e) {
                    Log.e(TAG, "start test app activity error!");
                    return;
                }
                mOperate.sleep(5000);

                int count = 0;
                for (int i = 0; i < caseTime; i++) {
                    //点击搜索栏
                    mNodeOperate.clickOnText("搜索", 3000);
                    //输入群，点击进入
                    mFunction.inputText("546479585", 2000);
                    //点击搜索栏
                    mNodeOperate.clickOnText("测试号集中营", 3000);
                    //点击AIO输入输入框上方的中间部分区域
                    mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                            -100);
                    //如果没有点击成功判断面板是否隐藏了
                    //通过厘米秀面板的特别动作id点击
                    mFunction.inputText("群和C2C切换发送:" + count, 2000);
                    if (!mNodeOperate.clickOnResouceId("avatar_item_imageview", 2000, 0)) {
                        mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                                -100);
                        mNodeOperate.clickOnResouceId("avatar_item_imageview", 2000, 0);
                    }

                    mNodeOperate.clickOnTextContain("返回", 1500);
                    mNodeOperate.clickOnTextContain("消息", 1500);

                    //输入群，点击进入
                    mFunction.inputText("1252781078", 2000);
                    //进入C2C
                    mNodeOperate.clickOnTextContain("运动达人", 3000);
                    //点击AIO输入输入框上方的中间部分区域
                    mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                            -100);
                    //通过厘米秀面板的特别动作id点击
                    mFunction.inputText("群和C2C切换发送:" + count, 2000);
                    if (!mNodeOperate.clickOnResouceId("avatar_item_imageview", 1000, 0)) {
                        mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                                -100);
                        mNodeOperate.clickOnResouceId("avatar_item_imageview", 1000, 0);
                    }

                    mNodeOperate.clickOnTextContain("返回", 1500);
                    mNodeOperate.clickOnTextContain("消息", 1500);

                    //每轮查询可用内存和进程内存情况,并保存到终端存储
                    mFunction.saveMem("com.tencent.mobileqq", fileName);
                }
                mOperate.sleep(2000);
                mUIOperate.sendKey(KeyEvent.KEYCODE_HOME, 2000);
                // 测试用例结束，打印日志
                mEventHandler.sendEmptyMessage(PRINT_LOG);
            }
        });
        currentThread.start();
    }

    // 循环播放动作
    private void CSMT_5(final int caseTime) {
        mShow.updateState("case: " + (testNumber + 1) + "\n" + "循环播放动作");
        Thread currentThread = new Thread(new Runnable() {

            @Override
            public void run() {

                //初始化参数
                String fileName = "CSMT-5-" + Time.getCurrentTimeSecond();
                //杀手Q进程还原状态
                mFunction.killAppByPackageName("com.tencent.mobileqq");
                mOperate.sleep(3000);
                //热启动手Q
                try {
                    mOperate.startActivity("com.tencent.mobileqq",
                            "com.tencent.mobileqq.activity.SplashActivity");
                } catch (Exception e) {
                    Log.e(TAG, "start test app activity error!");
                    return;
                }
                mOperate.sleep(5000);

                //点击搜索栏
                mNodeOperate.clickOnText("搜索", 3000);
                //输入群，点击进入
                mFunction.inputText("546479585", 2000);
                //点击搜索栏
                mNodeOperate.clickOnText("测试号集中营", 3000);
                //点击AIO输入输入框上方的中间部分区域
                mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1, -100);
                mNodeOperate.clickOnResouceId("tabView", 2000, 1);
                //循环交互点击
                //如果没有点击成功判断面板是否隐藏了
                //通过厘米秀面板的特别动作id点击
                if (!mNodeOperate.clickOnResouceId("avatar_item_imageview", 1000, 0)) {
                    mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1,
                            -100);
                }
                mNodeOperate.clickOnTextContain("大群主", 1000);

                //点击AIO输入输入框上方的中间部分区域
                mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1, -100);

                for (int i = 0; i < caseTime; i++) {
                    mNodeOperate.clickOnTextContain("大群主", 3000);

                    //每轮查询可用内存和进程内存情况,并保存到终端存储
                    mFunction.saveMem("com.tencent.mobileqq", fileName);
                }
                mOperate.sleep(2000);
                mUIOperate.sendKey(KeyEvent.KEYCODE_HOME, 2000);
                // 测试用例结束，打印日志
                mEventHandler.sendEmptyMessage(PRINT_LOG);
            }
        });
        currentThread.start();
    }

    // 面板进入商城切换互动页
    private void CSMT_6(final int caseTime) {
        mShow.updateState("case: " + (testNumber + 1) + "\n" + "面板进入商城切换互动页");
        Thread currentThread = new Thread(new Runnable() {

            @Override
            public void run() {

                //初始化参数
                String fileName = "CSMT-6-" + Time.getCurrentTimeSecond();
                //杀手Q进程还原状态
                mFunction.killAppByPackageName("com.tencent.mobileqq");
                mOperate.sleep(3000);
                //热启动手Q
                try {
                    mOperate.startActivity("com.tencent.mobileqq",
                            "com.tencent.mobileqq.activity.SplashActivity");
                } catch (Exception e) {
                    Log.e(TAG, "start test app activity error!");
                    return;
                }
                mOperate.sleep(5000);

                //点击搜索栏
                mNodeOperate.clickOnText("搜索", 3000);
                //输入群，点击进入
                mFunction.inputText("546479585", 2000);
                //点击搜索栏
                mNodeOperate.clickOnText("测试号集中营", 3000);
                //点击AIO输入输入框上方的中间部分区域
                mNodeOperate.clickOnResouceIdOffset("inputBar", 2000, 1, -100);

                for (int i = 0; i < caseTime; i++) {
                    //点击商城入口按钮
                    mNodeOperate.clickOnResouceId("btn_more_apollo", 5000, 0);
                    //切换到互动页
                    mNodeOperate.clickOnResouceIdOffset("rlCommenTitle", 3000, 0, 100);
                    //切换到商城页
                    mNodeOperate.clickOnResouceIdOffset("rlCommenTitle", 3000, 0, -100);

                    mUIOperate.sendKey(KeyEvent.KEYCODE_BACK, 3000);

                    //每轮查询可用内存和进程内存情况,并保存到终端存储
                    mFunction.saveMem("com.tencent.mobileqq", fileName);
                }
                mOperate.sleep(2000);
                mUIOperate.sendKey(KeyEvent.KEYCODE_HOME, 2000);
                // 测试用例结束，打印日志
                mEventHandler.sendEmptyMessage(PRINT_LOG);
            }
        });
        currentThread.start();
    }

    // private function
    // start shut screen
    private void shutScreen(long minutes) {
        powerOffDelay(mContext, minutes * (int) (60 * StaticData.timeGene));
        mUIOperate.sendKey(KeyEvent.KEYCODE_POWER, 0);
    }

    private void powerOffDelay(Context mContext, Long second) {
        if (second < 0) {
            return;
        }

        AlarmManager alarms = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);
        int alarmType = AlarmManager.RTC_WAKEUP;
        Date t = new Date();

        t.setTime(java.lang.System.currentTimeMillis() + 1000 * second);
        alarms.set(alarmType, t.getTime(), makePendingIntent(mContext));
    }

    private PendingIntent makePendingIntent(Context mContext) {
        Intent intentToFire = new Intent();
        intentToFire.setClass(mContext, unLockService.class);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0,
                intentToFire, 0);
        return pendingIntent;
    }

    private void cancelAlarmRepeat(Context mContext) {
        AlarmManager alarms = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(makePendingIntent(mContext));
    }

    private void goToSleep() {
        PowerManager powerManager = (PowerManager) mContext
                .getSystemService(Context.POWER_SERVICE);
        powerManager.goToSleep(SystemClock.uptimeMillis());
    }

    @SuppressWarnings("deprecation")
    private void sendNotification() {
        NotificationManager nm = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon,
                getResources().getString(R.string.app_name),
                System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        notification.setLatestEventInfo(this,
                getResources().getString(R.string.app_name), "Finish",
                contentIntent);
        notification.defaults = Notification.DEFAULT_SOUND;
        nm.notify("Finish", 0, notification);
    }

    /*
     * Unlock screen broadcast Receiver
     */
    public class RunTestBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context mContext, Intent mIntent) {
            // cancel the alarm unlock
            cancelAlarmRepeat(mContext);
            mEventHandler.sendEmptyMessageDelayed(PRINT_LOG, 5000);
            // unregister
            unregisterReceiver(mReceiver);
            unregisterReceiver(mActionReceiver);
        }
    }

}
