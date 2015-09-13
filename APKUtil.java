package com.intel.rainier.utility;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class APKUtil {
	
	public static final int INSTALLED = 0;
	public static final int UNINSTALLED = -1;
	public static final int INSTALLED_UPDATE = 1;

	public static int isAlreadyInstalled(String apkPath, Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);		
		String packageName = packageInfo.packageName;
		int versionCode = packageInfo.versionCode;
		
		int type = doType(pm, packageName, versionCode);
		
		return type;
	}
	
	public static int doType(PackageManager pm, String packageName, int versionCode) {
		List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo pi : pakageinfos) {
			String pi_packageName = pi.packageName;
			int pi_versionCode = pi.versionCode;
		
			if(packageName.endsWith(pi_packageName)){
				if(versionCode == pi_versionCode){
					return INSTALLED;
				}else if(versionCode > pi_versionCode){
					return INSTALLED_UPDATE;
				}
			}
		}
		return UNINSTALLED;
	}

}
