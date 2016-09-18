package com.dd.qqkeyboard;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends BaseKeyBoardActivity {

	private ListView mLV;
	private RelativeLayout content;
	private EditText et;
	private ArrayList<String> list;

	@Override
	public int setLayoutID() {
		return R.layout.activity_main;
	}

	@Override
	public ViewGroup findRootView() {
		content = (RelativeLayout) findViewById(R.id.content);
		mLV = (ListView) findViewById(R.id.lv);
		return content;
	}

	@Override
	public EditText findEditText() {
		et = (EditText) findViewById(R.id.et);

		return et;
	}

	@Override
	public boolean isShowKeyboard() {
		int lastVisiblePosition = mLV.getLastVisiblePosition();
		if (lastVisiblePosition == list.size() - 1) {
			return true;
		}
		return false;
	}

	@Override
	public void initView() {


		list = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			list.add("data" + i);
		}
		mLV.setAdapter(new MyAdapter());

		mLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), list.get(position), Toast.LENGTH_SHORT).show();
			}
		});
	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				switch (getItemViewType(position)) {
					case 0:
						convertView = View.inflate(getApplicationContext(), R.layout.item, null);
						break;
					case 1:
						convertView = View.inflate(getApplicationContext(), R.layout.item_other, null);
						break;
				}
			}
			switch (getItemViewType(position)) {
				case 0:
					TextView tv = (TextView) convertView.findViewById(R.id.tv_me);
					tv.setText(list.get(position));
					break;
				case 1:
					TextView tv2 = (TextView) convertView.findViewById(R.id.tv_other);
					tv2.setText(list.get(position));
					break;
			}
			return convertView;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position % 2 == 0) {
				return 0;
			} else {
				return 1;
			}
		}
	}

}
