package com.koobest.customization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.koobest.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyImageAdapter extends BaseAdapter {

	private Context context;
	private List<File> mylist;
	public File selected_file = null;
	private LayoutInflater inflater;
	private int selected = -1;
	private List<Drawable> imageCache = new ArrayList<Drawable>();

	public MyImageAdapter(Context mContext, List<File> filelist) {
		// TODO Auto-generated constructor stub
		context = mContext;
		mylist = filelist;
		inflater = LayoutInflater.from(context);
		System.out.println(mylist.size());

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mylist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LinearLayout li = (LinearLayout) inflater.inflate(R.layout.tab_image,
				null);
		ImageView image = (ImageView) li.findViewById(R.id.tab_image);
		Drawable da;
		System.out.println(position + ":" + mylist.get(position).getPath());
		if (imageCache.size() != mylist.size() && imageCache.size()<= position) {
			Bitmap bm = BitmapFactory
					.decodeFile(mylist.get(position).getPath());
			BitmapDrawable bmd = new BitmapDrawable(bm);
			da = (Drawable) bmd;
			imageCache.add(position,da);
			System.out.println(imageCache.size());
		} else
			da = imageCache.get(position);
		image.setBackgroundDrawable(da);
		image.setTag(position);
		image.setPadding(5, 5, 5, 5);
		image.setOnClickListener(new StyleSetListener());

		if (position == selected)
			li.setBackgroundColor(0xaaff0000);
		li.setMinimumHeight(60);
		return li;
	}

	private class StyleSetListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selected = Integer.parseInt(v.getTag().toString());
			selected_file = mylist.get(selected);
			System.out.println("selected");
			// mspec.setIndicator("selected",
			// mContext.getResources().getDrawable(
			// mylist.get(selected)));
			notifyDataSetChanged();
		}

	}

}
