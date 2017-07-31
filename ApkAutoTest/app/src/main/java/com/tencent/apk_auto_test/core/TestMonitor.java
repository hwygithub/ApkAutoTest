package com.tencent.apk_auto_test.core;

import android.content.Context;
import android.util.Log;

import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.ext.BlackBox;
import com.tencent.apk_auto_test.ext.UIImageActionBox;
import com.tencent.apk_auto_test.ext.UINodeActionBox;
import com.tencent.apk_auto_test.ext.temp.AppEntity;
import com.tencent.apk_auto_test.util.TimeUtil;
import com.tencent.apk_auto_test.util.TxtUtil;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by veehou on 2017/4/16.23:00
 * Modified by lloydgao on 2017/05/25.16:49
 */

public class TestMonitor {
    private static final String TAG = "TestMonitor";

    private TestResultPrinter mPrinter;
    private UINodeActionBox mNodeBox;
    private UIImageActionBox mImageBox;
    private BlackBox mBlackBox;
    private Context mContext;

    public TestMonitor(Context context, String name, UINodeActionBox nodeActionBox, UIImageActionBox imageActionBox, BlackBox blackBox) {
        mPrinter = TestResultPrinter.getInstance();
        mPrinter.setFileName(name);

        mNodeBox = nodeActionBox;
        mImageBox = imageActionBox;
        mBlackBox = blackBox;
        mContext = context;
    }

    /**
     * 检查node是否存在
     *
     * @param nodeType 参数类型
     */
    public boolean checkNode(String nodeType, String arg, int testNumber) {
        if (mNodeBox.isNodeExist(nodeType, arg)) {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName + " : " + arg, true);
            Log.d(TAG, nodeType + " node:" + arg + " is exist, pass");
            return true;
        } else {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName + " : " + arg, false);
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
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName + " : " + arg, false);
            Log.e(TAG, "--------node:" + arg + " exist, fail");
            return false;
        } else {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName + " : " + arg, true);
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
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName + " : " + imageName, true);
            Log.d(TAG, " imageName: " + imageName + " exists, pass");
            return true;
        } else {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName + " : " + imageName, false);
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
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName + " : " + imageName, false);
            Log.e(TAG, "--------imageName: " + imageName + " exists, fail");
            return false;
        } else {
            mPrinter.printResult(StaticData.runList.get(testNumber - 1).runCaseName + " : " + imageName, true);
            Log.d(TAG, " imageName: " + imageName + " not exists, pass");
        }
        return true;
    }

    /**
     * 检查是否有无法解析动作的情况
     *
     * @return
     */
    public boolean checkAnimationPlay() {
        //检查是否有黑块的情况
        String imageName = "error_opengl";
        if (mImageBox.isImageExist(imageName)) {
            mPrinter.printResult("error_opengl", false);
            Log.d(TAG, " imageName: " + imageName + " exists, pass");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 存储目标进程的进程的pid，同时检测pid是否改变
     *
     * @param pkgName  目标进程包的名字
     * @param fileName 存储文件的名字
     *                 by lloydgao
     */
    public boolean checkCrash(String pkgName, int pid, String fileName, int i) {
        StringBuffer strbuf = new StringBuffer();
        boolean flag;

        String index = String.format("%03d", i);
        strbuf.append("index:" + index);
        strbuf.append("\t").append(TimeUtil.getTime());

        long availMemory = mBlackBox.getAvailMemory(mContext);
        strbuf.append("\tAvailMemory:").append(availMemory);
        Log.v(TAG, "getAvailMemory : " + availMemory);

        AppEntity appEntity = mBlackBox.getAndroidProcess(pkgName);
        strbuf.append("\tAppMemory:").append(appEntity.getMemorySize());
        Log.v(TAG, "AppMemory : " + new DecimalFormat("0.00").format(appEntity.getMemorySize()));

        int newPid = appEntity.getPid();
        Log.v(TAG, "newPid : " + newPid);

        if (newPid == pid) {
            strbuf.append("\tPID:").append(newPid);
            Log.d(TAG, "checkCrash: " + pkgName + " pid: " + newPid + " is alive.");
            flag = true;
        } else {
            strbuf.append("\t").append("-----Error-----PID:" + pid + "->" + newPid + "-----maybe crashed!-----");
            Log.e(TAG, "checkCrash: " + pkgName + " may be crashed!");
            flag = false;
        }

        Log.v(TAG, strbuf.toString());
        TxtUtil.saveMsg("/sdcard/tencent-test/", strbuf.toString(), fileName);

        return flag;
    }

    /**
     * 查询资源文件是否存在（查询资源下载情况用）
     * by lloydgao
     *
     * @param file 目标资源文件路径
     */
    public boolean checkResExist(File file) {
        if (file.isDirectory()) {
            String[] files = file.list();
            if (files.length > 0) {
                Log.d(TAG, "Resource " + file.getPath() + "exists! PASS!");
                mPrinter.printResult("Resource " + file.getPath() + "exists!", true);
                return true;
            }
        }
        Log.d(TAG, "Resource " + file.getPath() + "NOT exist! FAILED!");
        mPrinter.printResult("Resource " + file.getPath() + "NOT exists!", false);
        return false;
    }
}
