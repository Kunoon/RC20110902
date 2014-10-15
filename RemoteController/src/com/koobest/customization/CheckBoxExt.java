package com.koobest.customization;

import android.content.Context;
import android.graphics.Color;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckBoxExt extends CheckBoxPreference{
	String androidNamespace = "http://schemas.android.com/apk/res/android";
	boolean defaultValue = false;
	public CheckBoxExt(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected View onCreateView(ViewGroup parent) {
		// TODO Auto-generated method stub
		if(this.isChecked()){
			if(this.isEnabled())
				this.setWidgetLayoutResource(com.koobest.R.layout.checkbox_on);
			else
				this.setWidgetLayoutResource(com.koobest.R.layout.checkbox_on_unenable);
		} else {
			this.setWidgetLayoutResource(com.koobest.R.layout.checkbox_off);	
		}
		return super.onCreateView(parent);
	}
	
	
	

}
