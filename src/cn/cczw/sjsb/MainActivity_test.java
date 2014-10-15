package cn.cczw.sjsb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.cczw.sjsb.base.BaseActivity;

public class MainActivity_test extends BaseActivity {
	Button btn1 = null;
	Button btn2 = null;
	OnClickListener btnclick =null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		btnclick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				switch(v.getId()){
				case R.id.button1:
					intent.setClass(MainActivity_test.this, MainActivity.class);
					break;
				}			
				startActivity(intent);
			}
		};
		
		initView();
		initEvent();
		
		
	}
	
	@SuppressLint("InlinedApi")
	public void initView(){
		btn1 = (Button) findViewById(R.id.button1);
//		btn2 = (Button) findViewById(R.id.button2);
	}
	
	public void initEvent(){
		btn1.setOnClickListener(btnclick);
//		btn2.setOnClickListener(btnclick);
	}
	
}