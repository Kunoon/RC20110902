package com.koobest.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import com.koobest.R;
import com.koobest.dialog.DialogWindow;
import com.koobest.menu.DialogMenu.MyBuilder.MenuAdapter;
import com.koobest.menu.DialogMenu.MyBuilder.OnItemClick;
import com.koobest.parse.BitmapManager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DialogMenu extends Dialog {

	public DialogMenu(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static class MyBuilder {
		private Context mcontext;
		private static List<Context> static_context = new ArrayList<Context>();
		private int theme_res;
		private int bg_res;
		private DialogWindow md;
		private List<Integer> item_id = new ArrayList<Integer>();
		private List<Integer> sub_menu_id = new ArrayList<Integer>();
		static private List<List<Integer>> static_sub_menu_id = new ArrayList<List<Integer>>();
		private List<Integer> visibal_item = new ArrayList<Integer>();
		private List<Boolean> item_visibal = new ArrayList<Boolean>();
		private HashMap<Integer, List<Integer>> sub_menu_items = new HashMap<Integer, List<Integer>>();
		static private List<HashMap<Integer, List<Integer>>> static_sub_menu_items = new ArrayList<HashMap<Integer, List<Integer>>>();
		private HashMap<Integer, String> item_title = new HashMap<Integer, String>();
		static private List<HashMap<Integer, String>> static_item_title = new ArrayList<HashMap<Integer, String>>();
		private HashMap<Integer, Integer> item_icon = new HashMap<Integer, Integer>();
		static private List<HashMap<Integer, Integer>> static_item_icon = new ArrayList<HashMap<Integer, Integer>>();
		private HashMap<Integer, Integer> id_to_pos = new HashMap<Integer, Integer>();
		private HashMap<Integer, OnItemClick> sub_item_listener = new HashMap<Integer, OnItemClick>();
		// private List<HashMap<Integer, OnItemClick>> sub_item_listeners = new
		// ArrayList<HashMap<Integer,OnItemClick>>();
		static private List<HashMap<Integer, OnItemClick>> static_sub_item_listeners = new ArrayList<HashMap<Integer, OnItemClick>>();
		private LayoutInflater inflater;
		private int num_colums = 0;
		private GridView gv;
		private MenuAdapter ma;
		static private List<MenuAdapter> static_ma = new ArrayList<MenuAdapter>();
		private OnItemClick listener = null;
		private boolean orientation;
		private int textsize = 0;
		private static List<DialogWindow> sd = new ArrayList<DialogWindow>();
		private static boolean dismissbyclick = false;

		public MyBuilder(Context context) {

			this(context, 0, 0);
		}

		public MyBuilder(Context context, int bg_id) {

			this(context, bg_id, 0);
		}

		public MyBuilder(Context context, int bg_id, int theme_id) {
			mcontext = context;
			theme_res = theme_id;
			bg_res = bg_id;
			if (theme_res == 0)
				md = new DialogWindow(mcontext);
			else
				md = new DialogWindow(mcontext, theme_res);
			md.setBackGroundId(R.drawable.menu_bg);
			if (bg_res != 0)
				md.setBackGroundId(bg_res);

			inflater = LayoutInflater.from(mcontext);
			md.setCanceledOnTouchOutside(true);
			md.getWindow().getAttributes().dimAmount = 0;
			md.setGravity(Gravity.BOTTOM);
			md.setWidth(LayoutParams.FILL_PARENT);
			
			setMenu();
		}

		public void setTextSize(int size) {
			textsize = size;
		}

		public void setWidth(int width) {
			md.setWidth(width);
		}

		public void setGravity(int gravity) {
			md.setGravity(gravity);
		}

		public void setNumColums(int num) {
			num_colums = num;
		}

		public MyBuilder addMenu(int id, String title) {
			item_id.add(id);
			item_visibal.add(true);
			id_to_pos.put(id, item_id.size() - 1);
			item_title.put(id, title);
			return this;
		}

		public MyBuilder addMenu(int id, int title_res) {
			item_id.add(id);
			item_visibal.add(true);
			id_to_pos.put(id, item_id.size() - 1);
			item_title.put(id, mcontext.getResources().getString(title_res));
			return this;
		}

		public MyBuilder addSubMenu(int id, String string) {
			// TODO Auto-generated method stub
			item_id.add(id);
			sub_menu_id.add(id);
			item_visibal.add(true);
			id_to_pos.put(id, item_id.size() - 1);
			item_title.put(id, string);
			return this;
		}

		public MyBuilder addSubMenu(int id, int res) {
			// TODO Auto-generated method stub
			item_id.add(id);
			sub_menu_id.add(id);
			item_visibal.add(true);
			id_to_pos.put(id, item_id.size() - 1);
			item_title.put(id, mcontext.getResources().getString(res));
			return this;
		}

		public void addSubMenuItem(int id, int sub_item_id, String string) {
			// TODO Auto-generated method stub
			if (sub_menu_items.get(id) == null) {
				List<Integer> li = new ArrayList<Integer>();
				li.add(sub_item_id);
				sub_menu_items.put(id, li);
			} else {
				sub_menu_items.get(id).add(sub_item_id);
			}
			item_title.put(sub_item_id, string);
		}

		public void addSubMenuItem(int id, int sub_item_id, int string) {
			// TODO Auto-generated method stub
			if (sub_menu_items.get(id) == null) {
				List<Integer> li = new ArrayList<Integer>();
				li.add(sub_item_id);
				System.out.println(id);
				sub_menu_items.put(id, li);
			} else {
				sub_menu_items.get(id).add(sub_item_id);
			}
			item_title.put(sub_item_id, mcontext.getResources().getString(
					string));
		}

		public MyBuilder setItemVisible(int id, boolean visible) {
			int pos = id_to_pos.get(id);
			item_visibal.set(pos, visible);
			return this;
		}

		public MyBuilder setItemTitle(int id, int title_res) {
			item_title.put(id, mcontext.getResources().getString(title_res));
			return this;
		}

		public MyBuilder setItemTitle(int id, String title) {
			item_title.put(id, title);
			return this;
		}

		public MyBuilder setMenuIcon(int id, int icon_res) {
			item_icon.put(id, icon_res);
			return this;
		}

		public void setSubMenuListner(int id, OnItemClick onItemClick) {
			// TODO Auto-generated method stub
			sub_item_listener.put(id, onItemClick);
		}

		public void setMenu() {
			gv = new GridView(mcontext);
			ma = new MenuAdapter();
			gv.setAdapter(ma);
			gv.setHorizontalSpacing(5);
			gv.setSelector(R.xml.shelf_item_background);
			gv.setNumColumns(num_colums);
			gv.setOnKeyListener(new View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					switch (keyCode) {
					case KeyEvent.KEYCODE_MENU:
						md.dismiss();
						return true;
					default:
						break;
					}
					return false;
				}
			});

			md.setContentView(gv);
		}

		public void setItemClickListener(OnItemClick li) {
			listener = li;
		}

		public void setBackGround(int bg_id) {
			md.setBackGroundId(bg_id);
		}

		public Window getWindow() {
			return md.getWindow();
		}


		public MyBuilder setOrientation(boolean voh) {
			orientation = voh;
			return this;
		}

		public void show() {
			visibal_item = new ArrayList<Integer>();
			for (int i = 0; i < item_id.size(); i++) {
				if (item_visibal.get(i) == true)
					visibal_item.add(i);
			}
			ma.notifyDataSetChanged();
			System.out.println("shown");
			if (num_colums != 0)
				gv.setNumColumns(num_colums);
			else {
				if (ma.getCount() == 1)
					gv.setNumColumns(1);
				else if (ma.getCount() <= 4)
					gv.setNumColumns(2);
				else if (ma.getCount() <= 6)
					gv.setNumColumns(3);
				else if (ma.getCount() >= 7)
					gv.setNumColumns(4);
			}
			md.show();
			System.out.println(sub_menu_items.get(4));
			static_sub_menu_items.add(sub_menu_items);
			static_item_icon.add(item_icon);
			static_item_title.add(item_title);
			static_sub_menu_id.add(sub_menu_id);
			static_context.add(mcontext);
			static_sub_item_listeners.add(sub_item_listener);
			static_ma.add(ma);
			md.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					if (!dismissbyclick) {
						System.out.println("dissmiss is called");
						if (sd.size() > 0)
							MenuDismiss();
					}
				}
			});
			sd.add(md);
		}

		public class MenuAdapter extends BaseAdapter {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return visibal_item.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return item_id.get(position);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				// View v = inflater.inflate(R.layout.menu_item, null);
				int id = item_id.get(visibal_item.get(position));
				MenuItemLayout li = new MenuItemLayout(mcontext);
				if (item_icon.get(id) != null) {

					// Drawable icon =
					// mcontext.getResources().getDrawable(item_icon.get(id));
					// BitmapDrawable bd = (BitmapDrawable) icon;
					// Bitmap bm = bd.getBitmap();
					// Toast.makeText(mcontext, bm.getWidth()+"+"bm.getHeight(),
					// duration)
					int size = getWindow().getWindowManager()
							.getDefaultDisplay().getWidth() / 10;
					// bm = BitmapManager.zoomBitmap(bm, 0, size
					// /bm.getHeight());
					// icon = new BitmapDrawable(bm);
					li.image.setBackgroundResource(item_icon.get(id));
					li.image.setAdjustViewBounds(true);
					li.image.setMaxWidth(size);
				}
				if (orientation)
					li.li.setOrientation(LinearLayout.HORIZONTAL);
				li.text.setText(item_title.get(id));
				li.text.setGravity(Gravity.LEFT);
				if (textsize != 0)
					li.text.setTextSize(textsize);
				li.setGravity(Gravity.CENTER);
				li.setBackgroundResource(R.drawable.bg_menu_selector);
				li.setTag(id);
				// li.setPadding(5, 5, 5, 5);
				// li.setLayoutParams(new
				// LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				if (listener != null)
					li.setOnClickListener(listener);
				else {
					li.setOnClickListener(new OnItemClick() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							super.onClick(v);
						}

					});
				}

				return li;
			}
		}

		static public class OnItemClick implements View.OnClickListener {

			@Override
			public void onClick(View v) {
				System.out.println("clicked");
				// TODO Auto-generated method stub
				boolean dismiss = true;
				try{
				for (int i = 0; i < static_sub_menu_id.get(static_sub_menu_id.size() - 1).size(); i++) {
					if (Integer.valueOf(v.getTag().toString()) == static_sub_menu_id
							.get(static_sub_menu_id.size() - 1).get(i)) {
						// System.out.println(sd.get(sd.size()-1).getWindow().getAttributes().y);
						// System.out.println("this is submenu,Top:"+v.getTop()+", Left:"+v.getLeft());
						// List<Integer> sub_id =
						// static_sub_menu_id.get(static_sub_menu_id.size()-1);
						HashMap<Integer, List<Integer>> sub_items = static_sub_menu_items
								.get(static_sub_menu_items.size() - 1);
						HashMap<Integer, String> sub_title = static_item_title
								.get(static_item_title.size() - 1);
						List<Integer> items = sub_items.get(Integer.valueOf(v
								.getTag().toString()));
						// System.out.println(Integer.valueOf(v.getTag().toString()));
						if (items == null)
							;
						else {
							dismiss = false;
							v.setBackgroundResource(R.drawable.bg_menu_item_selected);
							DialogMenu.MyBuilder subbuilder = new MyBuilder(
									static_context
											.get(static_context.size() - 1));
							HashMap<Integer, Integer> icons = static_item_icon
									.get(static_item_icon.size() - 1);
							for (int j = 0; j < items.size(); j++) {
								subbuilder.addMenu(items.get(j), sub_title
										.get(items.get(j)));
								if (icons.get(items.get(j)) != null)
									subbuilder.setMenuIcon(items.get(j), icons
											.get(items.get(j)));
							}
							subbuilder.setNumColums(1);
							subbuilder.setBackGround(R.drawable.bg_listview);
							android.view.WindowManager.LayoutParams ab = subbuilder
									.getWindow().getAttributes();
							subbuilder.setWidth((int) (subbuilder.getWindow()
									.getWindowManager().getDefaultDisplay()
									.getWidth() * 0.5f));
							subbuilder.setOrientation(true);
//							subbuilder.setOuterClickDismiss(true);
							subbuilder
									.setItemClickListener(static_sub_item_listeners
											.get(
													static_sub_item_listeners
															.size() - 1).get(
													Integer.valueOf(v.getTag()
															.toString())));
							subbuilder.setTextSize(18);
							ab.gravity = Gravity.BOTTOM;
							ab.y = v.getHeight();
							ab.x = (int) (v.getLeft() - subbuilder.getWindow()
									.getWindowManager().getDefaultDisplay()
									.getWidth() * 0.3);
							// subbuilder.
							subbuilder.show();
							// v.setBackgroundResource(R.drawable.bg_menu_item_selected);
						}
					}
				}
				}
				catch(ArrayIndexOutOfBoundsException e){
					System.out.println("DialogMenu 446 message " + e.getMessage());
					return;
				}
				if (dismiss) {
					int total = sd.size();
					for (int num = 0; num < total; num++) {
						dismissbyclick = true;
						System.out.println("dismiss");
						sd.get(total - num - 1).dismiss();
						DialogMenu.MyBuilder.MenuDismiss();
					}
					dismissbyclick = false;
				}
			}
		}

		static void MenuDismiss() {
			System.out.println("clear cache");
			sd.remove(sd.size() - 1);
			static_item_icon.remove(static_item_icon.size() - 1);
			static_item_title.remove(static_item_title.size() - 1);
			static_sub_menu_id.remove(static_sub_menu_id.size() - 1);
			static_sub_menu_items.remove(static_sub_menu_items.size() - 1);
			static_context.remove(static_context.size() - 1);
			static_ma.remove(static_ma.size() - 1);
			static_sub_item_listeners
					.remove(static_sub_item_listeners.size() - 1);
			if (static_ma.size() > 0)
				static_ma.get(static_ma.size() - 1).notifyDataSetChanged();

		}
	}
}
