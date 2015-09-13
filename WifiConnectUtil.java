package com.example.easytrans;

import java.lang.reflect.Method;
import java.util.List;

import junit.framework.Test;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;


public class WifiConnectUtil {
	private WifiManager wifiManager;
	private String TAG = "WifiConnectUtil";

	// 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	// constructor
	public WifiConnectUtil(WifiManager wifiManager) {
		this.wifiManager = wifiManager;
	}
	
	public WifiConnectUtil(WifiManager wifiManager, String TAG)
	{
		this.wifiManager = wifiManager;
		this.TAG = TAG;
	}
	
	public WifiConnectUtil(Context context)
	{
		this.wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}
	
	public WifiConnectUtil(Context context, String TAG)
	{
		this.wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		this.TAG = TAG;
	}

	// open Wifi
	public boolean openWifi() {
		boolean bRet = true;
		if (!wifiManager.isWifiEnabled()) {
			bRet = wifiManager.setWifiEnabled(true);
		}
		return bRet;
	}
	
	//close Wifi
	public boolean closeWifi(){
		boolean bRet = true;
		if (wifiManager.isWifiEnabled()) {
			bRet = wifiManager.setWifiEnabled(false);
		}
		return bRet;
	}

	// Connect to a given specific Wifi network
	public boolean connect(String SSID, String Password, WifiCipherType Type) {
		
		if (!openWifi()) {
			return false; //open wifi failed
		}
		
		LogUtil.d(TAG, "wifi is opened");
		
		//wait until WIFI is enable
		while (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			try {
				Thread.currentThread();
				Thread.sleep(50);
			} catch (InterruptedException ie) {
			}
		}
		
		LogUtil.d(TAG, "wifi is ready");
		
		//forget all the history configs
		//forgetAllConfigs(scan());
		
		WifiConfiguration wifiConfig = createWifiConfig(SSID, Password, Type);
		
		if (wifiConfig == null) {
			return false;
		}

		WifiConfiguration tempConfig = this.isExsitsConfig(SSID);

		if (tempConfig != null) {
			wifiManager.removeNetwork(tempConfig.networkId);
		}

		int netID = wifiManager.addNetwork(wifiConfig);
		boolean bRet = wifiManager.enableNetwork(netID, true);
		LogUtil.v(TAG, "connection ends");
		return bRet;
	}

	// check to see if the wifi network already configured
	private WifiConfiguration isExsitsConfig(String SSID) {
		List<WifiConfiguration> existingConfigs = wifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	public WifiConfiguration createWifiConfig(String SSID, String Password, WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (Type == WifiCipherType.WIFICIPHER_WEP) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (Type == WifiCipherType.WIFICIPHER_WPA) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.status = WifiConfiguration.Status.ENABLED;
		} else {
			return null;
		}
		return config;
	}
	
	
	// open or close Wifi-AP hotpot 
	public boolean setWifiApEnabled(String SSID, String preSHaredKey, boolean enabled) {
		
		if (enabled) {
			closeWifi(); //Ap and wifi could not be opened simultaneously 
		}
				
		try {
			//热点的配置类
			WifiConfiguration apConfig = new WifiConfiguration();
			//配置热点的名称(可以在名字后面加点随机数什么的)
			apConfig.SSID = SSID;
			//配置热点的密码
			apConfig.preSharedKey= preSHaredKey;
			
			apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			apConfig.status = WifiConfiguration.Status.ENABLED;
			//通过反射调用设置热点
			Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			//返回热点打开状态
			return (Boolean) method.invoke(wifiManager, apConfig, enabled);
			
		} catch (Exception e) {
				return false;
		}
	}
	
   
    
	public void addNetwork(WifiConfiguration wcg) { // 添加一个网络配置并连接  
		int wcgID = wifiManager.addNetwork(wcg);
		wifiManager.enableNetwork(wcgID, true);
	}
    
	//get all the available wifi networks
    public List<ScanResult> scan() 
    {
    	wifiManager.startScan();
		List<ScanResult> wifiList = null;
		wifiList = wifiManager.getScanResults();
		return wifiList;
	}
    
//    public void forgetAllConfigs(List<ScanResult> scanResults)
//    {
//    	if (scanResults != null) {
//			for (ScanResult result : scanResults) {
//				WifiConfiguration tempConfig = isExsitsConfig(result.SSID);
//				if (tempConfig != null) {
//					wifiManager.removeNetwork(tempConfig.networkId);
//				}
//			}
//		}
//    }
    
    //String wserviceName = Context.WIFI_SERVICE;
    
    // WifiInfo info = wm.getConnectionInfo();
    


    
    

    
}


