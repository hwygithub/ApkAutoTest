package com.tencent.apk_auto_test.ext.node;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.tencent.apk_auto_test.data.StaticData;

/**
 * This class process events that we receiver such as AccessibilityEvents and
 * send boardcast.
 */
public class NodeEventService extends AccessibilityService {

    private static final String TAG = "AccessibilityService";

    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    public void onInterrupt() {
    }

    /**
     * 初始化accessibility info
     */
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, TAG + " is onConnected.");
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
        StaticData.nodeEventService = this;
        Log.v(TAG, TAG + " is OnCreate.");
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, TAG + " is OnDestory.");
    }

    @SuppressLint("NewApi")
    public AccessibilityNodeInfo getRootInActiveWindow() {
        return super.getRootInActiveWindow();
    }


}
