package com.boge.android_0618_notebook.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.boge.android_0618_notebook.R;
import com.boge.android_0618_notebook.entity.Note;

public class NoteAdapter extends BaseAdapter {
	private Context context;
	private List<Note> noteList;
	private boolean isShow;
	private ViewHolder holder;
	private HashMap<Integer, Boolean> isCheck;

	public NoteAdapter(Context context, List<Note> noteList, boolean isShow) {
		super();
		this.context = context;
		this.noteList = noteList;
		this.isShow = isShow;
		initData();
	}

	private void initData() {
		isCheck = new HashMap<Integer, Boolean>();
		for (int i = 0; i < noteList.size(); i++) {
			isCheck.put(i, false);
		}

	}

	@Override
	public int getCount() {
		if (noteList != null) {
			return noteList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (noteList != null && position < noteList.size()) {
			return noteList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int index = position;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.activity_item, parent, false);
			holder.title_tv = (TextView) convertView.findViewById(R.id.title);
			holder.time_tv = (TextView) convertView.findViewById(R.id.time);
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Note note = noteList.get(position);
		holder.title_tv.setText(note.getTitle());
		holder.time_tv.setText(note.getTime());
		if (isShow) {
			holder.checkBox.setVisibility(View.VISIBLE);
			holder.time_tv.setVisibility(View.GONE);
			holder.checkBox.setChecked(isCheck.get(index));
			holder.checkBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								isCheck.put(index, true);
							} else {
								isCheck.put(index, false);
							}
						}
					});
		} else {
			holder.checkBox.setVisibility(View.GONE);
			holder.time_tv.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	public static class ViewHolder {
		TextView title_tv;
		TextView time_tv;
		CheckBox checkBox;
	}

	public HashMap<Integer, Boolean> getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(HashMap<Integer, Boolean> isCheck) {
		this.isCheck = isCheck;
	}

}
