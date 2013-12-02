package net.mbmt.gs.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NewsNotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("GS Service", "NewsNotificationReceiver.onReceive");
		NewsNotificationService.acquireStaticLock(context);
		Intent i = new Intent(context, NewsNotificationService.class);
		context.startService(i);
	}
}