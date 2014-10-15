package com.koobest.customization;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.koobest.R;
import com.koobest.parse.BitmapManager;

public class SeekBarDialog extends Dialog implements OnSeekBarChangeListener{

	Context mContext;
	SeekBar sbar;
	TextView text;
	ImageView img;
	Bitmap preview = null;
	Matrix matrix;
	String path = null;
	
	private View.OnClickListener onclick = null;
	
	int max = 0, progress = 0, incresment = 1;
	
	public SeekBarDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.getWindow().setBackgroundDrawableResource(R.drawable.bgconfig);
		this.mContext = context;
		matrix = new Matrix();
		this.setTitle(R.string.rc_dialog_titile_degree);
	}
	
	public SeekBarDialog(Context context, int shapeResourceId) {
		super(context, shapeResourceId);
		// TODO Auto-generated constructor stub
		
		this.mContext = context;
		matrix = new Matrix();
	}

	public void setSeekBar(int max,int progress,int progressIncresment,TextView view){
		View view1 = LayoutInflater.from(mContext).inflate(R.layout.seek_layout, null);
		WindowManager wm = (WindowManager) mContext.getSystemService("window");
		view1.setMinimumWidth((int) (wm.getDefaultDisplay().getWidth() * 0.8f));
		
		sbar = (SeekBar) view1.findViewById(R.id.seekbar);
		if(progressIncresment < 1)
			progressIncresment = 1;
		else if(progressIncresment > max)
			progressIncresment = max;
		
		this.incresment = progressIncresment;
		
		sbar.setMax(max / progressIncresment);
		sbar.setProgress(progress);
		
		sbar.setOnSeekBarChangeListener(this);
		
		this.max = max / progressIncresment;
		
		this.progress = progress;
		
		if(view == null){
			text = (TextView) view1.findViewById(R.id.seekbar_text);
			text.setText((incresment * progress) + "/" + max);
			text.setTextColor(Color.WHITE);
		} else {
			text = view;
		}
		
		if(path != null)
			preview = BitmapManager.compress(null, 0, path, 80,true);
		
		img = (ImageView) view1.findViewById(R.id.title_img);
		img.setAdjustViewBounds(true);
		
		if(preview != null)
			img.setImageBitmap(preview);
		else
			img.setBackgroundResource(R.drawable.add_item_icon);
		
		Button confirm = (Button) view1.findViewById(R.id.cg_bg_btn1);
		if(this.onclick == null)
			throw new NullPointerException("Click listener is undefined");
		else
			confirm.setOnClickListener(onclick);
		
		Button cancel = (Button) view1.findViewById(R.id.cg_bg_btn2);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SeekBarDialog.this.dismiss();
			}
		});
		
		this.setContentView(view1, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
	}
	
	public void setOnClickListener(View.OnClickListener ocl){
		this.onclick = ocl;
	}
	public void setBitmapPath(String path){
		this.path = path;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		this.progress = progress;
		int degrees = incresment * progress;
		
		matrix.postRotate(degrees);
		img.setImageBitmap(Bitmap.createBitmap(preview, 0, 0, preview.getWidth(), preview.getHeight(), matrix, true));
		matrix.reset();
		text.setText(degrees + "/" + (max * incresment));
	}
	
	public int getProgress(){
		
		return this.progress * incresment;
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
