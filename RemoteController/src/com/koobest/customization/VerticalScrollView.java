package com.koobest.customization;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import com.koobest.R;
import com.koobest.constant.ConfigConstant;
import com.koobest.parse.DomParse;
import com.koobest.parse.FileManager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

public class VerticalScrollView extends ScrollView implements ViewFactory{
	private Bitmap[] resIcon = null;// = {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.aa,R.drawable.aa,R.drawable.bb,R.drawable.cc,R.drawable.dd,R.drawable.ee};
//	ImageView[] iv = new ImageView[resIcon.length];
	
	int totalHeight,itemHeight,itemWidth;
	float ratio;
	int currentY = 0, finalY = 0;
	int temp = 0;
	private Paint mPaint;
	
	boolean keyUp = true;
	
	private PopupWindow popWin = null;
	private LinearLayout pop_ll = null;
	private ImageSwitcher switcher = null;
	private onItemSelected onSelected = null;
	private TextView pop_des = null;
	private LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
	
	private int textSize = 20;
	private int textColor = Color.WHITE;
	
	private Rect rect;
	private Context mContext = null;
	
	private int tempPos = 0, tempScroll = 0;
	private boolean itemChanged = true;
	
	private WindowManager wm = null;
//	private FileManager fm = null;
//	private File[] files = null;
	private HashMap<Integer, String[]> filesPathName = null;
	private DomParse mainCFG = null;
	private String[] description = null;
	
	private Resources res;
	
	public VerticalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.initalize(context);
	}
	public VerticalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.initalize(context);
	}
	public VerticalScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.initalize(context);
	}
    
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		currentY = (int) (ev.getY() * ratio + ev.getY());
//		currentY = (int) (this.getScrollY() + ev.getY());
//		System.out.println(currentY + " **************");
		int position = (int)(this.getScrollY() + ev.getY()) / itemHeight;
		if(position > resIcon.length - 1)
			position = resIcon.length - 1;
		
		
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			
			keyUp = false;
			
			currentY = (int) (this.getScrollY() + ev.getY());
			this.invalidate();
			
			switcher.setImageDrawable(new BitmapDrawable(resIcon[position]));
			pop_des.setText(description[position]);
			if(!popWin.isShowing())
				popWin.showAtLocation(this, Gravity.TOP, itemWidth, rect.top);
			
			temp = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			
			if(Math.abs(temp - (int)ev.getY()) > 10){
				this.scrollTo((int)ev.getX(), (int)(ev.getY() * ratio));
//				System.out.println(ev.getY() * ratio + " %%%%%%%%%%%");
				if(tempScroll == this.getScrollY()){
					currentY = (int) (this.getScrollY() + ev.getY());
					this.invalidate();
				}
				tempScroll = this.getScrollY();
			}
			else{
				currentY = (int) (this.getScrollY() + ev.getY());
				this.invalidate();
			}
			
			
			if(tempPos != position)
				itemChanged  = true;
			
			tempPos = position;
			if(itemChanged){
				switcher.setImageDrawable(new BitmapDrawable(resIcon[position]));
				pop_des.setText(description[position]);
				if(!popWin.isShowing())
					popWin.showAtLocation(this, Gravity.TOP, itemWidth, rect.top);
				
				itemChanged = false;
			}
			break;
//		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			keyUp = true;
			finalY =  ((LinearLayout)this.getChildAt(0)).getChildAt(position).getTop();
			
			if(popWin.isShowing())
				popWin.dismiss();
			
			if(this.onSelected != null)
				onSelected.onSelected(position, ((LinearLayout)this.getChildAt(0)).getChildAt(position), filesPathName.get(position)[0]);
			
			this.invalidate();
			break;
		default:
			break;
		}
		
		
		return true;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		if(!keyUp)
			canvas.drawRect(0, currentY - itemHeight / 2 , itemWidth, currentY + itemHeight / 2, mPaint);
		else
			canvas.drawRect(0, finalY , itemWidth, finalY + itemHeight, mPaint);
		
		
	}
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		getWindowVisibleDisplayFrame(rect);
	}
	
	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		ImageView iv = new ImageView(mContext);
		
		return iv;
	}

	public interface onItemSelected{
		public void onSelected(int position, View view, String configPath);
	}
	
	public void setOnItemSelectedListener(onItemSelected onSelected){
		this.onSelected = onSelected;
	}
	
	public void initalize(Context context){
		this.mContext = context;
		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		rect = new Rect();
		mPaint = new Paint();
		mPaint.setColor(Color.YELLOW);
		this.setVerticalScrollBarEnabled(false);
		res = getResources();
		
		resIcon = this.getConfigIcons();
//		BitmapFactory.Options opts = new BitmapFactory.Options();
//		opts.inJustDecodeBounds = true;
		
//		BitmapFactory.decodeResource(context.getResources(), resIcon[1],opts);
		
		pop_ll = new LinearLayout(context);
		pop_ll.setGravity(Gravity.CENTER);
		pop_ll.setOrientation(1);
		pop_ll.setBackgroundResource(R.xml.verticalpopbg);
		
		switcher = new ImageSwitcher(context);
		switcher.setFactory(this);
		switcher.setInAnimation(AnimationUtils.loadAnimation(mContext, 
                android.R.anim.fade_in)); 
        //系统的anim中的fade_out.xml 
		switcher.setOutAnimation(AnimationUtils.loadAnimation(mContext, 
                android.R.anim.fade_out));
		lp.gravity = Gravity.CENTER;
		
		
		pop_des = new TextView(context);
		pop_des.setTextColor(textColor);
//		pop_des.setTextScaleX(textSize);
		
		pop_ll.addView(switcher, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		pop_ll.addView(pop_des,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//		totalHeight = opts.outHeight * resIcon.length;
		
		itemWidth = wm.getDefaultDisplay().getWidth() / 4;
		itemHeight = itemWidth;
		totalHeight = itemHeight * resIcon.length;
		
		ratio = (float)totalHeight / wm.getDefaultDisplay().getHeight();
		
		popWin = new PopupWindow(pop_ll,wm.getDefaultDisplay().getWidth() - itemWidth,wm.getDefaultDisplay().getHeight());
		
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(1);
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		this.setMinimumHeight(itemWidth + 10); 
		for(Bitmap res:resIcon){
			
			ImageView iv = new ImageView(context);
			iv.setMaxWidth(itemWidth);
			iv.setMaxHeight(itemWidth);
			iv.setAdjustViewBounds(true);
			iv.setScaleType(ScaleType.FIT_CENTER);
			iv.setImageBitmap(res);
			iv.setPadding(5, 5, 5, 5);
			ll.addView(iv, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			
		}
		this.addView(ll);
		
		System.out.println(this.getChildCount());
		//
		
		
	}
	
	private Bitmap[] getConfigIcons(){
		//get config name and path
		FileManager fm = new FileManager(this.mContext);
		File[] files = null;
		try{
			files = fm.getFilteredFiles(ConfigConstant.CONFIGPATH, ".xml");
			mainCFG = new DomParse(ConfigConstant.MAINCFGFILEPATH);
		} catch(IOException e){
			return null;
		} catch(Exception e){
			return null;
		}
		
		filesPathName = fm.getFilesPathAndName(files);
		
		//get config's icon path
		Bitmap[] icons = new Bitmap[filesPathName.size()];
		description = new String[filesPathName.size()];
		
		String iconPath = null;
		String des = null;
		
		for(int i = 0; i < filesPathName.size(); i ++){
			iconPath = mainCFG.getSingleContent(filesPathName.get(i)[1].replace(".xml", ""));
			des = mainCFG.getSingleContent(filesPathName.get(i)[1].replace(".xml", "") + "Description");
			
			if(iconPath != null){
				icons[i] = BitmapFactory.decodeFile(iconPath);
			} else {
				iconPath = mainCFG.getSingleContent(filesPathName.get(i)[1].replace(".xml", "") + "type");
				if(iconPath != null){
					if(iconPath.toLowerCase().equals(ConfigConstant.TV_CODE)){
						icons[i] = BitmapFactory.decodeResource(getResources(), R.drawable.def_tv);
						if(des == null)
							des = res.getString(R.string.def_tv);
					}
					if(iconPath.toLowerCase().equals(ConfigConstant.AM_CODE)){
						icons[i] = BitmapFactory.decodeResource(getResources(), R.drawable.def_airmachine);
						if(des == null)
							des = res.getString(R.string.def_airmachine);
					}
					if(iconPath.toLowerCase().equals(ConfigConstant.AUDIO_CODE)){
						icons[i] = BitmapFactory.decodeResource(getResources(), R.drawable.def_audio);
						if(des == null)
							des = res.getString(R.string.def_audio);
					}
					if(iconPath.toLowerCase().equals(ConfigConstant.DVD_CODE)){
						icons[i] = BitmapFactory.decodeResource(getResources(), R.drawable.def_dvd);
						if(des == null)
							des = res.getString(R.string.def_dvd);
					}
				}
			}
			if(iconPath == null)
				icons[i] = BitmapFactory.decodeResource(getResources(), R.drawable.no_img);
			
			if(des == null || des.trim().length() == 0)
				des = res.getString(R.string.def_no_des);
			
			description[i] = des;
			
			des = null;
		}
		
		return icons;
	}
	
	
	 
}
