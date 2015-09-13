// ------
// NOTICE:
// 	#Permission needed
// 		<uses-permission android:name="android.permission.INTERNET"/>

// 	#ANR exception
// 		For Android 4.2+, possibly need to add LogUtil.applyEasyPolicy() 

// 	#External lib needed
// 		jsoup-1.7.3.jar

// #Current version:1.0   
// #version 1.0    @20140725
// 	>>Create from previous beta version
	
// Written by:GuYiwei 
// Email:Yiwei.gu09@gmail.com
  
//  IN ACCORDANCE WITH GPL LICENECE



package com.example.locationfromip;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;


public class InternetUtil {
	
	private static final String IpQueryUrl = "http://www.ip.cn/index.php?ip";
	
	 public static String getLocalIpAddress() {
	    	try {
	    		// 遍历网络接口
	    		Enumeration<NetworkInterface> infos = NetworkInterface
	    				.getNetworkInterfaces();
	    		while (infos.hasMoreElements()) {
	    			// 获取网络接口
	    			NetworkInterface niFace = infos.nextElement();
	    			Enumeration<InetAddress> enumIpAddr = niFace.getInetAddresses();
	    			while (enumIpAddr.hasMoreElements()) {
	    				InetAddress mInetAddress = enumIpAddr.nextElement();
	    				// 所获取的网络地址不是127.0.0.1时返回得得到的IP
	    				if (!mInetAddress.isLoopbackAddress()
	    						&& InetAddressUtils.isIPv4Address(mInetAddress
	    								.getHostAddress())) {
	    					return mInetAddress.getHostAddress().toString();
	    				}
	    			}
	    		}
	    	} catch (SocketException e) {
	               e.printStackTrace();
	    	}
	    	return null;
	    }
	 
	 public enum TYPE_PING_RESULT{
		 PASS, FAIL, IP_NULL, FAIL_IOEXCEPTION, FAIL_INTERUPTED_EXCEPTION
	 }
	 
	 public static InternetUtil.TYPE_PING_RESULT pingIpAddr(String ipAddress) {
		 
		 if (ipAddress == null) 
		 {
			return TYPE_PING_RESULT.IP_NULL;
		 }
		 
	    try 
	    {
	    	Process p = Runtime.getRuntime().exec("ping -c 1 " + ipAddress);
	        int status = p.waitFor();
	        if (status == 0) {
	            return TYPE_PING_RESULT.PASS;
	        } else {
	            return TYPE_PING_RESULT.FAIL;
	        }
	    } catch (IOException e) {
	        return TYPE_PING_RESULT.FAIL_IOEXCEPTION;
	    } catch (InterruptedException e) {
	        return TYPE_PING_RESULT.FAIL_INTERUPTED_EXCEPTION;
	    }
	}
	 
	 
	 public static String getExternalIpAddress() {
		IpPageAnalyzer analyzer = new IpPageAnalyzer(IpQueryUrl);
		return analyzer.get_ipAddr();
	 }
	 
	 public static String getGeoInfoByIp(String Ip)
	 {
		 if (Ip == null) {
			Ip = getExternalIpAddress();
		}
		 IpPageAnalyzer analyzer = new IpPageAnalyzer("http://www.ip.cn/index.php?ip=" + Ip );
		 return analyzer.get_geoInfo();
	 }
	 
	 public static String getOwnerByIp(String Ip)
	 {
		 if (Ip == null) {
			 Ip = getExternalIpAddress();
		}
		 IpPageAnalyzer analyzer = new IpPageAnalyzer("http://www.ip.cn/index.php?ip=" + Ip );
		 return analyzer.get_owner();
	 }
	 
	 
	 

}
	 

