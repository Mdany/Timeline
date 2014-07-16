package org.monsterlab.photo_text;

import java.util.ArrayList;
import java.util.List;

import org.monsterlab.database.DbManager;
import org.monsterlab.database.Photo_Text;

import android.content.Context;

public class M_Date {
	private DbManager mdb;
	public List<Photo_Text> ptList;
	private String[] dateUrls;
	private ArrayList<String> temp=new ArrayList<String>();
	
	/**
	 * 初始化Date对象时初始化dateUrls
	 */
	public M_Date(Context context){
		mdb=new DbManager(context);
		ptList=mdb.query();
		for(int i=0;i<ptList.size();i++)
		{
			temp.add(ptList.get(i).getDate());
		}
	}
	
	public String[] getDateUrls(){
		dateUrls=new String[temp.size()];
		return temp.toArray(dateUrls);
	}

}
