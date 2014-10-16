package cn.cczw.sjsb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.cczw.sjsb.base.BaseActivity;

/**
 * 自定义生成页面默认布局，由代码发起，必传参数（Intent），
 * ptype 页面类型  web:纯webview  native:纯原生空白页面
 * config 配置文件  ptype=web时：url；ptype=native时：json字符串
 * @author awen
 */
public class InflaterActivity extends BaseActivity{
	View root;	//根布局
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//处理消息
		Intent intent = getIntent();
		String ptype = intent.getStringExtra("ptype");
		String config = intent.getStringExtra("config");
		
		switch(ptype){
		case "web":
			setContentView(R.layout.webview_swiperefresh);
			initWebView(R.id.webview,config,true);
			break;
		case "native":
			setContentView(R.layout.inflater);
			root = findViewById(R.id.root_layout);
			break;
		}
	}
}
