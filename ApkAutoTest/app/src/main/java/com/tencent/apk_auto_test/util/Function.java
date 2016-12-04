package com.tencent.apk_auto_test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tencent.apk_auto_test.data.RunPara;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.data.TestCase;
import com.tencent.apk_auto_test.input.InputService;
import com.tencent.apk_auto_test.util.Telephony.Sms;
import com.test.function.Assert;
import com.test.function.Operate;

import android.annotation.SuppressLint;
import android.app.ActivityManager.MemoryInfo;
import android.app.KeyguardManager.KeyguardLock;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class Function {
    private static final String TAG = "Function";
    protected static final int RUN_SCHEME = 5;
    protected static final Uri CONTENT_URI = Uri.parse("content://call_log/calls");
    private static final String NAME = "com.tencent.apk_auto_test/com.tencent.apk_auto_test.services.AccessibilityEventService";
    private static final String METHOD_NAME = "com.tencent.apk_auto_test/.input.TestInputMethodService";

    // class
    private Context mContext;
    private Handler mHandler;
    private Operate mOperate;
    private Assert mAssert;
    private FillSms_MmsInfo info;
    private UIOperate mUIOperate;

    public Function(Context mContext) {
        this.mContext = mContext;
        mOperate = new Operate(mContext);
        mAssert = new Assert(mContext);
        mUIOperate = new UIOperate(mContext);
    }

    public Function(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
        mOperate = new Operate(mContext);
        mAssert = new Assert(mContext);
        info = new FillSms_MmsInfo(mContext);
    }

    public void changeSerial2Array(String[] serial) {
        StaticData.runList = new ArrayList<RunPara>();
        if (null != serial) {
            for (String s : serial) {
                try {
                    RunPara mPara = new RunPara();
                    mPara.runCase = getCaseName(s.split("-")[0]);
                    mPara.runCaseNumber = Integer.parseInt(s.split("-")[0]);
                    mPara.runNumber = Integer.parseInt(s.split("-")[1]);
                    StaticData.runList.add(mPara);
                } catch (Exception e) {
                    Toast.makeText(mContext, "Testcase xml error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        StaticData.runAdapter.notifyDataSetChanged();
    }

    public String getCaseName(String caseNumber) {
        for (String s : StaticData.chooseListText) {
            String number = (String) s.split("_")[1];
            if (caseNumber.equals(number)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Clear Back
     */
    public void killAllAcitivities2() {
        Intent intent = new Intent(
                "apptest.intent.action.MY_REQUEST_APP_CLEAN_RUNNING");
        intent.putExtra("KillAppFilterName", "com.tencent.apk_auto_test");
        mContext.startService(intent);
    }


    public void add10086CallLog() {
        Runnable mRunnable = new Runnable() {
            public void run() {
                long startTime = System.currentTimeMillis();
                ContentResolver resolver = mContext.getContentResolver();
                ContentValues values = new ContentValues(6);
                int ran = (int) (Math.random() * 100);
                long number = StaticData.phoneNumber;
                int callType = (int) ((Math.random() * 100) % 3) + 1;
                int duration = ran;
                startTime -= 10000;
                values.put("number", number);
                values.put("type", callType);
                values.put("date", startTime);
                values.put("duration", Long.valueOf(duration));
                values.put("new", Integer.valueOf(1));
                resolver.insert(CONTENT_URI, values);

                StaticData.mBar.incrementProgressBy(1);
                mHandler.sendEmptyMessage(RUN_SCHEME);
            }
        };
        Thread startaddCallLogs = new Thread(mRunnable, "mRunnable");
        startaddCallLogs.start();
    }

    public void clearAllSms() {
        mContext.getContentResolver().delete(Sms.CONTENT_URI, null, null);
    }

    /**
     * 初始化输入法服务
     */
    public void initInputMethod() {
        Log.e(TAG, "inputMethod service start");
        Intent service = new Intent();
        service.setClass(mContext, InputService.class);
        mContext.startService(service);
    }

    /**
     * 输入字符
     */
    public boolean inputText(String args, int delayTime) {
        boolean b1 = false;
        boolean b2 = false;
        try {
            b1 = InputService.inputMethod.clearText();
            b2 = InputService.inputMethod.setText(args, delayTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mOperate.sleep(delayTime);
        return b1 && b2;
    }

    private KeyguardLock keyguardLock;

    public void unLockScreen() {
        PowerManager pm = (PowerManager) mContext
                .getSystemService(mContext.POWER_SERVICE);
        mOperate.sleep(300);
        if (!pm.isScreenOn()) {
            mUIOperate.sendKey(KeyEvent.KEYCODE_POWER, 0);
        }
        mOperate.sleep(3000);
        mUIOperate.swipe((float) Global.SCREEN_WIDTH / 2,
                (float) (Global.SCREEN_HEIGHT * 0.5),
                (float) Global.SCREEN_WIDTH / 2,
                (float) (Global.SCREEN_HEIGHT * 0.1), 1000);
        // send broadcast
        mContext.sendBroadcast(new Intent(
                "com.apptest.cmcc_standard_autorun.action.runtest"));
    }

    public void LockScreen() {
        PowerManager pm = (PowerManager) mContext
                .getSystemService(mContext.POWER_SERVICE);
        mOperate.sleep(200);
        if (pm.isScreenOn()) {
            mUIOperate.sendKey(KeyEvent.KEYCODE_POWER, 0);
        }
    }

    /**
     * @category STREAM_VOICE_CALL max:6
     * @category STREAM_SYSTEM max:7
     * @category STREAM_RING max:7
     * @category STREAM_MUSIC max:16
     * @category STREAM_ALARM max:7
     * @category STREAM_NOTIFICATION max:7
     * @category STREAM_FM max:15
     */
    public void setAudioVolume(int mode, int volume) {
        AudioManager am = (AudioManager) mContext
                .getSystemService(Service.AUDIO_SERVICE);
        if (volume >= 0) {
            int maxVolume = 0;
            switch (mode) {
                case AudioManager.STREAM_MUSIC:
                    maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    am.setStreamVolume(AudioManager.STREAM_MUSIC,
                            volume <= maxVolume ? volume : maxVolume,
                            AudioManager.FLAG_SHOW_UI);
                    break;
                case AudioManager.STREAM_RING:
                    maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_RING);
                    am.setStreamVolume(AudioManager.STREAM_RING,
                            volume <= maxVolume ? volume : maxVolume,
                            AudioManager.FLAG_SHOW_UI);
                    break;
                case AudioManager.STREAM_VOICE_CALL:
                    maxVolume = am
                            .getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                    am.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                            volume <= maxVolume ? volume : maxVolume,
                            AudioManager.FLAG_SHOW_UI);
                    break;
                case AudioManager.STREAM_NOTIFICATION:
                    maxVolume = am
                            .getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                    am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                            volume <= maxVolume ? volume : maxVolume,
                            AudioManager.FLAG_SHOW_UI);
                    break;
                case AudioManager.STREAM_ALARM:
                    maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                    am.setStreamVolume(AudioManager.STREAM_ALARM,
                            volume <= maxVolume ? volume : maxVolume,
                            AudioManager.FLAG_SHOW_UI);
                    break;
                case AudioManager.STREAM_SYSTEM:
                    maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                    am.setStreamVolume(AudioManager.STREAM_SYSTEM,
                            volume <= maxVolume ? volume : maxVolume,
                            AudioManager.FLAG_SHOW_UI);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获得媒体声音大小
     */
    public int getAudioVolume(int mode) {
        AudioManager am = (AudioManager) mContext
                .getSystemService(Service.AUDIO_SERVICE);
        return am.getStreamVolume(mode);
    }

    /**
     * 设置当前屏幕亮度0--255
     */
    public void saveScreenBrightness(int paramInt) {
        try {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }

    }

    /**
     * 获得当前屏幕亮度0--255
     */
    public int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(
                    mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    public int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
        }
        return version;
    }

    // Returns the Service Provider Name (SPN).
    public int getSimOperatorNumber() {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = tm.getSimOperator();
        if (imsi != null) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002")
                    || imsi.equals("46007")) {
                // cmcc
                // 因为移动网络编号46000下的IMSI已经用完，所以虚拟了�?��46002编号�?34/159号段使用了此编号
                // //中国移动
                return 10086;
            } else if (imsi.startsWith("46001")) {
                // unicom
                return 10010;
            } else if (imsi.startsWith("46003")) {
                // telecom
                return 10001;
            }
        }
        return 0;
    }

    // Returns the Service Provider Name (SPN).
    public String getSimOperator() {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = tm.getSimOperator();
        if (imsi != null) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002")
                    || imsi.equals("46007")) {
                // cmcc
                // 因为移动网络编号46000下的IMSI已经用完，所以虚拟了�?��46002编号�?34/159号段使用了此编号
                // //中国移动
                return "中国移动";
            } else if (imsi.startsWith("46001")) {
                // unicom
                return "中国联通";
            } else if (imsi.startsWith("46003")) {
                // telecom
                return "中国电信";
            }
        }
        return null;
    }

    // Set purebackground
    public void setPureBackgroundEnable(boolean enable) {
        File file = new File("/data/data_bpm/bpm_sts.xml");
        StringBuilder text = new StringBuilder(
                "<?xml version='1.0' encoding='utf-8' standalone='yes' ?><gs><p att=\""
                        + enable + "\" /></gs>");
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(text.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Set the float window state
    public void setFloatWindowState(boolean enable, String pkgName) {
        List<String> packageNameList = new ArrayList<String>();
        packageNameList.add(pkgName);
        if (enable) {
            // 允许悬浮框
            SetFloatWindow.setPackageNameList(SetFloatWindow.BLACK_LIST_FILE,
                    packageNameList, SetFloatWindow.FILE_MODE_DEL);
        } else {
            // 禁止悬浮框
            SetFloatWindow.setPackageNameList(SetFloatWindow.BLACK_LIST_FILE,
                    packageNameList, SetFloatWindow.FILE_MODE_SAVE);
        }
    }

    // Send sms
    public void sendSmsAPI(String sendSmsNumber, String sendText) {
        if (sendSmsNumber.equals("")) {
            sendSmsNumber = "10086";
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sendSmsNumber, null, sendText, null, null);
    }

    // Download the file of uri
    @SuppressLint("NewApi")
    public void downloadStart(String urlString) {
        // String fileName = "oppoTest.apk";
        // String filePath = "/storage/sdcard0/Download/";
        //
        // ContentValues values = new ContentValues();
        // Uri uri = Uri.parse(urlString);
        // values.put("uri", uri.toString());
        // // values.put("useragent",
        // //
        // "Mozilla/5.0 (linux; U; Android 1.5; en-us; SDK Build/CUPCAKE) AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/525.20.1");
        // values.put("notificationpackage", "com.tencent.apk_auto_test");
        // values.put("notificationclass", "MainActivity");
        // values.put("visibility", Downloads.Impl.VISIBILITY_VISIBLE);
        // // values.put("mimetype", "application/vnd.android.package-archive");
        // values.put("hint", fileName);
        // values.put("_data", filePath + fileName);
        // values.put("description", uri.getHost());
        // // values.put("total_bytes", totalBytes);
        // values.put("destination", Downloads.Impl.DESTINATION_EXTERNAL);
        // // 将其插入到DownloadManager的数据库中，数据库会触发修改事件，启动下载任务
        // Uri downloadUri =
        // mContext.getContentResolver().insert(Downloads.Impl.CONTENT_URI,
        // values);
        //
        // long id = Long.parseLong(downloadUri.getLastPathSegment());
        // return id;

        DownloadManager downloadManager = (DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);
        String apkUrl = urlString;
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(apkUrl));
        request.setDestinationInExternalPublicDir("Trinea", "MeiLiShuo.apk");
        downloadManager.enqueue(request);
    }

    // Stop Download
    @SuppressLint("NewApi")
    public void stopDownload(long id) {
        DownloadManager dm = (DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);
        dm.remove(id);
    }

    public void setChangeGprsStateConfirmMode(int mode) {
        int GprsMode = mode;
        Settings.System.putInt(mContext.getContentResolver(),
                "popup_gprs_dialog", GprsMode);
    }

    public void setChangeWifiStateConfirmMode(int mode) {
        int WlanMode = mode;
        Settings.System.putInt(mContext.getContentResolver(),
                "popup_wifi_dialog", WlanMode);
    }

    public void setSwitchToGprsStateConfirmMode(int mode) {
        int nMode = mode;
        Settings.System.putInt(mContext.getContentResolver(),
                "popup_switch_to_gprs_dialog", nMode);
    }

    public void stopPackage(String packageName) {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // Check the resouce whether exist
    public boolean isResouceExist() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + "0BatteryTestResouce";
        File file = new File(path);
        if (file.exists())
            return true;
        else
            return false;
    }

    // Parser the testcase xml
    public List<TestCase> parserXml() {
        InputStream inputStream = null;
        File xmlFile = new File(StaticData.CASE_URL);

        // If the testcase xml dismiss,read the xml from assets
        if (!xmlFile.exists()) {
            try {
                inputStream = mContext.getAssets().open("TestCase.xml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                inputStream = new FileInputStream(xmlFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            List<TestCase> list = new ArrayList<TestCase>();
            PullParser parser = new PullParser();
            list = parser.parse(inputStream);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Set the traffic alert
    private static final String IS_LEAVING_ALERT = "is_leaving_alert";
    private static final String IS_LEAVING_ALERT_SIM2 = "is_leaving_alert_sim2";
    private static final String IS_EXCEPTION_ALERT = "is_exception_alert";
    private static final String IS_EXCEPTION_ALERT_SIM2 = "is_exception_alert_sim2";
    private static final String IS_CLOSE_NETWORK_ALERT = "is_close_network_alert";
    private static final String IS_CLOSE_NETWORK_ALERT_SIM2 = "is_close_network_alert_sim2";
    public static final String IS_BEYONG_TRAFFIC_EXCEPTION_ALERT = "is_beyond_traffic_exception_alert";
    public static final String IS_BEYONG_TRAFFIC_EXCEPTION_ALERT_SIM2 = "is_beyond_traffic_exception_alert_sim2";

    public void setTrafficAlert(boolean enable, boolean mIsSim2) {
        SharedPreferences policyAlertPref = mContext.getSharedPreferences(
                "policy_alert_set", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = policyAlertPref.edit();
        if (mIsSim2) {
            editor.putBoolean(IS_LEAVING_ALERT_SIM2, enable);
            editor.putBoolean(IS_EXCEPTION_ALERT_SIM2, enable);
            editor.putBoolean(IS_CLOSE_NETWORK_ALERT_SIM2, enable);
            editor.putBoolean(IS_BEYONG_TRAFFIC_EXCEPTION_ALERT_SIM2, enable);
        } else {
            editor.putBoolean(IS_LEAVING_ALERT, enable);
            editor.putBoolean(IS_EXCEPTION_ALERT, enable);
            editor.putBoolean(IS_CLOSE_NETWORK_ALERT, enable);
            editor.putBoolean(IS_BEYONG_TRAFFIC_EXCEPTION_ALERT, enable);
        }

        editor.commit();
    }


    // 获取可用内存大小(MB)
    public long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        // return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        return mi.availMem / 1024;
    }

    // To check if service is enabled
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v(TAG, "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(NAME)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

    public static boolean isInputMethodSettingsEnabled(Context context) {
        String mLastInputMethodId = Settings.Secure.getString(context
                        .getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_INPUT_METHODS);
        Log.e(TAG, mLastInputMethodId);
        if (mLastInputMethodId.contains(METHOD_NAME)) {
            Log.v(TAG, "test input method is enabled");
            return true;
        }
        return false;
    }

    public static boolean isInputMethodSettingsDefault(Context context) {
        String mLastInputMethodId = Settings.Secure.getString(context
                        .getApplicationContext().getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        Log.e(TAG, mLastInputMethodId);
        if (METHOD_NAME.equals(mLastInputMethodId)) {
            Log.v(TAG, "test input method is enabled");
            return true;
        }
        return false;
    }

    public boolean saveMem(String packageName, String fileName) {
        StringBuffer strbuf = new StringBuffer();

        long availMemory = getAvailMemory(mContext);
        Log.v(TAG, "getAvailMemory : " + availMemory);
        strbuf.append(getAvailMemory(mContext));
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> procInfo = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : procInfo) {
            System.out.println(runningAppProcessInfo.processName + String.format(",pid = %d", runningAppProcessInfo.pid));
            if (runningAppProcessInfo.processName.indexOf(packageName) != -1) {
                int pids[] = {runningAppProcessInfo.pid};
                Debug.MemoryInfo self_mi[] = am.getProcessMemoryInfo(pids);

                strbuf.append("\t").append(runningAppProcessInfo.processName)
                        .append("\t").append(runningAppProcessInfo.pid)
                        /*.append("\n dalvikPrivateDirty:").append(self_mi[0].dalvikPrivateDirty)
                        .append("\n dalvikPss:").append(self_mi[0].dalvikPss)
                        .append("\n dalvikSharedDirty:").append(self_mi[0].dalvikSharedDirty)
                        .append("\n nativePrivateDirty:").append(self_mi[0].nativePrivateDirty)
                        .append("\n nativePss:").append(self_mi[0].nativePss)
                        .append("\n nativeSharedDirty:").append(self_mi[0].nativeSharedDirty)
                        .append("\n otherPrivateDirty:").append(self_mi[0].otherPrivateDirty)
                        .append("\n otherPss:").append(self_mi[0].otherPss)
                        .append("\n otherSharedDirty:").append(self_mi[0].otherSharedDirty)
                        .append("\n TotalPrivateDirty:").append(self_mi[0].getTotalPrivateDirty())*/
                        .append("\t").append(self_mi[0].getTotalPss());
                        /*.append("\n TotalSharedDirty:").append(self_mi[0].getTotalSharedDirty());*/

            }
            Log.v(TAG, strbuf.toString());

            TxtUtil.saveMsg("/sdcard/tencent-test/", strbuf.toString(), fileName);
            return true;
        }
        return false;
    }

    public boolean isAppDebug(String name) {
        Set<String> debuggableApps = new HashSet<String>();
        List<ApplicationInfo> allApps = mContext.getPackageManager()
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo app : allApps) {
            String appName = app.packageName;
            if ((app.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
                //debuggableApps.add(appName);
                if (appName.contains(name)) {
                    Log.v(TAG, "mqq is debuggable");
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * 递归删除文件夹和其中的文件
     *
     * @param file
     */
    public void delFolder(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delFolder(childFiles[i]);
            }
            file.delete();
        }
    }

    public void clearAppByPackageName(String packageName) {
        ExecUtil.clear(packageName);
    }

    // 杀死其他应用
    public void killAppByPackageName(String packageName) {
        //adb shell force-stop
        ExecUtil.kill(packageName);
    }
}
