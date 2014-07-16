package org.monsterlab.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper的子类，实现创建数据库，更新数据库，关闭数据库的功能
 *
 */
public class DbHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "mydb.db";
	private static int DB_VERSION = 1;
	private String sql = null;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	/**
	 * 构造函数
	 * @context
	 */
	public DbHelper() {
		super(null, DB_NAME, null, DB_VERSION);
	}
	/**
 	* 构造函数
 	*/
	@Override
	public void onCreate(SQLiteDatabase db) {
		sql = "create table IF NOT EXISTS photo_text(_id integer primary key  autoincrement ,"
				+ "date varchar(255),"
				+ "photo varchar(255),"
				+ "text varchar(255));";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		super.close();
	}

}
