package cn.cczw.sjsb.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * 所有公共的message消息都从这里处理，包括js消息
 * @author awen
 *
 */
public class BaseHandler  extends Handler{
	@Override
	public void handleMessage(Message msg) {
		//Log.d("sjsb-msg",msg.toString());
		switch (msg.what) {
			case Constants.MESSAGE_RUNJS://执行js
				
				break;
			default:
				break;
		}
		Bundle bundle = msg.getData();
		Log.d("sjsb-msg",bundle.getString("param"));
		//super.handleMessage(msg);

	}
}
