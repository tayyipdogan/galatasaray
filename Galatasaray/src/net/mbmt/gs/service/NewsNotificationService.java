package net.mbmt.gs.service;

import net.mbmt.gs.R;
import net.mbmt.gs.activity.NewsContentActivity;
import net.mbmt.gs.entity.Category;
import net.mbmt.gs.entity.News;
import net.mbmt.gs.utils.Config;
import net.mbmt.gs.utils.GSException;
import net.mbmt.gs.utils.Global;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class NewsNotificationService extends IntentService {

	public static final String LOCK_NAME_STATIC = "org.galatasaray.static.lock";
	private static PowerManager.WakeLock lockStatic = null;

	private static int latestNewsNumber = 0;
	private static boolean isFirstCheck = true;;

	public NewsNotificationService() {
		super("GS Haber Servisi");
	}

	public static void acquireStaticLock(Context context) {
		getLock(context).acquire();
	}

	private synchronized static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic == null) {
			PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
			lockStatic.setReferenceCounted(true);
		}
		return (lockStatic);
	}

	@Override
	protected final void onHandleIntent(Intent intent) {
		try {
			Log.d("GS Service", "NewsNotificationService.onHandleIntent: " + intent);
			checkNews();
		} catch (Exception ex) {
			Log.d("GS Service", "NewsNotificationService.onHandleIntent: " + ex.getMessage());
		} finally {
			getLock(this).release();
		}

		NewsNotificationManager mgr = new NewsNotificationManager(this);
		NewsNotificationManager.running = false;
		mgr.start(false);
	}

	private void checkNews() throws GSException {
		Log.d("GS Service", "NewsNotificationService.checkNews");
		Category allNewsCat = new Category();
		allNewsCat.setId(0);

		try {
			Log.d("GS Service", "loading news...");
			allNewsCat.loadNews(false);
		} catch (GSException gsEx) {
			Log.d("GS Service", "NewsNotificationService.checkNews: " + gsEx.getMessage());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (News latestNews : allNewsCat.getNews()) {
			if (requiresNotification(latestNews)) {
				Log.d("GS Service", "news loaded, latest news number is " + latestNewsNumber);

				if (isFirstCheck) {
					Log.d("GS Service", "no notifications are done for first check");
					isFirstCheck = false;
				} else {
					Log.d("GS Service", "sending notification for " + latestNewsNumber);
					sendNotification(allNewsCat);
				}
				return;
			}
		}

		Log.d("GS Service", "no new news");
	}

	private void sendNotification(Category allNewsCat) {
		if (!Config.isNotificationsEnabled(this)) {
			Log.d("GS Service", "notifications are disabled");
			return;
		}

		Global.News = allNewsCat.getNews(latestNewsNumber);
		try {
			Global.Category = Category.getCategory(Global.News.getCatId());
		} catch (GSException e) {
			e.printStackTrace();
		}

		Intent notificationIntent = new Intent(this, NewsContentActivity.class);

		PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

		Notification note = new Notification(R.drawable.icon_gray, Global.News.getTitle(), System.currentTimeMillis());

		note.setLatestEventInfo(this, getString(R.string.app_name), Global.News.getTitle(), pi);
		
		note.sound = Config.getNotificationSound(this);
		if (Config.isVibrateOnNotify(this)) {
			note.defaults |= Notification.DEFAULT_VIBRATE;
		}

		note.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mgr.notify(latestNewsNumber, note);
	}

	private boolean requiresNotification(News news) {
		int[] notCatIds = Config.getNotificationCatIds(this);

		Category newsCat = null;
		try {
			newsCat = Category.getCategory(news.getCatId());
		} catch (GSException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < notCatIds.length; i++) {
			if ((notCatIds[i] == newsCat.getId() || notCatIds[i] == newsCat.getParentCatId()) && news.getNumber() > latestNewsNumber) {
				latestNewsNumber = news.getNumber();
				return true;
			}
		}

		return false;
	}
}
