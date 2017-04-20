package com.tencent.apk_auto_test.core;

import com.tencent.apk_auto_test.ext.UIImageActionBox;
import com.tencent.apk_auto_test.ext.UINodeActionBox;

/**
 * Created by veehou on 2017/4/16.23:00
 */

public class TestMonitor {
    private TestResultPrinter mPrinter;
    private UINodeActionBox mNodeBox;
    private UIImageActionBox mImageBox;

    public TestMonitor(String name, UINodeActionBox nodeActionBox, UIImageActionBox imageActionBox) {
        mPrinter = TestResultPrinter.getInstance(name);
        mNodeBox = nodeActionBox;
        mImageBox = imageActionBox;
    }

    /**
     * 检查node是否存在
     *
     * @param nodeType
     */
    public void checkNode(String nodeType, String... args) {
        if (nodeType.equals("text")) {
        } else {
        }
    }

    public void checkImage() {
    }

}
