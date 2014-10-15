package com.koobest.customization;


import com.koobest.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DialogChoice implements android.content.DialogInterface.OnClickListener{
	
	Context dContext;
	LayoutInflater inflater;
	AlertDialog.Builder abd;
	String[] choices;
	public DialogChoice(Context mContext,String[] choices){
		this.dContext = mContext;
		this.choices = choices;
		abd = new AlertDialog.Builder(mContext);
		inflater = LayoutInflater.from(mContext);
	}
	
	public void showDialog(){
		DialogChoiceAdapter dc_adapter = new DialogChoiceAdapter(dContext, inflater);
		//abd.setIcon(arg0);
		abd.setTitle("ÐÞ¸Ä");
		abd.setNegativeButton("È·¶¨", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		abd.setAdapter(dc_adapter, this);
		abd.show();
	
	}
	
	public class DialogChoiceAdapter extends BaseAdapter{
		LayoutInflater inflater;
		Context aContext;
		public DialogChoiceAdapter(Context context,LayoutInflater li){
			this.aContext = context;
			this.inflater = li;
			
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return choices.length;
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
			View dialog_item = inflater.inflate(R.layout.dialog_item, null);
			TextView tv = (TextView) dialog_item.findViewById(R.id.dialog_textview);
			tv.setTextColor(Color.BLACK);
			tv.setBackgroundColor(Color.WHITE);
			
			tv.setText(choices[position]);
			
			
			return convertView;
		}
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		System.out.println(choices[which]);
	}
	
}
