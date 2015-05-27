package com.jiyouliang.datastore;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inter.sharesdk.api.SharePlatform.SearchListener;
import com.inter.sharesdk.model.SearchData;
import com.jiyouliang.datastore.adapter.MyAdapter;
import com.jiyouliang.datastore.datacenter.DataCenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener,
		SearchListener {

	private ListView listView;
	private MyAdapter adapter;
	private List<SearchData> listData;
	private View footerView;
	private TextView tvCurPage;
	private int pagenum = 1; // 当前页码
	/**
	 * 最多显示的页数
	 */
	private final int MAX_PAGE = 8;
	private View next_page_view;
	private View last_page_view;
	private DataCenter dataCenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.listview);
		if (listData == null)
			listData = new ArrayList<SearchData>();
		initFooterView();

		adapter = new MyAdapter(this, listData);
		listView.setAdapter(adapter);

		dataCenter = DataCenter.getInstance(this);
		dataCenter.getSearchData(this, pagenum);
	}

	/**
	 * 底部分页View
	 */
	public void initFooterView() {
		footerView = LayoutInflater.from(this).inflate(R.layout.bottom_layout,
				null);
		tvCurPage = (TextView) footerView.findViewById(R.id.tv_cur_page);
		last_page_view = footerView.findViewById(R.id.iv_last_page);
		next_page_view = footerView.findViewById(R.id.iv_next_page);
		footerView.setVisibility(View.GONE);
		listView.addFooterView(footerView);
		tvCurPage.setText("第" + pagenum + "页");
		last_page_view.setVisibility(View.INVISIBLE);
		last_page_view.setOnClickListener(this);
		next_page_view.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.iv_next_page) {
			// 下一页
			showNextPage();
		} else if (v.getId() == R.id.iv_last_page) {
			// 上一页
			showLastPage();
		}
	}

	/**
	 * 上一页
	 */
	private void showLastPage() {
		if (--pagenum > 1) {
			dataCenter.getSearchData(this, pagenum);
			last_page_view.setVisibility(View.VISIBLE);
		} else {
			last_page_view.setVisibility(View.INVISIBLE);
			dataCenter.getSearchData(this, pagenum);
		}
	}

	/**
	 * 下一页
	 */
	private void showNextPage() {
		if (++pagenum <= MAX_PAGE) {
			dataCenter.getSearchData(this, pagenum);
			last_page_view.setVisibility(View.VISIBLE);
		} else {
			-- pagenum;
			Toast.makeText(this, "已经是最后一页", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSearchCallBack(ArrayList<SearchData> data) {
		if(data != null && data.size() > 0){
			listData.clear();
			listData.addAll(data);
			adapter.notifyDataSetChanged();
			tvCurPage.setText("第"+pagenum+"页");
			footerView.setVisibility(View.VISIBLE);
		}
	}

}
