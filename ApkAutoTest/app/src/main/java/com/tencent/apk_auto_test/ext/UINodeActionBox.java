package com.tencent.apk_auto_test.ext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.tencent.apk_auto_test.core.TestResultPrinter;
import com.tencent.apk_auto_test.data.Global;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.ext.node.NodeEventService;
import com.tencent.apk_auto_test.ext.node.NodeInfoDumper;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class UINodeActionBox extends UIActionBox {

    private static final boolean DEBUG = true;

    private ArrayList<AccessibilityNodeInfo> infos;
    private NodeEventService eventService;
    private static String TAG = "UINodeActionBox";

    // the wait time after click
    private long mWaitTime = 3000;
    private double mX = 1.0;
    private double mY = 1.0;

    private boolean mCurrentMode = true;


    public UINodeActionBox(Context context) {
        super(context);
        //以1080P为标准根据分辨率拉伸需要的坐标值
        mY = Global.SCREEN_HEIGHT / 1920;
        mX = Global.SCREEN_WIDTH / 1080;
    }

    /**
     * 设置严格模式开关，开启后每个点击是否成功都会判断
     */

    public void setStrictMode(boolean isStrictMode) {
        mCurrentMode = isStrictMode;
    }

    /**
     * update the infos list
     */
    @SuppressLint("NewApi")
    private void beforeClick() {
        AccessibilityNodeInfo rootNode = null;
        rootNode = getRootNode();
        if (rootNode == null) {
            System.err.println("ERROR: null root node returned by AccessibilityNodeInfosObtain.");
        }
        NodeInfoDumper dumper = new NodeInfoDumper(
                mContext);
        infos = dumper.dumpWindow(rootNode);
    }

    private void handleClickFalse(String node) {
        if (mCurrentMode) {
            TestResultPrinter mPrinter = TestResultPrinter.getInstance();
            mPrinter.printResult(StaticData.currentCase + ":click node:" + node, false);
        }
    }


    /**
     * 2015-1-19 hwy
     *
     * @param text     the ui with the text to click
     * @param waitTime the wait time after clcik(ms)
     * @return
     */
    public boolean clickOnText(String text, int waitTime) {
        mWaitTime = waitTime;
        beforeClick();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getText() + "";
            if (title.equals(text)) {
                Log.i(TAG, "---clickOnText " + text);
                return click(i);
            }
        }
        handleClickFalse(text);
        Log.e(TAG, "clickOnText false!");
        return false;
    }


    /**
     * 根据id点击控件
     *
     * @param id       resouceid
     * @param waitTime waittime
     * @return
     */
    public boolean clickOnResourceId(String id, int waitTime, int index) {
        mWaitTime = waitTime;
        int j = 0;
        int k = 0;
        beforeClick();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String idName = infos.get(i).getViewIdResourceName() + "";
            if (idName.contains(id)) {
                if (index == j) {
                    Log.i(TAG, "---clickOnResourceId " + id);
                    return click(i);
                }
                j++;
                k = i;
            }
        }
        if (index == -1) {
            Log.i(TAG, "---clickOnResourceId " + id + " index " + k);
            return click(k);
        }
        handleClickFalse(id);
        Log.e(TAG, "clickOnResourceId " + id + " false!");
        return false;
    }

    /**
     * 根据id长按控件
     *
     * @param id       resouceid
     * @param waitTime waittime
     * @return
     */
    public boolean clickOnResourceId(String id, int waitTime, int index, int clickTime) {
        mWaitTime = waitTime;
        int j = 0;
        int k = 0;
        beforeClick();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String idName = infos.get(i).getViewIdResourceName() + "";
            if (idName.contains(id)) {
                if (index == j && index != -1) {
                    Log.i(TAG, "---clickOnResourceId " + id);
                    return longClick(i, clickTime);
                }
                j++;
                k = i;
            }
        }
        if (index == -1) {
            Log.i(TAG, "---clickOnResourceId " + id + " index " + k);
            return longClick(k, clickTime);
        }
        handleClickFalse(id);
        Log.e(TAG, "clickOnResourceId " + id + " false!");
        return false;
    }


    /**
     * 2016-10-27 veehou
     *
     * @param id         resouce id
     * @param waitTime
     * @param offsetType 0:x,1:y
     * @param offset     与该控件中心的偏移量，包含正负数
     * @return
     */
    public boolean clickOnResourceIdOffset(String id, int waitTime, int index, int offsetType, int offset) {
        mWaitTime = waitTime;
        int j = 0;
        beforeClick();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String idName = infos.get(i).getViewIdResourceName() + "";
            if (idName.contains(id)) {
                if (index == j) {
                    Log.i(TAG, "---clickOnResourceIdOffset " + id);
                    return clickOffset(i, offsetType, offset);
                }
                j++;
            }
        }
        handleClickFalse(id);
        Log.e(TAG, "clickOnResourceId " + id + " false!");
        return false;
    }

    /**
     * 长按
     *
     * @param id
     * @param clickTime
     * @param waitTime
     * @param offsetType
     * @param offset
     * @return
     */
    public boolean clickOnResourceIdOffset(String id, int waitTime, int index, int offsetType, int offset, int clickTime) {
        mWaitTime = waitTime;
        int j = 0;
        beforeClick();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String idName = infos.get(i).getViewIdResourceName() + "";
            if (idName.contains(id)) {
                if (index == j) {
                    Log.i(TAG, "---long clickOnResourceIdOffset " + id);
                    return longClickOffset(i, offsetType, offset, clickTime);
                }
                j++;
            }
        }
        handleClickFalse(id);
        Log.e(TAG, "clickOnResourceId " + id + " false!");
        return false;
    }


    /**
     * click by description
     *
     * @param text
     * @return
     */
    public boolean clickOnDesc(String text, int waitTime) {
        mWaitTime = waitTime;
        beforeClick();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getContentDescription() + "";
            //Log.d(TAG, title);
            if (title.equals(text)) {
                Log.i(TAG, "clickOnDesc " + text);
                return click(i);
            }
        }
        handleClickFalse(text);
        Log.e(TAG, "clickOnText false!");
        return false;
    }

    /**
     * click the view contains the text
     *
     * @param text
     * @return
     */
    public boolean clickOnTextContain(String text, int waitTime) {
        mWaitTime = waitTime;
        beforeClick();
        if (infos == null || infos.size() == 0) {
            Log.e(TAG, "info null & size==0");
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getText() + "";
            if (title.contains(text)) {
                Log.i(TAG, "clickOnTextContain " + text);
                return click(i);
            }
        }
        handleClickFalse(text);
        Log.e(TAG, "clickOnText false!");
        return false;
    }

    /**
     * 通过id找到父控件点击其中子控件
     *
     * @param id       id名字
     * @param index    序号
     * @param waitTime 点击等待时间
     * @return 是否成功点击
     */
    public boolean clickOnListViewByResourceId(String id, int index, int waitTime) {
        mWaitTime = waitTime;
        beforeClick();
        if (null == getNodeByResourceId(id, 0)) {
            Log.e(TAG, "getNodeByResourceId null");
            return false;
        }
        AccessibilityNodeInfo list = getNodeByResourceId(id, 0);
        if (index > list.getChildCount()) {
            handleClickFalse(id);
            return false;
        }
        if (list.getChildCount() == 0) {
            handleClickFalse(id);
            return false;
        }
        return click(list.getChild(index));
    }


    public int getNodeByClass(String className, int index) {
        if (className == null || className.equals("")) {
            return -1;
        }
        int currentIndex = 0;
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).getClassName() + "";
            if (name.contains(className)) {
                currentIndex++;
                if (index == currentIndex) {
                    return i;
                }
            }
        }
        return -1;
    }

    private AccessibilityNodeInfo getNodeByResourceId(String id, int index) {
        if (id == null || id.equals("")) {
            return null;
        }
        int currentIndex = 0;
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).getViewIdResourceName() + "";
            if (name.contains(id)) {
                if (index == currentIndex) {
                    return infos.get(i);
                }
                currentIndex++;
            }
        }
        return null;
    }


    private Rect getRect(AccessibilityNodeInfo node) {
        if (node == null) {
            return new Rect();
        }
        Rect r = new Rect();
        node.getBoundsInScreen(r);
        return r;
    }

    private Rect getRect(int index) {
        Rect r = new Rect();
        try {
            infos.get(index).getBoundsInScreen(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }


    public boolean isNodeExist(String nodeType, String text) {
        beforeClick();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = "";
            if (nodeType.equals("text"))
                title = infos.get(i).getText() + "";
            else if (nodeType.equals("id"))
                title = infos.get(i).getViewIdResourceName() + "";
            if (title.contains(text)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNodeEqual(String nodeType, String text) {
        beforeClick();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = "";
            if (nodeType.equals("text"))
                title = infos.get(i).getText() + "";
            else if (nodeType.equals("id"))
                title = infos.get(i).getViewIdResourceName() + "";
            if (title.equals(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get root node.
     *
     * @return AccessibilityNodeInfo
     */
    private AccessibilityNodeInfo getRootNode() {
        AccessibilityNodeInfo rootNode = null;
        int i = 0;
        while (i < 10) {
            eventService = StaticData.nodeEventService;
            if (eventService != null) {
                rootNode = eventService.getRootInActiveWindow();
            } else {
                Log.v(TAG, "eventService is null");
            }
            if (rootNode == null) {
                if (DEBUG)
                    Log.e(TAG, "try to connect:" + i);
                i++;
                openAccessibilityServiceIfNotStart();
                SystemClock.sleep(500);
            } else {
                break;
            }
        }
        return rootNode;
    }

    private void openAccessibilityServiceIfNotStart() {
        Intent intent = new Intent();
        intent.setClass(mContext, NodeEventService.class);
        mContext.startService(intent);
    }

    private boolean click(AccessibilityNodeInfo node) {
        if (node == null) {
            Log.e(TAG, "click node null");
            return false;
        }
        Rect rect = getRect(node);
        if (rect == null) {
            Log.e(TAG, "click rect null");
            return false;
        }
        click(rect.centerX(), rect.centerY(), mWaitTime);
        return true;
    }

    private boolean click(int index) {
        if (index < 0) {
            return false;
        }

        Rect rect = getRect(index);
        if (rect == null) {
            return false;
        }
        click(rect.centerX(), rect.centerY(), mWaitTime);
        return true;
    }

    private boolean clickOffset(int index, int offsetType, int offset) {
        if (index < 0) {
            return false;
        }

        Rect rect = getRect(index);
        if (rect == null) {
            return false;
        }
        switch (offsetType) {
            case 0:
                click((float) (rect.centerX() + offset * mX), rect.centerY(), mWaitTime);
                break;
            case 1:
                click(rect.centerX(), (float) (rect.centerY() + offset * mY), mWaitTime);
                break;
            default:
                click(rect.centerX(), rect.centerY(), mWaitTime);
        }

        return true;
    }

    private boolean longClick(int index, int clickTime) {
        if (index < 0) {
            return false;
        }

        Rect rect = getRect(index);
        if (rect == null) {
            return false;
        }
        click(rect.centerX(), rect.centerY(), mWaitTime, clickTime);
        return true;
    }


    private boolean longClickOffset(int index, int offsetType, int offset, int clickTime) {
        if (index < 0) {
            return false;
        }

        Rect rect = getRect(index);
        if (rect == null) {
            return false;
        }
        switch (offsetType) {
            case 0:
                click((float) (rect.centerX() + offset * mX), rect.centerY(), mWaitTime, clickTime);
                break;
            case 1:
                click(rect.centerX(), (float) (rect.centerY() + offset * mY), mWaitTime, clickTime);
                break;
            default:
                click(rect.centerX(), rect.centerY(), mWaitTime, clickTime);
        }

        return true;
    }


}
