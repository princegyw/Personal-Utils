/* 
  #Current version:1.0   
  #version 1.0    @20140705
  	>>HandlerCenter is a Center to handle all handlers of an App
  	  Designed with singleton pattern
  	  
   	
  Written by:GuYiwei 
  Email:Yiwei.gu09@gmail.com
  
  IN ACCORDANCE WITH GPL LICENECE
*/

package com.pocketdigi.download;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Handler;
import android.os.Message;

public class HandlerCenter {
	
	private static HandlerCenter handlerList = null;
	
	//singleton
	private HandlerCenter(){}
	
	public static HandlerCenter getInstance()
	{
		if (handlerList == null) {
			handlerList = new HandlerCenter();
		}
		return handlerList;
	}
	
	private Map<String, Handler>mMap = new HashMap<String, Handler>();
	
	public static void addHandler(String className, Handler handler)
	{
		if (getInstance().mMap.get(className) == null) {
			getInstance().mMap.put(className, handler);
		}
		else{
			removeHandler(className);
			getInstance().mMap.put(className, handler);
		}
	}
	
	public static synchronized void removeHandler(String className)
	{
		getInstance().mMap.remove(className);
	}
	
	//send Message by handler
	public static void sendMessage(Handler handler, int msgId, Object msgObj)   {
		if (handler != null) {
			Message message = handler.obtainMessage(msgId, msgObj);
			handler.sendMessage(message);
			
		} else {
			LogUtil.d("null handler");
		}
	}
	
	//send Delayed message by handler
	public static void sendMessageDelayed(Handler handler, int msgId, Object msgObj, long delayMillis)   {
		if (handler != null) {
			Message message = handler.obtainMessage(msgId, msgObj);
			handler.sendMessageDelayed(message, delayMillis);
			
		} else {
			LogUtil.d("null handler");
		}
	}

	//send Message to all handler in the HandlerCenter
	public synchronized static void sendMessageToAll(int msgId, Object msgObj)   {
		synchronized (getInstance().mMap) {
			for (Entry<String, Handler> entry : getInstance().mMap.entrySet()) {
				Handler handler = entry.getValue();
				sendMessage(handler, msgId, msgObj);
			}
		}
	}

	public static void sendMessageToAll(int msgId)   {
		sendMessageToAll(msgId, null);
	}

	//send message by className
	public static void sendMessage(String className, int msgId, Object msgObj)  
	{
		Handler handler = getInstance().mMap.get(className);
		sendMessage(handler, msgId, msgObj);
	}
	
	public static void sendMessage(String className, int msgId)  
	{
		Handler handler = getInstance().mMap.get(className);
		sendMessage(handler, msgId, null);
	}
	
	//send delayed message by className
	public static void sendMessageDelayed(String className, int msgId, Object msgObj, long delayMillis)  
	{
		Handler handler = getInstance().mMap.get(className);
		sendMessageDelayed(handler, msgId, msgObj, delayMillis);
	}
	
	public static void sendMessageDelayed(String className, int msgId, long delayMillis)  
	{
		Handler handler = getInstance().mMap.get(className);
		sendMessageDelayed(handler, msgId, null, delayMillis);
	}
	
	//send message by class
	public static void sendMessage(Class<?> cls, int msgId, Object msgObj)  
	{
		sendMessage(cls.getName(), msgId, msgObj);
	}
	
	public static void sendMessage(Class<?> cls, int msgId)  
	{
		sendMessage(cls.getName(), msgId);
	}
	
	//send delayed message by class
	public static void sendMessageDelayed(Class<?> cls, int msgId, Object msgObj, long delayMillis)  
	{
		sendMessageDelayed(cls.getName(), msgId, msgObj, delayMillis);
	}
	
	public static void sendMessageDelayed(Class<?> cls, int msgId, long delayMillis)  
	{
		sendMessageDelayed(cls.getName(), msgId, null, delayMillis);
	}
}
