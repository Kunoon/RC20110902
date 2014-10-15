package com.koobest.parse;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.koobest.constant.ConfigConstant;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class FileManager {
	Context mContext;
	public FileManager(Context context){
		this.mContext = context;
	}
	public File[] getFilteredFiles(String path,final String extension_name) throws FileNotFoundException{
		 FileFilter filter = new FileFilter() {
	        	public boolean accept (File file) {
	        		if (file.isFile() && file.getAbsolutePath().toLowerCase().endsWith(extension_name)) {
	        			return true;
	        		}
	        			return false;
	        		}
	        	};
	        	
	        File director = new File(path);
	        if(!director.exists()){
	        	throw new FileNotFoundException("No such directory:" + path);
	        }
	        File[] files = director.listFiles(filter);
	        
	        return files;
	}
	
	public String[] getFilteredFilesName(String path,final String extension_name) throws FileNotFoundException{
			 FilenameFilter filter = new FilenameFilter() {
	
				@Override
				public boolean accept(File dir, String filename) {
					// TODO Auto-generated method stub
					if (filename.toLowerCase().endsWith(extension_name)) 
						return true;
					else
						return false;
				}
	
		    };
	        	
	        File director = new File(path);
	        if(!director.exists()){
	        	throw new FileNotFoundException("No such directory:" + path);
	        }
	        
	        String[] fileName = director.list(filter);
	        
	        return fileName;
	}
	
	public HashMap<Integer,String[]> getFilesPathAndName(File[] files){
		HashMap<Integer,String[]> map = new HashMap<Integer, String[]>();
		HashMap<Integer,String[]> map_sorted = new HashMap<Integer, String[]>();
		String[] path_name = new String[2]; 
		//ArrayList<String> names = new ArrayList<String>();
		int[] orders = new int[files.length]; 
		int[] temp = new int[files.length];
		int[] orders_sorted = new int[orders.length];
		
		String orderStr = null;
		
		DomParse main_dp = null;
		try {
			main_dp = new DomParse(ConfigConstant.MAINCFGFILEPATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 0;
		for(File file:files){
			path_name[0] = file.getPath();
			path_name[1] = file.getName();
			
			orderStr = main_dp.getSingleContent(file.getName().replace(".xml", "") + "order");
			if(orderStr != null)
				orders[i] = Integer.parseInt(orderStr);
			else
				orders[i] = 0;
				
			
			map.put(i, path_name);

	        path_name = new String[2];
	        i++;
		}
		temp = orders.clone();
		
		Arrays.sort(orders);
		
		for(int m = 0,j = orders.length - 1; m < orders.length; m++,j--){
			orders_sorted[j] = orders[m];
		}
		
		for(int m = 0; m < orders_sorted.length; m++){
			for(int n = 0; n < temp.length; n++){
				if(orders_sorted[m] == temp[n]){
					map_sorted.put(m, map.get(n)); 
					temp[n] = -1;
					break;
				}
			}
		}
		
//		for(int m = 0; m < map_sorted.size(); m++){
//			System.out.println(map_sorted.get(m)[1] + " " + map.get(m)[1] + " " + ConfigConstant.HABIT_RECORDER);
//		}
		if(ConfigConstant.HABIT_RECORDER)
			return map_sorted;
		else
			return map;
	}
	
	public static void creatNewFileInSdcard(String path) throws IOException{
		
		File file = new File(path);
		if(file.exists())
			return;
		
		file = null;
		
		String sdPath = null;
        String[] paths = path.split("/");;
        String newPath = null;
        int j = 0;
		
		if(paths.length == 1){
			
			throw new IOException("permission denied");
		}
		else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()) || Environment.MEDIA_UNMOUNTED.equals(Environment.getExternalStorageState()))
		{
			throw new IOException("SdCard unmounted or read-only");
		} else {
			sdPath = Environment.getExternalStorageDirectory().getPath();
			//paths = path.replace(sdPath, "").split("/");
	        newPath = sdPath;
	        j = sdPath.split("/").length;
		}
		for(int i = j;i < paths.length; i++){
        	newPath += "/" + paths[i];
        	
        	file = new File(newPath);
        	if(!file.exists() && i < (paths.length - 1))
        		file.mkdir();
        	
        	else if(!file.exists() && i == (paths.length - 1))
				try {
					if(paths[i].replace(".", "/").split("/").length > 1)
						file.createNewFile();
					else
						file.mkdir();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new IOException(e.getMessage());
				}
				
				file = null;
        }
 		
    	sdPath = null;
        paths = null;
        newPath = null;
 	}
	
	public static File[] FilesSort(File[] bs_dirs) {
		// TODO Auto-generated method stub
		int num_of_file = 0;
		int num_of_dir = 0;
		File[] files = new File[bs_dirs.length];
		File[] dir = new File[bs_dirs.length];
		for (int i = 0; i < bs_dirs.length; i++) {
			if(bs_dirs[i].isFile())
			{
				files[num_of_file] = bs_dirs[i];
				num_of_file++;
			}
			else
			{
				dir[num_of_dir] = bs_dirs[i];
				num_of_dir++;
			}
		}
		System.arraycopy(files, 0, dir, num_of_dir, num_of_file);
		return dir;
	}

}
