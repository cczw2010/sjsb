package cn.cczw.sjsb.base;

import android.content.Context;
import android.os.Vibrator;

public class VibratorApi {
	private static VibratorApi instance;
	private Vibrator vibrator;
	//构造器
	VibratorApi(Context context){
		if(instance==null){
			/* 
	         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到 
	         * */  
	        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);  
			//实例
			instance = this;
		}
	}
	//获取单例，请确保调用前初始化
	public static VibratorApi getInstance(){
		return instance;
	}

	/**
	 * 开始震动  
	 * @param continues 持续时间 毫秒
	 * @param betweens 间隔时间 毫秒
	 * @param times  震动循环次数，-1表示不循环 0 循环
	 */
	public void start(int continues, int betweens ,int times){
		long [] pattern = {betweens,continues};   // 停止 开启 停止 开启   
        vibrator.vibrate(pattern,times);           //重复两次上面的pattern 如果只想震动一次，index设为-1   
	}
	
	/**
	 * 停止
	 */
	public void stop(){
		vibrator.cancel();
	}
}