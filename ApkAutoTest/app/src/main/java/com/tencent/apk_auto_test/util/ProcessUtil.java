package com.tencent.apk_auto_test.util;


import android.util.Log;

import org.apache.poi.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

/**
 * Created by veehou on 2016/11/15.22:44
 */

public class ProcessUtil {
    private static final String TAG = "ProcessUtil";
    private static Process process;
    private static byte[] tempBuffer;
    private static StringBuilder buffer;


    public static ProcessStatus execute(final long timeout, final String... command) throws IOException,
            TimeoutException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        Worker worker = new Worker(process);
        worker.start();
        ProcessStatus processStatus = worker.getProcessStatus();
        try {
            worker.join(timeout);
            if (processStatus.exitCode == ProcessStatus.CODE_STARTED) {
                worker.interrupt();
                throw new TimeoutException();
            } else {
                return processStatus;
            }
        } catch (InterruptedException e) {
            worker.interrupt();
            throw e;
        } finally {
            process.destroy();
        }
    }

    private static class Worker extends Thread {
        private final Process process;
        private ProcessStatus processStatus;

        private Worker(Process process) {
            this.process = process;
            this.processStatus = new ProcessStatus();
        }

        public void run() {
            try {
                InputStream inputStream = process.getInputStream();
                try {
                    processStatus.output = IOUtils.toByteArray(inputStream);
                } catch (IOException ignore) {
                }
                processStatus.exitCode = process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public ProcessStatus getProcessStatus() {
            return this.processStatus;
        }

    }

    public static class ProcessStatus {
        public static final int CODE_STARTED = -257;
        public volatile int exitCode;
        public volatile byte[] output;
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
