package net.mbmt.gs.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public class Config {
	public static final String serviceUrl = "http://www.mbmt.net/gs.ashx";

	public static boolean isNotificationsEnabled(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("GSPrefEnableNotifications", true);
	}

	public static int getNotificationCheckInterval(Context context) {
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("GSPrefNotificationIntervals", "10"));
	}

	public static Uri getNotificationSound(Context context) {
		String strRingtonePreference = PreferenceManager.getDefaultSharedPreferences(context).getString("GSPrefNotificationTone", "DEFAULT_SOUND");
		return Uri.parse(strRingtonePreference);

	}

	public static boolean isVibrateOnNotify(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("GSPrefVibarateOnNotification", true);
	}

	public static int[] getNotificationCatIds(Context context) {
		List<Integer> list = new ArrayList<Integer>();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		for (int i = 1; i <= 7; i++) {
			if (sp.getBoolean("GSPrefCategories_" + i, true)) {
				list.add(new Integer(i));
			}
		}

		int i = 0;
		int[] arr = new int[list.size()];
		for (Integer integer : list) {
			arr[i++] = integer.intValue();
		}
		return arr;
	}
}
