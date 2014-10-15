package com.koobest.customization;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.GridView;

public class LockableGridView extends GridView {

	private boolean lockEnabled;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("KeyDown");
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public LockableGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public LockableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LockableGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setScreenLock(boolean lock) {
		lockEnabled = lock;
	}

	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (lockEnabled)
				return true;
			else
				return super.onTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}

}
