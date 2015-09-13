package painkiller.multithread_download;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Handler;
import android.os.Message;

public class HandlerGroup {
	
	private Map<String, Handler>mMap = null;
	
	//constructor
	public HandlerGroup()
	{
		mMap = new HashMap<String, Handler>();
	}
	
	public void destroy()
	{
		for (Iterator<?> iterator = mMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iterator.next();
			removeHandler((String) entry.getKey());
		}
	}
	
	public synchronized void addHandler(String className, Handler handler)
	{
		if (mMap.get(className) == null) {
			mMap.put(className, handler);
		}
		else{
			removeHandler(className);
			mMap.put(className, handler);
		}
	}
	
	public void addHandler(Class<?> cls, Handler handler)
	{
		addHandler(cls.getName(), handler);
	}
	
	public synchronized void removeHandler(String className)
	{
		mMap.remove(className);
	}
	
	public void removeHandler(Class<?> cls)
	{
		removeHandler(cls.getName());
	}
	
	//send Message by handler
	public void sendMessage(Handler handler, int msgId, Object msgObj)   {
		if (handler != null) {
			Message message = handler.obtainMessage(msgId, msgObj);
			handler.sendMessage(message);
			
		}
	}
	
	//send Delayed message by handler
	public void sendMessageDelayed(Handler handler, int msgId, Object msgObj, long delayMillis)   {
		if (handler != null) {
			Message message = handler.obtainMessage(msgId, msgObj);
			handler.sendMessageDelayed(message, delayMillis);
			
		}
	}
	
	//send Message to all handler in the HandlerCenter
	public synchronized void sendMessageToAll(int msgId, Object msgObj)   {
		synchronized (mMap) {
			for (Entry<String, Handler> entry : mMap.entrySet()) {
				Handler handler = entry.getValue();
				sendMessage(handler, msgId, msgObj);
			}
		}
	}

	public void sendMessageToAll(int msgId)   {
		sendMessageToAll(msgId, null);
	}

	//send message by className
	public void sendMessage(String className, int msgId, Object msgObj)  
	{
		Handler handler = mMap.get(className);
		sendMessage(handler, msgId, msgObj);
	}
	
	public void sendMessage(String className, int msgId)  
	{
		Handler handler = mMap.get(className);
		sendMessage(handler, msgId, null);
	}
	
	//send delayed message by className
	public void sendMessageDelayed(String className, int msgId, Object msgObj, long delayMillis)  
	{
		Handler handler = mMap.get(className);
		sendMessageDelayed(handler, msgId, msgObj, delayMillis);
	}
	
	public void sendMessageDelayed(String className, int msgId, long delayMillis)  
	{
		Handler handler = mMap.get(className);
		sendMessageDelayed(handler, msgId, null, delayMillis);
	}
	
	//send message by class
	public void sendMessage(Class<?> cls, int msgId, Object msgObj)  
	{
		sendMessage(cls.getName(), msgId, msgObj);
	}
	
	public void sendMessage(Class<?> cls, int msgId)  
	{
		sendMessage(cls.getName(), msgId);
	}
	
	//send delayed message by class
	public void sendMessageDelayed(Class<?> cls, int msgId, Object msgObj, long delayMillis)  
	{
		sendMessageDelayed(cls.getName(), msgId, msgObj, delayMillis);
	}
	
	public void sendMessageDelayed(Class<?> cls, int msgId, long delayMillis)  
	{
		sendMessageDelayed(cls.getName(), msgId, null, delayMillis);
	}
}

