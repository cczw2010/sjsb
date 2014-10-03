package cn.cczw.comm;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;

public class MyChromeClient extends WebChromeClient{
	@Override
	public void onConsoleMessage(String message, int lineNumber,
			String sourceID) {
		Log.d("SJSB", "source="+sourceID+"\n\t--->linenumber=" + lineNumber + "\n\t--->message="+ message);
	}
	// 处理提示事件 
	@Override 
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) { 
		final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());  
        
        builder.setTitle("请输入").setMessage(message);  
                  
        final EditText et = new EditText(view.getContext());  
        et.setSingleLine();  
        et.setText(defaultValue);  
        builder.setView(et)  	
                .setPositiveButton("确定", new OnClickListener() {  
                    @Override
					public void onClick(DialogInterface dialog, int which) {  
                        result.confirm(et.getText().toString());  
                    }  
          
                })  
                .setNeutralButton("取消", new OnClickListener() {  
                    @Override
					public void onClick(DialogInterface dialog, int which) {  
                        result.cancel();  
                    }  
                });
        // 禁止响应按back键的事件  
        // builder.setCancelable(false);  
        AlertDialog dialog = builder.create();  
        dialog.show();  
        return true;  
        // return super.onJsPrompt(view, url, message, defaultValue,  
        // result);  
	}; 
	@Override
	public boolean onJsAlert(WebView view, String url, String message,
			final JsResult result) {
		// Log.d("Web Alert", message+" from "+url);
		// return super.onJsAlert(view, url, message, result);
		// 构建一个Builder来显示网页中的alert对话框
		Builder builder = new Builder(view.getContext());
		builder.setTitle("消息");
			builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						result.confirm();
					}
				});
		builder.setCancelable(false);
		builder.create();
		builder.show();
		return true;
	}
	@Override
	public void onRequestFocus(WebView view) {
		// TODO Auto-generated method stub
		super.onRequestFocus(view);
	}
	@Override
	public void onCloseWindow(WebView window) {
		// TODO Auto-generated method stub
		super.onCloseWindow(window);
	}
	@Override
	public boolean onJsConfirm(WebView view, String url, String message,
			final JsResult result) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());  
        builder.setTitle("请确认")  
                .setMessage(message)  
                .setPositiveButton("确定",new OnClickListener() {  
                            @Override
							public void onClick(DialogInterface dialog,int which) {  
                                result.confirm();  
                            }  
                        })  
                .setNeutralButton("取消", new OnClickListener() {  
                    @Override
					public void onClick(DialogInterface dialog, int which) {  
                        result.cancel();  
                    }  
                });  
        builder.setOnCancelListener(new OnCancelListener() {  
            @Override  
            public void onCancel(DialogInterface dialog) {  
                result.cancel();  
            }  
        });  
        // 禁止响应按back键的事件  
        // builder.setCancelable(false);  
        AlertDialog dialog = builder.create();  
        dialog.show();  
        return true;  
        // return super.onJsConfirm(view, url, message, result);  
	}
}