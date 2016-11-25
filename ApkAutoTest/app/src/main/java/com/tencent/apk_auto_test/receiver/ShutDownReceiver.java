package com.tencent.apk_auto_test.receiver;

import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.util.Time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutDownReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// write log
		BatteryReceiver.writeLog(Time.getCurrentTimeSecond(), 4, 1);
		BatteryReceiver.writeLog(
				Time.getPassTimeString(StaticData.testStartTime,
						Time.getCurrentTime()), 5, 1);
		BatteryReceiver.writeLog(
				context.getResources().getString(R.string.txt_shut_down), 6, 1);
	}

}
