package cn.cczw.sjsb.base;

import java.io.File;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.cczw.comm.MyApplication;
import cn.cczw.comm.MyChromeClient;
import cn.cczw.sjsb.MainActivity;

public class BaseActivity extends Activity {
 	public MyApplication app = null;
	public WebView swebview = null;
	private Handler shandler = null;
	
	//protected HashMap<String,String> loadFns = null;	//页面加载完毕后执行（完后删除）的回调函数(key),参数字符串（val）
	public static String EXITAPP_MESSAGE = "exitapp";   //退出程序的标示
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //防止input被键盘挡住，缩放
		
		app = (MyApplication) MyApplication.getInstance();
		shandler = new BaseHandler();
		//loadFns = new HashMap<String, String>();
		//处理退出消息
		Intent intent = getIntent();
		String msg = intent.getStringExtra("data");
		if(msg!=null && EXITAPP_MESSAGE.equals(msg)){
			this.finish();
		}
	}
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if(Constants.JS_MENUBTN_CALLBACK!=null && Constants.JS_MENUBTN_CALLBACK!=""){
			runjs(Constants.JS_MENUBTN_CALLBACK+"()");
			return false;
		}else{
			return super.onMenuOpened(featureId, menu);
		}
	}
	@Override
	public void onBackPressed() {
		if(Constants.JS_BACKBTN_CALLBACK!=null && Constants.JS_BACKBTN_CALLBACK!=""){
			runjs(Constants.JS_BACKBTN_CALLBACK+"()");
		}else{
	 		super.onBackPressed();
		}
	}

	/* ----------------------- web --------------------------- */
	// 初始化webview,context为当前activity上下文  即.Activity.this
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	public void initWebView(int webviewid, String url) {
		swebview = (WebView) findViewById(webviewid);
		
		WebSettings ws = swebview.getSettings();
		ws.setJavaScriptCanOpenWindowsAutomatically(true);

		ws.setRenderPriority(RenderPriority.HIGH);// 提高渲染的优先级
		ws.setJavaScriptEnabled(true);
		
		ws.setDefaultTextEncodingName("UTF-8");
		ws.setSupportZoom(false);	// 设置是否支持缩放
		ws.setBuiltInZoomControls(false);// 设置是否启用内置的缩放控件
		
		ws.setSupportMultipleWindows(true);
 		ws.setAllowFileAccess(true);
		ws.setGeolocationEnabled(true);
		ws.setDomStorageEnabled(true);
		//设置定位的数据库路径
		//ws.setGeolocationDatabasePath(dir);
  		ws.setDatabaseEnabled(true);
		//设置数据库路径
		//ws.setDatabasePath(dir);
  		//把图片加载放在最后来加载渲染  4.4的话会出现无法加载图片的bug，鬼知道为什么
  		if(Build.VERSION.SDK_INT<19){
  			ws.setBlockNetworkImage(true);
  		}
  		ws.setLoadsImagesAutomatically(true); //自动加载图片
   		//开启应用程序缓存api
  		//ws.setAppCacheEnabled(true);
  		//String dir = app.getDir("cache", Context.MODE_PRIVATE).getPath();
  		//设置应用缓存的路径
  		//ws.setAppCachePath(dir);
  		//设置缓存的模式
  		ws.setCacheMode(WebSettings.LOAD_DEFAULT);
  		//设置应用缓存的最大尺寸
  		//ws.setAppCacheMaxSize(1024*1024*8);

  		swebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 去掉滚动条占位
 		swebview.setHorizontalScrollBarEnabled(false);
		swebview.setHorizontalScrollbarOverlay(false);
		swebview.setVerticalScrollBarEnabled(false);
		swebview.setVerticalScrollbarOverlay(false);
		swebview.setVerticalFadingEdgeEnabled(true);
		swebview.requestFocus();// 支持网页内部操作，比如点击按钮
//		swebview.setFocusable(true);
		swebview.setSelected(false);
		swebview.setSaveEnabled(false);//系统回收时，不保留webview内容,必须，防止addJavascriptInterface出问题
		swebview.setWebChromeClient(new chromeClient());
		swebview.setWebViewClient(new webviewClient());
		
		swebview.addJavascriptInterface(new JavascriptBridge(swebview,getApplicationContext(),this), Constants.JS_BridgeName);//注入js		
		// swebview.setLongClickable(false);
		// swebview.cancelLongPress(); //奇怪，这个设置后 有时候轻点击也不管用了
		swebview.loadUrl((url).trim());
		
	}
	class webviewClient extends WebViewClient{
 		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("SJSB", "shouldOverrideUrlLoading url=" + url);
			//Uri uri = Uri.parse(url); // url为你要链接的地址
			//view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}
	}
	class chromeClient extends MyChromeClient{
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			//Log.d("SJSB", "onProgressChanged newProgress:>"+newProgress);
			//if(newProgress==100){
		    //    Set<String> key = loadFns.keySet();
		    //    for (Iterator<String> it = key.iterator();it.hasNext();) {
			//		String fn = it.next();
			//		runjs(fn+"('"+loadFns.get(fn)+"')");
			//	}
		    //    loadFns.clear();
			//}
			super.onProgressChanged(view, newProgress);
		}
	}
	/**
	 *onActivityResult回调部分（例如js相机）
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("SJSB","onActivityResult====requestCode："+requestCode+">>resultCode："+resultCode);
		String _data =  null;
    	switch (requestCode) {
	    	case Constants.JS_REQUEST_CODE_CAMERA_CAPTURE: //js相机缩略图
	    		if(resultCode == RESULT_OK){
			    	Bundle bundle=data.getExtras();  //data为B中回传的Intent
			    	Bitmap bitmap = (Bitmap) bundle.get("data");
			    	_data = app.saveFileToSdcard(bitmap,Constants.JS_CAMERA_JPG,100);
			    	if(_data!=null && _data!=""){
			    		_data = "file://"+_data;
			    	}
	    		}
	    		break;
	    	case Constants.JS_REQUEST_CODE_CAMERA:   //js相机原图
	    		if(resultCode == RESULT_OK){
	    			_data =app.getAppFilePath()+File.separator+Constants.JS_CAMERA_JPG;
	    			if(_data!=null && _data!=""){
			    		_data = "file://"+_data;
			    	}
	    		}
	    		break;
	    	case Constants.JS_SCANNING_REQUEST_CODE:   //二维码扫描
	    		if(resultCode == RESULT_OK){
	    			_data = data.getStringExtra("data");
	    		}
	    		break;
	    	default:
	    		break;
    	}
    	final String callback =Constants.JS_CAMERA_CALLBACK;
    	final String result = _data==null?"":_data;
		Log.d("SJSB",callback+":"+result);
		//执行callback
		if(callback!=null && ""!=callback){
			//sdk19 以后onActivityResult返回当前页后会刷新,所以如果此处要执行js,
			//很可能js执行时页面还没有加载完毕，造成异常
			//if(Build.VERSION.SDK_INT<19 || swebview.getProgress()<100){
				runjs(callback+"('"+result+"')");
			//}else{
			//	loadFns.put(callback, result);
			//}
		}
	}
	/* ----------------------- common method --------------------------- */
	// 发送消息
	public void sendmessage(int message,String fn, String param) {
		Message msg = shandler.obtainMessage(message);
		Bundle data = new Bundle();
		data.putString("fn", fn);
		data.putString("param", param);
		msg.setData(data);
		shandler.sendMessage(msg);
	}
	/**
	 * 退出app
	 */
	public void exitApp(){
		//Intent intent=new Intent(Intent.ACTION_MAIN);
		//intent.addCategory(Intent.CATEGORY_HOME); 返回桌面
		Intent intent = new  Intent();
		intent.setClass(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("data", EXITAPP_MESSAGE);
		startActivity(intent);
	}
	/**执行js方法，无返回值*/
	public void runjs(String jsstr){
		//Log.d("SJSB",jsstr);
		///if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			swebview.loadUrl("javascript:"+jsstr);
		//} else {
			//swebview.evaluateJavascript("javascript:"+jsstr,null);
		//}
	}
} // cls
