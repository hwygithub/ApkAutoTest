package com.tencent.apk_auto_test.receiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.util.Time;
import com.tencent.apk_auto_test.util.TxtUtil;
import com.tencent.apk_auto_test.services.RunService;
import com.tencent.apk_auto_test.services.unLockService;
import com.test.function.Operate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class BatteryReceiver extends BroadcastReceiver {
	// static
	protected static final int RUN_TEST = 2;
	protected static final int END_TEST = 0;

	private int mLevel = 0;
	private static String mFileName;
	private int mCurRow = 0;
	public static String DIRCTORY = "/MpBatteryAutoTest";
	public static final String OutFilePath = Environment.getExternalStorageDirectory() + DIRCTORY;
	private boolean mRunningFlag = true;
	private static Context mContext;
	private int mLastLevel = 0;
	private long mLastTime = 0;
	private boolean mFirstFlag = true;
	public long mStartTime = 0;
	public String name;
	public String mTestTime;
	private int mRunTimes = 0;
	private int voltage = 0;
	// data
	private int index = 10;
	// class
	private Handler mHandler;
	private Operate mOperate;

	public BatteryReceiver(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		mOperate = new Operate(mContext);
		platformIsQcom = isQcomOrMTK();
		Log.e("hwy", Environment.getExternalStorageDirectory() + "");
	}

	public void setName(String name) {
		this.name = name;
		mTestTime = Time.getCurrentTimeSecond();
		mFileName = name + mTestTime + ".xls";
		recordHeader();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		StringBuilder sb = new StringBuilder();
		int rawlevel = intent.getIntExtra("level", -1);
		int scale = intent.getIntExtra("scale", -1);
		int status = intent.getIntExtra("status", -1);
		int health = intent.getIntExtra("health", -1);
		voltage = intent.getIntExtra("voltage", -1);
		mLevel = -1; // percentage, or -1 for unknown
		if (rawlevel >= 0 && scale > 0) {
			mLevel = (rawlevel * 100) / scale;
		}
		// stop test when the level reach the min level
		if (mLevel <= StaticData.minLevel) {
			stopTest(context);
		}
		// Click if the low power alert show
	}

	public void stopTest(final Context context) {
		Intent intentToFire = new Intent();
		intentToFire.setClass(mContext, unLockService.class);
		unLockService.mKeepScreenFlag = false;
		context.startService(intentToFire);
		// write the last case log
		TxtUtil.saveMsg(OutFilePath, "testIndex:" + RunService.testNumber, mFileName, mContext, index, 0);
		TxtUtil.saveMsg(OutFilePath, Time.getCurrentTimeSecond(), mFileName, mContext, index, 1);
		TxtUtil.saveMsg(OutFilePath, Time.getPassTime(StaticData.caseStartTime, Time.getCurrentTime()), mFileName,
				mContext, index, 2);
		TxtUtil.saveMsg(OutFilePath, mLevel + "%", mFileName, mContext, index, 3);
		TxtUtil.saveMsg(OutFilePath, (float) voltage / 1000 + "v", mFileName, mContext, index, 4);
		TxtUtil.saveMsg(OutFilePath, (mLastLevel - mLevel) + "%", mFileName, mContext, index, 5);
		TxtUtil.saveMsg(OutFilePath, "用例名称 todo", mFileName, mContext, index, 6);

		StaticData.testFinishEvent = this.mContext.getResources().getString(R.string.txt_finish_event);
		mHandler.sendEmptyMessageDelayed(END_TEST, 5000);
	}

	public void recordHeader() {

	}

	public static boolean isQcomOrMTK() {
		boolean isQcom = true;
		String HARDWARE = android.os.Build.HARDWARE;
		if (HARDWARE.contains("qcom")) {
			isQcom = true;
		} else {
			isQcom = false;
		}
		return isQcom;
	}

	public void printStartLevel() {
		TxtUtil.saveMsg(OutFilePath, mContext.getResources().getString(R.string.startLevel), mFileName, mContext, 6, 2);
		TxtUtil.saveMsg(OutFilePath, mLevel + "%", mFileName, mContext, 6, 3);
		TxtUtil.saveMsg(OutFilePath, mContext.getResources().getString(R.string.startVoltage), mFileName, mContext, 5,
				2);
		TxtUtil.saveMsg(OutFilePath, (float) voltage / 1000 + "v", mFileName, mContext, 5, 3);
		mLastLevel = mLevel;
	}

	public void printLog(int testNumber) {
		if (mLevel == 0) {
			SystemClock.sleep(1000);
			printLog(testNumber);
		} else {

			StringBuilder sb = new StringBuilder();
			TxtUtil.saveMsg(OutFilePath, "testIndex:" + testNumber, mFileName, mContext, index, 0);
			TxtUtil.saveMsg(OutFilePath, Time.getCurrentTimeSecond(), mFileName, mContext, index, 1);
			TxtUtil.saveMsg(OutFilePath, Time.getPassTime(StaticData.caseStartTime, Time.getCurrentTime()), mFileName,
					mContext, index, 2);
			TxtUtil.saveMsg(OutFilePath, mLevel + "%", mFileName, mContext, index, 3);
			TxtUtil.saveMsg(OutFilePath, (float) voltage / 1000 + "v", mFileName, mContext, index, 4);
			TxtUtil.saveMsg(OutFilePath, (mLastLevel - mLevel) + "%", mFileName, mContext, index, 5);
			TxtUtil.saveMsg(OutFilePath, "用例名称 todo", mFileName, mContext, index, 6);
			index++;
			mLastLevel = mLevel;
			// test next
			mHandler.sendEmptyMessage(RUN_TEST);
		}
	}

	public static void writeLog(String s, int rom, int col) {
		TxtUtil.saveMsg(OutFilePath, s, mFileName, mContext, rom, col);
	}

	private boolean platformIsQcom = false;

	public float getCurrent() {
		try {
			Process mLogcatProc;
			String line;
			if (platformIsQcom) {
				mLogcatProc = Runtime.getRuntime().exec("cat /sys/class/power_supply/battery/current_now");
			} else {
				mLogcatProc = Runtime.getRuntime().exec("cat /sys/class/power_supply/battery/BatteryAverageCurrent");
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
			while ((line = reader.readLine()) != null) {
				Log.e("getCurrent ", line + "");
				return Float.parseFloat(line);
			}
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}
}
