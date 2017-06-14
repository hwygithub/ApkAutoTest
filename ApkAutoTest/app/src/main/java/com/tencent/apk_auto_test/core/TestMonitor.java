package com.tencent.apk_auto_test.core;

import android.util.Log;

import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.ext.UIImageActionBox;
import com.tencent.apk_auto_test.ext.UINodeActionBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by veehou on 2017/4/16.23:00
 * Modified by lloydgao on 2017/05/25.16:49
 */

public class TestMonitor {
    private static final String TAG = "TestMonitor";
    private TestResultPrinter mPrinter;
    private UINodeActionBox mNodeBox;
    private UIImageActionBox mImageBox;

    public enum psInfoType {
        ALL, USER, PID, PPID, VSIZE, RSS, WCHAN, PC, NAME
    }

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
     * 调用ps命令获取某个特定包的进程信息
     *
     * @param target 目标包名或者PID
     * @param type   所需的参数(枚举类型)
     * @return 所需的信息
     * by lloydgao
     */
    public static String getProcInfo(String target, psInfoType type) {

        if (target.isEmpty()) {
            Log.e(TAG, "packageName is empty!");
            return null;
        }

        Process process = null;
        String psInfo = null;
        String pattern = "(\\S+)(\\s+)(\\d+)(\\s+)(\\d+)(\\s+)(\\S+)(\\s+)(\\S+)(\\s+)(\\S+)" +
                "(\\s+)(\\S+\\s\\S)(\\s+)(.+)";

        //因为非小米系统使用ps寻找指定包时，必须输入目标包名的最后15个字符，需要提前做判断
        if (target.length() > 15)
            target = target.substring(target.length() - 15);

        try {
            process = Runtime.getRuntime().exec("su");
            Log.i(TAG, "Try to execute ps command to find..." + target);

            OutputStream out = process.getOutputStream();
            out.write(("ps " + target + "\n").getBytes());
            out.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //第一行是无效信息,舍弃
            br.readLine();
            //第二行才是有用的信息
            if (br.ready()) {
                psInfo = br.readLine();
                Log.d(TAG, psInfo);

            } else {
                Log.e(TAG, target + " was not found!");
                process.getOutputStream().close();
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "getProcessInfo: Exception occurred!");
            e.printStackTrace();
        }

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(psInfo);
        int groupIndex = 0;
        if (m.find()) {
            switch (type) {
                case ALL:
                    groupIndex = 0;
                    break;
                case USER:
                    groupIndex = 1;
                    break;
                case PID:
                    groupIndex = 3;
                    break;
                case PPID:
                    groupIndex = 5;
                    break;
                case VSIZE:
                    groupIndex = 7;
                    break;
                case RSS:
                    groupIndex = 9;
                    break;
                case WCHAN:
                    groupIndex = 11;
                    break;
                case PC:
                    groupIndex = 13;
                    break;
                case NAME:
                    groupIndex = 15;
                    break;
            }
        } else {
            Log.e(TAG, "getProcessInfo: -----------Match failed!");
        }

        //关闭process
        try {
            process.getOutputStream().close();
        } catch (Exception e) {
            Log.e(TAG, "getProcessInfo: Exception occurred!");
            e.printStackTrace();
        }

        return m.group(groupIndex);
    }
}
