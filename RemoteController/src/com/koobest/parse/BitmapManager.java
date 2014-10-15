package com.koobest.parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.koobest.R;
import com.koobest.RemoteControllerMain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;

public class BitmapManager{
	
	public static class BitmapReadThread implements Runnable{
		private HashMap<String,Bitmap> cache;
		private Handler mHandler;
		boolean isFinished = true;
		private int msg_id = 1,times = 0;
		byte[] lock = new byte[0];
		private int last_position = 7;//,position_temp = 0;
		private File[] dirs;
		
		public BitmapReadThread(Handler handler, File[] dir){
			cache = new HashMap<String,Bitmap>();
			this.mHandler = handler;
			dirs = dir;
		}
		public void setDir(File[] dir)
		{
			dirs = dir;
		}
		
		public void setPosition(int pos){
			last_position = pos;
			times ++ ;
		}
		
		public void clear(){
			this.cache.clear();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			int position_temp = this.last_position;
			Message msg = mHandler.obtainMessage();
			Bitmap img_icon = null;		
			
			try{
			
				if(cache.get(dirs[position_temp].getParentFile().getPath()+position_temp) == null)
				{
					
					img_icon = BitmapManager.compress(null,0,dirs[position_temp].getPath(), 50,false);
					
					if(img_icon != null)
						cache.put(dirs[position_temp].getParentFile().getPath()+position_temp, img_icon);
					else
						cache.put(dirs[position_temp].getParentFile().getPath()+position_temp, BitmapFactory.decodeResource(RemoteControllerMain.res, R.drawable.image_icon));
	
					//				System.out.println("thread finish " + " position " + position_temp);
				}
			} catch (ArrayIndexOutOfBoundsException e){
				System.out.println("error: " + position_temp + " " + e.getMessage());
				return;
			}
			
			msg.what = msg_id;
			msg.obj = cache;		
			mHandler.sendMessage(msg);
		}
		
	}
		
	
	
	/**
	 * Compress the bitmap with the maxLength
	 * 
	 * @param path  The bitmap's path
	 * @param maxLength  The max length you want to set
	 * @return the compressed bitmap or null if decode failed
	 * 
	 * @author Ray
	 */
	
	public static Bitmap compress(Context context,int res_id,String path, int maxLength, boolean zoomBitmap){
		if(context != null && path != null){
			return null;
		}
		
    	BitmapFactory.Options opt = new BitmapFactory.Options();
    	int srcWidth = 0;
    	int srcHeight = 0;
    	opt.inJustDecodeBounds = true;
    	
    	//当decodeFile返回为空，不为图片分配内存，只返回大小
    	if(path != null){
    		BitmapFactory.decodeFile(path, opt);
    	
    	} else if(context != null){
    		BitmapFactory.decodeResource(context.getResources(), res_id, opt);
    	}
    	
    	srcWidth = opt.outWidth;
		srcHeight = opt.outHeight;
		
    	if(srcWidth < 1 || srcHeight < 1)
    		return null;
//    	int destWidth = 0;
//    	int destHeight = 0;
    	
    	//缩放比例
    	double ratio = 0.0;
    	
    	//System.out.println("src height and width " + srcWidth + srcHeight);
    	if(srcWidth > maxLength){
    		ratio = (double) (srcWidth/maxLength);
    	} else {
    		ratio = srcHeight/maxLength;
    	}
    	
    	
    	BitmapFactory.Options newOpt = new BitmapFactory.Options();
    	
    	newOpt.inSampleSize = (int) (2 + ratio);
    	//inJustDecodeBounds为false表示把图片读入内存
    	newOpt.inJustDecodeBounds = false;
    	
//    	//可能可以去除
//    	newOpt.outHeight = destHeight;
//    	newOpt.outWidth = destWidth;
    	
    	
    	Bitmap destBm = null;
    	
    	if(path != null){
    		destBm = BitmapFactory.decodeFile(path, newOpt);;
    	} else if(context != null){
    		destBm = BitmapFactory.decodeResource(context.getResources(), res_id, newOpt);
    	}
    	
    	if(zoomBitmap){
	    	if(destBm.getWidth() < destBm.getHeight()){
	    		
				destBm = BitmapManager.zoomBitmap(destBm, maxLength, 0);
					
			} else {
				
				destBm = BitmapManager.zoomBitmap(destBm, 0, maxLength);
			}
    	}
    	
//    	System.out.println("Bitmap Manager OP img width: " + destBm.getWidth() + " " + destBm.getHeight() + " maxLength: " + maxLength);
    	return destBm;
    }
	
	public static int getMaxLength(Context context,int res_id,String path){
		
		if(context != null && path != null){
			return 0;
		}
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
    	opt.inJustDecodeBounds = true;
    	//当decodeFile返回为空，不为图片分配内存，只返回大小
    	if(path != null){
    		File file = new File(path);
    		
    		if(!file.exists())
    			return 0;
    		else
    			BitmapFactory.decodeFile(path, opt);
    	}
    	else if (context != null)
    		BitmapFactory.decodeResource(context.getResources(), res_id, opt);
    	
    	int srcWidth = opt.outWidth;
    	int srcHeight = opt.outHeight;
		
		if(srcWidth >= srcHeight)
			return srcWidth;
		else
			return srcHeight;
	}
	
	/**
	 * 
	 * @param path The source bitmap path
	 * @param destPath The destination path
	 * @param rotate The degrees you want to post 
	 * @param maxLength The critical length to tell the function to compress the bitmap
	 * @param destLength The result length,it is a reference to decide the compression ratio
	 * @throws IOException 
	 * 
	 * @author Ray
	 */
	
	public static void savePostRotatedBitmap(String path,String destPath,int rotate,int maxLength, int destLength) throws IOException {
		//获得图片名
        File f = new File(path);
        if(!f.exists())
        	return;
        String name = f.getName();
        
        Bitmap destBmp = null;
		if(BitmapManager.getMaxLength(null, 0, path) > maxLength){
			destBmp = BitmapManager.compress(null, 0, path, destLength,true);
		} else {
			destBmp = BitmapFactory.decodeFile(path);
		}
		
		if(destBmp == null){
			throw new FileNotFoundException("Image not found");
		}
		
        //获得图片存储路径
        f = new File(destPath);
        if(!f.exists())
        	f.mkdir();
        //生成图片文件
        f = new File(destPath + "/" + name);
        if(!f.exists())
        	f.createNewFile();
        
        	
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        
		if(rotate != 0){
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate, destBmp.getWidth()/2, destBmp.getHeight()/2);
			
			destBmp = Bitmap.createBitmap(destBmp, 0, 0, destBmp.getWidth(), destBmp.getHeight(), matrix, true);
			matrix.reset();
		}
		
        destBmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
	}
	
	
	public static Bitmap zoomBitmap(Bitmap bitmap, int h, int w) {

		int width = bitmap.getWidth();

		int height = bitmap.getHeight();
		
		float scaleHeight = 0, scaleWidth = 0;
		Matrix matrix = new Matrix();
		if(w == 0 && h != 0){
			scaleHeight = ((float) h / height);
			matrix.postScale(scaleHeight, scaleHeight);
		}
		else if(h == 0 && w != 0){
			scaleWidth = ((float) w / width);
			matrix.postScale(scaleWidth, scaleWidth);
		} else if (h !=0 && w != 0){
			scaleHeight = ((float) h / height);
			scaleWidth = ((float) w / width);
			matrix.postScale(scaleWidth, scaleHeight);
		}

		

		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		
		matrix.reset();
		return newbmp;

	}
}
