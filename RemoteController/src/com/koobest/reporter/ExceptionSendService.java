package com.koobest.reporter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.koobest.R;
import com.koobest.constant.ConfigConstant;
import com.koobest.constant.URLConstant;
import com.koobest.socket.HttpConnection;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class ExceptionSendService extends Service{
	
	String result = "";
	String msg = "";
	SharedPreferences.Editor sp = null;
	boolean hasStart = false;
	
	Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message arg0) {
			// TODO Auto-generated method stub
			hasStart = false;
			ExceptionSendService.this.stopSelf();
			return true;
		}
	});
	
	public class SendMsgThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (ACCESSIBILITY_SERVICE) {
				sendMessage(msg);
				Message msg = mHandler.obtainMessage();
				mHandler.sendMessage(msg);
				sp.putBoolean("error_sending", false);
				sp.commit();
			}
			
			
		}
		
	}
	
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("service on create");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		System.out.println("on Start");
		sp = getSharedPreferences("com.koobest_preferences", 0).edit();

		msg = intent.getStringExtra("report");
		
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.reporter_sending), Toast.LENGTH_LONG).show();
		Thread thd = new Thread(new SendMsgThread());
		if(!hasStart)
			thd.start();
		hasStart = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void sendMessage(String msg){
		System.out.println("on sending");
		HttpConnection error_con = new HttpConnection();
		error_con.setParams(URLConstant.SCHEME, URLConstant.ERROR_HOST, URLConstant.PORT, URLConstant.PATH);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(URLConstant.PREFIX,URLConstant.ERROR_REPOTER_ROUTE));
		params.add(new BasicNameValuePair("sys_version_sdk",ConfigConstant.SYS_VERSION_SDK));
		params.add(new BasicNameValuePair("sys_version_release",ConfigConstant.SYS_VERSION_RELEASE));
		params.add(new BasicNameValuePair("email",ConfigConstant.SYS_USER_EMAIL));
		params.add(new BasicNameValuePair("message",msg));
		
		
		try{
			System.out.println("excute post");
			error_con.getResultsByPost(params);
			result = getResources().getString(R.string.reporter_send_success);
			System.out.println("send finish");
		} catch (Exception e){
			try {
				FileWriter error_log = new FileWriter(ConfigConstant.ERRORLOGPATH);
				error_log.write(msg + " / " + e.getMessage());
				error_log.flush();
				result = getResources().getString(R.string.reporter_sent_fail);
				System.out.println("Save to sdcard");
			} catch (IOException io) {
				// TODO Auto-generated catch block
				io.printStackTrace();
				return;
			} catch (Exception e1){
				System.out.println(CustomException.getInformation(e1));
				return;
			}
	}
	}

}
