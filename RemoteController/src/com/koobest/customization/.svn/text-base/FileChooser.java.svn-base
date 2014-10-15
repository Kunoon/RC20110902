package com.koobest.customization;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.AttributeSet;

public class FileChooser extends Preference implements OnPreferenceClickListener,OnPreferenceChangeListener{
	
	private final int STYLE_DROPDOWN = 17367116;
	
	Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			System.out.println(msg.obj.toString());
			FileChooser.this.persistString(msg.obj.toString());
			return true;
		}
	});
	
	DirManager dm;
	String defValue = null;
	
	public FileChooser(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		dm = new DirManager(context, mHandler);
		this.setOnPreferenceClickListener(this);
		this.setSelectable(true);
		this.setPersistent(true);
		this.setWidgetLayoutResource(STYLE_DROPDOWN);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
//		System.out.println(preference.getKey());
		dm.showPop();
		return true;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		System.out.println("changed");
		return false;
	}

}
