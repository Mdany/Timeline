package org.monsterlab.timeline;

import java.util.Calendar;
import java.util.Locale;

import org.monster.timeline.R;
import org.monsterlab.database.DbManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 写文本的界面
 *
 */
public class WTextActivity extends Activity {
	private static final int MAX_COUNT = 60;
	private static final String TAG = "text";
	private String eText = null;
	private String date = null;

	private ImageView back, send;
	private EditText edit;
	private TextView mTextView = null;
	private DbManager db = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.write_text);
		back = (ImageView) findViewById(R.id.back);
		send = (ImageView) findViewById(R.id.send);

		edit = (EditText) findViewById(R.id.edit1);
		edit.setSingleLine(false);
		edit.addTextChangedListener(textWatcher);
		edit.setSelection(edit.length());
		mTextView = (TextView) findViewById(R.id.count);

		db = new DbManager(WTextActivity.this);

		setLeftCount();

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WTextActivity.this,
						StatusActivity.class);
				startActivity(intent);
				finish();
			}

		});

		send.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				date = new DateFormat().format("yyyyMMdd",
						Calendar.getInstance(Locale.CHINA)).toString();
				eText = edit.getText().toString();
				if (!eText.equals("")) {
					Log.i(TAG, "000sssss");
					db.insertData(date, null, eText);
					Intent intent = new Intent(WTextActivity.this,
							StatusActivity.class);
					startActivity(intent);
					finish();
				}
				else{
					Toast.makeText(WTextActivity.this, "文本不能为空！", Toast.LENGTH_SHORT)
					.show();
				}
			}
		});

	}

	private TextWatcher textWatcher = new TextWatcher() {

		private int editStart;

		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = edit.getSelectionStart();
			editEnd = edit.getSelectionEnd();

			// 先去掉监听器，否则会出现栈溢出
			edit.removeTextChangedListener(textWatcher);

			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1

			while (calculateLength(s.toString()) > MAX_COUNT) { // //
																// 当输入字符个数超过限制的大小时，进行截断操作

				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			// mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了

			edit.setSelection(editStart);

			// 恢复监听器
			edit.addTextChangedListener(textWatcher);

			setLeftCount();
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};

	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}

	private void setLeftCount() {
		mTextView.setText("还可以输入"
				+ String.valueOf((MAX_COUNT - getInputCount())) + "个字符！");
	}

	private long getInputCount() {
		return calculateLength(edit.getText().toString());
	}
}
