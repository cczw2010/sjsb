package cn.cczw.sjsb;


import android.content.Context;
import android.os.Vibrator;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class SensorApi {
	private static SensorApi instance;
	//震动
	private Vibrator vibrator;
	//location
	private BDLocation mlocation;
	private LocationClient mLocationClient;
	public BDLocationListener myListener;
	String provider = null;
	//构造器
	SensorApi(Context context){
		if(instance==null){
			 /**
	         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到 
	         */  
	        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);  
	        //////////////////////////gps
	        mLocationClient = new LocationClient(context);     //声明LocationClient类
		    LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
			//option.setCoorType();//返回的定位结果是百度经纬度，默认值gcj02
			option.setScanSpan(1000);//设置发起定位请求的间隔时间为1000ms
			option.setIsNeedAddress(true);//设置是否需要地址信息，默认为无地址
			option.setNeedDeviceDirect(true);//在网络定位时，是否需要设备方向
			//option.SetIgnoreCacheException(true);	//设置是否进行异常捕捉
			mLocationClient.setLocOption(option);
			myListener = new myLocationListener();
			mLocationClient.registerLocationListener( myListener );
			instance = this;
		}
	}
	//获取单例，请确保调用前初始化,之所以多此一举是为了，再其他地方调用这些api，不用再传参初始化
	public static SensorApi getInstance(){
		return instance;
	}
	
	/**
	 * 开始震动  
	 * @param continues 持续时间 毫秒
	 * @param betweens 间隔时间 毫秒
	 * @param times  震动循环次数，-1表示不循环 0 循环
	 */
	public void startVibrator(int continues, int betweens ,int times){
		long [] pattern = {betweens,continues};   // 停止 开启 停止 开启   
        vibrator.vibrate(pattern,times);           //重复两次上面的pattern 如果只想震动一次，index设为-1   
	}
	
	/**
	 * 取消震动
	 */
	public void stopVibrator(){
		vibrator.cancel();
	}
	/**
	 * 启动location
	 */
	public void startLocation(){
		mlocation = null;
		mLocationClient.start();
	}
	/**
	 * 停止location
	 */
	public void stopLocation(){
		mLocationClient.stop();
	}
	/**
	 * 获取当前获取地址程序状态
	 */
	public boolean isLoationStarted(){
		return mLocationClient.isStarted();
	}
	/**
	 * 获取最新的地址信息
	 * @return
	 */
	public BDLocation getLocation(){
		return mlocation;
	}
	//gps监听类
	class myLocationListener implements BDLocationListener{
		@Override
		public void onReceiveLocation(BDLocation location) {
			//Log.d("cczw",location.getLocType()+":"+location.getCity());
			if (location != null){
				//if(location.getLatitude()!=4.9e-324 && location.getLongitude()!=4.9e-324){
					mlocation = location;
					//StringBuffer sb = new StringBuffer(256);
					//sb.append("time : ");
					//sb.append(location.getTime());
					//sb.append("\nerror code : ");
					//sb.append(location.getLocType());
					//sb.append("\nlatitude : ");
					//sb.append(location.getLatitude());
					//sb.append("\nlontitude : ");
					//sb.append(location.getLongitude());
					//sb.append("\nCity : ");
					//sb.append(location.getCity());
					//sb.append("\nAddrStr : ");
					//sb.append(location.getAddrStr());
					//sb.append("\nradius : ");
					//sb.append(location.getRadius());
					//if (location.getLocType() == BDLocation.TypeGpsLocation){
					//	sb.append("\nspeed : ");
					//	sb.append(location.getSpeed());
					//	sb.append("\nsatellite : ");
					//	sb.append(location.getSatelliteNumber());
					//} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
					//	sb.append("\naddr : ");
					//	sb.append(location.getAddrStr());
					//}
					//Log.d("sjsb",sb.toString());
				//}
			}
		}
	}
}