package cn.cczw.comm;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GpsApi {
	private static GpsApi instance;
	private LocationManager lm;
	private String bestProvider;
	private Criteria criteria;
	private Location location;
	//构造器
	GpsApi(Context context){
		if(instance==null){
			// 获取LocationManager对象
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	
			// 定义Criteria对象
			criteria = new Criteria();
			// 设置定位精确度 Criteria.ACCURACY_COARSE 比较粗略， Criteria.ACCURACY_FINE则比较精细
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			// 设置是否需要海拔信息 Altitude
			criteria.setAltitudeRequired(true);
			// 设置是否需要方位信息 Bearing
			criteria.setBearingRequired(true);
			// 设置是否允许运营商收费
			criteria.setCostAllowed(true);
			// 设置对电源的需求
			criteria.setPowerRequirement(Criteria.POWER_LOW);
	
			// 获取GPS信息提供者
			bestProvider = lm.getBestProvider(criteria, true);
			//Log.i("cczw-gps", "bestProvider = " + bestProvider);
	
			//位置变化监听器
			//locationListener =;
			//lm.requestLocationUpdates(bestProvider, 500, (float) 0.1, locationListener);
			//实例
			instance = this;
		}
	}
	/**
	 *  注册gps实时更新监听
	 * @param mintime  更新间隔时间 毫秒
	 * @param minDistance 更新最小变动距离
	 * @param listener 监听器 可为空
	 */
	public void registerListener(int minTime,float minDistance,LocationListener listener){
		if(listener == null){
			listener = new LocationListener() {
				// 当位置改变时触发
				@Override
				public void onLocationChanged(Location loc) {
					if (location != null) {
						Log.d("cczw","定位对象信息如下：" + location.toString() + "\n\t其中经度：" + location.getLongitude() + "\n\t其中纬度："
								+ location.getLatitude());
					} else {
						Log.i("cczw", "没有获取到定位对象Location");
					}
				}
				// Provider失效时触发
				@Override
				public void onProviderDisabled(String arg0) {
					Log.i("cczw", "Provider失效:"+arg0);
				}
				// Provider可用时触发
				@Override
				public void onProviderEnabled(String arg0) {
					Log.i("cczw", "Provider可用:"+arg0);
				}
				// Provider状态改变时触发
				@Override
				public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
					Log.i("cczw", "Provider状态改变:"+arg0+" : "+arg1);
				}
			};
		}
		// 实时监听
		lm.requestLocationUpdates(bestProvider, minTime, minDistance,listener);
	}

	
	//获取单例，请确保调用前初始化
	public static GpsApi getInstance(){
		return instance;
	}
	//获取最新的地址信息
	public Location getLastLocation(){
		return bestProvider==null?null:lm.getLastKnownLocation(bestProvider);
	}
}