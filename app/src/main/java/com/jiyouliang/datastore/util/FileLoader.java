package com.jiyouliang.datastore.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * 文件加载器
 * @author youliang.ji
 *
 */
public class FileLoader {

	private static final String TAG = "FileLoader";
	private static FileLoader instance = new FileLoader();
	private Context context;
	/**
	 * 缓存路径
	 */
	private final String cachePath = "com.demo.storage";

	public static FileLoader getInstance(Context context) {
		setContext(context);
		if (instance == null) {
			instance = new FileLoader();
		}
		return instance;
	}

	private static void setContext(Context context) {
		instance.context = context;
	}

	private Context getContext() {
		return instance.context;
	}

	/**
	 * 获取缓存路径
	 * @return
	 */
	private String getCachePath() {
		String f = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			f = Environment.getExternalStorageDirectory() + File.separator + cachePath;
		else
			f = getContext().getFilesDir() + File.separator + cachePath;
//		Log.e(TAG, "cachePath = "+f.toString());
		return f;
	}
	
	/**
	 * 创建新文件，文件已经存在则删除
	 * @param fileName 文件名（无路径，如storage/temp/cache.txt是错误的，应该写成chche.txt）
	 * @return
	 */
	public File createNewFile(String fileName){
		try {
			File path = new File(getCachePath());
			if(!path.exists()){
				path.mkdirs();
			}
			File file = new File(path, fileName);
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
//			Log.e(TAG, "fileName = "+fileName.toString());
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取文件
	 * @param fileName 文件名
	 * @return
	 */
	public File getFile(String fileName){
		return new File(getCachePath(), fileName);
	}
}
