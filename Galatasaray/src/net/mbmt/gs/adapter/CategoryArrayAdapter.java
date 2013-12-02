package net.mbmt.gs.adapter;

import net.mbmt.gs.R;
import net.mbmt.gs.entity.Category;
import net.mbmt.gs.utils.Global;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryArrayAdapter extends BaseAdapter {
	private Category[] categories;
	private LayoutInflater inflater;

	public CategoryArrayAdapter(Category[] categories) {
		this.categories = categories;
		this.inflater = (LayoutInflater) Global.Activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return categories.length;
	}

	@Override
	public Object getItem(int position) {
		return categories[position];
	}

	@Override
	public long getItemId(int position) {
		return categories[position].getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.cat_list_item, null);
		}
		TextView txtCatTitle = (TextView) convertView
				.findViewById(R.id.txtCatTitle);
		ImageView imgCatTitle = (ImageView) convertView
				.findViewById(R.id.imgCatTitle);
		Category cat = (Category) getItem(position);
		txtCatTitle.setText(cat.getName());
		imgCatTitle.setImageResource(cat.getImageId());

		return convertView;
	}
}
