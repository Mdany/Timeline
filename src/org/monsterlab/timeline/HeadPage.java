package org.monsterlab.timeline;

import org.monster.timeline.R;
import org.monsterlab.database.DbManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;

/**
 * APP运行时的渐变动画效果类
 *
 */
public class HeadPage extends Activity {
	// 声明ImageView对象
	ImageView imageView;
	// ImageView的alpha值
	int image_alpha = 255;
	// Handler对象用来给UI_Thread的MessageQueue发送消息
	Handler imageHandler;
	// 线程是否运行判断变量
	boolean flag = false;

	@SuppressLint("HandlerLeak")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.headpage);
		
		new DbManager(HeadPage.this);
		
		flag = true;
		// 获得ImageView的对象
		imageView = (ImageView) this.findViewById(R.id.headImage);
		// 设置imageView的Alpha值
		imageView.setAlpha(image_alpha);
		// 开启一个线程来让Alpha值递减
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					// 更新Alpha值
					new Thread(new Runnable() {
						@Override
						public void run() {
							while (flag) {
								try {
									Thread.sleep(20);
									// 更新Alpha值
									updateAlpha();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}).start();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		// 接受消息之后更新imageview视图
		imageHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				imageView.setAlpha(image_alpha);
				imageView.invalidate();
			}
		};
	}

	// 更新Alpha
	public void updateAlpha() {
		if (image_alpha - 5 >= 0) {
			image_alpha -= 5;
		} else {
			image_alpha = 0;
			flag = false;
			Intent intent = new Intent(HeadPage.this, StatusActivity.class);
			startActivity(intent);
			finish();
		}
		// 发送需要更新imageview视图的消息-->这里是发给主线程
		imageHandler.sendMessage(imageHandler.obtainMessage());
	}
}