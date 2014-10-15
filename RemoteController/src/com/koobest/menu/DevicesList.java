package com.koobest.menu;

import java.util.ArrayList;
import java.util.List;

import com.koobest.R;
import com.koobest.RemoteControllerMain;
import com.koobest.dialog.DialogExtension;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class DevicesList extends Activity {
	
	// Intent返回值
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
	private SharedPreferences sp;
	private BluetoothAdapter myAdapter = null;
	private List<String> devicesList = new ArrayList<String>();
	private ArrayAdapter<String> devicesArrayAdapter = null;
	private ListView devicesListView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置窗口
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.remote_devices_list);
		getWindow().setBackgroundDrawableResource(R.drawable.bgconfig);
		setTitle(R.string.tip_BT_SearchDevices);
		
		// 创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // 注册广播接收器
        this.registerReceiver(myBroadcastReceiver, filter);
        // 创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_DISCOVERY_FINISHED
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 注册广播接收器
        this.registerReceiver(myBroadcastReceiver, filter);
		
		sp = this.getSharedPreferences("com.koobest_preferences", 0);
		myAdapter = BluetoothAdapter.getDefaultAdapter();
		
		devicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devicesList);
		devicesListView = (ListView)findViewById(R.id.devicesListView);
		devicesListView.setAdapter(devicesArrayAdapter);
		
		devicesListView.setOnItemClickListener(myDeviceClickListener);
		devicesListView.setOnItemLongClickListener(myDeviceLongClickListener);
		
		DoDiscovery();
		
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
	
	/**
	 * 监听ListView
	 */
    private OnItemClickListener myDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        	// 取消搜索
            myAdapter.cancelDiscovery();

         // 获取蓝牙MAC地址
            String info = ((TextView) v).getText().toString();
            if(!info.equals(getResources().getString(R.string.tip_MyBuilderDialog_BT_Submenu_NotFoundDevice))) {
            	String address = info.substring(info.length() - 17);
            	RemoteControllerMain.bluetoothServer.ConnectToRemoteDevice(myAdapter.getRemoteDevice(address));
            }
            
            // 关闭Activity
            finish();
        }
    };
	
    private OnItemLongClickListener myDeviceLongClickListener = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
			// 取消搜索
            myAdapter.cancelDiscovery();
            // 获取蓝牙MAC地址
            final String info = devicesList.get(arg2);
            if(!info.equals(getResources().getString(R.string.tip_MyBuilderDialog_BT_Submenu_NotFoundDevice))) {
            	 final String address = info.substring(info.length() - 17);
                 List<String> list = new ArrayList<String>();
                 list.add(getResources().getString(R.string.tip_BT_SetOtherName));
                 list.add(getResources().getString(R.string.tip_BT_Delete));
                 list.add(getResources().getString(R.string.tip_BT_DeleteAll));
                 ArrayAdapter<String> defineMenuAdapter = new ArrayAdapter<String>(DevicesList.this, android.R.layout.simple_list_item_1, list);
                 View view = LayoutInflater.from(DevicesList.this).inflate(R.layout.define_device, null);
                 ListView defineListView = (ListView)view.findViewById(R.id.defineListView);
                 defineListView.setAdapter(defineMenuAdapter);
                 final DialogExtension defineMenuDialog = new DialogExtension(DevicesList.this);
                 defineMenuDialog.setTitle(info);
                 defineMenuDialog.getWindow().setBackgroundDrawableResource(R.drawable.bgconfig);
                 defineMenuDialog.setContentView(view);
                 defineMenuDialog.show();
                 
                 defineListView.setOnItemClickListener(new OnItemClickListener() {
     				@Override
     				public void onItemClick(AdapterView<?> arg0, View arg1, int menuNum, long arg3) {
     					defineMenuDialog.dismiss();
     					switch (menuNum) {
     					case 0:
     						// 取消搜索
     			            myAdapter.cancelDiscovery();
     			            // 获取蓝牙MAC地址
//     			            String info = devicesList.get(arg2);
//     			            final String address = info.substring(info.length() - 17);
     			            
     			            View view = LayoutInflater.from(DevicesList.this).inflate(R.layout.rename_device, null);
     			            final EditText newDeviceNameEditText = (EditText)view.findViewById(R.id.renameDeviceEditText);
     			            final Button confirmButton = (Button)view.findViewById(R.id.confirmButton);
     			            final Button cancelButton = (Button)view.findViewById(R.id.cancelButton);
     			            final DialogExtension tmpDialog = new DialogExtension(DevicesList.this);
     			            tmpDialog.setTitle(R.string.tip_BT_SetOtherName);
     			            tmpDialog.getWindow().setBackgroundDrawableResource(R.drawable.bgconfig);
     			            tmpDialog.setContentView(view);
     			            confirmButton.setOnClickListener(new OnClickListener() {
     							@Override
     							public void onClick(View arg0) {
     								System.out.println(newDeviceNameEditText.getEditableText());
     			            		sp.edit().putString(address, newDeviceNameEditText.getEditableText().toString()).commit();
     			            		String btState;
     			            		if(myAdapter.getRemoteDevice(address).getBondState() == BluetoothDevice.BOND_BONDED) {
     			            			btState = getResources().getString(R.string.tip_BT_Paired);
     			            		} else {
     			            			btState = getResources().getString(R.string.tip_BT_Unpair);
     			            		}
     			            		devicesList.set(arg2, newDeviceNameEditText.getEditableText().toString() + btState + "\n" + address);
     			            		devicesArrayAdapter.notifyDataSetChanged();
     			            		tmpDialog.dismiss();
     							}
     						});
     			            cancelButton.setOnClickListener(new OnClickListener() {
     							@Override
     							public void onClick(View arg0) {
     								tmpDialog.dismiss();
     							}
     						});
     			            tmpDialog.show();
     						break;
     					case 1:
     						devicesList.remove(arg2);
     						sp.edit().remove(address).commit();
     						devicesArrayAdapter.notifyDataSetChanged();
     						break;
     					case 2:
     						devicesList.clear();
     						sp.edit().clear().commit();
     						devicesArrayAdapter.notifyDataSetChanged();
     						break;

     					default:
     						break;
     					}
     				}
     			});
            }
			return false;
		}
	};
    
	/**
	 * 开启本地蓝牙的搜索功能
	 */
    private void DoDiscovery() {
        setProgressBarIndeterminateVisibility(true);
        // 如在本地蓝牙正在搜索，则停止它
        if (myAdapter.isDiscovering()) {
        	myAdapter.cancelDiscovery();
        }
        // 开启蓝牙搜索
        myAdapter.startDiscovery();
    }
    
	/**
	 * 记录搜索到的蓝牙设备
	 */
    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            // 找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	// 从Intent获取BluetoothDevice对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果设备已配对则跳过，否则记录
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	devicesList.add(device.getName() + getResources().getString(R.string.tip_BT_Unpair) + "\n" + device.getAddress());
                	devicesArrayAdapter.notifyDataSetChanged();
                }else if(device.getBondState() == BluetoothDevice.BOND_BONDED && sp.getString(device.getAddress(), "").trim().length() != 0) {
                	devicesList.add(device.getName() + getResources().getString(R.string.tip_BT_Paired) + "\n" + device.getAddress());
                	devicesArrayAdapter.notifyDataSetChanged();
                }
            // 搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	// 设置ProgressBar为不可见
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.btConnect_title);
                if (devicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.btConnect_nonePaired).toString();
                    devicesList.add(noDevices);
                }
            }
        }
    };
}
