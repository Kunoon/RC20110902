package com.koobest.customization;

import com.koobest.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;

public class AlertWindow extends AlertDialog{

	private static int style_id = R.style.FullHeightDialog;

	public AlertWindow(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}
	
	public AlertWindow(Context context) {
		super(context,style_id );	
		// TODO Auto-generated constructor stub
	}
	
	public void setBackGroundId(int id)
	{
		style_id = id;
	}
	
	public void show() {
		// TODO Auto-generated method stub
		setWindow();
		super.show();
	}
	@Override
	public void setCustomTitle(View customTitleView) {
		// TODO Auto-generated method stub
		customTitleView.setBackgroundResource(R.drawable.bgconfig);
		super.setCustomTitle(customTitleView);
		
	}

	@Override
	public View onCreatePanelView(int featureId) {
		// TODO Auto-generated method stub
		return super.onCreatePanelView(featureId);
	}

	@Override
	public ListView getListView() {
		// TODO Auto-generated method stub
		return super.getListView();
	}

	@Override
	public void setView(View view) {
		// TODO Auto-generated method stub
		view.setBackgroundColor(Color.BLUE);
		super.setView(view);
	}

	private void setWindow() {
		// TODO Auto-generated method stub
//		this.getWindow().setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.bgconfig));
		android.view.WindowManager.LayoutParams p = this.getWindow().getAttributes();
//		System.out.println("teete"+p.width+""+p.height)
		p.width = (int) (getWindow().getWindowManager().getDefaultDisplay().getWidth()*0.9);
		p.height = LayoutParams.WRAP_CONTENT;
		this.getWindow().setAttributes(p);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}
}
