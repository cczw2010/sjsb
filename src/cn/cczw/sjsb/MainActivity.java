package cn.cczw.sjsb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.cczw.sjsb.base.BaseActivity;

public class MainActivity extends BaseActivity {
	Button btn1 = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		initView();
		initEvent();
	}
	
	public void initView(){
		 btn1 = (Button) findViewById(R.id.button1);
	}
	
	
	public void initEvent(){
		btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SJSBTestActivity.class);
				startActivity(intent);
			}
		});
	}
}