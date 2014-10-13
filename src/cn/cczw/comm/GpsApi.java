package cn.cczw.comm;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class GpsApi {
	private static GpsApi instance;
	private BDLocation mlocation;
	private LocationClient mLocationClient;
	public BDLocationListener myListener;
	String provider = null;
	//构造器
	GpsApi(Context context){
		if(instance==null){
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
		    mLocationClient.registerLocationListener( myListener );    //注册监听函数
			//实例
			instance = this;
		}
	}
	/**
	 * 获取单例，请确保调用前初始化
	 * @return
	 */
	public static GpsApi getInstance(){
		return instance;
	}
	/**
	 * 启动
	 */
	public void start(){
		mLocationClient.start();
	}
	/**
	 * 停止
	 */
	public void stop(){
		mLocationClient.stop();
	}
	/**
	 * 获取最新的地址信息
	 * @return
	 */
	public BDLocation getLocation(){
		return mlocation;
	}
	
	
	//监听类
	class myLocationListener implements BDLocationListener{
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null){
				mlocation = location;
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				sb.append(location.getTime());
				sb.append("\nerror code : ");
				sb.append(location.getLocType());
				sb.append("\nlatitude : ");
				sb.append(location.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(location.getLongitude());
				sb.append("\nCity : ");
				sb.append(location.getCity());
				sb.append("\nAddrStr : ");
				sb.append(location.getAddrStr());
				sb.append("\nradius : ");
				sb.append(location.getRadius());
				if (location.getLocType() == BDLocation.TypeGpsLocation){
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
				}
				Log.d("sjsb",sb.toString());
			}
		}
		
	}
}