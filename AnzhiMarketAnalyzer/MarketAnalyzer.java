package com.example.anzhimaketdemo;

import java.util.ArrayList;

//Market Interface
public abstract class MarketAnalyzer extends WebpageAnalyzer {
	
	protected MarketAnalyzer(String url) {
		super(url);
	}
	
	abstract public String getApkLink();
	
	abstract public ArrayList<String> getAppScreenShots();
	
	abstract public String getAppName();

	abstract public String getAppLogo();

	abstract public String getAppDescription();

}
