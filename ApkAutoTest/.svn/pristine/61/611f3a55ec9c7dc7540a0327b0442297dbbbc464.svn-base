package com.tencent.apk_auto_test.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.util.Telephony.Sms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

public class FillSms_MmsInfo {
	public Context mContext;
	final static int ONLY_SEND_MODE = 0x7;
	final static int ONLY_RECEIVE_MODE = 0x8;
	final static int SEND_RECEIVE_MIX_MODE = 0x9;
	private final static long DATE_INTEVAL = 10000L;
	public static final int FILLSMS_LENGTH = 10000;
	private final static int SMS_TYPE_RECEIVE = 1;
	private final static int SMS_TYPE_SEND = 2;
	private ContentValues values;
	private ContentResolver mContentResolver;

	private Random rand = new Random();
	private String[] dests;
	private HashSet<String> recipients;

	int mReadFlag;

	public FillSms_MmsInfo(Context cotext) {
		mContext = cotext;
		mContentResolver = mContext.getContentResolver();
	}

	private void writeSmsDataBase(Context context, long threadId, long date,
			String phonenum, int smsType) {

		String[] SMS = mContext.getResources().getStringArray(
				R.array.Sms_Send_Message);
		int arr = rand.nextInt(51);
		values = new ContentValues(9);
		// values.put(Sms.GROUPADDRESS, address);
		values.put(Sms.ADDRESS, phonenum);
		values.put(Sms.DATE, date + DATE_INTEVAL);
		// read
		values.put(Sms.READ, mReadFlag);
		// not read
		// values.put(Sms.READ, Integer.valueOf(0));
		values.put(Sms.BODY, SMS[arr]);
		values.put(Sms.STATUS, -1);

		values.put("type", smsType);

		values.put("seen", 1);
		values.put(Sms.THREAD_ID, threadId);
		mContentResolver.insert(Uri.parse("content://sms"), values);
	}

	public static String[] formatePhoneName(String address) {
		List<String> numbers = new ArrayList<String>();
		String semiSepNumbers = address;

		for (String number : semiSepNumbers.split(";")) {
			if (!TextUtils.isEmpty(number))
				numbers.add(number);
		}
		return numbers.toArray(new String[numbers.size()]);
	}
}
