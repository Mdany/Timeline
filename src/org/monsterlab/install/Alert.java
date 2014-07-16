package org.monsterlab.install;

import org.monster.timeline.R;
import org.monsterlab.timeline.StatusActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;

public class Alert extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		final MediaPlayer mediaPlayer01;
		/**
		 * 使用MediaPlayer播放音频文件
		 */
		mediaPlayer01 = MediaPlayer.create(getBaseContext(), R.raw.ww);
		/**
		 * ww为音频文件
		 */
		Runnable rmp = new Runnable() {
			public void run() {
				mediaPlayer01.start();
			}
		};
		final Thread tmp = new Thread(rmp);
		/**
		 * Runnable是Thread的接口,接口可以实现多继承
		 */
		tmp.run();

		super.onCreate(savedInstanceState);
		Dialog dialog = new AlertDialog.Builder(Alert.this)
		/**
		 * AlertDialog的各种方法
		 */
		.setIcon(R.drawable.clock).setTitle("开始记录你的一天吧!!")
				.setMessage("不想的话请推迟!!!")

				.setPositiveButton("开始记录",
				/**
				 * 添加“yes”按钮
				 */
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = new Intent(Alert.this,
								StatusActivity.class);
						startActivity(intent);
						finish();
					}
				})

				.setNegativeButton("推迟",
				/**
				 * 添加按钮
				 */
				new DialogInterface.OnClickListener() {
					/**
					 * 为按钮“推迟”添加触发事件
					 */

					public void onClick(DialogInterface dialog, int which) {
						final String[] ary = getResources().getStringArray(
								R.array.time);
						/**
						 * 获取下拉列表,getResources()这个方法就可以获取存在系统的资源
						 * getResources().getStringArray
						 * (R.array.time)获取xml文件里的资源
						 */
						new AlertDialog.Builder(Alert.this)
								.setItems(ary,
										new DialogInterface.OnClickListener() {

											/**
											 * 下拉选项，调用values文件夹中的item
											 */
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												Intent intent = new Intent(
														Alert.this, Call.class);
												/**
												 * intent用来传递数据，Alert调用Call
												 */
												PendingIntent pi = PendingIntent
														.getBroadcast(
																Alert.this, 1,
																intent, 0);
												/**
												 * 创建PendingIntent对象封装Intent
												 */
												AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
												/**
												 * 设定一个时间，然后在该时间到来时，
												 * AlarmManager为我们广播一个我们设定的Intent
												 */
												switch (which) {
												case 0:
													am.set(AlarmManager.RTC_WAKEUP,
															System.currentTimeMillis() + 600 * 1000,
															pi);
													break;
												case 1:
													am.set(AlarmManager.RTC_WAKEUP,
															System.currentTimeMillis() + 1800 * 1000,
															pi);
													break;
												case 2:
													am.set(AlarmManager.RTC_WAKEUP,
															System.currentTimeMillis() + 3600 * 1000,
															pi);													
													break;		
												}
												SharedPreferences time1Share = getPreferences(0);
												SharedPreferences.Editor editor = time1Share
														.edit();
												editor.commit();
												finish();
											}

										}).create().show();
						/**
						 * 要创建一个AlertDialog，就要用到AlertDialog.Builder中的create()方法
						 */
					}
				}).create();
		dialog.show();
	}

}
