package org.monsterlab.database;

/**
 * //存储表的类，与表类似，便于对从表中读取的数据的操作，但是这样易成为漏洞
 * 因为函数多事public的
 *
 */
public class Photo_Text {
	private int _id=0;
	private String date="";
	private String photo="";
	private String text="";
	
	public Photo_Text(){}
	
	public Photo_Text(int _id,String date,String photo,String text)
	{
		this._id=_id;
		this.date=date;
		this.photo=photo;
		this.text=text;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
