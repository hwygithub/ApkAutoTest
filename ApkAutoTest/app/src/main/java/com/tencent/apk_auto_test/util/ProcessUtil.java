package com.tencent.apk_auto_test.util;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by veehou on 2016/11/15.22:44
 */

public class ProcessUtil {
    private static final String TAG = "ProcessUtil";
    private static Process process;
    private static byte[] tempBuffer;
    private static StringBuilder buffer;
    public static String firstLine = null;


    public static void execute(final String command) throws IOException, InterruptedException {
        initProcess();

        DataOutputStream dataOut = new DataOutputStream(process.getOutputStream());
        dataOut.writeBytes(command);
        dataOut.flush();
        dataOut.close();

        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
        errorGobbler.start();
        outputGobbler.start();
        process.waitFor();

        close();
    }

    private static class StreamGobbler extends Thread {
        InputStream inputStream;
        String type;

        StreamGobbler(InputStream inputStream, String type) {
            this.inputStream = inputStream;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                int cnt = 0;
                while ((line = bufferedReader.readLine()) != null) {

                    if (cnt == 0) firstLine = line;

                    if (type.equals("Error")) {
                        Log.e(TAG, line);
                    } else {
                        Log.i(TAG, line);
                    }
                    cnt++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
        initProcess();
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
        initProcess();
        Log.i(TAG, "start capture screen..." + capPath);
        try {
            OutputStream out = process.getOutputStream();
            out.write(("/system/bin/screencap -p " + capPath).getBytes());
            out.flush();
            out.close();
            process.waitFor();
            Log.i(TAG, "end capture screen");
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*************私有方法**************
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
