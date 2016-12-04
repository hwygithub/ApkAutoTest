package com.tencent.apk_auto_test.util;


import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by veehou on 2016/11/15.22:44
 */

public class ExecUtil {
    private static final String TAG = "ExecUtil";
    private static Process process;

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
