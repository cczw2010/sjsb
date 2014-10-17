package cn.cczw.sjsb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.cczw.sjsb.base.BaseActivity;

/**
 * 自定义生成页面默认布局，由代码发起，必传参数（Intent），
 * ptype 页面类型  web:纯webview  native:纯原生空白页面
 * config 配置文件  ptype=web时：url；ptype=native时：json字符串
 * @author awen
 */
public class InflaterActivity extends BaseActivity{
	LinearLayout root;	//根布局
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
			root = (LinearLayout) findViewById(R.id.root_layout);
			addScrollList();
			//addLayout();
			break;
		}
	}
	
	
	public void addLayout(){
		LinearLayout layout = new LinearLayout(this);
		
		TextView textview = new TextView(this);
		textview.setText("this is test");
		
		layout.addView(textview);
		root.addView(layout);
	}
	
	@SuppressLint("ResourceAsColor")
	public void addScrollList(){

		ScrollView scroll = new ScrollView(this);
		scroll.setFillViewport(true);
		
		String data[]={"谷歌","苹果","微软","IBM","亚马逊","甲骨文","三星","联想","索尼","HTC","诺基亚","摩托罗拉","百度","华为","中兴"};
//		String data[]={"谷歌","苹果","微软","IBM"};
		ListView list = new ListView(this);
		list.setBackgroundColor(990033);
		list.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,data));
		
		
		LayoutParams params =  new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		scroll.addView(list, params);
		root.addView(scroll,params);
	}
}
