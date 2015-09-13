package com.example.Utils;

import com.example.broadcastrecieverdemo.LogUtil;

import android.content.Context;
import android.os.PowerManager;

//singleton design
public class LightedGreenRoom {
	
	private volatile int count = 0; //keep count of visitors to the room
	private int clientCount = 0; //multi-client support  
	
	private PowerManager.WakeLock wakeLock = null;
	
	private static LightedGreenRoom lightedGreenRoom = null; //self
	
	private LightedGreenRoom(Context context) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LightedGreenRoom");
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public static void InitWithLightsOn(Context context) {
		if (lightedGreenRoom == null) {
			lightedGreenRoom = new LightedGreenRoom(context);
			lightedGreenRoom.turnOnLights();
		}
	}
	
	public static LightedGreenRoom getInstance() {
		return lightedGreenRoom;
	}
	
	//acquire wakeLock operation
	private void turnOnLights() {
		LogUtil.v("turnOnLights");
		wakeLock.acquire();
	}
	
	//release wakeLock operation
	private void turnOffLights() {
		LogUtil.v("turnOffLights");
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}
	
	//enter the room
	public synchronized void enter() {
		count++;
	}
	
	//leave the room
	public synchronized void leave() {
		if (count == 0) {
			return;
		}
		
		if (--count == 0) {
			turnOffLights(); //last visitor
		}
	}
	
	//register client; assume it always happen on the main thread; no sync
	public void registerClient() {
		clientCount++;
	}
	
	//un-register client; assume it always happen on the main thread; no sync
	public void unRegisterClient() {
		if (clientCount == 0) {
			return;
		}
		
		//if it is the last client who want to un-register(eg. called thru onDestroy() of a service); 
		//irrespective of count value
		if (--clientCount == 0) {
			count = 0;
			turnOffLights();
			lightedGreenRoom = null;
		}
	}
	
	public synchronized int getCount() {
		return count;
	}
	
	//assume called on main-thread
	public int getClientCount() {
		return clientCount;
	}
	
}
