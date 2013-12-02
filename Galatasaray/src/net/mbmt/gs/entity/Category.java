package net.mbmt.gs.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.mbmt.gs.R;
import net.mbmt.gs.utils.GSException;
import net.mbmt.gs.utils.Global;
import net.mbmt.gs.utils.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Category {
	private int id;
	private String name;
	private int oldestNewsNumber;
	private int latestNewsNumber;
	private boolean isMainCategory;
	private int parentCatId;
	private List<Category> childCatList;
	private List<News> newsList;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOldestNewsNumber() {
		return oldestNewsNumber;
	}

	public int getLatestNewsNumber() {
		return latestNewsNumber;
	}

	public boolean isMainCategory() {
		return isMainCategory;
	}

	public int getParentCatId() {
		return parentCatId;
	}

	public Category[] getChildCategories() {
		return childCatList.toArray(new Category[0]);
	}

	public News[] getNews() {
		return newsList.toArray(new News[0]);
	}

	public int getImageId() {
		if (name.startsWith("Tüm") || "Diðer".equals(name)) {
			return R.drawable.tum;
		}
		String resName = name.toLowerCase(new Locale("en-GB")).replace(' ', '_').replace(".", "").replace('ç', 'c').replace('ý', 'i').replace('ð', 'g').replace('ö', 'o').replace('þ', 's')
				.replace('ü', 'u');
		return Global.Activity.getResources().getIdentifier(resName, "drawable", Global.Activity.getPackageName());
	}

	public Category() {
		newsList = new ArrayList<News>();
		childCatList = new ArrayList<Category>();
	}

	public void loadNews(boolean more) throws GSException {
		if (!more && newsList.size() > 0) {
			return;
		}

		Document xmlDoc = Utils.downloadXML("c=" + id + "&n=" + oldestNewsNumber);

		NodeList newsNodes = xmlDoc.getElementsByTagName("News");
		News news = null;
		Node node = null;
		for (int i = 0; i < newsNodes.getLength(); i++) {
			node = newsNodes.item(i);
			news = new News();

			news.setNumber(Integer.parseInt(node.getAttributes().getNamedItem("Number").getNodeValue()));
			news.setTitle(node.getAttributes().getNamedItem("Title").getNodeValue());
			news.setDate(Utils.parseDate(node.getAttributes().getNamedItem("Date").getNodeValue()));
			news.setUrl(node.getAttributes().getNamedItem("Url").getNodeValue());
			news.setCatId(Integer.parseInt(node.getAttributes().getNamedItem("CatId").getNodeValue()));

			if (news.getNumber() < oldestNewsNumber || oldestNewsNumber < 1) {
				oldestNewsNumber = news.getNumber();
				newsList.add(news);
			}
			
			if (news.getNumber() > latestNewsNumber) {
				latestNewsNumber = news.getNumber(); 
			}
		}
	}

	public News getNews(int newsNumber) {
		for (News news : newsList) {
			if (news.getNumber() == newsNumber) {
				return news;
			}
		}
		return null;
	}

	public String toString() {
		return name;
	}

	private static List<Category> list;

	public static void reset() {
		if (list != null) {
			list.clear();
			list = null;
		}
		Global.Category = null;
	}

	public static Category[] getAllCategories() throws GSException {
		if (list == null) {
			loadList();
		}
		return list.toArray(new Category[0]);
	}

	public static Category[] getMainCategories() throws Exception {
		List<Category> mainCats = new ArrayList<Category>();

		for (Category cat : getAllCategories()) {
			if (cat.isMainCategory) {
				mainCats.add(cat);
			}
		}

		return mainCats.toArray(new Category[0]);
	}

	public static Category getCategory(int catId) throws GSException {
		for (Category cat : getAllCategories()) {
			if (cat.id == catId) {
				return cat;
			}
		}
		return null;
	}

	public static void loadList() throws GSException {
		if (list == null) {
			list = new ArrayList<Category>();
		} else {
			list.clear();
		}

		Document catXml = Utils.downloadXML("");

		NodeList childCatNodes = null;
		NodeList mainCatNodes = catXml.getDocumentElement().getChildNodes();
		Node mainCatNode = null;
		Node childCatNode = null;

		Category cat = null;
		Category childCat = null;
		for (int i = 0; i < mainCatNodes.getLength(); i++) {
			mainCatNode = mainCatNodes.item(i);

			cat = new Category();
			cat.isMainCategory = true;
			cat.name = mainCatNode.getAttributes().getNamedItem("Name").getNodeValue();
			cat.id = Integer.parseInt(mainCatNode.getAttributes().getNamedItem("Id").getNodeValue());
			cat.parentCatId = -1;

			childCatNodes = mainCatNode.getChildNodes();

			if (childCatNodes.getLength() > 0) {
				childCatNode = childCatNodes.item(0);
				if ("ChildCategories".equals(childCatNode.getNodeName())) {
					childCatNodes = childCatNode.getChildNodes();

					for (int j = 0; j < childCatNodes.getLength(); j++) {
						childCatNode = childCatNodes.item(j);

						childCat = new Category();
						childCat.isMainCategory = false;
						childCat.name = childCatNode.getAttributes().getNamedItem("Name").getNodeValue();
						childCat.id = Integer.parseInt(childCatNode.getAttributes().getNamedItem("Id").getNodeValue());
						childCat.parentCatId = cat.id;
						
						list.add(childCat);
						cat.childCatList.add(childCat);
					}
				}
			}

			list.add(cat);
		}
	}
}
