package com.koobest.setting;

import com.koobest.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Window;

public class ControllerSetting extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.getListView().setBackgroundResource(R.xml.preference_bg);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);
		this.addPreferencesFromResource(R.xml.controller_preference);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		this.getListView().setBackgroundResource(R.xml.preference_bg);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}
}
