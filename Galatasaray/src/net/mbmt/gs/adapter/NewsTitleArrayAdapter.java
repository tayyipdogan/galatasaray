package net.mbmt.gs.adapter;

import java.text.SimpleDateFormat;

import net.mbmt.gs.R;
import net.mbmt.gs.entity.News;
import net.mbmt.gs.utils.ImageLoader;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsTitleArrayAdapter extends BaseAdapter {
	private Activity activity;
	private News[] news;
	private LayoutInflater inflater;

	public NewsTitleArrayAdapter(Activity activity, News[] news) {
		this.activity = activity;
		this.news = news;
		this.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return news.length;
	}

	@Override
	public Object getItem(int position) {
		return news[position];
	}

	@Override
	public long getItemId(int position) {
		return news[position].getNumber();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.news_list_item, null);
		}

		TextView txtNewsTitle = (TextView) convertView
				.findViewById(R.id.txtNewsTitle);
		TextView txtNewsDate = (TextView) convertView
				.findViewById(R.id.txtNewsDate);
		ImageView imgNewsTitle = (ImageView) convertView
				.findViewById(R.id.imgNewsTitle);

		News selectedNews = (News) getItem(position);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

		txtNewsTitle.setText(selectedNews.getTitle());
		txtNewsDate.setText(sdf.format(selectedNews.getDate()));

		ImageLoader.getInstance().displayImage(selectedNews, activity,
				imgNewsTitle);

		return convertView;
	}
}
