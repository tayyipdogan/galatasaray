package net.mbmt.gs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.util.Log;

public class Utils {
	public static Document downloadXML(String queryString) throws GSException {
		InputStream is = null;
		long start = System.currentTimeMillis();
		while (true) {
			try {
				URL url = new URL(Config.serviceUrl + "?" + queryString);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(10000);
				is = conn.getInputStream();
				Document xDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

				checkError(xDoc);

				return xDoc;
			} catch (Exception ex) {
				Log.d("GS Service", "downloadXML: " + ex.getMessage());
				if (System.currentTimeMillis() - start > 60000) {
					throw new GSException(ex.getMessage());
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static String downloadString(String queryString) throws GSException {
		InputStream is = null;
		long start = System.currentTimeMillis();
		while (true) {
			try {
				URL url = new URL(Config.serviceUrl + "?" + queryString);
				URLConnection conn = url.openConnection();

				is = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				StringBuffer sb = new StringBuffer();

				String line = br.readLine();
				while (line != null) {
					sb.append(line);
					line = br.readLine();
				}

				return sb.toString();
			} catch (Exception ex) {
				Log.d("GS Service", "downloadString: " + ex.getMessage());
				if (System.currentTimeMillis() - start > 60000) {
					throw new GSException(ex.getMessage());
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static Date parseDate(String dateString) throws GSException {
		dateString = dateString.replace('T', ' ');
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		try {
			return sdf.parse(dateString);
		} catch (ParseException e) {
			throw new GSException(e.getMessage());
		}
	}

	public static void showMessage(String title, String msg) {
		AlertDialog alertDialog = new AlertDialog.Builder(Global.Activity).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
		alertDialog.show();
	}

	private static void checkError(Document doc) throws GSException {
		NodeList errorNodes = doc.getElementsByTagName("Error");
		if (errorNodes.getLength() > 0) {
			throw new GSException(errorNodes.item(0).getNodeValue());
		}
	}
}
