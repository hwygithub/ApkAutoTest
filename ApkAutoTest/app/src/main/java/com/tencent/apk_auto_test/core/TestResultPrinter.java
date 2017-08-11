package com.tencent.apk_auto_test.core;

import android.provider.ContactsContract;
import android.util.Log;

import com.tencent.apk_auto_test.ext.UIImageActionBox;
import com.tencent.apk_auto_test.util.TimeUtil;
import com.tencent.apk_auto_test.util.TxtUtil;


/**
 * Created by veehou on 2017/4/16.22:58
 */

public class TestResultPrinter {
    private static final String TAG = "TestResultPrinter";
    private static TestResultPrinter testResultPrinter = null;
    private String mResultFileName;
    private UIImageActionBox mUIImageActionBox = null;

    private TestResultPrinter() {
    }

    public void setFileName(String fileName) {
        mResultFileName = fileName;
    }

    public static synchronized TestResultPrinter getInstance() {
        if (null == testResultPrinter) {
            testResultPrinter = new TestResultPrinter();
        }
        return testResultPrinter;
    }

    public void setUIImageActionBox(UIImageActionBox tmpBox) {
        if (tmpBox != null) mUIImageActionBox = tmpBox;
    }

    public synchronized void printResult(String caseName, boolean isPass) {
        StringBuffer buffer = new StringBuffer();

        //fail则存储当前截屏
        if (!isPass) mUIImageActionBox.saveScreenshot(caseName);

        buffer.append(TimeUtil.getCurrentTimeSecond() + " ");
        buffer.append(isPass ? " pass " : " ----------fail---------- ");
        buffer.append(caseName + " ");
        buffer.append("\n");
        Log.v(TAG, "[printResult] " + buffer.toString());
        TxtUtil.saveMsg("/sdcard/tencent-test/", buffer.toString(), mResultFileName);
    }

    public synchronized void printInfo(String info) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(TimeUtil.getCurrentTimeSecond() + " ");
        buffer.append(info);
        buffer.append("\n");
        Log.v(TAG, "[printResult] " + buffer.toString());
        TxtUtil.saveMsg("/sdcard/tencent-test/", buffer.toString(), mResultFileName);
    }
}
