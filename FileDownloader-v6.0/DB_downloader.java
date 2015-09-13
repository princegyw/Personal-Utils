package painkiller.multithread_download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB_downloader {
	private DBHelper_downloader dbHelper;
	
	public DB_downloader(Context context) {
		dbHelper = new DBHelper_downloader(context);
	}
	
	public void saveInfos(List<DownloadInfo> infos)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		for (DownloadInfo info : infos) {
			String sql = "insert into download_info(task_id, thread_id, start_pos,"
					                 + "end_pos, cur_pos, seg_size, complete_size, is_done, url)"
					                 + "values(?,?,?,?,?,?,?,?,?)";
			Object[] bindArgs = {info.task_id, info.thread_id, info.start_pos, info.end_pos,
								 info.cur_pos, info.seg_size, info.complete_size, info.is_done, info.url};
			db.execSQL(sql, bindArgs);
		}
	}
	
	public void saveInfo(DownloadInfo info)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "insert into download_info(task_id, thread_id, start_pos,"
                + "end_pos, cur_pos, seg_size, complete_size, is_done, url)"
                + "values(?,?,?,?,?,?,?,?,?)";
		Object[] bindArgs = {info.task_id, info.thread_id, info.start_pos, info.end_pos,
							 info.cur_pos, info.seg_size, info.complete_size, info.is_done, info.url};
		db.execSQL(sql, bindArgs);
	}
	
	public DownloadInfo getAllInfoByUrl(String url)
	{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from download_info where url=? and thread_id = -1";
		Cursor cursor = db.rawQuery(sql, new String[]{url});
		cursor.moveToFirst();
		DownloadInfo info = null;
		if (cursor.getCount() != 0) {
			info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), 
                    cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7), cursor.getString(8));
		}
		cursor.close();
		return info;
	}
	
	public List<DownloadInfo> getSegInfosByUrl(String url)
	{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<DownloadInfo> list = new ArrayList<>();
		String sql = "select * from download_info where url=? and thread_id <> -1";
		Cursor cursor = db.rawQuery(sql, new String[]{url});
		
		while (cursor.moveToNext())
		{
			DownloadInfo info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), 
                    cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7), cursor.getString(8));
			list.add(info);
		}
		
		cursor.close();
		return list;
	}
	
	public void updateDownloadInfo(DownloadInfo info)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		//update Seg info
		String sql = "update download_info set cur_pos = ?, complete_size = ?, is_done = ? "
				   + "where url = ? and thread_id = ?";
		Object[] bindArgs = {info.cur_pos, info.complete_size, info.is_done, info.url, info.thread_id};
		db.execSQL(sql, bindArgs);
	}
	
	public void removeDownloadInfo(DownloadInfo info)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "delete from download_info where url = ?";
		db.execSQL(sql, new String[]{info.url});
	}
}
