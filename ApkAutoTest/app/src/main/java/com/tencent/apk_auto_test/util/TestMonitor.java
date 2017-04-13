package com.tencent.apk_auto_test.util;

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
 * Created by veehou on 2017/3/9.
 */

public class TestMonitor {
    private Context mContext;
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private static Button state;
    private static Button stop;

    public TestMonitor(Context context) {
        mContext = context;
    }


    private Handler mScreenHandler = new Handler() {
        public void handleMessage(Message msg) {
            String str1 = msg.getData().getString("text1");
            TestMonitor.state.setTextSize(15.0F);
            TestMonitor.state.setTextColor(Color.BLACK);
            TestMonitor.state.setText(str1);
            TestMonitor.state.setGravity(2);
        }
    };


    public void addView(View.OnClickListener StopClickListener) {
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.format = 1;
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = 100;
        wmParams.flags = 24;
        wmParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        wmParams.alpha = 0.3f;
        state = new Button(mContext);
        state.setBackgroundColor(Color.GRAY);
        state.setAlpha(0.4f);
        wm.addView(state, wmParams);

        WindowManager.LayoutParams wmParams2 = new WindowManager.LayoutParams();
        wmParams2.format = 1;
        wmParams2.x = 5;
        wmParams2.y = 35;
        wmParams2.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams2.width = 40;
        wmParams2.height = 40;
        wmParams2.flags = 8;
        wmParams.alpha = 0.6f;
        wmParams2.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        stop = new Button(mContext);
        stop.setBackgroundColor(Color.RED);
        stop.setOnClickListener(StopClickListener);
        stop.setAlpha(0.5f);
        wm.addView(stop, wmParams2);
    }

    public void removeView() {
        wm.removeView(stop);
        wm.removeView(state);
    }

    public void updateState(String s) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("text1", s);
        msg.setData(bundle);
        mScreenHandler.sendMessage(msg);
    }
}
