package com.example.anzhimaketdemo;
/*Beta2.0*/
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public abstract class WebpageAnalyzer {
	
	protected Document doc = null;
	
	protected WebpageAnalyzer(String url)
	{
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//delete <>
	protected String deleteSimpleBrackets(String str)
	{
		int leftbracket = -1;
		int rightbracket = -1;
		
		while( (leftbracket = str.indexOf('<') ) != -1 && ( rightbracket = str.indexOf('>') ) != -1 )
		{
			str = str.replace(str.substring(leftbracket, rightbracket+1), "");
		}
		return str;
	}
	
	//<div class = "name"> </div>
	protected Elements getElementsByClass(String classname)
	{		
		Elements elements = doc.getElementsByClass(classname);
		return elements;
	}
	
	//<a > </a>
	protected Elements getElementsByTag(String tag)
	{
		Elements elements = doc.select(tag);
		return elements;
	}
	
	protected Element getElementById(String id)
	{
		Element element = doc.getElementById(id);
		return element;
	}
	
	// return all links
	protected Elements getAllLinks() 
	{
		// get all links
		Elements links = doc.select("a[href]");		
		return links;
	}
	
	//return certain link
	protected ArrayList<String> getLinksByText(String text)
	{
		Elements links = getAllLinks();
		ArrayList<String> arrayList = new ArrayList<>();
		
		for (Element element : links) {
			if (element.text().equals(text)) {
				arrayList.add(element.attr("href"));
			}
		}
		
		return arrayList;
	}
	
	// return all pics
	protected Elements getAllImages()
	{
		// get all pic links
		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		return images;
	}
	
	//return certain Image
	protected ArrayList<String> getImagesByText(String text)
	{
		Elements links = getAllImages();
		ArrayList<String> arrayList = new ArrayList<>();
		
		for (Element element : links) {
			if (element.attr("alt").equals(text)) {
				arrayList.add(element.attr("src"));
			}
		}
		
		return arrayList;
	}
	
	//get Simple MetaContent
	protected String getMetaContent(String text)
	{
		// get all pic links
		String query = "meta[name=" + text + "]";
		String content = doc.select(query).first().attr("content");
		
		return content;
	}
	
	//find the nthOccurrence in a String
	protected int nthOccurrence(String str, char c, int n) {
	    int pos = str.indexOf(c, 0);
	    while (--n > 0 && pos != -1)
	        pos = str.indexOf(c, pos+1);
	    return pos;
	}
	
	protected int nthOccurrence(String str, String c, int n) {
	    int pos = str.indexOf(c, 0);
	    while (--n > 0 && pos != -1)
	        pos = str.indexOf(c, pos+1);
	    return pos;
	}
	
}
