package com.tencent.apk_auto_test.ext.temp;

import android.app.Application;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

/**
 * Created by veehou on 2017/5/2.
 * 共享数据，提供给media projection 在activity 和service间的数据通信
 */

public class ImageShareApplication extends Application {
    private int mResult;
    private Intent mIntent;
    private MediaProjectionManager mediaProjectionManager;

    public int getResult() {
        return mResult;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public MediaProjectionManager getMediaProjectionManager() {
        return mediaProjectionManager;
    }

    public void setResult(int result) {
        mResult = result;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public void setMediaProjectionManager(MediaProjectionManager mediaProjectionManager) {
        this.mediaProjectionManager = mediaProjectionManager;
    }
}
