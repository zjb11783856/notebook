package com.boge.android_0618_notebook.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.boge.android_0618_notebook.R;
import com.boge.android_0618_notebook.utils.MyMediarecorder;
import com.boge.android_0618_notebook.utils.MyMediraPlayer;
import com.boge.android_0618_notebook.utils.MyOpenHelper;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class EditActivity extends Activity implements OnClickListener,
		OnTouchListener {
	private EditText title_edt;
	private EditText content_edt;
	private ImageView img_iv;
	private ImageButton selectByGallery_ib;
	private ImageButton selectByCamera_ib;
	private ImageButton add_noiceBtn;
	private ImageButton recordBtn;
	private ImageButton playBtn;
	private TextView hint_tv;
	private TextView recordtime_tv;
	private Button gallery_btn;// 打开图库按钮
	private Button camera_btn;// 启动相机按钮
	private Button cancel_btn;// 取消按钮
	private MyOpenHelper myHelper;
	private SQLiteDatabase db;
	private int SUBMIT_STATE = 1;
	private int id;
	private View view;
	private Dialog dialog;
	private static final int GET_GALLERY_REQUEST = 1001;
	private static final int GET_CAMERA_REQUEST = 1002;
	private String IMG_NAME = "img.jpg";
	private ImageLoader imageLoader;
	private String pathName = null;
	private MyMediarecorder myRecorder;
	private MyMediraPlayer myPlayer;
	private String path = null;// 录音存储地址
	private BroadcastReceiver receiver;
	private boolean isonce = true;// 设置播放、暂停
	private File imgFile;// 拍照获取图片的路径

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		initView();
		initBroadcastReceiver();
	}

	private void initView() {
		title_edt = (EditText) findViewById(R.id.title);
		content_edt = (EditText) findViewById(R.id.content);
		img_iv = (ImageView) findViewById(R.id.img);
		selectByGallery_ib = (ImageButton) findViewById(R.id.selectByGallery);
		selectByCamera_ib = (ImageButton) findViewById(R.id.selectByCamera);
		add_noiceBtn = (ImageButton) findViewById(R.id.add_noice);
		playBtn = (ImageButton) findViewById(R.id.sound);
		playBtn.setOnClickListener(this);
		selectByGallery_ib.setOnClickListener(this);
		selectByCamera_ib.setOnClickListener(this);
		add_noiceBtn.setOnClickListener(this);
		myHelper = new MyOpenHelper(this, "notedb", null, 1);

		lookOrUpdate();
	}

	/**
	 * 注册广播
	 */
	private void initBroadcastReceiver() {
		receiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("PLAY_END");
		filter.addAction("TIME");
		registerReceiver(receiver, filter);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.selectByGallery:
			Intent localIntent = new Intent(Intent.ACTION_GET_CONTENT);
			localIntent.setType("image/*");
			startActivityForResult(localIntent, GET_GALLERY_REQUEST);
			break;
		case R.id.selectByCamera:
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory(), IMG_NAME)));
			startActivityForResult(cameraIntent, GET_CAMERA_REQUEST);
			break;
		case R.id.add_noice:
			recodeSoundDialog();
			break;

		case R.id.cancel:
			dialog.dismiss();
			break;
		case R.id.sound:
			playSound();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (SUBMIT_STATE == 1) {
				try {
					saveNoteToDB();
				} catch (Exception e) {
					e.printStackTrace();
					return super.onKeyDown(keyCode, event);

				}
			} else {
				updateNoteToDB();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 创建笔记
	 */
	private void saveNoteToDB() {
		String title = title_edt.getText().toString();
		String content = content_edt.getText().toString();

		ContentValues values = new ContentValues();

		if (img_iv.getDrawable() != null) {
			img_iv.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(img_iv.getDrawingCache());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			values.put("img", bos.toByteArray());
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd ");
		String time = sdf.format(new Date());
		db = myHelper.getWritableDatabase();
		values.put("title", title);
		if (content != null) {
			values.put("content", content);
		}
		values.put("time", time);
		if (path != null) {
			values.put("recordpath", path);
		}
		if (!title.equals("")) {
			db.insert("note", null, values);
		}

		db.close();
		EditActivity.this.finish();

	}

	/**
	 * 更新记录
	 */
	private void updateNoteToDB() {
		db = myHelper.getWritableDatabase();
		String title = title_edt.getText().toString();
		String content = content_edt.getText().toString();

		ContentValues values = new ContentValues();
		if (img_iv.getDrawable() != null) {
			img_iv.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(img_iv.getDrawingCache());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			values.put("img", bos.toByteArray());
		}

		values.put("title", title);
		if (content != null) {
			values.put("content", content);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String time = sdf.format(new Date());
		values.put("time", time);

		if (path != null) {
			values.put("recordpath", path);
		}

		String whereClause = "id=" + id;
		db.update("note", values, whereClause, null);
		db.close();
		EditActivity.this.finish();
	}

	/**
	 * 查看或更新note
	 */
	private void lookOrUpdate() {
		id = getIntent().getIntExtra("id", -1);
		if (id != -1) {
			queryNote(id);
			SUBMIT_STATE = 2;
		}

	}

	private void queryNote(int id) {
		db = myHelper.getReadableDatabase();
		String selection = "id=" + id;
		Cursor queryCursor = db.query("note", null, selection, null, null,
				null, null);
		for (queryCursor.moveToFirst(); !queryCursor.isAfterLast(); queryCursor
				.moveToNext()) {
			String title = queryCursor.getString(1);
			String content = queryCursor.getString(2);
			byte[] img_blob = queryCursor.getBlob(3);
			path = queryCursor.getString(5);

			if (img_blob != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(img_blob, 0,
						img_blob.length);
				img_iv.setImageBitmap(bitmap);
			}
			title_edt.setText(title);
			if (content != null) {
				content_edt.setText(content);
			}

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case GET_GALLERY_REQUEST:
			try {
				pathName = data.getData().toString();
				setHeadImage(pathName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case GET_CAMERA_REQUEST:
			try {
				imgFile = new File(Environment.getExternalStorageDirectory(),
						IMG_NAME);
				pathName = "file://" + imgFile.toString();
				setHeadImage(pathName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		if (myRecorder != null) {
			myRecorder.stop();
		}
		// 反注册广播
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (myPlayer != null) {
			myPlayer.stop();
		}
		super.onPause();
	}

	/**
	 * 设置图片
	 * 
	 * @param data
	 */
	private void setHeadImage(String pathName) {
		if (pathName != null) {
			imageLoader = ImageLoader.getInstance();
			ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
					this).memoryCacheSize(50 * 1024 * 1024)
					.memoryCacheSizePercentage(13).threadPoolSize(3)
					.memoryCache(new WeakMemoryCache()).build();
			imageLoader.init(configuration);
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.loading)
					.showImageForEmptyUri(R.drawable.ic_launcher)
					.showImageOnFail(R.drawable.error)
					.bitmapConfig(Bitmap.Config.ARGB_8888)
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
			imageLoader.displayImage(pathName, img_iv, options);
		}

	}

	/**
	 * 录音对话框
	 */
	private void recodeSoundDialog() {
		view = LayoutInflater.from(this).inflate(
				R.layout.activity_record_dialog, null);
		recordBtn = (ImageButton) view.findViewById(R.id.record);
		hint_tv = (TextView) view.findViewById(R.id.hint_tv);
		recordtime_tv = (TextView) view.findViewById(R.id.recordtime_tv);
		dialog = new Dialog(this, R.style.DialogTheme2);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		dialog.setContentView(view, params);
		Window window = dialog.getWindow();
		// 设置动画
		window.setWindowAnimations(R.style.main_menu_anim);
		// 获取屏幕高度
		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		int screenHeight = outMetrics.heightPixels;
		// 设置对话框位置
		WindowManager.LayoutParams attributes = window.getAttributes();
		attributes.x = 0;
		attributes.y = screenHeight;
		attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
		attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		dialog.onWindowAttributesChanged(attributes);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		recordBtn.setOnTouchListener(this);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startRecord();

			break;
		case MotionEvent.ACTION_UP:
			stopRecord();

			break;

		default:
			break;
		}

		return true;
	}

	/**
	 * 停止录音
	 */
	private void stopRecord() {
		myRecorder.stop();
		recordBtn.setBackgroundResource(R.drawable.recode_bg_normal);
		dialog.dismiss();

	}

	/**
	 * 开始录音
	 */
	private void startRecord() {
		if (path != null) {
			File file = new File(path);
			file.delete();
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String pathTime = sdf.format(new Date());
			path = getApplicationContext().getFilesDir().getAbsolutePath()
					+ File.separator + pathTime + ".amr";
			myRecorder = MyMediarecorder.getInstance(EditActivity.this);
			myRecorder.start(path);
			hint_tv.setText("松开停止录音");
			recordBtn.setBackgroundResource(R.drawable.recode_bg_pressed);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 播放录音
	 */
	private void playSound() {
		myPlayer = MyMediraPlayer.getInstance(EditActivity.this);

		try {
			if (path != null) {

				if (isonce) {
					playBtn.setImageResource(R.drawable.animation_play);
					myPlayer.play(path);
				} else {
					playBtn.setImageResource(R.drawable.play3);
					myPlayer.stop();
				}

				isonce = !isonce;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("PLAY_END")) {
				playBtn.setImageResource(R.drawable.play3);
			}
			if (intent.getAction().equals("TIME")) {
				int time = intent.getIntExtra("time", 0);
				int m = time / 60;
				int s = time - 60 * m;
				recordtime_tv.setText(String.valueOf(m) + " : "
						+ String.valueOf(s));
			}
		}
	}
}
