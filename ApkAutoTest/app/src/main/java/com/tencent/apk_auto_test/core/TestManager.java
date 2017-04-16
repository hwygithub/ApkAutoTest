package com.tencent.apk_auto_test.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.tencent.apk_auto_test.MainActivity;
import com.tencent.apk_auto_test.runner.CmShowAutoRunner;
import com.tencent.apk_auto_test.runner.CmShowDataRunner;
import com.tencent.apk_auto_test.runner.CmShowMemRunner;
import com.tencent.apk_auto_test.util.ExecUtil;
import com.tencent.apk_auto_test.util.Function;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by veehou on 2017/4/16.22:46
 */

public class TestManager {
    private static final String TAG = "TestManager";
    private Context mContext;

    public TestManager(Context context) {
        context = mContext;
    }

    /**
     * 检测测试环境
     *
     * @return
     */
    public boolean checkEnvironment() {
        //检查是否被root
        if (!ExecUtil.isRoot()) {
            Toast.makeText(mContext, "设备占未root，无法开始测试！！", Toast.LENGTH_LONG).show();
            return false;
        }
        //检测手Q是否是debug版本
        if (!isAppDebug("com.tencent.mobileqq")) {
            //提示
            Toast.makeText(mContext, "被测试app手Q版本需要使用debug版本！", Toast.LENGTH_LONG).show();
            return false;
        }
        //检查accessibility service是否开启
        if (!Function.isAccessibilitySettingsOn(mContext)) {
            //如果没有获取到系统的设置信息，跳转手动开启界面
            mContext.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            //提示
            Toast.makeText(mContext, "依赖服务尚未开启，保持辅助服务APK自动测试工具为开启状态！！！", Toast.LENGTH_LONG).show();
            return false;
        }
        //检查输入法是否开启成测试工具
        if (!Function.isInputMethodSettingsEnabled(mContext)) {
            //跳转到输入法选择界面
            mContext.startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            //提示
            Toast.makeText(mContext, "测试输入法尚未开启，保持输入法中APK自动测试工具被勾选中", Toast.LENGTH_LONG).show();
            return false;
        }

        //检查输入法是否默认为测试工具
        if (!Function.isInputMethodSettingsDefault(mContext)) {
            //监听如果是从输入法切换后回到工具界面时弹出输入法选择框
            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
            //提示
            Toast.makeText(mContext, "测试输入法尚未开启，选中输入法为APK自动测试工具，可能对实际输入有影响！！！", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isAppDebug(String name) {
        List<ApplicationInfo> allApps = mContext.getPackageManager()
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo app : allApps) {
            String appName = app.packageName;
            if ((app.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
                if (appName.contains(name)) {
                    Log.v(TAG, "mqq is debuggable");
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * 开始测试
     *
     * @param runnerIndex
     */
    public void startTest(int runnerIndex) {
        switch (runnerIndex) {
            case 0:
                mContext.startService(new Intent(mContext, CmShowAutoRunner.class));
                break;
            case 1:
                mContext.startService(new Intent(mContext, CmShowMemRunner.class));
                break;
            case 2:
                mContext.startService(new Intent(mContext, CmShowDataRunner.class));
                break;
        }
        Toast.makeText(mContext, "开始测试", Toast.LENGTH_LONG).show();

    }

    public void stopTest() {

    }


}
