package cn.cczw.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import cn.cczw.util.FileUtil;

public  class  FileDialog{
	public static final int FILE_TEXT=1;
	public static final int FILE_IMAGE=2;
	public static final int FILE_VIDEO=4;
	public static final int FILE_AUDIO=8;
  	public static final String parentFolder="上一页";
	Activity activity=null;
	Dialog dialog=null;
	String basepath=null;
	String curpath=null;
	LisAdapter  adapter=null;
 	ArrayList<String> fls=null;
 	ArrayList<Integer> icols=null;
  	TextView tv_title=null;
 	TextView tv_path=null;
 	OnSelectedFileListener listener=null;
 	int mimefilter=0;
 	public int listitemlayout=android.R.layout.simple_list_item_1;
 	public int icon_folder=android.R.drawable.sym_contact_card;
 	public int icon_file=0;
 
 	
	public FileDialog(final Activity activity,String title){
 		this.activity=activity;
 		
 		basepath=Environment.getExternalStorageDirectory().getAbsolutePath();		
		adapter=new LisAdapter(activity,listitemlayout,fls);
		fls=new ArrayList<String>();
		icols=new ArrayList<Integer>();
   		dialog =new Dialog(activity,android.R.style.Theme);  //dialog全屏
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
 		/**根布局*/
		LayoutParams lyparams=new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		lyparams.gravity=Gravity.CENTER;
  		LinearLayout lyroot=new LinearLayout(activity);
		lyroot.setLayoutParams(lyparams);
  		lyroot.setOrientation(LinearLayout.VERTICAL);
		lyroot.setPadding(10, 3, 10, 3);
		
		/**top layout*/
  		LinearLayout lytop=new LinearLayout(activity);
  		lyparams.height=android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
  		lytop.setLayoutParams(lyparams);
  		lytop.setOrientation(LinearLayout.VERTICAL);
  		lytop.setBackgroundResource(android.R.drawable.title_bar);
  		lytop.setPadding(10, 2, 10, 2);
 		//标题
   		tv_title=new TextView(activity);
   		tv_title.setLayoutParams(lyparams);
   		tv_title.setMinimumHeight(30);
  		tv_title.setGravity(Gravity.CENTER);
  		tv_title.setText(title);
  		tv_title.setTextSize(18);
  		tv_title.setPadding(5, 5, 5, 5);
  		tv_title.setTextColor(Color.WHITE);
		//路径
		tv_path=new TextView(activity);
		tv_path.setLayoutParams(lyparams);
		tv_path.setMinimumHeight(20);
		tv_path.setGravity(Gravity.LEFT);
		tv_path.setBackgroundColor(Color.GRAY);
		tv_path.setTextSize(12);
		tv_path.setPadding(5, 5, 5, 5);
		tv_path.setTextColor(Color.WHITE);
		//listview
 		final ListView lv=new ListView(activity);
  		lv.setLayoutParams(lyparams);
		lv.setAdapter(adapter);
 		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adap, View view, int pos,
					long arg3) {
				if(pos==0&&(fls.get(pos).equals(parentFolder))){
					File f=new File(curpath);
					String pname=f.getParent();
					if(f!=null){
 						flashList(pname);
					}
				}else{
					String fname=curpath+File.separator+fls.get(pos);
					File f=new File(fname);
 					if(f.isDirectory()){
 						Log.d("cczw",">"+mimefilter);
 						flashList(fname);
  					}else{
						if(listener!=null){
							listener.onClick(curpath,fls.get(pos));
						}
					}
				}
			}
		});
 		lytop.addView(tv_title);
 		lytop.addView(tv_path);
		lyroot.addView(lytop);
 		lyroot.addView(lv);
		dialog.setContentView(lyroot);
 	}
	/**设置root路径*/
	public void setRoot(String path){
		basepath=path;
	}
	/**设置当文件点击后的回调*/
	public void setOnSelectedFileListener(OnSelectedFileListener listener){
		this.listener=listener;
	}
	/**设置显示的文件类型过滤器
	 * filetype  类中的静态常量，多个用|隔开
	 * */
	public void setFileTypeFilter(int filetype){
		mimefilter=filetype;
	}
	/**显示filedialog*/
	public void show(){
		if(dialog!=null&&!dialog.isShowing()){
			flashList(basepath);
			dialog.show();
		}
	}
	/**关闭filedialog*/
	public void hide(){
		if(dialog!=null&&dialog.isShowing()){
			dialog.dismiss();
		}
	}
	/**文件选择后的回调*/
	public interface OnSelectedFileListener{
		public void onClick(String path,String name);
	}
	
	private class LisAdapter extends ArrayAdapter<String>{
 		public LisAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
  		}
		@Override
		public int getCount() {
 			return fls.size();
		}
		@Override
		public String getItem(int position) {
 			return fls.get(position);
		}
		@Override
		public long getItemId(int position) {
 			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, 64);
			TextView tv = new TextView(activity);
			tv.setLayoutParams(layoutParams);
			tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
 			tv.setText(fls.get(position));
 			if(icols.get(position)!=0){
 	 			tv.setCompoundDrawablesWithIntrinsicBounds(icols.get(position), 0, 0, 0);
 			}else{
 				tv.setPadding(20, 0, 0, 0);
 			}
   			return tv;
  		}
		@Override
		public boolean isEmpty() {
 			return fls.isEmpty();
		}
	}
	
	private void flashList(String path){
		try{
			File f=new File(path);
			Log.d("cczw", path+":"+basepath+":"+curpath);
			if(f.canRead()){				
				fls.clear();
				icols.clear();
				if(!path.equals(basepath)){
					fls.add(parentFolder);
					icols.add(icon_folder);
				}
				String[] fs=f.list();
				for(int i=0,len=fs.length;i<len;i++){
					String fpath=path+File.separator+fs[i];
					File ftmp=new File(fpath);
					if(ftmp.isDirectory()){
						icols.add(icon_folder);
						fls.add(fs[i]);
 					}else if(ftmp.isFile()){
 						if(mimefilter>0){
							String mime=FileUtil.getMimeType(fpath);
	 						//Log.d("cczw",">"+mime+">"+mimefilter);
							if(mime!=null){
								if(mime.startsWith("text")){
									if((mimefilter&FILE_TEXT)==0){
	  									 continue;
									 }
								}else if(mime.startsWith("image")){
									if((mimefilter&FILE_IMAGE)==0){
										continue;
									}
								 }else if(mime.startsWith ("video")){
									 if((mimefilter&FILE_VIDEO)==0){
	 									 continue;
									 }
								 }else if(mime.startsWith("audio")){
									 if((mimefilter&FILE_AUDIO)==0){
										 continue;
									 }
								 }else{
									 continue;
								 }
							}else{
								continue;
							}
						}
						icols.add(icon_file);
						fls.add(fs[i]);
 					}
				}
				adapter.notifyDataSetChanged();
				tv_path.setText(path);
				curpath=path;
			}
 		}catch (Exception e) {
			e.printStackTrace();
		}
  	}

}