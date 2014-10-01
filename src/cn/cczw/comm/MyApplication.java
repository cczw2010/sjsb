package cn.cczw.comm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.util.Log;

public class MyApplication extends Application {
	public static String TAG="cczw";
	//base
	private final String APPCOUNTER="_app_counter";
	private static Context context=null;
	private static MyApplication instance=null;
	private String path_cachefile = null;

	//phone status
	private AppReciver receiver=null;
	private int appLoadingnum;

	@Override
	public void onCreate() {
		super.onCreate();
		instance=this;
		context=getApplicationContext();
		//初始化一些公用的单例api
		new CommonApi(context);
		new GpsApi(context);
		new VibratorApi(context);
		//应用自定义缓存文件路径
		path_cachefile = CommonApi.getInstance().getSdcardPath()+File.separator+context.getPackageName();
		//同时生成sdcard目录
		File cachefile = new File(path_cachefile);
		if(!cachefile.isDirectory()){
			cachefile.mkdir();
		}
		//设置应用启动次数
		String counter = CommonApi.getInstance().getPrefString(APPCOUNTER);
		if(counter==null){
			appLoadingnum = 1;
		}else{
			appLoadingnum = Integer.parseInt(counter)+1;
		}
		 CommonApi.getInstance().setPrefString(APPCOUNTER, String.valueOf(appLoadingnum));
		Log.d("SJSB", "appcounter+"+appLoadingnum);
		//receiver
		receiver=new AppReciver();
		IntentFilter ifilter=new IntentFilter();
		ifilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//网络状态
		registerReceiver(receiver, ifilter);
	}
	@Override
	public void onTerminate() {
		super.onTerminate();
		unregisterReceiver(receiver);
	}
	
	/********************base***********************/
	public static Application getInstance(){
		return instance;
	}
	public static Context getContext(){
		return context;
	}
	public int getAppCounter(){
		return appLoadingnum;
	}
	/*************应用程序的自定义sdcard文件缓存处理*************/
	/**
	 * 获取自定义缓存目录
	 */
	public String getAppFilePath(){
		return path_cachefile;
	}
	/**
	 * 保存文件到自定义sdcard目录中
	 * @param io    文件输入流
	 * @param filename 文件名
	 * @return 文件路径
	 */
	public String saveFileToSdcard(Bitmap bm,String filename,int quantity){
		String path = null;
		if(path_cachefile!=null && path_cachefile!=""){
			File  f = new File(path_cachefile,filename);
	        try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
		        bm.compress(Bitmap.CompressFormat.JPEG, quantity, bos);
				bos.flush();
		        bos.close();
		        path = f.getAbsolutePath();
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}
		return path;
	}
}
