package com.tencent.apk_auto_test.core;

import android.util.Log;

import com.tencent.apk_auto_test.util.TimeUtil;
import com.tencent.apk_auto_test.util.TxtUtil;


/**
 * Created by veehou on 2017/4/16.22:58
 */

public class TestResultPrinter {
    private static final String TAG = "TestResultPrinter";
    public static TestResultPrinter testResultPrinter = null;
    private String mResultFileName;

    private TestResultPrinter(String fileName) {
        mResultFileName = fileName;
    }

    public static synchronized TestResultPrinter getInstance(String fileName) {
        if (null == testResultPrinter) {
            testResultPrinter = new TestResultPrinter(fileName);
        }
        return testResultPrinter;
    }

    public synchronized void printResult(String caseName, boolean isPass) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(TimeUtil.getCurrentTimeSecond() + "\t");
        buffer.append(caseName);
        buffer.append(isPass ? "pass" : "fail");
        Log.v(TAG, "[printResult] " + buffer.toString());
        TxtUtil.saveMsg("/sdcard/tencent-test/", buffer.toString(), mResultFileName);
    }
}
