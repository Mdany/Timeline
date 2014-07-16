package org.monsterlab.photo_text;

import java.util.ArrayList;
import java.util.List;

import org.monsterlab.database.DbManager;
import org.monsterlab.database.Photo_Text;

import android.content.Context;

/**
 * 获得图片的名称数组imageUrls的类
 *
 */
public class Image {
	private DbManager mdb;
	public List<Photo_Text> ptList;
	private String[] imageUrls;
	private ArrayList<String> temp=new ArrayList<String>();
	
	/**
	 * 初始化Image对象时初始化imageUrls
	 */
	public Image(Context context)
	{
		mdb=new DbManager(context);
		ptList=mdb.query();
//		System.out.println("ptList.get(0).getPhoto()"+"..."+ptList.get(0).getPhoto());
		for(int i=0;i<this.ptList.size();i++)
		{
//			System.out.println("ptList.get(i).getPhoto()"+"..."+ptList.get(i).getPhoto());
			if (ptList.get(i).getPhoto() != null) {
				temp.add(ptList.get(i).getPhoto());
			} else {
				temp.add("");
			}
		}
	}
	
	public String[] getImageUrls()
	{
		imageUrls=new String[temp.size()];
		return temp.toArray(imageUrls);
	}
	
}
