package cn.cczw.sjsb;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import cn.cczw.sjsb.base.BaseActivity;

public class SJSBTestActivity extends BaseActivity {
	
	private SwipeRefreshLayout mSwipeLayout;
	private OnRefreshListener swipeListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_container);
		
		swipeListener = new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeLayout.setRefreshing(false);
			}
		};
		
		mSwipeLayout.setOnRefreshListener(swipeListener);
		mSwipeLayout.setColorScheme(android.R.color.holo_green_dark, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		initView();
	}
	
	public void initView(){
		
		
		
		long t = System.currentTimeMillis(); 
//		String url = "http://www.baidu.com";
//		String url = "http://10.2.5.119/test.html?t="+t;
//		initWebView(R.id.webview,url,Test_SJSB.this);
		initWebView(R.id.webview,"file:///android_asset/template.html?v="+t);
	}
}
