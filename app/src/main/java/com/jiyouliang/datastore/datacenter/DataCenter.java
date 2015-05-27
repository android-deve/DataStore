package com.jiyouliang.datastore.datacenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.inter.sharesdk.api.SharePlatform;
import com.inter.sharesdk.api.SharePlatform.SearchListener;
import com.inter.sharesdk.model.SearchData;
import com.jiyouliang.datastore.util.FileLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 数据中心，负责存储数据
 * 
 * @author youliang.ji
 * @since 2014/9/24
 * 
 */
public class DataCenter {

	private static final String TAG = "DataCenter";
	private static DataCenter instance = new DataCenter();
	private static Context context;
	private SearchListener listener;
	private ArrayList<SearchData> cacheData; // 缓存数据

	// 内存中每一页数据
	private ArrayList<SearchData> mMemDataList;
	private HashMap<String, ArrayList<SearchData>> mMemMap; // 内存中的分页数据
	private ProgressDialog dialog;
	private int pagenum = 1;// 当前页码
	private final int pagesize = 10; // 每页长度
	private FileLoader fileLoader;
	
	/**
	 * 文件名前缀
	 */
	private final String PREFIX = "page";

	/**
	 * 文件名后缀
	 */
	private final String SUFFIX = ".data";// 前缀

	/**
	 * 单例
	 */
	private DataCenter() {
	}

	/**
	 * 获取实例对象
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized DataCenter getInstance(Context context) {
		DataCenter.context = context;
		if (instance == null) {
			instance = new DataCenter();

		}
		return instance;
	}

	/**
	 * 获取数据
	 * 
	 * @param pagenum
	 *            当前页码
	 * 
	 * @return
	 */
	public void getSearchData(SearchListener listener, int pagenum) {
		this.listener = listener;
		this.pagenum = pagenum;
		if (isExistInMemory()) {
			getMemoryData(this.listener);
		} else if (isExistInCache()) {
			Log.e(TAG, "缓存中存在第" + pagenum + "页数据");
			Log.e(TAG, "从缓存中获取第" + pagenum + "页数据");
			ArrayList<SearchData> cacheData = getCache();
			this.listener.onSearchCallBack(cacheData);
		} else {
			getNetData();
		}
	}

	/**
	 * 内存中是否内在
	 * 
	 * @return
	 */
	private boolean isExistInMemory() {
		if (mMemMap != null && mMemMap.size() > 0) {
			mMemDataList = mMemMap.get(pagenum + "");
			if (mMemDataList == null || mMemDataList.size() == 0) {
				Log.e(TAG, "内存中不存在第" + pagenum + "页数据");
				return false;
			} else {
				Log.e(TAG, "内存中存在第" + pagenum + "页数据");
				return true;
			}
		}
		if (mMemMap == null || mMemMap.size() == 0) {
			Log.e(TAG, "内存中不存在第" + pagenum + "页数据");
			return false;
		}
		return false;
	}

	/**
	 * 从内存获取
	 * 
	 * @param listener
	 */
	private void getMemoryData(final SearchListener listener) {
		mMemDataList = mMemMap.get(pagenum + "");
		listener.onSearchCallBack(mMemDataList);
		Log.e(TAG, "从内存中读取第" + pagenum + "页数据");
	}

	/**
	 * 从网络获取数据
	 */
	private void getNetData() {
		if (mMemMap == null) {
			mMemMap = new HashMap<String, ArrayList<SearchData>>();
			mMemDataList = new ArrayList<SearchData>();
		}
		SharePlatform platform = new SharePlatform();
		dialog = ProgressDialog.show(context, null, "请求网络中...");
		platform.getSearchData(pagesize, pagenum, new SearchListener() {

			@Override
			public void onSearchCallBack(ArrayList<SearchData> data) {
				if(data == null || data.size() == 0) {
					Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
					return;
				}
				storeInMemory(data);
				storeInCache(data);
				deleteMemory(); // 测试时打开：测试删除内存数据，看看是否会加载文件中缓存的数据
				Log.e(TAG, "访问网络");
				listener.onSearchCallBack(data);
				dialog.dismiss();
			}

		});
	}

	/**
	 * 缓存到文件，将系列化对象储存到文件中
	 * 
	 * @param data
	 */
	private void storeInCache(ArrayList<SearchData> data) {
		try {
			// 文件名
			String fileName = PREFIX + pagenum + SUFFIX; // 如page1.data
			fileLoader = FileLoader.getInstance(context);
			File file = fileLoader.createNewFile(fileName);
			ObjectOutputStream outputStream = new ObjectOutputStream(
					new FileOutputStream(file));
			// 系列化对象储存到文件中
			outputStream.writeObject(data);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 缓存中是否存在，如果存在，更新到页面，并保存到内存，方便下次使用
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isExistInCache() {
		try {
			if (fileLoader == null) {
				fileLoader = FileLoader.getInstance(context);
			}
			String fileName = PREFIX + pagenum + SUFFIX; // 文件名
			File file = fileLoader.getFile(fileName);
			if (file.exists()) {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(file));
				cacheData = (ArrayList<SearchData>) ois.readObject();
				ois.close();
				if (cacheData != null && cacheData.size() > 0) {
					// 一定要有这一句话，不然第1页不加载缓存
					if(mMemMap == null) mMemMap = new HashMap<String, ArrayList<SearchData>>();
					mMemMap.put(pagenum + "", cacheData);// 同时保存到内存中，下一次直接从内存到读取
					return true;
				}
				return false;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取缓存对象，将文件中的数据反系列化
	 * 
	 * @return
	 */
	private ArrayList<SearchData> getCache() {
		return cacheData;
	}

	/**
	 * 保存到内存
	 * 
	 * @param data
	 */
	private void storeInMemory(ArrayList<SearchData> data) {
		mMemMap.put(pagenum + "", data);
	}

	/**
	 * 测试删除内存数据
	 */
	private void deleteMemory() {
		// 当前第8页，删除第1、5、7页数据
		if (pagenum == 8) {
			mMemMap.remove(7 + "");
			mMemMap.remove(5 + "");
			mMemMap.remove(1 + "");
			Log.e(TAG, "删除第1、5、7页数据");
		}
	}

}
