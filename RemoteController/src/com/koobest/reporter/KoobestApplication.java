package com.koobest.reporter;




import android.app.Application;

public class KoobestApplication extends Application{
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		System.out.println("application created");
		CustomException customException = CustomException.getInstance(); 
	    customException.init(getApplicationContext());
	}
	
	
}
