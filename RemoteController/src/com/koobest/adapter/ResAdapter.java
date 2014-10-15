package com.koobest.adapter;

import com.koobest.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ResAdapter extends BaseAdapter{
	private int[] dataSrc;
	private String[] paths;
	private Context mContext;
	public boolean useSD = false;
	public int maxHeight = 0;
	
	public ResAdapter(Context context,int[] dataSrc,String[] paths){
		this.dataSrc = dataSrc;
		this.paths = paths;
		this.mContext = context;
	}
	
	public void setDataSrc(String[] paths,int[] ids){
		this.dataSrc = ids;
		this.paths = paths;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(!useSD)
			return dataSrc.length;
		else
			return paths.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(view == null){
//			view = inflater.inflate(R.layout.adapter_item, null);
//			ImageView iv = (ImageView) view.findViewById(R.id.iv1);
//			iv.setImageResource(dataSrc[arg0]);
			ImageView iv = new ImageView(mContext);
			if(!useSD)
				iv.setImageResource(dataSrc[arg0]);
			else
				iv.setImageBitmap(BitmapFactory.decodeFile(paths[arg0]));
			
			iv.setMaxHeight(maxHeight);
			iv.setAdjustViewBounds(true);
			view = (ImageView) iv;
		}
		
		
		return view;
	}
}
