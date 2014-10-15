package com.koobest.dialog;

import java.util.zip.Inflater;

import com.koobest.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class DialogAdapter extends BaseAdapter {
	private String[] items;
	private Context mcontext;
	private LayoutInflater inflate;
	private DialogWindow mydialog;
	private Object up_pos, down_pos;

	public DialogAdapter(Context context, String[] options, DialogWindow md) {
		// TODO Auto-generated constructor stub
		items = options;
		mcontext = context;
		inflate = LayoutInflater.from(mcontext);
		mydialog = md;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vv = inflate.inflate(R.layout.dialog_items, null);
		vv.setTag(position);
		TextView tv = (TextView) vv.findViewById(R.id.text);
		tv.setText(items[position]);
		tv.setTextColor(Color.DKGRAY);
		tv.setPadding(10, 10, 10, 10);
		return vv;
	}



}
