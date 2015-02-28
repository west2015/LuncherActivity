package com.nhd.mall.datebase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.nhd.mall.R;

/**
 * 数据库类 <br>
 * 用于数据库的创建和更新数据库表的操作
 * 
 * @author vendor
 * @version 2012年11月2日 23:43:15
 * @see Class
 * @since JDK1.0
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	/**版本号为1.0 数据库版本为1
	 * */
	private static final int VERSION = 1;
	private Context context;

	/**
	 * 类适配器
	 * 
	 * @param context
	 * @param name
	 *            数据库名称
	 * @param factory
	 * @param version
	 *            版本号
	 */
	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/**
	 * 类适配器
	 * 
	 * @param context
	 * @param name
	 *            版本号
	 */
	public DatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/**
	 * 类适配器
	 * 
	 * @param context
	 * @param name
	 *            数据库名称
	 * @param version
	 *            版本号
	 */
	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        db.execSQL(this.createHistory());
		// 如果返回值为flase说明文件拷贝出错
		if(!initCityDataBase()) return;
		initCityDataBase();

	}

    private String createHistory() {
            StringBuilder sb = new StringBuilder();
            sb.append("create table history(");
            sb.append("name NVARCHAR2(100));"); //
            return sb.toString();
    }

    private final String DATABASE_PATH = "/data/data/com.nhd.mall/databases";
	private final String DATABASE_FILENAME = "city_db";

	public Boolean initCityDataBase() {
		try {

			/*
			 * 获得mobliesys.db文件的绝对路径
			 */
			String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
			/*
			 * 用SD卡的目录路径创建一个文件对象
			 */
			File dir = new File(DATABASE_PATH);
			if (!dir.exists()) {
				/*
				 * 目录没存在，创建此目录
				 */
				dir.mkdir();
			}
			/*
			 * mobliesys.db3文件，则从res\raw目录中复制这个文件到
			 */
			if (!(new File(databaseFilename).exists())) {
				// 获得封装mobliesys.db文件的InputStream对象
				InputStream is = context.getResources().openRawResource(R.raw.city_db);
				/*
				 * 定义文件输出流对象
				 */
				FileOutputStream fos;
				/*
				 * 实例化文件输出流对象
				 */
				fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				// 开始复制mobliesys.db文件
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();

				is.close();

				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(this.createHistory());

        } catch (RuntimeException e) {
            // TODO: handle exception
        }
	}
}
