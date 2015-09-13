package com.read.apk;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ReadApk {

	/**
	 * 
	 * @param args
	 * @return void
	 * @author hsx
	 * @time 2013-4-24下午03:20:24
	 */
	public static void main(String[] args) {
		//String apkname = "./src//com//read//apk//x86_com.moji.mjweather_1550082.apk";
		String dir = args[0];
		
		
		
//		APKanalyzer analyzer = new APKanalyzer(apkname);
//		analyzer.readAPK();
//		int result = analyzer.getLibType();
//		
//		System.out.println(analyzer.getLibTypeString());
		
		APKBatchAnalyzer analyzer = new APKBatchAnalyzer(dir);
		analyzer.readApks();
		Map<String, String> libTypes = analyzer.getLibTypeString();
		
		for (String key : libTypes.keySet()) {
			System.out.println(key + " ; " + libTypes.get(key));
		}
	}

}
