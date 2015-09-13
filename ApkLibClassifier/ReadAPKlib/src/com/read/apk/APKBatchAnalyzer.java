package com.read.apk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APKBatchAnalyzer {
	private String dirPath = null;
	
	private Map<String, Integer> libResult = new HashMap<String, Integer>();
	private Map<String, String> libResultStrings = new HashMap<String, String>();
	
	//all apk files
	List<File> files = new ArrayList<>();
	
	public APKBatchAnalyzer(String dirPath) {
		this.dirPath = dirPath;
	}
	
	public void readApks() {
		listfiles(dirPath);
		
		if (files.size() > 0) {
			for (File file : files) {
				APKanalyzer apkAnalyzer = new APKanalyzer(file);
				apkAnalyzer.readAPK();
				libResult.put(file.getAbsolutePath(), Integer.valueOf(apkAnalyzer.getLibType()));
				libResultStrings.put(file.getAbsolutePath(), apkAnalyzer.getLibTypeString());
			}
		}
	}
	
	public Map<String, Integer> getLibType() {
		return libResult;
	}
	
	public Map<String, String> getLibTypeString() {
		return libResultStrings;
	}
	
	//read all files and subfiles
	private void listfiles(String directoryName) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	        	
	        	if (file.toString().contains(".apk")) {
	        		files.add(file);
		            //System.out.println(file.toString());
				} else {
					continue;
				}
	            
	        } else if (file.isDirectory()) {
	            listfiles(file.getAbsolutePath());
	        }
	    }
	}
}
