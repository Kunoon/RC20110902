package com.koobest.customization;

import java.util.List;
import java.util.zip.Inflater;

import com.koobest.R;
import com.koobest.RemoteController;

import android.content.Context;
import android.text.Html.TagHandler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost.TabSpec;

public class MyAdapter extends BaseAdapter {

	private List<Integer> mylist;
	private Context mContext;
	private int selected = -1;
	private LayoutInflater inflater;
	private TabSpec mspec;
	private View view;
	public int selected_id = 0;

	public MyAdapter(Context context, List<Integer> imagelist, TabSpec spec,
			View tabspec) {
		// TODO Auto-generated constructor stub
		mylist = imagelist;
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		mspec = spec;
		view = tabspec;
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
		image.setLayoutParams(new LinearLayout.LayoutParams(70,50));
		image.setBackgroundResource(mylist.get(position));
		image.setAdjustViewBounds(true);
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
			selected_id = mylist.get(selected);
			System.out.println("selected");
//			mspec.setIndicator("selected", mContext.getResources().getDrawable(
//					mylist.get(selected)));
			ImageView image = (ImageView) view
					.findViewById(R.id.tab_spec_image);
			image.setBackgroundResource(mylist.get(selected));
			notifyDataSetChanged();
		}

	}

}
