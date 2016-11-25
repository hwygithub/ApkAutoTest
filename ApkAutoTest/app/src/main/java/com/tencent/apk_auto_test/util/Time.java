package com.tencent.apk_auto_test.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.tencent.apk_auto_test.data.StaticData;

public class Time {

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static String getDate() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return year + "-" + month + "-" + day;
	}

	public static String getTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		long CurrentTime = System.currentTimeMillis();
		String Time = dateFormat.format(CurrentTime);
		return Time;
	}

	public static String getCurrentTimeSecond() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		// int hour = calendar.get(Calendar.HOUR_OF_DAY);
		// int minute = calendar.get(Calendar.MINUTE);
		// int second = calendar.get(Calendar.SECOND);
		// Log.e("", hour+" "+minute+" "+second);

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
		long CurrentTime = System.currentTimeMillis();
		String Time = dateFormat.format(CurrentTime);
		return year + "-" + month + "-" + day + "-" + Time;
	}

	public static String getPassTimeString(long startTime, long endTime) {
		long passTime = endTime - startTime;
		String timeString;
		long hour = 60 * 60 * 1000;
		long minute = 60 * 1000;
		long second = 1000;
		long hourNumber = passTime / hour;
		passTime = passTime % hour;
		long minutesNumber = passTime / minute;
		passTime = passTime % minute;
		long secondNumber = passTime / second;
		timeString = hourNumber + "小时" + minutesNumber + "分钟" + secondNumber
				+ "秒";
		return timeString;
	}

	public static String getPassTime(long startTime, long endTime) {
		long passTime = endTime - startTime;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		TimeZone tz = TimeZone.getTimeZone("GMT");
		dateFormat.setTimeZone(tz);
		String timeString = dateFormat.format(passTime);
		return timeString;
	}

	public static boolean isTimeOver(long startTime, int minutes) {
		long passTime = System.currentTimeMillis() - startTime;
		if (passTime > minutes * 60000 * StaticData.timeGene) {
			return true;
		} else {
			return false;
		}
	}
}
