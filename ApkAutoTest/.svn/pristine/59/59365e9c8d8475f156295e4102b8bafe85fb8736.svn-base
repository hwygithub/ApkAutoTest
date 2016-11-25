package com.tencent.apk_auto_test.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenActionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_USER_PRESENT)) {
			// send broadcast
			context.sendBroadcast(new Intent(
					"com.apptest.cmcc_standard_autorun.action.runtest"));
		}
	}
}
