package cn.cczw.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;

public class CommonApi {
	private final String SYS_SETTING="CCZW_SYS_SETTING";
	private static CommonApi instance=null;
	private TelephonyManager tm = null;
	private Context context=null;
	
 	//valus
	private Map<String,Object> mAppValues=null;
	
	CommonApi(Context context){
		if(instance==null){
			this.context = context;
			tm = (TelephonyManager) ((ContextWrapper) context).getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
			
			//应用临时变量
			mAppValues=new HashMap<String,Object>();
			
			instance = this;
		}
	}
	
	//获取单例,确保调用之前已经初始化
	public static CommonApi getInstance(){
		return instance;
	}
	/***********一些系统路径***********/
	public String getSdcardPath(){
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	public String getAppRootPath(){
		return  Environment.getRootDirectory().getAbsolutePath();
	}
	public String getCachePath(){
		return context.getCacheDir().getAbsolutePath();
	}
	public String getSdcardCachePath(){
		return context.getExternalCacheDir().getAbsolutePath();
	}
	public String getFilePath(){
		return context.getFilesDir().getAbsolutePath();
	}
	public String getSdcardFilePath(){
		return context.getExternalFilesDir(null).getAbsolutePath();
	}
	
	/**
	 * 获取设备唯一标示码id，单独获取deviceid,seril number,androidid 都不保险，很多设备或多或少都有问题，最好的办法就是都要，加班返回
	 * @return
	 */
	public String getUUID(){
	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	 
	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return deviceUuid.toString();
	}
	/**
	 * 获取设备信息  map
	 */
	public Map<String,Object> getDeviceInfo(){
		Map<String,Object> infos = new HashMap<String,Object>(); 
		infos.put("uuid", getUUID());
		infos.put("phoneNo", tm.getLine1Number());
		infos.put("phoneType",tm.getPhoneType());
		infos.put("netOperName", tm.getNetworkOperatorName());
		infos.put("netOperNo",tm.getNetworkOperator());
		infos.put("netTypeNo",tm.getNetworkType());
		infos.put("iso", tm.getNetworkCountryIso());
		infos.put("device_model", Build.MODEL);// 设备型号 
		infos.put("version_sdk", Build.VERSION.SDK_INT);// 设备SDK版本 
		infos.put("version_release", Build.VERSION.RELEASE);// 设备的系统版本  
		infos.put("hardware", Build.HARDWARE); 
		infos.put("host", Build.HOST);
		return infos;
	}
	/********************application value***********************/
	public Map<String,Object> getAppValues(){
		return this.mAppValues;
	}
	public Object getAppValue(String appkey){
		return this.mAppValues.get(appkey);
	}
	public String getAppString(String appkey){
		Object v=getAppValue(appkey);
		if(v!=null){
			return v.toString();
		}
		return null;
	}
	public void setAppValue(String appkey,Object appValue){
		 this.mAppValues.put(appkey, appValue);
	}
	public void setAppString(String appkey,String appValue){
		 this.mAppValues.put(appkey, appValue);
	}
	/**删除并返回某键值*/
	public Object removeAppValue(String appkey){
		return this.mAppValues.remove(appkey);
	}
	public void clearAppValues(){
		this.mAppValues.clear();
	}
	/********************SharedPreferences value***********************/
	public SharedPreferences getPreferences(){
		return context.getSharedPreferences(SYS_SETTING, Context.MODE_PRIVATE);
	}
	@SuppressWarnings("unchecked")
	public Map<String,Object> getPrefValues(){
		SharedPreferences pref=context.getSharedPreferences(SYS_SETTING,Context.MODE_PRIVATE);
		return (Map<String, Object>) pref.getAll();
	}
	public String getPrefString(String key){
		SharedPreferences pref=context.getSharedPreferences(SYS_SETTING,Context.MODE_PRIVATE);
		return pref.getString(key, null);
	}
	public boolean setPrefString(String key,String value){
		SharedPreferences pref = context.getSharedPreferences(SYS_SETTING,Context.MODE_PRIVATE);
		return pref.edit().putString(key, value).commit();
	}
	public boolean setPrefInt(String key, int v) {
		SharedPreferences pref = context.getSharedPreferences(SYS_SETTING,Context.MODE_PRIVATE);
		return pref.edit().putInt(key, v).commit();
	}
	public boolean removePrefValue(String key) {
		SharedPreferences pref = context.getSharedPreferences(SYS_SETTING,Context.MODE_PRIVATE);
		return pref.edit().remove(key).commit();
	}
	public boolean clearPrefValues(){
		SharedPreferences pref = context.getSharedPreferences(SYS_SETTING,Context.MODE_PRIVATE);
		return pref.edit().clear().commit();
	}
	
	/**获取app版本号
	 */
	public String getAppVersion(){
		PackageInfo info;
		String version=null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			 // 当前应用的版本名称  
			version = info.versionName;
		    // 当前版本的版本号  
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return version;
	}
	/**
	 * 将view截图
	 * @param view  要截取的片的view
	 * @return Bitmap
	 * */
	public Bitmap shotView(View view) {
		int heigh=view.getHeight();
		int width=view.getWidth();
		Bitmap bmp=null;
		//if(view.getClass()==WebView.class){
		//	Picture snapShot =((WebView)view).capturePicture();
		//	bmp = Bitmap.createBitmap(snapShot.getWidth(),snapShot.getHeight(), Bitmap.Config.ARGB_8888);
		//	Canvas canvas = new Canvas(bmp);
		//	snapShot.draw(canvas);
		//}else{
			bmp = Bitmap.createBitmap(width,heigh, Bitmap.Config.ARGB_8888);
			view.draw(new Canvas(bmp));
		//}
        return bmp;
    }
	/**
	 * 将bitmap转换成base64 的jpg
	 * @param bitmap    图片
	 * @param quantity   图片质量
	 * @return String
	 */
	public String bmp2Base64(Bitmap bitmap,int quantity){
		String result = null;
        try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        bitmap.compress(Bitmap.CompressFormat.JPEG, quantity, baos);  
			baos.flush();
	        baos.close(); 
	        byte[] bitmapBytes = baos.toByteArray();
	    	result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "data:image/jpg;base64,"+result;
	}
	
}
