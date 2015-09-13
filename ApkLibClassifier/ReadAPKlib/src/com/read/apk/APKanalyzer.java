package com.read.apk;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class APKanalyzer {
	private boolean lib_ARM = false;
	private boolean lib_X86 = false;
	
	public final int LIB_TYPE_ARM = 0;
	public final int LIB_TYPE_X86 = 1;
	public final int LIB_TYPE_BOTH = 2;
	public final int LIB_TYPE_NULL = -1;
	
	private String apkPath = null;
	
	//constructor
	public APKanalyzer(String apkPath) {
		this.apkPath = apkPath;
	}
	
	public APKanalyzer(File apkfile) {
		this.apkPath = apkfile.getAbsolutePath();
	}
	
	public void readAPK() {
		try {
			ZipFile zipFile = new ZipFile(apkPath);
			Enumeration<?> enumeration = zipFile.entries();
			
			while (enumeration.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)enumeration.nextElement();
				//System.out.println(entry);
				if (entry.toString().contains("lib/armeabi/")) {
					lib_ARM = true;
				} else if (entry.toString().contains("lib/x86/")) {
					lib_X86 = true;
				}
			}
			
			zipFile.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getLibType() {
		if (lib_ARM == true) {
			if (lib_X86 == true) {
				return LIB_TYPE_BOTH;
			} else {
				return LIB_TYPE_ARM;
			}
		} else {
			if (lib_X86 == true) {
				return LIB_TYPE_X86;
			} else {
				return LIB_TYPE_NULL;
			}
		}
	}
	
	public String getLibTypeString() {
		int libType = getLibType();
		switch (libType) {
		case LIB_TYPE_ARM:
			return "LIB_TYPE_ARM";
		
		case LIB_TYPE_X86:
			return "LIB_TYPE_X86";
			
		case LIB_TYPE_BOTH:
			return "LIB_TYPE_BOTH";
		
		case LIB_TYPE_NULL:
			return "LIB_TYPE_NULL";
		}
		return "LIB_TYPE_NULL";
		
	}
}
