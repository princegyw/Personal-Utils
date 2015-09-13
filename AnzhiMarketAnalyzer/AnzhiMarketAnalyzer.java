package com.example.anzhimaketdemo;

import java.util.ArrayList;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




public class AnzhiMarketAnalyzer extends MarketAnalyzer {

	static final String homeUrl = "http://www.anzhi.com/";
	static final String downloadBaseUrl = "http://m.anzhi.com/download.php?softid=";
	protected AnzhiMarketAnalyzer(String url) {
		super(url);
	}

	@Override
	public String getApkLink() {
		String detail_down = getElementsByClass("detail_down").first().toString();
		String id = detail_down.substring(detail_down.indexOf("(") + 1, detail_down.indexOf(")"));
		return downloadBaseUrl + id;
	}

	@Override
	public ArrayList<String> getAppScreenShots() {
		ArrayList<String> screenshots = new ArrayList<>();
		Elements detail_img_list = getElementsByClass("detail_img_list").first().getElementsByTag("li");
		for (Element element : detail_img_list) {
			screenshots.add(homeUrl + element.getElementsByTag("img").first().attr("src"));
		}
		return screenshots;
	}

	@Override
	public String getAppName() {
		String detail_line = getElementsByClass("detail_line").first().toString();
		return detail_line.substring(detail_line.indexOf("<h3>") + "<h3>".length(), detail_line.indexOf("</h3>"));
	}

	@Override
	public String getAppLogo() {
		Element element = getElementsByClass("detail_icon").first();
		return homeUrl + element.getElementsByTag("img").first().attr("src");
	}

	@Override
	public String getAppDescription() {
		String app_detail_infor = getElementsByClass("app_detail_infor").first().toString();
		String description = app_detail_infor.substring(app_detail_infor.indexOf("<p>") + "<p>".length(), app_detail_infor.indexOf("</p>"));
		return deleteSimpleBrackets(description);
	}

}
