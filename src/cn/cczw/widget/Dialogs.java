package cn.cczw.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Dialogs {
	public static Dialog getShareDialog(Context context,String title){
		int width=260;
		final Dialog dialog=new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//根布局
		LayoutParams lyparams=new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lyparams.gravity=Gravity.CENTER;
		lyparams.weight = 1.0f;
		
		LinearLayout lyroot=new LinearLayout(context);
		lyroot.setLayoutParams(lyparams);
		lyroot.getLayoutParams().width=width;
		lyroot.setOrientation(LinearLayout.VERTICAL);
		lyroot.setPadding(10, 3, 10, 3);

		//标题
		TextView tv=new TextView(context);
		tv.setLayoutParams(lyparams);
		tv.setGravity(Gravity.CENTER);
		tv.setText(title);
		tv.setTextSize(18);
		tv.setPadding(5, 5, 5, 5);
		tv.setTextColor(Color.DKGRAY);
 		//编辑框
		EditText et= new EditText(context);
		et.setHeight(120);
		et.setWidth(android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		et.setGravity(Gravity.TOP);
		
		//子布局
		LinearLayout ly=new LinearLayout(context);
		LayoutParams lp=new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		ly.setGravity(Gravity.CENTER);
		ly.setLayoutParams(lp);
		ly.setPadding(5, 5, 5, 5);

		//取消按钮
		Button btn_cancle=new Button(context);
		btn_cancle.setText("取消");
		btn_cancle.setTextSize(16);
		btn_cancle.setPadding(20, 5, 20, 5);
		btn_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dialog!=null){
					if(dialog.isShowing()){
						dialog.dismiss();
					}
				}
			}
		});
		//发表按钮
		Button btn_send=new Button(context);
		btn_send.setText("确定");
		btn_send.setTextSize(16);
		btn_send.setPadding(20, 5, 20, 5);
		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		ly.addView(btn_cancle);
		ly.addView(btn_send);

		lyroot.addView(tv);
		lyroot.addView(et);
		lyroot.addView(ly);

		dialog.setContentView(lyroot);
 		return dialog;
	}
	
	
}
