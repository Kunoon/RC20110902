package com.koobest.customization;

import com.koobest.dialog.DialogExtension;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekBarPreference extends Preference implements OnPreferenceClickListener, OnClickListener, OnSeekBarChangeListener{
	private String namespace = "http://schemas.android.com/apk/res/android";
	private String koobestns = "http://schemas.android.com/apk/res/com.koobest";
	
	private LayoutInflater inflater;
	private Context mContext;
	private SeekBar seekbar = null;
	private TextView seekbar_text = null;
	private DialogExtension.MyBuilder mDialog;
	
	private int max = 0,min = 0,defaultProgress = 0,configValue = 0;
	private String title = null;
	private String message = null;
	
	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		

		this.setOnPreferenceClickListener(this);
		
		
		inflater = LayoutInflater.from(context);
		
		if(this.isEnabled())
			this.setWidgetLayoutResource(com.koobest.R.layout.seekbar_preference_layout);
		else
			this.setWidgetLayoutResource(com.koobest.R.layout.seek_preference_layout_unenable);
		
		
		this.mContext = context;
//		TypedArray attrsArray = context.obtainStyledAttributes(attrs, com.koobest.R.styleable.SeekBarPreference);
		
		max = attrs.getAttributeIntValue(namespace, "max", 1);
		min = attrs.getAttributeIntValue(koobestns, "min",1);
//		
//		System.out.println(this.getKey() + " " + min);
		
		configValue = defaultProgress = attrs.getAttributeIntValue(namespace, "defaultValue", 50);
		String[] separate = null;
		separate = attrs.getAttributeValue(namespace, "title").split("@");
		if(separate.length != 1){
			title = context.getResources().getString(Integer.parseInt(separate[1]));
		}else {
			title = separate[0];
		}
		
//		String msg = attrsArray.getString(com.koobest.R.styleable.SeekBarPreference_message);
		
		String msg = attrs.getAttributeValue(koobestns, "message");
		if(msg != null){
			separate = msg.split("@");
		if(separate.length != 1){
			message = context.getResources().getString(Integer.parseInt(separate[1]));
		}else{
			message = separate[0];
		}
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		mDialog = new DialogExtension.MyBuilder(mContext);
		
		View seekbar_pre = inflater.inflate(com.koobest.R.layout.seekbar_preference, null);
		
		defaultProgress = this.getPersistedInt(-1);
		if(defaultProgress == -1)
			defaultProgress = configValue;
		
		seekbar = (SeekBar) seekbar_pre.findViewById(com.koobest.R.id.seekbar_preference);
		seekbar.setMax(max);
		seekbar.setProgress(defaultProgress);
		
		seekbar.setOnSeekBarChangeListener(this);
		
		seekbar_text = (TextView) seekbar_pre.findViewById(com.koobest.R.id.seekbar_preference_text);
		seekbar_text.setText(this.defaultProgress + "/" + this.max);
		
		Button confirm = (Button) seekbar_pre.findViewById(com.koobest.R.id.seekbar_preference_confirm_btn);
		confirm.setOnClickListener(this);
		
		Button cancel = (Button) seekbar_pre.findViewById(com.koobest.R.id.seekbar_preference_cancel_btn);
		cancel.setOnClickListener(this);
		
		mDialog.setTitle(title).setMessage(message).addView(seekbar_pre).show();
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case com.koobest.R.id.seekbar_preference_confirm_btn:
			
			if(shouldPersist()){
				this.persistInt(seekbar.getProgress());
			}
			mDialog.dismiss();
			break;
		
		case com.koobest.R.id.seekbar_preference_cancel_btn:
			mDialog.dismiss();
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if(progress < min){
			seekBar.setProgress(min);
			progress = min;
		}
		
		this.seekbar_text.setText(progress + "/" + this.max);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void notifyChanged() {
		// TODO Auto-generated method stub
		if(this.isEnabled())
			this.setWidgetLayoutResource(com.koobest.R.layout.seekbar_preference_layout);
		else
			this.setWidgetLayoutResource(com.koobest.R.layout.seek_preference_layout_unenable);
		super.notifyChanged();
	}
	
	
}
