package com.tencent.apk_auto_test.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.tencent.apk_auto_test.services.AccessibilityEventService;
import com.test.function.Assert;
import com.test.function.Operate;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

@SuppressLint("NewApi")
public class UINodeOperate {

    private static final boolean DEBUG = false;
    private static final String NAME = "com.tencent.apk_auto_test/com.tencent.apk_auto_test.services.AccessibilityEventService";
    private static final char ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR = ':';
    private final static TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(
            ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR);

    private ArrayList<AccessibilityNodeInfo> infos;
    private static String TAG = "UINodeOperate";
    private Context mContext;
    private Operate mOperate;
    private Assert mAssert;
    private UIOperate mUIOperate;
    // the wait time after click
    private int mWaitTime = 3000;
    private double mX = 1.0;
    private double mY = 1.0;

    public UINodeOperate(Context context) {
        this.mContext = context;
        mAssert = new Assert(context);
        mOperate = new Operate(mContext);
        mUIOperate = new UIOperate(mContext);
        //以1080P为标准根据分辨率拉伸需要的坐标值
        mY = Global.SCREEN_HEIGHT / 1920;
        mX = Global.SCREEN_WIDTH / 1080;

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
            AccessibilityEventService eventService = AccessibilityEventService
                    .getService();
            if (eventService != null) {
                rootNode = eventService.getRootInActiveWindow();
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
        openService(NAME);
        Intent intent = new Intent();
        intent.setClass(mContext, AccessibilityEventService.class);
        // If not start ,to start
        if (!mAssert.isMyServiceRunning(AccessibilityEventService.class
                .getName())) {
            mContext.startService(intent);
        }
    }

    /**
     * open AccessibilityEventService that this app create.
     *
     * @param preferenceKey NAME
     */
    private void openService(String preferenceKey) {

        // Parse the enabled services.
        Set<ComponentName> enabledServices = getEnabledServicesFromSettings(mContext);

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
            // Log.e(TAG, "enabledService:"+enabledService.toString());
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
    }


    public Set<ComponentName> getEnabledServicesFromSettings(Context context) {
        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        Log.v(TAG, "enabledServicesSetting ：" + enabledServicesSetting);
        if (enabledServicesSetting == null) {
            enabledServicesSetting = "";
        }
        Set<ComponentName> enabledServices = new HashSet<ComponentName>();
        TextUtils.SimpleStringSplitter colonSplitter = sStringColonSplitter;
        colonSplitter.setString(enabledServicesSetting);
        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);
            if (enabledService != null) {
                enabledServices.add(enabledService);
            }
        }
        return enabledServices;
    }


    private boolean click(int index) {
        if (index < 0) {
            return false;
        }

        Rect rect = getRect(index);
        if (rect == null) {
            return false;
        }
        mUIOperate.click(rect.centerX(), rect.centerY(), mWaitTime);
        return true;
    }

    private boolean click(int index, int offsetType, int offset) {
        if (index < 0) {
            return false;
        }

        Rect rect = getRect(index);
        if (rect == null) {
            return false;
        }
        switch (offsetType) {
            case 0:
                mUIOperate
                        .click((float) (rect.centerX() + offset * mX), rect.centerY(), mWaitTime);
                break;
            case 1:
                mUIOperate
                        .click(rect.centerX(), (float) (rect.centerY() + offset * mY), mWaitTime);
                break;
            default:
                mUIOperate.click(rect.centerX(), rect.centerY(), mWaitTime);
        }

        return true;
    }

    /**
     * click by index String spilt by "_"
     *
     * @param indexString
     * @return
     */
    private boolean click(String indexString) {
        if (infos == null || infos.size() == 0) {
            return false;
        }
        if (indexString == null || indexString.equals("")) {
            return false;
        }

        AccessibilityNodeInfo node = infos.get(0);

        String[] indexStrArray = indexString.split("_");
        for (String indexItem : indexStrArray) {
            try {
                node = node.getChild(Integer.parseInt(indexItem));
            } catch (Exception e) {
                Log.e(TAG, "click(String indexString):" + e.toString());
                return false;
            }
        }

        Rect rect = getRect(node);
        if (rect == null) {
            return false;
        }
        mUIOperate.click(rect.centerX(), rect.centerY(), mWaitTime);
        return true;
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
        Log.e(TAG, "click rect:" + rect.centerX() + " " + rect.centerY());
        mUIOperate.click(rect.centerX(), rect.centerY(), mWaitTime);
        return true;
    }

    /**
     * update the infos list
     */
    @SuppressLint("NewApi")
    public void getNodes() {
        AccessibilityNodeInfo rootNode = null;
        rootNode = getRootNode();
        if (rootNode == null) {
            System.err.println("ERROR: null root node returned by AccessibilityNodeInfosObtain.");
        }
        AccessibilityNodeInfoDumper dumper = new AccessibilityNodeInfoDumper(
                mContext);
        infos = dumper.dumpWindow(rootNode);
    }

    public boolean clickOnButton(int index) {
        int buttonIndex = getNodeByClass("Button", index);
        return click(buttonIndex);
    }

    /**
     * EditText
     *
     * @param index
     * @return
     */
    public boolean clickOnEditText(int index, int waitTime) {
        mWaitTime = waitTime;
        int editIndex = getNodeByClass("EditText", index);
        if (editIndex < 0) {
            return false;
        }
        Rect r = getRect(editIndex);
        if (r == null) {
            return false;
        }
        mUIOperate.click(r.centerX(), r.centerY(), mWaitTime);
        return true;
        // return click(editIndex);
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
        getNodes();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getText() + "";
            if (title.equals(text)) {
                return click(i);
            }
        }

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
    public boolean clickOnResouceId(String id, int waitTime, int index) {
        mWaitTime = waitTime;
        int j = 0;
        getNodes();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String idName = infos.get(i).getViewIdResourceName() + "";
            if (idName.contains(id)) {
                if (index == j)
                    return click(i);
                j++;
            }
        }

        Log.e(TAG, "clickOnResouceId " + id + " false!");
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
    public boolean clickOnResouceIdOffset(String id, int waitTime,
                                          int offsetType, int offset) {


        mWaitTime = waitTime;
        getNodes();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String idName = infos.get(i).getViewIdResourceName() + "";
            if (idName.contains(id)) {
                return click(i, offsetType, offset);
            }
        }

        Log.e(TAG, "clickOnResouceId " + id + " false!");
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
        getNodes();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getContentDescription() + "";
            if (title.contains(text)) {
                return click(i);
            }
        }
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
        getNodes();
        if (infos == null || infos.size() == 0) {
            Log.e(TAG, "info null & size==0");
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getText() + "";
            if (title.contains(text)) {
                Log.e(TAG, "start click");
                return click(i);
            }
        }
        Log.e(TAG, "clickOnText false!");
        return false;
    }

    /**
     * ListView child start from 0
     *
     * @param index
     * @return
     */
    public boolean clickOnListView(int index, int waitTime) {
        mWaitTime = waitTime;
        getNodes();
        int parentIndex = getNodeByClass("ListView", 1);
        if (!isChildExits(parentIndex, index)) {
            return false;
        }
        return click(infos.get(parentIndex).getChild(index));
    }

    public boolean clickOnListViewText(String text) {
        int parentIndex = getNodeByClass("ListView", 1);
        if (parentIndex < 0) {
            return false;
        }
        AccessibilityNodeInfo list = infos.get(parentIndex);

        while (isViewUpdate()) {
            textOldList = (ArrayList<String>) textList.clone();
            textList.clear();
            AccessibilityNodeInfo node = getItemByText(list, text);
            if (node != null) {
                return click(node);
            } else {
                Log.e(TAG, "getItemByText false");
            }
            Rect rect = getRect(list);
            mOperate.drag(rect.centerX(), rect.centerX(), rect.bottom - 40,
                    rect.top + 40, 5);
            mOperate.sleep(2000);
            getNodes();
        }
        Log.e(TAG, "isViewUpdate false");
        return false;
    }

    /**
     * click the next view after the text
     *
     * @param text
     * @return
     */
    public boolean clickOnNext(String text) {
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getText() + "";
            if (title.contains(text)) {
                return click(i + 1);
            }
        }
        return false;
    }

    /**
     * ImageView
     *
     * @param index
     * @return
     */
    public boolean clickOnImage(int index) {
        int imageIndex = getNodeByClass("ImageView", index);
        return click(imageIndex);
    }

    /**
     * ImageView or Button
     *
     * @param index
     * @return
     */
    public boolean clickOnImageOrButton(int index) {
        int imageIndex = getNodeByClass2("ImageView", "Button", index);
        return click(imageIndex);
    }

    /**
     * @param index (starts from 1)
     * @return
     */
    public boolean clickOnTab(int index) {
        int parentIndex = getNodeByClass("TabWidget", 1);
        if (!isChildExits(parentIndex, index)) {
            return false;
        }
        return click(infos.get(parentIndex).getChild(index - 1));
    }

    /**
     * @param index (starts from 1)
     * @return
     */
    public boolean clickOnGridView(int index) {
        int parentIndex = getNodeByClass("GridView", 1);
        if (!isChildExits(parentIndex, index)) {
            return false;
        }
        return click(infos.get(parentIndex).getChild(index - 1));
    }

    public boolean isChildExits(int parentIndex, int childIndex) {
        if (parentIndex <= 0) {
            return false;
        }
        if (infos.get(parentIndex).getChildCount() < childIndex) {
            return false;
        }
        return true;
    }

    /**
     * click the rightArea in the View
     *
     * @param index
     * @return
     */
    public boolean clickRight(int index) {
        if (index < 0) {
            return false;
        }
        Rect rect = getRect(index);
        if (rect == null) {
            return false;
        }
        mUIOperate.click(rect.left / 4 + rect.right / 4 * 3, rect.centerY(),
                mWaitTime);
        return true;
    }

    public boolean clickLastImageOfListItem(int index) {
        int listViewIndex = getNodeByClass("ListView", 1);
        // return getLastImageOfNode(infos.get(listViewIndex).getChild(index))
        // .performAction(AccessibilityNodeInfo.ACTION_CLICK);
        if (listViewIndex < 0) {
            return false;
        }
        AccessibilityNodeInfo nodeListView = infos.get(listViewIndex);
        if (nodeListView == null) {
            return false;
        }
        if (nodeListView.getChildCount() < index) {
            return false;
        }
        return click(getLastImageOfNode(nodeListView.getChild(index - 1)));
    }

    public boolean clickFirstImageOfListItem(int index) {
        int listViewIndex = getNodeByClass("ListView", 1);
        // return getLastImageOfNode(infos.get(listViewIndex).getChild(index))
        // .performAction(AccessibilityNodeInfo.ACTION_CLICK);
        if (listViewIndex < 0) {
            return false;
        }
        AccessibilityNodeInfo nodeListView = infos.get(listViewIndex);
        if (nodeListView.getChildCount() < index) {
            return false;
        }
        return click(getFirstImageOfNode(nodeListView.getChild(index - 1)));
    }

    public boolean clickOnTableRow(int tableIndex, int itemIndex) {
        int rowIndex = getNodeByClass("TableRow", tableIndex);
        if (rowIndex <= 0) {
            return false;
        }
        if (infos.get(rowIndex).getChildCount() < itemIndex) {
            return false;
        }
        // printViewInfos(infos.get(rowIndex));
        Log.e(TAG, "clickOnTableRow " + tableIndex + " " + itemIndex);
        Rect r = getRect(infos.get(rowIndex).getChild(itemIndex - 1));
        if (r == null) {
            return false;
        }
        mUIOperate.click(r.centerX(), r.centerY(), mWaitTime);
        return true;
    }

    public boolean setChecked(int index, boolean checked) {
        AccessibilityNodeInfo node = getNodeInfoByClass("CheckBox", index);
        if (node == null) {
            return false;
        }
        if (node.isChecked() != checked) {
            click(node);
        }
        return true;
    }

    public boolean setSwitch(int index, boolean checked) {
        AccessibilityNodeInfo node = getNodeInfoByClass("OppoSwitch", index);
        if (node == null) {
            return false;
        }
        if (node.isChecked() != checked) {
            click(node);
        }
        return true;
    }

    /**
     * the count of TextView
     *
     * @return
     */
    public int getTextCount() {
        int count = 0;
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).getClassName() + "";
            if (name.contains("TextView")) {
                count++;
            }
        }
        return count;
    }

    /**
     * the count of ImageView
     *
     * @return
     */
    public int getImageCount() {
        int count = 0;
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).getClassName() + "";
            if (name.contains("ImageView")) {
                count++;
            }
        }
        return count;
    }

    /**
     * the count of Button
     *
     * @return
     */
    public int getButtonCount() {
        int count = 0;
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).getClassName() + "";
            if (name.contains("Button")) {
                count++;
            }
        }
        return count;
    }

    /**
     * the count of Button and ImageView
     *
     * @return
     */
    public int getButtonOrImageCount() {
        int count = 0;
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).getClassName() + "";
            if (name.contains("Button") || name.contains("ImageView")) {
                count++;
            }
        }
        return count;
    }

    /**
     * the count of the child of the first ListView
     *
     * @return
     */
    public int getListCount() {
        int parentIndex = getNodeByClass("ListView", 1);
        if (parentIndex <= 0) {
            return 0;
        }
        return infos.get(parentIndex).getChildCount();
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

    public AccessibilityNodeInfo getNodeInfoByClass(String className, int index) {
        if (className == null || className.equals("")) {
            return null;
        }
        int currentIndex = 0;
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).getClassName() + "";
            if (name.contains(className)) {
                currentIndex++;
                if (index == currentIndex) {
                    return infos.get(i);
                }
            }
        }
        return null;
    }

    public int getNodeByClass2(String className, String className2, int index) {
        if (className == null || className.equals("")) {
            return -1;
        }
        int currentIndex = 0;
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).getClassName() + "";
            if (name.contains(className) || name.contains(className2)) {
                currentIndex++;
                if (index == currentIndex) {
                    return i;
                }
            }
        }
        return -1;
    }

    private ArrayList<String> textList = new ArrayList<String>();
    private ArrayList<String> textOldList = new ArrayList<String>();

    public AccessibilityNodeInfo getItemByText(AccessibilityNodeInfo view,
                                               String text) {
        AccessibilityNodeInfo result = null;
        if (view == null) {
            return null;
        }
        int size = view.getChildCount();
        for (int i = 0; i < size; i++) {
            AccessibilityNodeInfo item = view.getChild(i);
            String title = "";
            try {
                title = item.getText().toString();
            } catch (Exception e) {
            }
            if (!title.equals("")) {
                textList.add(title);
            }
            if (title.equals(text)) {
                result = item;
                break;
            } else {
                result = getItemByText(item, text);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public int getIndex(String textOrDesc) {
        if (infos == null || infos.size() == 0) {
            return -1;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getText() + "";
            String dexc = infos.get(i).getContentDescription() + "";
            if (title.contains(textOrDesc) || dexc.contains(textOrDesc)) {
                return i;
            }
        }
        return -1;
    }

    public Rect getImageRect(int index) {
        int imageIndex = getNodeByClass2("ImageView", "Button", index);
        return getRect(imageIndex);
    }

    //
    public Rect getRect(String text) {
        if (text == null || text.equals("")) {
            return new Rect();
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getText() + "";
            if (title.equals(text)) {
                Rect r = new Rect();
                infos.get(i).getBoundsInScreen(r);
                return r;
            }
        }
        return new Rect();
    }

    public Rect getRect(AccessibilityNodeInfo node) {
        if (node == null) {
            return new Rect();
        }
        Rect r = new Rect();
        node.getBoundsInScreen(r);
        return r;
    }

    public Rect getRect(int index) {
        Rect r = new Rect();
        try {
            infos.get(index).getBoundsInScreen(r);
        } catch (Exception e) {
        }
        return r;
    }

    /**
     * print the info of nodes in log
     */
    public void printViewInfos() {
        if (infos == null || infos.size() == 0) {
            return;
        }
        for (int i = 0; i < infos.size(); i++) {
            Log.e(TAG, i + ":" + infos.get(i).getText() + "");
            Log.e(TAG, i + ":" + infos.get(i).getClassName().toString());

        }
    }

    public AccessibilityNodeInfo getLastImageOfNode(AccessibilityNodeInfo root) {
        if (root == null) {
            return null;
        }
        AccessibilityNodeInfo lastNode = null;
        int size = root.getChildCount();
        for (int i = size - 1; i >= 0; i--) {
            String title = "";
            try {
                title = root.getChild(i).getClassName() + "";
            } catch (Exception e) {
            }
            if (title.contains("ImageView")) {
                lastNode = root.getChild(i);
                break;
            } else {
                AccessibilityNodeInfo childNode = getLastImageOfNode(root
                        .getChild(i));
                if (childNode != null) {
                    lastNode = childNode;
                    break;
                }
            }
        }
        return lastNode;
    }

    public AccessibilityNodeInfo getFirstImageOfNode(AccessibilityNodeInfo root) {
        if (root == null) {
            return null;
        }
        AccessibilityNodeInfo firstNode = null;
        int size = root.getChildCount();
        for (int i = 0; i < size; i++) {
            String title = "";
            try {
                title = root.getChild(i).getClassName() + "";
            } catch (Exception e) {
            }
            if (title.contains("ImageView")) {
                firstNode = root.getChild(i);
                break;
            } else {
                AccessibilityNodeInfo childNode = getFirstImageOfNode(root
                        .getChild(i));
                if (childNode != null) {
                    firstNode = childNode;
                    break;
                }
            }
        }
        return firstNode;
    }

    public void printButtonInfos() {
        if (infos == null || infos.size() == 0) {
            return;
        }
        int index = 0;
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).getClassName() + "";
            if (name.contains("Button")) {
                index++;
                Log.e(TAG, index + ":" + infos.get(i).getText().toString());
            }
        }
    }

    public void printViewInfos(AccessibilityNodeInfo root) {
        if (infos == null || infos.size() == 0) {
            return;
        }
        if (root == null) {
            return;
        }
        if (root.getChildCount() <= 0) {
            return;
        }
        int index = 0;
        AccessibilityNodeInfo node = null;
        int size = root.getChildCount();
        for (int i = 0; i < size; i++) {
            index++;
            node = root.getChild(i);
            Log.e(TAG, index + ":" + node.getText() + " " + node.getClassName());
            printViewInfos(node);
        }
    }

    public boolean isTextExits(String text) {
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String title = infos.get(i).getText() + "";
            if (title.contains(text)) {
                return true;
            }
        }
        return false;
    }

    public boolean isViewUpdate() {
        int size = textOldList.size();
        if (size == 0 || size != textList.size()) {
            return true;
        } else {
            for (int i = 0; i < size; i++) {
                if (!textList.get(i).equals(textOldList.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEnable(int index) {
        return infos.get(index).isEnabled();
    }

}
