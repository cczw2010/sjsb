package cn.cczw.comm;

import android.content.Context;
import android.os.Vibrator;

public class SensorApi {
	private static SensorApi instance;
	private Vibrator vibrator;
	//构造器
	SensorApi(Context context){
		if(instance==null){
			 /* 
	         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到 
	         * */  
	        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);  
	       
			instance = this;
		}
	}
	//获取单例，请确保调用前初始化
	public static SensorApi getInstance(){
		return instance;
	}
	
	/**
	 * 震动
	 * @param time 循环次数，-1表示不循环
	 */
	public void start(int time){
		long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启   
        vibrator.vibrate(pattern,time);           //重复两次上面的pattern 如果只想震动一次，index设为-1   
	}
	
	//取消震动
	public void stop(){
		vibrator.cancel();
	}
	
}