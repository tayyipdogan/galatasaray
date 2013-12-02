package net.mbmt.gs.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import net.mbmt.gs.R;
import net.mbmt.gs.entity.News;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader {		
	private static ImageLoader instance = null;
	
	private MemoryCache memoryCache = null;
	private PhotosQueue photosQueue = null;
	private PhotosLoader photoLoaderThread = null;

	private Map<ImageView, News> imageViews = null;

	public synchronized static ImageLoader getInstance() {
		if (instance == null) {
			instance = new ImageLoader();
		}
		return instance;
	}
	
	private ImageLoader() {
		memoryCache = new MemoryCache();
		photosQueue = new PhotosQueue();
		photoLoaderThread = new PhotosLoader();
		imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, News>());
		// Make the background thead low priority. This way it will not affect
		// the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
	}

	public void clearCache() {
		memoryCache.clear();
	}

	public void stopThread() {
		photoLoaderThread.interrupt();
	}

	public void displayImage(News news, Activity activity, ImageView imageView) {
		imageViews.put(imageView, news);
		Bitmap bitmap = memoryCache.get(news.getImageUrl());
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			queuePhoto(news.getImageUrl(), activity, imageView);
			imageView.setImageResource(R.drawable.default_header);
		}
	}

	private void queuePhoto(String url, Activity activity, ImageView imageView) {
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW) {
			photoLoaderThread.start();
		}
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	// stores list of photos to download
	private class PhotosQueue {
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

		// removes all instances of this ImageView
		public void Clean(ImageView image) {
			for (int j = 0; j < photosToLoad.size(); j++) {
				if (photosToLoad.get(j).imageView == image) {
					photosToLoad.remove(j--);
				}
			}
		}
	}

	private class PhotosLoader extends Thread {
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0) {
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad = null;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Bitmap bmp = getBitmap(photoToLoad);
						memoryCache.put(photoToLoad.url, bmp);
						News news = imageViews.get(photoToLoad.imageView);
						if (news != null && news.getImageUrl().equals(photoToLoad.url)) {
							news.setImage(bmp);
							BitmapDisplayer bd = new BitmapDisplayer(bmp,
									photoToLoad.imageView);
							Activity a = (Activity) photoToLoad.imageView
									.getContext();
							a.runOnUiThread(bd);
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}

		private Bitmap getBitmap(PhotoToLoad photoToLoad) {
			try {
				URL aURL = new URL(photoToLoad.url);
				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				Bitmap bmp = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
				return bmp;
			} catch (Exception ex) {
				ex.printStackTrace();
				return BitmapFactory.decodeResource(photoToLoad.imageView.getResources(), R.drawable.default_header);
			}
		}
	}

	// Used to display bitmap in the UI thread
	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;

		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap = b;
			imageView = i;
		}

		public void run() {
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else {
				imageView.setImageResource(R.drawable.default_header);
			}
		}
	}

	private class MemoryCache {
		private HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

		public Bitmap get(String id) {
			if (!cache.containsKey(id))
				return null;
			SoftReference<Bitmap> ref = cache.get(id);
			return ref.get();
		}

		public void put(String id, Bitmap bitmap) {
			cache.put(id, new SoftReference<Bitmap>(bitmap));
		}

		public void clear() {
			cache.clear();
		}
	}
}
