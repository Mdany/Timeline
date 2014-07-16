package org.monsterlab.install;

import java.util.Calendar;

import org.monster.timeline.R;
import org.monsterlab.timeline.StatusActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class InstallActivity extends Activity {

	TextView setTime1;
	/**
	 * 设置时间的内容
	 */
	Button mButton1;
	/**
	 * 设置时间按钮
	 */
	Button mButton2;
	/**
	 * 删除设置时间按钮
	 */
	String time1String = null;
	/**
	 * 初始设置为空
	 */
	String defalutString = "无设置";
	/**
	 * 显示的默认值为无设置
	 */
	AlertDialog builder = null;
	Calendar c = Calendar.getInstance();

	/**
	 * 获取日历
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab1);
		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		/**
		 * 取得活动的Preferences对象
		 */
		time1String = settings.getString("TIME1", defalutString);
		/**
		 * 为time1String赋默认值
		 */
		InitButton1();
		InitButton2();
		/**
		 * 设置和删除按钮的设定和事件
		 */
		setTime1.setText(time1String);

	}

	public void InitButton1()

	{

		setTime1 = (TextView) findViewById(R.id.setTime1);
		mButton1 = (Button) findViewById(R.id.mButton1);
		mButton1.setOnClickListener(new View.OnClickListener() {
			/**
			 * 为按钮设置添加事件
			 */
			public void onClick(View v) {
				c.setTimeInMillis(System.currentTimeMillis());
				/**
				 * 当前系统时间
				 */
				int mHour = c.get(Calendar.HOUR_OF_DAY);
				/**
				 * 获取小时
				 */
				int mMinute = c.get(Calendar.MINUTE);
				/**
				 * 获取分钟
				 */

				new TimePickerDialog(InstallActivity.this,
				/**
				 * 调用设置时间对话框
				 */
				new TimePickerDialog.OnTimeSetListener() {
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						c.setTimeInMillis(System.currentTimeMillis());
						c.set(Calendar.HOUR_OF_DAY, hourOfDay);
						c.set(Calendar.MINUTE, minute);
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);

						Intent intent = new Intent(InstallActivity.this,
								Call.class);
						/**
						 * intent用来传递数据，MainActivity调用Call
						 */
						PendingIntent sender = PendingIntent.getBroadcast(
								InstallActivity.this, 0, intent,
								PendingIntent.FLAG_UPDATE_CURRENT);
						/**
						 * 创建PendingIntent对象封装Intent
						 */
						AlarmManager am;
						am = (AlarmManager) getSystemService(ALARM_SERVICE);
						/**
						 * 设定一个时间，然后在该时间到来时，AlarmManager为我们广播一个我们设定的Intent
						 */
						am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
								sender);
						/**
						 * 设定时间
						 */
						String tmpS = format(hourOfDay) + ":" + format(minute);
						/**
						 * 定义时间
						 */
						setTime1.setText(tmpS);
						SharedPreferences time1Share = getPreferences(0);
						SharedPreferences.Editor editor = time1Share.edit();
						editor.putString("TIME1", tmpS);
						editor.commit();
						/**
						 * SharedPreferences保存数据，并提交
						 */

						Toast.makeText(InstallActivity.this, "设置时间为" + tmpS,
								Toast.LENGTH_SHORT).show();
						/**
						 * 时间显示位置效果
						 */
					}
				}, mHour, mMinute, true).show();
			}
		});
	}

	public void InitButton2() {
		mButton2 = (Button) findViewById(R.id.mButton2);
		mButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(InstallActivity.this, Call.class);
				PendingIntent sender = PendingIntent.getBroadcast(
						InstallActivity.this, 0, intent, 0);
				AlarmManager am;
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(sender);
				/**
				 * 取消已经注册的与参数匹配的定时器
				 */
				Toast.makeText(InstallActivity.this, "时间删除", Toast.LENGTH_SHORT)
						.show();
				setTime1.setText("无设置");

				SharedPreferences time1Share = getPreferences(0);
				SharedPreferences.Editor editor = time1Share.edit();
				editor.putString("TIME1", "无设置");
				editor.commit();
			}
		});
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		/**
		 * 返回键事件函数
		 */

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			InstallActivity.this.finish();
		}
		return true;
	}

	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}
}