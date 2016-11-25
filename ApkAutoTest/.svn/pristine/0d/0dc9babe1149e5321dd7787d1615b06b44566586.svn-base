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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.IActivityManager.ContentProviderHolder;
import android.app.OppoActivityManager;
import android.app.WallpaperInfo;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Point;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Settings.NameValueTable;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.Log;
import android.view.Display;
import android.view.IWindowManager;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import android.view.Surface;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodInfo;

import com.android.internal.statusbar.IStatusBarService;
import com.android.uiautomator.core.Configurator;

/**
 * System.nanoTime()/1000000L The InteractionProvider is responsible for
 * injecting user events such as touch events (includes swipes) and text key
 * events into the system. To do so, all it needs to know about are coordinates
 * of the touch events and text for the text input events. The
 * InteractionController performs no synchronization. It will fire touch and
 * text input events as fast as it receives them. All idle synchronization is
 * performed prior to querying the hierarchy. See {@link QueryController}
 */
public class InteractionControl {

	private static final String LOG_TAG = InteractionControl.class
			.getSimpleName();

	private static final boolean DEBUG = true;

	// private static final long DEFAULT_SCROLL_EVENT_TIMEOUT_MILLIS = 500;

	private final KeyCharacterMap mKeyCharacterMap = KeyCharacterMap
			.load(KeyCharacterMap.VIRTUAL_KEYBOARD);

	private final IWindowManager mWindowManager;

	private final long mLongPressTimeout;

	private static final long REGULAR_CLICK_LENGTH = 100;

	private static final int MOTION_EVENT_INJECTION_DELAY_MILLIS = 5;

	private static long mDownTime;

	private Context mContext;
	private ActivityManager activityManager;

	// private int[] pid;

	public InteractionControl(Context ctx) {

		mContext = ctx;
		// Obtain the window manager.
		mWindowManager = IWindowManager.Stub.asInterface(ServiceManager
				.getService(Context.WINDOW_SERVICE));
		activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		if (mWindowManager == null) {
			throw new RuntimeException("Unable to connect to WindowManager, "
					+ "is the system running?");
		}

		// the value returned is on the border of going undetected as used
		// by this framework during long presses. Adding few extra 100ms
		// of long press time helps ensure long enough time for a valid
		// longClick detection.
		// mLongPressTimeout = ViewConfiguration.getLongPressTimeout() * 2 +
		// 100;
		mLongPressTimeout = getSystemLongPressTime() * 2 + 100;
	}

	/**
	 * Get the system long press time
	 * 
	 * @return milliseconds
	 */
	private static long getSystemLongPressTime() {
		// Read the long press timeout setting.
		long longPressTimeout = 0;
		try {
			IContentProvider provider = null;
			Cursor cursor = null;
			IActivityManager activityManager = ActivityManagerNative
					.getDefault();
			String providerName = Settings.Secure.CONTENT_URI.getAuthority();
			IBinder token = new Binder();
			try {
				// 4.2
				ContentProviderHolder holder = activityManager
						.getContentProviderExternal(providerName, 0, token);

				if (holder == null) {
					throw new IllegalStateException("Could not find provider: "
							+ providerName);
				}
				provider = holder.provider;
				// 4.2
				// cursor = provider.query(Settings.Secure.CONTENT_URI,
				// new String[] { Settings.Secure.VALUE }, "name=?",
				// new String[] { Settings.Secure.LONG_PRESS_TIMEOUT }, null,
				// null);
				// 4.3
				cursor = provider.query(null, Settings.Secure.CONTENT_URI,
						new String[] { NameValueTable.VALUE }, "name=?",
						new String[] { Settings.Secure.LONG_PRESS_TIMEOUT },
						null, null);
				if (cursor.moveToFirst()) {
					longPressTimeout = cursor.getInt(0);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
				if (provider != null) {
					activityManager.removeContentProviderExternal(providerName,
							token);
				}
			}
		} catch (RemoteException e) {
			String message = "Error reading long press timeout setting.";
			Log.e(LOG_TAG, message, e);
			throw new RuntimeException(message, e);
		}
		return longPressTimeout;
	}

	/**
	 * Click at coordinates and blocks until the first specified accessibility
	 * event.
	 * 
	 * All clicks will cause some UI change to occur. If the device is busy,
	 * this will block until the device begins to process the click at which
	 * point the call returns and normal wait for idle processing may begin. If
	 * no evens are detected for the timeout period specified, the call will
	 * return anyway.
	 * 
	 * @param x
	 * @param y
	 * @param timeout
	 * @param eventType
	 *            is an {@link AccessibilityEvent} type
	 * @return True if busy state is detected else false for timeout waiting for
	 *         busy state
	 */
	// public boolean clickAndWaitForEvent(final int x, final int y, long
	// timeout,
	// final int eventType) {
	// return clickAndWaitForEvents(x, y, timeout, false, eventType);
	// }

	/**
	 * Click at coordinates and blocks until the specified accessibility events.
	 * It is possible to set the wait for all events to occur, in no specific
	 * order, or to the wait for any.
	 * 
	 * @param x
	 * @param y
	 * @param timeout
	 * @param waitForAll
	 *            boolean to indicate whether to wait for any or all events
	 * @param eventTypes
	 *            mask
	 * @return
	 */
	// public boolean clickAndWaitForEvents(final int x, final int y,
	// long timeout, boolean waitForAll, int eventTypes) {
	// String logString = String.format(
	// "clickAndWaitForEvents(%d, %d, %d, %s, %d)", x, y, timeout,
	// Boolean.toString(waitForAll), eventTypes);
	// Log.d(LOG_TAG, logString);
	//
	// Runnable command = new Runnable() {
	// @Override
	// public void run() {
	// if (touchDown(x, y)) {
	// SystemClock.sleep(REGULAR_CLICK_LENGTH);
	// touchUp(x, y);
	// }
	// }
	// };
	// return runAndWaitForEvents(command, timeout, waitForAll, eventTypes) !=
	// null;
	// }

	/**
	 * Runs a command and waits for a specific accessibility event.
	 * 
	 * @param command
	 *            is a Runnable to execute before waiting for the event.
	 * @param timeout
	 * @param eventType
	 * @return The AccessibilityEvent if one is received, otherwise null.
	 */
	// private AccessibilityEvent runAndWaitForEvent(Runnable command,
	// long timeout, int eventType) {
	// return runAndWaitForEvents(command, timeout, false, eventType);
	// }

	/**
	 * Runs a command and waits for accessibility events. It is possible to set
	 * the wait for all events to occur at least once for each, or wait for any
	 * one to occur at least once.
	 * 
	 * @param command
	 * @param timeout
	 * @param waitForAll
	 *            boolean to indicate whether to wait for any or all events
	 * @param eventTypesMask
	 * @return The AccessibilityEvent if one is received, otherwise null.
	 */
	// private AccessibilityEvent runAndWaitForEvents(Runnable command,
	// long timeout, final boolean waitForAll, final int eventTypesMask) {
	// if (eventTypesMask == 0)
	// throw new IllegalArgumentException("events mask cannot be zero");
	//
	// class EventPredicate implements Predicate<AccessibilityEvent> {
	// int mMask;
	//
	// EventPredicate(int mask) {
	// mMask = mask;
	// }
	//
	// @Override
	// public boolean apply(AccessibilityEvent t) {
	// // check current event in the list
	// if ((t.getEventType() & mMask) != 0) {
	// if (!waitForAll)
	// return true;
	//
	// // remove from mask since this condition is satisfied
	// mMask &= ~t.getEventType();
	//
	// // Since we're waiting for all events to be matched at least
	// // once
	// if (mMask != 0)
	// return false;
	//
	// // all matched
	// return true;
	// }
	// // not one of our events
	// return false;
	// }
	// }
	//
	// AccessibilityEvent event = null;
	// try {
	// event = mUiAutomatorBridge
	// .executeCommandAndWaitForAccessibilityEvent(command,
	// new EventPredicate(eventTypesMask), timeout);
	// } catch (TimeoutException e) {
	// Log.w(LOG_TAG, "runAndwaitForEvent timedout waiting for events: "
	// + eventTypesMask);
	// return null;
	// } catch (Exception e) {
	// Log.e(LOG_TAG,
	// "exception from executeCommandAndWaitForAccessibilityEvent",
	// e);
	// return null;
	// }
	// return event;
	// }

	/**
	 * Send keys and blocks until the first specified accessibility event.
	 * 
	 * Most key presses will cause some UI change to occur. If the device is
	 * busy, this will block until the device begins to process the key press at
	 * which point the call returns and normal wait for idle processing may
	 * begin. If no evens are detected for the timeout period specified, the
	 * call will return anyway with false.
	 * 
	 * @param keyCode
	 * @param metaState
	 * @param eventType
	 * @param timeout
	 * @return
	 */
	// public boolean sendKeyAndWaitForEvent(final int keyCode,
	// final int metaState, final int eventType, long timeout) {
	//
	// Runnable command = new Runnable() {
	// @Override
	// public void run() {
	// final long eventTime = SystemClock.uptimeMillis();
	// KeyEvent downEvent = KeyEvent.obtain(eventTime, eventTime,
	// KeyEvent.ACTION_DOWN, keyCode, 0, metaState,
	// KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
	// InputDevice.SOURCE_KEYBOARD, null);
	// if (injectEventSync(downEvent)) {
	// KeyEvent upEvent = KeyEvent.obtain(eventTime, eventTime,
	// KeyEvent.ACTION_UP, keyCode, 0, metaState,
	// KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
	// InputDevice.SOURCE_KEYBOARD, null);
	// injectEventSync(upEvent);
	// }
	// }
	// };
	//
	// return runAndWaitForEvent(command, timeout, eventType) != null;
	// }

	/**
	 * Clicks at coordinates without waiting for device idle. This may be used
	 * for operations that require stressing the target.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean click(float x, float y) {
		Log.d(LOG_TAG, "click (" + x + ", " + y + ")");

		if (touchDown(x, y)) {
			SystemClock.sleep(REGULAR_CLICK_LENGTH);
			if (touchUp(x, y))
				return true;
		}
		return true;
	}

	/**
	 * Double Clicks at coordinates without waiting for device idle. This may be
	 * used for operations that require stressing the target.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean doubleClick(float x, float y) {
		Log.d(LOG_TAG, "doubleClick (" + x + ", " + y + ")");

		if (touchDown(x, y)) {
			SystemClock.sleep(REGULAR_CLICK_LENGTH);
			if (touchUp(x, y))
				if (touchDown(x, y)) {
					SystemClock.sleep(REGULAR_CLICK_LENGTH);
					if (touchUp(x, y))
						return true;
				}
		}
		return true;
	}

	/**
	 * Clicks at coordinates and waits for for a TYPE_WINDOW_STATE_CHANGED event
	 * followed by TYPE_WINDOW_CONTENT_CHANGED. If timeout occurs waiting for
	 * TYPE_WINDOW_STATE_CHANGED, no further waits will be performed and the
	 * function returns.
	 * 
	 * @param x
	 * @param y
	 * @param timeout
	 * @return true if both events occurred in the expected order
	 */
	// public boolean clickAndWaitForNewWindow(final int x, final int y,
	// long timeout) {
	// return (clickAndWaitForEvents(x, y, timeout, true,
	// AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
	// + AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED));
	// }

	private static boolean touchDown(float x, float y) {
		if (DEBUG) {
			Log.d(LOG_TAG, "touchDown (" + x + ", " + y + ")");
		}
		mDownTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(mDownTime, mDownTime,
				MotionEvent.ACTION_DOWN, x, y, 1);
		event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		return injectEventSync(event);
	}

	private static boolean touchUp(float x, float y) {
		if (DEBUG) {
			Log.d(LOG_TAG, "touchUp (" + x + ", " + y + ")");
		}
		final long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(mDownTime, eventTime,
				MotionEvent.ACTION_UP, x, y, 1);
		event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		mDownTime = 0;
		boolean b = injectEventSync(event);
		event.recycle();
		return b;
	}

	private static boolean touchMove(float x, float y) {
		if (DEBUG) {
			Log.d(LOG_TAG, "touchMove (" + x + ", " + y + ")");
		}
		final long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(mDownTime, eventTime,
				MotionEvent.ACTION_MOVE, x, y, 1);
		event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		return injectEventSync(event);
	}

	/**
	 * Handle swipes in any direction where the result is a scroll event. This
	 * call blocks until the UI has fired a scroll event or timeout.
	 * 
	 * @param downX
	 * @param downY
	 * @param upX
	 * @param upY
	 * @param steps
	 * @return true if we are not at the beginning or end of the scrollable
	 *         view.
	 */
	// public boolean scrollSwipe(final int downX, final int downY, final int
	// upX,
	// final int upY, final int steps) {
	// Log.d(LOG_TAG, "scrollSwipe (" + downX + ", " + downY + ", " + upX
	// + ", " + upY + ", " + steps + ")");
	//
	// Runnable command = new Runnable() {
	// @Override
	// public void run() {
	// swipe(downX, downY, upX, upY, steps);
	// }
	// };
	//
	// AccessibilityEvent event = runAndWaitForEvent(command,
	// DEFAULT_SCROLL_EVENT_TIMEOUT_MILLIS,
	// AccessibilityEvent.TYPE_VIEW_SCROLLED);
	// if (event == null) {
	// return false;
	// }
	// // AdapterViews have indices we can use to check for the beginning.
	// if (event.getFromIndex() != -1 && event.getToIndex() != -1
	// && event.getItemCount() != -1) {
	// boolean foundEnd = event.getFromIndex() == 0
	// || (event.getItemCount() - 1) == event.getToIndex();
	// Log.d(LOG_TAG, "scrollSwipe reached scroll end: " + foundEnd);
	// return !foundEnd;
	// } else if (event.getScrollX() != -1 && event.getScrollY() != -1) {
	// // Determine if we are scrolling vertically or horizontally.
	// if (downX == upX) {
	// // Vertical
	// boolean foundEnd = event.getScrollY() == 0
	// || event.getScrollY() == event.getMaxScrollY();
	// Log.d(LOG_TAG, "Vertical scrollSwipe reached scroll end: "
	// + foundEnd);
	// return !foundEnd;
	// } else if (downY == upY) {
	// // Horizontal
	// boolean foundEnd = event.getScrollX() == 0
	// || event.getScrollX() == event.getMaxScrollX();
	// Log.d(LOG_TAG, "Horizontal scrollSwipe reached scroll end: "
	// + foundEnd);
	// return !foundEnd;
	// }
	// }
	// return event != null;
	// }

	/**
	 * Handle swipes in any direction.
	 * 
	 * @param downX
	 * @param downY
	 * @param upX
	 * @param upY
	 * @param steps
	 * @return
	 */
	public boolean swipe(float downX, float downY, float upX, float upY,
			int steps) {
		boolean ret = false;
		int swipeSteps = steps;
		double xStep = 0;
		double yStep = 0;

		// avoid a divide by zero
		if (swipeSteps == 0)
			swipeSteps = 1;

		xStep = ((double) (upX - downX)) / swipeSteps;
		yStep = ((double) (upY - downY)) / swipeSteps;

		// first touch starts exactly at the point requested
		ret = touchDown(downX, downY);
		for (int i = 1; i < swipeSteps; i++) {
			ret &= touchMove(downX + (int) (xStep * i), downY
					+ (int) (yStep * i));
			if (ret == false)
				break;
			// set some known constant delay between steps as without it this
			// become completely dependent on the speed of the system and
			// results
			// may vary on different devices. This guarantees at minimum we have
			// a preset delay.
			SystemClock.sleep(5);
		}
		ret &= touchUp(upX, upY);
		return (ret);
	}

	/**
	 * Performs a swipe between points in the Point array.
	 * 
	 * @param segments
	 *            is Point array containing at least one Point object
	 * @param segmentSteps
	 *            steps to inject between two Points
	 * @return true on success
	 */
	public static boolean swipe(Point[] segments, int segmentSteps) {
		boolean ret = false;
		double xStep = 0;
		double yStep = 0;

		// avoid a divide by zero
		if (segmentSteps == 0)
			segmentSteps = 1;

		// must have some points
		if (segments.length == 0)
			return false;

		// first touch starts exactly at the point requested
		ret = touchDown(segments[0].x, segments[0].y);

		// SystemClock.sleep(getSystemLongPressTime());

		for (int seg = 0; seg < segments.length; seg++) {
			if (seg + 1 < segments.length) {
				if (DEBUG)
					Log.d(LOG_TAG, "segments[seg+1].x:" + segments[seg + 1].x
							+ "  ,segments[seg+1].y:" + segments[seg + 1].y);
				xStep = ((double) (segments[seg + 1].x - segments[seg].x))
						/ segmentSteps;
				yStep = ((double) (segments[seg + 1].y - segments[seg].y))
						/ segmentSteps;

				// for(int i = 1; i < swipeSteps; i++) {
				ret &= touchMove(segments[seg].x + (int) (xStep * 1),
						segments[seg].y + (int) (yStep * 1));
				if (ret == false)
					break;
				// set some known constant delay between steps as without it
				// this
				// become completely dependent on the speed of the system and
				// results
				// may vary on different devices. This guarantees at minimum we
				// have
				// a preset delay.
				SystemClock.sleep(5);
				// }
			}
		}
		SystemClock.sleep(REGULAR_CLICK_LENGTH);
		ret &= touchUp(segments[segments.length - 1].x,
				segments[segments.length - 1].y);
		return (ret);
	}

	/**
	 * Performs a multi-touch gesture
	 * 
	 * Takes a series of touch coordinates for at least 2 pointers. Each pointer
	 * must have all of its touch steps defined in an array of
	 * {@link PointerCoords}. By having the ability to specify the touch points
	 * along the path of a pointer, the caller is able to specify complex
	 * gestures like circles, irregular shapes etc, where each pointer may take
	 * a different path.
	 * 
	 * To create a single point on a pointer's touch path <code>
	 *       PointerCoords p = new PointerCoords();
	 *       p.x = stepX;
	 *       p.y = stepY;
	 *       p.pressure = 1;
	 *       p.size = 1;
	 * </code>
	 * 
	 * @param touches
	 *            each array of {@link PointerCoords} constitute a single
	 *            pointer's touch path. Multiple {@link PointerCoords} arrays
	 *            constitute multiple pointers, each with its own path. Each
	 *            {@link PointerCoords} in an array constitute a point on a
	 *            pointer's path.
	 * @return <code>true</code> if all points on all paths are injected
	 *         successfully, <code>false
	 *        </code>otherwise
	 * @since API Level 18
	 */
	public boolean performMultiPointerGesture(PointerCoords[]... touches) {
		boolean ret = true;
		if (touches.length < 2) {
			throw new IllegalArgumentException(
					"Must provide coordinates for at least 2 pointers");
		}

		// Get the pointer with the max steps to inject.
		int maxSteps = 0;
		for (int x = 0; x < touches.length; x++)
			maxSteps = (maxSteps < touches[x].length) ? touches[x].length
					: maxSteps;

		// specify the properties for each pointer as finger touch
		PointerProperties[] properties = new PointerProperties[touches.length];
		PointerCoords[] pointerCoords = new PointerCoords[touches.length];
		for (int x = 0; x < touches.length; x++) {
			PointerProperties prop = new PointerProperties();
			prop.id = x;
			prop.toolType = MotionEvent.TOOL_TYPE_FINGER;
			properties[x] = prop;

			// for each pointer set the first coordinates for touch down
			pointerCoords[x] = touches[x][0];
		}
		// Touch down all pointers
		long downTime = SystemClock.uptimeMillis();
		MotionEvent event;
		event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
				MotionEvent.ACTION_DOWN, 1, properties, pointerCoords, 0, 0, 1,
				1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
		ret &= injectEventSync(event);

		for (int x = 1; x < touches.length; x++) {
			event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
					getPointerAction(MotionEvent.ACTION_POINTER_DOWN, x),
					x + 1, properties, pointerCoords, 0, 0, 1, 1, 0, 0,
					InputDevice.SOURCE_TOUCHSCREEN, 0);
			ret &= injectEventSync(event);
		}

		// Move all pointers
		for (int i = 1; i < maxSteps - 1; i++) {
			// for each pointer
			for (int x = 0; x < touches.length; x++) {
				// check if it has coordinates to move
				if (touches[x].length > i)
					pointerCoords[x] = touches[x][i];
				else
					pointerCoords[x] = touches[x][touches[x].length - 1];
			}

			event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
					MotionEvent.ACTION_MOVE, touches.length, properties,
					pointerCoords, 0, 0, 1, 1, 0, 0,
					InputDevice.SOURCE_TOUCHSCREEN, 0);

			ret &= injectEventSync(event);
			SystemClock.sleep(MOTION_EVENT_INJECTION_DELAY_MILLIS);
		}

		// For each pointer get the last coordinates
		for (int x = 0; x < touches.length; x++)
			pointerCoords[x] = touches[x][touches[x].length - 1];

		// touch up
		for (int x = 1; x < touches.length; x++) {
			event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
					getPointerAction(MotionEvent.ACTION_POINTER_UP, x), x + 1,
					properties, pointerCoords, 0, 0, 1, 1, 0, 0,
					InputDevice.SOURCE_TOUCHSCREEN, 0);
			ret &= injectEventSync(event);
		}

		Log.i(LOG_TAG, "x " + pointerCoords[0].x);
		// first to touch down is last up
		event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
				MotionEvent.ACTION_UP, 1, properties, pointerCoords, 0, 0, 1,
				1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
		ret &= injectEventSync(event);
		return ret;
	}

	private int getPointerAction(int motionEnvent, int index) {
		return motionEnvent + (index << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
	}

	/**
	 * Generates a two-pointer gesture with arbitrary starting and ending
	 * points.
	 * 
	 * @param startPoint1
	 *            start point of pointer 1
	 * @param startPoint2
	 *            start point of pointer 2
	 * @param endPoint1
	 *            end point of pointer 1
	 * @param endPoint2
	 *            end point of pointer 2
	 * @param steps
	 *            the number of steps for the gesture. Steps are injected about
	 *            5 milliseconds apart, so 100 steps may take around 0.5 seconds
	 *            to complete.
	 * @return <code>true</code> if all touch events for this gesture are
	 *         injected successfully, <code>false</code> otherwise
	 * @since API Level 18
	 */
	public boolean performTwoPointerGesture(Point startPoint1,
			Point startPoint2, Point endPoint1, Point endPoint2, int steps) {

		// avoid a divide by zero
		if (steps == 0)
			steps = 1;

		final float stepX1 = (endPoint1.x - startPoint1.x) / steps;
		final float stepY1 = (endPoint1.y - startPoint1.y) / steps;
		final float stepX2 = (endPoint2.x - startPoint2.x) / steps;
		final float stepY2 = (endPoint2.y - startPoint2.y) / steps;

		int eventX1, eventY1, eventX2, eventY2;
		eventX1 = startPoint1.x;
		eventY1 = startPoint1.y;
		eventX2 = startPoint2.x;
		eventY2 = startPoint2.y;

		// allocate for steps plus first down and last up
		PointerCoords[] points1 = new PointerCoords[steps + 2];
		PointerCoords[] points2 = new PointerCoords[steps + 2];

		// Include the first and last touch downs in the arrays of steps
		for (int i = 0; i < steps + 1; i++) {
			PointerCoords p1 = new PointerCoords();
			p1.x = eventX1;
			p1.y = eventY1;
			p1.pressure = 1;
			p1.size = 1;
			points1[i] = p1;

			PointerCoords p2 = new PointerCoords();
			p2.x = eventX2;
			p2.y = eventY2;
			p2.pressure = 1;
			p2.size = 1;
			points2[i] = p2;

			eventX1 += stepX1;
			eventY1 += stepY1;
			eventX2 += stepX2;
			eventY2 += stepY2;
		}

		// ending pointers coordinates
		PointerCoords p1 = new PointerCoords();
		p1.x = endPoint1.x;
		p1.y = endPoint1.y;
		p1.pressure = 1;
		p1.size = 1;
		points1[steps + 1] = p1;

		PointerCoords p2 = new PointerCoords();
		p2.x = endPoint2.x;
		p2.y = endPoint2.y;
		p2.pressure = 1;
		p2.size = 1;
		points2[steps + 1] = p2;

		return performMultiPointerGesture(points1, points2);
	}

	public boolean sendText(String text) {
		if (DEBUG) {
			Log.d(LOG_TAG, "sendText (" + text + ")");
		}

		KeyEvent[] events = mKeyCharacterMap.getEvents(text.toCharArray());

		if (events != null) {
			long keyDelay = Configurator.getInstance().getKeyInjectionDelay();
			for (KeyEvent event2 : events) {
				// We have to change the time of an event before injecting it
				// because
				// all KeyEvents returned by KeyCharacterMap.getEvents() have
				// the same
				// time stamp and the system rejects too old events. Hence, it
				// is
				// possible for an event to become stale before it is injected
				// if it
				// takes too long to inject the preceding ones.
				KeyEvent event = KeyEvent.changeTimeRepeat(event2,
						SystemClock.uptimeMillis(), 0);
				if (!injectEventSync(event)) {
					return false;
				}
				SystemClock.sleep(keyDelay);
			}
		}
		return true;
	}

	public boolean sendKey(int keyCode, int metaState) {
		if (DEBUG) {
			Log.d(LOG_TAG, "sendKey (" + keyCode + ", " + metaState + ")");
		}

		final long eventTime = SystemClock.uptimeMillis();
		KeyEvent downEvent = new KeyEvent(eventTime, eventTime,
				KeyEvent.ACTION_DOWN, keyCode, 0, metaState,
				KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
				InputDevice.SOURCE_KEYBOARD);
		if (injectEventSync(downEvent)) {
			KeyEvent upEvent = new KeyEvent(eventTime, eventTime,
					KeyEvent.ACTION_UP, keyCode, 0, metaState,
					KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
					InputDevice.SOURCE_KEYBOARD);
			if (injectEventSync(upEvent)) {
				return true;
			}
		}
		return false;
	}

	public boolean sendLongKey(int keyCode, int metaState) {
		if (DEBUG) {
			Log.d(LOG_TAG, "sendKey (" + keyCode + ", " + metaState + ")");
		}

		final long eventTime = SystemClock.uptimeMillis();
		KeyEvent downEvent = KeyEvent.obtain(eventTime, eventTime,
				KeyEvent.ACTION_DOWN, keyCode, 0, metaState,
				KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
				InputDevice.SOURCE_KEYBOARD, null);
		if (injectEventSync(downEvent)) {
			SystemClock.sleep(mLongPressTimeout);
			KeyEvent upEvent = KeyEvent.obtain(eventTime, eventTime,
					KeyEvent.ACTION_UP, keyCode, 0, metaState,
					KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
					InputDevice.SOURCE_KEYBOARD, null);
			if (injectEventSync(upEvent)) {
				// sleep(300);
				return true;
			}
		}
		return false;
	}

	/**
	 * Just can send Power key,cannot send Home key.
	 * 
	 * @return boolean
	 */
	public boolean sendPowerKeyLong() {
		if (DEBUG) {
			Log.d(LOG_TAG, "sendPowerKeyLong ()");
		}

		final long eventTime = SystemClock.uptimeMillis();
		KeyEvent downEvent = KeyEvent.obtain(eventTime, eventTime,
				KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER, 0, 0,
				KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
				InputDevice.SOURCE_KEYBOARD, null);
		if (injectEventSync(downEvent)) {
			SystemClock.sleep(mLongPressTimeout);
			KeyEvent upEvent = KeyEvent.obtain(eventTime, eventTime,
					KeyEvent.ACTION_UP, KeyEvent.KEYCODE_POWER, 0, 0,
					KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
					InputDevice.SOURCE_KEYBOARD, null);
			if (injectEventSync(upEvent)) {
				// sleep(300);
				return true;
			}
		}
		return false;
	}

	public static void sleep(long time) {
		SystemClock.sleep(time);
	}

	/**
	 * Check if the device is in its natural orientation. This is determined by
	 * checking whether the orientation is at 0 or 180 degrees.
	 * 
	 * @return true if it is in natural orientation
	 * @throws RemoteException
	 */
	public boolean isNaturalRotation() throws RemoteException {
		return mWindowManager.getRotation() == Surface.ROTATION_0
				|| mWindowManager.getRotation() == Surface.ROTATION_180;
	}

	public int getRotation() throws RemoteException {
		return mWindowManager.getRotation();
	}

	/**
	 * Rotates right and also freezes rotation in that position by disabling the
	 * sensors. If you want to un-freeze the rotation and re-enable the sensors
	 * see {@link #unfreezeRotation()}. Note that doing so may cause the screen
	 * contents to rotate depending on the current physical position of the test
	 * device.
	 * 
	 * @throws RemoteException
	 */
	public void setRotationRight() throws RemoteException {
		mWindowManager.freezeRotation(Surface.ROTATION_270);
	}

	/**
	 * Rotates left and also freezes rotation in that position by disabling the
	 * sensors. If you want to un-freeze the rotation and re-enable the sensors
	 * see {@link #unfreezeRotation()}. Note that doing so may cause the screen
	 * contents to rotate depending on the current physical position of the test
	 * device.
	 * 
	 * @throws RemoteException
	 */
	public void setRotationLeft() throws RemoteException {
		mWindowManager.freezeRotation(Surface.ROTATION_90);
	}

	public boolean setRotation(int rotation) throws RemoteException {

		Log.d(LOG_TAG, "setRotation:" + rotation);
		final long identity = Binder.clearCallingIdentity();
		try {
			if (rotation == -2) {
				mWindowManager.thawRotation();
			} else {
				mWindowManager.freezeRotation(rotation);
			}
			return true;
		} catch (RemoteException re) {
			re.printStackTrace();
		} finally {
			Binder.restoreCallingIdentity(identity);
		}
		return false;
	}

	/**
	 * Rotates up and also freezes rotation in that position by disabling the
	 * sensors. If you want to un-freeze the rotation and re-enable the sensors
	 * see {@link #unfreezeRotation()}. Note that doing so may cause the screen
	 * contents to rotate depending on the current physical position of the test
	 * device.
	 * 
	 * @throws RemoteException
	 */
	public void setRotationNatural() throws RemoteException {
		mWindowManager.freezeRotation(Surface.ROTATION_0);
	}

	/**
	 * Disables the sensors and freezes the device rotation at its current
	 * rotation state.
	 * 
	 * @throws RemoteException
	 */
	public void freezeRotation() throws RemoteException {
		mWindowManager.freezeRotation(-1);
	}

	/**
	 * Re-enables the sensors and un-freezes the device rotation allowing its
	 * contents to rotate with the device physical rotation.
	 * 
	 * @throws RemoteException
	 */
	public void unfreezeRotation() throws RemoteException {
		mWindowManager.thawRotation();
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
		WindowManager manager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
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
		IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager
				.getService(Context.POWER_SERVICE));
		return true;
	}

	private static boolean injectEventSync(InputEvent event) {
		if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) == 0) {
			event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		}
		return InputManager.getInstance().injectInputEvent(event,
				InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
	}

	public void sendMenuKeyLong() {
		final long token = Binder.clearCallingIdentity();

		IStatusBarService statusBarService = IStatusBarService.Stub
				.asInterface(ServiceManager.getService("statusbar"));
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

		IStatusBarService statusBarService = IStatusBarService.Stub
				.asInterface(ServiceManager.getService("statusbar"));
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
	 * @param time
	 *            press time
	 * @return true if successful.
	 */
	public boolean longTap(float x, float y, int time) {
		boolean successfull = false;
		int retry = 0;
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime, 0, x, y, 0);

		while ((!successfull) && (retry < 10)) {
			try {
				injectEventSync(event);
				successfull = true;
			} catch (SecurityException e) {
				e.printStackTrace();
				retry++;
			}
		}
		if (!successfull) {
			Assert.assertTrue("Click can not be completed!", false);
		}

		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, 2, x + 1.0F, y + 1.0F,
				0);

		injectEventSync(event);
		if (time > 0)
			sleep(time);
		else {
			sleep((int) (mLongPressTimeout));
		}
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, 1, x, y, 0);
		injectEventSync(event);
		return successfull;
	}

	/**
	 * clear EditText
	 * 
	 * @param text
	 * @throws Exception
	 */
	public void clearStringSync(String text) throws Exception {
		if (text == null) {
			return;
		}
		int n = text.length();
		KeyEvent event = new KeyEvent(System.currentTimeMillis(),
				System.currentTimeMillis(), KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, 0, 0);
		for (int i = 0; i < n; i++) {
			sendKeySync(KeyEvent.changeTimeRepeat(event,
					SystemClock.uptimeMillis(), 0));
		}
	}

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
		KeyEvent newEvent = new KeyEvent(downTime, eventTime, action, code,
				repeatCount, metaState, deviceId, scancode, flags
						| KeyEvent.FLAG_FROM_SYSTEM, source);
		InputManager.getInstance().injectInputEvent(newEvent,
				InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
	}

	private static final char ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR = ':';
	private final static SimpleStringSplitter sStringColonSplitter = new SimpleStringSplitter(
			ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR);

	public static Set<ComponentName> getEnabledServicesFromSettings(
			Context context) {
		String enabledServicesSetting = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.ENABLED_INPUT_METHODS);
		if (enabledServicesSetting == null) {
			enabledServicesSetting = "";
		}
		Set<ComponentName> enabledServices = new HashSet<ComponentName>();
		SimpleStringSplitter colonSplitter = sStringColonSplitter;
		colonSplitter.setString(enabledServicesSetting);
		while (colonSplitter.hasNext()) {
			String componentNameString = colonSplitter.next();
			ComponentName enabledService = ComponentName
					.unflattenFromString(componentNameString);
			if (enabledService != null) {
				enabledServices.add(enabledService);
			}
		}
		return enabledServices;
	}

	public String getCurrentPackage() {
		List<RunningAppProcessInfo> appProcess = activityManager
				.getRunningAppProcesses();
		if (appProcess == null) {
			return null;
		}
		String packageName = activityManager.getRunningTasks(1).get(0).topActivity
				.getPackageName();
		return packageName;
	}

	public void killPackage(int delayTime, String pkgName) {

		SystemClock.sleep(delayTime);
		// sendKey(KeyEvent.KEYCODE_BACK, 0);
		sendKey(KeyEvent.KEYCODE_HOME, 0);
		SystemClock.sleep(1000);
		Intent intent = new Intent(
				"apptest.intent.action.REQUEST_APP_CLEAN_RUNNING");
		Bundle b = new Bundle();
		b.putString("KillAppFilterName", "com.apptest.monitor");
		intent.putExtras(b);
		mContext.startService(intent);
		SystemClock.sleep(1000);

		// Intent intent = new Intent();
		// intent.setClass(mContext,
		// com.apptest.uiautomator.ClearRunningAppService.class);
		// mContext.startService(intent);
		SystemClock.sleep(1000);

		// ClearAllRunning();
		// OldClearAllRunning();
	}

	public boolean isAppRunning(String targetPackage) {
		List<RunningAppProcessInfo> appProcessList = activityManager
				.getRunningAppProcesses();
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

	private boolean hasSettings = false;

	private void ClearAllRunning() {

		ArrayList<RecentTaskInfo> mRunningAppList = getRunningAppList();
		final ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		int lastLeft = (int) getAvailMemory();
		int number = 0;
		int numTasks = mRunningAppList.size();
		OppoActivityManager oAm = new OppoActivityManager();
		ComponentName cn = null;
		try {
			cn = oAm.getTopActivityComponentName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String frontPackageName =
		// (mRunningAppList.get(0)).baseIntent.getComponent().getPackageName();
		String frontPackageName = "null";
		if (cn != null) {
			if (cn.getPackageName() != null) {
				frontPackageName = cn.getPackageName();
			}
		}

		// skip the first task - assume it's either the home screen or the
		// current activity.
		for (int i = 1; i < numTasks; i++) {
			RecentTaskInfo taskInfo = mRunningAppList.get(i);
			String pkgName = taskInfo.baseIntent.getComponent()
					.getPackageName();
			boolean app_lock = readLockFlag(pkgName);
			boolean isDelete = true;

			if (getHomes().contains(
					taskInfo.baseIntent.getComponent().getPackageName())
					|| getLocks()
							.contains(
									taskInfo.baseIntent.getComponent()
											.getPackageName())
					|| (frontPackageName != null && taskInfo.baseIntent
							.getComponent().getPackageName()
							.equals(frontPackageName))) {
				isDelete = false;
			}

			if (pkgName.equals("com.apptest.webcontrol")) {
				isDelete = false;
			}
			if (pkgName.equals("com.apptest.launcher")) {
				isDelete = false;
			}
			if (pkgName.equals("com.apptest.test.testinput")) {
				isDelete = false;
			}

			if (!app_lock && isDelete) {
				am.removeTask(taskInfo.persistentId);
				Log.e(LOG_TAG, "onStartCommand removeTask ="
						+ taskInfo.baseIntent.getComponent().getPackageName());
				number++;
			}
		}

		// some apps use the same package name,so remove package after
		// remove all tasks
		List<ActivityManager.RunningAppProcessInfo> mRunningList = getRunningAppList(mContext);
		numTasks = mRunningList.size();
		int iflytekPid = 0;
		Boolean isSavedList = getSavedBootCompletedAppListValue(mContext);

		if (isSavedList) {
			for (int i = 1; i < numTasks; i++) {
				ActivityManager.RunningAppProcessInfo runningAppProcessInfo = mRunningList
						.get(i);
				int size = runningAppProcessInfo.pkgList.length;
				String packageName = null;
				int uid = 0;
				for (int j = 0; j < size; j++) {
					ApplicationInfo appInfo = null;
					try {
						appInfo = mContext
								.getPackageManager()
								.getApplicationInfo(
										runningAppProcessInfo.pkgList[j],
										PackageManager.GET_UNINSTALLED_PACKAGES);
					} catch (NameNotFoundException e) {
						Log.w(LOG_TAG,
								"Error retrieving ApplicationInfo for pkg:"
										+ runningAppProcessInfo.pkgList[j]);
						continue;
					}
					if (appInfo != null) {
						packageName = appInfo.packageName;
						uid = appInfo.uid;
					}
				}
				boolean app_lock = readLockFlag(packageName);
				boolean isDelete = true;

				if (packageName.equals("com.apptest.webcontrol")) {
					isDelete = false;
				}
				if (packageName.equals("com.apptest.launcher")) {
					isDelete = false;
				}

				if (packageName.equals("com.apptest.test.testinput")) {
					isDelete = false;
				}
				if ("com.apptest.test.testinput"
						.equals(runningAppProcessInfo.processName)) {
					isDelete = false;
				}

				// kill com.iflytek.speech must kill
				// com.apptest.speechassist.engine first
				if ("com.iflytek.speech"
						.equals(runningAppProcessInfo.processName)) {
					iflytekPid = runningAppProcessInfo.pid;
				}
				// Log.e(TAG, "applicationInfo.packageName = " +
				// applicationInfo.packageName);
				if ((frontPackageName != null && packageName
						.equals(frontPackageName))
						|| getHomes().contains(packageName)
						|| getLocks().contains(packageName)
						|| getBooleanValue(mContext,
								runningAppProcessInfo.processName)
						|| findInputMethods(packageName)
						|| findLiveWallpapers(packageName)
						|| sSkipProcess
								.contains(runningAppProcessInfo.processName)) {
					isDelete = false;
				}

				if (!app_lock && isDelete) {
					if (("com.android.settings").equals(packageName)) {
						hasSettings = true;
					} else {
						if (true/* !OppoSettingsUtils.isExViersion() */) {
							// am.forceStopPackage(applicationInfo.packageName);
							Log.e(LOG_TAG, "onStartCommand killProcess pid ="
									+ runningAppProcessInfo.pid);
							Log.e(LOG_TAG,
									"onStartCommand killProcess packageName ="
											+ packageName);
							Log.e(LOG_TAG,
									"onStartCommand killProcess processName ="
											+ runningAppProcessInfo.processName);
							String audioPid = getActiveAudioPids();
							if (audioPid != null
									&& !"com.apptest.speechassist.engine"
											.equals(runningAppProcessInfo.processName)) {
								if (!audioPid.contains(Integer
										.toString(runningAppProcessInfo.pid))
										|| ("com.apptest.music")
												.equals(packageName)) {
									Log.e(LOG_TAG,
											"onStartCommand killProcess kill");
									// android.os.Process.killProcessQuiet(runningAppProcessInfo.pid);
									// am.forceStopPackage(packageName);
									try {
										new OppoActivityManager()
												.killPidForce(runningAppProcessInfo.pid);
									} catch (Exception e) {
									}
									forceStopPackageLocked(packageName, uid);
								}
							} else {
								Log.e(LOG_TAG,
										"onStartCommand killProcess kill");
								// kill com.iflytek.speech must kill
								// com.apptest.speechassist.engine first
								// android.os.Process.killProcessQuiet(runningAppProcessInfo.pid);
								// am.forceStopPackage(packageName);
								try {
									new OppoActivityManager()
											.killPidForce(runningAppProcessInfo.pid);
								} catch (Exception e) {
								}
								forceStopPackageLocked(packageName, uid);
								if ("com.apptest.speechassist.engine"
										.equals(runningAppProcessInfo.processName)
										&& iflytekPid != 0) {
									Log.e(LOG_TAG,
											"onStartCommand killProcess iflytekPid = "
													+ iflytekPid);
									// android.os.Process.killProcessQuiet(iflytekPid);
									// am.forceStopPackage("com.iflytek.speech");
									try {
										new OppoActivityManager()
												.killPidForce(iflytekPid);
									} catch (Exception e) {
									}
								}
							}
						}
					}
				}
			}
		}

		/*
		 * try { Thread.sleep(500); } catch (InterruptedException e) {
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		int clean = (int) getAvailMemory() - lastLeft;
		if (clean < 0 || number == 0) {
			clean = 0;
		}

		if (hasSettings) {
			Log.e(LOG_TAG, "CLEAE_ALL_RUNNING_FINISH");
			hasSettings = false;
			clearRunningFinish();
			// sleep 500ms so that the toast can show before settings be
			// force stop
			if (true/* !OppoSettingsUtils.isExViersion() */) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				am.forceStopPackage("com.android.settings");
			}
		}
	}

	private void forceStopPackageLocked(String packageName, int uid) {
		Intent intent = new Intent(Intent.ACTION_PACKAGE_RESTARTED,
				Uri.fromParts("package", packageName, null));
		/*
		 * if (!mProcessesReady) {
		 * intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY |
		 * Intent.FLAG_RECEIVER_FOREGROUND); }
		 */
		intent.putExtra(Intent.EXTRA_UID, uid);
		intent.putExtra(Intent.EXTRA_USER_HANDLE, UserHandle.getUserId(uid));
		mContext.sendBroadcast(intent);
	}

	private void clearRunningFinish() {
		Intent intent = new Intent(
				"android.intent.action.FORCE_CLOSE_ALL_PROCESS");
		mContext.sendBroadcast(intent);
	}

	ArrayList<RecentTaskInfo> getRunningAppList() {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		final ArrayList<RecentTaskInfo> taskList = (ArrayList<RecentTaskInfo>) am
				.getRecentTasks(21, ActivityManager.RECENT_IGNORE_UNAVAILABLE);

		return taskList;
	}

	List<ActivityManager.RunningAppProcessInfo> getRunningAppList(
			Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> procList = am
				.getRunningAppProcesses();
		return procList;
	}

	public boolean readLockFlag(String packageName) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("package_name", packageName);

		if (mContext.getContentResolver().update(
				Uri.parse("content://com.android.systemui.applock"),
				contentValues, null, null) == 1) {
			return true;
		} else {
			return false;
		}
	}

	private long getAvailMemory() {
		IActivityManager am = ActivityManagerNative.getDefault();
		MemoryInfo mi = new MemoryInfo();
		try {
			am.getMemoryInfo(mi);
		} catch (RemoteException e) {
		}
		return mi.availMem;
	}

	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = mContext.getPackageManager();

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
			Log.e(LOG_TAG, "getHomes packageName ="
					+ ri.activityInfo.packageName);
		}

		return names;
	}

	private List<String> getLocks() {
		Log.e(LOG_TAG, "getLocks start");

		List<String> names = new ArrayList<String>();
		PackageManager packageManager = mContext.getPackageManager();

		List<ResolveInfo> resolveInfo = mContext.getPackageManager()
				.queryIntentServices(new Intent("apptest.intent.action.keyguard"),
						PackageManager.GET_META_DATA);

		Log.e(LOG_TAG, "getLocks resolveInfo = " + resolveInfo);

		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.serviceInfo.packageName);
			Log.e(LOG_TAG, "getLocks packageName ="
					+ ri.serviceInfo.packageName);
		}

		return names;
	}

	private Boolean getSavedBootCompletedAppListValue(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				"boot_completed_app_list", Context.MODE_PRIVATE);
		return sp.getBoolean("SavedBootCompletedAppList", false);
	}

	private Boolean getBooleanValue(Context context, String string) {
		SharedPreferences sp = context.getSharedPreferences(
				"boot_completed_app_list", Context.MODE_PRIVATE);
		return sp.getBoolean(string, false);

	}

	private boolean findInputMethods(String string) {
		List<ResolveInfo> list = mContext.getPackageManager()
				.queryIntentServices(new Intent(InputMethod.SERVICE_INTERFACE),
						PackageManager.GET_META_DATA);

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
			String className = info.getServiceName();

			if (packageName.equals(string)) {
				return true;
			}
		}
		return false;
	}

	public boolean findLiveWallpapers(String string) {
		List<ResolveInfo> list = mContext.getPackageManager()
				.queryIntentServices(
						new Intent(WallpaperService.SERVICE_INTERFACE),
						PackageManager.GET_META_DATA);

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
			String className = info.getServiceName();

			if (packageName.equals(string)) {
				return true;
			}
		}
		return false;
	}

	private String getActiveAudioPids() {
		AudioManager AudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);

		String pids = AudioManager.getParameters("get_pid");
		Log.i(LOG_TAG, "getActiveAudioPids pids = " + pids);
		if (pids == null || pids.length() == 0) {
			return null;
		}
		return pids;
	}

	private static List<String> sSkipProcess = Arrays.asList(new String[] {
			"com.tencent.mm:push", "com.android.phone", "com.android.systemui",
			"com.android.systemui:screenshot", "com.apptest.OppoSimUnlockScreen",
			"com.apptest.kinect", "android.process.acore",
			"eyesight.service.service", "com.apptest.maxxaudio",
			"com.android.bluetooth", "com.mediatek.bluetooth",
			"com.apptest.usbselection", "com.android.facelock", "system" });

	/**
	 * 解压缩功�? 将zipFile文件解压到folderPath目录�?
	 * 
	 * @param zipFile
	 *            解压文件
	 * @param folderPath
	 *            解压到路�?
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 *             int
	 */
	public int upZipFile(File zipFile, String folderPath) throws ZipException,
			IOException {
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
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getRealFileName(folderPath, ze.getName())));
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
	 * 给定根目录，返回�?��相对路径�?��应的实际文件�?
	 * 
	 * @param baseDir
	 *            指定根目�?
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文�?
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
	 * @throws Exception
	 *             void
	 */
	public void cut_File(String from, String to) {
		try {
			Process p = Runtime.getRuntime().exec("/system/bin/su");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					p.getOutputStream()));
			String cmd = "cp -r -f " + from + " " + to;
			writer.write(cmd);
			writer.flush();
			SystemClock.sleep(4000);
			writer.close();
			p.destroy();
			Process p2 = Runtime.getRuntime().exec("/system/bin/su");
			BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(
					p2.getOutputStream()));
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

	private void OldClearAllRunning() {

		ArrayList<RecentTaskInfo> mRunningAppList = getRunningAppList();
		final ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		boolean hasSettings = false;
		int numTasks = mRunningAppList.size();

		for (int i = 0; i < numTasks; i++) {
			RecentTaskInfo taskInfo = mRunningAppList.get(i);
			boolean app_lock = readLockFlag(taskInfo.baseIntent.getComponent()
					.getPackageName());
			boolean isDelete = true;

			String pkgName = taskInfo.baseIntent.getComponent()
					.getPackageName();

			if (pkgName.equals("com.apptest.webcontrol")) {
				isDelete = false;
			}
			if (pkgName.equals("com.apptest.launcher")) {
				isDelete = false;
			}
			if (pkgName.equals("com.apptest.test.testinput")) {
				isDelete = false;
			}

			if (getHomes().contains(pkgName)) {
				isDelete = false;
			}

			if (!app_lock && isDelete) {
				am.removeTask(taskInfo.persistentId);
				Log.e(LOG_TAG, "onStartCommand removeTask ="
						+ taskInfo.baseIntent.getComponent().getPackageName());
			}
		}

		// some apps use the same package name,so remove package after
		// remove all tasks
		for (int i = 0; i < numTasks; i++) {
			RecentTaskInfo taskInfo = mRunningAppList.get(i);
			String pkgName = taskInfo.baseIntent.getComponent()
					.getPackageName();

			boolean app_lock = readLockFlag(pkgName);
			boolean isDelete = true;

			if (pkgName.equals("com.apptest.webcontrol")) {
				isDelete = false;
			}
			if (pkgName.equals("com.apptest.launcher")) {
				isDelete = false;
			}

			if (pkgName.equals("com.apptest.test.testinput")) {
				isDelete = false;
			}

			// if some apps use the same package name,and one in the
			// front,then do not force stop it
			// do not force stop launchers
			if (getHomes().contains(pkgName) || findInputMethods(pkgName)) {
				isDelete = false;
			}

			if (!app_lock && isDelete) {
				if (("com.android.settings").equals(pkgName)) {
					hasSettings = true;
				} else {
					Log.e(LOG_TAG, "onStartCommand forceStopPackage ="
							+ pkgName);
					if (!isExViersion()) {
						am.forceStopPackage(pkgName);
					}
				}
			}
		}

		if (hasSettings) {
			Log.e(LOG_TAG, "CLEAE_ALL_RUNNING_FINISH");
			hasSettings = false;
			clearRunningFinish();
			// sleep 500ms so that the toast can show before settings be
			// force stop
			if (!isExViersion()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				am.forceStopPackage("com.android.settings");
			}
		}
	}

	public static boolean isExViersion() {
		final String Default_Region = "CN";
		String region = SystemProperties.get("persist.sys.apptest.region",
				Default_Region);
		return !region.equals(Default_Region);
	}

}
