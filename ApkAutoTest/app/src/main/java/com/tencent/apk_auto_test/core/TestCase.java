package com.tencent.apk_auto_test.core;

/**
 * Created by veehou on 2017/4/16.22:50
 */

public interface TestCase {
    void setCaseName(String caseName);

    String getCaseName();

    void setCaseTime();

    void runBeforeTest();

    void runTest();

    void endTest();
}
