package cn.cczw.sjsb;

import android.os.Bundle;
import cn.cczw.sjsb.base.BaseActivity;

public class SJSBTestActivity extends BaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_swiperefresh);
		
		initView();
	}
	
	public void initView(){
		
		
		
		long t = System.currentTimeMillis(); 
//		String url = "http://www.baidu.com";
//		String url = "http://10.2.5.119/test.html?t="+t;
//		initWebView(R.id.webview,url,Test_SJSB.this,true);
		initWebView(R.id.webview,"file:///android_asset/template.html?v="+t,true);
	}
}
