package com.tencent.apk_auto_test.task;

import android.view.KeyEvent;

import com.tencent.apk_auto_test.core.TestTask;
import com.tencent.apk_auto_test.data.Global;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.util.TimeUtil;

/**
 * 厘米秀基本功能回归测试
 * Created by veehou on 2017/4/17.
 */

public class CmShowBasicTask extends TestTask {
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
            mFunction.clearAppByPackageName("com.tencent.mobileqq");
            mBox.sleep(3000);

            _InitQQ();

            //点击登录
            while (!mNodeBox.clickOnResourceId("btn_login", 2000, 0))
                mBox.sleep(2000);
            //点击输入QQ号
            mNodeBox.clickOnTextContain("QQ号", 1000);
            mFunction.inputText(StaticData.testUin, 1000);
            //点击输入密码
            mNodeBox.clickOnResourceId("password", 1000, 0);
            mFunction.inputText(StaticData.testPwd, 1000);

            mNodeBox.clickOnDesc("登录", 5000);
            mNodeBox.clickOnTextContain("关闭", 2000);
        }
    }

    // [1]非开通厘米秀用户进入AIO
    public void CSAT_1(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            // TODO: 2017/3/5 关闭厘米秀功能
            //删除厘米秀素材
            //mFunction.delFolder(new File("sdcard/tencent/MobileQQ/.apollo"));

            //点击搜索栏
            mNodeBox.clickOnText("搜索", 1000);
            //输入群，点击进入
            mFunction.inputText("1220232584", 1000);
            //进入好友AIO
            mNodeBox.clickOnTextContain("我的好友", 2000);
            //// TODO: 2017/3/5 判断小人没有显示
        }
    }

    // [2]通过C2C功能面板开通厘米秀
    public void CSAT_2(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //点击搜索栏
            mNodeBox.clickOnText("搜索", 1000);
            //输入群，点击进入
            mFunction.inputText("1220232584", 1000);
            //进入好友AIO
            mNodeBox.clickOnTextContain("我的好友", 2000);
            //点击功能面板按钮
            mNodeBox.clickOnResourceId("qq_aio_panel_plus", 1000, 0);
            //向左滑动
            mBox.swipe((float) Global.SCREEN_WIDTH - 100, (float) (Global.SCREEN_HEIGHT * 0.8), 100, (float) (Global.SCREEN_HEIGHT * 0.8),
                    1000);
            //点击厘米秀
            mNodeBox.clickOnDesc("厘米秀按钮", 1000);
            //点击开启厘米秀
            mNodeBox.clickOnText("开启厘米秀", 1000);
            // TODO: 2017/3/5 小人显示；弹起动作面板

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
            mNodeBox.clickOnResourceId("conversation_head", 3000, 0);
            //点击抽屉页厘米秀小人
            mNodeBox.clickOnResourceIdOffset("nightmode", 3000, 0, 0, 200);
            //// TODO: 2017/3/5 进入Web页；小人展示、播放sayhi
        }
    }

    // [5] 互动页切换到换装页
    public void CSAT_5(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过抽屉页进入换装页
            _OpenChangeClothesWeb();
            // TODO: 2017/3/5 切换到换装页；小人展示播放sayhi
        }
    }

    //［6］通过换装页分享入口进入分享浮层
    public void CSAT_6(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过抽屉页进入换装页
            _OpenChangeClothesWeb();
            //点击换装分享入口
            mImageBox.clickOnImage("btn_web_share", 2000);
            // TODO: 2017/3/14 小人展示、播放随机动作
        }
    }

    //［7］分享浮层分享到好友
    public void CSAT_7(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过抽屉页进入换装页
            _OpenChangeClothesWeb();
            //点击换装分享入口
            mImageBox.clickOnImage("btn_web_share", 2000);
            //点击分享按钮
            mImageBox.clickOnImage("btn_web_share_send", 2000);
            //点击好友按钮
            mNodeBox.clickOnText("好友", 4000);
            //点击我的电脑并发送
            mNodeBox.clickOnText("我的电脑", 1000);
            mNodeBox.clickOnText("发送", 2000);
            // TODO: 2017/3/14 增加发送成功的判断
        }
    }

    //［8］分享浮层分享到QQ空间
    public void CSAT_8(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //通过抽屉页进入换装页
            _OpenChangeClothesWeb();
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
            //通过抽屉页进入换装页
            _OpenChangeClothesWeb();
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
            //通过抽屉页进入换装页
            _OpenChangeClothesWeb();
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
            //通过抽屉页进入换装页
            _OpenChangeClothesWeb();
            //点击装扮区的取消全部并保存
            mImageBox.clickOnImage("tab_web_clothes_2", 1000);
            mImageBox.clickOnImage("icon_web_clothes_white", 1000);
            mImageBox.clickOnImage("btn_web_save_dress", 2000);
            // TODO: 2017/3/14 小人更换为默认打底装扮
            //选择套装的new标签的套装并保存
            mImageBox.clickOnImage("tab_web_clothes_1", 1000);
            mImageBox.clickOnImage("icon_web_new", 1000);
            mImageBox.clickOnImage("btn_web_save_dress", 2000);
            // TODO: 2017/3/14 小人更换装扮成功
        }
    }

    //［12］换装页预览播放动作
    //［13］通过资料卡入口进入互动页
    public void CSAT_13(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //进入抽屉页
            mNodeBox.clickOnResourceId("conversation_head", 2000, 0);
            //点击本人头像
            mNodeBox.clickOnResourceId("head", 5000, 0);
            //点击资料卡小人
            mImageBox.clickOnImage("cmshow_me_hide", 7000);
            // TODO: 2017/3/14 小人从部分隐藏变为展示；播放sayhi动作
            //再次点击资料卡小人
            mImageBox.clickOnImage("cmshow_me_stand", 4000);
            // TODO: 2017/3/14 切换到互动页
        }
    }

    //［14］通过陌生人资料卡入口进入换装页
    //［15］通过AIO小人浮层入口进入换装页
    public void CSAT_15(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenActionTab］进入测试群号AIO打开面板
            _OpenActionTab();
            //长按AIO自己小人
            mNodeBox.clickOnResourceIdOffset("inputBar", 1000, 0, 1, -100, 2000);
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

    //［19］FLY小游戏结束后分享成绩到AIO
    public void CSAT_19(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入游戏面板
            mNodeBox.clickOnResourceId("tabView", 1000, 0);
            //点击挑战纪录按钮
            mNodeBox.clickOnResourceId("apollo_aio_game_item_second", 4000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isTextExits("新手引导")) {
                mNodeBox.clickOnText("返回", 2000);
                continue;
            }
            // TODO: 2017/3/22 跳转到游戏界面；开始进行和测试号的影子比赛
            //循环长按点击屏幕等待，60s游戏结束
            long startTime = System.currentTimeMillis();
            while (!TimeUtil.isTimeOver(startTime, (float) 1.5)) {
                mBox.click((float) (Global.SCREEN_WIDTH / 2), (float) (Global.SCREEN_HEIGHT / 2), 3000, 1000);
            }
            //点击分享按钮
            mImageBox.clickOnImage("btn_game_end_share", 2000);
            //点击分享好友按钮
            mNodeBox.clickOnText("好友", 4000);
            //点击测试号并发送
            mNodeBox.clickOnText("搜索", 1000);
            mFunction.inputText("1220232584", 1000);
            mNodeBox.clickOnTextContain("厘米", 1000);
            mNodeBox.clickOnText("发送", 2000);
            //点击关闭按钮
            mImageBox.clickOnImage("btn_game_end_close", 2000);

            // TODO: 2017/3/22 AIO中显示分享的结构化消息；回到AIO；AIO小人正常显示
        }
    }

    //［20］FLY小游戏结束后分享成绩到QQ空间
    public void CSAT_20(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入游戏面板
            mNodeBox.clickOnResourceId("tabView", 1000, 0);
            //点击挑战纪录按钮
            mNodeBox.clickOnResourceId("apollo_aio_game_item_second", 4000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isTextExits("新手引导")) {
                mNodeBox.clickOnText("返回", 2000);
                continue;
            }
            // TODO: 2017/3/22 跳转到游戏界面；开始进行和测试号的影子比赛
            //循环长按点击屏幕等待，60s游戏结束
            long startTime = System.currentTimeMillis();
            while (!TimeUtil.isTimeOver(startTime, (float) 1.5)) {
                mBox.click((float) (Global.SCREEN_WIDTH / 2), (float) (Global.SCREEN_HEIGHT / 2), 3000, 1000);
            }
            //点击分享按钮
            mImageBox.clickOnImage("btn_game_end_share", 2000);
            //点击qq空间按钮
            mNodeBox.clickOnText("QQ空间", 4000);
            // TODO: 2017/3/22 跳转到说说发表页

        }
    }

    // [21] FLY小游戏开始后退出游戏
    public void CSAT_21(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入游戏面板
            mNodeBox.clickOnResourceId("tabView", 1000, 0);
            //点击挑战纪录按钮
            mNodeBox.clickOnResourceId("apollo_aio_game_item_second", 4000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isTextExits("新手引导")) {
                mNodeBox.clickOnText("返回", 2000);
                continue;
            }
            //开始后点击左上角X结束游戏
            mImageBox.clickOnImage("btn_game_exit", 2000);
            // TODO: 2017/3/22 回到AIO；AIO小人正常显示

        }
    }

    // [22] FLY小游戏开始游戏后最小最大化
    public void CSAT_22(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入游戏面板
            mNodeBox.clickOnResourceId("tabView", 1000, 0);
            //点击挑战纪录按钮
            mNodeBox.clickOnResourceId("apollo_aio_game_item_second", 4000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isTextExits("新手引导")) {
                mNodeBox.clickOnText("返回", 2000);
                continue;
            }
            //开始后点击右上角最小化按钮最小化游戏
            mImageBox.clickOnImage("btn_game_min_window", 2000);
            // TODO: 2017/3/22 回到AIO；AIO显示顶部条
            //点击顶部条
            mNodeBox.clickOnResourceId("qq_aio_tips_container", 3000, 0);
            // TODO: 2017/3/22 回到游戏界面

        }
    }

    // [23] FLY小游戏开始游戏后返回消息列表
    public void CSAT_23(final int caseTime) {
        for (int i = 0; i < caseTime; i++) {
            //［_OpenC2CActionTab］进入测试号AIO打开面板
            _OpenC2CActionTab();
            //进入游戏面板
            mNodeBox.clickOnResourceId("tabView", 1000, 0);
            //点击挑战纪录按钮
            mNodeBox.clickOnResourceId("apollo_aio_game_item_second", 4000, 0);
            //如果进入新手引导则返回
            if (mNodeBox.isTextExits("新手引导")) {
                mNodeBox.clickOnText("返回", 2000);
                continue;
            }
            //开始后点击back键
            mBox.sendKey(KeyEvent.KEYCODE_BACK, 2000);
            //点击确认退出按钮
            mNodeBox.clickOnText("确定", 2000);
            // TODO: 2017/3/23  回到手q消息列表
        }
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
            mNodeBox.clickOnResourceId("tabView", 2000, 3);
            //发送最新的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 0);
            //长按消息列表的动作消息
            mNodeBox.clickOnResourceId("qq_aio_apollo_action_icon", 2000, 0, 2000);
            //点击存动作
            mNodeBox.clickOnText("存动作", 2000);
            //进入收藏面板
            mNodeBox.clickOnResourceIdOffset("tabView", 2000, 4, 0, -20);
            //点击最近的动作
            mNodeBox.clickOnResourceId("avatar_item_imageview", 1000, 1);
            // TODO: 2017/3/23  动作成功播放
        }
    }


    //[30] 通过自定义动作入口进入换装页动作面板
    //[31] 跳转到web换装页
    //[32] 通过互动页进入AI互动页
}
