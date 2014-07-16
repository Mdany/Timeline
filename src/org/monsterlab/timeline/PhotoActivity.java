package org.monsterlab.timeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import org.monster.timeline.R;
import org.monsterlab.database.DbManager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SdCardPath")
/**
 * 照相界面，显示图片的一个缩略图
 *
 */
public class PhotoActivity extends Activity {

	private static final String TAG = "photo";
	private static final int MAX_COUNT = 40;
	protected static final int REQUEST_CODE = 0;
	private String editText = null;
	private String name = null;
	private String date = null;

	private ImageView back, send;
	private ImageView imageView;
	private EditText edit1;
	private TextView mTextView = null;

	private DbManager db = null;
	private Bitmap bitmap = null;
	private Intent data = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.take_photo);
		back = (ImageView) findViewById(R.id.back_bt);
		send = (ImageView) findViewById(R.id.send_bt);
		edit1 = (EditText) findViewById(R.id.edit);
		edit1.setSingleLine(false);
		edit1.addTextChangedListener(mTextWatcher);
		edit1.setSelection(edit1.length());
		mTextView = (TextView) findViewById(R.id.count);
		setLeftCount();// 计算 提示的限制字数

		db = new DbManager(PhotoActivity.this);

		imageView = (ImageView) findViewById(R.id.touch);
		imageView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				Uri imageUri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), "bufferPhoto.jpg"));
				// 指定照片保存路径（SD卡），workupload.jpg为一个临时文件，每次拍照后这个图片都会被替换
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(cameraIntent, REQUEST_CODE);
				// 打开相机，照片并且保存到bufferPhoto.jpg缓存临时文件，
				// 照片不缩小
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PhotoActivity.this,
						StatusActivity.class);
				startActivity(intent);
				finish();
			}
		});

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editText = edit1.getText().toString();
				if (bitmap != null) {
					Log.i(TAG, "sent" + " ... " + name + "..." + editText);
					savePhotoToLocal(data, bitmap);// save photo to sdcard
					db.insertData(date, name, editText);
					Intent intent = new Intent(PhotoActivity.this,
							StatusActivity.class);
					startActivity(intent);
					finish();
				}
				else{
					Toast.makeText(PhotoActivity.this, "图片不能为空！", Toast.LENGTH_SHORT)
					.show();
				}
			}
		});

	}
	
	/**
	 * 获取缩小一定比例的bitmap图片并显示
	 * 
	 * @requestCode
	 * @resultCode
	 * @data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap btm;
		if (resultCode != Activity.RESULT_OK) {// result is not correct
			return;
		} else {
			if (requestCode == REQUEST_CODE) {
				// 将保存在本地的图片取出并缩小后显示在界面上
				Bitmap camorabitmap = BitmapFactory.decodeFile(Environment
						.getExternalStorageDirectory() + "/bufferPhoto.jpg");
				this.bitmap=camorabitmap;
				if (null != camorabitmap) {
					// 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
					int scale = ImageThumbnail.reckonThumbnail(
							camorabitmap.getWidth(), camorabitmap.getHeight(),
							240, 360);
					// 获取比例
					btm = ImageThumbnail.tailorThumbnail(camorabitmap,
							camorabitmap.getWidth() / scale,
							camorabitmap.getHeight() / scale);
					/*
					 * 这里后两个参数，若scale为1则参数不变，即为获得的尺寸，若scale大于1
					 * ，参数大小变为设定的尺寸大小。返回一个剪裁完的图片
					 */
					// 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
					// camorabitmap.recycle();
					// 将处理过的图片显示在界面上，并保存到本地
					// imageView.setVisibility(View.VISIBLE);
					this.data = data;
					imageView.setImageBitmap(btm);
				}
			}
		}
	}

	/**
	 * 保存图片到本地
	 * 
	 * @param data
	 * @param bMap
	 */
	@SuppressWarnings("static-access")
	public void savePhotoToLocal(Intent data, Bitmap bMap) {
		// 如果文件夹不存在则创建文件夹，并将bitmap图像文件保存
		String sdStatus = Environment.getExternalStorageState();
		FileOutputStream fileOut = null;
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			Log.i("TestFile", "SD card is not avaiable/writeable right now.");
			return;
		}
		this.date = new DateFormat().format("yyyyMMdd",
				Calendar.getInstance(Locale.CHINA)).toString();
		this.name = new DateFormat().format("yyyyMMdd_hhmmss",
				Calendar.getInstance(Locale.CHINA))
				+ ".jpg";
		Toast.makeText(this, name, Toast.LENGTH_LONG).show();
		File file = new File("/sdcard/myImage/");
		if (!file.exists())
			file.mkdirs();// 创建文件夹
		String fileName = "/sdcard/myImage/" + name;
		try {
			// 将bitmap转为jpg文件保存
			fileOut = new FileOutputStream(fileName);
			bMap.compress(CompressFormat.JPEG, 100, fileOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOut.flush();
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		private int editStart;

		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = edit1.getSelectionStart();
			editEnd = edit1.getSelectionEnd();

			// 先去掉监听器，否则会出现栈溢出
			edit1.removeTextChangedListener(mTextWatcher);

			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1

			while (calculateLength(s.toString()) > MAX_COUNT) { // //
																// 当输入字符个数超过限制的大小时，进行截断操作

				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			// mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了

			edit1.setSelection(editStart);

			// 恢复监听器
			edit1.addTextChangedListener(mTextWatcher);

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
		return calculateLength(edit1.getText().toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

/**
 * 
 * 获取缩略图的类
 *
 */
class ImageThumbnail {
	
	/**
	 * 获取缩小比例
	 * @param oldWidth
	 * @param oldHeight
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static int reckonThumbnail(int oldWidth, int oldHeight,
			int newWidth, int newHeight) {// 两个if语句判断存储的图片是否大于设定的大小，不满足则记录下存储图与规定
											// 大小之间的比例
		if ((oldHeight > newHeight && oldWidth > newWidth)
				|| (oldHeight <= newHeight && oldWidth > newWidth)) {
			int scale1 = (int) (oldWidth / (float) newWidth);
			if (scale1 <= 1)
				scale1 = 1;
			return scale1;
		} else if (oldHeight > newHeight && oldWidth <= newWidth) {
			int scale2 = (int) (oldHeight / (float) newHeight);
			if (scale2 <= 1)
				scale2 = 1;
			return scale2;
		}
		return 1;
	}

	/**
	 * 剪取缩略图
	 * 
	 * @param bMap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap tailorThumbnail(Bitmap bMap, int width, int height) {
		int bMapWidth = bMap.getWidth();
		int bMapHeght = bMap.getHeight();
		Matrix matrix = new Matrix();// 创建矩阵类对象
		matrix.postScale((float) width / bMapWidth, (float) height / bMapHeght);
		// Postconcats the matrix with the specified scale. M' = S(sx, sy) * M
		return Bitmap.createBitmap(bMap, 0, 0, bMapWidth, bMapHeght, matrix,
				true);
		/*
		 * public static Bitmap createBitmap (Bitmap source, int x, int y, int
		 * width, int height, Matrix m, boolean filter)
		 * 从原始位图剪切图像，这是一种高级的方式。可以用Matrix(矩阵)来实现旋转等高级方式截图 参数说明： 　　Bitmap
		 * source：要从中截图的原始位图 　　int x:起始x坐标 　　int y：起始y坐标 int width：要截的图的宽度 int
		 * height：要截的图的宽度 Bitmap.Config config：一个枚举类型的配置，可以定义截到的新位图的质量
		 * 返回值：返回一个剪切好的Bitmap
		 */
	}
}
