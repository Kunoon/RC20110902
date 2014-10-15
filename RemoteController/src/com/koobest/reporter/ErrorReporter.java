package com.koobest.reporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.koobest.constant.ConfigConstant;
import com.koobest.constant.URLConstant;
import com.koobest.socket.HttpConnection;

public class ErrorReporter{
	private static HttpConnection error_con = null;
	public static int sendByNet = -2;
	public static int sendFailed = -3;
	
	public static void sendErrorFile(Handler send_result){
		final Handler send_msg = send_result;
		Thread sendError = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					File file = new File(ConfigConstant.ERRORLOGPATH);
					BufferedReader buff = new BufferedReader(new FileReader(file));
					StringBuilder sb = new StringBuilder();
					String readline = null;
					
					while((readline = buff.readLine()) != null){
						sb.append(readline + "\n");
					}
					
					sendErrorMessage(sb.toString(), send_msg);
					file.delete();
				} catch (IOException e){
					return;
				}
				super.run();
			}
			
		};
		sendError.start();
		
	}
	
	public static void sendErrorMessage(String message,Handler send_result){
		final String msg = message;
		final Handler response_h = send_result;
		
		Thread thd = new Thread(){
			@Override
			public void run() {
				
				// TODO Auto-generated method stub
				Message response_msg = null;
				error_con = new HttpConnection();
				error_con.setParams(URLConstant.SCHEME, URLConstant.ERROR_HOST, URLConstant.PORT, URLConstant.PATH);
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(URLConstant.PREFIX,URLConstant.ERROR_REPOTER_ROUTE));
				params.add(new BasicNameValuePair("sys_version_sdk",ConfigConstant.SYS_VERSION_SDK));
				params.add(new BasicNameValuePair("sys_version_release",ConfigConstant.SYS_VERSION_RELEASE));
				params.add(new BasicNameValuePair("email",ConfigConstant.SYS_USER_EMAIL));
				params.add(new BasicNameValuePair("message",msg));
				
				
				try{
					if(response_h != null){
						response_msg = response_h.obtainMessage();
						response_msg.arg1 = sendByNet;
						response_h.sendMessage(response_msg);
					}
					error_con.getResultsByPost(params);
					System.out.println("send finish");
				} catch (Exception e){
					try {
						
						FileWriter error_log = new FileWriter(ConfigConstant.ERRORLOGPATH);
						error_log.write(msg + " /n " + e.getMessage());
						error_log.flush();
						if(response_h != null){
							response_msg = response_h.obtainMessage();
							response_msg.arg1 = sendFailed;
							response_h.sendMessage(response_msg);
						}
						System.out.println("Save to sdcard");
					} catch (IOException io) {
						// TODO Auto-generated catch block
						io.printStackTrace();
						return;
					}
				}
			
				super.run();
			}
			
		};
		
		if(ConfigConstant.ERROR_START)
			thd.start();
	}
	
}
