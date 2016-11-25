package com.tencent.apk_auto_test.services;

import java.util.Set;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.tencent.apk_auto_test.util.UINodeOperate;

/**
 * This class process events that we receiver such as AccessibilityEvents and
 * send boardcast.
 */
public class AccessibilityEventService extends AccessibilityService {

    private static final String TAG = AccessibilityEventService.class.getSimpleName();
    private Context mContext;
    private UINodeOperate mNodeOperate;


    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    public void onInterrupt() {
    }

    /**
     * 初始化accessibility info
     */
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.v(TAG, TAG + " is onConnected.");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT
                | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
                | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
                | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mNodeOperate = new UINodeOperate(mContext);
        openService(NAME);
        accessibilityEventService = this;
        Log.v(TAG, TAG + " is OnCreate.");
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, TAG + " is OnDestory.");
    }

    private static AccessibilityEventService accessibilityEventService;

    @SuppressLint("NewApi")
    public AccessibilityNodeInfo getRootInActiveWindow() {
        return super.getRootInActiveWindow();
    }

    public static AccessibilityEventService getService() {
        return accessibilityEventService;
    }

    private static final String NAME = "com.tencent.apk_auto_test/com.tencent.apk_auto_test.services.AccessibilityEventService";
    private static final char ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR = ':';

    public void openService(String preferenceKey) {
        Log.e(TAG, "accessibility openService start");
        // Parse the enabled services.
        Set<ComponentName> enabledServices = mNodeOperate
                .getEnabledServicesFromSettings(mContext);

        // Determine enabled services and accessibility state.
        ComponentName toggledService = ComponentName
                .unflattenFromString(preferenceKey);
        final boolean accessibilityEnabled;
        if (true) {
            // Enabling at least one service enables accessibility.
            accessibilityEnabled = true;
            enabledServices.add(toggledService);
        }

        // Update the enabled services setting.
        StringBuilder enabledServicesBuilder = new StringBuilder();
        // Keep the enabled services even if they are not installed since we
        // have no way to know whether the application restore process has
        // completed. In general the system should be responsible for the
        // clean up not settings.
        for (ComponentName enabledService : enabledServices) {
            Log.e(TAG, "enabledService:" + enabledService.toString());
            enabledServicesBuilder.append(enabledService.flattenToString());
            enabledServicesBuilder
                    .append(ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR);
        }
        final int enabledServicesBuilderLength = enabledServicesBuilder
                .length();
        if (enabledServicesBuilderLength > 0) {
            enabledServicesBuilder
                    .deleteCharAt(enabledServicesBuilderLength - 1);
        }
        // Settings.Secure.putString(mContext.getContentResolver(),
        // Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        // enabledServicesBuilder.toString());
        //
        // // Update accessibility enabled.
        // Settings.Secure.putInt(mContext.getContentResolver(),
        // Settings.Secure.ACCESSIBILITY_ENABLED, accessibilityEnabled ? 1
        // : 0);
        Log.e(TAG, " Accessibility openService end");
    }
    // open AccessibilityEventService end.

}
