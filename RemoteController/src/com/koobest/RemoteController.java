package com.koobest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ViewSwitcher.ViewFactory;

import com.koobest.bluetooth.BluetoothConstant;
import com.koobest.constant.ConfigConstant;
import com.koobest.customization.DirManager;
import com.koobest.customization.LockableGridView;
import com.koobest.customization.MyAdapter;
import com.koobest.customization.MyImageAdapter;
import com.koobest.customization.SeekBarDialog;
import com.koobest.customization.VerticalScrollView;
import com.koobest.customization.VerticalScrollView.onItemSelected;
import com.koobest.dialog.DialogExtension;
import com.koobest.dialog.DialogExtension.MyBuilder.onDialogButtonClick;
import com.koobest.dialog.DialogExtension.MyBuilder.onDialogItemClick;
import com.koobest.menu.DialogMenu;
import com.koobest.menu.DialogMenu.MyBuilder;
import com.koobest.menu.DialogMenu.MyBuilder.OnItemClick;
import com.koobest.parse.BitmapManager;
import com.koobest.parse.DomParse;
import com.koobest.parse.FileManager;
import com.koobest.setting.ControllerSetting;
import com.koobest.view.cusHscroll;

public class RemoteController extends ActivityGroup implements ViewFactory {
	/***************************************************************************************************
	 * @author Yongkun
	 */
	// false if Mode is coding
	// true if Mode is decoding

	public static boolean markDecoding = false;
	private static boolean markTimeOut = false;

	// private TimeOutThread timeOutThread = null;
	// private JudgeIRCodeExistThread judgeIRCodeExistThread = null;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	/**************************************************************************************************/
	/**************************************************************************************************/

	private final int MENU_LOCK = Menu.FIRST;
	private final int MENU_CHANGE = Menu.FIRST + 1;
	private final int MENU_CHANGE_BG = Menu.FIRST + 2;
	private static final int MENU_PREF = Menu.FIRST + 3;

	public static final int ROWPOS = R.id.btn_set;

	public static final int TAG_CODE = R.id.btn_cancel;

	BufferedInputStream buff;
	EditText setDes, setName;
	DomParse dp;
	HashMap<Integer, HashMap<String, String>> map = null;
	Toast toast;
	private boolean Save_enabled = false;
	private RemoteControllerAdapter rc_adapter;
	LayoutInflater inflater;
	private boolean changeModeEnabled = false, positon_replace_enable = false;

	private Button btn_add, btn_save, btn_delete;
	public Dialog btn_dialog;
	private ProgressDialog pro_dialog;
	private LinearLayout pre_li;
	private LinearLayout pre_btn_li;
	private LockableGridView rc_view;
	private SharedPreferences sp;
	private float btn_height;
	int pre_position, after_position;
	private Editor sp_editor;
	private boolean screenLock = false;
	private TextView text;
	private SeekBar dsb;
	private mySwitcher vswitcher;
	private String xmlPath;

	// 震动
	private Vibrator vibrator;
	private int vibrator_strength = 50, btn_width, btn_gap;
	boolean isVibrator = false;
	private ArrayList<Integer> numlist;

	public int modify_row_pos;
	private DisplayMetrics dm = new DisplayMetrics();
	// 添加背景图片路径
	// private final String remoteControllerBGCache = "/RcBGCache";
	int maxLength = 0;
	private DirManager bg_chooser;

	private ProgressDialog pd = null;
	private Handler mHandler = new Handler(new Handler.Callback() {

		private void setMessage(String savePath) {
			Message msg = mHandler.obtainMessage();
			msg.obj = savePath;
			msg.arg1 = 10;
			mHandler.sendMessage(msg);
		}

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.obj == null)
				return true;

			if (msg.arg1 == 10) {
				if (pd != null)
					pd.dismiss();

				pre_li.setBackgroundDrawable(new BitmapDrawable(BitmapFactory
						.decodeFile(msg.obj.toString())));
				return true;
			}
			// 检测图片信息
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(msg.obj.toString(), opt);

			final String path = msg.obj.toString();
			File file = new File(path);
			final String name = file.getName();
			final String savePath = ConfigConstant.REMOTECONTROLLERBGCACHEPATH
					+ "/" + name;

			if (opt.outWidth > opt.outHeight) {
				new DialogExtension.MyBuilder(RemoteController.this).setTitle(
						R.string.tip).setMessage(R.string.rc_dialog_notice)
						.setButton(R.string.of_course,
								new onDialogButtonClick() {
									@Override
									public void onClick(View view) {
										// TODO Auto-generated method stub
										super.onClick(view);

										final SeekBarDialog seekbardialog = new SeekBarDialog(
												RemoteController.this);
										seekbardialog.setBitmapPath(path);

										View.OnClickListener onclick = new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												// TODO Auto-generated method
												// stub

												pd
														.setMessage(RemoteController.this
																.getResources()
																.getString(
																		R.string.dialog_in_progress));
												pd.show();

												seekbardialog.dismiss();

												Thread thd = new Thread() {

													@Override
													public void run() {
														// TODO Auto-generated
														// method stub
														super.run();
														try {
															BitmapManager
																	.savePostRotatedBitmap(
																			path,
																			ConfigConstant.REMOTECONTROLLERBGCACHEPATH,
																			seekbardialog
																					.getProgress(),
																			maxLength,
																			maxLength);
														} catch (IOException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														}

														dp
																.createElementWithUpdate(
																		"RemoteController",
																		"Background",
																		null,
																		savePath,
																		null,
																		true);

														setMessage(savePath);
													}

												};

												thd.start();
											}

										};

										seekbardialog
												.setOnClickListener(onclick);
										seekbardialog.setSeekBar(360, 0, 90,
												null);
										seekbardialog.show();

									}
								}).setButton(R.string.cancel,
								new onDialogButtonClick() {

									@Override
									public void onClick(View dialog) {
										// TODO Auto-generated method stub

										pd
												.setMessage(RemoteController.this
														.getResources()
														.getString(
																R.string.dialog_in_progress));
										pd.show();

										super.onClick(dialog);

										Thread thd = new Thread() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												super.run();

												try {
													BitmapManager
															.savePostRotatedBitmap(
																	path,
																	ConfigConstant.REMOTECONTROLLERBGCACHEPATH,
																	0,
																	maxLength,
																	maxLength);
												} catch (IOException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}

												dp.createElementWithUpdate(
														"RemoteController",
														"Background", null,
														savePath, null, true);

												setMessage(savePath);
											}

										};
										thd.start();
									}
								}).show();

			} else {
				pd.setMessage(RemoteController.this.getResources().getString(
						R.string.dialog_in_progress));
				pd.show();

				Thread thd = new Thread() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();

						try {
							BitmapManager.savePostRotatedBitmap(path,
									ConfigConstant.REMOTECONTROLLERBGCACHEPATH,
									0, maxLength, maxLength);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						dp.createElementWithUpdate("RemoteController",
								"Background", null, savePath, null, true);

						setMessage(savePath);
					}

				};
				thd.start();
			}

			return false;
		}
	});

	public TabHost tabhost;

	public MyAdapter iadapter1;

	public MyAdapter iadapter2;

	public MyImageAdapter iadapter3;
	private MyBuilder menu_dialog;
	private View v;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		cusHscroll main = new cusHscroll(this, this.getWindowManager()
				.getDefaultDisplay().getWidth());
		inflater = LayoutInflater.from(this);
		v = inflater.inflate(R.layout.remote_controller_main, null);
		// System.out.println(v);

		// vswitcher.
		// Create ProgressDialog
		pd = new ProgressDialog(RemoteController.this);

		// 震动提示相关
		sp = this.getSharedPreferences("com.koobest_preferences", 0);

		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		isVibrator = sp.getBoolean("vibrator", false);
		vibrator_strength = sp.getInt("vibrator_strength", 50);
		sp_editor = sp.edit();
		// 初始化文件选择器
		bg_chooser = new DirManager(this, mHandler);

		toast = Toast.makeText(getApplicationContext(), "Parse Error!",
				Toast.LENGTH_SHORT);

		LinearLayout parse_error = (LinearLayout) toast.getView();

		ImageView error_img = new ImageView(getApplicationContext());

		btn_add = (Button) v.findViewById(R.id.btn_set);
		btn_save = (Button) v.findViewById(R.id.btn_cancel);
		btn_delete = (Button) v.findViewById(R.id.btn_delete);
		btn_add.setOnClickListener(new BtnAddListener());
		btn_save.setOnClickListener(new SaveListener());
		btn_delete.setOnClickListener(new BtnDeleteListener());
		btn_add.setVisibility(View.GONE);
		btn_save.setVisibility(View.GONE);
		btn_delete.setVisibility(View.GONE);
		Intent i = this.getIntent();

		// DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);

		try {
			
			
			dp = new DomParse(i.getStringExtra("xmlPath"));
			if (dp.getSingleContent("NumForeach") == null) {
				dp.modifyButNums(28);
				// dp.createElementWithUpdate("RemoteController", "NumRows",
				// null,
				// "7", null, true);
				// dp.createElementWithUpdate("RemoteController", "NumButtons",
				// null, "3", null, true);
				dp.createElementWithUpdate("RemoteController", "NumForeach",
						null, "4/4/4/4/4/4/4", null, true);
			}
			// else
			// {
			// String num = dp.getSingleContent("NumForeach");
			// String[] nums = num.split("/");
			// int total = 0;
			// for(int count = 0;count<nums.length;count++)
			// total = Integer.valueOf(nums[count])+total;
			// dp.modifyButNums(total);
			// dp.writeXML();
			// }
			if (dp.getSingleContent("RowsOnScreen") == null)
				dp.createElementWithUpdate("RemoteController", "RowsOnScreen",
						null, "7", null, true);
			if (dp.getSingleContent("btn_width") == null)
				dp.createElementWithUpdate("RemoteController", "btn_width",
						null, "5", null, true);
			if (dp.getSingleContent("btn_height") == null)
				dp.createElementWithUpdate("RemoteController", "btn_height",
						null, "5", null, true);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			toast.setText("file not found!");
			error_img.setImageResource(R.drawable.file_not_found);
			toast.setGravity(Gravity.CENTER, 0, 0);
			parse_error.addView(error_img, 0);
			toast.show();

			finish();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("parse error");
			error_img.setImageResource(R.drawable.parse_error);
			toast.setGravity(Gravity.CENTER, 0, 0);
			parse_error.addView(error_img, 0);
			toast.show();
			finish();
			return;
		}
		sp_editor.putInt("controller_rows_onscreen", Integer.parseInt(dp
				.getSingleContent("RowsOnScreen")));
		sp_editor.putInt("controller_btn_width", Integer.parseInt(dp
				.getSingleContent("btn_width")));
		sp_editor.putInt("controller_btn_height", Integer.parseInt(dp
				.getSingleContent("btn_height")));
		sp_editor.commit();
		System.out.println(sp.getAll());

		btn_height = dm.heightPixels;
		btn_height = btn_height / sp.getInt("controller_rows_onscreen", 6) - 5;
		btn_gap = sp.getInt("controller_btn_height", 20);
		btn_width = sp.getInt("controller_btn_width", 20);
		map = dp.getAllWidgets("button");
		rc_adapter = new RemoteControllerAdapter(this, map, 0);

		// 背景图片

		if (this.getWindowManager().getDefaultDisplay().getWidth() > this
				.getWindowManager().getDefaultDisplay().getHeight())
			maxLength = this.getWindowManager().getDefaultDisplay().getWidth();
		else
			maxLength = this.getWindowManager().getDefaultDisplay().getHeight();

		// 判断路径是否合法，图片文件是否存在
		String path = dp.getSingleContent("Background");

		if (path != null) {
			if (path.trim().length() == 0)
				path = null;
			else {
				File file = new File(path);
				if (!file.exists()) {
					Toast.makeText(this, R.string.rc_dialog_imgloss,
							Toast.LENGTH_SHORT).show();
					path = null;
				}
			}
		}

		String num_eachrow = dp.getSingleContent("NumForeach");
		String[] nums_each = num_eachrow.split("/");

		numlist = new ArrayList<Integer>();
		for (int j = 0; j < nums_each.length; j++)
			numlist.add(Integer.parseInt(nums_each[j]));

		pre_li = (LinearLayout) v.findViewById(R.id.rc_main);
		pre_btn_li = (LinearLayout) v.findViewById(R.id.rc_btn_li);
		rc_view = (LockableGridView) v
				.findViewById(R.id.remote_controller_gridview);

		rc_view.setCacheColorHint(0);
		rc_view.setGravity(Gravity.CENTER);
		rc_view.setNumColumns(1);
		// rc_view.setBackgroundColor(Color.LTGRAY);
		rc_view.setAdapter(rc_adapter);
		// rc_view.setHorizontalSpacing(5);
		rc_view.setFadingEdgeLength(0);
		rc_view.setVerticalScrollBarEnabled(false);

		rc_view.setClickable(false);

		main.mid.addView(v);
		setContentView(main);
		VerticalScrollView vsv = new VerticalScrollView(this);
		main.left.addView(vsv);
		Drawable slide_left = this.getResources().getDrawable(
				R.drawable.slide_left_bg);
		BitmapDrawable bd = (BitmapDrawable) slide_left;
		Bitmap bm_left = bd.getBitmap();

		bm_left = Bitmap.createBitmap(bm_left, 0, 0, 80, 480);

		bd = new BitmapDrawable(bm_left);
		slide_left = bd;

		main.left.setBackgroundDrawable(slide_left);

		vsv.setOnItemSelectedListener(new onItemSelected() {

			@Override
			public void onSelected(int position, View view, String configPath) {
				// TODO Auto-generated method stub
				try {
					DomParse main_dp = new DomParse(ConfigConstant.MAINCFGFILEPATH);
					
					String filename = new File(configPath).getName().replace(".xml", "");
					
					String orderStr = null;
					orderStr = main_dp.getSingleContent(filename + "order");
					
					int order = 0;
					
					if(orderStr != null){
						order = Integer.parseInt(orderStr);
					} else {
						DomParse.createRCOrder(configPath,main_dp,true);
					}
					order++;
					System.out.println("RC 553 message " + filename + " Order:" + order);
					main_dp.createElementWithUpdate("shelf", filename + "order", null, String.valueOf(order), null, true);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("RC 572 message:" + e.getMessage());
				}
				xmlPath = configPath;
				
				vswitcher.showNext();
				
			}
		});
		main.right.setVisibility(View.GONE);
		vswitcher = new mySwitcher(this);
		vswitcher.setFactory(this);
		vswitcher.setInAnimation(this, R.anim.win_enter);
		vswitcher.setOutAnimation(this, R.anim.win_exit);

		vswitcher.showNext();
		v.setVisibility(View.GONE);
		main.mid.setOrientation(LinearLayout.VERTICAL);
		main.mid.addView(vswitcher, new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.FILL_PARENT,
				android.widget.LinearLayout.LayoutParams.FILL_PARENT));

	}

	class mySwitcher extends ViewSwitcher {

		public mySwitcher(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void showNext() {
			// TODO Auto-generated method stub
			LinearLayout li = (LinearLayout) this.getNextView();
			if (xmlPath != null) {
				try {
					System.out.println("changed:" + xmlPath);
					dp = null;
					dp = new DomParse(xmlPath);
					if (dp.getSingleContent("NumForeach") == null) {
						dp.modifyButNums(28);

						dp
								.createElementWithUpdate("RemoteController",
										"NumForeach", null, "4/4/4/4/4/4/4",
										null, true);
					}

					if (dp.getSingleContent("RowsOnScreen") == null)
						dp.createElementWithUpdate("RemoteController",
								"RowsOnScreen", null, "7", null, true);
					if (dp.getSingleContent("btn_width") == null)
						dp.createElementWithUpdate("RemoteController",
								"btn_width", null, "5", null, true);
					if (dp.getSingleContent("btn_height") == null)
						dp.createElementWithUpdate("RemoteController",
								"btn_height", null, "5", null, true);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					toast.setText("file not found!");
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();

					finish();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("parse error");
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					finish();
					return;
				}
				sp_editor.putInt("controller_rows_onscreen", Integer
						.parseInt(dp.getSingleContent("RowsOnScreen")));
				sp_editor.putInt("controller_btn_width", Integer.parseInt(dp
						.getSingleContent("btn_width")));
				sp_editor.putInt("controller_btn_height", Integer.parseInt(dp
						.getSingleContent("btn_height")));
				sp_editor.commit();
				System.out.println(sp.getAll());

				btn_height = dm.heightPixels;
				btn_height = btn_height
						/ sp.getInt("controller_rows_onscreen", 6) - 5;
				btn_gap = sp.getInt("controller_btn_height", 20);
				btn_width = sp.getInt("controller_btn_width", 20);
				map = dp.getAllWidgets("button");
			}
			String path = dp.getSingleContent("Background");

			if (path != null) {
				if (path.trim().length() == 0)
					path = null;
				else {
					File file = new File(path);
					if (!file.exists()) {
						path = null;
					}
				}
			}

			Drawable remoteControllerBackground = null;
			if (path != null) {
				remoteControllerBackground = new BitmapDrawable(BitmapFactory
						.decodeFile(path));
			} else {
				remoteControllerBackground = getResources().getDrawable(
						R.drawable.rc_def_bg);
			}
			li.setBackgroundDrawable(remoteControllerBackground);
			rc_view.setAdapter(rc_adapter);
			rc_view.setVisibility(View.VISIBLE);

			pre_btn_li.removeAllViews();
			pre_li.removeAllViews();
			li.setOrientation(LinearLayout.VERTICAL);

			li.addView(rc_view, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
			LinearLayout sub_li = new LinearLayout(RemoteController.this);
			sub_li.setOrientation(LinearLayout.HORIZONTAL);
			sub_li.addView(btn_add, new LinearLayout.LayoutParams(50,
					LayoutParams.WRAP_CONTENT, 1));
			sub_li.addView(btn_delete, new LinearLayout.LayoutParams(50,
					LayoutParams.WRAP_CONTENT, 1));
			sub_li.addView(btn_save, new LinearLayout.LayoutParams(50,
					LayoutParams.WRAP_CONTENT, 1));
			li.addView(sub_li, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			pre_btn_li = sub_li;
			pre_li = li;
			super.showNext();
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if (!sp.getBoolean("show_rc_dialog", false)) {
			DialogExtension.MyBuilder hint_dialog = new DialogExtension.MyBuilder(
					RemoteController.this);
			hint_dialog.setTitle("提示");
			hint_dialog.setMessage(R.string.help);
			hint_dialog.setButton(R.string.confirm, null);
			CheckBox cb = new CheckBox(RemoteController.this);
			cb.setText("不再显示该对话框");
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					sp_editor.putBoolean("show_rc_dialog", isChecked);
					sp_editor.commit();
				}
			});
			cb.setTextColor(Color.BLUE);
			hint_dialog.addView(cb);
			hint_dialog.show();
		}
		super.onStart();
	}

	private class BtnAddListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			DialogExtension.MyBuilder mdb = new DialogExtension.MyBuilder(
					RemoteController.this);
			View lo = inflater.inflate(R.layout.dialog_seekbar, null);
			text = (TextView) lo.findViewById(R.id.seekbar_text);
			dsb = (SeekBar) lo.findViewById(R.id.dialog_seekbar);

			dsb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated
					// method stub

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated
					// method stub

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated
					// method stub
					text.setText(String.valueOf(progress));
				}
			});

			dsb.setProgress(3);
			mdb.addView(lo);
			mdb.setTitle(R.string.newrow_num);
			mdb.setButton(R.string.confirm, new onDialogButtonClick() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					numlist.add(numlist.size(), dsb.getProgress());
					updateNumRows(numlist);
					Save_enabled = true;
					rc_adapter.notifyDataSetChanged();
					super.onClick(v);
				}

			}).setButton(R.string.cancel, null).show();

		}

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub

		if (!dp
				.getSingleContent("RowsOnScreen")
				.trim()
				.equals(
						String
								.valueOf(
										sp
												.getInt(
														"controller_rows_onscreen",
														7)).trim())
				|| !dp.getSingleContent("btn_width").trim().equals(
						String.valueOf(sp.getInt("controller_btn_width", 7))
								.trim())
				|| !dp.getSingleContent("btn_height").trim().equals(
						String.valueOf(sp.getInt("controller_btn_height", 7))
								.trim())) {
			System.out.println(dp.getSingleContent("RowsOnScreen") + "+"
					+ String.valueOf(sp.getInt("controller_rows_onscreen", 7)));
			System.out.println(dp.getSingleContent("btn_width") + "+"
					+ String.valueOf(sp.getInt("controller_btn_width", 7)));
			System.out.println(dp.getSingleContent("btn_height") + "+"
					+ String.valueOf(sp.getInt("controller_btn_height", 7)));
			dp.createElementWithUpdate("RemoteController", "RowsOnScreen",
					null, String.valueOf(sp.getInt("controller_rows_onscreen",
							7)), null, false);
			dp.createElementWithUpdate("RemoteController", "btn_width", null,
					String.valueOf(sp.getInt("controller_btn_width", 20)),
					null, false);
			dp.createElementWithUpdate("RemoteController", "btn_height", null,
					String.valueOf(sp.getInt("controller_btn_height", 20)),
					null, false);

			btn_height = dm.heightPixels;
			btn_height = btn_height / sp.getInt("controller_rows_onscreen", 6)
					- 5;
			btn_width = sp.getInt("controller_btn_width", 20);
			btn_gap = sp.getInt("controller_btn_height", 20);
			rc_adapter.notifyDataSetChanged();
			btn_save.setVisibility(View.VISIBLE);
			Save_enabled = true;
		}
		super.onRestart();
	}

	private class BtnDeleteListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (numlist.size() > 1) {
				int total = 0;
				for (int i = 0; i < numlist.size(); i++)
					total += numlist.get(i);

				for (int i = 0; i < numlist.get(numlist.size() - 1); i++)
					map.put(total - 1 - i, null);

				numlist.remove(numlist.size() - 1);
				rc_adapter.notifyDataSetChanged();
				updateNumRows(numlist);

				Save_enabled = true;
			} else {
				for (int i = 0; i < numlist.get(0); i++) {
					map.put(i, null);
					dp.deleteButton(i);
					rc_adapter.notifyDataSetChanged();
				}
			}
		}

	}

	private class SaveListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			btn_add.setVisibility(View.GONE);
			btn_save.setVisibility(View.GONE);
			btn_delete.setVisibility(View.GONE);
			if (Save_enabled) {
				DialogExtension.MyBuilder mb = new DialogExtension.MyBuilder(
						RemoteController.this);
				mb.setTitle(R.string.hint);
				mb.setMessage(R.string.dialog_save_quest);
				mb.setButton(R.string.confirm, new onDialogButtonClick() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dp.writeXML();
						super.onClick(v);
					}

				}).setButton(R.string.cancel, new onDialogButtonClick() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = RemoteController.this.getIntent();
						try {
							dp = null;
							if (xmlPath == null)
								dp = new DomParse(i.getStringExtra("xmlPath"));
							else
								dp = new DomParse(xmlPath);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						map = dp.getAllWidgets("button");
						
						String num_eachrow = dp.getSingleContent("NumForeach");
						String[] nums_each = num_eachrow.split("/");

						numlist = new ArrayList<Integer>();
						for (int j = 0; j < nums_each.length; j++)
							numlist.add(Integer.parseInt(nums_each[j]));

						
						rc_adapter.notifyDataSetChanged();
						super.onClick(v);
					}

				}).show();

			}
			Save_enabled = false;
			changeModeEnabled = false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		menu.add(0, MENU_CHANGE, Menu.NONE, R.string.change_label);

		menu_dialog = new DialogMenu.MyBuilder(RemoteController.this);
		menu_dialog.addMenu(MENU_CHANGE, R.string.change_label);
		menu_dialog.setMenuIcon(MENU_CHANGE, R.drawable.about);
		menu_dialog.addMenu(MENU_LOCK, R.string.menu_lock);
		menu_dialog.setMenuIcon(MENU_LOCK, R.drawable.ck_off);
		menu_dialog.addMenu(MENU_CHANGE_BG, R.string.menu_change_bg);
		menu_dialog.setMenuIcon(MENU_CHANGE_BG, R.drawable.change_bg);
		menu_dialog.addMenu(MENU_PREF, R.string.menu_preference);
		menu_dialog.setMenuIcon(MENU_PREF, R.drawable.menu_config);
		menu_dialog.setNumColums(4);
		menu_dialog.setItemClickListener(new OnItemClick() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (Integer.valueOf(v.getTag().toString())) {
				case MENU_CHANGE:
					changeModeEnabled = !changeModeEnabled;
					if (changeModeEnabled) {
						rc_view.setScreenLock(false);
						// Toast.makeText(RemoteController.this,
						// R.string.modify_enabled, Toast.LENGTH_LONG)
						// .show();
						btn_add.setVisibility(View.VISIBLE);
						btn_save.setVisibility(View.VISIBLE);
						btn_delete.setVisibility(View.VISIBLE);
					} else {
						rc_view.setScreenLock(screenLock);
						Intent i = RemoteController.this.getIntent();
						try {
							dp = null;
							dp = new DomParse(i.getStringExtra("xmlPath"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						map = dp.getAllWidgets("button");
						Toast.makeText(RemoteController.this,
								R.string.modify_disabled, Toast.LENGTH_SHORT)
								.show();
						btn_add.setVisibility(View.GONE);
						btn_save.setVisibility(View.GONE);
						btn_delete.setVisibility(View.GONE);
					}
					rc_adapter.notifyDataSetChanged();
					break;

				case MENU_LOCK:
					screenLock = !screenLock;
					rc_view.setScreenLock(screenLock);
					break;

				case MENU_CHANGE_BG:
					bg_chooser.showPop();

					break;
				case MENU_PREF:
					RemoteController.this.startActivity(new Intent(
							RemoteController.this, ControllerSetting.class));
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

		if (screenLock) {
			menu_dialog.setItemTitle(MENU_LOCK, R.string.menu_unlock);
		} else {
			menu_dialog.setItemTitle(MENU_LOCK, R.string.menu_lock);
		}

		if (changeModeEnabled) {
			menu_dialog.setItemVisible(MENU_LOCK, false);
			menu_dialog.setItemTitle(MENU_CHANGE, R.string.change_cancel);
		} else {
			menu_dialog.setItemVisible(MENU_LOCK, true);
			menu_dialog.setItemTitle(MENU_CHANGE, R.string.change_label);
		}

		menu_dialog.show();
		return false;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case MENU_CHANGE:
			changeModeEnabled = !changeModeEnabled;
			if (changeModeEnabled) {
				rc_view.setScreenLock(false);
				Toast.makeText(RemoteController.this, R.string.modify_enabled,
						Toast.LENGTH_LONG).show();
				btn_add.setVisibility(View.VISIBLE);
				btn_save.setVisibility(View.VISIBLE);
				btn_delete.setVisibility(View.VISIBLE);
			} else {
				rc_view.setScreenLock(screenLock);
				Intent i = RemoteController.this.getIntent();
				try {
					dp = null;
					dp = new DomParse(i.getStringExtra("xmlPath"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				map = dp.getAllWidgets("button");
				Toast.makeText(RemoteController.this, R.string.modify_disabled,
						Toast.LENGTH_SHORT).show();
				btn_add.setVisibility(View.GONE);
				btn_save.setVisibility(View.GONE);
				btn_delete.setVisibility(View.GONE);
			}
			rc_adapter.notifyDataSetChanged();
			break;

		case MENU_LOCK:
			screenLock = !screenLock;
			rc_view.setScreenLock(screenLock);
			break;

		case MENU_CHANGE_BG:
			bg_chooser.showPop();

			break;
		case MENU_PREF:
			this.startActivity(new Intent(this, ControllerSetting.class));
			break;
		default:
			break;
		}

		return true;
	}

	public class RemoteControllerAdapter extends BaseAdapter implements
			OnClickListener, OnLongClickListener, OnTouchListener {

		// private static final int POSITION_ID = 152482;
		private Context mContext;
		private LayoutInflater inflater;
		private Object identifier;
		int rowposition;

		public RemoteControllerAdapter(Context context,
				HashMap<Integer, HashMap<String, String>> map, int position) {
			this.mContext = context;
			inflater = LayoutInflater.from(context);
			rowposition = position;
			// this.widgetsMap = map;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return numlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int start = 0;
			int gv_btn_width;
			int gv_btn_height;
			int layout_height = -2;
			int layout_width = -2;
			for (int i = 0; i < position; i++)
				start = start + numlist.get(i);
			View layout = inflater.inflate(R.layout.remote_controller, null);
			layout.setBackgroundColor(0x11cccccc);
			LinearLayout li = (LinearLayout) layout.findViewById(R.id.rc_li);
			li.setOrientation(LinearLayout.HORIZONTAL);
			li.setClickable(false);
			LinearLayout container;
			Button btn;
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(50,
					(int) (btn_height - 5), 1);
			lp.setMargins(btn_width, btn_gap, btn_width, btn_gap);

			gv_btn_width = (RemoteController.this.getWindow()
					.getWindowManager().getDefaultDisplay().getWidth() - 20)
					/ numlist.get(position) - 2 * btn_width - 4;

			gv_btn_height = (int) (btn_height - 5);

			for (int c_position = start; c_position < start
					+ numlist.get(position); c_position++) {
				layout_height = -2;
				layout_width = -2;

				container = new LinearLayout(mContext);
				btn = new Button(RemoteController.this);
				container.setGravity(Gravity.CENTER);
				container.setLayoutParams(lp);
				li.addView(container);
				// btn.setWidth(50);
				btn.setTag(c_position);
				btn.setTag(ROWPOS, position);
				// System.out.println(position);
				// btn.setImageDrawable(getResources().getDrawable(R.drawable.add_item_icon));
				btn.setOnClickListener(this);
				btn.setBackgroundResource(R.drawable.btn_drawable_default);
				btn.setClickable(true);
				if (map.get(c_position) != null) {
					Drawable icon = null;

					if (map.get(c_position).get("icon").trim().length() != 0) {

								String path = map.get(c_position).get("icon");
								icon = getResources().getDrawable(
										Integer.parseInt(path));
								System.out.println(icon.isStateful());

								// if (icon.isStateful()) {
								// BitmapDrawable bd = (BitmapDrawable)
								// icon;
								// Bitmap temp = bd.getBitmap();
								if ((float) icon.getIntrinsicHeight()
										/ icon.getIntrinsicWidth() >= (float) gv_btn_height
										/ gv_btn_width) {
									layout_height = gv_btn_height;
									layout_width = icon.getIntrinsicWidth()
											* gv_btn_height
											/ icon.getIntrinsicHeight();
								} else {

									layout_width = gv_btn_width;
									layout_height = icon.getIntrinsicHeight()
											* gv_btn_width
											/ icon.getIntrinsicWidth();
								}

								btn.setBackgroundResource(Integer
										.parseInt(path));

					}

					btn.setTag(TAG_CODE, map.get(c_position).get("code"));
					btn.setText(map.get(c_position).get("description"));
					btn.setTextSize(btn_height / 4f);
					if (map.get(c_position).get("code") == "") {
						// btn.setClickable(false);
						// btn.setBackgroundResource(android.R.drawable.title_bar);
					}
				} else {
					btn
							.setBackgroundResource(android.R.drawable.alert_dark_frame);
					btn.setVisibility(View.INVISIBLE);

				}
				container.addView(btn, new LinearLayout.LayoutParams(
						layout_width, layout_height));
				if (changeModeEnabled) {
					btn.setClickable(true);
					btn.setOnLongClickListener(this);
					btn.setVisibility(View.VISIBLE);
				}
				if (positon_replace_enable) {
					btn.setVisibility(View.VISIBLE);
					btn.setOnClickListener(this);
					layout.setBackgroundColor(0x77c9c9c9);
					if (pre_position == c_position)
						btn.setBackgroundColor(0x77880000);
				}
				// layout.setMinimumHeight((int) btn_height);
				// btn.setMaxHeight((int) btn_height);
			}
			layout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					return true;
				}
			});
			// layout.setBackgroundResource(R.drawable.bgconfig);
			return layout;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			System.out.println("real:" + v.getHeight() + "+" + v.getWidth());
			if (!positon_replace_enable && !changeModeEnabled) {
				Toast.makeText(mContext, v.getTag(TAG_CODE).toString(),
						Toast.LENGTH_SHORT).show();
				if (isVibrator)
					vibrator.vibrate(vibrator_strength);
				/***************************************************************************************************
				 * @author Yongkun
				 */

				if (bluetoothAdapter.isEnabled()
						&& RemoteControllerMain.bluetoothServer
								.GetDeviceState() == BluetoothConstant.STATE_CONNECTED) {
					SendIRCode(v.getTag(TAG_CODE).toString());
				} else if (!bluetoothAdapter.isEnabled()) {
					Toast.makeText(getApplicationContext(),
							R.string.tip_BluetoothDeviceUnavailable,
							Toast.LENGTH_SHORT).show();
				} else if (bluetoothAdapter.isEnabled()
						&& RemoteControllerMain.bluetoothServer
								.GetDeviceState() != BluetoothConstant.STATE_CONNECTED) {
					Toast.makeText(getApplicationContext(),
							R.string.tip_NotConnectRemote, Toast.LENGTH_SHORT)
							.show();
				}
				/**************************************************************************************************/
			}

			else if (positon_replace_enable) {
				after_position = Integer.parseInt(v.getTag().toString());
				System.out.println("pre:" + pre_position + " after:"
						+ after_position);
				dp.ChangePosition(pre_position, after_position);
				ChangeMapPosition(pre_position, after_position);
				positon_replace_enable = false;
				rc_adapter.notifyDataSetChanged();
				Save_enabled = true;
			} else {
				modify_row_pos = Integer.parseInt(v.getTag(ROWPOS).toString());
				identifier = v.getTag();
				// System.out.println("identify" + identifier);
				// TODO Auto-generated method stub

				new DialogExtension.MyBuilder(RemoteController.this).setItems(
						mContext.getResources().getStringArray(
								R.array.rc_options), new onDialogItemClick() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								switch (arg2) {
								case 0:
									super.onItemClick(arg0, arg1, arg2, arg3);
									dp.deleteButton(Integer.parseInt(identifier
											.toString()));
									map.put(Integer.parseInt(identifier
											.toString()), null);
									rc_adapter.notifyDataSetInvalidated();
									Save_enabled = true;
									Toast.makeText(RemoteController.this,
											"Delete a button",
											Toast.LENGTH_SHORT).show();
									break;
								case 1:
									super.onItemClick(arg0, arg1, arg2, arg3);
									DialogExtension.MyBuilder mb = new DialogExtension.MyBuilder(
											mContext);
									View v = inflater.inflate(
											R.layout.dialog_modify, null);
									setName = (EditText) v
											.findViewById(R.id.dialog_edit);
									mb.addView(v);
									mb.setButton(R.string.confirm,
											new onDialogButtonClick() {

												@Override
												public void onClick(View v) {
													// TODO Auto-generated
													// method stub
													int num = Integer
															.parseInt(identifier
																	.toString());
													dp
															.modifyDescription(
																	num,
																	setName
																			.getText()
																			.toString());
													if (map.get(num) == null) {
														HashMap<String, String> temp_map = new HashMap<String, String>();
														temp_map
																.put(
																		"description",
																		setName
																				.getText()
																				.toString());
														temp_map
																.put("code", "");
														temp_map
																.put("icon", "");
														map.put(num, temp_map);
													} else {
														map
																.get(num)
																.put(
																		"description",
																		setName
																				.getText()
																				.toString());
													}

													rc_adapter
															.notifyDataSetChanged();
													Save_enabled = true;
													super.onClick(v);
												}

											});

									mb.setButton(R.string.cancel,
											new onDialogButtonClick() {

												@Override
												public void onClick(View v) {
													// TODO Auto-generated
													// method stub
													super.onClick(v);
												}

											}).show();
									break;
								case 2:
									super.onItemClick(arg0, arg1, arg2, arg3);

									if (bluetoothAdapter.isEnabled()
											&& RemoteControllerMain.bluetoothServer
													.GetDeviceState() == BluetoothConstant.STATE_CONNECTED) {

										pro_dialog = new ProgressDialog(
												RemoteController.this);
										pro_dialog
												.getWindow()
												.setBackgroundDrawable(
														getResources()
																.getDrawable(
																		R.drawable.bgconfig));
										pro_dialog
												.setButton(
														"Cancel",
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(
																	DialogInterface arg0,
																	int arg1) {
																// TODO
																// Auto-generated
																// method stub
																markTimeOut = true;
																System.out
																		.println("markTimeOut = true");
															}
														});
										// pro_dialog =
										// ProgressDialog.show(RemoteController.this,
										// "",
										// "Loading. Please wait...", true);
										pro_dialog
												.setMessage("Loading. Please wait...");
										pro_dialog
												.setOnDismissListener(new SetCodeListener());
										pro_dialog.show();
										/***************************************************************************************************
										 * @author Yongkun
										 */
										// 将控制盒置为解码模式(Set decoding)
										SendModeCommand(getResources()
												.getString(
														R.string.decodingCommand));
										markDecoding = true;
										new JudgeIRCodeExistThread().start();
									} else if (!bluetoothAdapter.isEnabled()) {
										Toast
												.makeText(
														getApplicationContext(),
														R.string.tip_BluetoothDeviceUnavailable,
														Toast.LENGTH_SHORT)
												.show();
									} else if (bluetoothAdapter.isEnabled()
											&& RemoteControllerMain.bluetoothServer
													.GetDeviceState() != BluetoothConstant.STATE_CONNECTED) {
										Toast.makeText(getApplicationContext(),
												R.string.tip_NotConnectRemote,
												Toast.LENGTH_SHORT).show();
									}
									/**************************************************************************************************/
									break;
								case 3:
									super.onItemClick(arg0, arg1, arg2, arg3);

									DialogExtension.MyBuilder mdb = new DialogExtension.MyBuilder(
											mContext);

									View lo = inflater.inflate(
											R.layout.dialog_seekbar, null);
									text = (TextView) lo
											.findViewById(R.id.seekbar_text);
									dsb = (SeekBar) lo
											.findViewById(R.id.dialog_seekbar);

									dsb
											.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

												@Override
												public void onStopTrackingTouch(
														SeekBar seekBar) {
													// TODO Auto-generated
													// method stub

												}

												@Override
												public void onStartTrackingTouch(
														SeekBar seekBar) {
													// TODO Auto-generated
													// method stub

												}

												@Override
												public void onProgressChanged(
														SeekBar seekBar,
														int progress,
														boolean fromUser) {
													// TODO Auto-generated
													// method stub
													if (seekBar.getProgress() == 0)
														seekBar.setProgress(1);
													text
															.setText(String
																	.valueOf(seekBar
																			.getProgress()));
												}
											});
									dsb.setProgress(3);
									mdb.addView(lo);
									mdb.setTitle(R.string.dialog_numofrow);
									mdb
											.setButton(R.string.confirm,
													new onDialogButtonClick() {

														@Override
														public void onClick(
																View v) {

															modifyRowNum(
																	modify_row_pos,
																	dsb
																			.getProgress());
															Save_enabled = true;
															rc_adapter
																	.notifyDataSetChanged();
															super.onClick(v);
														}

													}).setButton(
													R.string.cancel, null)
											.show();

									break;
								case 4:
									super.onItemClick(arg0, arg1, arg2, arg3);
									DialogExtension.MyBuilder mab = new DialogExtension.MyBuilder(
											mContext);
									if (map.get(Integer.parseInt(identifier
											.toString())) == null) {
										mab
												.setTitle(R.string.warning)
												.setMessage(
														R.string.rc_set_btn_first)
												.setButton(R.string.confirm,
														null).show();
									} else {

										View con = inflater.inflate(
												R.layout.dialog_button_style,
												null);
										tabhost = (TabHost) con
												.findViewById(R.id.tabhost);
										tabhost.setup(RemoteController.this
												.getLocalActivityManager());

										GridView gv1 = (GridView) con
												.findViewById(R.id.tab1);
										GridView gv2 = (GridView) con
												.findViewById(R.id.tab2);
										GridView gv3 = (GridView) con
												.findViewById(R.id.tab3);

										tabhost.setup();

										List<Integer> imagelist = new ArrayList<Integer>();
										imagelist
												.add(R.drawable.btn_drawable_1);
										imagelist
												.add(R.drawable.btn_drawable_2);
										imagelist
												.add(R.drawable.btn_drawable_4);
										imagelist
												.add(R.drawable.style_return_combine);
										imagelist
												.add(R.drawable.style_unknown_combine);
										imagelist
												.add(R.drawable.style_slience_combine);
										List<Integer> imagelist1 = new ArrayList<Integer>();
										imagelist1.add(R.drawable.menu_play);
										imagelist1.add(R.drawable.menu_left);
										imagelist1.add(R.drawable.menu_right);
										imagelist1.add(R.drawable.menu_down);
										imagelist1.add(R.drawable.menu_up);
										imagelist1
												.add(R.drawable.style_down_combine);
										imagelist1
												.add(R.drawable.style_up_combine);
										// Custom
										String sdpath = Environment
												.getExternalStorageDirectory()
												.getPath();
										String extraPath = sdpath
												+ "/RemoteControllerConfig/extraButton";
										FileManager fm = new FileManager(
												RemoteController.this);

										File[] files;
										List<File> filelist = new ArrayList<File>();
										try {
											files = fm.getFilteredFiles(
													extraPath, ".jpg");
											for (File file : files) {

												filelist.add(file);

											}

											files = null;
											files = fm.getFilteredFiles(
													extraPath, ".png");

											for (File file : files) {
												filelist.add(file);
											}

										} catch (FileNotFoundException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										TabHost.TabSpec spec = tabhost
												.newTabSpec("style1");
										spec.setContent(R.id.tab1);
										View tabspec = inflater.inflate(
												R.layout.tab_spec, null);
										View tabspec1 = inflater.inflate(
												R.layout.tab_spec, null);
										spec.setIndicator("style1");

										tabhost.addTab(spec);

										iadapter1 = new MyAdapter(mContext,
												imagelist, spec, tabspec);
										iadapter2 = new MyAdapter(mContext,
												imagelist1, null, tabspec1);
										iadapter3 = new MyImageAdapter(
												mContext, filelist);
										gv1.setAdapter(iadapter1);
										gv1.setNumColumns(3);

										gv2.setAdapter(iadapter2);
										gv2.setNumColumns(3);

										gv3.setAdapter(iadapter3);
										gv3.setNumColumns(3);

										TabHost.TabSpec spec2 = tabhost
												.newTabSpec("style2");
										spec2.setContent(R.id.tab2);
										spec2.setIndicator("style2");

										tabhost.addTab(spec2);

										TabHost.TabSpec spec3 = tabhost
												.newTabSpec("formFile");

										spec3.setContent(R.id.tab3);
										spec3.setIndicator("Customization");
										tabhost.addTab(spec3);

										con.setPadding(0, 5, 0, 0);
										mab
												.setDialogContentView(con)
												.setButton(
														R.string.confirm,
														new onDialogButtonClick() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub
																switch (tabhost
																		.getCurrentTab()) {
																case 0:
																	int pos = Integer
																			.parseInt(identifier
																					.toString());
																	if (iadapter1.selected_id != 0) {
																		HashMap<String, String> tempmap = map
																				.get(pos);
																		tempmap
																				.put(
																						"icon",
																						String
																								.valueOf(iadapter1.selected_id));
																		dp
																				.modifyIcon(
																						pos,
																						String
																								.valueOf(iadapter1.selected_id));
																		
																		Save_enabled = true;

																		rc_adapter
																				.notifyDataSetChanged();
																	}
																	break;
																case 1:
																	int pos1 = Integer
																			.parseInt(identifier
																					.toString());
																	if (iadapter2.selected_id != 0) {
																		HashMap<String, String> tempmap = map
																				.get(pos1);
																		tempmap
																				.put(
																						"icon",
																						String
																								.valueOf(iadapter2.selected_id));
																		dp
																				.modifyIcon(
																						pos1,
																						String
																								.valueOf(iadapter2.selected_id));
																		Save_enabled = true;

																		rc_adapter
																				.notifyDataSetChanged();
																	}

																	break;
																case 2:
																	int pos2 = Integer
																			.parseInt(identifier
																					.toString());
																	if (iadapter3.selected_file != null) {
																		HashMap<String, String> tempmap = map
																				.get(pos2);
																		tempmap
																				.put(
																						"icon",
																						iadapter3.selected_file
																								.getPath());
																		dp
																				.modifyIcon(
																						pos2,
																						iadapter3.selected_file
																								.getPath());
																		Save_enabled = true;

																		rc_adapter
																				.notifyDataSetChanged();
																	}

																	break;
																default:
																	break;
																}
																super
																		.onClick(v);
															}

														}).setButton(
														R.string.cancel, null)
												.show();
									}
									break;
								case 5:
									super.onItemClick(arg0, arg1, arg2, arg3);

									break;
								}
								// TODO Auto-generated method stub
							}

						}).setTitle(R.string.controller_modify).show();
			}
		}

		private void ChangeMapPosition(int prePosition, int afterPosition) {
			// TODO Auto-generated method stub
			HashMap<String, String> pre_map = map.get(prePosition);
			HashMap<String, String> after_map = map.get(afterPosition);
			map.put(prePosition, after_map);
			map.put(afterPosition, pre_map);
		}

		@Override
		public boolean onLongClick(View v) {
			positon_replace_enable = true;
			pre_position = Integer.parseInt(v.getTag().toString());
			rc_adapter.notifyDataSetChanged();
			return true;
		}

		private class SetCodeListener implements
				android.content.DialogInterface.OnDismissListener {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				/***************************************************************************************************
				 * @author Yongkun
				 */
				String code = RemoteControllerMain.receiveITCode;
				RemoteControllerMain.receiveITCode = "";
				/**************************************************************************************************/
				if (!code.equals("")) {
					int num = Integer.parseInt(identifier.toString());
					HashMap<String, String> m_map = map.get(num);
					m_map.put("code", code);

					dp.modifyCode(num, code);
					Save_enabled = true;
					rc_adapter.notifyDataSetChanged();

				}
				/***************************************************************************************************
				 * @author Yongkun
				 */
				// 将控制盒置为编码模式
				if (bluetoothAdapter.isEnabled()
						&& RemoteControllerMain.bluetoothServer
								.GetDeviceState() == BluetoothConstant.STATE_CONNECTED) {
					SendModeCommand(getResources().getString(
							R.string.codingCommand));
					markDecoding = false;
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.tip_BluetoothDeviceUnavailable,
							Toast.LENGTH_SHORT).show();
				}
				/**************************************************************************************************/
			}

		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	private void updateNumRows(ArrayList<Integer> list) {
		// TODO Auto-generated method stub
		int total = 0;
		String setNumrow = "";
		for (int i = 0; i < list.size(); i++) {
			total = total + list.get(i);
			if (i == 0)
				setNumrow = setNumrow + list.get(i);
			else {
				setNumrow = setNumrow + "/";
				setNumrow = setNumrow + list.get(i);
			}
		}
		dp.createElementWithUpdate("RemoteController", "NumForeach", null,
				setNumrow, null, false);

		dp.modifyButNums(total);
	}

	private void modifyRowNum(int Rowpos, int changedNum) {
		int pre_pos = 0;
		int total = 0;
		int pre_num = numlist.get(Rowpos);
		int diff = changedNum - pre_num;

		for (int i = 0; i < numlist.size(); i++) {
			total = total + numlist.get(i);
			if (i <= Rowpos)
				pre_pos = pre_pos + numlist.get(i);
		}
		if (diff > 0) {
			for (int i = total - 1; i > pre_pos - 2; i--) {
				map.put(i + diff, map.get(i));
			}
			for (int i = 0; i < diff; i++)
				map.put(i + pre_pos, null);
		}
		if (diff < 0) {
			for (int i = pre_pos + diff; i < total - 1 + diff; i++) {
				map.put(i, map.get(i - diff));
			}
			for (int i = 0; i < Math.abs(diff); i++)
				map.remove(total - 1 - i);
		}

		if (diff != 0) {
			String setNumrow = "";
			numlist.set(Rowpos, changedNum);
			for (int i = 0; i < numlist.size(); i++) {
				if (i == 0)
					setNumrow = setNumrow + numlist.get(i);
				else {
					setNumrow = setNumrow + "/";
					setNumrow = setNumrow + numlist.get(i);
				}
			}
			dp.createElementWithUpdate("RemoteController", "NumForeach", null,
					setNumrow, null, false);

			dp.modifyRow(diff, pre_pos);
		}
	}

	/***************************************************************************************************
	 * @author Yongkun
	 */

	/**
	 * 发送模式控制命令(Send Mode Command)
	 * 
	 * @param modeCommand
	 */
	private void SendModeCommand(String modeCommand) {
		SendData(modeCommand);
	}

	/**
	 * 发送IR控制命令(Send IR Code)
	 * 
	 * @param irCode
	 */
	private void SendIRCode(String irCode) {

		if (!IsNumber(irCode)) {
			Toast.makeText(getApplicationContext(), "控制码格式不正确",
					Toast.LENGTH_SHORT).show();
			return;
		}
		SendData(irCode);

	}

	/**
	 * 发送数据(Send Data)
	 * 
	 * @param tmpStr
	 */
	private void SendData(String tmpStr) {
		// 检查连接状态(Check the Connection State)
		if (RemoteControllerMain.bluetoothServer.GetDeviceState() != BluetoothConstant.STATE_CONNECTED) {
			Toast.makeText(getApplicationContext(),
					R.string.tip_NotConnectRemote, Toast.LENGTH_SHORT).show();
			return;
		}
		// 检查是否存在被传送数据(Data is exist?)
		if (tmpStr.length() > 0) {
			// 获取消息字节，并告诉BluetoothChatService写程序(Get Message Bytes And Write
			// out)
			byte[] send = tmpStr.getBytes();
			RemoteControllerMain.bluetoothServer.write(send);
		}
	}

	/**
	 * 判读是否有红外数据学习到
	 * 
	 * @author Yongkun
	 * 
	 */
	private class JudgeIRCodeExistThread extends Thread {
		@Override
		public void run() {
			super.run();
			// timeOutThread = new TimeOutThread();
			// timeOutThread.start();
			// 若未接受到红外数据并且学习未超时，则继续等待
			while (RemoteControllerMain.receiveITCode.equals("")
					&& !markTimeOut) {

			}
			// // 中断超时
			// timeOutThread.interrupt();
			if (pro_dialog.isShowing()) {
				pro_dialog.dismiss();
			}

			// 重置标志位
			markDecoding = false;
			markTimeOut = false;
			stop();
		}
	}

	// /**
	// * 解码超时
	// * @author Yongkun
	// *
	// */
	// private class TimeOutThread extends Thread {
	// @Override
	// public void run() {
	// try {
	// sleep(TIMEOUT);
	// markTimeOut = true;
	// stop();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	private boolean IsNumber(String judgedStr) {
		Pattern pattern = Pattern.compile("^[-\\+]?[,\\d]*$");
		return pattern.matcher(judgedStr).matches();
	}

	/**************************************************************************************************/
	// 设置时取消返回
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			if (changeModeEnabled) {
				changeModeEnabled = !changeModeEnabled;
				rc_adapter.notifyDataSetChanged();
				btn_add.setVisibility(View.GONE);
				btn_save.setVisibility(View.GONE);
				btn_delete.setVisibility(View.GONE);
				if (Save_enabled) {
					DialogExtension.MyBuilder mb = new DialogExtension.MyBuilder(
							RemoteController.this);
					mb.setTitle(R.string.hint);
					mb.setMessage(R.string.dialog_save_quest).setButton(
							R.string.confirm, new onDialogButtonClick() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									dp.writeXML();
									super.onClick(v);
								}

							}).setButton(R.string.cancel,
							new onDialogButtonClick() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent i = RemoteController.this
											.getIntent();
									try {
										dp = null;
										dp = new DomParse(i
												.getStringExtra("xmlPath"));
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									map = dp.getAllWidgets("button");

									String num_eachrow = dp
											.getSingleContent("NumForeach");
									String[] nums_each = num_eachrow.split("/");

									numlist = new ArrayList<Integer>();
									for (int j = 0; j < nums_each.length; j++)
										numlist.add(Integer
												.parseInt(nums_each[j]));
									rc_adapter.notifyDataSetChanged();
									super.onClick(v);
								}

							}).show();
				}
				return true;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		return new LinearLayout(RemoteController.this);
	}

}
