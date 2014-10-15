package com.koobest.reporter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import com.koobest.R;
import com.koobest.constant.ConfigConstant;
import com.koobest.database.DatabaseHelper;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class CustomException implements UncaughtExceptionHandler{
private Context mContext; 
    
	NotificationManager myNotificationManager= null;

    private Thread.UncaughtExceptionHandler defaultExceptionHandler; 
    //µ¥ÀýÉùÃ÷CustomException; 
    private static CustomException customException; 
    
    private CustomException(){        
    } 
    
    public static CustomException getInstance(){ 
        if(customException == null){ 
            customException = new CustomException(); 
        } 
        return customException; 
    } 
    
    
	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		// TODO Auto-generated method stub
		
		 if(defaultExceptionHandler != null){ 
			 	try {
					myNotificationManager.cancel(ConfigConstant.NOTIFICATION_ID);
					myNotificationManager.cancel(ConfigConstant.NOTIFICATION_NEWDEVICES_ID);
				} catch (Exception e) {}
				
	            Intent i = new Intent(mContext,com.koobest.reporter.ExceptionSendService.class);
	            
	            SharedPreferences sp = mContext.getSharedPreferences("com.koobest_preferences", 0);
	            SharedPreferences.Editor edit = sp.edit();
	            
	            
	    		SQLiteDatabase db = new DatabaseHelper(mContext, "rc_test_db").getReadableDatabase();
	    		if(db.isOpen())
	    			db.close();
	    		
	            //Maybe the judge is still useful,so leave it no deleted
	            if(ConfigConstant.ERROR_START){
	            	String reporter = getInformation(exception);
	            	if(reporter == null)
	            		reporter = "***********Exception analyse failed************";
	            	i.putExtra("report", getInformation(exception));
		            mContext.startService(i);
//		            edit.putBoolean("error_sending", true);
//		            edit.commit();
	            }
	    
				System.out.println("exception finish");
				defaultExceptionHandler.uncaughtException(thread, exception); 
	            
				//android.os.Process.killProcess(android.os.Process.myPid());
				//System.exit(0);

	        } 
		     
		 
		        
		 
	}
	
	
	public void init(Context context) {   
        mContext = context;  
        myNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        defaultExceptionHandler  = Thread.getDefaultUncaughtExceptionHandler();   
        Thread.setDefaultUncaughtExceptionHandler(this);  
      }
	
	public static String getInformation(Throwable exception){
		 String info = null;
         ByteArrayOutputStream baos = null;
         PrintStream printStream = null;
         try {
                 baos = new ByteArrayOutputStream();
                 printStream = new PrintStream(baos);
                 exception.printStackTrace(printStream);
                 byte[] data = baos.toByteArray();
                 info = new String(data);
                 data = null;
         } catch (Exception e) {
                 e.printStackTrace();
         } finally {
                 try {
                         if (printStream != null) {
                                 printStream.close();
                         }
                         if (baos != null) {
                                 baos.close();
                         }
                 } catch (Exception e) {
                         e.printStackTrace();
                 }
         }
		return info;
	}
}
