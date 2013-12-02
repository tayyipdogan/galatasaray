package net.mbmt.gs.entity;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.mbmt.gs.utils.Config;
import net.mbmt.gs.utils.GSException;
import net.mbmt.gs.utils.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class News {
	private int number;
	private String title;
	private String url;
	private Date date;
	private String content;
	private int catId;
	private Bitmap image;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() throws GSException {
		if (content == null) {
			loadContent();
		}
		return content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Bitmap getImage() throws GSException {
		if (image == null) {
			loadImage();
		}
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getImageUrl() {
		return Config.serviceUrl + "?i=" + number;
	}

	public String toString() {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			return title + "\n@" + sdf.format(date);
		}
		return title;
	}

	private void loadContent() throws GSException {
		this.content = Utils.downloadString("n=" + number);
	}

	private void loadImage() throws GSException {
		try {
			String url = Config.serviceUrl + "?i=" + number;
			URL aURL = new URL(url);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			this.image = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (Exception ex) {
			throw new GSException(ex.getMessage());
		}
	}
}
