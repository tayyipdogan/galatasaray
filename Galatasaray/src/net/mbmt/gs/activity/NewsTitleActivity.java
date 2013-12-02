package net.mbmt.gs.activity;

import net.mbmt.gs.R;
import net.mbmt.gs.adapter.NewsTitleArrayAdapter;
import net.mbmt.gs.entity.Category;
import net.mbmt.gs.entity.News;
import net.mbmt.gs.utils.ActivityProcess;
import net.mbmt.gs.utils.GSException;
import net.mbmt.gs.utils.Global;
import net.mbmt.gs.utils.IActivityProcessContext;
import net.mbmt.gs.utils.Utils;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class NewsTitleActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.news_title);
			Global.Activity = this;

			loadNews(false);
		} catch (Exception ex) {
			Utils.showMessage("Error", ex.getMessage());
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			super.onListItemClick(l, v, position, id);

			Global.News = (News) l.getItemAtPosition(position);

			if (Global.News != null) {
				Intent i = new Intent(this, NewsContentActivity.class);
				startActivity(i);
			}
		} catch (Exception ex) {
			Utils.showMessage("Hata", ex.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu_news_title, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mi_news_title_load_more:
			loadNews(true);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void loadNews(boolean more) {
		new NewsTitleLoader(more).load();
	}

	private class NewsTitleLoader {
		private boolean more;

		NewsTitleLoader(boolean more) {
			this.more = more;
		}

		void load() {
			ActivityProcess.execute(new IActivityProcessContext() {
				@Override
				public void success() {
					NewsTitleActivity.this.setTitle(Global.Category.getName());
					News[] news = Global.Category.getNews();
					NewsTitleArrayAdapter adapter = new NewsTitleArrayAdapter(NewsTitleActivity.this, news);
					setListAdapter(adapter);
					if (news.length > 11) {
						setSelection(getListView().getCount() - 1);
					}
				}

				@Override
				public void process() throws GSException {
					Category cat = Global.Category;
					cat.loadNews(more);
				}

				@Override
				public void fail(Exception failEx) {
					Utils.showMessage("Hata", failEx.getMessage());
				}
			}, "Yükleniyor", "Lütfen bekleyiniz...");
		}
	}
}