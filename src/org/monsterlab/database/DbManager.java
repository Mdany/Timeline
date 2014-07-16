package org.monsterlab.database;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库管理类，对数据库进行插入、删除、查询操作
 *
 */
public class DbManager {

	private DbHelper dbHelper = null;
	private SQLiteDatabase db = null;

	/**
	 * 构造函数
	 * @context
	 */
	public DbManager(Context context) {
		dbHelper = new DbHelper(context);
		db = dbHelper.getReadableDatabase();
	}

	/**
	 * 构造函数
	 */
	public DbManager() {
		dbHelper = new DbHelper();
		db = dbHelper.getReadableDatabase();
	}
	
	/**
	 * 数据库插入操作函数
	 * @param date
	 * @param pName
	 * @param tContent
	 */
	public void insertData(String date, String pName, String tContent) {
		// Log.i(TAG, "数据库开始");
		db.execSQL("insert into photo_text(date,photo,text) values(?,?,?);",
				new Object[] { date, pName, tContent });
		// Log.i(TAG, "数据库结束");
	}
	//插入数据函数

	/**
	 * 查询函数，返回这个表的内容
	 * @return
	 */
	public List<Photo_Text> query() {
		ArrayList<Photo_Text> ptList = new ArrayList<Photo_Text>();
		Cursor c = db.rawQuery("SELECT * FROM photo_text;", null);
		
		if(c == null){
			System.out.println("cursor is null");
			
		}
		while (c.moveToNext()) {
			Photo_Text pt = new Photo_Text(c.getInt(c.getColumnIndex("_id")),
					c.getString(c.getColumnIndex("date")), c.getString(c
							.getColumnIndex("photo")), c.getString(c
							.getColumnIndex("text")));
			//pt是一个表的对象并存储到ptList对象中
			ptList.add(pt);
			
		}
		c.close();
		closeDB();
		
//		for(int i = 0; i < ptList.size();i++){
//			Photo_Text pt = ptList.get(i);
//			System.out.println(pt.get_id()+" "+pt.getDate());
//		//打印出ptList的内容
//			
//		}
		return ptList;
	}

	/**
	 * 关闭数据库的函数
	 */
	public void closeDB() {
		db.close();
	}
	public SQLiteDatabase getDb(){
		return db;
	}
	public void setDb(SQLiteDatabase db){
		this.db=db;
	}

}
