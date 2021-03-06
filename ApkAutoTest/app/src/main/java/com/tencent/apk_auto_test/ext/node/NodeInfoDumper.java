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

package com.tencent.apk_auto_test.ext.node;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

@SuppressLint("NewApi")
public class NodeInfoDumper {

    @SuppressWarnings("unused")
    private Context mContext;
    private static final boolean DEBUG = false;

    // private ArrayList<AccessibilityNode> list;
    private ArrayList<AccessibilityNodeInfo> listInfo;
    private static final String TAG = "NodeInfoDumper";
    private static final String[] NAF_EXCLUDED_CLASSES = new String[]{
            android.widget.GridView.class.getName(),
            android.widget.GridLayout.class.getName(),
            android.widget.ListView.class.getName(),
            android.widget.TableLayout.class.getName()};
    HandlerThread mHT = null;

    // write2Arraylist tolist;

    public NodeInfoDumper(Context context) {
        mContext = context;
    }

    /**
     * Using {@link AccessibilityNodeInfo} this method will walk the layout
     * hierarchy and generates an xml dump to the location specified by
     * <code>dumpFile</code>
     */
    @SuppressLint("LongLogTag")
    public ArrayList<AccessibilityNodeInfo> dumpWindow(
            AccessibilityNodeInfo root) {
        if (root == null) {
            return null;
        }
        // list= new ArrayList<AccessibilityNode>();
        listInfo = new ArrayList<AccessibilityNodeInfo>();

        final long startTime = SystemClock.uptimeMillis();
        try {
            dumpNodeRec(root, 0);
            final long endTime = SystemClock.uptimeMillis();
            Log.w(TAG, "Fetch time: " + (endTime - startTime) + "ms");
        } catch (IOException e) {
            Log.e(TAG, "failed to dump window", e);
        }
        return listInfo;
    }

    @SuppressLint("NewApi")
    private void dumpNodeRec(AccessibilityNodeInfo node, int index)
            throws IOException {
        AccessibilityNodeInfo clone = AccessibilityNodeInfo.obtain(node);
        // if(clone.isClickable()||clone.isLongClickable()||clone.isCheckable()||clone.isScrollable()){
        if (clone.isVisibleToUser()) {
            listInfo.add(clone);
        }
        // }

        // if(clone.isClickable()){
        // listInfo.add(clone);
        // }

        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (child.isVisibleToUser()) {
                    if (DEBUG) {
                        Log.e(TAG,
                                index + ":" + child.getText() + " "
                                        + child.getClassName()
                                        + child.getViewIdResourceName());
                    }
                    dumpNodeRec(child, i);
                    child.recycle();
                } else {
                    if (DEBUG) {
                        Log.i(TAG, String.format(
                                "Skipping invisible child: %s",
                                child.toString()));
                    }
                }
            } else {
                Log.i(TAG, String.format("Null child %d/%d, parent: %s", i,
                        count, node.toString()));
            }
        }
    }

    // public AccessibilityNodeInfo dumpNode(AccessibilityNodeInfo node,
    // int index, int x, int y) throws IOException {
    // AccessibilityNodeInfo clone = AccessibilityNodeInfo.obtain(node);
    //
    //
    // int count = node.getChildCount();
    // for (int i = 0; i < count; i++) {
    // AccessibilityNodeInfo child = node.getChild(i);
    // if (child != null) {
    // if (child.isVisibleToUser()) {
    // dumpNode(child, i, x, y);
    // child.recycle();
    // } else {
    // if(DEBUG) {
    // Log.i(TAG, String.format("Skipping invisible child: %s",
    // child.toString()));
    // }
    // }
    // } else {
    // Log.i(TAG, String.format("Null child %d/%d, parent: %s",
    // i, count, node.toString()));
    // }
    // }
    // return clone;
    // }

    /**
     * The list of classes to exclude my not be complete. We're attempting to
     * only reduce noise from standard layout classes that may be falsely
     * configured to accept clicks and are also enabled.
     *
     * @param n
     * @return
     */
    @SuppressWarnings("unused")
    private static boolean nafExcludedClass(AccessibilityNodeInfo n) {
        String className = safeCharSeqToString(n.getClassName());
        for (String excludedClassName : NAF_EXCLUDED_CLASSES) {
            if (className.endsWith(excludedClassName))
                return true;
        }
        return false;
    }

    /**
     * We're looking for UI controls that are enabled, clickable but have no
     * text nor content-description. Such controls configuration indicate an
     * interactive control is present in the UI and is most likely not
     * accessibility friendly. We refer to such controls here as NAF controls
     * (Not Accessibility Friendly)
     *
     * @param node
     * @return false if a node fails the check, true if all is OK
     */
    @SuppressWarnings("unused")
    private static boolean nafCheck(AccessibilityNodeInfo node) {
        boolean isNaf = node.isClickable() && node.isEnabled()
                && safeCharSeqToString(node.getContentDescription()).isEmpty()
                && safeCharSeqToString(node.getText()).isEmpty();

        if (!isNaf)
            return true;

        // check children since sometimes the containing element is clickable
        // and NAF but a child's text or description is available. Will assume
        // such layout as fine.
        return childNafCheck(node);
    }

    /**
     * This should be used when it's already determined that the node is NAF and
     * a further check of its children is in order. A node maybe a container
     * such as LinerLayout and may be set to be clickable but have no text or
     * content description but it is counting on one of its children to fulfill
     * the requirement for being accessibility friendly by having one or more of
     * its children fill the text or content-description. Such a combination is
     * considered by this dumper as acceptable for accessibility.
     *
     * @param node
     * @return
     */
    private static boolean childNafCheck(AccessibilityNodeInfo node) {
        int childCount = node.getChildCount();
        for (int x = 0; x < childCount; x++) {
            AccessibilityNodeInfo childNode = node.getChild(x);

            if (!safeCharSeqToString(childNode.getContentDescription())
                    .isEmpty()
                    || !safeCharSeqToString(childNode.getText()).isEmpty())
                return true;

            if (childNafCheck(childNode))
                return true;
        }
        return false;
    }

    private static String safeCharSeqToString(CharSequence cs) {
        if (cs == null)
            return "";
        else {
            return stripInvalidXMLChars(cs);
        }
    }

    private static String stripInvalidXMLChars(CharSequence cs) {
        StringBuffer ret = new StringBuffer();
        char ch;
            /*
             * http://www.w3.org/TR/xml11/#charsets [#x1-#x8], [#xB-#xC],
		 * [#xE-#x1F], [#x7F-#x84], [#x86-#x9F], [#xFDD0-#xFDDF],
		 * [#x1FFFE-#x1FFFF], [#x2FFFE-#x2FFFF], [#x3FFFE-#x3FFFF],
		 * [#x4FFFE-#x4FFFF], [#x5FFFE-#x5FFFF], [#x6FFFE-#x6FFFF],
		 * [#x7FFFE-#x7FFFF], [#x8FFFE-#x8FFFF], [#x9FFFE-#x9FFFF],
		 * [#xAFFFE-#xAFFFF], [#xBFFFE-#xBFFFF], [#xCFFFE-#xCFFFF],
		 * [#xDFFFE-#xDFFFF], [#xEFFFE-#xEFFFF], [#xFFFFE-#xFFFFF],
		 * [#x10FFFE-#x10FFFF].
		 */
        for (int i = 0; i < cs.length(); i++) {
            ch = cs.charAt(i);

            if ((ch >= 0x1 && ch <= 0x8) || (ch >= 0xB && ch <= 0xC)
                    || (ch >= 0xE && ch <= 0x1F) || (ch >= 0x7F && ch <= 0x84)
                    || (ch >= 0x86 && ch <= 0x9f)
                    || (ch >= 0xFDD0 && ch <= 0xFDDF)
                    || (ch >= 0x1FFFE && ch <= 0x1FFFF)
                    || (ch >= 0x2FFFE && ch <= 0x2FFFF)
                    || (ch >= 0x3FFFE && ch <= 0x3FFFF)
                    || (ch >= 0x4FFFE && ch <= 0x4FFFF)
                    || (ch >= 0x5FFFE && ch <= 0x5FFFF)
                    || (ch >= 0x6FFFE && ch <= 0x6FFFF)
                    || (ch >= 0x7FFFE && ch <= 0x7FFFF)
                    || (ch >= 0x8FFFE && ch <= 0x8FFFF)
                    || (ch >= 0x9FFFE && ch <= 0x9FFFF)
                    || (ch >= 0xAFFFE && ch <= 0xAFFFF)
                    || (ch >= 0xBFFFE && ch <= 0xBFFFF)
                    || (ch >= 0xCFFFE && ch <= 0xCFFFF)
                    || (ch >= 0xDFFFE && ch <= 0xDFFFF)
                    || (ch >= 0xEFFFE && ch <= 0xEFFFF)
                    || (ch >= 0xFFFFE && ch <= 0xFFFFF)
                    || (ch >= 0x10FFFE && ch <= 0x10FFFF))
                ret.append(".");
            else
                ret.append(ch);
        }
        return ret.toString();
    }
}

// /*
// * Copyright (C) 2012 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
// package com.apptest.uiautotest;
//
// import android.content.Context;
// import android.os.Environment;
// import android.os.SystemClock;
// import android.util.Log;
// import android.util.Xml;
// import android.view.Display;
// import android.view.WindowManager;
// import android.view.accessibility.AccessibilityNodeInfo;
//
// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.io.StringWriter;
//
// import org.xmlpull.v1.XmlSerializer;
//
// /**
// *
// * @hide
// */
// public class NodeInfoDumper {
//
// private static final String TAG =
// NodeInfoDumper.class.getSimpleName();
// private static final String[] NAF_EXCLUDED_CLASSES = new String[] {
// android.widget.GridView.class.getName(),
// android.widget.GridLayout.class.getName(),
// android.widget.ListView.class.getName(),
// android.widget.TableLayout.class.getName()
// };
// private static Context mContext;
//
// NodeInfoDumper(Context context) {
// mContext = context;
// }
//
// /**
// * Using {@link AccessibilityNodeInfo} this method will walk the layout
// hierarchy
// * and generates an xml dump into the /data/local/window_dump.xml
// * @param info
// */
// public static void dumpWindowToFile(AccessibilityNodeInfo info) {
// File baseDir = new File(Environment.getDataDirectory(), "local");
// if (!baseDir.exists()) {
// baseDir.mkdir();
// baseDir.setExecutable(true, false);
// baseDir.setWritable(true, false);
// baseDir.setReadable(true, false);
// }
// dumpWindowToFile(info, new File(
// new File(Environment.getDataDirectory(), "local"), "zxl_window_dump.xml"));
// }
//
// /**
// * Using {@link AccessibilityNodeInfo} this method will walk the layout
// hierarchy
// * and generates an xml dump to the location specified by
// <code>dumpFile</code>
// * @param info
// */
// public static void dumpWindowToFile(AccessibilityNodeInfo root, File
// dumpFile) {
// if (root == null) {
// return;
// }
// final long startTime = SystemClock.uptimeMillis();
// try {
// FileWriter writer = new FileWriter(dumpFile);
// XmlSerializer serializer = Xml.newSerializer();
// StringWriter stringWriter = new StringWriter();
// serializer.setOutput(stringWriter);
// serializer.startDocument("UTF-8", true);
// serializer.startTag("", "hierarchy");
// WindowManager wm = (WindowManager)
// mContext.getSystemService(Context.WINDOW_SERVICE);
// Display display = wm.getDefaultDisplay();
// serializer.attribute("", "rotation", Integer.toString(
// display.getRotation()));
// dumpNodeRec(root, serializer, 0);
// serializer.endTag("", "hierarchy");
// serializer.endDocument();
// writer.write(stringWriter.toString());
// writer.close();
// } catch (IOException e) {
// Log.e(TAG, "failed to dump window to file", e);
// }
// final long endTime = SystemClock.uptimeMillis();
// Log.w(TAG, "Fetch time: " + (endTime - startTime) + "ms");
// }
//
// private static void dumpNodeRec(AccessibilityNodeInfo node, XmlSerializer
// serializer,
// int index) throws IOException {
// serializer.startTag("", "node");
// if (!nafExcludedClass(node) && !nafCheck(node))
// serializer.attribute("", "NAF", Boolean.toString(true));
// serializer.attribute("", "index", Integer.toString(index));
// serializer.attribute("", "text", safeCharSeqToString(node.getText()));
// serializer.attribute("", "class", safeCharSeqToString(node.getClassName()));
// serializer.attribute("", "package",
// safeCharSeqToString(node.getPackageName()));
// serializer.attribute("", "content-desc",
// safeCharSeqToString(node.getContentDescription()));
// serializer.attribute("", "checkable", Boolean.toString(node.isCheckable()));
// serializer.attribute("", "checked", Boolean.toString(node.isChecked()));
// serializer.attribute("", "clickable", Boolean.toString(node.isClickable()));
// serializer.attribute("", "enabled", Boolean.toString(node.isEnabled()));
// serializer.attribute("", "focusable", Boolean.toString(node.isFocusable()));
// serializer.attribute("", "focused", Boolean.toString(node.isFocused()));
// serializer.attribute("", "scrollable",
// Boolean.toString(node.isScrollable()));
// serializer.attribute("", "long-clickable",
// Boolean.toString(node.isLongClickable()));
// serializer.attribute("", "password", Boolean.toString(node.isPassword()));
// serializer.attribute("", "selected", Boolean.toString(node.isSelected()));
// serializer.attribute("", "bounds",
// AccessibilityNodeInfoHelper.getVisibleBoundsInScreen(node,
// mContext).toShortString());
// int count = node.getChildCount();
// for (int i = 0; i < count; i++) {
// AccessibilityNodeInfo child = node.getChild(i);
// if (child != null) {
// if (child.isVisibleToUser()) {
// dumpNodeRec(child, serializer, i);
// child.recycle();
// } else {
// Log.i(TAG, String.format("Skipping invisible child: %s",
// child.toString()));
// }
// } else {
// Log.i(TAG, String.format("Null child %d/%d, parent: %s",
// i, count, node.toString()));
// }
// }
// serializer.endTag("", "node");
// }
//
// /**
// * The list of classes to exclude my not be complete. We're attempting to
// * only reduce noise from standard layout classes that may be falsely
// * configured to accept clicks and are also enabled.
// *
// * @param n
// * @return
// */
// private static boolean nafExcludedClass(AccessibilityNodeInfo n) {
// String className = safeCharSeqToString(n.getClassName());
// for(String excludedClassName : NAF_EXCLUDED_CLASSES) {
// if(className.endsWith(excludedClassName))
// return true;
// }
// return false;
// }
//
// /**
// * We're looking for UI controls that are enabled, clickable but have no
// * text nor content-description. Such controls configuration indicate an
// * interactive control is present in the UI and is most likely not
// * accessibility friendly. We refer to such controls here as NAF controls
// * (Not Accessibility Friendly)
// *
// * @param node
// * @return false if a node fails the check, true if all is OK
// */
// private static boolean nafCheck(AccessibilityNodeInfo node) {
// boolean isNaf = node.isClickable() && node.isEnabled()
// && safeCharSeqToString(node.getContentDescription()).isEmpty()
// && safeCharSeqToString(node.getText()).isEmpty();
//
// if (!isNaf)
// return true;
//
// // check children since sometimes the containing element is clickable
// // and NAF but a child's text or description is available. Will assume
// // such layout as fine.
// return childNafCheck(node);
// }
//
// /**
// * This should be used when it's already determined that the node is NAF and
// * a further check of its children is in order. A node maybe a container
// * such as LinerLayout and may be set to be clickable but have no text or
// * content description but it is counting on one of its children to fulfill
// * the requirement for being accessibility friendly by having one or more of
// * its children fill the text or content-description. Such a combination is
// * considered by this dumper as acceptable for accessibility.
// *
// * @param node
// * @return
// */
// private static boolean childNafCheck(AccessibilityNodeInfo node) {
// int childCount = node.getChildCount();
// for (int x = 0; x < childCount; x++) {
// AccessibilityNodeInfo childNode = node.getChild(x);
//
// if (!safeCharSeqToString(childNode.getContentDescription()).isEmpty()
// || !safeCharSeqToString(childNode.getText()).isEmpty())
// return true;
//
// if (childNafCheck(childNode))
// return true;
// }
// return false;
// }
//
// private static String safeCharSeqToString(CharSequence cs) {
// if (cs == null)
// return "";
// else {
// return stripInvalidXMLChars(cs);
// }
// }
//
// private static String stripInvalidXMLChars(CharSequence cs) {
// StringBuffer ret = new StringBuffer();
// char ch;
// /* http://www.w3.org/TR/xml11/#charsets
// [#x1-#x8], [#xB-#xC], [#xE-#x1F], [#x7F-#x84], [#x86-#x9F], [#xFDD0-#xFDDF],
// [#x1FFFE-#x1FFFF], [#x2FFFE-#x2FFFF], [#x3FFFE-#x3FFFF],
// [#x4FFFE-#x4FFFF], [#x5FFFE-#x5FFFF], [#x6FFFE-#x6FFFF],
// [#x7FFFE-#x7FFFF], [#x8FFFE-#x8FFFF], [#x9FFFE-#x9FFFF],
// [#xAFFFE-#xAFFFF], [#xBFFFE-#xBFFFF], [#xCFFFE-#xCFFFF],
// [#xDFFFE-#xDFFFF], [#xEFFFE-#xEFFFF], [#xFFFFE-#xFFFFF],
// [#x10FFFE-#x10FFFF].
// */
// for (int i = 0; i < cs.length(); i++) {
// ch = cs.charAt(i);
//
// if((ch >= 0x1 && ch <= 0x8) || (ch >= 0xB && ch <= 0xC) || (ch >= 0xE && ch
// <= 0x1F) ||
// (ch >= 0x7F && ch <= 0x84) || (ch >= 0x86 && ch <= 0x9f) ||
// (ch >= 0xFDD0 && ch <= 0xFDDF) || (ch >= 0x1FFFE && ch <= 0x1FFFF) ||
// (ch >= 0x2FFFE && ch <= 0x2FFFF) || (ch >= 0x3FFFE && ch <= 0x3FFFF) ||
// (ch >= 0x4FFFE && ch <= 0x4FFFF) || (ch >= 0x5FFFE && ch <= 0x5FFFF) ||
// (ch >= 0x6FFFE && ch <= 0x6FFFF) || (ch >= 0x7FFFE && ch <= 0x7FFFF) ||
// (ch >= 0x8FFFE && ch <= 0x8FFFF) || (ch >= 0x9FFFE && ch <= 0x9FFFF) ||
// (ch >= 0xAFFFE && ch <= 0xAFFFF) || (ch >= 0xBFFFE && ch <= 0xBFFFF) ||
// (ch >= 0xCFFFE && ch <= 0xCFFFF) || (ch >= 0xDFFFE && ch <= 0xDFFFF) ||
// (ch >= 0xEFFFE && ch <= 0xEFFFF) || (ch >= 0xFFFFE && ch <= 0xFFFFF) ||
// (ch >= 0x10FFFE && ch <= 0x10FFFF))
// ret.append(".");
// else
// ret.append(ch);
// }
// return ret.toString();
// }
// }

