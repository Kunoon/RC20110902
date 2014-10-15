package com.koobest.customization;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.SAXException;

import com.koobest.R;
import com.koobest.RemoteControllerMain;
import com.koobest.adapter.RCInformationAdapter;
import com.koobest.constant.ConfigConstant;
import com.koobest.constant.URLConstant;
import com.koobest.database.DatabaseHelper;
import com.koobest.dialog.DialogExtension;
import com.koobest.dialog.DialogExtension.MyBuilder.onDialogButtonClick;
import com.koobest.parse.DomParse;
import com.koobest.reporter.ErrorReporter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SpinnerExt extends LinearLayout implements OnClickListener,
		OnLongClickListener {
	private final String androidns = "http://schemas.android.com/apk/res/android";
	public static String[] tempP = new String[2];
	public static String[] searchParams = new String[3];

	TextView emu_spinner_tv = null;
	EditText emu_spinner_et = null;
	ImageButton emu_spinner_btn = null;

	int textSize = 20, textViewPaddingLeft = 5, linearlayou_height = 32;
	private final int TEXTVIEW_ID = 1, EDITTEXT_ID = 2, IMAGEBUTTON_ID = 3;
	private String hint = "";
	private String text = "";

	private RCInformationAdapter rc_list = null;
	private Context mContext;
	private InputMethodManager imm = null;

	private int MSG__EXCEPTION = -1;
	
	private ProgressDialog progress;
	private List<NameValuePair> params;
	private String tagName = null;
	// DialogExtension.MyBuilder dialog = null;

	Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.arg1 == 13) {
				progress.dismiss();
				Dialog dialog = (Dialog)msg.obj;
				dialog.show();
			}
			//ERROR HANDLER
			if(msg.arg1 == MSG__EXCEPTION){
				if(msg.obj != null){
					Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
					
				}
				progress.dismiss();
			}
			
			return false;
		}
	});

	public SpinnerExt(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		params = new ArrayList<NameValuePair>();

		for (int i = 0; i < 2; i++) {
			tempP[i] = "";
			searchParams[i] = "";
		}
		searchParams[2] = "";

		progress = new ProgressDialog(context);

		rc_list = new RCInformationAdapter();

		imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		emu_spinner_tv = new TextView(context);
		emu_spinner_et = new EditText(context);
		emu_spinner_btn = new ImageButton(context);

		// 设置TextView
//		hint = attrs.getAttributeValue(androidns, "hint");
		String[] separate;
		String msg = attrs.getAttributeValue(androidns, "hint");
		if(msg != null){
			separate = msg.split("@");
			
			if(separate.length != 1){
				this.hint = context.getResources().getString(Integer.parseInt(separate[1]));
			}else{
				this.hint = separate[0];
			}
		}
		
		emu_spinner_tv.setId(TEXTVIEW_ID);
		emu_spinner_tv.setFocusable(true);
		emu_spinner_tv.setBackgroundResource(R.xml.spinnerext_bg);
		emu_spinner_tv.setPadding(textViewPaddingLeft, 0, 0, 0);
		emu_spinner_tv.setHint(hint);
		emu_spinner_tv.setHintTextColor(Color.GRAY);
		emu_spinner_tv.setText(text);
		emu_spinner_tv.setTextSize(textSize);
		emu_spinner_tv.setTextColor(Color.BLACK);

		// 设置EditView
		emu_spinner_et.setId(EDITTEXT_ID);
		emu_spinner_et.setBackgroundResource(R.xml.spinnerext_edittext_bg);
		emu_spinner_et.setPadding(textViewPaddingLeft, 0, 0, 0);
		emu_spinner_et.setTextSize(textSize);

		emu_spinner_et.setVisibility(GONE);
		emu_spinner_et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				System.out.println(hasFocus + " focusChangelistener");
				if (SpinnerExt.this.getId() == R.id.spext_mnf && !hasFocus) {
					tempP[0] = emu_spinner_et.getText().toString();
				}
				if (SpinnerExt.this.getId() == R.id.spext_type && !hasFocus) {
					tempP[1] = emu_spinner_et.getText().toString();
				}
			}
		});
		// 设置ImageButton
		emu_spinner_btn.setId(IMAGEBUTTON_ID);
		emu_spinner_btn.setBackgroundResource(R.drawable.dropdown);
		emu_spinner_btn.setAdjustViewBounds(true);

		// 设置监听
		emu_spinner_tv.setOnClickListener(this);
		emu_spinner_et.setOnClickListener(this);
		emu_spinner_btn.setOnClickListener(this);

		// 设置长按监听
		emu_spinner_tv.setOnLongClickListener(this);
		emu_spinner_et.setOnLongClickListener(this);

		this.addView(emu_spinner_tv, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 1));
		this.addView(emu_spinner_et, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 1));
		this.addView(emu_spinner_btn, new LayoutParams(linearlayou_height,linearlayou_height));

		// this.addView(emu_spinner_tv);
	}

	public void setAdapter(RCInformationAdapter list) {
		this.rc_list = list;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// final DialogExtension.MyBuilder dialog = new
		// DialogExtension.MyBuilder(mContext);
		final Dialog dialog = new Dialog(mContext);
		switch (this.getId()) {
		case R.id.spext_mnf:
			params.add(new BasicNameValuePair(URLConstant.PREFIX,URLConstant.MANUFACTURERS_ROUTE));

//	        params.add(new BasicNameValuePair("route","product/product"));
//	        params.add(new BasicNameValuePair("product_id","51"));
			tagName = "manufacturer";
			break;
		case R.id.spext_type:
			params.add(new BasicNameValuePair(URLConstant.PREFIX,URLConstant.CATEGORIES_ROUTE));
			tagName = "category";
			break;
		case R.id.spext_model:
			params.add(0,new BasicNameValuePair(URLConstant.PREFIX,URLConstant.MODELS_ROUTE));
			params.add(new BasicNameValuePair("manufacturer", tempP[0]));
			params.add(new BasicNameValuePair("category", tempP[1]));
			tagName = "model";
			break;
		}

		switch (v.getId()) {
		case IMAGEBUTTON_ID:
		case TEXTVIEW_ID:
			if (this.rc_list == null)
				throw new NullPointerException("Adapter is undefined");
			
			System.out.println("onclick");
			
			progress.show();

			Thread thd = new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					ListView datalist = new ListView(mContext);
					Message msg = mHandler.obtainMessage();
					try {
						ArrayList<String> info = new ArrayList<String>();
						datalist.setCacheColorHint(Color.TRANSPARENT);
						// rc_list.setDataSource(params);
						if (tagName.equals("model")) {
							DomParse dom = rc_list.getClient().getResultsByGet(
									params, null);
							HashMap<Integer, HashMap<String, String>> results = dom
									.getAllWidgets(tagName);

							for (int i = 0; i < results.size(); i++) {
								Set<String> keyset = results.get(i).keySet();
								Object[] keyarray = keyset.toArray();
								for (int j = 0; j < results.get(i).size(); j++) {
//									System.out.println(keyarray[j].toString()+ " "+ results.get(i).get(keyarray[j].toString()));
									info.add(results.get(i).get(keyarray[j].toString()));
									// this.notifyDataSetChanged();

								}
							}
						} else {
							DatabaseHelper dbHelper = new DatabaseHelper(
									mContext, "rc_test_db");
							SQLiteDatabase db = dbHelper.getReadableDatabase();
							String tag_id = tagName + "_id";
							String tag_name = tagName + "_name";
							Cursor cursor = db.query(tagName, new String[] {
									tag_id, tag_name }, null, null, null, null,
									null);
							while (cursor.moveToNext()) {
								String name = cursor.getString(cursor.getColumnIndex(tag_name));
//								System.out.println("query--->" + name);
								info.add(name);
							}
							cursor.close();
							db.close();
						}
						// }
						rc_list.setDataSource(info);

						info.add(0, "clear");

						datalist.setAdapter(rc_list);
						datalist.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										// TODO Auto-generated method stub
										TextView temp = (TextView) arg1;
										if (temp.getTag() != null
												&& temp.getTag().toString()
														.equals("clear")) {
											emu_spinner_tv.setText("");
											if (SpinnerExt.this.getId() == R.id.spext_mnf) {
												tempP[0] = "";
												searchParams[0] = "";
											} else if (SpinnerExt.this.getId() == R.id.spext_type) {
												tempP[1] = "";
												searchParams[1] = "";
											} else if (SpinnerExt.this.getId() == R.id.spext_model) {
												searchParams[2] = "";
											}

										} else {
											emu_spinner_tv.setText(temp
													.getText());
											if (SpinnerExt.this.getId() == R.id.spext_mnf) {
												tempP[0] = temp.getText()
														.toString();
												searchParams[0] = tempP[0];
											}
											if (SpinnerExt.this.getId() == R.id.spext_type) {
												tempP[1] = temp.getText()
														.toString();
												searchParams[1] = tempP[1];
											}
											if (SpinnerExt.this.getId() == R.id.spext_model) {
												searchParams[2] = temp.getText().toString();
											}
										}
										dialog.dismiss();
									}
								});

						dialog.setTitle(R.string.socket_choice_list_title);
						dialog.getWindow().setBackgroundDrawableResource(R.drawable.dirbackground);
						dialog.addContentView(datalist, new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT));
						params.clear();
						System.out.println("Thread finish");
					} catch (SAXException saxe){
						System.out.println(saxe.getMessage() + " ParseError");
						msg.arg1 = MSG__EXCEPTION;
						msg.obj = mContext.getResources().getString(R.string.exception_sax) + "_" + saxe.getMessage();
						mHandler.sendMessage(msg);
						return;
					} catch (ParserConfigurationException e){
						System.out.println(e.getMessage() + " IOException from spinnerEXT");
						msg.arg1 = MSG__EXCEPTION;
						msg.obj = mContext.getResources().getString(R.string.exception_time_out) + "_" + e.getMessage();
						return;
					} catch (ConnectTimeoutException e){
						System.out.println(e.getMessage() + " ConnectionException from spinnerEXT");
						msg.arg1 = MSG__EXCEPTION;
						msg.obj = mContext.getResources().getString(R.string.exception_time_out) + "_" + e.getMessage();
						mHandler.sendMessage(msg);
						return;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage() + " from spinnerEXT " + e.getClass().getName()) ;
						msg.arg1 = MSG__EXCEPTION;
						msg.obj = mContext.getResources().getString(R.string.exception_io) + "_" + e.getMessage();
						mHandler.sendMessage(msg);
						return;
						
					} catch (URISyntaxException e){
						System.out.println(e.getMessage() + " URISyn from spinnerEXT" );
						msg.arg1 = MSG__EXCEPTION;
						msg.obj = mContext.getResources().getString(R.string.exception_urisyn) + "_" + e.getMessage();
						mHandler.sendMessage(msg);
						return;
					}  catch (Exception e){
						msg.arg1 = MSG__EXCEPTION;
						msg.obj = mContext.getResources().getString(R.string.exception_unknown) + "_" + e.getMessage();
						mHandler.sendMessage(msg);
						System.out.println(e.getMessage() + " Excpetion from spinnerExt" );
						return;
					} 

					
					msg.obj = dialog;
					msg.arg1 = 13;
					mHandler.sendMessage(msg);

					super.run();
				}

			};
			//				
			thd.start();
			break;
		case EDITTEXT_ID:
			System.out.println("eidt click");
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case TEXTVIEW_ID:

			// 设置TextView消失
			emu_spinner_tv.setVisibility(GONE);
			// 设置Button消失
			emu_spinner_btn.setVisibility(GONE);
			// 设置EditText显示
			emu_spinner_et.setVisibility(VISIBLE);
			emu_spinner_et.setFocusable(true);
			
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			
			break;
		case EDITTEXT_ID:
			emu_spinner_et.setText("");
			emu_spinner_et.setVisibility(GONE);
			
			imm.hideSoftInputFromWindow(getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			
			emu_spinner_tv.setVisibility(VISIBLE);
			emu_spinner_btn.setVisibility(VISIBLE);
			break;
		}
		return true;
	}

	public String getChoice() {
		if (emu_spinner_et.getText().toString() == null
				|| emu_spinner_et.getText().toString().trim().length() == 0) {

			return emu_spinner_tv.getText().toString();
		} else
			return emu_spinner_et.getText().toString();
	}

}
