package com.koobest.customization;

import com.koobest.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class DialogWindow extends Dialog{
	private int bg_id = R.drawable.bgconfig;
	public DialogWindow(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public DialogWindow(Context context)
	{
		super(context,R.style.FullHeightDialog);

	}
	public void setBackGroundId(int id)
	{
		bg_id = id;
	}
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		setWindow();
		super.show();
	}
	private void setWindow() {
		// TODO Auto-generated method stub
		this.getWindow().setBackgroundDrawable(this.getContext().getResources().getDrawable(bg_id));
		android.view.WindowManager.LayoutParams p = this.getWindow().getAttributes();
//		System.out.println("teete"+p.width+""+p.height)
		p.width = (int) (getWindow().getWindowManager().getDefaultDisplay().getWidth()*0.9);
		p.height = LayoutParams.WRAP_CONTENT;
		this.getWindow().setAttributes(p);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}
}
