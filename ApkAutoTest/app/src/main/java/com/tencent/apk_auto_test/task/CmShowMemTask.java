package com.tencent.apk_auto_test.task;

import android.view.KeyEvent;

import com.tencent.apk_auto_test.core.TestTask;
import com.tencent.apk_auto_test.data.StaticData;

/**
 * 厘米秀内存测试
 * Created by veehou on 2017/4/17.
 */

public class CmShowMemTask extends TestTask {
    @Override
    public String getTaskSimpleName() {
        return "CSMT";
    }


    /****************
     * 用例执行部分
     ******************/
    //登陆测试账号
    public void CSMT_0(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //杀手Q进程还原状态
            mFunction.clearAppByPackageName("com.tencent.mobileqq");
            mBox.sleep(3000);

            _InitQQ();

            //点击登录
            while (!mNodeBox.clickOnResourceId("btn_login", 3000, 0))
                mBox.sleep(5000);
            //点击输入QQ号
            mNodeBox.clickOnTextContain("QQ号", 1000);
            mFunction.inputText(StaticData.testUin, 1000);
            //点击输入密码
            mNodeBox.clickOnResourceId("password", 1000, 0);
            mFunction.inputText(StaticData.testPwd, 1000);

            mNodeBox.clickOnDesc("登录", 10000);
            mNodeBox.clickOnTextContain("关闭", 2000);
        }
    }

    // 群单人动作交叉发送
    public void CSMT_1(final int caseTime) {
        //进入群AIO打开动作面板
        _OpenActionTab();
        //点击tab切换到单人动作
        mNodeBox.clickOnResourceId("tabView", 2000, 1);
        //点击发送并播放厘米秀动作，交叉互相打断播放
        for (int i = 0; i < caseTime; i++) {
            //循环交互点击
            //如果没有点击成功判断面板是否隐藏了
            //通过厘米秀面板的特别动作id点击
            if (!mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 0)) {
                mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);
            }
            mFunction.inputText("群单人交叉发送:" + i, 1000);
            mNodeBox.clickOnResourceId("avatar_item_imageview", 2000, 1);
            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }
    }

    // 单双人动作交叉发送
    public void CSMT_2(final int caseTime) {
        //进入群AIO打开动作面板
        _OpenActionTab();
        //点击发送并播放厘米秀动作，交叉互相打断播放
        for (int i = 0; i < caseTime; i++) {
            //点击tab切换到单人动作
            mNodeBox.clickOnResourceId("tabView", 2000, 1);
            //循环交互点击
            //点击单人tab的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 0);
            //点击双人tab的动作
            mFunction.inputText("单双人动作交叉发送:" + i, 1000);
            //点击tab切换到双人动作
            mNodeBox.clickOnResourceId("tabView", 2000, 2);
            mNodeBox.clickOnResourceId("avatar_item_imageview", 2000, 0);
            mNodeBox.clickOnTextContain("大群主", 1000);
            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }

    }

    // 群双人动作交叉发送
    public void CSMT_3(final int caseTime) {
        //进入群AIO打开动作面板
        _OpenActionTab();
        //点击tab切换到双人动作
        mNodeBox.clickOnResourceId("tabView", 2000, 2);
        //点击发送并播放厘米秀动作，交叉互相打断播放
        int count = 0;
        for (int i = 0; i < caseTime; i++) {
            //循环交互点击
            //如果没有点击成功判断面板是否隐藏了
            //通过厘米秀面板的特别动作id点击
            if (!mNodeBox.clickOnResourceId("avatar_item_imageview", 2000, 0)) {
                mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);
            }
            mNodeBox.clickOnTextContain("大群主", 1000);
            mFunction.inputText("群双人交叉发送:" + i, 1000);
            mNodeBox.clickOnResourceId("avatar_item_imageview", 2000, 1);
            mNodeBox.clickOnTextContain("大群主", 1000);
            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }

    }

    // 弹幕动作发送
    public void CSMT_4(final int caseTime) {
        //进入群AIO打开动作面板
        _OpenActionTab();
        //点击tab切换到弹幕动作
        mNodeBox.clickOnResourceId("tabView", 2000, 3);
        int count = 0;
        for (int i = 0; i < caseTime; i++) {
            //循环交互点击
            //如果没有点击成功判断面板是否隐藏了
            //通过厘米秀面板的特别动作id点击
            if (!mNodeBox.clickOnResourceId("avatar_item_imageview", 2000, 0)) {
                mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);
            }
            mNodeBox.clickOnTextContain("大群主", 1000);
            mFunction.inputText("弹幕发送:" + i, 2000);

            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }
    }

    // 群和C2C切换发送
    public void CSMT_5(final int caseTime) {

        for (int i = 0; i < caseTime; i++) {
            //进入群AIO打开动作面板
            _OpenActionTab();
            //点击tab切换到单人动作
            mNodeBox.clickOnResourceId("tabView", 2000, 1);
            //如果没有点击成功判断面板是否隐藏了
            //通过厘米秀面板的特别动作id点击
            mFunction.inputText("群和C2C切换发送:" + i, 1000);
            if (!mNodeBox.clickOnResourceId("avatar_item_imageview", 2000, 0)) {
                mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);
                mNodeBox.clickOnResourceId("avatar_item_imageview", 2000, 0);
            }

            mNodeBox.clickOnResourceId("ivdefaultLeftBtn", 1000, 0);

            //输入群，点击进入
            mFunction.inputText("1220232584", 1000);
            //进入C2C
            mNodeBox.clickOnTextContain("厘米", 2000);
            //点击AIO输入输入框上方的中间部分区域
            mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);
            //点击tab切换到单人动作
            mNodeBox.clickOnResourceId("tabView", 2000, 1);
            //通过厘米秀面板的特别动作id点击
            mFunction.inputText("群和C2C切换发送:" + i, 2000);
            if (!mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 0)) {
                mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);
                mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 0);
            }

            mNodeBox.clickOnResourceId("ivdefaultLeftBtn", 1000, 0);

            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }

    }

    // 循环播放动作
    public void CSMT_6(final int caseTime) {
        //进入群AIO打开动作面板
        _OpenActionTab();
        mNodeBox.clickOnResourceId("tabView", 2000, 2);
        //循环交互点击
        //如果没有点击成功判断面板是否隐藏了
        //通过厘米秀面板的特别动作id点击
        if (!mNodeBox.clickOnResourceId("avatar_item_imageview", 2000, 0)) {
            mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);
        }
        mNodeBox.clickOnTextContain("大群主", 1000);
        //点击AIO输入输入框上方的中间部分区域
        mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);

        for (int i = 0; i < caseTime; i++) {
            mNodeBox.clickOnResourceId("avatar_item_imageview", 2000, 0);

            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }
    }

    // 面板进入商城
    public void CSMT_7(final int caseTime) {
        //进入群AIO打开动作面板
        _OpenActionTab();
        for (int i = 0; i < caseTime; i++) {
            //点击商城入口按钮
            mNodeBox.clickOnResourceId("btn_more_apollo", 5000, 0);

            mBox.sendKey(KeyEvent.KEYCODE_BACK, 3000);

            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }
    }

    // 抽屉页进入互动页
    public void CSMT_8(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //切换到抽屉页
            mNodeBox.clickOnResourceId("conversation_head", 3000, 0);
            //点击厘米秀小人
            mNodeBox.clickOnResourceIdOffset("nightmode", 3000, 0, 0, 200);

            mBox.sendKey(KeyEvent.KEYCODE_BACK, 3000);
            //切换到消息列表
            mNodeBox.clickOnResourceId("conversation_head", 3000, 0);
            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }
    }

    // 当前消息列表AIO切换
    public void CSMT_9(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            for (int j = 1; j <= 5; j++) {
                //切换到抽屉页
                mNodeBox.clickOnListViewByResourceId("recent_chat_list", j, 500);
                mNodeBox.clickOnResourceId("ivdefaultLeftBtn", 1000, 0);
            }
            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }

    }

    // 创建游戏后退出
    public void CSMT_10(final int caseTime) {
        //点击搜索栏
        mNodeBox.clickOnText("搜索", 1000);
        //输入群，点击进入
        mFunction.inputText("546479585", 2000);
        //点击搜索栏
        mNodeBox.clickOnText("测试号集中营", 3000);

        for (int i = 0; i < caseTime; i++) {
            //点击AIO输入输入框上方的中间部分区域
            mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);

            //点击开始游戏
            mNodeBox.clickOnResourceId("apollo_aio_game_item_first", 3500, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isNodeExits("text", "新手引导")) {
                mNodeBox.clickOnResourceId("ivdefaultLeftBtn", 1000, 0);
                continue;
            }
            //通过y偏移点击退出按钮
            mImageBox.clickOnImage("btn_game_exit", 1000);

            //每轮查询可用内存和进程内存情况,并保存到终端存储
            mFunction.saveMem("com.tencent.mobileqq", mRunFileName, i);
        }

    }
}
