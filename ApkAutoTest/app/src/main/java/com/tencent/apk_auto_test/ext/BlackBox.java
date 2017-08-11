package com.tencent.apk_auto_test.ext;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.tencent.apk_auto_test.data.RunPara;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.data.TestCase;
import com.tencent.apk_auto_test.ext.input.InputService;
import com.tencent.apk_auto_test.ext.temp.AppEntity;
import com.tencent.apk_auto_test.util.ParserUtil;
import com.tencent.apk_auto_test.util.ProcessUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return mi.availMem / 1024 / 1024;
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
     * 获取指定包的pid
     *
     * @param pkgName 目标进程包的名字
     * @return pid or 没有指定进程时返回null
     */
    public String getPID(String pkgName) {
        String pid = null;
        String pattern = "(\\d+)(\\s\\S*encent\\.mobileqq)";
        Pattern r = Pattern.compile(pattern);

        //使用pgrep命令查找指定包的PID时，需要输入包名最后的15个字符
        if (pkgName.length() > 15)
            pkgName = pkgName.substring(pkgName.length() - 15);

        try {
            ProcessUtil.execute("pgrep -l " + pkgName + "\n");
            if (ProcessUtil.firstLine != null) {
                Matcher m = r.matcher(ProcessUtil.firstLine);
                if (m.find())
                    pid = m.group(1);
                else
                    Log.e(TAG, "getPID: match failed!");
            } else
                Log.e(TAG, "getPID: Nothing received!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return pid;
    }


    /**
     * 5.0系统以上获取运行的进程方法
     *
     * @param packageName 目标包名
     * @return
     */
    public AppEntity getAndroidProcess(String packageName) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<AndroidAppProcess> listInfo = ProcessManager.getRunningAppProcesses();
            if (listInfo.isEmpty() || listInfo.size() == 0) {
                return null;
            }
            for (AndroidAppProcess info : listInfo) {
                ApplicationInfo app = getApplicationInfo(info.name);
                // 过滤自己当前的应用
                if (app == null || mContext.getPackageName().equals(app.packageName)) {
                    continue;
                }
                // 过滤系统的应用
                if ((app.flags & app.FLAG_SYSTEM) > 0) {
                    continue;
                }
                if (app.packageName.equals(packageName)) {
                    AppEntity ent = new AppEntity();
                    // 计算应用所占内存大小
                    int[] myMempid = new int[]{info.pid};
                    Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
                    double memSize = memoryInfo[0].dalvikPrivateDirty / 1024.0;

                    ent.setMemorySize(memSize);//应用所占内存的大小
                    ent.setPid(info.pid);

                    return ent;
                }
            }
        } else {
            List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runprocessInfo : appProcesses) {
                if (runprocessInfo.processName.equals(packageName)) {
                    AppEntity ent = new AppEntity();

                    //获取到当前进程的pid      // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
                    int pid = runprocessInfo.pid;
                    int[] myMempid = new int[]{pid};
                    //获取到内存的基本信息
                    Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
                    //Debug.MemoryInfo memoryInfo = activityManager.ge;
                    //getTotalPrivateDirty()返回的值单位是KB，所以我们要换算成MB，也就是乘以1024
                    int totalPrivateDirty = memoryInfo[0].dalvikPrivateDirty / 1024;

                    ent.setMemorySize(totalPrivateDirty);//应用所占内存的大小
                    ent.setPid(runprocessInfo.pid);

                    return ent;
                }

            }
        }
        return null;
    }

    /**
     * 通过包名返回一个应用的Application对象
     *
     * @param pkgName packageName of APK
     * @return ApplicationInfo
     */

    private ApplicationInfo getApplicationInfo(String pkgName) {
        List<ApplicationInfo> appList;
        // 通过包管理器，检索所有的应用程序
        PackageManager pm = mContext.getPackageManager();
        appList = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        if (pkgName == null) {
            return null;
        }
        for (ApplicationInfo appinfo : appList) {
            if (pkgName.equals(appinfo.processName)) {
                return appinfo;
            }
        }
        return null;
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
        File file = new File(copyPath);
        if (!file.exists()) {
            return;
        }
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
