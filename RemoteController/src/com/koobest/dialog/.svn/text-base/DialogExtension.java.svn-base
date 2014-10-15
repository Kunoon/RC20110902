package com.koobest.dialog;


import java.util.ArrayList;
import java.util.List;

import com.koobest.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DialogExtension extends Dialog {

	public DialogExtension(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static class MyBuilder {
		private Context mContext;
		public DialogWindow md;
		static private List<DialogWindow> md_list = new ArrayList<DialogWindow>();
		private LinearLayout dialogLayout;
		WindowManager wm = null;
		private LinearLayout.LayoutParams simple, pweight, pcenter;
		private String title = null, message = null;
		private LinearLayout buttonsLayout = null;
		private View view = null;
		private View dialogContentView = null;
		private LinearLayout ViewLayout;
		private ListView dv = null;
		private LayoutInflater inflater;
		private final int Btn_text_size = 20, Title_text_size = 25,
				Mess_text_size = 18;
		
		private int TITLE_TEXT_COLOR = Color.WHITE,MSG_TEXT_COLOR = Color.rgb(112, 146, 190);
		public MyBuilder(Context context) {
			this.mContext = context;
			wm = (WindowManager) context.getSystemService("window");

			dialogLayout = new LinearLayout(context);
			ViewLayout = new LinearLayout(context);
			ViewLayout.setOrientation(1);
			dialogLayout.setMinimumWidth((int) (wm.getDefaultDisplay()
					.getWidth() * 0.8));
			dialogLayout.setOrientation(1);

			md = new DialogWindow(context);

			buttonsLayout = new LinearLayout(context);
			buttonsLayout.setOrientation(0);
			layoutset();
		}

		public MyBuilder(Context context, int shapeResourceId) {
			this.mContext = context;
			wm = (WindowManager) context.getSystemService("window");

			dialogLayout = new LinearLayout(context);
			dialogLayout.setMinimumWidth((int) (wm.getDefaultDisplay()
					.getWidth() * 0.8));
			dialogLayout.setOrientation(1);

			ViewLayout = new LinearLayout(context);
			ViewLayout.setOrientation(1);

			md = new DialogWindow(context);
			md.setBackGroundId(shapeResourceId);

			buttonsLayout = new LinearLayout(context);
			buttonsLayout.setOrientation(0);
			layoutset();

		}

		private void layoutset() {
			// TODO Auto-generated method stub
			inflater = LayoutInflater.from(mContext);
			ViewLayout.setVisibility(View.GONE);
			simple = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			pweight = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT,1);
			pcenter = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			pcenter.gravity = Gravity.CENTER;
			pweight.leftMargin = 20;
			pweight.rightMargin = 20;
			pweight.topMargin = 0;
			pweight.bottomMargin = 15;
		}

		public MyBuilder setTitle(String title) {
			this.title = title;
			return this;
		}

		public MyBuilder setTitle(int tip) {
			// TODO Auto-generated method stub
			this.title = mContext.getResources().getString(tip);
			return this;
		}

		public MyBuilder addView(View view) {
			this.view = view;
			return this;
		}

		public MyBuilder setItems(String[] options, onDialogItemClick oc) {
			dv = new ListView(mContext);
			DialogAdapter da = new DialogAdapter(mContext, options, md);
			System.out.println(da);
			dv.setAdapter(da);
			dv.setOnItemClickListener(oc);
			dv.setBackgroundResource(R.drawable.bg_listview);
			dv.setCacheColorHint(Color.TRANSPARENT);
			dv.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						System.out.println(v.getTag());
						// md.dismiss();
						break;
					case MotionEvent.ACTION_DOWN:
						System.out.println(v.getTag());
						// md.dismiss();
						break;
					}

					return false;
				}
			});
			return this;
		}
		
		public MyBuilder setItems(String[] options, onDialogItemClick oc , int items_bg_id) {
			dv = new ListView(mContext);
			DialogAdapter da = new DialogAdapter(mContext, options, md);
			System.out.println(da);
			dv.setAdapter(da);
			dv.setOnItemClickListener(oc);
			dv.setBackgroundResource(items_bg_id);
			dv.setCacheColorHint(Color.TRANSPARENT);
			dv.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						System.out.println(v.getTag());
						// md.dismiss();
						break;
					case MotionEvent.ACTION_DOWN:
						System.out.println(v.getTag());
						// md.dismiss();
						break;
					}

					return false;
				}
			});
			return this;
		}

		public MyBuilder setButton(Button button) {

			buttonsLayout.addView(button, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
			return this;
		}

		public MyBuilder setButton(int ofCourse, onDialogButtonClick btnonclick) {
			// TODO Auto-generated method stub
			Button newbtn = new Button(mContext);
			newbtn.setText(mContext.getResources().getString(ofCourse));
			newbtn.setTextSize(Btn_text_size);
			
			newbtn.setGravity(Gravity.CENTER);
			newbtn.setBackgroundResource(R.drawable.btn_dialog);
			if (btnonclick == null) {
				newbtn.setOnClickListener(new onDialogButtonClick() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						super.onClick(v);
					}

				});
			} else {
				newbtn.setOnClickListener(btnonclick);
			}

			buttonsLayout.addView(newbtn, pweight);
			return this;
		}

		public MyBuilder setButton(String name, onDialogButtonClick btnonclick) {
			Button newbtn = new Button(mContext);
			newbtn.setText(name);
			newbtn.setTextSize(Btn_text_size);
			newbtn.setBackgroundResource(R.drawable.btn_dialog);
//			newbtn.setPadding(20, 10, 20, 10);
			newbtn.setGravity(Gravity.CENTER);
			if (btnonclick == null) {
				newbtn.setOnClickListener(new onDialogButtonClick() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						super.onClick(v);
					}

				});
			} else {
				newbtn.setOnClickListener(btnonclick);
			}

			buttonsLayout.addView(newbtn, pweight);
			return this;
		}

		public MyBuilder setMessage(String mes) {
			message = mes;
			return this;
		}

		public MyBuilder setMessage(int rcDialogNotice) {
			// TODO Auto-generated method stub
			message = mContext.getResources().getString(rcDialogNotice);
			return this;
		}

		public MyBuilder setDialogContentView(int resid) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			dialogContentView = inflater.inflate(resid, null);

			return this;
		}

		public MyBuilder setDialogContentView(View view) {
			this.dialogContentView = view;
			return this;
		}

		public void show() {
			mdCreat();
			md.show();

		}
		private void mdCreat(){
			if (title != null) {
				TextView tvtitle = new TextView(mContext);
				tvtitle.setText(title);
				tvtitle.setTextSize(Title_text_size);
				tvtitle.setShadowLayer(2, 2, 2, 0xaaaaaaaa);
				tvtitle.setTextColor(this.TITLE_TEXT_COLOR);
//				tvtitle.setTextColor(Color.rgb(255, 242, 0));
				tvtitle.setGravity(Gravity.CENTER);
				tvtitle.setPadding(5, 5, 5, 5);
				// tvtitle.setBackgroundResource(R.drawable.bg_title);
				dialogLayout.addView(tvtitle, simple);
				// md.setTitle(title);
			}
			if (message != null) {
				TextView mes = new TextView(mContext);
				// TextView mes = (TextView)
				// inflater.inflate(R.layout.dialong_message,
				// null).findViewById(R.id.dialog_mes);
				mes.setText(message);
				mes.setTextSize(Mess_text_size);
				mes.setPadding(20, 0, 20, 0);
				mes.setTextColor(this.MSG_TEXT_COLOR);
				mes.setGravity(Gravity.CENTER);
				ViewLayout.addView(mes, simple);
				ViewLayout.setVisibility(View.VISIBLE);
			}
			if (dv != null)
				dialogLayout.addView(dv, simple);
			if (view != null) {
				ViewLayout.addView(view, simple);
				ViewLayout.setVisibility(View.VISIBLE);

			}

			if (dialogContentView == null) {
				System.out.println("childs" + ViewLayout.getChildCount());
				ViewLayout.setPadding(10, 2, 10, 10);
				dialogLayout.addView(ViewLayout, simple);
				if (buttonsLayout.getChildCount() != 0) {
					buttonsLayout.setBackgroundResource(R.drawable.bg_buttons);
					ViewLayout.setVisibility(View.VISIBLE);
					dialogLayout.addView(buttonsLayout, simple);
				}
//				md.setCanceledOnTouchOutside(true);
				md.setContentView(dialogLayout);
			} else {
				dialogLayout.addView(dialogContentView, pweight);
				if (buttonsLayout.getChildCount() != 0) {
					buttonsLayout.setBackgroundResource(R.drawable.bg_buttons);
					ViewLayout.setVisibility(View.VISIBLE);
					dialogLayout.addView(buttonsLayout, simple);
				}
				md.setContentView(dialogLayout);

			}
			md_list.add(md);
			System.out.println("added");
			
		}
		public void dismiss(){
			md.dismiss();
		}
		
		
		public DialogWindow creat(){
			mdCreat();

			return md;
		}
		
		
		public static class onDialogButtonClick implements View.OnClickListener {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				md_list.get(md_list.size()-1).dismiss();
//				md_list.remove(md_list.size()-1);
//				System.out.println("from dialogExt:dismiss");
				try{
					System.out.println("list_nums:"+md_list.size());
					if(md_list.size() > 0){
						md_list.get(md_list.size()-1).dismiss();
						md_list.remove(md_list.size()-1);
					}
				} catch (ArrayIndexOutOfBoundsException e){
					System.out.println(e.getMessage() + " ArrayIndexOutOfBoundsException Dialog Extension " + md_list.size());
					return;
				}
			}
		}

		public static class onDialogItemClick implements OnItemClickListener {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				try{
					System.out.println("list_nums:"+md_list.size());
					if(md_list.size() > 0){
						md_list.get(md_list.size()-1).dismiss();
						md_list.remove(md_list.size()-1);
					}
				} catch (ArrayIndexOutOfBoundsException e){
					System.out.println(e.getMessage() + " ArrayIndexOutOfBoundsException Dialog Extension " + md_list.size() + " " + arg2);
					return;
				}
			}

		}

	}


}
