package cn.cczw.sjsb;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

public class swebview extends WebView{
	
	public swebview(Context context) {
		super(context, null);
	}

	public swebview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int ow, int oh) {
		//Log.d("cczw","resize::"+w+">"+h+">"+ow+">"+oh);
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, ow, oh);
	}
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		//Log.d("cczw","onWindowVisibilityChanged::"+visibility);
		super.onWindowVisibilityChanged(visibility);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		//Log.d("cczw","onScrollChanged::"+l+">"+t+">"+oldl+">"+oldt);
		super.onScrollChanged(l, t, oldl, oldt);
	}
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		//Log.d("cczw","onVisibilityChanged::"+visibility);
		super.onVisibilityChanged(changedView, visibility);
	}
}
