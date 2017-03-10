package com.tencent.apk_auto_test.runner;

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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.tencent.apk_auto_test.HelpActivity;
import com.tencent.apk_auto_test.MainActivity;
import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.data.Global;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.receiver.BatteryReceiver;
import com.tencent.apk_auto_test.receiver.ScreenActionReceiver;
import com.tencent.apk_auto_test.receiver.ShutDownReceiver;
import com.tencent.apk_auto_test.util.Function;
import com.tencent.apk_auto_test.util.TestMonitor;
import com.tencent.apk_auto_test.util.TimeUtil;
import com.tencent.apk_auto_test.util.UINodeOperate;
import com.tencent.apk_auto_test.util.UIOperate;
import com.test.function.Assert;
import com.test.function.Operate;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CmShowAutoRunner extends Service {
    // static
    protected static final int END_TEST = 0;
    protected static final int START_TEST = 1;
    protected static final int RUN_TEST = 2;
    protected static final int START_SCHEME = 3;
    protected static final int END_SCHEME = 4;
    protected static final int RUN_SCHEME = 5;
    protected static final int END_CASE = 70;

    private String TAG = "CmShowAutoRunner";

    // class
    private Operate mOperate;
    private Assert mAssert;
    private Context mContext;
    private TestMonitor mTestMonitor;
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
    private Method method;
    private CmShowAutoRunner serviceObject;

    // data
    public static int testNumber;
    private int schemeNumber;
    private String mRunFileName;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == mHandler) {
            return START_STICKY_COMPATIBILITY;
        }
        mHandler.sendEmptyMessageDelayed(START_SCHEME, 2000);
        return super.onStartCommand(intent, flags, startId);
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

    private void setClass() {
        mHandler = new TestHandler();
        mContext = getApplicationContext();
        mOperate = new Operate(mContext);
        mTestMonitor = new TestMonitor(mContext);
        mAssert = new Assert(mContext);
        mFunction = new Function(mContext, mHandler);
        mEventHandler = new EventHandler();
        mNodeOperate = new UINodeOperate(mContext);
        mUIOperate = new UIOperate(mContext);
        serviceObject = this;
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
            mTestMonitor.removeView();
            stopSelf();
            mFunction.stopPackage(mContext.getPackageName());
        }
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
                    // delete log folder
                    mFunction.delFolder(new File("/sdcard/tencent-test"));
                    // 设置系统对手Q的隐私权限为全部允许
                    // Set the update window
                    mTestMonitor.addView(new StopClickListener());

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
                    StaticData.testStartTime = TimeUtil.getCurrentTime();
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
                        StaticData.caseStartTime = TimeUtil.getCurrentTime();
                        startRunCase(StaticData.caseNumber, caseTime);
                    }
                    break;
                case END_TEST:
                    String testTime = TimeUtil.getPassTimeString(
                            StaticData.testStartTime, TimeUtil.getCurrentTime());
                    // write log
                    BatteryReceiver.writeLog(TimeUtil.getCurrentTimeSecond(), 4, 1);
                    BatteryReceiver.writeLog(testTime, 5, 1);
                    BatteryReceiver.writeLog(StaticData.testFinishEvent, 6, 1);
                    // After test,send sms  ---remove in 2016/12/21
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
                case END_CASE:
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
     * 用例执行入口
     * 优化代码结构：去除switch方式判断执行用例，采用反射方式动态获取类名后执行
     *
     * @param caseNumber test case name
     * @param caseTime   test case time
     */
    private void startRunCase(final int caseNumber, final int caseTime) {

        Method[] methods = getClass().getDeclaredMethods();
        //1、获取用例的数量
        int caseSum = 0;
        for (Method m : methods) {
            if (m.getName().contains("CSAT"))
                caseSum++;
        }
        //处理异常输入
        if (caseNumber >= caseSum) {
            Log.e(TAG, "case number over length");
            return;
        }
        //2、根据用例number执行拼接后指定的用例·
        String caseMethod = "CSAT_" + caseNumber;
        //getDeclaredMethod 能获取所有方法,getMethod 只能获取public 方法
        try {
            method = getClass().getDeclaredMethod(caseMethod, int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Thread currentThread = new Thread(new Runnable() {

            @Override
            public void run() {
                //悬浮窗显示状态
                mTestMonitor.updateState("case:" + (testNumber++ + 1) + " \t" + StaticData.chooseListText[caseNumber]);
                //初始化参数
                mRunFileName = "CSAT-" + caseNumber + "-" + TimeUtil.getCurrentTimeSecond();
                //热启动手Q
                if (caseNumber != 0 && caseNumber != 1)
                    _InitQQ();
                try {
                    method.invoke(serviceObject, caseTime);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                mUIOperate.sendKey(KeyEvent.KEYCODE_HOME, 1000);
                // 测试用例结束
                mEventHandler.sendEmptyMessage(END_CASE);
            }
        });
        currentThread.start();
    }

    /****************
     * 代码复用
     ******************/
    //［_InitQQ］杀手Q进程还原状态、启动手Q
    private void _InitQQ() {
        //杀手Q进程还原状态
        mFunction.killAppByPackageName("com.tencent.mobileqq");
        mOperate.sleep(1000);
        //热启动手Q
        try {
            mOperate.startActivity("com.tencent.mobileqq",
                    "com.tencent.mobileqq.activity.SplashActivity");
        } catch (Exception e) {
            Log.e(TAG, "start test app activity error!");
            return;
        }
        mOperate.sleep(5000);
    }

    //［_OpenActionTab］进入群AIO打开动作面板
    private void _OpenActionTab() {
        //点击搜索栏
        mNodeOperate.clickOnText("搜索", 1000);
        //输入群，点击进入
        mFunction.inputText("546479585", 1000);
        //点击测试群
        mNodeOperate.clickOnText("测试号集中营", 1000);
        //点击AIO输入输入框上方的中间部分区域
        mNodeOperate.clickOnResourceIdOffset("inputBar", 1000, 1,
                -100);
    }

    //［_OpenC2CActionTab］进入测试号AIO打开面板
    private void _OpenC2CActionTab() {
        //点击搜索栏
        mNodeOperate.clickOnText("搜索", 1000);
        //输入群，点击进入
        mFunction.inputText("1220232584", 1000);
        //点击测试群
        mNodeOperate.clickOnText("我的好友", 1000);
        //点击AIO输入输入框上方的中间部分区域
        mNodeOperate.clickOnResourceIdOffset("inputBar", 1000, 1,
                -100);
    }

    /****************
     * 用例执行部分--开始
     ******************/
    //登陆测试账号
    public void CSAT_0(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //杀手Q进程还原状态
            mFunction.clearAppByPackageName("com.tencent.mobileqq");
            mOperate.sleep(3000);

            _InitQQ();

            //点击登录
            while (!mNodeOperate.clickOnResourceId("btn_login", 2000, 0))
                mOperate.sleep(2000);
            //点击输入QQ号
            mNodeOperate.clickOnTextContain("QQ号", 1000);
            mFunction.inputText(StaticData.testUin, 1000);
            //点击输入密码
            mNodeOperate.clickOnResourceId("password", 1000, 0);
            mFunction.inputText(StaticData.testPwd, 1000);

            mNodeOperate.clickOnDesc("登录", 5000);
            mNodeOperate.clickOnTextContain("关闭", 500);
        }
    }

    // [1]非开通厘米秀用户进入AIO
    public void CSAT_1(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            // TODO: 2017/3/5 关闭厘米秀功能
            //删除厘米秀素材
            mFunction.delFolder(new File("sdcard/tencent/MobileQQ/.apollo"));

            _InitQQ();

            //点击搜索栏
            mNodeOperate.clickOnText("搜索", 1000);
            //输入群，点击进入
            mFunction.inputText("1220232584", 1000);
            //进入好友AIO
            mNodeOperate.clickOnTextContain("我的好友", 2000);
            //// TODO: 2017/3/5 判断小人没有显示
        }
    }

    // [2]通过C2C功能面板开通厘米秀
    public void CSAT_2(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //点击搜索栏
            mNodeOperate.clickOnText("搜索", 1000);
            //输入群，点击进入
            mFunction.inputText("1220232584", 1000);
            //进入好友AIO
            mNodeOperate.clickOnTextContain("我的好友", 2000);
            //点击功能面板按钮
            mNodeOperate.clickOnResourceId("qq_aio_panel_plus", 1000, 0);
            //向左滑动
            mUIOperate.swipe((float) Global.SCREEN_WIDTH - 100, (float) (Global.SCREEN_HEIGHT * 0.8), 100, (float) (Global.SCREEN_HEIGHT * 0.8),
                    1000);
            //点击厘米秀
            mNodeOperate.clickOnDesc("厘米秀按钮", 1000);
            //点击开启厘米秀
            mNodeOperate.clickOnText("开启厘米秀", 1000);
            // TODO: 2017/3/5 小人显示；弹起动作面板

        }
    }

    // [3]进入抽屉页自动播放sayhi动作
    public void CSAT_3(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入抽屉页
            mNodeOperate.clickOnResourceId("conversation_head", 3000, 0);
            // TODO: 2017/3/5 小人显示、播放sayhi
        }
    }

    // [4] 通过抽屉页入口进入web页
    public void CSAT_4(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入抽屉页
            mNodeOperate.clickOnResourceId("conversation_head", 3000, 0);
            //点击抽屉页厘米秀小人
            mNodeOperate.clickOnResourceIdOffset("nightmode", 3000, 0, 200);
            //// TODO: 2017/3/5 进入Web页；小人展示、播放sayhi
        }
    }

    // [5] 互动页切换到换装页
    public void CSAT_5(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入抽屉页
            mNodeOperate.clickOnResourceId("conversation_head", 3000, 0);
            //点击抽屉页厘米秀小人
            mNodeOperate.clickOnResourceIdOffset("nightmode", 3000, 0, 200);
            //点击切换到换装页
            mNodeOperate.clickOnResourceIdOffset("rlCommenTitle", 3000, 0, -100);
            // TODO: 2017/3/5 切换到换装页；小人展示播放sayhi 
        }
    }

    //［6］通过换装页分享入口进入分享浮层
    public void CSAT_6(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入抽屉页
            mNodeOperate.clickOnResourceId("conversation_head", 3000, 0);
            //点击抽屉页厘米秀小人
            mNodeOperate.clickOnResourceIdOffset("nightmode", 3000, 0, 200);
            //点击切换到换装页
            mNodeOperate.clickOnResourceIdOffset("rlCommenTitle", 3000, 0, -100);
            //点击换装分享入口
        }
    }

    //［7］分享浮层分享到好友
    public void CSAT_7(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入抽屉页
            mNodeOperate.clickOnResourceId("conversation_head", 3000, 0);
            //点击抽屉页厘米秀小人
            mNodeOperate.clickOnResourceIdOffset("nightmode", 3000, 0, 200);
            //点击切换到换装页
            mNodeOperate.clickOnResourceIdOffset("rlCommenTitle", 3000, 0, -100);
            //点击换装分享入口
        }
    }
    //［8］分享浮层分享到QQ空间
    //［9］分享浮层下载图片
    //［10］分享浮层更换小人动作

    /****************
     * 用例执行部分--结束
     ******************/


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
            mEventHandler.sendEmptyMessageDelayed(END_CASE, 5000);
            // unregister
            unregisterReceiver(mReceiver);
            unregisterReceiver(mActionReceiver);
        }
    }

}
