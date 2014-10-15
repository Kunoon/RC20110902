package com.koobest.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class cusHscroll extends HorizontalScrollView {
	public boolean scroll_enabled;
	public LinearLayout left;
	public LinearLayout right;
	public LinearLayout mid;
	private Context mcontext;
	private int win_width;
	private Handler handler;
	protected int left_width;
	private int right_width;

	private Handler scrollHandler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (Math.abs(cusHscroll.this.getScrollX() - left_width) > 15) {
				if ((cusHscroll.this.getScrollX() - left_width) > 0)
					cusHscroll.this.scrollBy(-15, 0);
				else
					cusHscroll.this.scrollBy(15, 0);
				scrollHandler.sendEmptyMessageDelayed(1, 30);
			} else
				cusHscroll.this.scrollTo(left_width, 0);
			return false;
		}
	});
	private lockedScrollView lsv;

	public cusHscroll(Context context, int width) {
		super(context);
		win_width = width;
		mcontext = context;
		this.initial();
		// TODO Auto-generated constructor stub
	}

	public cusHscroll(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public cusHscroll(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public cusHscroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void initial() {
		left_width = win_width / 4 + 10;
		right_width = win_width / 5 + 10;
		left = new LinearLayout(mcontext);
		right = new LinearLayout(mcontext);
		mid = new LinearLayout(mcontext);
		// this.setScrollBarStyle(left_width);
		// this.setHorizontalScrollBarEnabled(false);
		LinearLayout li = new LinearLayout(mcontext);
		li.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.addView(li);
		li.addView(left, new LinearLayout.LayoutParams(left_width,
				LayoutParams.FILL_PARENT));
		// lsv = new lockedScrollView(mcontext);
		// li.addView(lsv, new LinearLayout.LayoutParams(win_width,
		// LayoutParams.WRAP_CONTENT));
		// lsv.addView(mid, new LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		li.addView(mid, new LinearLayout.LayoutParams(win_width,
				LayoutParams.WRAP_CONTENT));
		li.addView(right, new LinearLayout.LayoutParams(right_width,
				LayoutParams.FILL_PARENT));
		right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		right.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					System.out.println("down");
					cusHscroll.this.dispatchTouchEvent(event);
				}
				return false;
			}
		});
		this.setVisibility(View.INVISIBLE);
		this.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		handler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				cusHscroll.this.scrollTo(left_width, 0);
				cusHscroll.this.setVisibility(View.VISIBLE);
				return false;
			}
		});
		Timer time = new Timer();
		time.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(1);
			}
		}, 500);
		this.setFadingEdgeLength(0);
		this.setOnTouchListener(new OnTouchListener() {

			private float start_x;
			private float start_y;
			private boolean unset_start;
			private boolean args_setted;
			private double args;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					start_x = event.getX();
					start_y = event.getY();
//					System.out.println("firstpoint:" + start_x);
//					if (v.getTag() != null)
//						System.out.println(v.getTag().toString());
					unset_start = false;
					break;
				case MotionEvent.ACTION_MOVE:
					if (unset_start) {
						start_x = event.getX();
						start_y = event.getY();
						unset_start = false;
					} else {
						if (!args_setted) {
							System.out.println("setted");
							args_setted = true;
							args = Math.atan(Math.abs(event.getY() - start_y)
									/ Math.abs(event.getX() - start_x));
							// System.out.println(args);
						}
						// lsv.scrollBy(0, (int) -(event.getY() - start_y));
						start_y = event.getY();

					}
					if (args_setted)
						if (args <= Math.PI / 4) {
							System.out.println("scroll   args = " + args);
							cusHscroll.this.scrollBy((int) -((int) 1.5 * (event
									.getX() - start_x)), 0);
							// System.out.println(event.getX()-start_x);
							start_x = event.getX();
						}
//					System.out.println("scrollx:"
//							+ cusHscroll.this.getScrollX());
					break;
				case MotionEvent.ACTION_UP:
					// mid.dispatchTouchEvent(event);

					start_x = 0;
					start_y = 0;
					unset_start = true;
					args = 90;
					args_setted = false;
					if (cusHscroll.this.getScrollX() > left_width + right_width
							/ 2)
						cusHscroll.this.fullScroll(View.FOCUS_RIGHT);
					else if (cusHscroll.this.getScrollX() < left_width / 2)
						cusHscroll.this.fullScroll(View.FOCUS_LEFT);
					else
						scrollHandler.sendEmptyMessage(1);
					break;
				}
				return true;
			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// if (scroll_enabled)
		// return super.onTouchEvent(ev);
		// else
		return false;
	}

}
