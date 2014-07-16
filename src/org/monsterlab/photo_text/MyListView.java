package org.monsterlab.photo_text;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.monster.timeline.R;
import org.monsterlab.database.DbManager;
import org.monsterlab.timeline.MiddleActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

public class MyListView extends ListView implements OnTouchListener {

	private DbManager dbManager;
	private Cursor cursor;
	private static MyListView mList;
	/**
	 * SimpleAdapter
	 */
	public static SimpleAdapter simpleAdapter;

	/**
	 * SimpleAdapter的内容来源
	 */
	private ArrayList<Map<String, Object>> listItems;

	/**
	 * 向List<Map<String, Object>>中添加的内容表1
	 */
	private Map<String, Object> listItem1;

	/**
	 * 向List<Map<String, Object>>中添加的内容表2
	 */
	private Map<String, Object> listItem2;

	/**
	 * 用于标记List<Map<String, Object>>中添加图片的位置,没图片时好便于添加文本
	 */
	private int tag;

	/**
	 * 记录当前已加载到第几页
	 */
	private int page;

	/**
	 * 每页要加载的图片数量
	 */
	public static final int PAGE_SIZE = 15;

	/**
	 * ImageCache类实例化
	 */
	private ImageLoader imageLoader = null;

	/**
	 * 所有正在或者等待下载的图片任务
	 */
	private static Set<BitmapWorkerTask> taskLoader;

	/**
	 * 线程下载时的中间变量
	 */
	private String mImageUrl;

	/**
	 * ListView布局的高度。
	 */
	private static int listViewHeight;

	/**
	 * MyListView下的直接子布局。
	 */
	private static View listLayout;

	/**
	 * 记录上垂直方向的滚动距离。
	 */
	private static int lastListY = -1;

	/**
	 * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
	 */
	private boolean loadOnce;

	/**
	 * sdcard基本路径
	 */
	private String filePath;

	/**
	 * 存储图片名称的数组
	 */
	private String[] imageName;

	/**
	 * 图片路径的数组
	 */
	private String[] photo;

	/**
	 * 文本内容的数组
	 */
	private String[] text;

	/**
	 * 日期String类型的数组
	 */
	private String[] mDate;

	/**
	 * 转换成是日期Date型
	 */
	private String[] date;

	/**
	 * 记录的数量
	 */
	private int size = 0;

	/**
	 * Image对象
	 */
	private Image image = new Image(getContext());

	/**
	 * Text对象
	 */
	private Text mtext = new Text(getContext());

	/**
	 * M_Date对象
	 */
	private M_Date m_ate = new M_Date(getContext());

	/**
	 * 获取图片名称数组
	 * 
	 * @return
	 */
	private String[] getImageName() {
		return image.getImageUrls();
	}

	/**
	 * 获取文本内容数组
	 * 
	 * @return
	 */
	private String[] getTextUrls() {
		return mtext.getTextUrls();
	}

	/**
	 * 获取日期串
	 * 
	 * @return
	 */
	private String[] getDateUrls() {
		return m_ate.getDateUrls();
	}

	/**
	 * 把String类型的日期转化为Date的日期
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String[] getDate() {
		String[] d = new String[size];
		Date date;
		long time;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat  sdf1 = new SimpleDateFormat("yyyy/MM/dd ");
		for (int i = 0; i < size; i++) {
			try {
				date = sdf.parse(mDate[i]);
				time=(long)date.getTime();
				d[i]=sdf1.format(new Date(time));
				System.out.println(d[i]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return d;
	}


	/**
	 * 图片存储路径
	 * 
	 * @return
	 */
	private String[] getImageUrls() {
		// Environment.getExternalStorageState()获取路径是
		// 否成功如获取成功，返回值为MEDIA_MOUNTED
		String[] mPhoto = new String[size];
		String mFilePath;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			filePath = Environment.getExternalStorageDirectory().getPath();
		}
		for (int i = 0; i < size; i++) {
			mFilePath = filePath + "/myImage/" + imageName[i];
			// System.out.println("mFilePath:"+"..."+mFilePath);
			mPhoto[i] = mFilePath;
		}
		return mPhoto;
	}

	/**
	 * 应用安装时显示的图片，以后不显示
	 */
	private void inifun(){
		String[] season=new String[] {"春","夏","秋","冬"};
		String[] date=new String[] {"","","",""};
		int[] id=new int[] {R.drawable.chun,R.drawable.xia,R.drawable.qiu,R.drawable.dong};
		for(int i=0;i<season.length;i++){
			listItem1 = new HashMap<String, Object>();
			listItem2 = new HashMap<String, Object>();
			listItem1.put("season1", season[i]);
			listItem1.put("id1", id[i]);
			listItem1.put("date1", date[i]);
			listItems.add(listItem1);
			++i;
			listItem2.put("season2", season[i]);
			listItem2.put("id2", id[i]);
			listItem2.put("date2", date[i]);
			listItems.add(listItem2);
		}
		simpleAdapter = new SimpleAdapter(getContext(), listItems,
				R.layout.linearlayout, new String[] { "season1", "id1",
						"date1", "season2", "id2", "date2" }, new int[] {
						R.id.text1, R.id.photo1, R.id.date1, R.id.text2,
						R.id.photo2, R.id.date2 });
		mList.setAdapter(MyListView.simpleAdapter);
	}

	/**
	 * MyListView构造函数
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param attrs
	 */

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mList = (MyListView) findViewById(R.id.mylist);
		dbManager=new DbManager(getContext());
		cursor=dbManager.getDb().rawQuery("select * from photo_text;",null);
		size = image.ptList.size();
		// System.out.println("size"+"..."+size);
		tag = 0;
		filePath = null;
		imageLoader = ImageLoader.getInstance();
		taskLoader = new HashSet<BitmapWorkerTask>();
		imageName = getImageName();
		text = getTextUrls();
		mDate = getDateUrls();
		date = getDate();
		photo = getImageUrls();
		listItems = new ArrayList<Map<String, Object>>();
		setOnTouchListener(MyListView.this);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public MyListView(Context context) {
		super(context);
	}

	/**
	 * 在Handler中进行图片可见性检查的判断，以及加载更多图片的操作。
	 */
	private static Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			MyListView list = (MyListView) msg.obj;
			listLayout = mList.getChildAt(0);
			if (listLayout == null) {
				mList.inifun();
			}
			int listScrollY = list.getScrollY();
			// 如果当前的滚动位置和上次相同，表示已停止滚动
			if (listScrollY == lastListY) {
				// 当滚动的最底部，并且当前没有正在下载的任务时，开始加载下一页的图片
				if (listViewHeight + listScrollY >= listLayout.getHeight()
						&& taskLoader.isEmpty()) {
					list.loadMoreImages();
				}
				// list.checkVisibility();
			} else {
				lastListY = listScrollY;
				Message message = new Message();
				message.obj = list;
				// 5毫秒后再次对滚动位置进行判断
				handler.sendMessageDelayed(message, 5);
			}
		};

	};

	/**
	 * 进行一些关键性的初始化操作，获取ListView的高度，在这里开始加载第一页的图片。
	 */
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed && !loadOnce) {
			loadMoreImages();
			listViewHeight = getHeight();
			loadOnce = true;
			if(cursor.getCount()==0){
			listLayout = mList.getChildAt(0);
			if (listLayout == null) {
				mList.inifun();
			}
			}
			// System.out.println(listLayout+"onLayout被调用");
		}
	}

	/**
	 * 监听用户的触屏事件，如果用户手指离开屏幕则开始进行滚动检测。
	 */
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Message message = new Message();
			message.obj = this;
			handler.sendMessageDelayed(message, 5);
		}
		return false;
	}

	/**
	 * 开始加载下一页的图片，每张图片都会开启一个异步线程去下载。
	 */
	public void loadMoreImages() {
		if (hasSDCard()) {
			int startIndex = page * PAGE_SIZE;
			int endIndex = page * PAGE_SIZE + PAGE_SIZE;
			if (startIndex < photo.length) {
				Toast.makeText(getContext(), "正在加载...", Toast.LENGTH_SHORT)
						.show();
				if (endIndex > photo.length) {
					endIndex = photo.length;
				}
				for (int i = 0; i < photo.length; i++) {
					BitmapWorkerTask task = new BitmapWorkerTask();
					taskLoader.add(task);
					task.execute(photo[i]);
				}
				page++;
			} else {
				Toast.makeText(getContext(), "已没有更多图片", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(getContext(), "未发现SD卡", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 判断手机是否有SD卡。
	 * 
	 * @return 有SD卡返回true，没有返回false。
	 */
	private boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	// /**
	// * 遍历imageViewList中的每张图片，对图片的 &&存在性&& 进行检查，如果图片已经离开屏幕可见范围，则将图片替换成一张空图。
	// */
	// public void checkVisibility() {
	// for (int i = 0; i < imageViewList.size(); i++) {
	// ImageView imageView = imageViewList.get(i);
	// int borderTop = (Integer) imageView.getTag(R.string.border_top);
	// int borderBottom = (Integer) imageView
	// .getTag(R.string.border_bottom);
	// if (borderBottom > getScrollY()
	// && borderTop < getScrollY() + listViewHeight) {
	// String imageUrl = (String) listView.getTag(R.string.image_url);
	// Bitmap bitmap = imageCache.getBitmapFromMemoryCache(imageUrl);
	// if (bitmap != null) {
	// imageView.setImageBitmap(bitmap);
	// } else {
	// BitmapWorkerTask task = new BitmapWorkerTask();
	// task.execute(imageUrl);
	// }
	// } else {
	// imageView.setImageResource(R.drawable.empty_photo);
	// }
	// }
	// }

	/**
	 * 异步线程的共享资源（临界资源）
	 * 
	 * @param bitmap
	 */
	public synchronized void fun(Bitmap bitmap, String mImagePath) {
		listItem1 = new HashMap<String, Object>();
		listItem2 = new HashMap<String, Object>();
		System.out.println("getContext" + "..." + getContext());
		if (tag < photo.length) {
			if (tag % 2 == 0) {
				System.out.println("这是1" + "...");
				if (bitmap != null) {
					System.out.println("photo1.0" + "..." + bitmap);
					listItem1.put("photo1", bitmap);
					System.out.println("text1.0[tag]" + "..." + text[tag]);
					listItem1.put("text1", text[tag]);
					System.out.println("date1.0[tag]" + "..." + date[tag]);
					listItem1.put("date1", date[tag]);
					listItem2.put("imageUrl", mImagePath);
				} else {
					System.out.println("photo1.0" + "..." + bitmap);
					listItem1.put("photo1",R.drawable.shuzhi1);
					System.out.println("text1.1[tag]" + "..." + text[tag]);
					listItem1.put("text1", text[tag]);
					System.out.println("date1.1[tag]" + "..." + date[tag]);
					listItem1.put("date1", date[tag]);
					listItem2.put("imageUrl", mImagePath);
				}
				listItems.add(listItem1);
			} else {
				System.out.println("这是2" + "...");
				if (bitmap != null) {
					System.out.println("photo2.0" + "..." + bitmap);
					listItem2.put("photo2", bitmap);
					System.out.println("text2.0[tag]" + "..." + text[tag]);
					listItem2.put("text2", text[tag]);
					System.out.println("date2.0[tag]" + "..." + date[tag]);
					listItem2.put("date2", date[tag]);
					listItem2.put("imageUrl", mImagePath);
				} else {
					System.out.println("photo1.0" + "..." + bitmap);
					listItem2.put("photo2", R.drawable.shuzhi);
					System.out.println("text2.1[tag]" + "..." + text[tag]);
					listItem2.put("text2", text[tag]);
					System.out.println("date2.1[tag]" + "..." + date[tag]);
					listItem2.put("date2", date[tag]);
					listItem2.put("imageUrl", mImagePath);
				}
				listItems.add(listItem2);
			}
			mList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(position<photo.length){
					Intent intent = new Intent(getContext(),
							MiddleActivity.class);
					intent.putExtra("image_path", photo[position]);
					intent.putExtra("text", text[position]);
					getContext().startActivity(intent);
					}
				}
			});
			++tag;
			System.out.println("这是tag" + "..." + tag);
		}
		if (tag == photo.length) {
			simpleAdapter = new SimpleAdapter(getContext(), listItems,
					R.layout.linearlayout, new String[] { "text1", "photo1",
							"date1", "text2", "photo2", "date2" }, new int[] {
							R.id.text1, R.id.photo1, R.id.date1, R.id.text2,
							R.id.photo2, R.id.date2 });
			simpleAdapter.setViewBinder(new ViewBinder() {

				@Override
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					if ((view instanceof ImageView) & (data instanceof Bitmap)) {
						ImageView iv = (ImageView) view;
						Bitmap bm = (Bitmap) data;
						iv.setImageBitmap(bm);
						return true;
					}
					return false;

				}

			});
			mList.setAdapter(MyListView.simpleAdapter);
		}
	}

	/**
	 * 异步加载图片的任务
	 * 
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		public BitmapWorkerTask() {
		}

		// 在后台加载图片。
		@Override
		protected Bitmap doInBackground(String... params) {
			mImageUrl = params[0];
			Bitmap imageBitmap = imageLoader
					.getBitmapFromMemoryCache(mImageUrl);
			if (imageBitmap == null) {
				imageBitmap = loadImage(mImageUrl);
			}
			return imageBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			fun(bitmap, mImageUrl);
		}

		/**
		 * 根据传入的URL，对图片进行加载.
		 * 
		 * @param imageUrl
		 *            图片的URL地址
		 * @return 加载到内存的图片。
		 */
		private Bitmap loadImage(String imageUrl) {
			File imageFile = new File(imageUrl);
			if (!imageFile.exists()) {
				System.out.println("File imageFile = new File(imageUrl)不存在");
			}
			if (imageUrl != filePath + "/myImage/" + "") {
				Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
						imageFile.getPath(), 100, 100);
				if (bitmap != null) {
					imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
					return bitmap;
				}
			}
			return null;
		}

	}

}
