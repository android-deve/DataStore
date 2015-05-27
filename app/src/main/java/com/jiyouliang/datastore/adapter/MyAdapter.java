package com.jiyouliang.datastore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inter.sharesdk.model.SearchData;
import com.jiyouliang.datastore.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * 适配器，不继承BadaAdapter，使用ArrayAdapter更方便，不需要ViewHoder
 * @author youliang.ji
 *
 */
public class MyAdapter extends ArrayAdapter<SearchData> {

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public MyAdapter(Context context, List<SearchData> data) {
		super(context, 0, data);
		initImageLoader(context);  
	}

	/**
	 * 初始化ImageLoader
	 * @param context
	 */
	private void initImageLoader(Context context) {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));  
		
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.image_default)
        .showImageForEmptyUri(R.drawable.image_default)  
        .showImageOnFail(R.drawable.image_default)  
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .considerExifParams(true)  
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .build();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SearchData item = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_item_layout, parent, false);
		}
		setView(convertView, item);
		return convertView;
	}

	/**
	 * 渲染页面
	 * @param convertView
	 * @param item
	 */
	private void setView(View convertView, SearchData item) {
		TextView name = (TextView) convertView.findViewById(R.id.tv_name);
		TextView address = (TextView) convertView.findViewById(R.id.tv_address);
		TextView phone = (TextView) convertView.findViewById(R.id.tv_phone);
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		name.setText(item.getName() != null ? item.getName() : "");
		address.setText(item.getAddress() != null ? item.getAddress() : "");
		phone.setText(item.getPhone() != null ? item.getPhone() : "");
		Log.e("MyAdapter", "uri = "+item.getUrl());
		imageLoader.displayImage(item.getUrl(), image, options);
	}
}
