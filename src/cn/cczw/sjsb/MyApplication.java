package cn.cczw.sjsb;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class MyApplication extends Application {
	//base
	private final String APPCOUNTER="_app_counter";
	private static MyApplication instance=null;
	private String path_cachefile = null;

	//phone status
	private AppReciver receiver=null;
	private int appLoadingnum;

	@Override
	public void onCreate() {
		super.onCreate();
		if(instance==null){
			instance=this;
			Context context=getApplicationContext();
			
			//初始化一些公用的单例api
			new CommonApi(context);
			new SensorApi(context);
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
			//receiver
			receiver=AppReciver.getInstance();
			IntentFilter ifilter=new IntentFilter();
			ifilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//网络状态
			registerReceiver(receiver, ifilter);
		}
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
	/**
	 * 获取应用启动次数
	 * @return
	 */
	public int getAppCounter(){
		return appLoadingnum;
	}
	/**
	 * 获取自定义缓存目录
	 */
	public String getAppFilePath(){
		return path_cachefile;
	}
}
