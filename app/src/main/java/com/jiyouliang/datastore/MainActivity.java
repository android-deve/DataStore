package com.jiyouliang.datastore;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
import com.jiyouliang.datastore.datacenter.DataCenterImpl;
import com.jiyouliang.datastore.model.Student;
import com.jiyouliang.datastore.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener,
		SearchListener {

	private static final String TAG = MainActivity.class.getSimpleName();
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

		List<User> user = initTempUser();
		List<User> user2 = initTempUser2();
		List<Student> students = iniStudentList();

		DataCenterImpl.getInstance(this).store2Cache(user, "user.text", User.class);
		DataCenterImpl.getInstance(this).store2Cache(students, "student.text", Student.class);

		Class cl1 = User.class;
		Class cl2 = User.class;
		Class cl3 = Student.class;


		Log.e(TAG, "get user from cache");
		List<User> cacheList = DataCenterImpl.getInstance(this).getFromCache(User.class, "user.text");
		for(User u : cacheList){
			Log.e(TAG,u.toString());
		}

		Log.e(TAG, "get student from cache");
		List<Student> studentList = DataCenterImpl.getInstance(this).getFromCache(Student.class, "student.text");
		for(Student u : studentList){
			Log.e(TAG, u.toString());
		}
	}

	private List<User> initTempUser(){
		List<User> userList = new ArrayList<User>();
		for(int i = 0; i < 10;i ++){
			User user = new User();
			user.setName("Jack" + i);
			user.setAge(10 + i);
			user.setAddress("汇潮科技大厦" + i);

			userList.add(user);
		}
		return userList;
	}

	private List<User> initTempUser2(){
		List<User> userList = new ArrayList<User>();
		for(int i = 0; i < 10;i ++){
			User user = new User();
			user.setName("Jack" + i + 100);
			user.setAge(10 + i);
			user.setAddress("汇潮科技大厦" + i + 100);

			userList.add(user);
		}
		return userList;
	}

	private List<Student> iniStudentList(){
		List<Student> students = new ArrayList<Student>();
		for(int i = 0;i < 3; i ++){
			Student stu = new Student();
			stu.setAchool("北大" + i);
			stu.setProfessional("计算机科学与技术" + i);
			students.add(stu);
		}
		return students;
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
