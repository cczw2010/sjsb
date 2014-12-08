package cn.cczw.sjsb.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

public class AppReciver extends  BroadcastReceiver{
	final public static int  STATUS_NET_NONE=0;
	final public static int  STATUS_NET_WIFI=1;
	final public static int  STATUS_NET_GPRS=2;
	final public static int  STATUS_NET_CMWAP=3;
	//唯一实例
	private static AppReciver instance = null;
	private boolean hasNetwork=false;
	private int netType;
	//获取唯一实例
	public static AppReciver getInstance(){
		return instance;
	}
	//获取网络状态
	public boolean hasNetWork(){
		return this.hasNetwork;
	}
	//获取网络类型
	public int getNetType(){
		return this.netType;
	}
	//构造函数
	public AppReciver(){
		if(instance==null){
			instance = this;
		}
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("cczw", "ACTION=="+intent.getAction());
		if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
			checkNetStatus();
		}
	}
	/**
	 *
	 */
	/*******想访问网络状态，首先得添加权限ACCESS_NETWORK_STATE*******<br>
	Android 使用cmwap GPRS 方式联网
	CMWAP和CMNET只是中国移动为其划分的两个GPRS接入方式。中国移动对CMWAP作了一定的限制，主要表现在CMWAP接入时只能访问 GPRS网络内的IP（10.*.*.*），
	而无法通过路由访问Internet，我们用CMWAP浏览Internet上的网页 就是通过WAP网关协议或它提供的HTTP代理服务实现的。
	一句话：CMWAP是移动限制的，理论上只能上WAP网，而CMNET可以用GPRS浏览WWW
	 * */
	private void checkNetStatus(){
		// 跳转到无线网络设置界面  
		//startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));  
		// 跳转到无限wifi网络设置界面  
		// startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));  
		ConnectivityManager connManager = (ConnectivityManager)MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);  
		// 获取代表联网状态的NetWorkInfo对象  
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if(networkInfo==null||!networkInfo.isAvailable()){
			hasNetwork=false;
			netType = STATUS_NET_NONE;
		}else{
			hasNetwork=true;
			State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
			if(State.CONNECTED==state){  
				if("cmwap".equals(networkInfo.getExtraInfo())){
					netType=STATUS_NET_CMWAP;
				}else{
					netType=STATUS_NET_GPRS; 
				}
			}  
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
			if(State.CONNECTED==state){  
				netType=STATUS_NET_WIFI;
			}
		}
		//Log.d("cczw", "net changed>>"+netType+":"+hasNetwork);
	}
}
