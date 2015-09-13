package com.example.easytrans;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

public class InternetUtil {
	
	 public static String getLocalIpAddress() {
	    	try {
	    		// ��������ӿ�
	    		Enumeration<NetworkInterface> infos = NetworkInterface
	    				.getNetworkInterfaces();
	    		while (infos.hasMoreElements()) {
	    			// ��ȡ����ӿ�
	    			NetworkInterface niFace = infos.nextElement();
	    			Enumeration<InetAddress> enumIpAddr = niFace.getInetAddresses();
	    			while (enumIpAddr.hasMoreElements()) {
	    				InetAddress mInetAddress = enumIpAddr.nextElement();
	    				// ����ȡ�������ַ����127.0.0.1ʱ���صõõ���IP
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
	 
}
