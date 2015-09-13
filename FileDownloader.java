/*For Android usage, the following permissions must be added:
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.INTERNET"/>

    For Android 4.2+, the following codes must be added:
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	StrictMode.setThreadPolicy(policy);

  #Current version:4.1   

  Written by:GuYiwei
  Email:Yiwei.gu09@gmail.com

  IN ACCORDANCE WITH GPL LICENECE
*/

package com.example.apkdownloadspeedtest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;


public class FileDownloader {
	private String url = null; // file URL
	private String path = null; // file storage path

	public static final int MSG_START = 0;
	public static final int MSG_RESUME = 1;
	public static final int MSG_SEGSTART = 2;
	public static final int MSG_INPROGRESS = 3;
	public static final int MSG_SEGACCOMPLISH = 4;
	public static final int MSG_ACCOMPLISH = 5;
	public static final int MSG_PAUSED = 6;
	public static final int MSG_CANCELED = 7;
	public static final int MSG_ERROR = -1;

	private int id = -1; //id identify the download task

	private File file = null; //dest File

	//Total data
	private int fileLength = 0; //total file length
	private int totalDownloadSize = 0; //total already download size

	//Segment data
	private int [] downloadSize = null; //segment download size
	private int [] segmentSize = null; //segment size
	private int [] curPosition = null; //segment curPosition
	private int [] begPosition = null; //segment begPosition
	private int [] endPosition = null; //segment endPosition
	private boolean [] isSegmentDone = null; //segment done flag

	//control flag
	private boolean isPaused = false;
	private boolean isCanceling = false;

	//thread members
	private ThreadGroup threads = null; //threadGroup
	private int THREAD_NUM = 1;
	private Lock lock = null;   //thread lock

	//constants
	public final static int BUFFER_SIZE = 1024;

	//Interface of callbacks
	public static abstract class onFileDownloaderListener {
		public void onStart(int downloaderId, int fileLength){}
		public void onResume(int downloaderId){}
		public void  onSegStart(int downloaderId, int segId, int segLength){}
		public void  onInProcess(int downloaderId, int segId, int curPos, int begPos, int endPos, int totalDoneSize){}
		public abstract void onAccomplish(int downloaderId);
		public void onSegAccomplish(int downloaderId, int segId){}
		public void onPaused(int downloaderId){}
		public void onCanceled(int downloaderId){}
		public void onError(String errInfo){}
	}

	//HandlerGroup to handler message
	private static HandlerGroup handlerGroup= new HandlerGroup(); //Only one instance for all FileDownloaders

	//constructor
	@SuppressLint("NewApi")
	public FileDownloader(String url, String path, int threadNum) {
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		this.url = url;
		this.path = path;
		this.THREAD_NUM = threadNum;

		id = (int)System.currentTimeMillis();
		fileLength = onGetFileLength();

		//Initialize ThreadGroup
		threads = new ThreadGroup(String.valueOf(id));

		//threadNum pre-processor
		if (THREAD_NUM < 1 || THREAD_NUM >25) {
			THREAD_NUM = 1;
		}

		//initialize segment datum
		initSegmentData();

	}

	//initialize or reset segment data
	private void initSegmentData()
	{
		totalDownloadSize = 0;
		downloadSize =  new int[THREAD_NUM];
		segmentSize = new int[THREAD_NUM];

		curPosition = new int[THREAD_NUM];
		begPosition = new int[THREAD_NUM];
		endPosition = new int[THREAD_NUM];
		isSegmentDone = new boolean[THREAD_NUM];

		int blockSize = fileLength / THREAD_NUM; //segment size

		if (THREAD_NUM == 1) { //download with single thread
			begPosition[0] = 0;
			endPosition[0] = fileLength;
		}
		else {
			for (int i = 0; i < THREAD_NUM - 1; i++) {
				begPosition[i] = i * blockSize;
				endPosition[i] = begPosition[i] + blockSize;
				curPosition[i] = begPosition[i];
				segmentSize[i] = blockSize;
			}
			//The last segment is the remaining part when cut
			begPosition[THREAD_NUM-1] = endPosition[THREAD_NUM-2];
			endPosition[THREAD_NUM-1] = fileLength;
			curPosition[THREAD_NUM-1] = begPosition[THREAD_NUM-1];
			segmentSize[THREAD_NUM-1] = endPosition[THREAD_NUM-1] - begPosition[THREAD_NUM-1];
		}

	}

	//Uniform Interface for Using FileDownloader
	public void runDownload()
	{
		//do nothing if it is canceling
		if (isCanceling) {
			//LogUtil.v("id = " + id + "; " + "do nothing:is canceling");
			return;
		}

		//do nothing if exEcute runDownload successively
		if (isExistSegDownloadsInMid() && isPaused == false) {
			//LogUtil.v("id = " + id + "; " + "do nothing:runDownload succesively");
			return;
		}

		//if File is null
		if (file == null) {
			//LogUtil.v("file is null, new a file");
			file = new File(getFullPath());
		}

		isPaused = false;
		lock = new ReentrantLock(); //initialize thread lock to guard multiThreads reading and writing shared var

		//if start download
		if (totalDownloadSize == 0) {
			handlerGroup.sendMessageToAll( MSG_START, pack_MSG_START());
		}
		else {
			handlerGroup.sendMessageToAll( MSG_RESUME, pack_MSG_RESUME());
		}

		//new a download thread for each segment
		for (int tid = 0; tid < THREAD_NUM; tid++) {
			if (isSegmentDone[tid] == false) { //only download the unaccomplished segment
				//LogUtil.v("id = " + id + "," + "tid = " + tid + "; " + "runDownload: [" + (curPosition[tid]) + " , " + (endPosition[tid]) + ")");
				fragmentDownload(tid);
			}
		}
	}

	private boolean isExistSegDownloadsInMid()
	{
		for (int i = 0; i < isSegmentDone.length; i++) {
			if (curPosition[i] != begPosition[i]) {
				return true;
			}
		}
		return false;
	}

	public void pauseDownload()
	{
		isPaused = true;
		threads.interrupt();

		//wait a while since halting running threads needs time
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			handlerGroup.sendMessageToAll( MSG_ERROR);
			e.printStackTrace();
		}
		//LogUtil.v("id = " + id + "; " + "Paused");
		handlerGroup.sendMessageToAll( MSG_PAUSED, pack_MSG_PAUSED());
	}

	public void cancelDownload()
	{
		if (isCanceling) {
			//LogUtil.v("id = " + id + "; " + "run cancelDownload successively");
			return;
		}
		isCanceling = true; //guard for canceling process; start
		//LogUtil.v("id = " + id + "; " + "cancelling...");
		pauseDownload();

		if (file != null && file.exists()) { //take care of the condition order
			file.delete();
			file = null;
		}
		initSegmentData();
		handlerGroup.sendMessageToAll( MSG_CANCELED, pack_MSG_CANCELED());

		isCanceling = false; //guard for canceling process; done
		//LogUtil.v("id = " + id + "; " + "cancel done");
	}

	public void reDownload()
	{
		cancelDownload();
		runDownload();
	}

	//download fragment [startPosition, endPosition); eg: [0, fileLen)
	private void fragmentDownload(final int tid) {

		new Thread(threads, new Runnable() {

			@Override
			public void run() {
				BufferedInputStream bis = null;
				RandomAccessFile fos = null;
				byte[] buf = new byte[BUFFER_SIZE];
				URLConnection con = null;
				try {
					//if tid seg start download
					if (downloadSize[tid] == 0) {
						handlerGroup.sendMessageToAll( MSG_SEGSTART, pack_MSG_SEGSTART(tid));
					}

					URL myURL = new URL(url);
					con = myURL.openConnection();
					con.setAllowUserInteraction(true);
					// set http request bytes startPosition -> endPosition
					con.setRequestProperty("Range", "bytes=" + curPosition[tid] + "-" + endPosition[tid]);
					fos = new RandomAccessFile(file, "rw"); //rw file with RandomAccessFile

					// seek write position
					fos.seek(curPosition[tid]);
					//LogUtil.v("id = " + id + "," + "tid = " + tid + ";" + "seek position:" + fos.getFilePointer());
					bis = new BufferedInputStream(con.getInputStream());

					if (segmentSize[tid] < 0) {
						handlerGroup.sendMessageToAll( MSG_ERROR, pack_MSG_ERROR("error segementSize"));
						return;
					}

					// loop to read bis and write to fos; Stream controls read-write thread synchronization
					while (curPosition[tid] < endPosition[tid]) {
						if (isPaused) { //if paused
							bis.close(); //caution of leak
							fos.close();
							return;
						}

						int len = bis.read(buf, 0, BUFFER_SIZE);
						if (len == -1) {
							break;
						}

						lock.lock(); //Critical area begin

						curPosition[tid] += len;
						if (curPosition[tid] >= endPosition[tid]) {
							//done seg download
							len = len - (curPosition[tid] - endPosition[tid]);
							curPosition[tid] = endPosition[tid];
							downloadSize[tid] = segmentSize[tid];

							totalDownloadSize += len;
							fos.write(buf, 0, len);
							isSegmentDone[tid] = true;

							//LogUtil.v("id = " + id + "," + "tid = " + tid + "; " + "downSegSize = " + downloadSize[tid] + "segDone");
							//LogUtil.v("id = " + id + "," + "tid = " + tid + "; " + "totalDoneSize = " + totalDownloadSize);
							handlerGroup.sendMessageToAll( MSG_INPROGRESS, pack_MSG_INPROCESS(tid) );
							handlerGroup.sendMessageToAll( MSG_SEGACCOMPLISH, pack_MSG_SEGACCOMPLISH(tid));
						}
						else
						{
							downloadSize[tid] += len;
							totalDownloadSize += len;

							//LogUtil.v("before write fos points: " + fos.getFilePointer());
							//LogUtil.v("writeLen = " + len);
							fos.write(buf, 0, len);
							//LogUtil.v("after write fos points: " + fos.getFilePointer());

							//LogUtil.v("id = " + id + "," + "tid = " + tid + "; " + "downSegSize = " + downloadSize[tid]);
							//LogUtil.v("id = " + id + "," + "tid = " + tid + "; " + "totalDoneSize = " + totalDownloadSize);
							handlerGroup.sendMessageToAll( MSG_INPROGRESS, pack_MSG_INPROCESS(tid) );
						}

						lock.unlock(); //Critical area end

					} //end of while


						//if all segs download accomplished
					if (totalDownloadSize == fileLength) {
						//LogUtil.v("id = " + id + "; ALLDone: accompSize = " + totalDownloadSize);
						handlerGroup.sendMessageToAll( MSG_ACCOMPLISH, pack_MSG_ACCOMPLISH());
					}

				} catch (IOException e) {
					handlerGroup.sendMessageToAll(MSG_ERROR);
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
		}).start(); //thread start
	}

	//Listener for user to track messages send by FileDownloader
	//To implement user's own callback functions, just to implement onFileDownloaderListener Interface
	//Every call will new a handler in handlerGroup
	public static void setOnFileDownloaderListener(Class<?> callercls, onFileDownloaderListener listener) {


		FileDownloaderHandler handler = new FileDownloaderHandler(listener);

		// register inner handler
		handlerGroup.addHandler(callercls, handler);
	}

	public static class FileDownloaderHandler extends Handler
	{
		private onFileDownloaderListener listener;

		public FileDownloaderHandler(onFileDownloaderListener listener) {
			this.listener = listener;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			int downloaderId = unPack_MSG_id(msg); //download task Id
			int segId = -1;

			switch (msg.what) {

				case FileDownloader.MSG_START:
					int fileLen = unPack_MSG_START_fileLength(msg);
					listener.onStart(downloaderId, fileLen);
					break;

			    case FileDownloader.MSG_RESUME:
			    	listener.onResume(downloaderId);
			    	break;

				case FileDownloader.MSG_SEGSTART:
					segId = unPack_MSG_SEGSTART_segId(msg);
					int segLength = unPack_MSG_SEGSTART_segLength(msg);
					listener.onSegStart(downloaderId, segId, segLength);
					break;

				case FileDownloader.MSG_INPROGRESS:
					segId = unPack_MSG_INPROGRESS_segId(msg);
					int curPos = unPack_MSG_INPROGRESS_curPos(msg);
					int begPos = unPack_MSG_INPROGRESS_begPos(msg);
					int endPos = unPack_MSG_INPROGRESS_endPos(msg);
					int totalDoneSize = unPack_MSG_INPROGRESS_totalDoneSize(msg);
					listener.onInProcess(downloaderId, segId, curPos, begPos, endPos, totalDoneSize);
					break;

			    case FileDownloader.MSG_ACCOMPLISH:
			    	listener.onAccomplish(downloaderId);
			    	break;

			    case FileDownloader.MSG_SEGACCOMPLISH:
			    	segId = unPack_MSG_SEGACCOMPLISH_segId(msg);
			    	listener.onSegAccomplish(downloaderId, segId);
			    	break;

			    case FileDownloader.MSG_PAUSED:
			    	listener.onPaused(downloaderId);
			    	break;

			    case FileDownloader.MSG_CANCELED:
			    	listener.onCanceled(downloaderId);
			    	break;

			    case FileDownloader.MSG_ERROR:
			    	String errInfo = unPack_MSG_ERROR_errInfo(msg);
			    	listener.onError(errInfo);
			    	break;

			} // end of switch

		}


	}

	//get Download Task Id
	public int getDownloaderId() {
		return id;
	}

	// get file fullPath
	public String getFullPath() {
		String filename = url.substring(url.lastIndexOf("/") + 1);
		return path + filename;
	}

	//get filename
	public String getFilename() {
		return url.substring(url.lastIndexOf("/") + 1);
	}

	//get fileLength; for user
	public int getFileLength()
	{
		return fileLength;
	}

	//get fileLength; for private use
	private int onGetFileLength()
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

	private Bundle newBundleInstanceWithId()
	{
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		return bundle;
	}

	//packs are for packing Message bundle data
	private Bundle pack_MSG_START()
	{
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("fileLength", fileLength);
		return bundle;
	}

	private Bundle pack_MSG_RESUME()
	{
		return new Bundle(newBundleInstanceWithId());
	}

	private Bundle pack_MSG_SEGSTART(int segId)
	{
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("segId", segId);
		bundle.putInt("segLength", segmentSize[segId]);
		return bundle;
	}

	private Bundle pack_MSG_ACCOMPLISH()
	{
		return new Bundle(newBundleInstanceWithId());
	}

	private Bundle pack_MSG_PAUSED()
	{
		return new Bundle(newBundleInstanceWithId());
	}

	private Bundle pack_MSG_CANCELED()
	{
		return new Bundle(newBundleInstanceWithId());
	}

	private Bundle pack_MSG_SEGACCOMPLISH(int segId)
	{
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("segId", segId);
		return bundle;
	}

	private Bundle pack_MSG_ERROR(String err_info)
	{
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putString("errInfo", err_info);
		return bundle;
	}

	private Bundle pack_MSG_INPROCESS(int segId)
	{
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("segId", segId);
		bundle.putInt("curPos", curPosition[segId]);
		bundle.putInt("begPos", begPosition[segId]);
		bundle.putInt("endPos", endPosition[segId]);
		bundle.putInt("totalDoneSize", totalDownloadSize);
		return bundle;
	}

	////unPacks are for user to get Message information
	private static int unPack_MSG_id(Message msg)
	{
		return ((Bundle) msg.obj).getInt("id");
	}

	private static int unPack_MSG_START_fileLength(Message msg)
	{
		return ((Bundle) msg.obj).getInt("fileLength");
	}

	private static int unPack_MSG_SEGSTART_segId(Message msg)
	{
		return ((Bundle) msg.obj).getInt("segId");
	}

	private static int unPack_MSG_SEGSTART_segLength(Message msg)
	{
		return ((Bundle) msg.obj).getInt("segLength");
	}

	private static String unPack_MSG_ERROR_errInfo(Message msg)
	{
		return ((Bundle) msg.obj).getString("errInfo");
	}

	private static int unPack_MSG_INPROGRESS_curPos(Message msg)
	{
		return ((Bundle) msg.obj).getInt("curPos");
	}

	private static int unPack_MSG_INPROGRESS_begPos(Message msg)
	{
		return ((Bundle) msg.obj).getInt("begPos");
	}

	private static int unPack_MSG_INPROGRESS_endPos(Message msg)
	{
		return ((Bundle) msg.obj).getInt("endPos");
	}

	private static int unPack_MSG_INPROGRESS_segId(Message msg)
	{
		return ((Bundle) msg.obj).getInt("segId");
	}

	private static int unPack_MSG_INPROGRESS_totalDoneSize(Message msg)
	{
		return ((Bundle) msg.obj).getInt("totalDoneSize");
	}

	private static int unPack_MSG_SEGACCOMPLISH_segId(Message msg)
	{
		return ((Bundle) msg.obj).getInt("segId");
	}



}
