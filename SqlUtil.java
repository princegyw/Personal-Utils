package com.example.wilson.contentproviderdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SqlUtil {
	
	public static void loadAndExecuteStatementsFromAssetFile(Context context, SQLiteDatabase database, String assetFilename) throws IOException {
		List<String> statements = getSqlStatementsFromAssetFile(context, assetFilename);
		for (String statement : statements) {
            Log.d("gyw_SqlUtil", "SqlUtil::loadAndExecuteStatementsFromAssetFile statementLen = " + statement.length());
            Log.d("gyw_SqlUtil", "SqlUtil::loadAndExecuteStatementsFromAssetFile statement = " + statement + "endl");
			database.execSQL(statement);
		}
	}
	
	public static List<String> getSqlStatementsFromAssetFile(Context context, String assetFilename) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		String raw = getStringFromAssetFile(context, assetFilename);
		for (String statement : raw.split(";")) {
			if (statement != null && !statement.isEmpty() && !statement.trim().isEmpty()) {
				list.add(statement);
			}
		}
		return list;
	}
	
	private static String getStringFromAssetFile(Context context, String assetFilename) throws IOException {
		if (context == null) {
			throw new NullPointerException("context is null");
		}
		InputStream is = context.getAssets().open(assetFilename);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}
}
