package cn.cczw.sjsb;

import android.os.Bundle;
import cn.cczw.sjsb.base.BaseActivity;

public class MainActivity extends BaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_swiperefresh);
		
		initView();
	}
	
	public void initView(){
		//long t = System.currentTimeMillis(); 
		//initWebView(R.id.webview,"file:///android_asset/index.html?v="+t,true);
		initWebView(R.id.webview,"file:///android_asset/index.html",true);
	}
}
