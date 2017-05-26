package com.tencent.apk_auto_test.core;

import android.util.Log;

import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.ext.UIImageActionBox;
import com.tencent.apk_auto_test.ext.UINodeActionBox;

/**
 * Created by veehou on 2017/4/16.23:00
 * Modified by lloydgao on 2017/05/25.16:49
 */

public class TestMonitor {
    private static final String TAG = "TestMonitor";
    private TestResultPrinter mPrinter;
    private UINodeActionBox mNodeBox;
    private UIImageActionBox mImageBox;

    public TestMonitor(String name, UINodeActionBox nodeActionBox, UIImageActionBox imageActionBox) {
        mPrinter = TestResultPrinter.getInstance();
        mPrinter.setFileName(name);
        mNodeBox = nodeActionBox;
        mImageBox = imageActionBox;
    }

    /**
     * 检查node是否存在
     *
     * @param nodeType 参数类型
     */
    public boolean checkNode(String nodeType, String arg, int testNumber) {
        if (mNodeBox.isNodeExist(nodeType, arg)) {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName, true);
            Log.d(TAG, nodeType + " node:" + arg + " is exist, pass");
            return true;
        } else {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName, false);
            Log.e(TAG, "--------node:" + arg + " not exist, fail");
        }
        return false;
    }

    /**
     * 检查node是否消失
     *
     * @param nodeType 参数类型
     */
    public boolean checkNodeDisappear(String nodeType, String arg, int testNumber) {
        if (mNodeBox.isNodeExist(nodeType, arg)) {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName, false);
            Log.e(TAG, "--------node:" + arg + " exist, fail");
            return false;
        } else {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName, true);
            Log.d(TAG, nodeType + " node:" + arg + " exists, pass");
        }
        return true;
    }


    /**
     * 检查image是否存在
     *
     * @param imageName  图像资源名
     * @param testNumber 测试用例序号
     */
    public boolean checkImage(String imageName, int testNumber) {
        if (mImageBox.isImageExist(imageName)) {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName, true);
            Log.d(TAG, " imageName: " + imageName + " exists, pass");
            return true;
        } else {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName, false);
            Log.e(TAG, "--------imageName: " + imageName + " not exist, fail");
        }
        return false;
    }

    /**
     * 检查image是否消失
     *
     * @param imageName  图像资源名
     * @param testNumber 测试用例序号
     */
    public boolean checkImageDisappear(String imageName, int testNumber) {
        if (mImageBox.isImageExist(imageName)) {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName, false);
            Log.e(TAG, "--------imageName: " + imageName + " exists, fail");
            return false;
        } else {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName, true);
            Log.d(TAG, " imageName: " + imageName + " not exists, pass");
        }
        return true;
    }
}
