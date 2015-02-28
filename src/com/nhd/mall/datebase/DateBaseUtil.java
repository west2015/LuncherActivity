package com.nhd.mall.datebase;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.nhd.mall.R;

public class DateBaseUtil {
	private static SQLiteDatabase database;
	
	public static SQLiteDatabase getInstance(Context context){
		if (database == null) {
			DatabaseHelper helper = new DatabaseHelper(context, context.getResources().getString(R.string.db_name));
			database = helper.getWritableDatabase();
		}
		return database;
	}
}
