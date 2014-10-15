package com.koobest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.xml.sax.SAXException;

import com.koobest.adapter.RCInformationAdapter;
import com.koobest.adapter.RCsAdapter;
import com.koobest.adapter.ResAdapter;
import com.koobest.bluetooth.BluetoothConstant;
import com.koobest.bluetooth.BluetoothServerT;
import com.koobest.constant.ConfigConstant;
import com.koobest.constant.URLConstant;
import com.koobest.customization.DirManager;
import com.koobest.customization.MyGridView;
import com.koobest.customization.PreImageView;
import com.koobest.customization.SpinnerExt;
import com.koobest.database.UpdateInfo;
import com.koobest.dialog.DialogExtension;
import com.koobest.dialog.DialogExtension.MyBuilder;
import com.koobest.dialog.DialogExtension.MyBuilder.onDialogButtonClick;
import com.koobest.dialog.DialogExtension.MyBuilder.onDialogItemClick;
import com.koobest.menu.DevicesList;
import com.koobest.menu.DialogMenu;
import com.koobest.menu.DialogMenu.MyBuilder.OnItemClick;
import com.koobest.parse.BitmapManager;
import com.koobest.parse.DomParse;
import com.koobest.parse.FileManager;
import com.koobest.reporter.CustomException;
import com.koobest.reporter.ErrorReporter;
import com.koobest.setting.GlobalSetting;
import com.koobest.socket.HttpConnection;
import com.koobest.watcher.MemoryWatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.os.Handler.Callback;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RemoteControllerMain extends Activity {
	/** Called when the activity is first created. */

	/***************************************************************************************************
	 * @author Yongkun
	 */
	// private boolean foundMark = true;
	private int devicesNum = 0;
	private final int DIALOG_EXIT = 1;
	private final int DIALOG_SEND_ERROR_MSG = 13;
	private final int ADD_RC_BY_INTERNET = 11;
	private final int ADD_CUSTOMIZATION = 12;
	// 蓝牙通信管理服务(Bluetooth Communication Server)
	public static BluetoothServerT bluetoothServer;
	public static String receiveITCode = "";

	// 更新DB
	private boolean FIRST_SEARCH = true;
	private boolean SHALL_SEARCH = false;

	private NotificationManager myNotificationManager;
	private String connectedDeviceAdd;

	private List<BluetoothDevice> pairedDevicesList = new ArrayList<BluetoothDevice>();

	// 声明广播接收器
	private IntentFilter myIntentFilter = null;

	// private TextView connectStateTitleTextView;
	private BluetoothAdapter bluetoothAdapter = null;
	/**************************************************************************************************/
	private ProgressDialog waiting_dialog = null;
	private HttpConnection con;
	private FileManager fm;
	private File[] files;
	private Bitmap icon;
	private HashMap<Integer, String[]> map;
	private ImageAdapter iAdapter;
	private ImageView shelf_head;
	private LayoutInflater i;
	private String[] choices = null;
	private final int OPTION_MODIFY_BG = 0;
	private final int OPTION_MODIFY_DESC = 1;
	private final int OPTION_DELETE = 2;
	private final int OPTION_POSTATE = 3;

	private boolean sdOK = true;
	// message arg1
	private final int RCLIST_MSG = 15;
	private final int RC_CONFIG_DOWNLOADED = 16;
	private int MSG_EXCEPTION = -1;
	private int NOT_FOUND = 404;
	private int CHANGE_RCICON_MSG = 21;

	private View layout;

	private DirManager dm;
	private DomParse dp;

	private MemoryWatcher mw = null;
	private MyGridView gv = null;
	private String subTagName = null, currentFilePath = null;

	public static Resources res;
	public static boolean isSettingChanged = false;

	public static int VERTICALSPACING = 35;
	public static int HORIZAONTALSPACING = 5;
	private int NUMCOLUMNS = 2;
	private int PADDINGLEFT = 15;
	private int PADDINGRIGHT = 15;

	// MENU SETTING
	private final int GLOBAL_SETTING_GROUP = 0;
	private final int SETTING_ID = 1;
	private final int ADD_RC_ID = 2;
	private final int CHOICE_BG_ID = 3;
	private final int ABOUT_GROUP = 2;
	private final int ABOUT_ID = 10;

	public static int singleIconWH = 0, minuHeight = 0, numRC = 0;
	//	
	// 振动提示
	private Vibrator vibrator;
	SharedPreferences sp;

	WindowManager wm;
	// Update
	UpdateInfo ui = null;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.obj == null && msg.arg1 == RC_CONFIG_DOWNLOADED) {
				waiting_dialog.dismiss();
				try {
					files = fm.getFilteredFiles(ConfigConstant.CONFIGPATH,
							".xml");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(RemoteControllerMain.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return false;
				}
				iAdapter.map4adapter = fm.getFilesPathAndName(files);
				iAdapter.clearCache();
				iAdapter.notifyDataSetChanged();
				Toast
						.makeText(
								RemoteControllerMain.this,
								res.getString(R.string.rc_tip_save_cfg)
										+ ConfigConstant.CONFIGPATH,
								Toast.LENGTH_SHORT).show();
				return true;
			}

			if (msg.obj != null && msg.arg1 == RCLIST_MSG) {
				Dialog dialog = (Dialog) msg.obj;
				dialog.show();
				waiting_dialog.dismiss();
				return true;
			}

			if (dp != null && subTagName != null && msg.obj != null && msg.arg1 == CHANGE_RCICON_MSG) {
				System.out.println("msg idenfy: " + msg.arg1);
				File temp = new File(msg.obj.toString());
				String name = temp.getName();

				try {
					BitmapManager.savePostRotatedBitmap(msg.obj.toString(),
							ConfigConstant.CONFIGPATH + "/pngcache", 0, 1,
							minuHeight);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dp.createElementWithUpdate("shelf", subTagName, null,
						ConfigConstant.CONFIGPATH + "/pngcache/" + name, null,
						true);

				iAdapter.clearCache();
				iAdapter.notifyDataSetChanged();
				
			}

			if (msg.arg1 == CHANGE_RCICON_MSG) {
				Display d = getWindowManager().getDefaultDisplay();
				WindowManager.LayoutParams p = getWindow().getAttributes();
				p.height = (int) d.getHeight();
				p.width = (int) d.getWidth();
				p.alpha = 1.0f;
				getWindow().setAttributes(p);
				return true;
			}

			// ERROR HANDLER
			if (msg.arg1 == MSG_EXCEPTION) {

				if (msg.obj != null) {
					Toast.makeText(RemoteControllerMain.this,
							msg.obj.toString(), Toast.LENGTH_SHORT).show();
				}

				waiting_dialog.dismiss();
				return true;
			}

			if (msg.arg1 == ErrorReporter.sendByNet) {
				Toast.makeText(
						RemoteControllerMain.this,
						RemoteControllerMain.this.getResources().getString(
								R.string.reporter_sending), Toast.LENGTH_SHORT)
						.show();
				return true;
			}

			if (msg.arg1 == ErrorReporter.sendFailed) {
				Toast
						.makeText(
								RemoteControllerMain.this,
								RemoteControllerMain.this.getResources()
										.getString(R.string.reporter_sent_fail)
										+ ConfigConstant.CONFIGPATH,
								Toast.LENGTH_SHORT).show();
				return true;
			}

			if (msg.arg1 == NOT_FOUND) {
				waiting_dialog.dismiss();
				Toast.makeText(RemoteControllerMain.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				return true;
			}
			return false;
		}
	});

	Handler mwHandler;
	HandlerThread h_thread;
	Matrix matrix;

	// The Resource for the shelf
	private int[] shelfBackInner = { R.drawable.anew };
	private int[] shelfFrontInner = { R.drawable.shelf };

	private String[] shelfBackOuter = null;
	private String[] shelfFrontOuter = null;
	private com.koobest.menu.DialogMenu.MyBuilder md;
	private Handler uphandler;
	private Handler updateHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = this.getSharedPreferences("com.koobest_preferences", 0);
		ConfigConstant.SYS_USER_EMAIL = sp.getString("user_email", "");
		ConfigConstant.HABIT_RECORDER = sp.getBoolean("habit_recorder", false);
		ConfigConstant.ERROR_START = sp.getBoolean("error_reporter", false);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// SD卡状态检测
		if (!Environment.getExternalStorageDirectory().canRead()
				|| Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED_READ_ONLY)
				|| Environment.getExternalStorageState().equals(
						Environment.MEDIA_UNMOUNTED)) {

			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.error_sdcard_unmounted),
					Toast.LENGTH_LONG).show();
			// finish();
			setContentView(R.layout.unmounted_sd_layout);
			sdOK = false;
			return;
		}

		// 获取Option选项
		choices = getResources().getStringArray(R.array.rcm_option);
		// 获取配置文件路径
		// String mainConfigPath = ConfigConstant.MAINCFGFILEPATH;

		try {
			File sfile = new File(ConfigConstant.MAINCFGFILEPATH);
			if (!sfile.exists())
				DomParse.createNewXml(ConfigConstant.MAINCFGFILEPATH, "shelf",
						null, 0);

			dm = new DirManager(this, this.mHandler);

			dp = new DomParse(ConfigConstant.MAINCFGFILEPATH);
			dp.setWritedFilePath(ConfigConstant.MAINCFGFILEPATH);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
			dp = null;
			e.printStackTrace();
			// finish();

		} catch (NullPointerException e) {
			// 把文件浏览根目录
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
			System.out.println(e.getMessage());
			dm = null;
			finish();
		} catch (Exception e) {

			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
			dp = null;
		}

		if (dm == null) {

			return;
		}
		// Socket
		con = new HttpConnection();
		con.setParams(URLConstant.SCHEME, URLConstant.HOST, URLConstant.PORT,
				URLConstant.PATH);
		ui = new UpdateInfo(RemoteControllerMain.this, con);

		// 更新数据库
		HandlerThread hThread = new HandlerThread("sql_update");
		hThread.start();

		updateHandler = new Handler(hThread.getLooper(),
				new Callback() {

					@Override
					public boolean handleMessage(Message msg) {
						// TODO Auto-generated method stub
						if(ui.shallUpdate())
						{
							waiting_dialog.dismiss();
							System.out.println("shall update");
							DialogExtension.MyBuilder update_dialog = new MyBuilder(RemoteControllerMain.this);
							update_dialog.setTitle(R.string.hint);
							update_dialog.setMessage(R.string.update_message);
							update_dialog.setButton(R.string.confirm, new onDialogButtonClick(){

								@Override
								public void onClick(
										View v) {
									// TODO Auto-generated method stub
									super.onClick(v);
									waiting_dialog.show();

									uphandler.sendEmptyMessage(22);
								}
								
							});
							update_dialog.setButton(R.string.cancel, null);
							update_dialog.show();
						}
						else
							waiting_dialog.dismiss();
						
						waiting_dialog.setTitle("Please wait");
						return false;
					}
				});
		HandlerThread ut = new HandlerThread("update params");
		ut.start();
		uphandler = new Handler(ut.getLooper(), new Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				ui.updateVersion();
				waiting_dialog.dismiss();
				return false;
			}
		});
		
		waiting_dialog = new ProgressDialog(this);

		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		matrix = new Matrix();

		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		singleIconWH = (metrics.widthPixels - (PADDINGLEFT + PADDINGRIGHT) - (NUMCOLUMNS - 1)
				* HORIZAONTALSPACING)
				/ NUMCOLUMNS;

		fm = new FileManager(this);

		String resPath = ConfigConstant.SHELFRESOURCE;

		try {
			files = fm.getFilteredFiles(ConfigConstant.CONFIGPATH, ".xml");

			File resDir = new File(resPath);
			if (!resDir.exists())
				FileManager.creatNewFileInSdcard(resPath);

			File[] res = fm.getFilteredFiles(resPath, ".bg");
			shelfBackOuter = new String[res.length];
			int i = 0;
			for (File file : res) {
				shelfBackOuter[i] = file.getPath();
				i++;
			}

			res = fm.getFilteredFiles(resPath, ".fnt");
			shelfFrontOuter = new String[res.length];
			i = 0;
			for (File file : res) {
				shelfFrontOuter[i] = file.getPath();
				i++;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
			finish();
			return;

		} catch (IOException e) {
			System.out.println(e.getMessage());
			finish();
			return;
		}

		map = fm.getFilesPathAndName(files);

		numRC = map.size();

		

		
		/***************************************************************************************************
		 * @author Yongkun
		 */
		// 设置窗口布局(Window Layout)

		setContentView(R.layout.main);

		res = this.getResources();

		// 更新数据库，发送错误报告
		if (HttpConnection.hasInternet(this)) {
//			Message mes = updateHandler.obtainMessage();
//			System.out.println("update db");
//			updateHandler.sendMessage(mes);

			if (ConfigConstant.ERROR_START) {
				File errorfile = new File(ConfigConstant.ERRORLOGPATH);
				if (errorfile.exists()
						&& !sp.getBoolean("error_reporter_auto_send", false)) {
					showDialog(DIALOG_SEND_ERROR_MSG);
				} else if (errorfile.exists()
						&& sp.getBoolean("error_reporter_auto_send", false)) {
					ErrorReporter.sendErrorFile(null);
				}

			}
		} else {
			Toast.makeText(this, res.getString(R.string.error_no_internet),
					Toast.LENGTH_LONG).show();
		}

		myNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		myNotificationManager.cancel(ConfigConstant.NOTIFICATION_ID);
		showNotification(R.drawable.tip_icon, getResources().getString(
				R.string.tip_Program_Starting), "", new Intent(
				getApplicationContext(), RemoteControllerMain.class), false,
				ConfigConstant.NOTIFICATION_ID);

		// 获取本地蓝牙设备(Get Local BluetoothAdapter)
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// 如果本地蓝牙不存在，则关闭程序(If the local bluetoothAdapter is not exist, the
		// program will finish.)
		if (bluetoothAdapter == null) {
			Toast.makeText(this, R.string.tip_NoBluetooth, Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		} else {
			// 如果蓝牙设备处于关闭状态，则开启蓝牙(if the bluetoothAdapter is closed, it will be
			// opened)
			if (!bluetoothAdapter.isEnabled()
					&& !sp.getBoolean("bt_auto", false)) {
				new DialogExtension.MyBuilder(this).setTitle(R.string.tip)
						.setMessage(R.string.tip_Connection_confirm).setButton(
								R.string.confirm, new onDialogButtonClick() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										super.onClick(v);
										bluetoothAdapter.enable();
										myNotificationManager
												.cancel(ConfigConstant.NOTIFICATION_ID);
										showNotification(
												R.drawable.tip_icon,
												getResources()
														.getString(
																R.string.tip_BT_Enabling),
												"",
												new Intent(
														getApplicationContext(),
														RemoteControllerMain.class),
												false,
												ConfigConstant.NOTIFICATION_ID);
									}

								}).setButton(R.string.cancel, null).show();
			} else if (!bluetoothAdapter.isEnabled()
					&& sp.getBoolean("bt_auto", false)) {
				bluetoothAdapter.enable();
				myNotificationManager.cancel(ConfigConstant.NOTIFICATION_ID);
				showNotification(R.drawable.tip_icon, getResources().getString(
						R.string.tip_BT_Enabling), "", new Intent(
						getApplicationContext(), RemoteControllerMain.class),
						false, ConfigConstant.NOTIFICATION_ID);
			} else if (bluetoothAdapter.isEnabled()) {
				if (bluetoothServer == null) {
					SetupBluetoothServer();
				}
			}
		}

		NUMCOLUMNS = sp.getInt("num_columns", 2);

		// /The Memory Watcher

		// h_thread = new HandlerThread("handler_thread");
		// h_thread.start();
		//        
		// mwHandler = new Handler(h_thread.getLooper(),new Handler.Callback() {
		//			
		// @Override
		// public boolean handleMessage(Message arg0) {
		// // TODO Auto-generated method stub
		//
		// Toast.makeText(getApplicationContext(), arg0.obj.toString() + "kb",
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }
		// });
		//        
		// mw = new MemoryWatcher(mwHandler);

		// mwHandler.post(mw);

		// The Memory Watcher
		wm = this.getWindowManager();

		i = LayoutInflater.from(this);
		layout = i.inflate(R.layout.empty_view, null);
		layout.setBackgroundColor(Color.WHITE);

		addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		// Handle the bitmap to make it fit any screen
		Bitmap shelf_h = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.shelf_head);
		shelf_h = BitmapManager.zoomBitmap(shelf_h, 0, this.getWindowManager()
				.getDefaultDisplay().getWidth());
		shelf_head = (ImageView) this.findViewById(R.id.shelf_head);
		shelf_head.setImageBitmap(shelf_h);

		int notify_height = metrics.heightPixels * (25 / 480);
		minuHeight = (int) ((float) (metrics.heightPixels - shelf_h.getHeight() - notify_height) / 3f);

		gv = (MyGridView) findViewById(R.id.mygridview);

		gv.setNumColumns(NUMCOLUMNS);
		gv.setDrawingCacheEnabled(true);
		gv.setVerticalFadingEdgeEnabled(false);
		gv.setPadding(PADDINGLEFT, 0, PADDINGRIGHT, 0);
		gv.setCacheColorHint(Color.TRANSPARENT);
		gv.setSelector(R.xml.shelf_item_background);
		gv.setHorizontalSpacing(HORIZAONTALSPACING);
		gv.setVerticalSpacing(VERTICALSPACING);
		gv.setEmptyView(layout);

		// 初始化适配器
		iAdapter = new ImageAdapter(this, map);
		gv.setAdapter(iAdapter);

	}

	/***************************************************************************************************
	 * @author Yongkun
	 */

	@Override
	protected void onResume() {
		try {
			dp = new DomParse(ConfigConstant.MAINCFGFILEPATH);
			System.out.println("RCM 1793:ReRead dp");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("RCM 1796 Message " + e.getMessage());
		}
		
		super.onResume();
		// 注册广播接收器
		myIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(BTStartListenBroadcastReceiver, myIntentFilter);
		// 创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_FOUND
		myIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(FoundBTDevicesListenBroadcastReceiver,
				myIntentFilter);
		// 创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_DISCOVERY_FINISHED
		myIntentFilter = new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(FoundBTDevicesListenBroadcastReceiver,
				myIntentFilter);

		myIntentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		this.registerReceiver(BTConnectListenBroadcastReceiver, myIntentFilter);
		myIntentFilter = new IntentFilter(
				BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		this.registerReceiver(BTConnectListenBroadcastReceiver, myIntentFilter);
		myIntentFilter = new IntentFilter(
				BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.registerReceiver(BTConnectListenBroadcastReceiver, myIntentFilter);

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (sdOK) {
			if (bluetoothAdapter != null) {
				bluetoothAdapter.cancelDiscovery();
			}
			// 注销用于监听蓝牙扫描的广播接收器
			unregisterReceiver(FoundBTDevicesListenBroadcastReceiver);
			// foundMark = false;
			myNotificationManager
					.cancel(ConfigConstant.NOTIFICATION_NEWDEVICES_ID);
		}
	}

	/**************************************************************************************************/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		System.out.println("creat menu");
		// TODO Auto-generated method stub
		menu.add(GLOBAL_SETTING_GROUP, SETTING_ID, 1, R.string.menu_setting)
				.setIcon(R.drawable.menu_config);
		menu.add(GLOBAL_SETTING_GROUP, ADD_RC_ID, 0, R.string.menu_add_rc)
				.setIcon(R.drawable.menu_add);
		menu
				.add(GLOBAL_SETTING_GROUP, CHOICE_BG_ID, 2,
						R.string.menu_choice_bg).setIcon(R.drawable.change_bg);
		menu.add(ABOUT_GROUP, ABOUT_ID, 3, R.string.menu_about).setIcon(
				R.drawable.about);
		md = new DialogMenu.MyBuilder(RemoteControllerMain.this);
		md.addMenu(SETTING_ID, R.string.menu_setting);
		md.setMenuIcon(SETTING_ID, R.drawable.menu_config);
		md.addSubMenu(ADD_RC_ID, R.string.menu_add_rc);
		md.addSubMenuItem(ADD_RC_ID, ADD_RC_BY_INTERNET,
				R.string.submenu_addfrom_net);
		md.addSubMenuItem(ADD_RC_ID, ADD_CUSTOMIZATION,
				R.string.submenu_addby_diy);
		md.setMenuIcon(ADD_CUSTOMIZATION, R.drawable.add_rc_customization);
		md.setMenuIcon(ADD_RC_BY_INTERNET, R.drawable.add_by_internet);
		md.setSubMenuListner(ADD_RC_ID, new OnItemClick() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (Integer.valueOf(v.getTag().toString())) {

				case ADD_CUSTOMIZATION:
					final EditText et_cfg_des = new EditText(
							RemoteControllerMain.this);
					Random rand = new Random();
					// Create a new random name for the config file
					String randomName = "rc"
							+ String.valueOf(rand.nextInt(1000));
					String cfg_file = ConfigConstant.CONFIGPATH + "/"
							+ randomName + ".xml";

					File temp = new File(cfg_file);
					int i = 0;
					while (temp.exists()) {
						i++;
						randomName = "rc"
								+ String.valueOf(rand.nextInt(1000) + i);
						cfg_file = ConfigConstant.CONFIGPATH + "/" + randomName
								+ ".xml";
						temp = new File(cfg_file);
					}

					// 创建文件
					try {
						HashMap<String, HashMap<String, String>> parent = new HashMap<String, HashMap<String, String>>();
						parent.put("Buttons", null);

						DomParse.createNewXml(cfg_file, "RemoteController",
								parent, 1);

						files = fm.getFilteredFiles(ConfigConstant.CONFIGPATH,
								".xml");

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(getApplicationContext(),
								"Op Failed,the reason is " + e.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
					final String finalName = randomName;
					// Create Description
					new DialogExtension.MyBuilder(RemoteControllerMain.this)
							.setTitle(
									getResources().getString(
											R.string.dialog_add_guide_title))
							.addView(et_cfg_des).setMessage(
									R.string.notice_add_description).setButton(
									R.string.of_course,
									new onDialogButtonClick() {
										@Override
										public void onClick(View arg0) {
											dp.createElementWithUpdate("shelf",
													finalName + "Description",
													null, et_cfg_des.getText()
															.toString(), null,
													true);
											Toast
													.makeText(
															RemoteControllerMain.this,
															R.string.notice_check_identify,
															Toast.LENGTH_LONG)
													.show();

											iAdapter.map4adapter = fm
													.getFilesPathAndName(files);
											iAdapter.clearCache();
											iAdapter.notifyDataSetChanged();
											super.onClick(arg0);
										}
									}).setButton(R.string.do_it_later,
									new onDialogButtonClick() {

										@Override
										public void onClick(View dialog) {
											// TODO Auto-generated method stub
											iAdapter.map4adapter = fm
													.getFilesPathAndName(files);
											iAdapter.clearCache();
											iAdapter.notifyDataSetChanged();
											super.onClick(dialog);
										}
									}).show();
					break;
				case ADD_RC_BY_INTERNET:
										
					RCInformationAdapter info = new RCInformationAdapter(
							RemoteControllerMain.this, con);
					// List<NameValuePair> params = new
					// ArrayList<NameValuePair>();

					View view = LayoutInflater.from(RemoteControllerMain.this)
							.inflate(R.layout.spinner_ext_dialog, null);

					final SpinnerExt spext_mnf = (SpinnerExt) view
							.findViewById(R.id.spext_mnf);
					spext_mnf.setAdapter(info);

					final SpinnerExt spext_type = (SpinnerExt) view
							.findViewById(R.id.spext_type);
					spext_type.setAdapter(info);

					final SpinnerExt spext_model = (SpinnerExt) view
							.findViewById(R.id.spext_model);
					spext_model.setAdapter(info);

					DialogExtension.MyBuilder main_dialog = new DialogExtension.MyBuilder(
							RemoteControllerMain.this);

					main_dialog.setTitle(R.string.socket_main_guide_dialog)
							.setMessage(
									R.string.socket_main_guide_dialog_message)
							.addView(view).setButton(R.string.search,
									new onDialogButtonClick() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub

												final Dialog rclist_dialog = new Dialog(
														RemoteControllerMain.this);
												final List<NameValuePair> rc_params = new ArrayList<NameValuePair>();
												rc_params
														.add(new BasicNameValuePair(
																URLConstant.PREFIX,
																URLConstant.RC_OBTAIN_ROUTE));
												rc_params
														.add(new BasicNameValuePair(
																"manu_name",
																spext_mnf
																		.getChoice()));
												rc_params
														.add(new BasicNameValuePair(
																"cate_name",
																spext_type
																		.getChoice()));
												rc_params
														.add(new BasicNameValuePair(
																"model",
																spext_model
																		.getChoice()));

												waiting_dialog.show();

												Thread thd = new Thread() {

													@Override
													public void run() {
														// TODO Auto-generated
														// method stub
														Looper.prepare();
														Message msg = mHandler
																.obtainMessage();
														ListView rcs_list = new ListView(
																RemoteControllerMain.this);
														try {
															DomParse result = con
																	.getResultsByGet(
																			rc_params,
																			null);
															HashMap<Integer, HashMap<String, String>> resultMap = result
																	.getAllWidgets("controller");
															if (resultMap
																	.size() == 0) {
																msg.arg1 = NOT_FOUND;
																msg.obj = RemoteControllerMain.this
																		.getString(R.string.socket_not_found);
																mHandler
																		.sendMessage(msg);
																return;
															}
															RCsAdapter rcadapter = new RCsAdapter(
																	RemoteControllerMain.this,
																	resultMap);

															rcs_list
																	.setAdapter(rcadapter);
															rcs_list
																	.setCacheColorHint(Color.TRANSPARENT);
															rcs_list
																	.setOnItemClickListener(new OnItemClickListener() {
																		@Override
																		public void onItemClick(
																				AdapterView<?> arg0,
																				View arg1,
																				int arg2,
																				long arg3) {
																			// TODO
																			// Auto-generated
																			// method
																			// stub
																			if (arg1
																					.getTag() == null) {
																				rclist_dialog
																						.dismiss();
																				return;
																			}
																			rclist_dialog
																					.dismiss();

																			rc_params
																					.clear();
																			rc_params
																					.add(new BasicNameValuePair(
																							URLConstant.PREFIX,
																							URLConstant.GETRC_ROUTE));
																			rc_params
																					.add(new BasicNameValuePair(
																							"rc_id",
																							arg1
																									.getTag()
																									.toString()));
																			waiting_dialog
																					.show();
																			// Create
																			// a
																			// name;
																			Random rand = new Random();
																			String randomName = "rc"
																					+ String
																							.valueOf(rand
																									.nextInt(1000));
																			String cfg_file = ConfigConstant.CONFIGPATH
																					+ "/"
																					+ randomName
																					+ ".xml";

																			File temp = new File(
																					cfg_file);
																			int i = 0;
																			while (temp
																					.exists()) {
																				i++;
																				randomName = "rc"
																						+ String
																								.valueOf(rand
																										.nextInt(1000)
																										+ i);
																				cfg_file = ConfigConstant.CONFIGPATH
																						+ "/"
																						+ randomName
																						+ ".xml";
																				temp = new File(
																						cfg_file);
																			}

																			final String finalName = randomName;
																			Thread read_rc = new Thread() {
																				Message msg = mHandler.obtainMessage();

																			@Override
																			public void run() {
																				// TODO
																				// Auto-generated
																				// method
																				// stub
																				try {
																					con.getResultsByGet(rc_params,ConfigConstant.CONFIGPATH+ "/"+ finalName + ".xml");
																					if(con.type != null && dp != null){
																						dp = new DomParse(ConfigConstant.MAINCFGFILEPATH);
																						dp.createElementWithUpdate("shelf", finalName + "type", null, con.type, null, true);
																					}
																						
																				} catch (SAXException saxe) {
																					System.out.println(saxe.getMessage() + " ParseError");
																					msg.arg1 = MSG_EXCEPTION;
																					msg.obj = RemoteControllerMain.this.getResources().getString(R.string.exception_sax) + "_" + saxe.getMessage();
																					mHandler.sendMessage(msg);
																					return;
																				} catch (ParserConfigurationException e) {
																					System.out.println(e.getMessage() + " IOError from rcm");
																					msg.arg1 = MSG_EXCEPTION;
																					msg.obj = RemoteControllerMain.this.getResources().getString(R.string.exception_unknown) + "_" + e.getMessage();
																					mHandler.sendMessage(msg);
																					return;
																				} catch (ConnectTimeoutException e) {
																					System.out.println(e.getMessage() + " ConnectionException from rcm");
																					msg.arg1 = MSG_EXCEPTION;
																					msg.obj = RemoteControllerMain.this.getResources().getString(R.string.exception_time_out) + "_" + e.getMessage();
																					mHandler.sendMessage(msg);
																					return;
																				}  catch (IOException e) {
																						// TODO
																						// Auto-generated
																						// catch
																						// block
																						e
																								.printStackTrace();
																						System.out
																								.println(e
																										.getMessage()
																										+ " from rcm "
																										+ e
																												.getClass()
																												.getName());
																						msg.arg1 = MSG_EXCEPTION;
																						msg.obj = RemoteControllerMain.this
																								.getResources()
																								.getString(
																										R.string.exception_io)
																								+ "_"
																								+ e
																										.getMessage();
																						mHandler
																								.sendMessage(msg);
																						return;

																					} catch (URISyntaxException e) {
																						System.out
																								.println(e
																										.getMessage()
																										+ " URISyn from rcm");
																						msg.arg1 = MSG_EXCEPTION;
																						msg.obj = RemoteControllerMain.this
																								.getResources()
																								.getString(
																										R.string.exception_urisyn)
																								+ "_"
																								+ e
																										.getMessage();
																						mHandler
																								.sendMessage(msg);
																						return;
																					} catch (Exception e) {
																						msg.arg1 = MSG_EXCEPTION;
																						msg.obj = RemoteControllerMain.this
																								.getResources()
																								.getString(
																										R.string.exception_unknown)
																								+ "_"
																								+ e
																										.getMessage();
																						mHandler
																								.sendMessage(msg);
																						System.out
																								.println(e
																										.getMessage()
																										+ " Excpetion from rcm");
																						return;
																					}

																					msg.arg1 = RC_CONFIG_DOWNLOADED;
																					msg.obj = null;
																					mHandler
																							.sendMessage(msg);
																					super.run();
																				}
																				

																			};

																			read_rc.start();
																		}
																	});

															rclist_dialog
																	.setTitle(R.string.socket_result_list);
															rclist_dialog
																	.getWindow()
																	.setBackgroundDrawableResource(
																			R.drawable.dirbackground);
															rclist_dialog
																	.addContentView(
																			rcs_list,
																			new LayoutParams(
																					LayoutParams.FILL_PARENT,
																					LayoutParams.FILL_PARENT));

														} catch (SAXException saxe) {
															System.out
																	.println(saxe
																			.getMessage()
																			+ " ParseError");
															msg.arg1 = MSG_EXCEPTION;
															msg.obj = RemoteControllerMain.this
																	.getResources()
																	.getString(
																			R.string.exception_sax)
																	+ "_"
																	+ saxe
																			.getMessage();
															mHandler
																	.sendMessage(msg);
															return;
														} catch (ParserConfigurationException e) {
															System.out
																	.println(e
																			.getMessage()
																			+ " IOError from rcm");
															msg.arg1 = MSG_EXCEPTION;
															msg.obj = RemoteControllerMain.this
																	.getResources()
																	.getString(
																			R.string.exception_unknown)
																	+ "_"
																	+ e
																			.getMessage();
															mHandler
																	.sendMessage(msg);
															return;
														} catch (ConnectTimeoutException e) {
															System.out
																	.println(e
																			.getMessage()
																			+ " ConnectionException from rcm");
															msg.arg1 = MSG_EXCEPTION;
															msg.obj = RemoteControllerMain.this
																	.getResources()
																	.getString(
																			R.string.exception_time_out)
																	+ "_"
																	+ e
																			.getMessage();
															mHandler
																	.sendMessage(msg);
															return;
														} catch (IOException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
															System.out
																	.println(e
																			.getMessage()
																			+ " from rcm "
																			+ e
																					.getClass()
																					.getName());
															msg.arg1 = MSG_EXCEPTION;
															msg.obj = RemoteControllerMain.this
																	.getResources()
																	.getString(
																			R.string.exception_io)
																	+ "_"
																	+ e
																			.getMessage();
															mHandler
																	.sendMessage(msg);
															return;

														} catch (URISyntaxException e) {
															System.out
																	.println(e
																			.getMessage()
																			+ " URISyn from rcm");
															msg.arg1 = MSG_EXCEPTION;
															msg.obj = RemoteControllerMain.this
																	.getResources()
																	.getString(
																			R.string.exception_urisyn)
																	+ "_"
																	+ e
																			.getMessage();
															mHandler
																	.sendMessage(msg);
															return;
														} catch (Exception e) {
															msg.arg1 = MSG_EXCEPTION;
															msg.obj = RemoteControllerMain.this
																	.getResources()
																	.getString(
																			R.string.exception_unknown)
																	+ "_"
																	+ e
																			.getMessage();
															mHandler
																	.sendMessage(msg);
															System.out
																	.println(e
																			.getMessage()
																			+ " Excpetion from rcm");
															return;
														}

														msg.obj = rclist_dialog;
														msg.arg1 = RCLIST_MSG;
														mHandler
																.sendMessage(msg);

														super.run();
													}

												};

												thd.start();

											

											// super.onClick(v);
										}

									}).setButton(R.string.back,
									new onDialogButtonClick() {
										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											super.onClick(v);
										}
									}).show();
					if(FIRST_SEARCH)
					{
						FIRST_SEARCH = false;
						waiting_dialog.setTitle("Looking for update");
						waiting_dialog.show();
						updateHandler.sendEmptyMessage(22);
					}
					
					break;
				}

				super.onClick(v);
			}

		});

		md.setMenuIcon(ADD_RC_ID, R.drawable.menu_add);
		md.addMenu(CHOICE_BG_ID, R.string.menu_choice_bg);
		md.setMenuIcon(CHOICE_BG_ID, R.drawable.change_bg);
		md.addMenu(ABOUT_ID, R.string.menu_about);
		md.setMenuIcon(ABOUT_ID, R.drawable.about);
		md.setNumColums(4);
		md.setItemClickListener(new OnItemClick() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (Integer.valueOf(v.getTag().toString())) {
				case SETTING_ID:
					RemoteControllerMain.this.startActivity(new Intent(
							RemoteControllerMain.this, GlobalSetting.class));
					break;
				case ADD_RC_ID:

					break;

				case CHOICE_BG_ID:
					LinearLayout douGallery = (LinearLayout) LayoutInflater
							.from(RemoteControllerMain.this).inflate(
									R.layout.double_gallery, null);

					douGallery.setMinimumHeight(getWindowManager()
							.getDefaultDisplay().getHeight() / 2);

					int maxHeight = RemoteControllerMain.this
							.getWindowManager().getDefaultDisplay().getHeight() / 3;

					final PreImageView display = (PreImageView) douGallery
							.findViewById(R.id.preiv);
					// Set display view's maxHeight
					display.setMaxHeight(maxHeight);
					display.setAdjustViewBounds(true);

					Gallery top = (Gallery) douGallery
							.findViewById(R.id.gallery_top);

					Gallery bottom = (Gallery) douGallery
							.findViewById(R.id.gallery_bottom);

					final ResAdapter resBG = new ResAdapter(
							RemoteControllerMain.this, shelfBackInner,
							shelfBackOuter);
					resBG.maxHeight = maxHeight;

					final int zoomHeight = maxHeight;
					top.setAdapter(resBG);
					top.setOnItemClickListener(new OnItemClickListener() {
						Bitmap background = null;

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							if (!resBG.useSD) {
								if (BitmapManager.getMaxLength(
										RemoteControllerMain.this,
										shelfBackInner[arg2], null) > 300)
									background = BitmapManager.compress(
											RemoteControllerMain.this,
											shelfBackInner[arg2], null, 300,
											false);
								else
									background = BitmapFactory.decodeResource(
											res, shelfBackInner[arg2]);

								background = BitmapManager.zoomBitmap(
										background, zoomHeight, 0);
								display.setImageBitmap(background);
								display.displaySD[0] = false;
							} else {
								if (BitmapManager.getMaxLength(null, 0,
										shelfBackOuter[arg2]) > 300)
									background = BitmapManager
											.compress(null, 0,
													shelfBackOuter[arg2], 300,
													false);
								else
									background = BitmapFactory
											.decodeFile(shelfBackOuter[arg2]);

								background = BitmapManager.zoomBitmap(
										background, zoomHeight, 0);
								//

								display.setImageBitmap(BitmapFactory
										.decodeFile(shelfBackOuter[arg2]));
								display.displaySD[0] = true;
							}
							display.position[0] = arg2;
						}
					});

					final ResAdapter resFront = new ResAdapter(
							RemoteControllerMain.this, shelfFrontInner,
							shelfFrontOuter);
					resFront.maxHeight = maxHeight;

					bottom.setAdapter(resFront);
					bottom.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Bitmap front = null;
							// TODO Auto-generated method stub
							if (!resFront.useSD) {
								if (BitmapManager.getMaxLength(
										RemoteControllerMain.this,
										shelfFrontInner[arg2], null) > 300)
									front = BitmapManager.compress(
											RemoteControllerMain.this,
											shelfFrontInner[arg2], null, 300,
											false);
								else
									front = BitmapFactory.decodeResource(res,
											shelfFrontInner[arg2]);

								front = BitmapManager.zoomBitmap(front, 0,
										display.getWidth() - 10);
								//
								display.setFront(front);
								display.displaySD[1] = false;
							} else {
								if (BitmapManager.getMaxLength(null, 0,
										shelfFrontOuter[arg2]) > 300)
									front = BitmapManager.compress(null, 0,
											shelfFrontOuter[arg2], 300, false);
								else
									front = BitmapFactory
											.decodeFile(shelfFrontOuter[arg2]);

								front = BitmapManager.zoomBitmap(front, 0,
										display.getWidth() - 10);
								display.setFront(front);
								display.displaySD[1] = true;
							}
							display.position[1] = arg2;
						}
					});

					AlertDialog.Builder ab = new AlertDialog.Builder(
							RemoteControllerMain.this);

					ab.setView(douGallery).setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									Bitmap background = null, front = null;
									// System.out.println(wm.getDefaultDisplay().getHeight()
									// + " " +
									// wm.getDefaultDisplay().getWidth());
									if (!display.displaySD[0]) {
										if (BitmapManager
												.getMaxLength(
														RemoteControllerMain.this,
														shelfBackInner[display.position[0]],
														null) > gv.max) {
											background = BitmapManager
													.compress(
															RemoteControllerMain.this,
															shelfBackInner[display.position[0]],
															null, gv.max, false);
										} else {
											background = BitmapFactory
													.decodeResource(
															res,
															shelfBackInner[display.position[0]]);
										}

										dp
												.createElementWithUpdate(
														"shelf",
														"shelfbackground",
														null,
														String
																.valueOf(shelfBackInner[display.position[0]])
																+ "_"
																+ display.displaySD[0],
														null, true);

										background = BitmapManager.zoomBitmap(
												background, wm
														.getDefaultDisplay()
														.getHeight(), wm
														.getDefaultDisplay()
														.getWidth());

										// gv.setShelfBG(background);

									} else {
										if (BitmapManager
												.getMaxLength(
														null,
														0,
														shelfBackOuter[display.position[0]]) > gv.max) {
											background = BitmapManager
													.compress(
															null,
															0,
															shelfBackOuter[display.position[0]],
															gv.max, false);
										} else {
											background = BitmapFactory
													.decodeFile(shelfBackOuter[display.position[0]]);
										}

										dp
												.createElementWithUpdate(
														"shelf",
														"shelfbackground",
														null,
														shelfBackOuter[display.position[0]]
																+ "_"
																+ display.displaySD[0],
														null, true);

										background = BitmapManager.zoomBitmap(
												background, wm
														.getDefaultDisplay()
														.getHeight(), wm
														.getDefaultDisplay()
														.getWidth());

									}
									if (!display.displaySD[1]) {
										if (BitmapManager
												.getMaxLength(
														RemoteControllerMain.this,
														shelfFrontInner[display.position[1]],
														null) > gv.max) {
											front = BitmapManager
													.compress(
															RemoteControllerMain.this,
															shelfFrontInner[display.position[1]],
															null, gv.max, false);
										} else {
											front = BitmapFactory
													.decodeResource(
															res,
															shelfFrontInner[display.position[1]]);
										}
										dp
												.createElementWithUpdate(
														"shelf",
														"shelffront",
														null,
														String
																.valueOf(shelfFrontInner[display.position[1]])
																+ "_"
																+ display.displaySD[1],
														null, true);
										front = BitmapManager.zoomBitmap(front,
												0, wm.getDefaultDisplay()
														.getWidth() - 20);

										// gv.setShelfFront(front);
									} else {
										if (BitmapManager
												.getMaxLength(
														null,
														0,
														shelfFrontOuter[display.position[1]]) > gv.max) {
											front = BitmapManager
													.compress(
															null,
															0,
															shelfFrontOuter[display.position[1]],
															gv.max, false);
										} else {
											front = BitmapFactory
													.decodeFile(shelfFrontOuter[display.position[1]]);
										}
										dp
												.createElementWithUpdate(
														"shelf",
														"shelffront",
														null,
														shelfFrontOuter[display.position[1]]
																+ "_"
																+ display.displaySD[1],
														null, true);
										front = BitmapManager.zoomBitmap(front,
												0, wm.getDefaultDisplay()
														.getWidth() - 20);

										// gv.setShelfFront(front);
									}

									gv.setShelfBG(background, front);
									// gv.setShelfFront(front);

									gv.invalidate();
								}
							}).setNegativeButton(R.string.cancel, null).show();

					douGallery
							.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									// TODO Auto-generated method stub
									if (!resBG.useSD) {
										resBG.useSD = true;
										resFront.useSD = true;
									} else {
										resBG.useSD = false;
										resFront.useSD = false;
									}
									vibrator.vibrate(30);
									resBG.notifyDataSetChanged();
									resFront.notifyDataSetChanged();

									return false;
								}
							});

					break;
				case ABOUT_ID:
					break;

				default:
					break;

				}

				super.onClick(v);
			}

		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		md.show();
		return false;
	}

	// 适配器
	public class ImageAdapter extends BaseAdapter implements OnTouchListener,
			OnClickListener, OnLongClickListener {
		private Context mContext;
		private LayoutInflater inflater;

		public HashMap<Integer, String[]> map4adapter;

		private HashMap<Integer, Object> cache;
		public int[] record = null;

		boolean noImgHasDecoded = false;

		long pattern = 50;

		int startY, endY, c = 0;

		public ImageAdapter(Context context, HashMap<Integer, String[]> map) {
			this.mContext = context;
			inflater = LayoutInflater.from(context);
			this.map4adapter = map;

			cache = new HashMap<Integer, Object>();

			record = new int[map.size()];

		}

		public void clearCache() {
			record = null;
			record = new int[this.map4adapter.size()];

			cache.clear();
			noImgHasDecoded = false;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return map4adapter.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		// here need to be modified
		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub

			if (view == null) {
				view = inflater.inflate(R.layout.listview_item, null);

			}
			view.setMinimumHeight(minuHeight);

			ImageButton iv1 = (ImageButton) view.findViewById(R.id.frontImage1);

			if (record[position] > 0) {
				icon = (Bitmap) cache.get(position);

			} else {
				iv1.setOnTouchListener(this);
				iv1.setOnClickListener(this);
				iv1.setOnLongClickListener(this);

				String imgPath = map4adapter.get(position)[0].replace(".xml",
						".png");
				String name = map4adapter.get(position)[1].replace(".xml", "");
				String customPath = null;

				if (dp != null)
					customPath = dp.getSingleContent(name);

				if (customPath != null) {
					if (BitmapManager.getMaxLength(null, 0, customPath) > minuHeight) {

						icon = BitmapManager.compress(null, 0, customPath,
								minuHeight, true);
					} else
						icon = BitmapFactory.decodeFile(customPath);
				} else
					icon = BitmapFactory.decodeFile(imgPath);

				if (icon == null) {
					if (dp != null) {
						String type = null;
						type = dp.getSingleContent(name + "type");
						if(type != null){
							if(type.toLowerCase().equals(ConfigConstant.TV_CODE)){
								icon = BitmapFactory.decodeResource(
										mContext.getResources(), R.drawable.def_tv);
							}
							if (type.toLowerCase().equals(
									ConfigConstant.AM_CODE)) {
								icon = BitmapFactory.decodeResource(mContext
										.getResources(),
										R.drawable.def_airmachine);
							}
							if (type.toLowerCase().equals(
									ConfigConstant.AUDIO_CODE)) {
								icon = BitmapFactory.decodeResource(mContext
										.getResources(), R.drawable.def_audio);
							}
							if (type.toLowerCase().equals(
									ConfigConstant.DVD_CODE)) {
								icon = BitmapFactory.decodeResource(mContext
										.getResources(), R.drawable.def_dvd);
							}
						}
					}
				}

				if (icon == null && !noImgHasDecoded) {

					icon = BitmapFactory.decodeResource(
							mContext.getResources(), R.drawable.no_img);

					cache.put(-1, icon);

					noImgHasDecoded = true;

				} else {

					if (icon == null) {
						icon = (Bitmap) cache.get(-1);

					} else {

						cache.put(position, icon);
						record[position]++;
					}
				}
			}

			iv1.setTag(map4adapter.get(position)[1].replace(".xml", ""));
			iv1.setTag(R.id.frontImage1, map4adapter.get(position)[0]);

			iv1.setBackgroundColor(Color.TRANSPARENT);

			iv1.setImageBitmap(icon);
			iv1.setAdjustViewBounds(true);
			iv1.setMaxHeight(minuHeight);
			iv1.setMinimumHeight(minuHeight);
			// System.out.println("icon's width:" + icon.getWidth() + " height:"
			// + icon.getHeight() + " minuHeight: " + minuHeight);

			icon = null;

			return view;

		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.frontImage1:
				ImageButton ib = (ImageButton) v;

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// ib.setImageResource(R.drawable.action_down);
					ib.setBackgroundColor(Color.GREEN);
					startY = (int) event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {

					ib.setBackgroundColor(Color.TRANSPARENT);
					// System.out.println(startX - event.getX());
					int yOffset = (int) (event.getY() - startY);
					if (yOffset > 10 && yOffset < 30) {
						vibrator.vibrate(pattern);
						// System.out.println(startY - event.getY());

						String result = dp.getSingleContent(v.getTag()
								.toString()
								+ "Description");

						if (result != null)
							Toast.makeText(mContext, result, Toast.LENGTH_LONG)
									.show();
						else
							Toast.makeText(mContext, v.getTag().toString(),
									Toast.LENGTH_LONG).show();

					}
				}

				break;

			}

			return false;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
//			DomParse main_dp = null;
//			try {
//				main_dp = new DomParse(ConfigConstant.MAINCFGFILEPATH);
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				System.out.println("RCMain 1343 message " + e1.getMessage());
//				Toast.makeText(RemoteControllerMain.this, "error " + e1.getMessage(), Toast.LENGTH_LONG).show();
//				return;
//			}
			
			String orderStr = dp.getSingleContent(arg0.getTag().toString() + "order");
			int order = 0;
			
			if(orderStr != null){
				order = Integer.parseInt(orderStr);
			} else {
				try {
					DomParse.createRCOrder(arg0.getTag(R.id.frontImage1).toString(),dp,true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("RCMain 1350 message " + e.getMessage());
				}
			}
			order++;
			
			dp.createElementWithUpdate("shelf", arg0.getTag() + "order", null, String.valueOf(order), null, true);
//			System.out.println(arg0.getTag() + " " + arg0.getTag(R.id.frontImage1) + " order "+ order +  " RCMain 1338 message");
			
			final Animation mA = new ScaleAnimation(0.5f, getWindowManager().getDefaultDisplay().getWidth() / arg0.getWidth(), 0.5f, getWindowManager()
					.getDefaultDisplay().getHeight() / arg0.getHeight(), 0, arg0.getLeft(), 0, arg0.getTop());
			mA.setDuration(2000);
			Intent intent = new Intent(mContext, RemoteController.class);
			intent.putExtra("xmlPath", arg0.getTag(R.id.frontImage1).toString());
			mContext.startActivity(intent);

			RemoteControllerMain.this.overridePendingTransition(R.anim.rc_show, R.anim.exit_main);
		}

		@Override
		public boolean onLongClick(View arg0) {
			// TODO Auto-generated method stub
			
			currentFilePath = arg0.getTag(R.id.frontImage1).toString();

			subTagName = arg0.getTag().toString().replace(".xml", "");

			new DialogExtension.MyBuilder(RemoteControllerMain.this).setTitle(
					R.string.dialog_setting_title).setItems(choices,
					new onDialogItemClick() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							switch (arg2) {
							case OPTION_MODIFY_BG:

								super.onItemClick(arg0, arg1, arg2, arg3);
								WindowManager m = getWindowManager();
								Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
								WindowManager.LayoutParams p = getWindow()
										.getAttributes(); // 获取对话框当前的参数值
								p.height = (int) d.getHeight();// 高度设置为屏幕的1.0
								p.width = (int) d.getWidth(); // 宽度设置为屏幕的0.8
								p.alpha = 0.7f; // 设置本身透明度
								p.dimAmount = 1.0f; // 设置黑暗度
								getWindow().setAttributes(p); // 设置生效

								dm.showPop();
								break;
							case OPTION_MODIFY_DESC:
								super.onItemClick(arg0, arg1, arg2, arg3);
								final EditText et = new EditText(
										RemoteControllerMain.this);
								// System.out.println("SubTagName:" +
								// subTagName);
								
								new DialogExtension.MyBuilder(RemoteControllerMain.this).setTitle(R.string.dialog_modify_identify).addView(et).setButton(
												R.string.confirm,new onDialogButtonClick() {
													@Override
													public void onClick(
															View arg0) {
														// TODO Auto-generated
														// method stub
														
														if (et.getText().toString().trim().length() == 0) {
															new AlertDialog.Builder(RemoteControllerMain.this).setMessage(R.string.warn_empty_input).setPositiveButton(
																			R.string.confirm,
																			new DialogInterface.OnClickListener() {
																				@Override
																				public void onClick(
																						DialogInterface arg0,
																						int arg1) {
																					dp
																							.createElementWithUpdate(
																									"shelf",
																									subTagName
																											+ "Description",
																									null,
																									et
																											.getText()
																											.toString(),
																									null,
																									true);

																				}
																			})
																	.setNegativeButton(
																			R.string.cancel,
																			null)
																	.show();
														} else {
															dp
																	.createElementWithUpdate(
																			"shelf",
																			subTagName
																					+ "Description",
																			null,
																			et
																					.getText()
																					.toString(),
																			null,
																			true);
															Toast
																	.makeText(
																			RemoteControllerMain.this,
																			R.string.notice_check_identify,
																			Toast.LENGTH_LONG)
																	.show();
														}
														super.onClick(arg0);
													}

												}).setButton(R.string.cancel,
												null).show();
								
								break;

							case OPTION_DELETE:
								super.onItemClick(arg0, arg1, arg2, arg3);
								new DialogExtension.MyBuilder(
										RemoteControllerMain.this)
										.setMessage(R.string.warn_delete_rc)
										.setButton(R.string.confirm,
												new onDialogButtonClick() {
													@Override
													public void onClick(View arg0) {
														// System.out.println(currentFilePath);
														// 删除缓存文件
														try {
															DomParse getCachePath = new DomParse(
																	currentFilePath);
															String cachepath = getCachePath
																	.getSingleContent("Background");

															if (cachepath != null
																	&& cachepath
																			.trim()
																			.length() != 0) {
																File cachefile = new File(
																		cachepath);
																if (cachefile
																		.exists())
																	cachefile
																			.delete();
																getCachePath = null;
															}
														} catch (Exception e1) {
															// TODO
															// Auto-generated
															// catch block
															e1
																	.printStackTrace();
															ErrorReporter
																	.sendErrorMessage(
																			CustomException
																					.getInformation(e1),
																			null);
															System.out
																	.println(e1
																			.getMessage());
														}
														File fileDelete = new File(
																currentFilePath);
														if (fileDelete.delete()) {
															Toast
																	.makeText(
																			mContext
																					.getApplicationContext(),
																			"File Deleted",
																			Toast.LENGTH_SHORT)
																	.show();

															// 删除缓存文件
															String pngcache = dp
																	.getSingleContent(subTagName);
															if (pngcache != null
																	&& pngcache
																			.trim()
																			.length() != 0) {
																File cfile = new File(
																		dp
																				.getSingleContent(subTagName));
																if (cfile
																		.exists())
																	cfile
																			.delete();
															}
															dp.removeSingleContent(subTagName, false);
															dp.removeSingleContent(subTagName + "Description", false);
															dp.removeSingleContent(subTagName + "type", false);
															dp.removeSingleContent(subTagName + "order", false);
//															System.out.println("RCMain 1486 message: " + subTagName);
															dp.writeXML();
//															dp.createElementWithUpdate("shelf",subTagName,null,"",null,true);
//															dp.createElementWithUpdate("shelf",subTagName+ "Description",null,"",null,true);
															try {
																files = fm
																		.getFilteredFiles(
																				ConfigConstant.CONFIGPATH,
																				".xml");
															} catch (FileNotFoundException e) {
																// TODO
																// Auto-generated
																// catch block
																e
																		.printStackTrace();
																System.out
																		.println(e
																				.getMessage());
															}
															ImageAdapter.this.map4adapter = fm
																	.getFilesPathAndName(files);
															ImageAdapter.this
																	.clearCache();
															ImageAdapter.this
																	.notifyDataSetChanged();
														} else
															Toast
																	.makeText(
																			mContext
																					.getApplicationContext(),
																			"Delete File Fail",
																			Toast.LENGTH_SHORT)
																	.show();
														super.onClick(arg0);
													}

												}).setButton(R.string.cancel,
												null)
										.show();
								break;

							case OPTION_POSTATE:
								super.onItemClick(arg0, arg1, arg2, arg3);
								String srcPath = dp
										.getSingleContent(subTagName);
								if (srcPath == null) {
									Toast.makeText(getApplicationContext(),
											R.string.notice_unsupport_res,
											Toast.LENGTH_SHORT).show();
									break;
								}
								// 获得文件名
								File file = new File(srcPath);
								if (!file.exists()) {
									Toast.makeText(getApplicationContext(),
											"Can not find the bitmap",
											Toast.LENGTH_SHORT).show();
									break;
								}
								String filename = file.getName();
								// 获得存储文件夹名
								// String destPath =
								// ConfigConstant.PNGCACHEPATH;
								try {
									BitmapManager.savePostRotatedBitmap(
											srcPath,
											ConfigConstant.PNGCACHEPATH, 90,
											minuHeight, minuHeight);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Toast.makeText(getApplicationContext(), e
											.getMessage(), Toast.LENGTH_LONG);
								}
								dp.createElementWithUpdate("shelf", subTagName,
										null, ConfigConstant.PNGCACHEPATH + "/"
												+ filename, null, true);
								// 刷新适配器内容
								iAdapter.clearCache();
								iAdapter.notifyDataSetChanged();
								break;
							default:
								super.onItemClick(arg0, arg1, arg2, arg3);

								break;
							}
							// super.onItemClick(arg0, arg1, arg2, arg3);
						}

					}).show();

			
			return true;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && sdOK) {

			if (mw != null) {
				mw.setStop(true);
				mw = null;
			}
			showDialog(DIALOG_EXIT);
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		System.out.println("onDestroy");
		if (mw != null)
			mw.setStop(true);

		// 注销广播接收器
		unregisterReceiver(BTStartListenBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		if (null != vibrator) {
			vibrator.cancel();
		}
		if (mw != null)
			mw.setStop(true);
		super.onStop();
	}

	/***************************************************************************************************
	 * @author Yongkun
	 */

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EXIT:
			DialogExtension.MyBuilder exitBuilder = new MyBuilder(
					RemoteControllerMain.this);
			exitBuilder.setTitle(R.string.tip_AlertDialog_ExitBt_Title);
			exitBuilder.setMessage(R.string.tip_AlertDialog_ExitBt_Message);
			exitBuilder.setButton(R.string.confirm, new onDialogButtonClick() {
				public void onClick(View v) {
					dismissDialog(DIALOG_EXIT);

					if (bluetoothAdapter.isEnabled()) {
						bluetoothAdapter.disable();
					}
					myNotificationManager
							.cancel(ConfigConstant.NOTIFICATION_ID);
					myNotificationManager
							.cancel(ConfigConstant.NOTIFICATION_NEWDEVICES_ID);

					if (ui != null) {
						ui.closeDataBase();
						System.out.println("close database");
					}
				

					finish();
					System.exit(0);
					super.onClick(v);
				}
			});
			exitBuilder.setButton(R.string.pause, new onDialogButtonClick() {
				public void onClick(View v) {
					dismissDialog(DIALOG_EXIT);
					myNotificationManager
							.cancel(ConfigConstant.NOTIFICATION_ID);
					showNotification(R.drawable.tip_icon, getResources()
							.getString(R.string.tip_Program_Sleep),
							getResources().getString(R.string.pause),
							new Intent(getApplicationContext(),
									RemoteControllerMain.class), false,
							ConfigConstant.NOTIFICATION_ID);

					finish();
					if (bluetoothServer != null) {
						bluetoothServer.StopServer();
						bluetoothServer = null;
					}
					super.onClick(v);
				}
			});
			// exitBuilder.setButton(R.string.cancel, new onDialogButtonClick()
			// {
			// public void onClick(View v) {
			// dismissDialog(DIALOG_EXIT);
			// super.onClick(v);
			// }
			// });
			return exitBuilder.creat();
		case DIALOG_SEND_ERROR_MSG:
			DialogExtension.MyBuilder dialog = new DialogExtension.MyBuilder(
					this);
			dialog.setTitle(R.string.dialog_send_msg_title).setMessage(
					R.string.dialog_send_msg_message).setButton(
					R.string.of_course, new onDialogButtonClick() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							ErrorReporter.sendErrorFile(null);
							super.onClick(v);
						}

					}).setButton(R.string.cancel, null);
			return dialog.creat();

		default:
			break;
		}
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}

	private void SetupBluetoothServer() {

		// 如存在已配对的蓝牙设备就将其添加到ArrayAdapter
		if (bluetoothAdapter.getBondedDevices().size() > 0) {
			for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
				if (sp.getString(device.getAddress(), "").trim().length() != 0) {
					pairedDevicesList.add(device);
					System.out.println("###" + device.getName() + "---"
							+ device.getAddress());
				}
			}
		}
		// 初始化BluetoothService(Initialization BluetoothService)
		if (bluetoothServer == null) {
			bluetoothServer = new BluetoothServerT(this, handler);
		} else if (bluetoothServer != null) {
			if (sp.getBoolean("bt_role", false)
					&& bluetoothServer.GetDeviceState() == BluetoothConstant.STATE_NONE) {
				// 开启蓝牙服务
				bluetoothServer.StartServer();
			}
		}
		if (bluetoothServer != null && !pairedDevicesList.isEmpty()) {
			System.out.println(devicesNum);
			bluetoothServer.ConnectToRemoteDevice(pairedDevicesList
					.get(devicesNum));
			devicesNum++;
		} else if (bluetoothServer != null && pairedDevicesList.isEmpty()) {
			DoDiscovery();
		}
	}

	/**
	 * 将BbluetoothServer返回的信息传递给一个Handler(Using Handler Gets Data what
	 * BbluetoothServer Return)
	 */
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothConstant.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				// 根据设备状态设置右侧状态标题(According to the device status setting state
				// title)
				case BluetoothConstant.STATE_NONE:
					break;
				case BluetoothConstant.STATE_LISTEN:
					break;
				case BluetoothConstant.STATE_CONNECTED:
					break;
				case BluetoothConstant.STATE_CONNECTING:
					break;
				}
				break;
			// 读数据(Read Data)
			case BluetoothConstant.MESSAGE_READ:
				// 从消息队列中读出数据(Read data from message queue)
				byte[] readBuf = (byte[]) msg.obj;
				// 将字节数据转换为字符串数据(Byets data to String data)
				receiveITCode = new String(readBuf, 0, msg.arg1);
				// 提示读到数据(TIP)
				Toast.makeText(getApplicationContext(), receiveITCode,
						Toast.LENGTH_SHORT).show();
				break;
			// 获取远程设备名(Get Remote Bluetooth Devices)
			case BluetoothConstant.MESSAGE_DEVICE_NAME:
				String connectedDeviceName = msg.getData().getString(
						BluetoothConstant.DEVICE_NAME);
				connectedDeviceAdd = msg.getData().getString(
						BluetoothConstant.DEVICE_ADDR);
				if (sp.getString(connectedDeviceAdd, "").trim().length() == 0) {
					sp.edit()
							.putString(connectedDeviceAdd, connectedDeviceName)
							.commit();
				}
				String strTmp = getResources().getString(
						R.string.tip_BT_ConnectedTo)
						+ sp.getString(connectedDeviceAdd, "");
				myNotificationManager.cancel(ConfigConstant.NOTIFICATION_ID);
				showNotification(R.drawable.tip_icon, strTmp, strTmp,
						new Intent(getApplicationContext(),
								RemoteControllerMain.class), true,
						ConfigConstant.NOTIFICATION_ID);
				DoDiscovery();
				break;
			case BluetoothConstant.MESSAGE_TOAST:
				System.out.println(devicesNum);
				if (devicesNum < pairedDevicesList.size()
						&& msg.getData().getInt(BluetoothConstant.TOAST) == R.string.tip_NoConnectToRemote) {
					bluetoothServer.ConnectToRemoteDevice(pairedDevicesList
							.get(devicesNum));
					devicesNum++;
				} else if (devicesNum == pairedDevicesList.size()
						&& msg.getData().getInt(BluetoothConstant.TOAST) == R.string.tip_NoConnectToRemote) {
					myNotificationManager
							.cancel(ConfigConstant.NOTIFICATION_ID);
					showNotification(R.drawable.tip_icon, getResources()
							.getString(R.string.tip_NoConnectToRemote), "",
							new Intent(getApplicationContext(),
									RemoteControllerMain.class), false,
							ConfigConstant.NOTIFICATION_ID);
					DoDiscovery();
					devicesNum++;
				} else if (devicesNum > pairedDevicesList.size()
						&& msg.getData().getInt(BluetoothConstant.TOAST) == R.string.tip_NoConnectToRemote) {
					myNotificationManager
							.cancel(ConfigConstant.NOTIFICATION_ID);
					showNotification(R.drawable.tip_icon, getResources()
							.getString(R.string.tip_NoConnectToRemote), "",
							new Intent(getApplicationContext(),
									RemoteControllerMain.class), false,
							ConfigConstant.NOTIFICATION_ID);
				}

				if (msg.getData().getInt(BluetoothConstant.TOAST) == R.string.tip_LostConnect
						&& bluetoothAdapter.isEnabled()
						&& bluetoothServer != null
						&& sp.getBoolean("bt_role", false)) {
					bluetoothServer.StopServer();
					bluetoothServer.StartServer();
				}
				break;
			}
		}
	};

	/**************************************************************************************************/

	// 当屏幕旋转时
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);

	}
	
	@Override
	protected void onRestart() {
		super.onRestart();

		try {
			dp = new DomParse(ConfigConstant.MAINCFGFILEPATH);
//			System.out.println("RCM 1793:ReRead dp");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			System.out.println("RCM 1796 Message " + e.getMessage());
		}
		myNotificationManager.cancel(ConfigConstant.NOTIFICATION_NEWDEVICES_ID);

		if (isSettingChanged) {
			if (gv != null) {
				NUMCOLUMNS = sp.getInt("num_columns", 2);
				iAdapter.map4adapter = fm.getFilesPathAndName(files);
				gv.setNumColumns(NUMCOLUMNS);
				iAdapter.clearCache();
				gv.setChange();
				iAdapter.notifyDataSetChanged();
				isSettingChanged = false;

			} else {
				Toast.makeText(getApplicationContext(),
						R.string.error_breakdown, Toast.LENGTH_SHORT);
				finish();
				return;

			}

		}
	}

	// sp.getBoolean("bt_auto", false) &&

	/**************************************************************************************
	 * Yongkun
	 */
	private final BroadcastReceiver BTStartListenBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
					BluetoothAdapter.ERROR);
			if (btState == BluetoothAdapter.STATE_ON) {
				myNotificationManager.cancel(ConfigConstant.NOTIFICATION_ID);
				showNotification(R.drawable.tip_icon, getResources().getString(
						R.string.tip_BT_Enable), "", null, false,
						ConfigConstant.NOTIFICATION_ID);
				SetupBluetoothServer();
			}
		}
	};

	private final BroadcastReceiver BTConnectListenBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
				System.out.println("BluetoothDevice.ACTION_ACL_CONNECTED");
			} else if (intent.getAction().equals(
					BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)) {
				System.out
						.println("BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED");
			} else if (intent.getAction().equals(
					BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				System.out.println("BluetoothDevice.ACTION_ACL_DISCONNECTED");
			}
		}
	};

	private final BroadcastReceiver FoundBTDevicesListenBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 找到设备
			if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
				// 从Intent获取BluetoothDevice对象
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (!device.getAddress().equals(connectedDeviceAdd)) {
					myNotificationManager
							.cancel(ConfigConstant.NOTIFICATION_NEWDEVICES_ID);
					showNotification(R.drawable.tip_other_device_icon,
							getResources().getString(
									R.string.tip_BT_DiscoveryDevices),
							getResources().getString(
									R.string.tip_BT_DiscoveryDevices_Summary),
							new Intent(RemoteControllerMain.this,
									DevicesList.class), false,
							ConfigConstant.NOTIFICATION_NEWDEVICES_ID);
				}
			}
		}
	};

	/**
	 * 开启本地蓝牙的搜索功能
	 */
	private void DoDiscovery() {
		// 如在本地蓝牙正在搜索，则停止它
		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}
		// 开启蓝牙搜索
		bluetoothAdapter.startDiscovery();
	}

	public void showNotification(int icon, String tickertext, String content,
			Intent intent, boolean tip, int ID) {
		// Notification管理器，后面的参数分别是显示在顶部通知栏的小图标，小图标旁的文字（短暂显示，自动消失）系统当前时间
		Notification notification = new Notification(icon, tickertext, System
				.currentTimeMillis());
		if (tip) { // 声音与振动提示
			notification.defaults = Notification.DEFAULT_ALL;
		}
		// 点击通知后的动作
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.setLatestEventInfo(this, getResources().getString(
				R.string.app_name), content, pendingIntent);
		myNotificationManager.notify(ID, notification);
	}

	/***************************************************************************************/

}