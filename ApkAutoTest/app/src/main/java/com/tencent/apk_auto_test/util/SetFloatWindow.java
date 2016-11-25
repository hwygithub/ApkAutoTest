package com.tencent.apk_auto_test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

/**
 * Set Float Window State model class
 * 
 * @author hwy 2014-10-13
 * 
 */
public class SetFloatWindow {
	private static final String TAG = "SetFloatWindow";

	public static void setPackageNameList(File file,
			List<String> packageNameList, int mode) {
		List<String> packageNameFileList = getPackageNameList(file);
		for (int i = 0; i < packageNameList.size(); i++) {
			String packageName = packageNameList.get(i);
			Log.d(TAG,
					"setPackageNameList packageName: " + packageName
							+ " mode: " + mode + " contains: "
							+ packageNameFileList.contains(packageName));
			if (isPermitNotDisplay(packageName)) {
				Log.d(TAG, "isPermitNotDisplay");
				continue;
			}

			if ((packageNameFileList.contains(packageName) && (FILE_MODE_SAVE & ~mode) == 0)
					|| (!packageNameFileList.contains(packageName) && (FILE_MODE_DEL & ~mode) == 0)) {
				Log.e(TAG, "setPackageNameList error return");
				return;
			} else if (packageNameFileList.contains(packageName)
					&& (FILE_MODE_DEL & ~mode) == 0) {
				packageNameFileList.remove(packageName);
			} else if (!packageNameFileList.contains(packageName)
					&& (FILE_MODE_SAVE & ~mode) == 0) {
				packageNameFileList.add(packageName);
			}
		}

		try {
			if (!file.exists()) {
				(new File(DIR)).mkdirs();
			}

			FileOutputStream fos = new FileOutputStream(file);

			Iterator<String> iterator = packageNameFileList.iterator();
			while (iterator.hasNext()) {
				String str = iterator.next();
				fos.write((str + "\n").getBytes());
			}
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getPackageNameList(File file) {
		List<String> packageNameList = new ArrayList<String>();
		if (!file.exists()) {
			return packageNameList;
		}

		try {
			FileReader fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);

			String line = null;
			while (!TextUtils.isEmpty((line = reader.readLine()))) {
				packageNameList.add(new String(line));
			}

			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return packageNameList;
	}

	public static boolean isPermitNotDisplay(String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		for (String element : PERMIT_NOT_DISPLAY) {
			if (packageName.equals(element)) {
				return true;
			}
		}
		return false;
	}

	public static final String IS_FLOAT_WINDOW_FIRST_STARTED = "is_float_window_first_started";
	public static final String DIR = "//data//apptest//floatwindow//";
	public static final String BLACK_LIST_FILE_NAME = "FloatWindowBlackList.txt";
	public static final File BLACK_LIST_FILE = new File(DIR
			+ BLACK_LIST_FILE_NAME);

	public static final int FILE_MODE_SAVE = 1 << 0;
	public static final int FILE_MODE_DEL = 1 << 1;

	public static final String PERMIT_NOT_DISPLAY[] = { "com.miui.mihome2",
			"com.miui.miuilite" };
}