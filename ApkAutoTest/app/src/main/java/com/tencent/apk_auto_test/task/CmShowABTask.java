package com.tencent.apk_auto_test.task;

import android.util.Log;

import com.tencent.apk_auto_test.core.TestTask;
import com.tencent.apk_auto_test.data.Global;
import com.tencent.apk_auto_test.data.StaticData;

import java.util.Random;

/**
 * Created by emmyzhou on 2017/8/7.
 */

public class CmShowABTask extends TestTask {

    private static final String TAG = "CmShowABTask";

    @Override
    public String getTaskSimpleName() {
        return "CSAB";
    }


    /****************
     * 用例执行部分--开始
     ******************/
    //登陆测试账号
    public void CSAB_0(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //杀手Q进程还原状态
            mBlackBox.clearAppByPackageName("com.tencent.mobileqq");
            mBox.sleep(3000);

            try {
                mBox.openApp("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity");
            } catch (Exception e) {
                Log.e(TAG, "start test app activity error!");
                return;
            }

            mNodeBox.setStrictMode(false);
            //点击登录
            while (!mNodeBox.clickOnResourceId("btn_login", 2000, 0))
                mBox.sleep(2000);
            mNodeBox.setStrictMode(true);
            //点击输入QQ号
            mNodeBox.clickOnTextContain("QQ号", 1000);
            mBlackBox.inputText(StaticData.testUin, 1000);
            //点击输入密码
            mNodeBox.clickOnResourceId("password", 1000, 0);
            mBlackBox.inputText(StaticData.testPwd, 1000);

            mNodeBox.clickOnDesc("登录", 90000);
        }
    }

    //[1] 房主:新手模式，游戏结束后进入游戏公众号和道具商城
    public void CSAB_1(final int caseTime) {
        // [_OpenGameTab]进入游戏面板
        _OpenGameTab("gudong");
        for (int i = 0; i < caseTime; i++) {
            //点击开始游戏按钮
            mNodeBox.clickOnResourceId("btn_start_game_single", 4000, 0);
            //如果进入新手引导则点击关闭按钮
            if (mImageBox.isImageExist("btn_gudong_exit_guideline")) {
                mImageBox.clickOnImage("btn_gudong_exit_guideline", 2000);
            }
            //循环等待判断是否开始游戏,获取当前方法名作为参数
            boolean isSucceedStart = _isBeginGame(new Exception().getStackTrace()[0].getMethodName());
            if (isSucceedStart) {
                //游戏开始...
                _PlayGuDong();
                //点击进入公众号
                mImageBox.clickOnImage("btn_gudong_official_accounts", 3000);
                mNodeBox.clickOnText("返回", 1000);
                //点击进入商城
                mImageBox.clickOnImage("btn_gudong_store", 3000);
                mNodeBox.clickOnText("返回", 1000);
                mImageBox.clickOnImage("btn_game_tab", 1000);
                //点击关闭按钮
                mImageBox.clickOnImage("btn_game_gudong_exit", 2000);
            }
            mNodeBox.clickOnResourceId("aio_listview1_footerview", 2000, 0);
            //每轮查询可用内存和进程内存情况,并保存到终端存储
            monitor.checkCrash("com.tencent.mobileqq", pid, mRunFileName, i);
        }
    }

    //[2] 房主：大逃杀模式，通过QQ分享邀请好友加入房间,
    public void CSAB_2(final int caseTime) {
        // [_OpenGameTab]进入游戏面板
        _OpenGameTab("gudong");
        for (int i = 0; i < caseTime; i++) {
            //点击开始游戏按钮
            mNodeBox.clickOnResourceId("btn_start_game_single", 4000, 0);
            //如果进入新手引导则点击关闭按钮
            if (mImageBox.isImageExist("btn_gudong_exit_guideline")) {
                mImageBox.clickOnImage("btn_gudong_exit_guideline", 2000);
            }
            mImageBox.clickOnImage("btn_gudong_invite", 1000);
            mNodeBox.clickOnResourceId("app_icon", 1000, 0);
            //点击搜索栏
            mNodeBox.clickOnText("搜索", 1000);
            //输入好友号，点击进入
            mBlackBox.inputText("1050001189", 3000);
            mNodeBox.clickOnText("我的好友", 2000);
            //选择大逃杀模式
            mImageBox.clickOnImage("btn_gudong_Battle_Royale", 3000);
            //循环等待判断是否开始游戏,获取当前方法名作为参数
            boolean isSucceedStart = _isBeginGame(new Exception().getStackTrace()[0].getMethodName());
            if (isSucceedStart) {
                //游戏开始...
                _PlayGuDong();
                mImageBox.clickOnImage("btn_game_gudong_exit", 2000);
            }
            mNodeBox.clickOnResourceId("aio_listview1_footerview", 2000, 0);
            //每轮查询可用内存和进程内存情况,并保存到终端存储
            monitor.checkCrash("com.tencent.mobileqq", pid, mRunFileName, i);
        }
    }

    //[3] 作为房主创建房间，并通过微信邀请好友加入
    public void CSAB_3(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
        }
    }

    //[4] 普通玩家：新手模式，游戏结束后分享结果至空间及好友处,
    public void CSAB_4(final int caseTime) {
        //[_OpenActionTab］进入群AIO打开动作面板
        _OpenActionTab();
        //关闭动作面板
        mNodeBox.clickOnResourceId("aio_listview1_footerview", 2000, 0);
        for (int i = 0; i < caseTime; i++) {
            //用于判断等待期间是否进入游戏
            boolean isSucceedEnter = false;
            //用于判断游戏是否正常开始
            boolean isSucceedStart;
            //循环等待点击PLAY按钮
            for (int j = 0; j < 10; j++) {
                if (mImageBox.isImageExist("btn_game_aio_play")) {
                    mImageBox.clickOnImage("btn_game_aio_play", 4000);
                    isSucceedEnter = true;
                    break;
                }
                mBox.sleep(6000);
            }
            if (!isSucceedEnter) {
                Log.d(TAG, "---------------------CSAB_4: The game did not start in the waiting time");
                monitor.checkCrash("com.tencent.mobileqq", pid, mRunFileName, i);
                continue;
            }
            //如果进入新手引导则点击关闭按钮
            if (mImageBox.isImageExist("btn_gudong_exit_guideline")) {
                mImageBox.clickOnImage("btn_gudong_exit_guideline", 2000);
            }
            //点击准备就绪按钮
            mImageBox.clickOnImage("btn_gudong_prepare", 2000);
            //循环等待判断是否开始游戏,获取当前方法名作为参数
            isSucceedStart = _isBeginGame(new Exception().getStackTrace()[0].getMethodName());
            if (isSucceedStart) {
                //游戏开始...
                _PlayGuDong();
                //分享游戏结果至QQ空间
                mImageBox.clickOnImage("btn_gudong_share", 2000);
                mNodeBox.clickOnResourceId("app_icon", 2000, 0);
                mNodeBox.clickOnText("发表", 2000);
                //分享游戏结果给好友
                mImageBox.clickOnImage("btn_gudong_share", 2000);
                mNodeBox.clickOnResourceId("app_icon", 2000, 1);
                mNodeBox.clickOnText("搜索", 1000);
                mBlackBox.inputText("546479585", 3000);
                mNodeBox.clickOnText("测试号集中营", 1000);
                mNodeBox.clickOnResourceId("dialogRightBtn", 3000, 0);
                //关闭游戏
                mImageBox.clickOnImage("btn_game_gudong_exit", 2000);
            }
            //每轮查询可用内存和进程内存情况,并保存到终端存储
            monitor.checkCrash("com.tencent.mobileqq", pid, mRunFileName, i);
        }
    }

    //[5] 普通玩家：大逃杀模式，接受手Q邀请进入房间,
    public void CSAB_5(final int caseTime) {
        //点击搜索栏
        mNodeBox.clickOnText("搜索", 1000);
        //输入好友号，点击进入
        mBlackBox.inputText("1077000139", 3000);
        //点击测试号
        mNodeBox.clickOnTextContain("我的好友", 2000);

        for (int i = 0; i < caseTime; i++) {
            //用于判断等待期间是否进入游戏
            boolean isSucceedEnter = false;
            //循环等待点击PLAY按钮
            for (int j = 0; j < 10; j++) {
                if (mImageBox.isImageExist("btn_game_aio_play")) {
                    mImageBox.clickOnImage("btn_game_aio_play", 4000);
                    isSucceedEnter = true;
                    break;
                }
                mBox.sleep(6000);
            }
            if (!isSucceedEnter) {
                Log.d(TAG, "---------------------CSAB_5: The game did not start in the waiting time");
                monitor.checkCrash("com.tencent.mobileqq", pid, mRunFileName, i);
                continue;
            }
            //如果进入新手引导则点击关闭按钮
            if (mImageBox.isImageExist("btn_gudong_exit_guideline")) {
                mImageBox.clickOnImage("btn_gudong_exit_guideline", 2000);
            }
            //点击准备就绪按钮
            mImageBox.clickOnImage("btn_gudong_prepare", 2000);
            //循环等待判断是否开始游戏,获取当前方法名作为参数
            boolean isSucceedStart = _isBeginGame(new Exception().getStackTrace()[0].getMethodName());
            if (isSucceedStart) {
                //游戏开始...
                _PlayGuDong();
                mImageBox.clickOnImage("btn_game_gudong_exit", 2000);
            }
            //每轮查询可用内存和进程内存情况,并保存到终端存储
            monitor.checkCrash("com.tencent.mobileqq", pid, mRunFileName, i);
        }
    }

    //[6] 作为普通玩家接受微信邀请进入房间
    public void CSAB_6(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
        }
    }

    //[_PlayGuDong] 咕咚大作战游戏中代码实现
    public void _PlayGuDong() {
        Random random = new Random();
        int times = 0;
        while (!mImageBox.isImageExist("btn_gudong_exit")) {
            float downX = (float) Global.SCREEN_WIDTH / 2;
            float downY = (float) Global.SCREEN_HEIGHT / 2;
            float TrendX[] = {1, 300, 300, 300, 1, -300, -300, -300};
            float TrendY[] = {-300, -300, 0, 300, 300, 300, 0, -300};
            int k = random.nextInt(7);
            float upX = downX + TrendX[k];
            float upY = downY + TrendY[k];
            //游戏时间为120s
            mBox.swipe(downX, downY, upX, upY, 5000);
            times++;
            if (times >= 18)
                break;
        }
    }

    //[_isBeginGame]循环等待判断是否开始游戏
    public boolean _isBeginGame(String method_name) {
        boolean isSucceedStart = false;
        for (int j = 0; j <= 12; j++) {
            if (mImageBox.isImageExist("btn_gudong_prepare_well") || mImageBox.isImageExist(("btn_gudong_start_immediately"))) {
                mBox.sleep(5000);
                continue;
            } else if (mImageBox.isImageExist("btn_gudong_hint_exit")) {
                Log.i(TAG, "---------------------" + method_name + ": The game has not started because of a shortage of people");
                mImageBox.clickOnImage("btn_gudong_hint_exit", 1000);
                break;
            }
            isSucceedStart = true;
            break;
        }
        return isSucceedStart;
    }
}
