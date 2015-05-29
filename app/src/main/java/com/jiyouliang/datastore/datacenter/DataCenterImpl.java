package com.jiyouliang.datastore.datacenter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.jiyouliang.datastore.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
/**
 * ��������
 * @author YouLiang.Ji
 */
public class DataCenterImpl {

    private List<User> listTemp;

    private DataCenterImpl(){}

    private static DataCenterImpl instance;
    private static Context context;
    private static final String CACHE_FILE_PATH = "com.jiyouliang.datastore";
    private static final String TAG = DataCenterImpl.class.getSimpleName();
    private static List  data;
    private static Class clazz;

    public static DataCenterImpl getInstance(Context context){
        if(instance == null){
            synchronized (DataCenterImpl.class){
                instance = new DataCenterImpl();
            }
        }
        if(instance.context == null) instance.context = context;
        return instance;
    }

    /**缓存路径*/
    private String getCachePath() {
        String f = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            f = Environment.getExternalStorageDirectory() + File.separator + CACHE_FILE_PATH;
        else
            f = context.getExternalCacheDir() + File.separator + CACHE_FILE_PATH;
        Log.e(TAG, "cachePath = " + f.toString());
        return f;
    }

    /**存储到缓存*/
    public <T> T store2Cache(List<T> list, String fileName, Class obj) {
        store2Memory(list, obj);
        File filePath = new File(getCachePath());
        File cacheFile = null;
        try {
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            cacheFile = new File(filePath, fileName);
            if (!cacheFile.exists()) {
                cacheFile.createNewFile();
            }
            ObjectOutputStream objOutStream = new ObjectOutputStream(new FileOutputStream(cacheFile));
            objOutStream.writeObject(list);
            objOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**从缓存中获取对象集*/
    public <T> List<T> getFromCache(Class<T> clazz, String fileName){
        File cacheFile = new File(getCachePath(), fileName);
        List<T> data = null;
        try {
            ObjectInputStream objInputStream = new ObjectInputStream(new FileInputStream(cacheFile));
            data = (ArrayList<T>)objInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**缓存中是否存在*/
    public boolean existInCache(String fileName){
        return new File(fileName).exists();
    }

    /**对象集存储到内存中*/
    public <T> boolean store2Memory(List<T> list, Class obj){
        Log.e(TAG, "store2Memory");
        if(data == null) data = new ArrayList<T>();
        Class<?> aClass = list.get(0).getClass();
        if(clazz == null)  {
            clazz = obj;
        }

        if(clazz == obj){
            Log.e(TAG, "clazz == obj.getClass");
        }

        if(clazz != null && list != null && list.size() > 0 && clazz != null && clazz == obj.getClass()){
            // 同一个类型
            data.addAll(list);
        } else if(clazz != null && list != null && list.size() > 0 && clazz != null && clazz != obj.getClass()){
            // 不同类型
            clazz = obj;
            data = null;
            data = new ArrayList<T>();
            data.addAll(list);
        }
        Log.e(TAG, "print list size = " + list.size());
       for(int i = 0; i < data.size(); i++){
           Log.e(TAG, data.get(i).toString());
       }
        return (data != null && data.size() > 0) ? true : false;
    }

    public <T> boolean existInMemory(Class<T> clazz){
//        if(memoryList instanceof  )
        try {
            ParameterizedType paramType = (ParameterizedType)DataCenterImpl.class.getField("memoryList").getGenericType();
            Log.e(TAG, "Class Type=" + paramType.getActualTypeArguments()[0].toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

}
