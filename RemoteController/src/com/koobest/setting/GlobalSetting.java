package com.koobest.setting;

import java.util.ArrayList;
import java.util.List;

import com.koobest.R;
import com.koobest.RemoteControllerMain;
import com.koobest.constant.ConfigConstant;
import com.koobest.customization.CheckBoxExt;
import com.koobest.customization.SeekBarPreference;
import com.koobest.menu.DevicesList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Window;

public class GlobalSetting extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	SharedPreferences prefs;
	SeekBarPreference sbp;

	/***************************************************************************************
	 * Yongkun
	 */
	SharedPreferences sp;
	public static final int MESSAGE_BLUETOOTH_STATE = 0;

	private List<String> devicesList = new ArrayList<String>();

	private BluetoothAdapter myAdapter = BluetoothAdapter.getDefaultAdapter();
	// 声明广播接收器
	private IntentFilter myIntentFilter = null;
	private CheckBoxExt btEnableCheckBoxPreference = null;
	private CheckBoxExt btRoleCheckBoxPreference = null;
	private CheckBoxExt auto_send = null;
	
	private Preference btVisiblePreference = null;
	private Preference btSearchPreference = null;

	private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			HandleStateChanged(btState);
		}
	};

	/**************************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.getListView().setBackgroundResource(R.xml.preference_bg);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);
		this.addPreferencesFromResource(R.xml.global_preference);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		prefs.registerOnSharedPreferenceChangeListener(this);

		/***************************************************************************************
		 * Yongkun
		 */
		sp = this.getSharedPreferences("com.koobest_preferences", 0);
		// 如存在已配对的蓝牙设备就将其添加到ArrayAdapter
		if(myAdapter.getBondedDevices().size() > 0) {
			for(BluetoothDevice device : myAdapter.getBondedDevices()) {
				if(sp.getString(device.getAddress(), "").trim().length() != 0) {
					devicesList.add(sp.getString(device.getAddress(), "") + getResources().getString(R.string.tip_BT_Paired) + "\n" + device.getAddress());
				}
				
			}
		}
		
		btEnableCheckBoxPreference = (CheckBoxExt) findPreference("bt_enable");
		btRoleCheckBoxPreference = (CheckBoxExt) findPreference("bt_role");
		btVisiblePreference = (Preference) findPreference("bt_visible");
		btSearchPreference = (Preference) findPreference("bt_search");
		btVisiblePreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						MakeBTDiscoverable();
						return false;
					}
				});
		btSearchPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						startActivity(new Intent(GlobalSetting.this, DevicesList.class));
						return false;
					}
				});

		if (!prefs.getBoolean("bt_enable", false)) {
			AboutBt(false);
			btSearchPreference.setWidgetLayoutResource(R.layout.seek_preference_layout_unenable);
		} else {
			btSearchPreference.setWidgetLayoutResource(R.layout.seekbar_preference_layout);
		}
		
		
		if (!prefs.getBoolean("bt_role", false)) {
			btVisiblePreference.setEnabled(false);
		}
		/**************************************************************************************/
		sbp = (SeekBarPreference) findPreference("vibrator_strength");

		if (!prefs.getBoolean("vibrator", false))
			sbp.setEnabled(false);
		
		auto_send = (CheckBoxExt) findPreference("error_reporter_auto_send");
	}

	/***************************************************************************************
	 * Yongkun
	 */
	@Override
	protected void onResume() {
		super.onResume();
		HandleStateChanged(myAdapter.getState());
		// 注册广播接收器
		myIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(myBroadcastReceiver, myIntentFilter);
	}
	@Override
	protected void onStop() {
		// 确定已经结束设备扫描
		if (myAdapter.isDiscovering()) {
        	myAdapter.cancelDiscovery();
        }
		unregisterReceiver(myBroadcastReceiver);
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**************************************************************************************/

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if (key.equals("num_columns")) {
			RemoteControllerMain.isSettingChanged = true;
		} 
		if(key.equals("bottom_adjust")){
			RemoteControllerMain.isSettingChanged = true;
		}
		if(key.equals("habit_recorder")){
			ConfigConstant.HABIT_RECORDER = sharedPreferences.getBoolean(key, false);
			RemoteControllerMain.isSettingChanged = true;
		}
		if(key.equals("error_reporter")){
			ConfigConstant.ERROR_START = sharedPreferences.getBoolean(key, false);
			auto_send.setEnabled(ConfigConstant.ERROR_START);
			if(!ConfigConstant.ERROR_START)
				auto_send.setChecked(false);
		}
		if(!sharedPreferences.getBoolean("vibrator", false))
			sbp.setEnabled(false);
		else
			sbp.setEnabled(true);
		/***************************************************************************************
		 * Yongkun
		 */
		if (sharedPreferences.getBoolean("bt_enable", false)) {
			myAdapter.enable();
			btSearchPreference.setWidgetLayoutResource(R.layout.seekbar_preference_layout);
		} else {
			myAdapter.disable();
			btSearchPreference.setWidgetLayoutResource(R.layout.seek_preference_layout_unenable);
		}
		if (sharedPreferences.getBoolean("bt_role", false)) {
			btVisiblePreference.setEnabled(true);
//			Toast.makeText(GlobalSetting.this, R.string.tip_BT_RoleMark, Toast.LENGTH_SHORT).show();
//			if(RemoteControllerMain.bluetoothServer != null) {
//				RemoteControllerMain.bluetoothServer.StartServer();
//			}
		} else {
			btVisiblePreference.setEnabled(false);
//			Toast.makeText(GlobalSetting.this, R.string.tip_BT_RoleMark, Toast.LENGTH_SHORT).show();
//			if(RemoteControllerMain.bluetoothServer != null) {
//				RemoteControllerMain.bluetoothServer.StopService();
//			}
		}
		
		/**************************************************************************************/
	}

	/***************************************************************************************
	 * Yongkun
	 */
	private void HandleStateChanged(int btState) {
		switch (btState) {
		case BluetoothAdapter.STATE_TURNING_ON:
			btEnableCheckBoxPreference
					.setSummary(R.string.tip_Preference_BT_Enabling);
			btEnableCheckBoxPreference.setEnabled(false);

			AboutBt(false);
			break;
		case BluetoothAdapter.STATE_ON:
			btEnableCheckBoxPreference.setChecked(true);
			btEnableCheckBoxPreference.setSummary(null);
			btEnableCheckBoxPreference.setEnabled(true);
			
			AboutBt(true);
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:
			btEnableCheckBoxPreference
					.setSummary(R.string.tip_Preference_BT_Disabling);
			btEnableCheckBoxPreference.setEnabled(false);

			AboutBt(false);
			break;
		case BluetoothAdapter.STATE_OFF:
			btEnableCheckBoxPreference.setChecked(false);
			btEnableCheckBoxPreference
					.setSummary(R.string.tip_Preference_BT_Default);
			btEnableCheckBoxPreference.setEnabled(true);

			AboutBt(false);
			break;
		default:
			break;
		}
	}

	private void AboutBt(boolean bool) {
		btVisiblePreference.setEnabled(bool);
		btRoleCheckBoxPreference.setEnabled(bool);
		btSearchPreference.setEnabled(bool);
		if (!prefs.getBoolean("bt_role", false)) {
			btVisiblePreference.setEnabled(false);
		}
	}

	/**
	 * 设置本地蓝牙设备为可发现状态(Set Bluetooth Discoverable)
	 */
	private void MakeBTDiscoverable() {
		if (myAdapter.getState() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoveryIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoveryIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
			startActivity(discoveryIntent);
		}
	}

	/**************************************************************************************/

}
