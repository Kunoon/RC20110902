package com.koobest.dialog;

import com.koobest.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class DialogWindow extends Dialog{
	
	private int bg_id = R.drawable.bgconfig;
	private int gravity = Gravity.CENTER;
	private int win_width = (int) (getWindow().getWindowManager().getDefaultDisplay().getWidth()*0.9);;
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
	public void setGravity(int pa){
		gravity = pa;
	}
	public void setWidth(int width){
		win_width  = width;
	}
	private void setWindow() {
		// TODO Auto-generated method stub
		this.getWindow().setBackgroundDrawableResource(bg_id);
		android.view.WindowManager.LayoutParams p = this.getWindow().getAttributes();
//		System.out.println("teete"+p.width+""+p.height)
		p.width = win_width;
		p.height = LayoutParams.WRAP_CONTENT;
//		p.dimAmount=0.3f;
		p.dimAmount=0;
		p.gravity = gravity;
		this.getWindow().setAttributes(p);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}
}
