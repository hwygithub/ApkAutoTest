package com.tencent.apk_auto_test.ext;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Debug;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.apk_auto_test.core.TestMonitor;
import com.tencent.apk_auto_test.core.TestResultPrinter;
import com.tencent.apk_auto_test.data.RunPara;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.data.TestCase;
import com.tencent.apk_auto_test.ext.input.InputService;
import com.tencent.apk_auto_test.util.ParserUtil;
import com.tencent.apk_auto_test.util.ProcessUtil;
import com.tencent.apk_auto_test.util.TimeUtil;
import com.tencent.apk_auto_test.util.TxtUtil;

import junit.framework.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BlackBox {
    private static final String TAG = "BlackBox";
    private static final String NAME = "com.tencent.apk_auto_test/com.tencent.apk_auto_test.ext.node.NodeEventService";
    private static final String METHOD_NAME = "com.tencent.apk_auto_test/.ext.input.TestInputMethodService";

    // class
    private Context mContext;
    private UIActionBox uiActionBox;


    public BlackBox(Context context) {
        mContext = context;
        uiActionBox = new UIActionBox(context);
    }

    public void changeSerial2Array(String[] serial) {
        StaticData.runList = new ArrayList<RunPara>();
        if (null != serial) {
            for (String s : serial) {
                try {
                    RunPara mPara = new RunPara();
                    mPara.runCaseName = getCaseName(s.split("-")[0]);
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
        return StaticData.chooseListText[Integer.parseInt(caseNumber)];
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

        uiActionBox.sleep(delayTime);
        return b1 && b2;
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
            ParserUtil parser = new ParserUtil();
            list = parser.parse(inputStream);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    /**
     * 存储目标进程的进程信息
     *
     * @param pkgName  目标进程包的名字
     * @param fileName 存储文件的名字
     *                 by lloydgao
     */
    public boolean saveProcInfo(String pkgName, String fileName, int i) {
        String device = Build.BRAND;
        String info = null;
        StringBuffer strbuf = new StringBuffer();
        boolean flag;

        Log.v(TAG, "getProcInfo of " + pkgName);
        String index = String.format("%03d", i);
        strbuf.append("index: " + index);
        strbuf.append("\t").append(TimeUtil.getTime());

        if (device.equalsIgnoreCase("xiaomi")) {
            //TODO: 不能取得所有进程信息，待修改
            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> procInfo = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo tmpInfo : procInfo) {
                Log.d(TAG, "ProcessList: " + tmpInfo.processName);
                if (tmpInfo.processName.equalsIgnoreCase("com.tencent.mobileqq")) {
                    Integer pid = tmpInfo.pid;
                    info = TestMonitor.getProcInfo(pid.toString(), TestMonitor.psInfoType.ALL);
                    break;
                }
            }
        } else {
            info = TestMonitor.getProcInfo(pkgName, TestMonitor.psInfoType.ALL);
        }

        if (info == null) {
            strbuf.append("\t").append("----------------Error-----------------");
            Log.e(TAG, "saveProcInfo: getProcessInfo failed!");
            flag = false;
        } else {
            strbuf.append("\t").append(info);
            Log.d(TAG, "saveProcInfo: getProcessInfo successful!");
            flag = true;
        }

        Log.v(TAG, strbuf.toString());
        TxtUtil.saveMsg("/sdcard/tencent-test/", strbuf.toString(), fileName);

        return flag;
    }

    public boolean saveMem(String packageName, String fileName, int i) {
        StringBuffer strbuf = new StringBuffer();

        long availMemory = getAvailMemory(mContext);
        Log.v(TAG, "getAvailMemory : " + availMemory);
        String index = String.format("%03d", i);
        strbuf.append("index:" + index);
        strbuf.append("\t").append(TimeUtil.getTime());
        strbuf.append("\t").append(getAvailMemory(mContext));
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfo = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : procInfo) {
            System.out.println(runningAppProcessInfo.processName + String.format(",pid = %d", runningAppProcessInfo.pid));
            if (runningAppProcessInfo.processName.indexOf(packageName) != -1) {
                int pids[] = {runningAppProcessInfo.pid};
                Debug.MemoryInfo self_mi[] = am.getProcessMemoryInfo(pids);

                strbuf.append("\t").append(runningAppProcessInfo.processName)
                        .append("\t").append(runningAppProcessInfo.pid)
                        /*.append("\n dalvikPrivateDirty:").append(self_mi[0].dalvikPrivateDirty)*/
                        .append("\t dalvikPss:").append(self_mi[0].dalvikPss)
                        /*.append("\n dalvikSharedDirty:").append(self_mi[0].dalvikSharedDirty)
                        .append("\n nativePrivateDirty:").append(self_mi[0].nativePrivateDirty)
                        .append("\t nativePss:").append(self_mi[0].nativePss)
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


    /**
     * 递归删除文件夹和其中的文件
     *
     * @param file
     */
    public void delFolder(File file) {
        if (file.isFile()) {
            boolean isDeleted = file.delete();
            if (isDeleted == false)
                Log.e(TAG, "delete " + file.getName() + " failed");
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                boolean isDeleted = file.delete();
                if (isDeleted == false)
                    Log.e(TAG, "delete " + file.getName() + " failed");
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delFolder(childFiles[i]);
            }
            boolean isDeleted = file.delete();
            if (isDeleted == false)
                Log.e(TAG, "delete " + file.getName() + " failed");
        }
    }

    /**
     * 复制asset文件到手机目录
     */
    public void copyAssetsFile(String assetsPath, String copyPath) throws IOException {
        InputStream inputStream = mContext.getAssets().open(assetsPath);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(copyPath));
        byte[] buffer = new byte[1024];
        int byteCount = 0;
        while ((byteCount = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, byteCount);
        }
        fileOutputStream.flush();
        inputStream.close();
        fileOutputStream.close();
    }

    public void clearAppByPackageName(String packageName) {
        ProcessUtil.clear(packageName);
    }

    // 杀死其他应用
    public void killAppByPackageName(String packageName) {
        //adb shell force-stop
        ProcessUtil.kill(packageName);
    }

}
