package com.example.apkdownloadspeedtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper_downloader extends SQLiteOpenHelper {

	public DBHelper_downloader(Context context) {
		//"download.db" is the name of database
		super(context, "download.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table IF NOT EXISTS download_info(" 
				+ "task_id	integer,"
				+ "thread_id	  integer,"
				+ "start_pos	  integer,"
				+ "end_pos	      integer,"
				+ "cur_pos        integer,"
				+ "seg_size       integer,"
 				+ "complete_size  integer,"
				+ "is_done	      integer,"
				+ "url	          text,"
				+ "primary key(url, thread_id)"
				+ ")"
				);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
