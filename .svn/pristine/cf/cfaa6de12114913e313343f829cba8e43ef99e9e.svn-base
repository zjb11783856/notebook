package com.boge.android_0618_notebook.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boge.android_0618_notebook.R;
import com.boge.android_0618_notebook.adapter.NoteAdapter;
import com.boge.android_0618_notebook.entity.Note;
import com.boge.android_0618_notebook.utils.DatabaseUtils;
import com.boge.android_0618_notebook.utils.MyOpenHelper;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {
	private TextView create_tv;
	private ListView noteList_lv;
	private MyOpenHelper myHelper;
	private SQLiteDatabase db;
	private List<Note> noteList;
	private NoteAdapter noteAdapter;
	private boolean isShow = false;// 默认为显示checkbox
	private RelativeLayout titleLayout;
	private LinearLayout menuLayout;
	private Button cancel_btn;
	private Button all_btn;
	private Button delete_btn;
	private boolean isExit = false;
	private boolean once = true;// 第一次点击为全选第二次为全不选
	private DatabaseUtils dbUstils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initAdapter();
	}

	private void initView() {
		noteList = new ArrayList<Note>();
		dbUstils = DatabaseUtils.getInstance(this);

		create_tv = (TextView) findViewById(R.id.create);
		noteList_lv = (ListView) findViewById(R.id.noteList);
		titleLayout = (RelativeLayout) findViewById(R.id.titleLayout);
		menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
		cancel_btn = (Button) findViewById(R.id.cancel_btn);
		all_btn = (Button) findViewById(R.id.all_btn);
		delete_btn = (Button) findViewById(R.id.delete_btn);
		cancel_btn.setOnClickListener(this);
		all_btn.setOnClickListener(this);
		delete_btn.setOnClickListener(this);

		myHelper = new MyOpenHelper(this, "notedb", null, 1);
		create_tv.setOnClickListener(this);
		noteList_lv.setOnItemClickListener(this);
		noteList_lv.setOnItemLongClickListener(this);

	}

	private void initAdapter() {
		queryNoteTitle();
		noteAdapter = new NoteAdapter(this, noteList, isShow);
		noteList_lv.setAdapter(noteAdapter);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.create:
			Intent intent = new Intent(MainActivity.this, EditActivity.class);
			startActivity(intent);
			break;
		case R.id.all_btn:
			selectAll();
			break;
		case R.id.delete_btn:
			deleteData();
			break;
		case R.id.cancel_btn:
			isShow = false;
			titleLayout.setVisibility(View.VISIBLE);
			menuLayout.setVisibility(View.GONE);
			initAdapter();
			break;
		default:
			break;
		}

	}

	/**
	 * 全选或全不选
	 */
	private void selectAll() {
		if (once) {
			for (int i = 0; i < noteList.size(); i++) {
				noteAdapter.getIsCheck().put(i, true);
			}
			once = false;
			all_btn.setText(R.string.others);
		} else {
			for (int i = 0; i < noteList.size(); i++) {
				noteAdapter.getIsCheck().put(i, false);
			}
			once = true;
			all_btn.setText(R.string.all);
		}
		noteAdapter.notifyDataSetChanged();

	}

	private void queryNoteTitle() {
		noteList.clear();
		db = myHelper.getReadableDatabase();
		Cursor queryCursor = db.query("note", null, null, null, null, null,
				null);
		for (queryCursor.moveToFirst(); !queryCursor.isAfterLast(); queryCursor
				.moveToNext()) {
			int id = queryCursor.getInt(0);
			String title = queryCursor.getString(1);
			String time = queryCursor.getString(4);
			Note note = new Note(id, title, time);
			noteList.add(note);
		}
		db.close();

	}

	/**
	 * 删除数据
	 */
	private void deleteData() {
		boolean change = false;
		for (int i = 0; i < noteList.size(); i++) {
			boolean isCheck = noteAdapter.getIsCheck().get(i);
			if (isCheck) {
				int id = noteList.get(i).getId();
				dbUstils.delete(id);
				change = true;
			}
		}
		if (change) {
			queryNoteTitle();
			noteAdapter.notifyDataSetChanged();
			titleLayout.setVisibility(View.VISIBLE);
			menuLayout.setVisibility(View.GONE);
		} else {
			Toast.makeText(this, "未选中任何选项", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onResume() {

		queryNoteTitle();
		noteAdapter.notifyDataSetChanged();

		super.onResume();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		int id = noteList.get(position).getId();
		Intent intent = new Intent(MainActivity.this, EditActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		isShow = true;
		titleLayout.setVisibility(View.GONE);
		menuLayout.setVisibility(View.VISIBLE);
		initAdapter();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitByDoubleClick();
		}
		return false;
	}

	/**
	 * 双击退出程序
	 */
	private void exitByDoubleClick() {
		Timer timer = null;
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, "双击退出程序", Toast.LENGTH_SHORT).show();
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					isExit = false;

				}
			}, 1000);
		} else {
			MainActivity.this.finish();
		}
	}

}
