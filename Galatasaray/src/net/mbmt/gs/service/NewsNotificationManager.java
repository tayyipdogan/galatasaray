package net.mbmt.gs.service;

import java.util.Calendar;

import net.mbmt.gs.utils.Config;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NewsNotificationManager {
	public static boolean running = false;
	private static PendingIntent pendingIntent;
		
	private Context context;
	private AlarmManager alarmManager;

	public NewsNotificationManager(Context context) {
		this.context = context;
		this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void stop() {
		Log.d("GS Service", "NewsNotificationManager.stop");
		alarmManager.cancel(pendingIntent);
		running = false;
	}
		
	public void start(boolean force) {
		if (!running && (Config.isNotificationsEnabled(context) || force)) {
			running = true;
			
			Log.d("GS Service", "NewsNotificationManager.setReminder: " + Config.getNotificationCheckInterval(context));
			Intent i = new Intent(context, NewsNotificationReceiver.class);
			
			Calendar when = Calendar.getInstance(); 
			when.add(Calendar.MINUTE, Config.getNotificationCheckInterval(context));
	
			pendingIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
			alarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pendingIntent);
		}
	}
}
