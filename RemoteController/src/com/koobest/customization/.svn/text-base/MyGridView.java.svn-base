package com.koobest.customization;

import java.util.ArrayList;

import com.koobest.R;
import com.koobest.RemoteControllerMain;
import com.koobest.constant.ConfigConstant;
import com.koobest.parse.BitmapManager;
import com.koobest.parse.DomParse;
import com.koobest.parse.FileManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.Toast;

public class MyGridView extends GridView {

	Paint mPaint;
	FileManager fm;
	// File[] files;
	private Bitmap background = null, foreground = null;
	int dens;
	float cleft;
	float fheight;
	private int width = 0;

	private int j = 1;
	boolean goUp = true;

	public Matrix matrix = null;


	private SharedPreferences sp;
	private int BOTTOMADJUST = 10;
	
	int numColumns = 2;
	ArrayList<Bitmap> clipedBitmap;
	boolean isColumnChanged = true;
	private float fgap;
	
	// private DomParse dp;
	public int max = 0;
//	String mainConfigPath = RemoteControllerMain.configDirPath
//			+ "/mainconfig.cfg";

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources res = context.getResources();
		
		DomParse dp = null;
		// TODO Auto-generated constructor stub
		try {
			dp = new DomParse(ConfigConstant.MAINCFGFILEPATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mPaint = new Paint();
		mPaint.setColor(Color.CYAN);

		sp = context.getSharedPreferences("com.koobest_preferences", 0);
		clipedBitmap = new ArrayList<Bitmap>();

		this.setChange();

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();

		wm.getDefaultDisplay().getMetrics(dm);
		if (dm.widthPixels > dm.heightPixels)
			max = dm.widthPixels;
		else
			max = dm.heightPixels;

		String backgroundpath = dp.getSingleContent("shelfbackground");
		String[] backgroundInfo = { String.valueOf(R.drawable.anew), "false" };
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		if (backgroundpath != null) {
			String[] temp = backgroundpath.split("_");
			BitmapFactory.decodeResource(context.getResources(), Integer.parseInt(temp[0]),opts);
			if(opts.outHeight != 0)
				backgroundInfo = temp;
		}

		String shelfFrontPath = dp.getSingleContent("shelffront");
		String[] frontInfo = { String.valueOf(R.drawable.shelf), "false" };
		if (shelfFrontPath != null) {
			String[] temp = shelfFrontPath.split("_");
			BitmapFactory.decodeResource(context.getResources(), Integer.parseInt(temp[0]),opts);
			if( opts.outHeight > 0)
				frontInfo = temp;
			
		}
		
		// 如果图片过大，进行压缩
		if (!Boolean.parseBoolean(backgroundInfo[1])) {
			if (BitmapManager.getMaxLength(context, Integer
					.parseInt(backgroundInfo[0]), null) > max) {
				background = BitmapManager.compress(context, Integer
						.parseInt(backgroundInfo[0]), null, max,false);
			} else
				background = BitmapFactory.decodeResource(res, Integer
						.parseInt(backgroundInfo[0]));
		} else {
			if (BitmapManager.getMaxLength(null, 0, backgroundInfo[0]) > max) {
				background = BitmapManager.compress(null, 0, backgroundInfo[0],
						max,false);
			} else
				background = BitmapFactory.decodeFile(backgroundInfo[0]);
		}

		if (!Boolean.parseBoolean(frontInfo[1])) {
			if (BitmapManager.getMaxLength(context, Integer
					.parseInt(frontInfo[0]), null) > max)
				foreground = BitmapManager.compress(context, Integer
						.parseInt(frontInfo[0]), null, max,false);
			else
				foreground = BitmapFactory.decodeResource(res, Integer
						.parseInt(frontInfo[0]));
		} else {
			if (BitmapManager.getMaxLength(null, 0, frontInfo[0]) > max) {
				foreground = BitmapManager.compress(null, 0, frontInfo[0], max,false);
			} else
				foreground = BitmapFactory.decodeFile(frontInfo[0]);
		}

		background = BitmapManager.zoomBitmap(background, wm
				.getDefaultDisplay().getHeight(), wm.getDefaultDisplay()
				.getWidth());
		foreground = BitmapManager.zoomBitmap(foreground, 0, wm
				.getDefaultDisplay().getWidth() - 20);

		width = dm.widthPixels;
		
		fheight = (float) foreground.getHeight();
		fgap = (background.getWidth() - foreground.getWidth()) * width
				/ background.getWidth() / 2;


	}

	public void setShelfBG(Bitmap shelfBG, Bitmap shelfFront) {
		if (shelfBG != null)
			this.background = shelfBG;

		if (shelfFront != null)
			this.foreground = shelfFront;

		this.fheight = (float) shelfFront.getHeight(); //foreground.getHeight()
				/// (float) background.getWidth() * width;

		fgap = (background.getWidth() - foreground.getWidth()) * width
				/ background.getWidth() / 2;
		
	}

	

	public void setChange() {
		isColumnChanged = true;
		j = 1;
	}

	
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
//		Toast.makeText(mContext, this.getChildAt(0).getTop() + " " + this.getChildAt(0).getHeight(), Toast.LENGTH_SHORT).show();

//		System.out.println("********On draw************** " + this.getHeight());
		for(int i = 0; i < this.getChildCount();i++){
			this.getChildAt(i).setMinimumHeight(this.getHeight() / 3);
		}
		if (isColumnChanged) {

			isColumnChanged = false;

			numColumns = sp.getInt("num_columns", 2);
			
			BOTTOMADJUST = sp.getInt("bottom_adjust", 10);
		}

		int pos = this.getFirstVisiblePosition() / numColumns;
		int child_height = this.getChildAt(0).getHeight();
		int offset = this.getChildAt(0).getTop()
				- (child_height + RemoteControllerMain.VERTICALSPACING) * pos;

		// use this.height is more compatible than background.getheight
		if (Math.abs(offset) > j * this.getHeight()) {
			j++;
		} else if (Math.abs(offset) < (j - 1) * this.getHeight()) {
			j--;
		}

		canvas.drawBitmap(background, 0, offset + this.getHeight() * (j - 1),
				null);

		canvas.drawBitmap(background, 0, offset + this.getHeight() * j, null);

		cleft = fgap;
		drawShelf(canvas);
//		drawShaderTop(canvas, this.getChildAt(0).getTop()-fheight, +this.getChildAt(0).getTop() + DOWNLIMIT-fheight);

	}

	private void drawShelf(Canvas canvas) {
		// TODO Auto-generated method stub

		float newTop = this.getChildAt(0).getTop() - RemoteControllerMain.VERTICALSPACING;
		
		canvas.drawBitmap(foreground, fgap, newTop, null);

		int yOffset = this.getChildAt(0).getHeight() - BOTTOMADJUST;
		canvas.drawBitmap(foreground, fgap, yOffset + this.getChildAt(0).getTop() , null);

		
		yOffset = 2 * this.getChildAt(0).getHeight() + RemoteControllerMain.VERTICALSPACING - BOTTOMADJUST;
		canvas.drawBitmap(foreground, fgap, yOffset + this.getChildAt(0).getTop() , null);

		yOffset = (this.getChildAt(0).getHeight() + RemoteControllerMain.VERTICALSPACING) * 2 - BOTTOMADJUST;
		canvas.drawBitmap(foreground, fgap, yOffset + this.getChildAt(0).getBottom(), null);

	}

	//This method is abandoned	
	private void drawShaderTop(Canvas canvas, float top, float bot) {
		// TODO Auto-generated method stub
		Paint lpaint = new Paint();
		LinearGradient shaderLeft = new LinearGradient(0, bot, 0, top,
				0x11111111, 0xcc111111, TileMode.CLAMP);
		lpaint.setShader(shaderLeft);
		float gap = bot - top;
		Path lpath = new Path();
		lpath.moveTo(cleft, top);
		lpath.lineTo(getWidth() - cleft, top);
		lpath.lineTo(getWidth() - cleft - 10, top + gap);
		lpath.lineTo(cleft + 10, top + gap);
		lpath.lineTo(cleft, top);
		canvas.drawPath(lpath, lpaint);
		lpath.reset();
		int x = this.getChildAt(0).getHeight()
				+ RemoteControllerMain.VERTICALSPACING;
		shaderLeft = new LinearGradient(0, x + bot, 0, x + top, 0x11111111,
				0xcc111111, TileMode.CLAMP);
		lpaint.setShader(shaderLeft);
		lpath.moveTo(cleft, x + top);
		lpath.lineTo(getWidth() - cleft, x + top);
		lpath.lineTo(getWidth() - cleft - 10, x + top + gap);
		lpath.lineTo(cleft + 10, x + top + gap);
		lpath.lineTo(cleft, x + top);
		canvas.drawPath(lpath, lpaint);
		lpath.reset();
		// x = -x;
		// shaderLeft = new LinearGradient(0, x + bot, 0, x + top, 0x11111111,
		// 0xcc111111, TileMode.CLAMP);
		// lpaint.setShader(shaderLeft);
		// lpath.moveTo(cleft, x + top);
		// lpath.lineTo(getWidth() - cleft, x + top);
		// lpath.lineTo(getWidth() - cleft - 10, x + top + gap);
		// lpath.lineTo(cleft + 10, x + top + gap);
		// lpath.lineTo(cleft, x + top);
		// canvas.drawPath(lpath, lpaint);
		// lpath.reset();
		// x = -x;
		x = 2 * x;
		shaderLeft = new LinearGradient(0, x + bot, 0, x + top, 0x11111111,
				0xcc111111, TileMode.CLAMP);
		lpaint.setShader(shaderLeft);
		lpath.moveTo(cleft, x + top);
		lpath.lineTo(getWidth() - cleft, x + top);
		lpath.lineTo(getWidth() - cleft - 10, x + top + gap);
		lpath.lineTo(cleft + 10, x + top + gap);
		lpath.lineTo(cleft, x + top);
		canvas.drawPath(lpath, lpaint);
		lpath.reset();
		x = (int) (x * 1.5f);

		shaderLeft = new LinearGradient(0, x + bot, 0, x + top, 0x11111111,
				0xcc111111, TileMode.CLAMP);
		lpaint.setShader(shaderLeft);
		lpath.moveTo(cleft, x + top);
		lpath.lineTo(getWidth() - cleft, x + top);
		lpath.lineTo(getWidth() - cleft - 10, x + top + gap);
		lpath.lineTo(cleft + 10, x + top + gap);
		lpath.lineTo(cleft, x + top);
		canvas.drawPath(lpath, lpaint);

	}
	
	//This method is abandoned
	public void drawShaderSide(Canvas canvas, float start, float end) {
		Paint lpaint = new Paint();
		LinearGradient shaderLeft = new LinearGradient(start, 0, end, 0,
				0x11c9c9c9, 0xcc555555, TileMode.CLAMP);

		lpaint.setShader(shaderLeft);

		Path lpath = new Path();
		lpath.moveTo(start, this.getChildAt(0).getTop() - 5);
		lpath.lineTo(end, this.getChildAt(0).getTop() - 5);
		lpath.lineTo(end, this.getChildAt(0).getBottom() + 5);
		lpath.lineTo(start, this.getChildAt(0).getBottom() + 5);
		lpath.lineTo(start, this.getChildAt(0).getTop() - 5);
		canvas.drawPath(lpath, lpaint);
		lpath.reset();

		int x = this.getChildAt(0).getHeight()
				+ RemoteControllerMain.VERTICALSPACING;
		lpath.moveTo(start, x + this.getChildAt(0).getTop() - 5);
		lpath.lineTo(end, x + this.getChildAt(0).getTop() - 5);
		lpath.lineTo(end, x + this.getChildAt(0).getBottom() + 5);
		lpath.lineTo(start, x + this.getChildAt(0).getBottom() + 5);
		lpath.lineTo(start, x + this.getChildAt(0).getTop() - 5);
		canvas.drawPath(lpath, lpaint);
		lpath.reset();

		x = 2 * x;
		lpath.moveTo(start, x + this.getChildAt(0).getTop() - 5);
		lpath.lineTo(end, x + this.getChildAt(0).getTop() - 5);
		lpath.lineTo(end, x + this.getChildAt(0).getBottom() + 5);
		lpath.lineTo(start, x + this.getChildAt(0).getBottom() + 5);
		lpath.lineTo(start, x + this.getChildAt(0).getTop() - 5);
		canvas.drawPath(lpath, lpaint);

	}

	public void releaseMemory() {
		if (this.background != null && !this.background.isRecycled()) {
			this.background.recycle();
			this.foreground.recycle();
		}
	}

}
