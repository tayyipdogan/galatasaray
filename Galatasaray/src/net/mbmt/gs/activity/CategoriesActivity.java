package net.mbmt.gs.activity;

import net.mbmt.gs.R;
import net.mbmt.gs.adapter.CategoryArrayAdapter;
import net.mbmt.gs.entity.Category;
import net.mbmt.gs.utils.Global;
import net.mbmt.gs.utils.Utils;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class CategoriesActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.categories);
			
			Global.Activity = this;
			
			loadMainCats();
		} catch (Exception ex) {
			Utils.showMessage("Hata", ex.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu_categories, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent i = new Intent(this, PreferencesActivity.class);
		startActivity(i);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			super.onListItemClick(l, v, position, id);

			Global.Category = (Category) l.getItemAtPosition(position);
			Category[] childCats = Global.Category.getChildCategories();

			if (childCats.length == 0) {
				showNewsTitles();
			} else {
				childCats = addAllCategory(childCats);
				showCategories(childCats);
			}
		} catch (Exception ex) {
			Utils.showMessage("Hata", ex.getMessage());
		}
	}

	private void showCategories(Category[] cats) {
		CategoryArrayAdapter adapter = new CategoryArrayAdapter(cats);
		setListAdapter(adapter);
		if (Global.Category != null) {
			this.setTitle(Global.Category.getName());
		} else {
			this.setTitle("Haber Kategorileri");
		}
	}

	private void showNewsTitles() {
		Intent i = new Intent(this, NewsTitleActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		try {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (!"Haber Kategorileri".equals(this.getTitle())) {
					loadMainCats();
					return true;
				}
				new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Uyarý").setMessage("Uygulamadan çýkmak istediðinize emin misiniz?")
						.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								CategoriesActivity.this.finish();
								Category.reset();
								Global.News = null;
							}
						}).setNegativeButton("Hayýr", null).show();
				return true;
			}
		} catch (Exception ex) {
			Utils.showMessage("Hata", ex.getMessage());
		}
		return super.onKeyDown(keyCode, event);
	}

	private void loadMainCats() throws Exception {
		Global.Category = null;
		showCategories(Category.getMainCategories());
	}

	private Category[] addAllCategory(Category[] cats) {
		Category[] arr = new Category[cats.length + 1];

		System.arraycopy(cats, 0, arr, 0, cats.length);
		arr[cats.length] = new Category();
		arr[cats.length].setName("Tüm " + Global.Category.getName() + " Haberleri");
		arr[cats.length].setId(Global.Category.getId());

		return arr;
	}
}
