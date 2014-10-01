package cn.cczw.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;

public class MobileUtil {
	/**
	 在使用Android连接网络的时候，并不是每次都能连接到网络，在这个时候，我们最好是在程序启动的时候对网络的状态进行一下判断，如果没有网络则进行即时提醒用户进行设置。
	要判断网络状态，首先需要有相应的权限，android.permission.ACCESS_NETWORK_STATE
	*/
	public boolean NetWorkStatus(final Context context) {  
	  
		boolean netSataus = false;  
        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		  
        cwjManager.getActiveNetworkInfo();  
		  
		if (cwjManager.getActiveNetworkInfo() != null) {  
            netSataus = cwjManager.getActiveNetworkInfo().isAvailable();  
        }  
		  
		if (netSataus) {  
            Builder b = new AlertDialog.Builder(context).setTitle("没有可用的网络")  
		                    .setMessage("是否对网络进行设置？");  
            b.setPositiveButton("是", new DialogInterface.OnClickListener() {  
												@Override
												public void onClick(DialogInterface dialog, int whichButton) {  
										            Intent mIntent = new Intent("/");  
										            ComponentName comp = new ComponentName(  
																			"com.android.settings",  
																			"com.android.settings.WirelessSettings");  
										            mIntent.setComponent(comp);  
										            mIntent.setAction("android.intent.action.VIEW");  
										            ((Activity) context).startActivityForResult(mIntent,0);  // 如果在设置完成后需要再次进行操作，可以重写操作代码，在这里不再重写  
										        }  
										        }).setNeutralButton("否", new DialogInterface.OnClickListener() {  
													@Override
													public void onClick(DialogInterface dialog, int whichButton) {  
											            dialog.cancel();  
											        }  
										        }).show();  
		}  
		return netSataus;  
	} 
	
	

}
