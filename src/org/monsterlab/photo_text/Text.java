package org.monsterlab.photo_text;

import java.util.ArrayList;
import java.util.List;
import org.monsterlab.database.DbManager;
import org.monsterlab.database.Photo_Text;

import android.content.Context;

/**
 * 获取文本内容数组textUrl的类
 *
 */
public class Text {
	private DbManager mdb;
	public List<Photo_Text> ptList;
	private String[] textUrls;
	private ArrayList<String> temp=new ArrayList<String>();
	/**
	 * 初始化Text对象初始化
	 */
	public Text(Context context)
	{
		mdb=new DbManager(context);
		ptList=mdb.query();
		for(int i=0;i<ptList.size();i++)
		{
			if (ptList.get(i).getText() != null) {
				temp.add(ptList.get(i).getText());
			} else {
				temp.add("");
			}
		}
	}
	
	public String[] getTextUrls()
	{
		textUrls=new String[temp.size()];
		return temp.toArray(textUrls);
	} 

}
