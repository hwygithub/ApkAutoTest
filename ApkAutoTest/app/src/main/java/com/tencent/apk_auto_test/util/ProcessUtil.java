package com.tencent.apk_auto_test.util;


import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by veehou on 2016/11/15.22:44
 */

public class ProcessUtil {
    private static final String TAG = "ProcessUtil";
    private static Process process;
    private static byte[] tempBuffer;
    private static StringBuilder buffer;


    /**
     * 结束进程,执行操作调用即可
     */
    public static void kill(String packageName) {
        initProcess();
        OutputStream out = process.getOutputStream();
        String cmd = "am force-stop " + packageName + " \n";
        try {
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }

    /**
     * 清除数据,执行操作调用即可
     */
    public static void clear(String packageName) {
        initProcess();
        OutputStream out = process.getOutputStream();
        String cmd = "pm clear " + packageName + " \n";
        try {
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }

    /**
     * 查看是否root
     *
     * @return
     */
    public static boolean isRoot() {
        try {
            process = Runtime.getRuntime().exec("su");
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            int i = process.waitFor();
            if (0 == i) {
                process = Runtime.getRuntime().exec("su");
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;

    }

    /**
     * 调用系统截图工具获取屏幕截图字节数组，格式为png，注意这是一个耗时操作，约为1-5秒。
     * 如果屏幕分辨率过高，防止内存溢出，可以考虑直接保存到文件的命令u -c /system/bin/screencap -p /sdcard/screenshot.png
     *
     * @return png格式图片的字节数组
     */
    public static void getScreenCap(String capPath) {
        Log.i(TAG, "start capture screen..." + capPath);
        try {
            process = Runtime.getRuntime().exec("su -c /system/bin/screencap -p " + capPath);
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            process.waitFor();
            Log.i(TAG, "end capture screen");
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*************私有方法**************/
    /**
     * 初始化进程
     */
    private static void initProcess() {
        if (process == null)
            try {
                process = Runtime.getRuntime().exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * 关闭输出流
     */
    private static void close() {
        if (process != null)
            try {
                process.getOutputStream().close();
                process = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}
