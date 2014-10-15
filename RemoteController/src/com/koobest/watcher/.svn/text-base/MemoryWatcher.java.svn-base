package com.koobest.watcher;

import android.os.Handler;
import android.os.Message;

public class MemoryWatcher implements Runnable{
	Handler mHandler;
	long memory = 0;
	boolean stop = false;
	public MemoryWatcher(Handler h){
		this.mHandler = h;
	}
	
	public void setStop(boolean stop){
		this.stop = stop;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message msg = mHandler.obtainMessage();
		msg.obj = Runtime.getRuntime().totalMemory() / 1024;
		
		mHandler.sendMessage(msg);
		
		if(!stop)
			mHandler.postDelayed(this, 5000);
	}

}
