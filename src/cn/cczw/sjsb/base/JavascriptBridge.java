package cn.cczw.sjsb.base;

import java.io.File;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;
import cn.cczw.comm.AppReciver;
import cn.cczw.comm.CommonApi;
import cn.cczw.comm.GpsApi;
import cn.cczw.comm.MyApplication;
import cn.cczw.comm.VibratorApi;
import cn.cczw.sjsb.InflaterActivity;
import cn.cczw.util.DataCleanManager;
import cn.hugo.android.scanner.CaptureActivity;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;

public class JavascriptBridge {
	public WebView swebview = null;
	public Context appcontext = null;
	public BaseActivity activitycontent = null;
	public ProgressDialog webviewspd = null;
	public MyApplication myapp =null;
	public AppReciver myreciver =null;
	public CommonApi commapi =null;
	/**
	 * js桥接类
	 * @param webview  webview实例对象
	 * @param context  应用程序上下文
	 * @param activity 当前Activity实例
	 */
	public JavascriptBridge(WebView webview,Context context,Activity activity){
		swebview = webview;
		appcontext =  context;
		activitycontent = (BaseActivity)activity;
		myapp = (MyApplication) MyApplication.getInstance();
		myreciver = AppReciver.getInstance();
		commapi = CommonApi.getInstance();
	}
	/**
	 * 获取启动次数
	 */
	@JavascriptInterface
	public int getAppCounter(){
		return myapp.getAppCounter();
	}
	/**
	 * 获取应用版本
	 */
	@JavascriptInterface
	public String getAppVersion(){
		return commapi.getAppVersion();
	}
	/**
	 *  打印log
	 * @param tag
	 * @param msg
	 */
	@JavascriptInterface
	public void log(String tag, String msg) {
		Log.d(tag, msg);
	}
	/**
	 * 设置当菜单键按下时的js回调函数，如果为空则执行系统的原生操作
	 * @param callback
	 */
	@JavascriptInterface
	public void setMenuBtnFn(String callback){
		activitycontent.JS_MENUBTN_CALLBACK = callback;
	}
	/**
	 * 设置当返回键按下时的js回调函数，如果为空则执行系统的原生操作
	 * @param callback
	 */
	@JavascriptInterface
	public void setBackBtnFn(String callback){
		activitycontent.JS_BACKBTN_CALLBACK = callback;
	}
	/**
	 * 设置当前页下拉上拉控件的状态，初始为DISABLE
	 * @param String mode  DISABLED 禁用，ENABLE启用
	 */
	@JavascriptInterface
	public void setSwipeMode(String mode){
		activitycontent.sendmessage(BaseActivity.MESSAGE_SWIPEMODE, null,mode);
	}
	/**
	 * 停止上拉下拉动画
	 * @param String mode  DISABLED 禁用，BOTH 上下都要，REFRESH只要下拉刷新，LOAD只要上拉加载
	 */
	@JavascriptInterface
	public void clearSwipeAnim(){
		activitycontent.sendmessage(BaseActivity.MESSAGE_CLEARSWIPEANIM, null,null);
	}
	/**
	 * 设置下拉刷新的回调方法，前提是当前模式支持下拉刷新
	 * @param callback
	 */
	@JavascriptInterface
	public void setSwipeRefreshFn(String callback){
		activitycontent.JS_SWIPEREFRESH_CALLBACK = callback;
	}
	/**
	 * 退出应用
	 */
	@JavascriptInterface
	public void exitApp(){
		activitycontent.exitApp();
	}
	/**
	 *  显示loading
	 * @param str
	 */
	@JavascriptInterface
	public void showProgress(String str) {
		if(webviewspd==null){
			webviewspd = new ProgressDialog(activitycontent,ProgressDialog.STYLE_SPINNER);
			if(str == null){
				str = "";
			}
			webviewspd.setMessage(str);
		}
		if (!webviewspd.isShowing()) {
			webviewspd.show();
		}
	}
	/**
	 *  隐藏loading
	 */
	@JavascriptInterface
	public void disProgress() {
		if (webviewspd != null && webviewspd.isShowing()) {
			webviewspd.dismiss();
		}
	}
	/**
	 *  获取webview高
	 * @return
	 */
	@JavascriptInterface
	public int getWebHeight() {
		return swebview.getHeight();
	}
	/**
	 *  获取webview宽
	 * @return
	 */
	@JavascriptInterface
	public int getWebWidth() {
		return swebview.getWidth();
		//return swebview.getMeasuredWidth();
	}
	
	/**
	 * toast 提示框
	 * @param str 提示信息
	 * @param pos 位置 	0居中(默认)  1底部中间，2顶部中间，3左边中间，4右边中间
	 * @param inteval 显示时长 0短(默认) 1长
	 */
	@JavascriptInterface
	public void toast(String str,int pos,int inteval) {
		int _pos = Gravity.CENTER;
		int _inteval = Toast.LENGTH_SHORT;
		
		switch(pos){
		case 1:
			_pos = Gravity.BOTTOM;
			break;
		case 2:
			_pos = Gravity.TOP;
			break;
		case 3:
			_pos = Gravity.LEFT;
			break;
		case 4:
			_pos = Gravity.RIGHT;
			break;
		}
		
		if(inteval==1){
			_inteval = Toast.LENGTH_LONG;
		}
		Toast toast = Toast.makeText(appcontext, str, _inteval);
		toast.setGravity(_pos, 0, 0);
		toast.show();
	}
	/**
	 *  检查是否在线		
	 * @return
	 */
	@JavascriptInterface
	public boolean onLine(){
		return myreciver.hasNetWork(); 
	}
	/**
	 *  获取网络类型
	 *  0  无
	 *  1 wifi
	 *  2 gprs
	 *  3 cmwap
	 * @return
	 */
	@JavascriptInterface
	public int netType(){
		return  myreciver.getNetType();
	}
	
	/**
	 * 获取手机信息,返回json字符串
	 */
	@JavascriptInterface
	public String getDeviceInfo(){
		Map<String,Object> infos = commapi.getDeviceInfo();
		Gson gson = new Gson();
		return gson.toJson(infos);
	}
	/**
	 * 设置应用级变量,不随应用退出而退出,但不建议大量使用
	 * @param key	键
	 * @param val   值
	 */
	@JavascriptInterface
	public boolean setAppProp(String key,String val){
		return commapi.setPrefString(key,val);
	}
	/**
	 * 获取应用级变量
	 */
	@JavascriptInterface
	public String getAppProp(String key){
		return commapi.getPrefString(key);
	}
	/**
	 * 删除某应用级变量
	 */
	@JavascriptInterface
	public boolean removeAppProp(String key){
		return commapi.removePrefValue(key);
	}
	/**清空应用级变量
	 */
	@JavascriptInterface
	public boolean clearAppProp(){
		return commapi.clearPrefValues();
	}
	/**
	 * 清空浏览器缓存
	 */
	@JavascriptInterface
	public void clearWebCache(){
		try{
			swebview.post(new Runnable() {
	            @Override
	            public void run() {
	            	swebview.clearCache(true);
	            	swebview.clearFormData();
	            	swebview.clearHistory();
	            	CookieSyncManager.createInstance(activitycontent);   
	            	CookieSyncManager.getInstance().startSync();   
	            	CookieManager.getInstance().removeSessionCookie();   
	            }
	        });
		}catch(Exception e){
			Log.d("SJSB",e.getMessage());
		}
	}
	
	/**
	 * 清空应用缓存
	 */
	@JavascriptInterface
	public void clearAppCache(){
		DataCleanManager.cleanInternalCache(myapp);
		DataCleanManager.cleanDatabases(myapp);
		DataCleanManager.cleanExternalCache(myapp);
		DataCleanManager.cleanFiles(myapp);
		DataCleanManager.cleanSharedPreference(myapp);
		DataCleanManager.cleanCustomCache(myapp.getAppFilePath());
		clearWebCache();
		//		Log.d("SJSB-path",myapp.getAppRootPath());
		//		Log.d("SJSB-path",myapp.getSdcardPath());
		//		Log.d("SJSB-path",myapp.getCachePath());
		//		Log.d("SJSB-path",myapp.getSdcardCachePath());
		//		Log.d("SJSB-path",myapp.getPackageResourcePath());
		//		Log.d("SJSB-path",myapp.getFilePath());
		//		Log.d("SJSB-path",myapp.getSdcardFilePath());
	}
	/**
	 * 获取webview截屏
	 * @param int quantity 质量 0~100
	 * return String 本地文件路径（注意缓存）
	 */
	@SuppressLint("NewApi")
	@JavascriptInterface
	public String getSnapshot(int quantity){
		Bitmap bitmap = commapi.shotView(swebview);
		String result = activitycontent.saveFileToSdcard(bitmap,null,BaseActivity.JS_SNAPSHOT_JPG,60);
		//Log.d("SJSB",result);
		if(result!=null && result!=""){
			result = "file://"+result;
		}
		return result;
	}
	/**
	 * js相机
	 * @param type   返回原图还是缩略图   0  原图  1 缩略图
	 * @param save   0 不保存（其实所有的不保存图片公用一个文件缓存）  1保存（每次生成新的）
	 * @param callback  回调的js方法，得到一个文件地址，或者null
	 */
	@JavascriptInterface
	public void camera(int type,int save,String callback){
		//将callback注册到webview上
		activitycontent.JS_CAMERA_CALLBACK = callback;
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(save==1){
			BaseActivity.JS_CAMERA_JPG = "camera_"+System.currentTimeMillis()+".jpg";
		}else{
			BaseActivity.JS_CAMERA_JPG = "camera_tmp.jpg";
		}
		if(type==0){
			Uri uri = Uri.fromFile(new File(myapp.getAppFilePath(),BaseActivity.JS_CAMERA_JPG));
			intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);// 如果设置了这个将直接将图片保存为路径，否则将只返回一个Bitmap缩略图给activitycontent
	        activitycontent.startActivityForResult(intent,BaseActivity.JS_REQUEST_CODE_CAMERA);
		}else{
	        activitycontent.startActivityForResult(intent,BaseActivity.JS_REQUEST_CODE_CAMERA_CAPTURE);
		}
	}
	
	/**二维码解析*/
	@JavascriptInterface
	public void qrcodeCamera(String callback){
		//将callback注册到webview上
		activitycontent.JS_CAMERA_CALLBACK = callback;
  		
		Intent intent = new Intent();  
        intent.setClass(activitycontent, CaptureActivity.class);  
        activitycontent.startActivityForResult(intent, BaseActivity.JS_SCANNING_REQUEST_CODE);
       
	}
	/**
	 * 获取地理信息，（同步阻塞）
	 * return string (longitude 精度，latitude维度，altitude海拔,accuracy精度。。省，市，区。。。)
	 */
	@JavascriptInterface
	public String getLocation(){
		GpsApi.getInstance().start();
		BDLocation loc = null;
		while(loc==null){
			loc = GpsApi.getInstance().getLocation();
		}
		GpsApi.getInstance().stop();
		
		String json = loc==null?"":"{\"longitude\":"+loc.getLongitude()+ //经度
				";\"latitude\":"+loc.getLatitude()+				//维度
				//";\"altitude\":"+loc.getAltitude()+				//获取高度信息，目前没有实现
				";\"accuracy\":"+loc.getRadius()+				//定位精度
				";\"direction\":"+loc.getDirection()+			//获取手机当前的方向
				";\"province\":"+loc.getProvince()+				//省份
				";\"citycode\":"+loc.getCityCode()+
				";\"city\":"+loc.getCity()+						//获取城市
				";\"district\":"+loc.getDistrict()+				//获取区/县信息
				";\"street\":"+loc.getStreet()+					//街道信息
				";\"streetNumber\":"+loc.getStreetNumber()+
				";\"addr\":"+loc.getAddrStr()+					//获取详细地址信息
				"}";
		//Log.d("sjsb",">>"+json);
		return json;
	}
	/**
	 * 开始震动  
	 * @param continues 持续时间 毫秒
	 * @param betweens 间隔时间 毫秒
	 * @param times  震动循环次数，-1表示不循环 0 循环
	 */
	@JavascriptInterface
	public void vibrator(int continues, int betweens ,int times){
		VibratorApi.getInstance().start(continues,betweens,times);
	}
	/**
	 * 停止震动
	 */
	@JavascriptInterface
	public void stopVibrator(){
		VibratorApi.getInstance().stop();
	}
	
	/**新开web页面
	 *@param String ptype 页面类型  web:纯webview  native:纯原生空白页面
	 *@param String config 配置文件  ptype=web时：url；ptype=native时：json配置字符串
	 */
	@JavascriptInterface
	public void openView(String ptype,String config){
		Intent intent = new Intent();
		intent.setClass(activitycontent,InflaterActivity.class);
		intent.putExtra("ptype", ptype);
		intent.putExtra("config", config);

		activitycontent.startActivity(intent);
	}
}
