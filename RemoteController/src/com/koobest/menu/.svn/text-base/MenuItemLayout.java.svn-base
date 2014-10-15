package com.koobest.menu;

import java.util.jar.Attributes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuItemLayout extends LinearLayout{
	public TextView text;
	public ImageView image;
	public LinearLayout li;
	private Context mcontext;
	public MenuItemLayout(Context context) {
		super(context);
		mcontext = context;
		initial();
		// TODO Auto-generated constructor stub
	}

	public MenuItemLayout(Context context, AttributeSet attrs){
		super(context, attrs);
		mcontext = context;
		initial();
	}
	public void initial(){
		li = new LinearLayout(mcontext);
		li.setPadding(0, 5, 0, 5);
		text = new TextView(mcontext);
		image = new ImageView(mcontext);
		li.setOrientation(VERTICAL);
		int length = 40;
		li.addView(image,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
//		image.setGravity(Gravity.CENTER);
		li.addView(text,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1));
		text.setGravity(Gravity.CENTER);
		li.setGravity(Gravity.CENTER);
		text.setTextColor(Color.BLACK);
		this.addView(li, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	}
}
