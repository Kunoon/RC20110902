package com.koobest.customization;

import com.koobest.parse.BitmapManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PreImageView extends ImageView{
	
	Bitmap front = null;
	public int[] position = new int[2];
	public boolean[] displaySD = new boolean[2];
	
	public PreImageView(Context context,AttributeSet attrs) {
		super(context,attrs);
		// TODO Auto-generated constructor stub
		
	}
	
	
	public void setFront(Bitmap front){
		this.front = front;
		this.invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		if(front != null){
			canvas.drawBitmap(front, 10, this.getHeight() / 3, null);
			canvas.drawBitmap(front, 10, this.getHeight() * 2 / 3, null);
		}
	}
	
	
}
