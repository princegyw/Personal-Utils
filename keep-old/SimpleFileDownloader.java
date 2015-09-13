package com.pocketdigi.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class FileDownloader {
	private String url = null; //file URL
	private String path = null; //file storage path
	private Class<?> callerCls = null;
	
	public static final int MSG_START = 0;
	public static final int MSG_FILELENGTH = 1;
	public static final int MSG_INPROGRESS = 2;
	public static final int MSG_ACCOMPLISH = 3;
	public static final int MSG_ERROR = -1;
	
	private static int tid_cnt = -1;
	private int tid = -1;
	
	public FileDownloader(String url, String path, Handler handler, Class<?> callerCls)
	{
		this.url = url;
		this.path = path;
		this.callerCls = callerCls;
		//register handler in HandlerCenter
		HandlerCenter.addHandler(callerCls.getName(), handler);
		tid = ++tid_cnt;
	}
	
	public int getDownloaderId()
	{
		return tid;
	}
	
	public void download()
	{
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					HandlerCenter.sendMessage(callerCls, MSG_START, packBundlewithTid((int)Thread.currentThread().getId()));
					URL myURL = new URL(url);
					URLConnection conn = myURL.openConnection();
					conn.connect();
					
					//get fileSize
					int fileSize = conn.getContentLength();

					InputStream in = conn.getInputStream();
					
					if (fileSize < 0 || in == null) {
						throw new IOException();
					}
					
					FileOutputStream fos = new FileOutputStream(getFullPath());
					
					byte buffer[] = new byte[1024];
					//send file-length msg
					HandlerCenter.sendMessage(callerCls, MSG_FILELENGTH, packBundlewithTid(fileSize));
					int downloadFileSize = 0;
					while(true)
					{
						int numread = in.read(buffer);
						if (numread < 0) {
							//read ends
							break;
						}
						
						fos.write(buffer, 0, numread);
						downloadFileSize += numread;
						HandlerCenter.sendMessage(callerCls, MSG_INPROGRESS, packBundlewithTid(downloadFileSize));
					}
					
					//done download
					HandlerCenter.sendMessage(callerCls, MSG_ACCOMPLISH, packBundlewithTid(-1));
					
					//close
					in.close();
					fos.close();
					
				} catch (IOException e) {
					HandlerCenter.sendMessage(callerCls, MSG_ERROR);
					e.printStackTrace();	
				}
				
			}
		});
		
		thread.start();
	}
	
	//get file fullPath
	public String getFullPath()
	{
		String filename = url.substring(url.lastIndexOf("/") + 1);
		return path + filename;
	}
	
	public String getFilename()
	{
		return url.substring(url.lastIndexOf("/") + 1);
	}
	
	private Bundle packBundlewithTid(int value)
	{
		Bundle bundle = new Bundle();
		bundle.putInt("tid", tid);
		bundle.putInt("value", value);
		return bundle;
	}
	
	public static int getMessageId(Message msg)
	{
		Bundle bundle = (Bundle)msg.obj;
		return bundle.getInt("tid");
	}
	
	public static int getMessageValue(Message msg)
	{
		Bundle bundle = (Bundle)msg.obj;
		return bundle.getInt("value");
	}
}
