package com.tencent.apk_auto_test.task;

import android.util.Log;
import android.view.KeyEvent;

import com.tencent.apk_auto_test.core.TestTask;
import com.tencent.apk_auto_test.data.Global;
import com.tencent.apk_auto_test.data.StaticData;

import java.io.File;

/**
 * 厘米秀基本功能回归测试
 * Created by veehou on 2017/4/17.
 */

public class CmShowBasicTask extends TestTask {
    private static final String TAG = "CmShowBasicTask";

    @Override
    public String getTaskSimpleName() {
        return "CSAT";
    }

    /****************
     * 用例执行部分--开始
     ******************/
    //登陆测试账号
    public void CSAT_0(final int caseTime) {
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

    // [1]非开通厘米秀用户进入AIO
    public void CSAT_1(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            // TODO: 2017/3/5 关闭厘米秀功能
            //删除厘米秀素材
            //mBlackBox.delFolder(new File("sdcard/tencent/MobileQQ/.apollo"));
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //// TODO: 2017/3/5 判断小人没有显示

        }
    }

    // [2]通过C2C功能面板开通厘米秀
    public void CSAT_2(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //点击搜索栏
            mNodeBox.clickOnText("搜索", 1000);
            //输入群，点击进入
            mBlackBox.inputText("1220232584", 1000);
            //进入好友AIO
            mNodeBox.clickOnTextContain("我的好友", 4000);
            //点击功能面板按钮
            mNodeBox.clickOnResourceId("qq_aio_panel_plus", 1000, 0);
            //向左滑动
            mBox.swipe((float) Global.SCREEN_WIDTH - 100, (float) (Global.SCREEN_HEIGHT * 0.8), 100, (float) (Global.SCREEN_HEIGHT * 0.8),
                    1000);
            //点击厘米秀
            mNodeBox.clickOnDesc("厘米秀按钮", 3000);
            //点击开启厘米秀
            //mNodeBox.clickOnText("开启厘米秀", 1000);
            // TODO: 2017/3/5 小人显示；弹起动作面板
            monitor.checkNode("id", "btn_more_apollo", testNumber);
        }
    }

    // [3]进入抽屉页自动播放sayhi动作
    public void CSAT_3(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入抽屉页
            mNodeBox.clickOnResourceId("conversation_head", 3000, 0);
            // TODO: 2017/3/5 小人显示、播放sayhi
        }
    }

    // [4] 通过抽屉页入口进入web页
    public void CSAT_4(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入抽屉页
            mNodeBox.clickOnResourceId("conversation_head", 5000, 0);
            //点击抽屉页厘米秀小人
            mNodeBox.clickOnResourceIdOffset("nightmode", 3000, 0, 0, 200);
            //// TODO: 2017/3/5 进入Web页；小人展示、播放sayhi
        }
    }

    // [5] 通过动作面板入口进入换装页
    public void CSAT_5(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过AIO进入换装页
            _AIOOpenChangeClothesWeb();
            // TODO: 2017/3/5 切换到换装页；小人展示播放sayhi
        }
    }

    //［6］通过换装页分享入口进入分享浮层
    public void CSAT_6(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过AIO进入换装页
            _AIOOpenChangeClothesWeb();
            //点击换装分享入口
            mImageBox.clickOnImage("btn_web_share", 2000);
            // TODO: 2017/3/14 小人展示、播放随机动作
        }
    }

    //［7］分享浮层分享到好友
    public void CSAT_7(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过AIO进入换装页
            _AIOOpenChangeClothesWeb();
            //点击换装分享入口
            mImageBox.clickOnImage("btn_web_share", 2000);
            //点击分享按钮
            mImageBox.clickOnImage("btn_web_share_send", 2000);
            //点击好友按钮
            mNodeBox.clickOnText("好友", 6000);
            //点击我的电脑并发送
            mNodeBox.clickOnText("我的电脑", 1000);
            mNodeBox.clickOnText("发送", 2000);
            // TODO: 2017/3/14 增加发送成功的判断
        }
    }

    //［8］分享浮层分享到QQ空间
    public void CSAT_8(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过AIO进入换装页
            _AIOOpenChangeClothesWeb();
            //点击换装分享入口
            mImageBox.clickOnImage("btn_web_share", 2000);
            //点击分享按钮
            mImageBox.clickOnImage("btn_web_share_send", 2000);
            //点击qq空间按钮
            mNodeBox.clickOnText("QQ空间", 4000);
            // TODO: 2017/3/14 增加跳转成功的判断
        }
    }

    //［9］分享浮层下载图片
    public void CSAT_9(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过AIO进入换装页
            _AIOOpenChangeClothesWeb();
            //点击换装分享入口
            mImageBox.clickOnImage("btn_web_share", 2000);
            //点击下载按钮
            mImageBox.clickOnImage("btn_web_share_download", 2000);
            // TODO: 2017/3/14 下载成功
        }
    }

    //［10］分享浮层更换小人动作
    public void CSAT_10(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过AIO进入换装页
            _AIOOpenChangeClothesWeb();
            //点击换装分享入口
            mImageBox.clickOnImage("btn_web_share", 2000);
            //点击下载按钮
            mImageBox.clickOnImage("btn_web_share_change", 2000);
            // TODO: 2017/3/14 小人播放随机动作
        }
    }

    //［11］换装页更换装扮并保存
    public void CSAT_11(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过AIO进入换装页
            _AIOOpenChangeClothesWeb();

            while (true) {

                mImageBox.clickOnImage("icon_web_clothes_white", 1000);

            }

            // TODO: 2017/3/14 小人更换装扮成功
        }
    }

    //［12］换装页预览播放动作
    public void CSAT_12(final int caseTime) {

    }

    //［13］通过资料卡入口进入互动页
    public void CSAT_13(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入抽屉页
            mNodeBox.clickOnResourceId("conversation_head", 2000, 0);
            //点击本人头像
            mNodeBox.clickOnResourceId("head", 5000, 0);
            //点击资料卡小人
            mImageBox.clickOnImage("cmshow_me_show", 6000);
            // TODO: 2017/3/14 小人从部分隐藏变为展示；播放sayhi动作
            //再次点击资料卡小人
            mImageBox.clickOnImage("cmshow_me_show", 8000);
            // TODO: 2017/3/14 切换到互动页
        }
    }

    //［14］通过陌生人资料卡入口进入换装页
    public void CSAT_14(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //点击＋
            mNodeBox.clickOnResourceId("conversation_title_right_btn", 1000, 0);
            //加好友
            mNodeBox.clickOnTextContain("加好友", 2000);
            //搜索
            mNodeBox.clickOnTextContain("QQ号", 1000);
            //陌生人测试号
            mBlackBox.inputText("503855711", 2000);
            //找人
            mNodeBox.clickOnTextContain("找人", 5000);
            //点击资料卡小人
            mImageBox.clickOnImage("cmshow_me_show", 6000);
            //再次点击资料卡小人
            mImageBox.clickOnImage("cmshow_me_show", 8000);
            //点击商城按钮
            mImageBox.clickOnImage("btn_aio_float_change", 4000);
            // TODO: 2017/3/14 切换到互动页
        }

    }

    //［15］通过AIO小人浮层入口进入换装页
    public void CSAT_15(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenActionTab］进入测试群号AIO打开面板
            _OpenActionTab();
            //长按AIO自己小人
            mNodeBox.clickOnResourceIdOffset("inputBar", 1000, 0, 1, -100, 3000);
            //点击商城按钮
            mImageBox.clickOnImage("btn_aio_float_change", 4000);
            // TODO: 2017/3/16 跳转到换装页

        }
    }

    //［16］通过AIO小人浮层入口进入送花详情页
    public void CSAT_16(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenActionTab］进入测试群号AIO打开面板
            _OpenActionTab();
            //长按AIO自己小人
            mNodeBox.clickOnResourceIdOffset("inputBar", 1000, 0, 1, -100, 2000);
            //点击送花按钮
            mImageBox.clickOnImage("btn_aio_float_flower", 4000);
            // TODO: 2017/3/16 跳转到送花详情页

        }
    }

    //［17］通过游戏面板游戏次数入口进入我的游戏卷页
    public void CSAT_17(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入游戏面板
            mNodeBox.clickOnResourceId("tabView", 1000, 0);
            //点击游戏面板次数按钮
            mImageBox.clickOnImage("icon_game_time", 4000);
            // TODO: 2017/3/22  跳转到我的游戏卷

        }
    }

    //［18］通过游戏面板排行榜入口进入战绩中心
    public void CSAT_18(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入游戏面板
            mNodeBox.clickOnResourceId("tabView", 1000, 0);
            //点击排行榜入口按钮
            mImageBox.clickOnImage("icon_game_result", 4000);
            // TODO: 2017/3/22  跳转到战绩中心
        }
    }

    //[19] 通过AIO游戏面板启动厘米FLY
    public void CSAT_19(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入厘米FLY游戏面板详情页
            _OpenGameTab("cmFly");
            //点击开始游戏
            mNodeBox.clickOnResourceId("btn_start_game_single", 10000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isNodeExist("text", "新手引导")) {
                mNodeBox.clickOnResourceId("ivTitleBtnLeft", 2000, 0);
                //点击开始游戏
                mNodeBox.clickOnResourceId("btn_start_game_single", 10000, 0);
            }
        }
    }

    //[20] 通过AIO游戏面板启动厘米大乱斗
    public void CSAT_20(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入厘米大乱斗游戏面板详情页
            _OpenGameTab("cmLuandou");
            //点击开始游戏
            mNodeBox.clickOnResourceId("btn_start_game_single", 10000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isNodeExist("text", "新手引导")) {
                mNodeBox.clickOnResourceId("ivTitleBtnLeft", 1000, 0);
                //点击开始游戏
                mNodeBox.clickOnResourceId("btn_start_game_single", 10000, 0);
            }
        }
    }

    // [21] FLY小游戏开始后退出游戏
    public void CSAT_21(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入厘米FLY游戏面板详情页
            _OpenGameTab("cmFly");
            //点击挑战纪录按钮
            mNodeBox.clickOnResourceId("btn_start_game_single", 4000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isNodeExist("text", "新手引导")) {
                mNodeBox.clickOnResourceId("ivTitleBtnLeft", 1000, 0);
                continue;
            }
            //开始后点击左上角X结束游戏
            mImageBox.clickOnImage("btn_game_cmFly_exit", 2000);
            // TODO: 2017/3/22 回到AIO；AIO小人正常显示

        }
    }

    // [22] FLY小游戏开始游戏后最小最大化
    public void CSAT_22(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入厘米FLY游戏面板详情页
            _OpenGameTab("cmFly");
            //点击挑战纪录按钮
            mNodeBox.clickOnResourceId("btn_start_game_single", 4000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isNodeExist("text", "新手引导")) {
                mNodeBox.clickOnResourceId("ivTitleBtnLeft", 1000, 0);
                continue;
            }
            //开始后点击右上角最小化按钮最小化游戏
            mImageBox.clickOnImage("btn_game_cmFly_min", 2000);
            // TODO: 2017/3/22 回到AIO；AIO显示顶部条
            //点击顶部条
            mNodeBox.clickOnResourceId("qq_aio_tips_container", 3000, 0);
            // TODO: 2017/3/22 回到游戏界面

        }
    }

    // [23] --
    public void CSAT_23(final int caseTime) {
    }

    // [24] 发送厘米秀单人动作
    public void CSAT_24(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入单人动作面板
            mNodeBox.clickOnResourceId("tabView", 1000, 1);
            //发送最新的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 4000, 0);
            // TODO: 2017/3/23 动作成功播放；状态显示为单人;消息列表出现动作消息
        }
    }

    // [25] 交叉发送厘米秀单人动作
    public void CSAT_25(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入单人动作面板
            mNodeBox.clickOnResourceId("tabView", 1000, 1);
            //发送最新的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 0);
            //播放过程中发送另一个动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 4000, 1);
            // TODO: 2017/3/23 动作被打断并且成功播放另一个动作
        }
    }

    //[26] 发送厘米秀双人动作
    public void CSAT_26(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入双人动作面板
            mNodeBox.clickOnResourceId("tabView", 1000, 2);
            //发送最新的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 4000, 0);
            // TODO: 2017/3/23 动作成功播放；状态显示为双人；消息列表出现动作消息
        }
    }

    //[27] 通过动作消息播放厘米秀动作
    public void CSAT_27(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入双人动作面板
            mNodeBox.clickOnResourceId("tabView", 2000, 2);
            //发送最新的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 0);
            //点击消息列表的动作消息
            mNodeBox.clickOnResourceId("qq_aio_apollo_action_icon", 3000, 0);
            // TODO: 2017/3/23 再次播放该动作
        }
    }

    //[28] 发送厘米秀弹幕动作
    public void CSAT_28(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入弹幕动作面板
            mNodeBox.clickOnResourceId("tabView", 2000, 3);
            //发送最新的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 4000, 0);
            // TODO: 2017/3/23  动作成功播放
        }
    }

    //[29] 通过动作消息收藏厘米秀动作
    public void CSAT_29(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入弹幕动作面板
            mNodeBox.clickOnResourceId("tabView", 2000, 4);
            //发送最新的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 0);
            //长按消息列表的动作消息
            mNodeBox.clickOnResourceId("qq_aio_apollo_action_icon", 2000, -1, 2000);
            //点击存动作
            mNodeBox.clickOnText("存动作", 2000);
            //进入收藏面板
            mNodeBox.clickOnResourceIdOffset("tabView", 2000, 3, 0, -20);
            //点击最近的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 1);
            // TODO: 2017/3/23  动作成功播放
        }
    }


    //[30] 通过自定义动作入口进入换装页动作面板
    public void CSAT_30(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入收藏收集面板
            mNodeBox.clickOnResourceId("tabView", 2000, 3);
            //点击管理动作按钮
            mNodeBox.clickOnText("管理动作", 5000);
            // TODO: 2017/3/23  检查进入换装页并切换到自定义动作面板
        }
    }

    //[31] 通过互动页厘米秀形象进入AI互动页
    public void CSAT_31(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //[_OpenChangeClothesWeb] 通过抽屉页进入互动页
            _OpenChangeClothesWeb();
            //长按厘米秀形象
            mImageBox.clickOnImage("cmshow_me_web", 3000, 2000);
            // TODO: 2017/3/23  进入到AI页
        }
    }

    //[32] --
    public void CSAT_32(final int caseTime) {

    }

    //[33] 大乱斗游戏中退出游戏
    public void CSAT_33(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入厘米大乱斗游戏面板详情页
            _OpenGameTab("cmLuandou");

            //点击开始游戏
            mNodeBox.clickOnResourceId("btn_start_game_single", 4000, 0);
            //如果进入新手引导页面则退出重来
            if (mNodeBox.isNodeExist("text", "新手引导")) {
                mNodeBox.clickOnResourceId("ivdefaultLeftBtn", 1000, 0);
                continue;
            }
            //开始后点击右上角X结束游戏
            mImageBox.clickOnImage("btn_game_luandou_exit", 2000);
            //检测是否成功退出了游戏
            monitor.checkImageDisappear("btn_game_luandou_exit", testNumber);

        }
    }

    //[34] 大乱斗游戏中最小化最大化游戏
    public void CSAT_34(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入厘米大乱斗游戏面板详情页
            _OpenGameTab("cmLuandou");
            //点击开始游戏
            mNodeBox.clickOnResourceId("btn_start_game_single", 4000, 0);
            //如果进入新手引导页面则退出重来
            if (mNodeBox.isNodeExist("text", "新手引导")) {
                mNodeBox.clickOnResourceId("ivdefaultLeftBtn", 1000, 0);
                continue;
            }
            //开始后点击右上角最小化按钮结束游戏
            mImageBox.clickOnImage("btn_game_luandou_min", 2000);
            //检测是否成功最小化
            //最小化后点击顶部的状态栏回到游戏
            mNodeBox.clickOnResourceId("qq_aio_tips_container", 3000, 0);
            //检测是否成功回到游戏
            monitor.checkImage("btn_game_luandou_min", testNumber);
        }
    }

    //[35] C2C进入表情面板有小白脸新手引导并发送小白脸
    public void CSAT_35(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入表情面板
            mNodeBox.clickOnResourceId("qq_aio_panel_emotion", 5000, 0);
            mBox.sendKey(KeyEvent.KEYCODE_BACK, 2000);
            mBox.sendKey(KeyEvent.KEYCODE_BACK, 2000);
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            mNodeBox.clickOnResourceId("qq_aio_panel_emotion", 2000, 0);
            //点击我来试试
            mImageBox.clickOnImage("icon_face_guard", 3000);
            //循环点击小白脸动作
            mImageBox.clickOnImage("icon_white_face_#1", 500);
            mImageBox.clickOnImage("icon_white_face_#2", 500);
            //发送
            mNodeBox.clickOnResourceId("fun_btn", 2000, 0);
        }
    }

    //[36] --
    public void CSAT_36(final int caseTime) {
    }

    //[37] 无游戏资源从WEB页面在群里发起游戏
    public void CSAT_37(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            // 删除游戏资源
            try {
                File cmFlyRes = new File("/sdcard/tencent/mobileqq/.apollo/game/1");
                mBlackBox.delFolder(cmFlyRes);
                Log.d(TAG, "---------------------CSAT_37: Resources of cmFly deleted!");
                // [_OpenGameTab]进入游戏面板
                _OpenGameTab("cmFly");
                // 进入游戏主页
                mNodeBox.clickOnResourceId("icon_game_rank", 5000, 0);
                // 点击“发起游戏”
                mImageBox.clickOnImage("btn_game_center_start", 10000);
                // 搜索选择测试群
                mNodeBox.clickOnText("搜索", 1000);
                // 输入群，点击进入
                mBlackBox.inputText("546479585", 3000);
                // 点击测试群
                mNodeBox.clickOnText("测试号集中营", 5000);
                // 收起游戏
                mImageBox.clickOnImage("btn_game_cmFly_min", 1000);
                // 检查是否成功发出游戏消息
                monitor.checkNode("id", "apollo_aio_game_bubble_head4", testNumber);
            } catch (NullPointerException e) {
                Log.d(TAG, "---------------------CSAT_37: Resources of cmFly not found!");
            }
        }
    }

    //[38] WEB页发起C2C游戏
    public void CSAT_38(final int caseTime) {
        for (int i = 0; i < caseTime; ++i) {
            // [_OpenGameTab]进入游戏面板
            _OpenGameTab("cmFly");
            // 进入游戏主页
            mNodeBox.clickOnResourceId("icon_game_rank", 5000, 0);
            // 点击“发起游戏”
            mImageBox.clickOnImage("btn_game_center_start", 10000);
            // 搜索选择测试群
            mNodeBox.clickOnText("搜索", 1000);
            // 进入好友AIO
            mBlackBox.inputText("1220232584", 3000);
            mNodeBox.clickOnTextContain("我的好友", 4000);
            // 收起游戏
            mImageBox.clickOnImage("btn_game_cmFly_min", 1000);
            // 检查是否成功发出游戏消息
            monitor.checkNode("id", "apollo_aio_game_bubble_head4", testNumber);
        }
    }

    //[39] 进入游戏详情页自动下载游戏资源
    public void CSAT_39(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            // 删除厘米Fly游戏资源文件
            try {
                File cmFlyRes = new File("/sdcard/tencent/mobileqq/.apollo/game/1");
                mBlackBox.delFolder(cmFlyRes);
                Log.d(TAG, "---------------------CSAT_37: Resources of cmFly deleted!");
                // [_OpenGameTab]进入游戏面板
                _OpenGameTab("cmFly");
                // 点击开始游戏
                mNodeBox.clickOnResourceId("btn_start_game", 10000, 0);
                // 检查资源文件是否下载成功
                monitor.checkResExist(cmFlyRes, i);
            } catch (NullPointerException e) {
                Log.d(TAG, "---------------------CSAT_37: Resources of cmFly not found!");
            }
        }
    }

    //[40] 发起游戏后的banner展示
    public void CSAT_40(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            // [_OpenGameTab]进入游戏面板
            _OpenGameTab("cmFly");
            //点击即时PK按钮
            mNodeBox.clickOnResourceId("btn_start_game", 4000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isNodeExist("text", "新手引导")) {
                mNodeBox.clickOnResourceId("ivTitleBtnLeft", 1000, 0);
                continue;
            }
            //开始后点击右上角最小化按钮最小化游戏
            mImageBox.clickOnImage("btn_game_cmFly_min", 2000);
            //检测AIO和消息列表是否有banner
            mNodeBox.clickOnText("等待玩家加入", 2000);
            //收起游戏
            mImageBox.clickOnImage("btn_game_cmFly_min", 2000);
            //返回消息列表
            mNodeBox.clickOnResourceId("leftbackroot", 1000, 0);
            //点击取消搜索
            mNodeBox.clickOnResourceId("btn_cancel_search", 1000, 0);
            //检查是否有游戏banner
            monitor.checkNode("id", "tipsbar_icon", testNumber);
        }
    }

    //[41] 游戏中再次通过AIO发起游戏
    public void CSAT_41(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            // [_OpenGameTab]进入游戏面板
            _OpenGameTab("cmFly");
            //点击即时PK按钮
            mNodeBox.clickOnResourceId("btn_start_game", 4000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isNodeExist("text", "新手引导")) {
                mNodeBox.clickOnResourceId("ivTitleBtnLeft", 1000, 0);
                continue;
            }
            //开始后点击右上角最小化按钮最小化游戏
            mImageBox.clickOnImage("btn_game_cmFly_min", 2000);
            //点击AIO输入输入框上方的中间部分区域，拉起游戏面板
            mNodeBox.clickOnResourceIdOffset("inputBar", 2000, 0, 1, -100);
            //再次回到游戏面板
            if (mImageBox.isImageExist("btn_game_tab_gray")) {
                mImageBox.clickOnImage("btn_game_tab_gray", 1000);
            }
            mNodeBox.clickOnResourceId("icon_back", 1000, 0);
            //点击厘米大乱斗
            int counter = 0;
            while (!mImageBox.isImageExist("logo_small_cmluandou") && counter != 5) {
                mBox.swipe((float) Global.SCREEN_WIDTH - 100, (float) (Global.SCREEN_HEIGHT * 0.8),
                        (float) (Global.SCREEN_WIDTH - 100), (float) (Global.SCREEN_HEIGHT * 0.7), 500);
                counter++;
            }
            mImageBox.clickOnImage("logo_small_cmluandou", 3000);
            //点击发起游戏
            mNodeBox.clickOnResourceId("btn_start_game", 5000, 0);
            //出现弹框，点击确定
            mNodeBox.clickOnText("确定", 5000);
            //检查是否成功发起新游戏
            monitor.checkImage("btn_game_luandou_exit", testNumber);
        }
    }
}
