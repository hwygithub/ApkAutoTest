package com.tencent.apk_auto_test.core;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * 测试工具的tips
 * <p>
 * Created by veehou on 2017/3/9.
 */

public class TestTips {
    private WindowManager wm;
    private Context mContext;
    private Handler mScreenHandler;

    private Button state;
    private Button stop;

    public TestTips(Context context) {
        mContext = context;
        mScreenHandler = new TipsHandler();
    }

    /**
     * 更新tips的内容
     *
     * @param tipsContent 内容
     */
    public void updateTips(String tipsContent) {
        if (null == wm) {
            return;
        }
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("text1", tipsContent);
        msg.setData(bundle);
        mScreenHandler.sendMessage(msg);
    }

    public void removeTips() {
        wm.removeView(stop);
        wm.removeView(state);
    }


    public void initTips(View.OnClickListener StopClickListener) {
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.format = 1;
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = 150;
        wmParams.flags = 24;
        wmParams.gravity = Gravity.BOTTOM | Gravity.START;
        wmParams.alpha = 0.3f;
        state = new Button(mContext);
        state.setBackgroundColor(Color.GRAY);
        state.setAlpha(0.8f);
        wm.addView(state, wmParams);

        WindowManager.LayoutParams wmParams2 = new WindowManager.LayoutParams();
        wmParams2.format = 1;
        wmParams2.x = 15;
        wmParams2.y = 35;
        wmParams2.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams2.width = 50;
        wmParams2.height = 50;
        wmParams2.flags = 8;
        wmParams.alpha = 0.6f;
        wmParams2.gravity = Gravity.BOTTOM | Gravity.END;
        stop = new Button(mContext);
        stop.setBackgroundColor(Color.RED);
        stop.setOnClickListener(StopClickListener);
        stop.setAlpha(0.5f);
        wm.addView(stop, wmParams2);
    }

    private class TipsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String str1 = msg.getData().getString("text1");
            state.setTextSize(15.0F);
            state.setTextColor(Color.BLACK);
            state.setText(str1);
            state.setGravity(2);
        }
    }

}
