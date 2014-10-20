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
import cn.cczw.sjsb.MainActivity;
import cn.cczw.sjsb.R;
import cn.swiperefreshandload.SwipeRefreshLayout;
import cn.swiperefreshandload.SwipeRefreshLayout.OnLoadListener;
import cn.swiperefreshandload.SwipeRefreshLayout.OnRefreshListener;

@SuppressLint({ "InlinedApi", "ResourceAsColor" })
public class BaseActivity extends Activity {
 	public MyApplication app = null;
	public WebView swebview = null;
	private Handler shandler = null;
	private SwipeRefreshLayout mSwipeLayout;

	public static String EXITAPP_MESSAGE = "exitapp";   //退出程序的标示
	public static String PAGE_ERROR = "file:///android_asset/error.html";   //加载本地页面错误显示页
	/*js request_code部分*/
	final public static int JS_REQUEST_CODE_CAMERA = 930001;		//js相机原图 request_code
	final public static int JS_REQUEST_CODE_CAMERA_CAPTURE = 930002;//js相机缩略图 request_code
    final public static int JS_SCANNING_REQUEST_CODE = 930003;   	//js扫二维码 request_code
    /*message部分*/
    final public static int MESSAGE_SWIPEMODE = 1000;				// 设置上拉下拉控件不可用
    final public static int MESSAGE_CLEARSWIPEANIM = 1001;			// 清空上拉下拉动画
    
	//临时文件
	public static String JS_CAMERA_JPG = null;	//相机照的临时文件
	final public static String JS_SNAPSHOT_JPG = "snapshot_js.jpg";//快照临时文件
	//js回调函数
	public String JS_CAMERA_CALLBACK = null;		//相机回调
	public String JS_MENUBTN_CALLBACK = null;		//菜单键回调
	public String JS_BACKBTN_CALLBACK = null;		//返回按钮回调
	public String JS_SWIPEREFRESH_CALLBACK = null;	//下拉刷新时的回调
	public String JS_SWIPERELOAD_CALLBACK = null;	//上拉刷新时的回调
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //防止input被键盘挡住，缩放
		
		app = (MyApplication) MyApplication.getInstance();
		shandler = new BaseHandler();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		//处理退出消息
		String msg = intent.getStringExtra("data");
		if(msg!=null && EXITAPP_MESSAGE.equals(msg)){
			this.finish();
		}
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//Log.d("SJSB",this.JS_MENUBTN_CALLBACK+"==JS_MENUBTN_CALLBACK");
		if(swebview!=null && JS_MENUBTN_CALLBACK!=null && JS_MENUBTN_CALLBACK!="" && !PAGE_ERROR.equals(swebview.getUrl())){
			runjs(JS_MENUBTN_CALLBACK+"()");
			return false;
		}else{
			return super.onPrepareOptionsMenu(menu);
		}
	}
	@Override
	public void onBackPressed() {
		if(swebview!=null && JS_BACKBTN_CALLBACK!=null && JS_BACKBTN_CALLBACK!="" && !PAGE_ERROR.equals(swebview.getUrl())){
			runjs(JS_BACKBTN_CALLBACK+"()");
		}else if(swebview!=null && swebview.canGoBack()){
			swebview.goBack();
		}else{
			super.onBackPressed();
		}
	}
	
	/* ----------------------- web --------------------------- */
	@SuppressWarnings("deprecation")
	public void initWebView(int webviewid,String url,boolean enablePullPush) {
		swebview = (WebView) findViewById(webviewid);
		//是否初始化下拉刷新，上拉加载控件
		if(enablePullPush){
			mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_container);
			mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					if(JS_SWIPEREFRESH_CALLBACK!=null && JS_SWIPEREFRESH_CALLBACK!=""){
						runjs(JS_SWIPEREFRESH_CALLBACK+"()");
					}else{
						mSwipeLayout.setRefreshing(false);
					}
				}
			});
			//下拉刷新
			mSwipeLayout.setOnLoadListener(new OnLoadListener() {
				@Override
				public void onLoad() {
					if(JS_SWIPERELOAD_CALLBACK!=null && JS_SWIPERELOAD_CALLBACK!=""){
						runjs(JS_SWIPERELOAD_CALLBACK+"()");
					}else{
						mSwipeLayout.setLoading(false);
					}
				}
			});
	        mSwipeLayout.setColor(R.color.holo_blue_bright,
		                            R.color.holo_green_light,
		                            R.color.holo_orange_light,
		                            R.color.holo_red_light);
	        mSwipeLayout.setMode(SwipeRefreshLayout.Mode.DISABLED);
	        //mSwipeLayout.setLoadNoFull(true);
		}
		
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
  		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
  		//设置应用缓存的最大尺寸
  		//ws.setAppCacheMaxSize(1024*1024*8);

  		swebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 去掉滚动条占位
 		swebview.setHorizontalScrollBarEnabled(false);
		swebview.setHorizontalScrollbarOverlay(false);
		swebview.setVerticalScrollBarEnabled(false);
		swebview.setVerticalScrollbarOverlay(false);
		swebview.setVerticalFadingEdgeEnabled(false);
		swebview.requestFocus();// 支持网页内部操作，比如点击按钮
//		swebview.setFocusable(true);
		swebview.setSelected(false);
		swebview.setSaveEnabled(false);//系统回收时，不保留webview内容,必须，防止addJavascriptInterface出问题
		swebview.setWebChromeClient(new MyChromeClient());
		swebview.setWebViewClient(new webviewClient());
		swebview.addJavascriptInterface(new JavascriptBridge(swebview,getApplicationContext(),this), getString(R.string.jsclassname));//注入js		
		// swebview.setLongClickable(false);
		// swebview.cancelLongPress(); //奇怪，这个设置后 有时候轻点击也不管用了
		swebview.loadUrl((url).trim());
	}
	/**
	 *  清空下拉上拉动画
	 */
	private void clearSwipeLayoutAnim(){
		if(mSwipeLayout!=null){
			mSwipeLayout.setRefreshing(false);
			mSwipeLayout.setLoading(false);
		}
	}
	
	class webviewClient extends WebViewClient{
 		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
 			Log.d("sjsb", "shouldOverrideUrlLoading:"+url);
 			clearSwipeLayoutAnim();
 			return super.shouldOverrideUrlLoading(view, url);
		}
 		
 		@Override
 		public void onReceivedError(WebView view, int errorCode,
 				String description, String failingUrl) {
			Log.d("sjsb","onReceivedError:"+failingUrl+":errorCode="+errorCode+";description="+description);
 			//如果带下拉刷新，清空动画
			clearSwipeLayoutAnim();
			super.onReceivedError(view, errorCode, description, failingUrl);
 		}
		@Override
		public void onPageFinished(WebView view, String url) {
			//Log.d("sjsb","onPageFinished>参数url:"+url+";当前url:"+swebview.getUrl());
			//如果带下拉刷新，清空动画
			clearSwipeLayoutAnim();
			super.onPageFinished(view, url);
			//Dialogs.getShareDialog(BaseActivity.this, "asdasdasdasd").show();
		}
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			//Log.d("sjsb","onPageStarted:"+url);
			super.onPageStarted(view, url, favicon);
		}
		@Override
		public void onLoadResource(WebView view, String url) {
			//Log.d("sjsb","onLoadResource:"+url);
			super.onLoadResource(view, url);
		}
	}
	/**
	 *onActivityResult回调部分（例如js相机）
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Log.d("SJSB","onActivityResult====requestCode："+requestCode+">>resultCode："+resultCode);
		String _data =  null;
    	switch (requestCode) {
	    	case JS_REQUEST_CODE_CAMERA_CAPTURE: //js相机缩略图
	    		if(resultCode == RESULT_OK){
			    	Bundle bundle=data.getExtras();  //data为B中回传的Intent
			    	Bitmap bitmap = (Bitmap) bundle.get("data");
			    	_data = app.saveFileToSdcard(bitmap,JS_CAMERA_JPG,100);
			    	if(_data!=null && _data!=""){
			    		_data = "file://"+_data;
			    	}
	    		}
	    		break;
	    	case JS_REQUEST_CODE_CAMERA:   //js相机原图
	    		if(resultCode == RESULT_OK){
	    			_data =app.getAppFilePath()+File.separator+JS_CAMERA_JPG;
	    			if(_data!=null && _data!=""){
			    		_data = "file://"+_data;
			    	}
	    		}
	    		break;
	    	case JS_SCANNING_REQUEST_CODE:   //二维码扫描
	    		if(resultCode == RESULT_OK){
	    			_data = data.getStringExtra("data");
	    		}
	    		break;
	    	default:
	    		break;
    	}
    	final String callback =this.JS_CAMERA_CALLBACK;
    	final String result = _data==null?"":_data;
		//Log.d("SJSB",callback+":"+result);
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
	/**
	 * 所有公共的message消息都从这里处理，包括js消息
	 * @author awen
	 *
	 */
	@SuppressLint("HandlerLeak")
	class  BaseHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			//Log.d("sjsb-msg",msg.toString());
			Bundle bundle = msg.getData();
			switch (msg.what) {
			case MESSAGE_SWIPEMODE:
				//设置下拉上拉控件模式
				if(mSwipeLayout!=null){
					String mode = bundle.getString("param");
					switch(mode){
					case "BOTH":
						mSwipeLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
						break;
					case "DISABLED":
						mSwipeLayout.setMode(SwipeRefreshLayout.Mode.DISABLED);
						break;
					case "REFRESH":
						mSwipeLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_START);
						break;
					case "LOAD":
						mSwipeLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_END);
						break;
					}
				}
				break;
			case MESSAGE_CLEARSWIPEANIM:
				//设置下拉刷新不可用
				clearSwipeLayoutAnim();
			default:
				break;
			}
		}
	}
	/**
	 * 发送消息
	 * @param message
	 * @param fn
	 * @param param
	 */
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
