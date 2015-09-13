/*For Android usage, the following permissions must be added:
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.INTERNET"/> 
  
  #Current Version:1.0   
  #Version 1.0    @20140705
  	Enable basic download features with one download thread
   	
  Written by:GuYiwei 
  Email:Yiwei.gu09@gmail.com
  
  IN ACCORDANCE WITH GPL LICENECE
*/

package com.pocketdigi.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;

public class FileDownloader {
	private String url = null; // file URL
	private String path = null; // file storage path
	private Class<?> callerCls = null;

	public static final int MSG_START = 0;
	public static final int MSG_INPROGRESS = 1;
	public static final int MSG_ACCOMPLISH = 2;
	public static final int MSG_CANCELED = 3;
	public static final int MSG_ERROR = -1;

	private int id = -1;
	
	private File file = null; //dest File
	private int fileLength = 0;
	private int curPosition = 0; //tracks the current position; Pos:curPosition is not downloaded yet
	private int downloadSize = 0; //already download file size
	
	//control flag
	private boolean isPaused = false;
	private boolean isCanceling = false;
	
	private Thread thread = null;

	public FileDownloader(String url, String path, Handler handler,
			Class<?> callerCls) {
		this.url = url;
		this.path = path;
		this.callerCls = callerCls;
		// register handler in HandlerCenter
		HandlerCenter.addHandler(callerCls.getName(), handler);
		//HandlerCenter.addHandler(FileDownloader.class);
		
		//id = ++id_cnt;
		id = (int)System.currentTimeMillis();
		fileLength = getFileLength(); 
		
	}
		
	//Uniform Interface for Using FileDownloader
	public void runDownload()
	{
		//do nothing if it is canceling
		if (isCanceling) {
			LogUtil.v("id = " + id + "; " + "do nothing:is canceling");
			return;
		}
		
		//do nothing if exEcute runDownload successively
		if (curPosition != 0 && isPaused == false) {
			LogUtil.v("id = " + id + "; " + "do nothing:runDownload succesively");
			return;
		}
		
		if (file == null) {
			file = new File(getFullPath());
		}
		
		LogUtil.v("id = " + id + "; " + "runDownload: [" + (curPosition) + " , " + (fileLength - 1) + "]");

		isPaused = false;
		fragmentDownload(curPosition, fileLength, file);
	}
	
	public void pauseDownload()
	{	
		isPaused = true;
		thread.interrupt();

		
		//wait a while since halting running thread needs time
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			HandlerCenter.sendMessage(callerCls, MSG_ERROR);
			e.printStackTrace();
		}
		LogUtil.v("id = " + id + "; " + "Paused: cur = " + curPosition);
	}
	
	public void cancelDownload()
	{
		if (isCanceling) {
			LogUtil.v("id = " + id + "; " + "run cancelDownload successively");
			return; 
		}
		isCanceling = true; //guard for canceling process; start
		LogUtil.v("id = " + id + "; " + "cancelling...");
		pauseDownload();
		
		if (file != null && file.exists()) { //take care of the condition order
			file.delete();
			file = null;
		}
		curPosition = 0;
		downloadSize = 0;
		HandlerCenter.sendMessage(callerCls, MSG_CANCELED,
				packBundlewithId(curPosition));

		isCanceling = false; //guard for canceling process; done
		LogUtil.v("id = " + id + "; " + "cancel done");
	}

	public int getDownloaderId() {
		return id;
	}

	//download fragment [startPosition, endPosition); eg: [0, fileLen]
	public void fragmentDownload(final int startPosition, final int endPosition,
			final File file) {
	
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				final int BUFFER_SIZE = 1024 * 4;
				BufferedInputStream bis = null;
				RandomAccessFile fos = null;
				byte[] buf = new byte[BUFFER_SIZE];
				URLConnection con = null;
				try {
					
					//if start download
					if (downloadSize == 0) {
						HandlerCenter.sendMessage(callerCls, MSG_START,
								packBundlewithId(-1) );
					}

					URL myURL = new URL(url);
					con = myURL.openConnection();
					con.setAllowUserInteraction(true);
					// 设置当前线程下载的起点，终点
					con.setRequestProperty("Range", "bytes=" + startPosition
							+ "-" + endPosition);
					// 使用java中的RandomAccessFile 对文件进行随机读写操作
					fos = new RandomAccessFile(file, "rw");
					// 设置开始写文件的位置
					fos.seek(startPosition);
					bis = new BufferedInputStream(con.getInputStream());		
					
					// get fileSize
					int fileSize = con.getContentLength();

					if (fileSize < 0) {
						HandlerCenter.sendMessage(callerCls, MSG_ERROR);
					}
					
					// 开始循环以流的形式读写文件
					while (curPosition < endPosition) {
						if (isPaused) { //if paused
							bis.close(); //caution of leak
							fos.close();
							return;
						}
								
						int len = bis.read(buf, 0, BUFFER_SIZE);
						if (len == -1) {
							break;
						}
						fos.write(buf, 0, len);
						curPosition += len;
						downloadSize += len;
						
						HandlerCenter.sendMessage(callerCls, MSG_INPROGRESS,
								packBundlewithId(curPosition));
						LogUtil.v("id = " + id + "; " + "cur = " + curPosition);
						LogUtil.v("id = " + id + "; " + "downSize = " + downloadSize);
						//LogUtil.v("isPaused = " + String.valueOf(isPaused) + "; id = " + tid);
						

					}

					//if download accomplished
					if (downloadSize == fileLength) {
						HandlerCenter.sendMessage(callerCls, MSG_ACCOMPLISH,
								packBundlewithId(-1));
					}

				} catch (IOException e) {
					HandlerCenter.sendMessage(callerCls, MSG_ERROR);
					e.printStackTrace();
				}
				finally
				{
					try {
						bis.close();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			}
		});

		thread.start();
	}

	// get file fullPath
	public String getFullPath() {
		String filename = url.substring(url.lastIndexOf("/") + 1);
		return path + filename;
	}

	public String getFilename() {
		return url.substring(url.lastIndexOf("/") + 1);
	}
	
	//not thread safe
	public int getFileLength()
	{	
		int fileSize = -1;
		try {
			URL myURL = new URL(url);
			URLConnection conn = myURL.openConnection();
			
			// get fileSize
			fileSize = conn.getContentLength();
		} catch (IOException e) {
			fileSize = -1;
		}
		return fileSize;	
	}

	private Bundle packBundlewithId(int value) {
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		bundle.putInt("value", value);
		return bundle;
	}

	public static int getMessageId(Message msg) {
		Bundle bundle = (Bundle) msg.obj;
		return bundle.getInt("id");
	}

	public static int getMessageValue(Message msg) {
		Bundle bundle = (Bundle) msg.obj;
		return bundle.getInt("value");
	}
}
