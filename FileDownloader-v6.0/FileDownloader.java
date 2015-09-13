package com.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;

public class FileDownloader {

	private String url = null; // file URL
	private String path = null; // file storage path

	private static final int MSG_START = 0;
	private static final int MSG_SEGSTART = 1;
	private static final int MSG_RESUME = 2;
	private static final int MSG_RESUMESESSION = 3;
	private static final int MSG_INPROGRESS = 4;
	private static final int MSG_SEGACCOMPLISH = 5;
	private static final int MSG_ACCOMPLISH = 6;
	private static final int MSG_PAUSED = 7;
	private static final int MSG_CANCELED = 8;
	private static final int MSG_HAVEDONE = 9;
	private static final int MSG_ERROR = -1;

	private int id = -1; // id identify the download task

	private File file = null; // dest File

	// Total data
	private int fileLength = 0; // total file length
	private int totalDownloadSize = 0; // total already download size

	// Segment data
	private int[] downloadSize = null; // segment download size
	private int[] segmentSize = null; // segment size
	private int[] curPosition = null; // segment curPosition
	private int[] begPosition = null; // segment begPosition
	private int[] endPosition = null; // segment endPosition
	private boolean[] isSegmentDone = null; // segment done flag

	// control flag
	private boolean isPaused = false;
	private boolean isCanceling = false;
	private boolean isRunning = false;

	// thread members
	private ThreadGroup threads = null; // threadGroup
	private int THREAD_NUM = 1;
	private Lock lock = null; // thread lock

	// constants
	private final static int BUFFER_SIZE = 1024;

	// database
	private DB_downloader db = null;
	private DownloadInfo downloadInfo_all = null;
	private DownloadInfo downloadInfo_seg = null;

	// ResumeStatusCode
	int dataStatus = -1;

	// HandlerGroup to handler message
	private static HandlerGroup handlerGroup = new HandlerGroup();

	// constructor
	@SuppressLint("NewApi")
	public FileDownloader(Context context, String url, String path,
			int threadNum) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		this.url = url;
		this.path = path;
		this.THREAD_NUM = threadNum;
		db = new DB_downloader(context);

		id = (int) System.currentTimeMillis();
		fileLength = onGetFileLength();

		// Initialize ThreadGroup
		threads = new ThreadGroup(String.valueOf(id));

		// threadNum pre-processor
		if (THREAD_NUM < 1 || THREAD_NUM > 25) {
			THREAD_NUM = 1;
		}

		dataStatus = judgeDbDataStatus();
		// LogUtil.v("dataStatus = " + dataStatus);

		if (dataStatus == FILE_DONE_PROPER || dataStatus == FILE_UNDONE_PROPER) {
			loadDownloadData();
		} else {
			initSegmentData(dataStatus);
		}

		// send RESUMESESSION message
		handlerGroup.sendMessageToAll(MSG_RESUMESESSION,
				pack_MSG_RESUMESESSION());

	}

	// initialize or reset segment data
	private void initSegmentData(int dataStatus) {
		downloadInfo_all = new DownloadInfo(id, -1, THREAD_NUM, fileLength, -1,
				fileLength, 0, 0, url);
		if (dataStatus == FILE_UNDONE_THREADNUM_CHANGED
				|| dataStatus == FILE_DONE_THREADNUM_CHANGED
				|| dataStatus == FILE_CLEAR) { // remove exist datum in db
			db.removeDownloadInfo(downloadInfo_all);
		}

		if (dataStatus == FILE_FIRST_TIME_DOWNLOAD
				|| dataStatus == FILE_UNDONE_THREADNUM_CHANGED
				|| dataStatus == FILE_DONE_THREADNUM_CHANGED) { // save
			db.saveInfo(downloadInfo_all);
		} else { // update
			db.updateDownloadInfo(downloadInfo_all);
		}

		totalDownloadSize = 0;
		downloadSize = new int[THREAD_NUM];
		segmentSize = new int[THREAD_NUM];

		curPosition = new int[THREAD_NUM];
		begPosition = new int[THREAD_NUM];
		endPosition = new int[THREAD_NUM];
		isSegmentDone = new boolean[THREAD_NUM];

		int blockSize = fileLength / THREAD_NUM; // segment size

		if (THREAD_NUM == 1) { // download with single thread
			begPosition[0] = 0;
			endPosition[0] = fileLength;

			// save segInfo for single thread
			downloadInfo_seg = new DownloadInfo(id, 0, 0, fileLength, 0,
					fileLength, 0, 0, url);
			if (dataStatus == FILE_FIRST_TIME_DOWNLOAD
					|| dataStatus == FILE_UNDONE_THREADNUM_CHANGED
					|| dataStatus == FILE_DONE_THREADNUM_CHANGED) { // save
				db.saveInfo(downloadInfo_seg);
			} else { // update
				db.updateDownloadInfo(downloadInfo_seg);
			}
		} else {
			for (int i = 0; i < THREAD_NUM - 1; i++) {
				begPosition[i] = i * blockSize;
				endPosition[i] = begPosition[i] + blockSize;
				curPosition[i] = begPosition[i];
				segmentSize[i] = blockSize;

				// save segInfo for each thread
				downloadInfo_seg = new DownloadInfo(id, i, begPosition[i],
						endPosition[i], curPosition[i], segmentSize[i], 0, 0,
						url);
				if (dataStatus == FILE_FIRST_TIME_DOWNLOAD
						|| dataStatus == FILE_UNDONE_THREADNUM_CHANGED
						|| dataStatus == FILE_DONE_THREADNUM_CHANGED) { // save
					db.saveInfo(downloadInfo_seg);
				} else { // update
					db.updateDownloadInfo(downloadInfo_seg);
				}
			}
			// The last segment is the remaining part when cut
			begPosition[THREAD_NUM - 1] = endPosition[THREAD_NUM - 2];
			endPosition[THREAD_NUM - 1] = fileLength;
			curPosition[THREAD_NUM - 1] = begPosition[THREAD_NUM - 1];
			segmentSize[THREAD_NUM - 1] = endPosition[THREAD_NUM - 1]
					- begPosition[THREAD_NUM - 1];

			// save segInfo for the last thread
			downloadInfo_seg = new DownloadInfo(id, THREAD_NUM - 1,
					begPosition[THREAD_NUM - 1], endPosition[THREAD_NUM - 1],
					curPosition[THREAD_NUM - 1], segmentSize[THREAD_NUM - 1],
					0, 0, url);
			if (dataStatus == FILE_FIRST_TIME_DOWNLOAD
					|| dataStatus == FILE_UNDONE_THREADNUM_CHANGED
					|| dataStatus == FILE_DONE_THREADNUM_CHANGED) { // save
				db.saveInfo(downloadInfo_seg);
			} else { // update
				db.updateDownloadInfo(downloadInfo_seg);
			}
		}

	}

	// load data from DB
	private void loadDownloadData() {
		List<DownloadInfo> info_seg_get = db.getSegInfosByUrl(url);
		DownloadInfo info_all_get = db.getAllInfoByUrl(url);
		id = info_all_get.task_id;
		totalDownloadSize = info_all_get.complete_size;
		THREAD_NUM = info_seg_get.size();

		// initialize arrays
		downloadSize = new int[THREAD_NUM];
		segmentSize = new int[THREAD_NUM];

		curPosition = new int[THREAD_NUM];
		begPosition = new int[THREAD_NUM];
		endPosition = new int[THREAD_NUM];
		isSegmentDone = new boolean[THREAD_NUM];

		for (DownloadInfo downloadInfo : info_seg_get) {
			int tid = downloadInfo.thread_id;
			downloadSize[tid] = downloadInfo.complete_size;
			segmentSize[tid] = downloadInfo.seg_size;
			curPosition[tid] = downloadInfo.cur_pos;
			begPosition[tid] = downloadInfo.start_pos;
			endPosition[tid] = downloadInfo.end_pos;
			if (downloadInfo.is_done == 1) {
				isSegmentDone[tid] = true;
			} else {
				isSegmentDone[tid] = false;
			}
		}
	}

	public static final int FILE_DONE_PROPER = 0; // file has been downloaded
													// and can be found in path
	public static final int FILE_DONE_THREADNUM_CHANGED = 1; // file has been
																// downloaded
																// but can not
																// be found in
																// path
	public static final int FILE_DONE_PATH_CHANGED = 2; // file has been
															// downloaded but
															// can not be found
															// in path
	public static final int FILE_UNDONE_PROPER = 3; // file download
													// unaccomplished and
													// can be found in path
	public static final int FILE_UNDONE_THREADNUM_CHANGED = 4; // file
																// download
																// unaccomplished
																// but
																// THREAD_NUM
																// changes
	public static final int FILE_UNDONE_PATH_CHANGED = 5; // file download
															// unaccomplished
															// but can not be
															// found in path
	public static final int FILE_FIRST_TIME_DOWNLOAD = 6; // file never be downloaded

	public static final int FILE_CLEAR = -1;

	private int judgeDbDataStatus() {
		File file = new File(getFullPath());
		DownloadInfo info_all_get = db.getAllInfoByUrl(url);

		if (info_all_get == null) {
			return FILE_FIRST_TIME_DOWNLOAD;
		}

		if (info_all_get.is_done == 1) { // file has been downloaded
			if (file.exists()) { // file exist
				return FILE_DONE_PROPER;
			} else if (info_all_get.start_pos != THREAD_NUM) { // thread num
																// changes
				return FILE_DONE_THREADNUM_CHANGED;
			} else {
				return FILE_DONE_PATH_CHANGED;
			}
		} else { // file download unaccomplished
			if (info_all_get.start_pos != THREAD_NUM) { // start_pos is used as
														// thread_num for
														// allInfo record
				return FILE_UNDONE_THREADNUM_CHANGED;
			} else if (!file.exists()) {
				return FILE_UNDONE_PATH_CHANGED;
			} else {
				return FILE_UNDONE_PROPER;
			}
		}
	}

	// Uniform Interface for Using FileDownloader
	public void runDownload() {
		// do nothing if it is canceling
		if (isCanceling) {
			// LogUtil.v("id = " + id + "; " + "do nothing:is canceling");
			return;
		}

		// do nothing if exEcute runDownload successively
		if (isRunning) {
			// LogUtil.v("id = " + id + ";do nothing:runDownload successively");
			return;
		}

		// if File is null
		if (file == null) {
			// LogUtil.v("file is null, new a file");
			file = new File(getFullPath());
		}

		isPaused = false;
		lock = new ReentrantLock(); // initialize thread lock to guard
									// multiThreads reading and writing shared
									// var

		// if start download
		if (totalDownloadSize == 0) { // start to download from beginning
			handlerGroup.sendMessageToAll(MSG_START, pack_MSG_START());
		} else if (totalDownloadSize == fileLength) { // has already done
			handlerGroup.sendMessageToAll(MSG_HAVEDONE, pack_MSG_HAVEDONE());
			return;
		} else {
			handlerGroup.sendMessageToAll(MSG_RESUME, pack_MSG_RESUME());
		}

		isRunning = true;

		// new a download thread for each segment
		for (int tid = 0; tid < THREAD_NUM; tid++) {
			if (isSegmentDone[tid] == false) { // only download the
												// unaccomplished segment
				// LogUtil.v("id = " + id + "," + "tid = " + tid + "; " +
				// "runDownload: [" + (curPosition[tid]) + " , " +
				// (endPosition[tid]) + ")");
				fragmentDownload(tid);
			}
		}
	}

	public void pauseDownload() {
		isPaused = true;
		isRunning = false;
		threads.interrupt();

		// wait a while since halting running threads needs time
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			handlerGroup.sendMessageToAll(MSG_ERROR);
			e.printStackTrace();
		}
		// LogUtil.v("id = " + id + "; " + "Paused");
		handlerGroup.sendMessageToAll(MSG_PAUSED, pack_MSG_PAUSED());
	}

	public void cancelDownload() {
		if (isCanceling) {
			// LogUtil.v("id = " + id + "; " +
			// "run cancelDownload successively");
			return;
		}
		isCanceling = true; // guard for canceling process; start
		// LogUtil.v("id = " + id + "; " + "cancelling...");
		pauseDownload();

		// if File is null
		if (file == null) {
			// LogUtil.v("file is null, new a file");
			file = new File(getFullPath());
		}

		if (file.exists()) {// delete file on disk
			file.delete();
			file = null;
		}
		initSegmentData(FILE_CLEAR);
		handlerGroup.sendMessageToAll(MSG_CANCELED, pack_MSG_CANCELED());

		isCanceling = false; // guard for canceling process; done
		// LogUtil.v("id = " + id + "; " + "cancel done");
	}

	public void reDownload() {
		cancelDownload();
		runDownload();
	}

	// download fragment [startPosition, endPosition); eg: [0, fileLen)
	private void fragmentDownload(final int tid) {

		new Thread(threads, new Runnable() {

			@Override
			public void run() {
				BufferedInputStream bis = null;
				RandomAccessFile fos = null;
				byte[] buf = new byte[BUFFER_SIZE];
				URLConnection con = null;
				try {
					// if tid seg start download
					if (downloadSize[tid] == 0) {
						handlerGroup.sendMessageToAll(MSG_SEGSTART,
								pack_MSG_SEGSTART(tid));
					}

					URL myURL = new URL(url);
					con = myURL.openConnection();
					con.setAllowUserInteraction(true);
					// set http request bytes startPosition -> endPosition
					con.setRequestProperty("Range", "bytes=" + curPosition[tid]
							+ "-" + endPosition[tid]);
					fos = new RandomAccessFile(file, "rw"); // rw file with
															// RandomAccessFile

					// seek write position
					fos.seek(curPosition[tid]);
					// LogUtil.v("id = " + id + "," + "tid = " + tid + ";" +
					// "seek position:" + fos.getFilePointer());
					bis = new BufferedInputStream(con.getInputStream());

					if (segmentSize[tid] < 0) {
						handlerGroup.sendMessageToAll(MSG_ERROR,
								pack_MSG_ERROR("error segementSize"));
						return;
					}

					// loop to read bis and write to fos; Stream controls
					// read-write thread synchronization
					while (curPosition[tid] < endPosition[tid]) {
						if (isPaused) { // if paused
							bis.close(); // caution of leak
							fos.close();
							return;
						}

						int len = bis.read(buf, 0, BUFFER_SIZE);
						if (len == -1) {
							break;
						}

						lock.lock(); // Critical area begin

						curPosition[tid] += len;
						if (curPosition[tid] >= endPosition[tid]) {
							// done seg download
							len = len - (curPosition[tid] - endPosition[tid]);
							curPosition[tid] = endPosition[tid];
							downloadSize[tid] = segmentSize[tid];

							totalDownloadSize += len;
							fos.write(buf, 0, len);
							isSegmentDone[tid] = true;

							// LogUtil.v("id = " + id + "," + "tid = " + tid +
							// "; " + "downSegSize = " + downloadSize[tid] +
							// "segDone");
							// LogUtil.v("id = " + id + "," + "tid = " + tid +
							// "; " + "totalDoneSize = " + totalDownloadSize);

							// update segInfo when seg done
							downloadInfo_seg = new DownloadInfo(id, tid,
									begPosition[tid], endPosition[tid],
									curPosition[tid], segmentSize[tid],
									downloadSize[tid], 1, url);
							db.updateDownloadInfo(downloadInfo_seg);

							// update allInfo
							downloadInfo_all = new DownloadInfo(id, -1,
									THREAD_NUM, fileLength, -1, fileLength,
									totalDownloadSize, 0, url);
							db.updateDownloadInfo(downloadInfo_all);

							handlerGroup.sendMessageToAll(MSG_INPROGRESS,
									pack_MSG_INPROCESS(tid));
							handlerGroup.sendMessageToAll(MSG_SEGACCOMPLISH,
									pack_MSG_SEGACCOMPLISH(tid));
						} else {
							downloadSize[tid] += len;
							totalDownloadSize += len;

							// LogUtil.v("before write fos points: " +
							// fos.getFilePointer());
							// LogUtil.v("writeLen = " + len);
							fos.write(buf, 0, len);
							// LogUtil.v("after write fos points: " +
							// fos.getFilePointer());

							// LogUtil.v("id = " + id + "," + "tid = " + tid +
							// "; " + "downSegSize = " + downloadSize[tid]);
							// LogUtil.v("id = " + id + "," + "tid = " + tid +
							// "; " + "totalDoneSize = " + totalDownloadSize);

							// update segInfo when seg not done
							downloadInfo_seg = new DownloadInfo(id, tid,
									begPosition[tid], endPosition[tid],
									curPosition[tid], segmentSize[tid],
									downloadSize[tid], 0, url);
							db.updateDownloadInfo(downloadInfo_seg);

							// update allInfo
							downloadInfo_all = new DownloadInfo(id, -1,
									THREAD_NUM, fileLength, -1, fileLength,
									totalDownloadSize, 0, url);
							db.updateDownloadInfo(downloadInfo_all);

							handlerGroup.sendMessageToAll(MSG_INPROGRESS,
									pack_MSG_INPROCESS(tid));
						}

						lock.unlock(); // Critical area end

					} // end of while

					// if all segs download accomplished
					if (totalDownloadSize == fileLength) {
						// LogUtil.v("id = " + id + "; ALLDone: accompSize = " +
						// totalDownloadSize);
						// set isRunning flag
						isRunning = false;
						// update allInfo
						downloadInfo_all = new DownloadInfo(id, -1, THREAD_NUM,
								fileLength, -1, fileLength, totalDownloadSize,
								1, url);
						db.updateDownloadInfo(downloadInfo_all);
						handlerGroup.sendMessageToAll(MSG_ACCOMPLISH,
								pack_MSG_ACCOMPLISH());
					}

				} catch (IOException e) {
					handlerGroup.sendMessageToAll(MSG_ERROR);
					e.printStackTrace();
				} finally {
					try {
						bis.close();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start(); // thread start
	}

	// Listener for user to track messages send by FileDownloader
	// To implement user's own callback functions, just to implement Listener
	// Interface of onDownloadProcessListener
	public interface onDownloadProcessListener {
		public void onResumeSession(int downloaderId, int resumeStatusCode,
				int fileLength, int totalDoneSize);

		public void onInProcess(int downloaderId, int segId, int curPos,
				int begPos, int endPos, int totalDoneSize);

		public void onAccomplish(int downloaderId);
	}

	// set listener
	public static void setOnDownloadProcessListener(
			onDownloadProcessListener listener) {
		DownloadProcessHandler handler = new DownloadProcessHandler(listener);
		handlerGroup.addHandler("DOWNLOAD_CENTER_OnDownloadProcess", handler);
	}

	// msg handler and dispatch to corresponding callbacks
	private static class DownloadProcessHandler extends Handler {
		private onDownloadProcessListener listener;

		public DownloadProcessHandler(onDownloadProcessListener listener) {
			this.listener = listener;
		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			int downloaderId = unPack_MSG_id(msg); // download task Id
			int segId = -1;
			int fileLen = -1;
			int totalDoneSize = -1;

			switch (msg.what) {

			case FileDownloader.MSG_RESUMESESSION:
				int resumeStatusCode = unPack_MSG_RESUMESESSION_resumeStatusCode(msg);
				fileLen = unPack_MSG_RESUMESESSION_fileLength(msg);
				totalDoneSize = unPack_MSG_RESUMESESSION_totalDoneSize(msg);
				listener.onResumeSession(downloaderId, resumeStatusCode,
						fileLen, totalDoneSize);
				break;

			case FileDownloader.MSG_INPROGRESS:
				segId = unPack_MSG_INPROGRESS_segId(msg);
				int curPos = unPack_MSG_INPROGRESS_curPos(msg);
				int begPos = unPack_MSG_INPROGRESS_begPos(msg);
				int endPos = unPack_MSG_INPROGRESS_endPos(msg);
				totalDoneSize = unPack_MSG_INPROGRESS_totalDoneSize(msg);
				listener.onInProcess(downloaderId, segId, curPos, begPos,
						endPos, totalDoneSize);
				break;

			case FileDownloader.MSG_ACCOMPLISH:
				listener.onAccomplish(downloaderId);
				break;

			} // end of switch
		}
	}

	// Listener for user to track messages send by FileDownloader
	// To implement user's own callback functions, just to implement Listener
	// Interface of onOperationResultListener
	public interface onOperationResultListener {
		public void onStart(int downloaderId, int fileLength);

		public void onResume(int downloaderId, int fileLength, int totalDoneSize);

		public void onPaused(int downloaderId);

		public void onCanceled(int downloaderId);

		public void onHaveDone(int downloaderId);
	}

	// set listener
	public static void setOnOperationResultListener(
			onOperationResultListener listener) {
		OperationResultHandler handler = new OperationResultHandler(listener);
		handlerGroup.addHandler("DOWNLOAD_CENTER_OnOperationResult", handler);
	}

	// msg handler and dispatch to corresponding callbacks
	private static class OperationResultHandler extends Handler {
		private onOperationResultListener listener;

		public OperationResultHandler(onOperationResultListener listener) {
			this.listener = listener;
		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			int downloaderId = unPack_MSG_id(msg); // download task Id
			int fileLen = -1;
			int totalDoneSize = -1;

			switch (msg.what) {

			case FileDownloader.MSG_START:
				fileLen = unPack_MSG_START_fileLength(msg);
				listener.onStart(downloaderId, fileLen);
				break;

			case FileDownloader.MSG_RESUME:
				fileLen = unPack_MSG_RESUME_fileLength(msg);
				totalDoneSize = unPack_MSG_RESUME_totalDoneSize(msg);
				listener.onResume(downloaderId, fileLen, totalDoneSize);
				break;

			case FileDownloader.MSG_PAUSED:
				listener.onPaused(downloaderId);
				break;

			case FileDownloader.MSG_CANCELED:
				listener.onCanceled(downloaderId);
				break;

			case FileDownloader.MSG_HAVEDONE:
				listener.onHaveDone(downloaderId);
				break;

			} // end of switch
		}
	}

	// Listener for user to track messages send by FileDownloader
	// To implement user's own callback functions, just to implement Listener
	// Interface of onOnSegmentInfoListener
	public interface onOnSegmentInfoListener {
		public void onSegStart(int downloaderId, int segId, int segLength);

		public void onSegAccomplish(int downloaderId, int segId);
	}

	// set listener
	public static void setOnOnSegmentInfoListener(
			onOnSegmentInfoListener listener) {
		OnSegmentInfoHandler handler = new OnSegmentInfoHandler(listener);
		handlerGroup.addHandler("DOWNLOAD_CENTER_OnSegmentInfo", handler);
	}

	// msg handler and dispatch to corresponding callbacks
	private static class OnSegmentInfoHandler extends Handler {
		private onOnSegmentInfoListener listener;

		public OnSegmentInfoHandler(onOnSegmentInfoListener listener) {
			this.listener = listener;
		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			int downloaderId = unPack_MSG_id(msg); // download task Id
			int segId = -1;

			switch (msg.what) {

			case FileDownloader.MSG_SEGSTART:
				segId = unPack_MSG_SEGSTART_segId(msg);
				int segLength = unPack_MSG_SEGSTART_segLength(msg);
				listener.onSegStart(downloaderId, segId, segLength);
				break;

			case FileDownloader.MSG_SEGACCOMPLISH:
				segId = unPack_MSG_SEGACCOMPLISH_segId(msg);
				listener.onSegAccomplish(downloaderId, segId);
				break;

			} // end of switch
		}
	}

	// Listener for user to track messages send by FileDownloader
	// To implement user's own callback functions, just to implement Listener
	// Interface of onOnErrorInfoListener
	public interface onOnErrorInfoListener {
		public void onError(int downloaderId, String errInfo);
	}

	// set listener
	public static void setOnOnErrorInfoListener(onOnErrorInfoListener listener) {
		OnErrorInfoHandler handler = new OnErrorInfoHandler(listener);
		handlerGroup.addHandler("DOWNLOAD_CENTER_OnErrorInfo", handler);
	}

	// msg handler and dispatch to corresponding callbacks
	private static class OnErrorInfoHandler extends Handler {
		private onOnErrorInfoListener listener;

		public OnErrorInfoHandler(onOnErrorInfoListener listener) {
			this.listener = listener;
		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			int downloaderId = unPack_MSG_id(msg); // download task Id

			switch (msg.what) {

			case FileDownloader.MSG_ERROR:
				String errInfo = unPack_MSG_ERROR_errInfo(msg);
				listener.onError(downloaderId, errInfo);
				break;

			} // end of switch
		}
	}

	// get Download Task Id
	public int getTaskId() {
		return id;
	}

	// get file fullPath
	public String getFullPath() {
		String filename = url.substring(url.lastIndexOf("/") + 1);
		return path + filename;
	}

	// get filename
	public String getFilename() {
		return url.substring(url.lastIndexOf("/") + 1);
	}

	// get fileLength; for user
	public int getFileLength() {
		return fileLength;
	}

	// get fileLength; for private use
	private int onGetFileLength() {
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

	private Bundle newBundleInstanceWithId() {
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		return bundle;
	}

	// packs are for packing Message bundle data
	private Bundle pack_MSG_START() {
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("fileLength", fileLength);
		return bundle;
	}

	private Bundle pack_MSG_RESUME() {
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("fileLength", fileLength);
		bundle.putInt("totalDoneSize", totalDownloadSize);
		return bundle;
	}

	private Bundle pack_MSG_RESUMESESSION() {
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("resumeStatusCode", dataStatus);
		bundle.putInt("fileLength", fileLength);
		bundle.putInt("totalDoneSize", totalDownloadSize);
		return bundle;
	}

	private Bundle pack_MSG_SEGSTART(int segId) {
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("segId", segId);
		bundle.putInt("segLength", segmentSize[segId]);
		return bundle;
	}

	private Bundle pack_MSG_ACCOMPLISH() {
		return new Bundle(newBundleInstanceWithId());
	}

	private Bundle pack_MSG_PAUSED() {
		return new Bundle(newBundleInstanceWithId());
	}

	private Bundle pack_MSG_CANCELED() {
		return new Bundle(newBundleInstanceWithId());
	}

	private Bundle pack_MSG_SEGACCOMPLISH(int segId) {
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("segId", segId);
		return bundle;
	}

	private Bundle pack_MSG_ERROR(String err_info) {
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putString("errInfo", err_info);
		return bundle;
	}

	private Bundle pack_MSG_INPROCESS(int segId) {
		Bundle bundle = new Bundle(newBundleInstanceWithId());
		bundle.putInt("segId", segId);
		bundle.putInt("curPos", curPosition[segId]);
		bundle.putInt("begPos", begPosition[segId]);
		bundle.putInt("endPos", endPosition[segId]);
		bundle.putInt("totalDoneSize", totalDownloadSize);
		return bundle;
	}

	private Bundle pack_MSG_HAVEDONE() {
		return new Bundle(newBundleInstanceWithId());
	}

	// //unPacks are for user to get Message information
	private static int unPack_MSG_id(Message msg) {
		return ((Bundle) msg.obj).getInt("id");
	}

	private static int unPack_MSG_START_fileLength(Message msg) {
		return ((Bundle) msg.obj).getInt("fileLength");
	}

	private static int unPack_MSG_RESUME_fileLength(Message msg) {
		return ((Bundle) msg.obj).getInt("fileLength");
	}

	private static int unPack_MSG_RESUME_totalDoneSize(Message msg) {
		return ((Bundle) msg.obj).getInt("totalDoneSize");
	}

	private static int unPack_MSG_RESUMESESSION_resumeStatusCode(Message msg) {
		return ((Bundle) msg.obj).getInt("resumeStatusCode");
	}

	private static int unPack_MSG_RESUMESESSION_fileLength(Message msg) {
		return ((Bundle) msg.obj).getInt("fileLength");
	}

	private static int unPack_MSG_RESUMESESSION_totalDoneSize(Message msg) {
		return ((Bundle) msg.obj).getInt("totalDoneSize");
	}

	private static int unPack_MSG_SEGSTART_segId(Message msg) {
		return ((Bundle) msg.obj).getInt("segId");
	}

	private static int unPack_MSG_SEGSTART_segLength(Message msg) {
		return ((Bundle) msg.obj).getInt("segLength");
	}

	private static String unPack_MSG_ERROR_errInfo(Message msg) {
		return ((Bundle) msg.obj).getString("errInfo");
	}

	private static int unPack_MSG_INPROGRESS_curPos(Message msg) {
		return ((Bundle) msg.obj).getInt("curPos");
	}

	private static int unPack_MSG_INPROGRESS_begPos(Message msg) {
		return ((Bundle) msg.obj).getInt("begPos");
	}

	private static int unPack_MSG_INPROGRESS_endPos(Message msg) {
		return ((Bundle) msg.obj).getInt("endPos");
	}

	private static int unPack_MSG_INPROGRESS_segId(Message msg) {
		return ((Bundle) msg.obj).getInt("segId");
	}

	private static int unPack_MSG_INPROGRESS_totalDoneSize(Message msg) {
		return ((Bundle) msg.obj).getInt("totalDoneSize");
	}

	private static int unPack_MSG_SEGACCOMPLISH_segId(Message msg) {
		return ((Bundle) msg.obj).getInt("segId");
	}

}
