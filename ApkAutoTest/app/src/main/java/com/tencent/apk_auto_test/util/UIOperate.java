package com.tencent.apk_auto_test.util;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.WallpaperInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodInfo;

import com.android.internal.statusbar.IStatusBarService;
import com.test.function.Operate;

/**
 * System.nanoTime()/1000000L The InteractionProvider is responsible for
 * injecting user events such as touch events (includes swipes) and text key
 * events into the system. To do so, all it needs to know about are coordinates
 * of the touch events and text for the text input events. The
 * InteractionController performs no synchronization. It will fire touch and
 * text input events as fast as it receives them. All idle synchronization is
 * performed prior to querying the hierarchy. See
 */
public class UIOperate {

    private static final String LOG_TAG = UIOperate.class.getSimpleName();

    private static final boolean DEBUG = true;

    private final long mLongPressTimeout;

    private Context mContext;
    private ActivityManager activityManager;
    private Operate mOperate;

    public UIOperate(Context ctx) {

        mContext = ctx;
        mOperate = new Operate(mContext);
        activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        mLongPressTimeout = 1000 * 2 + 100;
    }

    /**
     * Clicks at coordinates without waiting for device idle. This may be used
     * for operations that require stressing the target.
     *
     * @param x
     * @param y
     * @param waitTime
     * @return
     */
    public boolean click(float x, float y, int waitTime) {
        Log.d(LOG_TAG, "click (" + x + ", " + y + ")");
        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input tap " + x + " " + y + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            mOperate.sleep(waitTime);
            while ((line = err.readLine()) != null) {
                Log.i(LOG_TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(LOG_TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG_TAG, e.getMessage());
            return false;
        }

    }

    /**
     * Double Clicks at coordinates without waiting for device idle. This may be
     * used for operations that require stressing the target.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean doubleClick(float x, float y) {
        Log.d(LOG_TAG, "doubleClick (" + x + ", " + y + ")");

        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input tap " + x + " " + y + ";");
            dataOut.writeBytes("input tap " + x + " " + y + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            while ((line = err.readLine()) != null) {
                Log.i(LOG_TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(LOG_TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG_TAG, e.getMessage());
            return false;
        }
    }

    /**
     * Handle swipes in any direction.
     *
     * @param downX
     * @param downY
     * @param upX
     * @param upY
     * @param duration
     * @return
     */
    public boolean swipe(float downX, float downY, float upX, float upY, long duration) {
        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input swipe " + downX + " " + downY + " " + upX + " " + upY + " " + duration + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            while ((line = err.readLine()) != null) {
                Log.i(LOG_TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(LOG_TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG_TAG, e.getMessage());
            return false;
        }
    }

    public boolean sendText(String text, int waitTime) {
        if (DEBUG) {
            Log.d(LOG_TAG, "sendText (" + text + ")");
        }

        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input text " + text + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            mOperate.sleep(waitTime);
            while ((line = err.readLine()) != null) {
                Log.i(LOG_TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(LOG_TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG_TAG, e.getMessage());
            return false;
        }
    }

    public boolean sendKey(int keyCode,int waitTime) {
        int metaState = 0;
        if (DEBUG) {
            Log.d(LOG_TAG, "sendKey (" + keyCode + ", " + metaState + ")");
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input keyevent " + keyCode + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            mOperate.sleep(waitTime);
            while ((line = err.readLine()) != null) {
                Log.i(LOG_TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(LOG_TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG_TAG, e.getMessage());
            return false;
        }
    }

    public boolean sendLongKey(int keyCode, int metaState) {
        if (DEBUG) {
            Log.d(LOG_TAG, "sendLongKey (" + keyCode + ", " + metaState + ")");
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input keyevent --longpress " + keyCode + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            while ((line = err.readLine()) != null) {
                Log.i(LOG_TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(LOG_TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG_TAG, e.getMessage());
            return false;
        }
    }

    public void sleep(long time) {
        SystemClock.sleep(time);
    }

    /**
     * This method simply presses the power button if the screen is OFF else it
     * does nothing if the screen is already ON.
     *
     * @return true if the device was asleep else false
     * @throws RemoteException
     */
    public boolean wakeDevice() throws RemoteException {
        if (!isScreenOn()) {
            sendKey(KeyEvent.KEYCODE_POWER, 0);
            return true;
        }
        return false;
    }

    public Point getScreen() {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        return p;
    }

    /**
     * This method simply presses the power button if the screen is ON else it
     * does nothing if the screen is already OFF.
     *
     * @return true if the device was awake else false
     * @throws RemoteException
     */
    public boolean sleepDevice() throws RemoteException {
        if (isScreenOn()) {
            sendKey(KeyEvent.KEYCODE_POWER, 0);
            return true;
        }
        return false;
    }

    /**
     * Checks the power manager if the screen is ON
     *
     * @return true if the screen is ON else false
     * @throws RemoteException
     */
    public boolean isScreenOn() throws RemoteException {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    /**
     * 执行一个shell命令，并返回字符串值
     *
     * @param cmd           命令名称&参数组成的数组（例如：{"/system/bin/cat", "/proc/version"}）
     * @param workdirectory 命令执行路径（例如："system/bin/"）
     * @return 执行结果组成的字符串
     * @throws IOException
     */
    public static synchronized String run(String[] cmd, String workdirectory) throws IOException {
        StringBuffer result = new StringBuffer();
        try {
            // 创建操作系统进程（也可以由Runtime.exec()启动）
            // Runtime runtime = Runtime.getRuntime();
            // Process proc = runtime.exec(cmd);
            // InputStream inputstream = proc.getInputStream();
            ProcessBuilder builder = new ProcessBuilder(cmd);
            InputStream in = null;
            // 设置一个路径（绝对路径了就不一定需要）
            if (workdirectory != null) {
                // 设置工作目录（同上）
                builder.directory(new File(workdirectory));
                // 合并标准错误和标准输出
                builder.redirectErrorStream(true);
                // 启动一个新进程
                Process process = builder.start();
                // 读取进程标准输出流
                in = process.getInputStream();
                byte[] re = new byte[1024];
                while (in.read(re) != -1) {
                    result = result.append(new String(re));
                }
            }
            // 关闭输入流
            if (in != null) {
                in.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result.toString();
    }

    public void sendMenuKeyLong() {
        final long token = Binder.clearCallingIdentity();

        IStatusBarService statusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        try {
            statusBarService.toggleRecentApps();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error toggling recent apps.");
        }
        Binder.restoreCallingIdentity(token);
    }

    public void openRecents() {
        final long token = Binder.clearCallingIdentity();

        IStatusBarService statusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        try {
            statusBarService.toggleRecentApps();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error toggling recent apps.");
        }
        Binder.restoreCallingIdentity(token);
    }

    /**
     * Touches down for a long press at the specified coordinates.
     *
     * @param x
     * @param y
     * @param time press time
     * @return true if successful.
     */
    public boolean longTap(float x, float y, int time) {
        Log.d(LOG_TAG, "longTap (" + x + ", " + y + ")");
        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input swipe " + x + " " + y + " " + (x + 1) + " " + (y + 1) + " " + mLongPressTimeout
                    + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            while ((line = err.readLine()) != null) {
                Log.i(LOG_TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(LOG_TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG_TAG, e.getMessage());
            return false;
        }
    }

    @SuppressLint("NewApi")
    public void sendKeySync(KeyEvent event) {
        long downTime = event.getDownTime();
        long eventTime = event.getEventTime();
        int action = event.getAction();
        int code = event.getKeyCode();
        int repeatCount = event.getRepeatCount();
        int metaState = event.getMetaState();
        int deviceId = event.getDeviceId();
        int scancode = event.getScanCode();
        int source = event.getSource();
        int flags = event.getFlags();
        if (source == InputDevice.SOURCE_UNKNOWN) {
            source = InputDevice.SOURCE_KEYBOARD;
        }
        if (eventTime == 0) {
            eventTime = SystemClock.uptimeMillis();
        }
        if (downTime == 0) {
            downTime = eventTime;
        }
        KeyEvent newEvent = new KeyEvent(downTime, eventTime, action, code, repeatCount, metaState, deviceId, scancode,
                flags | KeyEvent.FLAG_FROM_SYSTEM, source);
        //InputManager.getInstance().injectInputEvent(newEvent, InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
    }


    public String getCurrentPackage() {
        List<RunningAppProcessInfo> appProcess = activityManager.getRunningAppProcesses();
        if (appProcess == null) {
            return null;
        }
        String packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        return packageName;
    }

    /**
     * 打开应用
     * <p>
     * eg: jsonObject.toString():
     * </p>
     * <p>
     * {"Function_Name":"Open_App","Package_Name":"com.android.contacts"}
     * </p>
     *
     * @param
     * @return
     * @throws JSONException
     */
    public boolean openApp(JSONObject jsonObject) throws JSONException {
        String pkgName = jsonObject.getString("Package_Name");
        Intent testActivity = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
        mContext.startActivity(testActivity);
        SystemClock.sleep(3000);
        return true;
    }

    /**
     * 打开应用
     * <p>
     * eg: jsonObject.toString():
     * </p>
     * <p>
     * {"Function_Name":"Open_App_Activity","Package_Name":
     * "com.android.contacts",
     * "Activity_Name":"com.android.contacts.activities.DialtactsActivity"}
     * </p>
     *
     * @param
     * @return
     * @throws JSONException
     */
    public boolean openAppActivity(JSONObject jsonObject) throws JSONException {
        String pkgName = jsonObject.getString("Package_Name");
        String className = jsonObject.getString("Activity_Name");
        Intent testActivity = new Intent();
        testActivity.setClassName(pkgName, className);
        testActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        testActivity.addCategory(Intent.CATEGORY_LAUNCHER);
        testActivity.setAction(Intent.ACTION_MAIN);
        SystemClock.sleep(1000);
        mContext.startActivity(testActivity);
        return true;
    }

    /**
     * 根据指定action，打开指定activity
     * <p>
     * eg: jsonObject.toString():
     * </p>
     * <p>
     * {"Function_Name":"Open_App_Action","Action_Name":
     * "android.settings.WIFI_SETTINGS"}
     * </p>
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public boolean openAppAction(JSONObject jsonObject) throws JSONException {
        String action = jsonObject.getString("Action_Name");
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        SystemClock.sleep(2000);

        return true;
    }

    /**
     * For apptest ColorOS
     *
     * @param delayTime
     * @param pkgName   void
     */
    public void killPackage(int delayTime, String pkgName) {

        SystemClock.sleep(delayTime);
        // sendKey(KeyEvent.KEYCODE_BACK, 0);
        sendKey(KeyEvent.KEYCODE_HOME, 0);
        SystemClock.sleep(1000);
        Intent intent = new Intent("apptest.intent.action.REQUEST_APP_CLEAN_RUNNING");
        Bundle b = new Bundle();
        b.putString("KillAppFilterName", "com.apptest.PhenixTestServer");
        intent.putExtras(b);
        mContext.startService(intent);
        SystemClock.sleep(1000);
    }

    public boolean isAppRunning(String targetPackage) {
        List<RunningAppProcessInfo> appProcessList = activityManager.getRunningAppProcesses();
        if (appProcessList == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
            String[] pkgNameList = appProcess.pkgList;
            for (int i = 0; i < pkgNameList.length; i++) {
                String pkgName = pkgNameList[i];
                if (pkgName.equals(targetPackage))
                    return true;
            }
        }
        return false;
    }

    public ArrayList<RecentTaskInfo> getRecentRunningAppList() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        final ArrayList<RecentTaskInfo> taskList = (ArrayList<RecentTaskInfo>) am.getRecentTasks(21,
                ActivityManager.RECENT_IGNORE_UNAVAILABLE);

        return taskList;
    }

    public List<ActivityManager.RunningAppProcessInfo> getRunningAppList(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procList = am.getRunningAppProcesses();
        return procList;
    }

    /**
     * For apptest ColorOS
     *
     * @param packageName
     * @return boolean
     */
    public boolean readLockFlag(String packageName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("package_name", packageName);

        if (mContext.getContentResolver().update(Uri.parse("content://com.android.systemui.applock"), contentValues,
                null, null) == 1) {
            return true;
        } else {
            return false;
        }
    }

    public long getAvailMemory() {
        IActivityManager am = ActivityManagerNative.getDefault();
        MemoryInfo mi = new MemoryInfo();
        try {
            am.getMemoryInfo(mi);
        } catch (RemoteException e) {
        }
        return mi.availMem;
    }

    public List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = mContext.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
            Log.e(LOG_TAG, "getHomes packageName =" + ri.activityInfo.packageName);
        }

        return names;
    }

    /**
     * For apptest ColorOS
     *
     * @return List<String>
     */
    public List<String> getLocks() {
        Log.e(LOG_TAG, "getLocks start");

        List<String> names = new ArrayList<String>();

        List<ResolveInfo> resolveInfo = mContext.getPackageManager().queryIntentServices(
                new Intent("apptest.intent.action.keyguard"), PackageManager.GET_META_DATA);

        Log.e(LOG_TAG, "getLocks resolveInfo = " + resolveInfo);

        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.serviceInfo.packageName);
            Log.e(LOG_TAG, "getLocks packageName =" + ri.serviceInfo.packageName);
        }

        return names;
    }

    /**
     * For apptest ColorOS
     *
     * @param context
     * @return Boolean
     */
    @SuppressWarnings("unused")
    private Boolean getSavedBootCompletedAppListValue(Context context) {
        SharedPreferences sp = context.getSharedPreferences("boot_completed_app_list", Context.MODE_PRIVATE);
        return sp.getBoolean("SavedBootCompletedAppList", false);
    }

    /**
     * For apptest ColorOS
     *
     * @param context
     * @param string
     * @return Boolean
     */
    @SuppressWarnings("unused")
    private Boolean getBooleanValue(Context context, String string) {
        SharedPreferences sp = context.getSharedPreferences("boot_completed_app_list", Context.MODE_PRIVATE);
        return sp.getBoolean(string, false);

    }

    public boolean findInputMethods(String string) {
        List<ResolveInfo> list = mContext.getPackageManager().queryIntentServices(
                new Intent(InputMethod.SERVICE_INTERFACE), PackageManager.GET_META_DATA);

        int listSize = list.size();

        for (int i = 0; i < listSize; i++) {
            ResolveInfo resolveInfo = list.get(i);
            InputMethodInfo info;
            try {
                info = new InputMethodInfo(mContext, resolveInfo);
            } catch (XmlPullParserException e) {
                continue;
            } catch (IOException e) {
                continue;
            }

            String packageName = info.getPackageName();

            if (packageName.equals(string)) {
                return true;
            }
        }
        return false;
    }

    public boolean findLiveWallpapers(String string) {
        List<ResolveInfo> list = mContext.getPackageManager().queryIntentServices(
                new Intent(WallpaperService.SERVICE_INTERFACE), PackageManager.GET_META_DATA);

        int listSize = list.size();

        for (int i = 0; i < listSize; i++) {
            ResolveInfo resolveInfo = list.get(i);
            WallpaperInfo info;
            try {
                info = new WallpaperInfo(mContext, resolveInfo);
            } catch (XmlPullParserException e) {
                continue;
            } catch (IOException e) {
                continue;
            }

            String packageName = info.getPackageName();

            if (packageName.equals(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解压缩功能. 将zipFile文件解压到folderPath目录下.
     *
     * @param zipFile    解压文件
     * @param folderPath 解压到路径
     * @return
     * @throws ZipException
     * @throws IOException  int
     */
    public int upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = zList.nextElement();
            if (ze.isDirectory()) {
                Log.d("upZipFile", "ze.getName() = " + ze.getName());
                String dirstr = folderPath + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d("upZipFile", "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        Log.d("upZipFile", "finish.");
        return 0;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    // substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d("upZipFile", "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                // substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            ret = new File(ret, substr);
            Log.d("upZipFile", "2ret = " + ret);
            return ret;
        }
        return ret;
    }

    /**
     * 功能:文件复制，并删除本地文件
     *
     * @param from
     * @param to
     * @throws Exception void
     */
    public void cut_File(String from, String to) {
        try {
            Process p = Runtime.getRuntime().exec("/system/bin/su");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            String cmd = "cp -r -f " + from + " " + to;
            writer.write(cmd);
            writer.flush();
            SystemClock.sleep(4000);
            writer.close();
            p.destroy();
            Process p2 = Runtime.getRuntime().exec("/system/bin/su");
            BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(p2.getOutputStream()));
            String cmd2 = "rm -r " + from;
            writer2.write(cmd2);
            writer2.flush();
            SystemClock.sleep(4000);
            writer2.close();
            p2.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAllFilesOfDir(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFilesOfDir(files[i]);
        }
        path.delete();
    }

    /**
     * For apptest ColorOS
     *
     * @return boolean
     */
    public static boolean isExViersion() {
        final String Default_Region = "CN";
        String region = SystemProperties.get("persist.sys.apptest.region", Default_Region);
        return !region.equals(Default_Region);
    }

}
