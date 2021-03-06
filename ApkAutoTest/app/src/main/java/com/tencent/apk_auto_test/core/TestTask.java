package com.tencent.apk_auto_test.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.data.Global;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.ext.BlackBox;
import com.tencent.apk_auto_test.ext.UIActionBox;
import com.tencent.apk_auto_test.ext.UIImageActionBox;
import com.tencent.apk_auto_test.ext.UINodeActionBox;
import com.tencent.apk_auto_test.ui.HelpActivity;
import com.tencent.apk_auto_test.util.TimeUtil;

import java.io.File;
import java.io.IOException;
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
    public int pid = 0;

    public UIActionBox mBox;
    public UINodeActionBox mNodeBox;
    public UIImageActionBox mImageBox;
    public TestMonitor monitor;
    public BlackBox mBlackBox;

    public String mRunFileName;
    public String mGameMode;

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
        mContext = getApplicationContext();

        Log.i(TAG, "TestTask is destroy");
        stopForeground(true);

        // return to the main activity
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mContext, HelpActivity.class);
        startActivity(intent);


        android.os.Process.killProcess(android.os.Process.myPid());

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // StartForeground

        if (null != StaticData.chooseArray) {
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
        mBlackBox = new BlackBox(mContext);
        mTips = new TestTips(mContext);
        mTestTask = this;
        monitor = new TestMonitor(mContext, getTaskSimpleName(), mNodeBox, mImageBox, mBlackBox);
        TestResultPrinter mPrinter = TestResultPrinter.getInstance();
        mPrinter.setUIImageActionBox(mImageBox);

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

        mBlackBox.initInputMethod();
        mBlackBox.delFolder(new File("/sdcard/tencent-test"));
        //清空原有的截图记录
        mImageBox.clearScreenshot();

        try {
            mBlackBox.copyAssetsFile("dress-test/cmshow_me_face.png", "sdcard/tencent/MobileQQ/.apollo/dress/3107/dress.png");
        } catch (IOException e) {
            Log.e(TAG, "copy face error");
            e.printStackTrace();
        }


        int number = StaticData.runList.get(testNumber).runCaseNumber;
        int time = StaticData.runList.get(testNumber).runNumber;
        startRunCase(number, time, new MyTestTaskController());
    }

    private class StopClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mTips.removeTips();
            onDestroy();
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
        StaticData.currentCase = StaticData.runList.get(testNumber).runCaseName;
        TestResultPrinter mPrinter = TestResultPrinter.getInstance();
        mPrinter.printInfo("[startRunCase]:" + "\t" + StaticData.chooseListText[caseNumber]);
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
                //获取当前PID
                if (null != mBlackBox.getAndroidProcess("com.tencent.mobileqq")) {
                    pid = mBlackBox.getAndroidProcess("com.tencent.mobileqq").getPid();
                }
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
            stopSelf();
        }
    }

    public abstract String getTaskSimpleName();

    /****************
     * 代码复用
     ******************/
    //［_InitQQ］杀手Q进程还原状态、启动手Q
    public void _InitQQ() {
        //杀手Q进程还原状态
        mBlackBox.killAppByPackageName("com.tencent.mobileqq");
        mBox.sleep(4000);
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
        mBlackBox.inputText("546479585", 3000);
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
        mBlackBox.inputText("1220232584", 3000);
        //点击测试号
        mNodeBox.clickOnTextContain("我的好友", 2000);
        //点击AIO输入输入框上方的中间部分区域
        mNodeBox.clickOnResourceIdOffset("input", 1000, 0, 1, -200);
    }

    //［_OpenGameTab］进入某个游戏的tab，默认为游戏排序界面
    public void _OpenGameTab(String gameName) {
        //［_OpenActionTab］进入群AIO打开动作面板
        _OpenActionTab();
        //点击游戏面板tab
        mNodeBox.clickOnResourceId("tabView", 2000, 0);
        String logoName = null;
        if (gameName == "cmFly") {
            logoName = "logo_small_cmfly";
        } else if (gameName == "cmLuandou") {
            logoName = "logo_small_cmluandou";
        } else if (gameName == "gudong") {
            logoName = "logo_small_gudong";
        }

        if (null != logoName) {
            //检查当前游戏是否为目标游戏，否则上下滑动
            int i = 0;
            while (!mImageBox.isImageExist(logoName) && i != 5) {
                mBox.swipe((float) Global.SCREEN_WIDTH - 100, (float) (Global.SCREEN_HEIGHT * 0.8),
                        (float) (Global.SCREEN_WIDTH - 100), (float) (Global.SCREEN_HEIGHT * 0.7), 500);
                i++;
            }
            mImageBox.clickOnImage(logoName, 3000);
        }

    }


    //[_OpenChangeClothesWeb] 通过抽屉页进入互动页
    public void _OpenChangeClothesWeb() {
        //进入抽屉页
        mNodeBox.clickOnResourceId("conversation_head", 3000, 0);
        //点击抽屉页厘米秀小人
        mNodeBox.clickOnResourceIdOffset("nightmode", 8000, 0, 0, 200);
    }

    //[_AIOOpenChangeClothesWeb] 通过AIO进入换装页
    public void _AIOOpenChangeClothesWeb() {
        //［_OpenC2CActionTab］进入测试号AIO打开面板
        _OpenC2CActionTab();
        //点击换装入口
        mNodeBox.clickOnResourceId("btn_more_apollo", 8000, 0);
    }

}
