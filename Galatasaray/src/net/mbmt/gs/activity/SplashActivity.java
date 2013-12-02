package net.mbmt.gs.activity;

import net.mbmt.gs.R;
import net.mbmt.gs.entity.Category;
import net.mbmt.gs.service.NewsNotificationManager;
import net.mbmt.gs.utils.ActivityProcess;
import net.mbmt.gs.utils.GSException;
import net.mbmt.gs.utils.Global;
import net.mbmt.gs.utils.IActivityProcessContext;
import net.mbmt.gs.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SplashActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		try {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			super.onCreate(savedInstanceState);
			setContentView(R.layout.splash);

			Category.reset();

			NewsNotificationManager mgr = new NewsNotificationManager(this);
			mgr.start(false);
			// Intent serviceIntent = new Intent(getApplicationContext(),
			// OldNotificationService.class);
			// startService(serviceIntent);

			Global.News = null;
			Global.Activity = this;

			new SplashLoader().load();
		} catch (Exception ex) {
			Utils.showMessage("Hata", ex.getMessage());
			finish();
		}
	}

	class SplashLoader {
		void load() {
			ActivityProcess.execute(new IActivityProcessContext() {
				@Override
				public void success() {
					Intent i = new Intent(SplashActivity.this, CategoriesActivity.class);
					startActivity(i);
					Global.Activity.finish();
				}

				@Override
				public void process() throws GSException {
					Category.getAllCategories();
				}

				@Override
				public void fail(Exception failEx) {
					AlertDialog.Builder adb = new AlertDialog.Builder(Global.Activity);
					adb.setIcon(android.R.drawable.ic_dialog_alert);
					adb.setTitle("Hata");
					adb.setMessage("Uygulama baþlatýlýrken hata oluþtu. Lütfen internet baðlantýnýzýn açýk olduðundan emin olunuz!");
					adb.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Category.reset();
							Global.News = null;
							Global.Activity.finish();
						}
					});
					adb.show();
				}
			}, "Yükleniyor", "Lütfen bekleyiniz...");
		}
	}
}
