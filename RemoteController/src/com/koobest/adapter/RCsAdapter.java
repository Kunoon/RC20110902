package com.koobest.adapter;

import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RCsAdapter extends BaseAdapter{
	private HashMap<Integer,HashMap<String, String>> rcs;
	private Context mContext;
	
	public RCsAdapter(Context context,HashMap<Integer,HashMap<String, String>> rcs){
		this.rcs = rcs;
		this.mContext = context;
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rcs.size();
	}
	
	public void setDataSource(HashMap<Integer,HashMap<String, String>> rcs){
		this.rcs = rcs;
		this.notifyDataSetChanged();
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
//			LinearLayout rc_list = new LinearLayout(mContext);
			TextView rc_info = new TextView(mContext);
			Object[] keyarray = rcs.get(position).keySet().toArray();
			StringBuilder sb = new StringBuilder();
			for(int j = 1; j < keyarray.length; j++){
//				System.out.println(keyarray[j].toString() + " " + rcs.get(position).get(keyarray[j].toString()));
				sb.append(rcs.get(position).get(keyarray[j].toString()) + " ");	
			}
			rc_info.setText(sb.toString());
			rc_info.setTextSize(25);
			rc_info.setTag(rcs.get(position).get(keyarray[0].toString()));
			convertView = rc_info;
		}
		return convertView;
	}

}
