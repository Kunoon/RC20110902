package com.koobest.constant;

import android.os.Environment;

public class ConfigConstant {
	//For Error Reporter
	public static String SYS_VERSION_SDK = android.os.Build.VERSION.SDK;
	public static String SYS_VERSION_RELEASE = android.os.Build.VERSION.RELEASE;
	public static String SYS_USER_EMAIL = "";
	public static boolean ERROR_START = false;
	public static boolean HABIT_RECORDER = false;
	
	public static String SDPATH = Environment.getExternalStorageDirectory().getPath() ;
	
	public static String CONFIGPATH = SDPATH + "/RemoteControllerConfig";
	
	public static String MAINCFGFILEPATH = CONFIGPATH + "/mainconfig.cfg";
	
	public static String SHELFRESOURCE = CONFIGPATH + "/shelfResource";
	
	public static String PNGCACHEPATH = CONFIGPATH + "/pngcache";
	
	public static String ERRORLOGPATH = CONFIGPATH + "/errorlog.txt";
	//RemoteController
	public static String REMOTECONTROLLERBGCACHEPATH = CONFIGPATH + "/RcBGCache";
	
	// Í¨ÖªÀ¸
	public static int NOTIFICATION_ID = 2005;
	public static int NOTIFICATION_NEWDEVICES_ID = 2006;
	
	//TYPE
	public static String TV_CODE = "tv";
	public static String AM_CODE = "airmachine";
	public static String DVD_CODE = "dvd";
	public static String AUDIO_CODE = "audio";
	
}
