package com.koobest.customization;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.koobest.R;
import com.koobest.parse.BitmapManager;
import com.koobest.parse.FileManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebSettings.TextSize;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class DirManager implements OnItemClickListener, OnClickListener {
	/** Called when the activity is first created. */
	private int CHANGE_RCICON_MSG = 21;
	
	FileManager fManager;
	File parentFile;
	File[] dirs;
	File[] childDirector;
	public PopupWindow popWin;
	boolean isChild = false;
	private boolean intoDir = false;
	
	Context mContext;
	ListView fileList;
	FileAdapter fAdapter;
	LayoutInflater inflater;
	Handler mHandler, readHandler, notifyHandler;

	public static boolean threadFinished = true;
	public boolean isFirst = true;

	View main_layout;

	private int popHeight = 0;
	private int popWidth = 0;
	private float yScale = 1.0f;
	private float xScale = 1.0f;

//	private int MAXLENGTH = 0;
//	private int CACHESIZELIMIE = 100;

	public int times = 0;

	private HandlerThread handler_t;
	protected boolean back_clicked = false;
	
	private String selectedPath = null;
	
	private ArrayList<String> imageDirector;
	public DirManager(Context context, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
		
		imageDirector = new ArrayList<String>();
		
		handler_t = new HandlerThread("handler_thread");
		handler_t.start();
		notifyHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				fAdapter.notifyDataSetChanged();
				return false;
			}
		});

		readHandler = new Handler(handler_t.getLooper(),
				new Handler.Callback() {

					@SuppressWarnings("unchecked")
					@Override
					public boolean handleMessage(Message msg) {
						// TODO Auto-generated method stub

						fAdapter.refreshCahce((HashMap<String, Bitmap>) msg.obj);
						Message msg1 = notifyHandler.obtainMessage();
						notifyHandler.sendMessage(msg1);
						return false;
					}
				});

		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		wm.getDefaultDisplay().getMetrics(metrics);

		popHeight = (int) (metrics.heightPixels * yScale);
		popWidth = (int) (metrics.widthPixels * xScale);

//		if (popWidth > popHeight)
//			MAXLENGTH = popWidth / 3;
//		else
//			MAXLENGTH = popHeight / 3;

		inflater = LayoutInflater.from(context);
		View pop_main = inflater.inflate(R.layout.popup_window_main, null);
		
		File root = Environment.getExternalStorageDirectory();
		// File root = Environment.getRootDirectory().listFiles()[0];

		if (!root.canRead())
			throw new NullPointerException("SDCard can not be read");

		String sdPath = root.getPath();

		ImageView back = (ImageView) pop_main.findViewById(R.id.img_back);
		ImageView cancel = (ImageView) pop_main.findViewById(R.id.img_cancel);
		
		
		
		back.setOnClickListener(this);
		cancel.setOnClickListener(this);

		File files = new File(sdPath);

		dirs = files.listFiles();

		Arrays.sort(dirs);
		dirs = FileManager.FilesSort(dirs);


		fileList = (ListView) pop_main.findViewById(R.id.dm_pop_listview);
		fileList.setClickable(false);
		fAdapter = new FileAdapter(mContext);

		fileList.setAdapter(fAdapter);

		fileList.setOnItemClickListener(this);
//		fileList.setOnItemLongClickListener(this);
		fileList.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				fAdapter.removeHandlerCallBack();
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					switch (dirs.length) {
					case 0:
						if (parentFile.getParent() == null)
							Toast.makeText(mContext, "top director",
									Toast.LENGTH_SHORT).show();
						else {
							dirs = parentFile.getParentFile().listFiles();
							Arrays.sort(dirs);
							dirs = FileManager.FilesSort(dirs);
							fAdapter.setDir(dirs);
							fAdapter.notifyDataSetChanged();
						}

						break;
					default:
						if (dirs[0].getParentFile().getParent() == null)
							popWin.dismiss();
						else {
							dirs = dirs[0].getParentFile().getParentFile()
									.listFiles();
							Arrays.sort(dirs);
							dirs = FileManager.FilesSort(dirs);
							fAdapter.setDir(dirs);
							fAdapter.notifyDataSetChanged();
						}

						break;
					}
				}

				return false;
			}
		});
		popWin = new PopupWindow(pop_main, popWidth, popHeight - 30);
		
		popWin.setOnDismissListener(new OnDismissListener() {
			// TODO Auto-generated method stub
			@Override
			public void onDismiss() {
				Message msg = mHandler.obtainMessage();
				if(selectedPath != null){
					msg.obj = selectedPath;
					selectedPath = null;
				}
				msg.arg1 = CHANGE_RCICON_MSG;
				mHandler.sendMessage(msg);
				
			}
		});
		// System.out.println(Thread.currentThread().getId());
		main_layout = inflater.inflate(R.layout.main, null);

		popWin.setFocusable(true);

		popWin.setAnimationStyle(android.R.style.Animation_Dialog);

		popWin.update();
	}

	public void showPop() {

		popWin.showAtLocation(main_layout,
				Gravity.CENTER, 0, 20);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		if (dirs[position].isDirectory()) {

			if (dirs[position].listFiles() == null) {
				// 这儿加个图标
				Toast.makeText(mContext, "permission denied",
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				parentFile = dirs[position];
				dirs = dirs[position].listFiles();
				Arrays.sort(dirs);
				dirs = FileManager.FilesSort(dirs);
				fAdapter.setDir(dirs);
				intoDir = true;
				fAdapter.notifyDataSetChanged();
			}

		} else if (dirs[position].isFile()) {
			
			String filePath = dirs[position].getPath();
			if (filePath.endsWith(".png") || filePath.endsWith(".jpg") || filePath.endsWith("ico") || filePath.endsWith(".bmp")) {
//				Message msg = this.mHandler.obtainMessage();
//				msg.obj = dirs[position].getPath();
//				msg.arg1 = 1;
//				mHandler.sendMessage(msg);
				this.selectedPath = filePath;
				
				popWin.dismiss();

			} else {
				Toast.makeText(mContext, "file error", Toast.LENGTH_SHORT)
						.show();
			}

		}
	}

//	@Override
//	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//			int position, long arg3) {
//		// TODO Auto-generated method stub
//		if (dirs[position].isDirectory()) {
//			Toast.makeText(mContext, "long", Toast.LENGTH_SHORT).show();
//		}
//		return true;
//	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		fAdapter.removeHandlerCallBack();
		if (v.getId() == R.id.img_back) {

			switch (dirs.length) {
			case 0:
				if (parentFile.getParent() == null)
					Toast.makeText(mContext, "top director", Toast.LENGTH_SHORT).show();
				else {
					dirs = parentFile.getParentFile().listFiles();
					Arrays.sort(dirs);
					dirs = FileManager.FilesSort(dirs);
					fAdapter.setDir(dirs);
					fAdapter.notifyDataSetChanged();
				}

				break;
			default:
				if (dirs[0].getParentFile().getParent() == null)
					Toast
							.makeText(mContext, "top director",
									Toast.LENGTH_SHORT).show();
				else {
					dirs = dirs[0].getParentFile().getParentFile().listFiles();
					Arrays.sort(dirs);
					dirs = FileManager.FilesSort(dirs);
					fAdapter.setDir(dirs);
					fAdapter.notifyDataSetChanged();
				}

				break;
			}
		}
		if (v.getId() == R.id.img_cancel) {
			// fAdapter.clearCache();
			popWin.dismiss();
		}

	}

	static class ViewHolder {
		ImageView icon;
		TextView name;
	}

	
	public void searchDirectory(File director){
    	File[] files = director.listFiles();
    	for(File file:files){
    		if(file.isDirectory() && file.canRead())
    			this.searchDirectory(file);
    		else if(file.isFile() && (file.getPath().endsWith(".png") || file.getPath().endsWith(".jpg"))){
    			if(file.getParentFile().getPath().equals(Environment.getExternalStorageDirectory().getPath()))
    				continue;
    			
    			imageDirector.add(file.getParentFile().getPath());
    			break;
    		}
    	}
    }
	
	// 适配器
	public class FileAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater inflater;
		File[] fdirs;
		public int id = 0;
		private HashMap<String, Bitmap> cache;
		boolean sameDir = true;
		HashMap<String, String> pathes = null;
		BitmapManager.BitmapReadThread readThread = null;
		HandlerThread myThread;
		ImageHandler myHandler;
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				if(filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png") || filename.endsWith(".bmp") || filename.endsWith(".ico"))
					return true;
				else
					return false;
			}
	        	
	       };
	       

		public FileAdapter(Context context) {
			this.mContext = context;
			inflater = LayoutInflater.from(context);
			cache = new HashMap<String, Bitmap>();

			readThread = new BitmapManager.BitmapReadThread(readHandler, dirs);
			pathes = new HashMap<String, String>();
			myThread = new HandlerThread("setCache");
			myThread.start();
			myHandler = new ImageHandler(myThread.getLooper());

		}

		private class ImageHandler extends Handler {

			public ImageHandler(Looper looper) {
				// TODO Auto-generated constructor stub
				super(looper);
			}

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
			}

		}
		
		public void removeHandlerCallBack(){
			this.myHandler.removeCallbacks(readThread);
		}
		
		public void refreshCahce(HashMap<String, Bitmap> cache) {
			this.cache = cache;
		}

		public void clearCache() {
			cache.clear();
		}

		public void setDir(File[] dirs) {
			// TODO Auto-generated method stub
			fdirs = dirs;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dirs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
//			System.out.println(getCount());
			if(intoDir ){
				readThread.setDir(dirs);
				intoDir = false;
			}
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = inflater.inflate(R.layout.pop_window_item, null);
				
				holder.icon = (ImageView) convertView.findViewById(R.id.file_icon);
				holder.name = (TextView) convertView.findViewById(R.id.file_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// System.out.println(String.valueOf(position));

			if (dirs[position].isDirectory()){
				
				if(dirs[position].list() == null || dirs[position].list().length == 0)
					holder.icon.setImageResource(R.drawable.empty_directory_icon);
				else{
					if(dirs[position].list(filter) == null || dirs[position].list(filter).length == 0){
						holder.icon.setImageResource(R.drawable.directory_files);
					} else {
						holder.icon.setImageResource(R.drawable.directory_image_only);
					}
				}
//				holder.icon.setImageResource(R.drawable.dictory_icon);
				
			}

			if (dirs[position].isFile()) {

				if (dirs[position].getName().endsWith(".jpg") || dirs[position].getName().endsWith(".png")) {

					String parentPath = dirs[position].getParentFile()
							.getPath();

					// System.out.println(BitmapManager.getMaxLength(dirs[position].getPath()));

					if (cache.get(parentPath + position) == null) {

						if (BitmapManager.getMaxLength(null, 0, dirs[position].getPath()) < 256) {
							if (BitmapManager.getMaxLength(null, 0, dirs[position].getPath()) < 1)
								holder.icon.setImageResource(R.drawable.image_icon);
							else
								holder.icon.setImageBitmap(BitmapFactory.decodeFile(dirs[position].getPath()));
						} else {
							
							if (times > fileList.getChildCount() + 10) {
								myHandler.removeCallbacks(readThread);
//								 System.out.println("**********************delete******************");
								times = 0;
							} else {
								readThread.setPosition(position);
								myHandler.postDelayed(readThread,300);
							}

							times++;
							holder.icon.setImageResource(R.drawable.image_icon);
						}

						// if(pathes.size() > CACHESIZELIMIE || cache.size() >
						// CACHESIZELIMIE)
						// cache.clear();

					} else {
						holder.icon.setImageBitmap(cache.get(parentPath
								+ position));

					}

				} else
					holder.icon.setImageResource(R.drawable.file_icon);
			}
			
			holder.name.setTextSize(20);
			holder.name.setText(dirs[position].getName());
			
			if(convertView == null)
				System.out.println("error");
			
			return convertView;
		}

	}
	
}
