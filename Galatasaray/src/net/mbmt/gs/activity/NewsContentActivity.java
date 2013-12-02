package net.mbmt.gs.activity;

import net.mbmt.gs.R;
import net.mbmt.gs.utils.ActivityProcess;
import net.mbmt.gs.utils.GSException;
import net.mbmt.gs.utils.Global;
import net.mbmt.gs.utils.IActivityProcessContext;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;

public class NewsContentActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.news_content);
			Global.Activity = this;

			new NewsContentLoader().load();
		} catch (Exception ex) {
			showInWebView(ex.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu_news_content, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mi_news_content_open_url:
			openUrl();
			return true;
		case R.id.mi_news_content_share_facebook:
			shareOnFacebook();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void openUrl() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Global.News.getUrl()));
		startActivity(browserIntent);
	}

	private void shareOnFacebook() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/sharer.php?u=" + Global.News.getUrl()));
		startActivity(browserIntent);
	}

	private void showInWebView(String text) {
		WebView web = (WebView) findViewById(R.id.webContent);
		web.loadData("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + text, "text/html", "UTF-8");
	}
	
	class NewsContentLoader {
		void load() {
			ActivityProcess.execute(new IActivityProcessContext() {
				@Override
				public void success() {
					try {
						showInWebView(Global.News.getContent());
						ImageView img = (ImageView) findViewById(R.id.imgNewsImage);
						img.setImageBitmap(Global.News.getImage());
					}
					catch (GSException g) { }
					NewsContentActivity.this.setTitle(Global.News.getTitle());
				}

				@Override
				public void process() throws GSException {
					Global.News.getContent();
					Global.News.getImage();
				}

				@Override
				public void fail(Exception failEx) {
					showInWebView("Haber içeriði alnamadý: " + failEx.getMessage());
				}
			}, "Yükleniyor", "Lütfen bekleyiniz...");
		}
	}
}