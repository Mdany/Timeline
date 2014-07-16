package org.monsterlab.timeline;

import java.util.Calendar;

import org.monster.timeline.R;
import org.monsterlab.install.InstallActivity;
import org.monsterlab.photo_text.MyListView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 显示主界面，并实现图片显示和按钮的功能
 * 
 */
public class StatusActivity extends Activity {

	private MyListView mList;
	/**
	 * TAG标志
	 */
	private static final String TAG = "Status";

	/**
	 * 初始化Message.what
	 */
	private static final int SHOW_DATAPICK = 0;

	/**
	 * DATE_DIALOG_ID
	 */
	private static final int DATE_DIALOG_ID = 1;

	/**
	 * DatePicker年变量
	 */
	private int mYear;

	/**
	 * DatePicker月变量
	 */
	private int mMonth;

	/**
	 * DatePicker日变量
	 */
	private int mDay;

	/**
	 * 弹出框触发的按钮
	 */
	private ImageView dialog,dialog1;

	/**
	 * 设置按钮
	 */
	private ImageView install;

	/**
	 * 左上角的图片式的按钮
	 */

	/**
	 * AlertDialog对象
	 */
	private AlertDialog alertDialog;

	/**
	 * onCreate函数，你问我？
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		dialog = (ImageView) findViewById(R.id.dialog);
		install = (ImageView) findViewById(R.id.install);

		dialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//dialog.setVisibility(View.GONE);
				//dialog1=(ImageView) findViewById(R.id.dialog1);
				//dialog1.setVisibility(View.VISIBLE);
				showAlterDialog();

			}
		});

		install.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StatusActivity.this,
						InstallActivity.class);
				startActivity(intent);
			}
		});

		mList = (MyListView) findViewById(R.id.mylist);
		mList.setAdapter(MyListView.simpleAdapter);

		mList.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// System.out.println(text[position] + "1");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	/**
	 * 显示dialog弹出框的函数
	 */
	private void showAlterDialog() {
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();

		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.LEFT | Gravity.TOP);
		lp.x = 100; // new x
		lp.y = 600; // new y
		lp.alpha = 0.7f; // 透明度
		window.setAttributes(lp);
		// set dialog layout
		window.setContentView(R.layout.dialog);

		Button TPhoto = (Button) window.findViewById(R.id.camera);
		Log.i(TAG, "onclick");
		TPhoto.setOnClickListener(new OnClickListener() {
			// go to PhotoActivity.java
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StatusActivity.this,
						PhotoActivity.class);
				startActivity(intent);
				finish();
			}
		});
		Button WText = (Button) window.findViewById(R.id.text);
		WText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StatusActivity.this,
						WTextActivity.class);
				startActivity(intent);
				finish();
			}
		});
		Button cancel = (Button) window.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.cancel();
			}
		});
	}

	/**
	 * 设置日期
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
	}

	/**
	 * 处理日期和时间控件的Handler
	 */
	@SuppressLint("HandlerLeak")
	Handler dateandtimeHandler = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			showDialog(DATE_DIALOG_ID);
		}
	};

}
