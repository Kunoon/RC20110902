package com.koobest.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;

import com.koobest.parse.DomParse;
import com.koobest.socket.HttpConnection;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RCInformationAdapter extends BaseAdapter{
	private ArrayList<String> info;
	private HttpConnection con = null;
	private DomParse dom = null;
	private Context mContext;
	
	public RCInformationAdapter(){
		info = new ArrayList<String>();
	}
	
	public RCInformationAdapter(Context context,HttpConnection con){
		this.con = con;
		this.mContext = context;
		info = new ArrayList<String>();
		
	}
	
	public HttpConnection getClient(){
		return this.con;
	}
	

	public void setDataSource(ArrayList<String> info) {
//		info.clear();
		this.info = info;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return info.size();
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
			TextView tv = new TextView(this.mContext);
			tv.setTextColor(Color.WHITE);
			tv.setTextSize(25);
			if(info.get(position).equals("clear")){
				tv.setBackgroundColor(Color.LTGRAY);
				tv.setTag("clear");
				tv.setText("Çå³ý");
				tv.setTextColor(Color.BLACK);
			} else {
				tv.setText(info.get(position));
			}
			convertView = tv;
		}
		return convertView;
	}

}
