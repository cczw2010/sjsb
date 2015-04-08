package cn.cczw.sjsb;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/**
 * 自定义生成页面默认布局，由代码发起，必传参数（Intent），
 * ptype 页面类型  web:纯webview  native:纯原生空白页面
 * config 配置文件  ptype=web时：url；ptype=native时：json字符串
 * @author awen
 */
public class InflaterActivity extends BaseActivity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		//处理消息
		Intent intent = getIntent();
		String ptype = intent.getStringExtra("ptype");
		String config = intent.getStringExtra("config");
		
		switch(ptype){
		case "web":
			setContentView(R.layout.webview_swiperefresh);
			config = resolveUrl(config);
			initWebView(R.id.webview,config,true);
			break;
		case "native":
			setContentView(R.layout.inflater);
			//LinearLayout root = (LinearLayout) findViewById(R.id.root_layout);
			break;
		}
	}
	/**
	 * web文件地址解析，本地地址自动转成android assets格式，url地址不作处理
	 */
	private String  resolveUrl(String url){
		if(!url.startsWith("http:")&&!url.startsWith("file:")){
			url = "file:///android_asset/"+url;
		}
		return url;
	}
}
