package cn.cczw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import cn.cczw.util.http.FilePart;
import cn.cczw.util.http.MultipartEntity;
import cn.cczw.util.http.Part;
import cn.cczw.util.http.StringPart;

/**
 * http 操作类
 * by awen
 * email:awen1983@live.cn
 * */
public class HttpUtil {
	public static final String TAG="cczw_http";
	
	public static final int CONNECT_TIMEOUT=2000;
	public static final int SO_TIMEOUT=10000;
	public static final String CHARSET=HTTP.UTF_8;
	public static final String USER_AGENT="Android-HttpClient-cczw";

	public static final String CMWAP_HOST = "10.0.0.172";
	public static final int CMWAP_PORT = 80;
	
	private static String CACHEPATH=null;

	private static FileInputStream is;

	private static FileOutputStream fout; 
	/**
	 * 说明:根据网络类型获取httpclient对象
	 * */
	private static DefaultHttpClient getHttpClient(){
		BasicHttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, CHARSET);
		HttpProtocolParams.setUserAgent(params, USER_AGENT);

 		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http",PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		DefaultHttpClient httpclient = new DefaultHttpClient(conMgr, params);
		
 		//判断是否wap
		//MyApplication app=(MyApplication) MyApplication.getInstance();
		//if(app.getNetType()==MyApplication.STATUS_NET_CMWAP){
			//HttpHost proxy = new HttpHost(CMWAP_HOST, CMWAP_PORT, "http");
			//httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		//}
		return httpclient;
	}
	/**
	 * get请求
	 * 返回HttpEntity 对象或者null
	 * @throws Exception
	 * */
	public static HttpEntity get(String url,Map<String, String> params){
		HttpClient httpclient=getHttpClient();
		StringBuffer bufer=new StringBuffer(url);
		if(params!=null && params.size() > 0){
			if(url.indexOf("?")==-1){bufer.append("?");}
			for (Map.Entry<String, String> kv : params.entrySet()) {
				bufer.append("&"+kv.getKey()+"="+kv.getValue());
			}
		}
		Log.d(TAG, "http_get：url="+bufer.toString());
		HttpGet httpget=new HttpGet(new String(bufer));
		HttpResponse httpResponse=null;
		try {
			httpResponse = httpclient.execute(httpget);
		} catch (Exception e) {
 			e.printStackTrace();
			return null;
		}
		HttpEntity entity=httpResponse.getEntity();
		//httpclient.getConnectionManager().shutdown();
		//log header
		//logheader(httpResponse);
		return entity;
	}
	/**
	 * @param url 		请求的地址
	 * @param params 	请求的键值对
 	 *@return 返回inputStream或者null
	 * */
	public static InputStream getInputStream(String url,Map<String, String> params){
		HttpEntity entity=get(url,params);
		if(entity==null){
			return null;
		}
		try {
			return entity.getContent();
		} catch (Exception e) {
 			e.printStackTrace();
 			return null;
		} 
	}
	/**
	 * @param url 		请求的地址
	 * @param params 	请求的键值对
	 * @param charset	字符串的编码类型，null默认为UTF-8(HTTP.UTF_8)
	 *@return 返回字符串或者null
	 * */
	public static String getString(String url,Map<String, String> params,String charset) {
		HttpEntity entity=get(url,params);
		if(entity==null){
			return null;
		}
		if(charset==null||("").equals(charset)){
			charset=CHARSET;
		}
		try {
			return EntityUtils.toString(entity,charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	/**
	 * @param url 		请求的地址
	 * @param params 	请求的键值对
	 * @param charset	字符串的编码类型，null默认为UTF-8(HTTP.UTF_8)
	 *@return 返回JSONObject对象或者null
	 * */
	public static JSONObject getJSON(String url,Map<String, String> params,String charset){
		String json=getString(url,params, charset);
		if(json==null){
			return null;
		}
 		try {
			return new JSONObject(json);
		} catch (JSONException e) {
 			e.printStackTrace();
 			return null;
		}
	}
	/**
	 * 获取图片资源
	 * @param url 		请求的地址
	 * @return 返回Bitmap对象
	 * */
	public static Bitmap getbitmap(String url){
		InputStream is=getInputStream(url,null);
		return is==null?null:BitmapFactory.decodeStream(is);
 	}
	/**
	 * post
	 * @param params  字符串参数list
	 * @param files	  上传的文件list
	 * @param charset 数据的编码方式,null默认为UTF-8(HTTP.UTF_8)
	 * @return 返回相应字符串
	 * @throws Exception 
	 * */
	public static String post(String url,Map<String,String>params,Map<String,File>files,String charset){
		try {
		 //取得默认的HttpClient  
		 HttpClient httpclient = new DefaultHttpClient();
		//HttpPost连接对象
		 HttpPost httpRequest = new HttpPost(url);
		//使用StringPart来保存要传递的Post的文字参数 
		 List<Part> partList = new ArrayList<Part>();
		 if(params!=null){
			 for (Map.Entry<String, String> kv : params.entrySet()) {
				 partList.add(new StringPart(kv.getKey(),kv.getValue()));
			 }
		 }
		//使用FilePart来保存要传递的Post的文件参数 
		 if(files!=null){
			 for (Map.Entry<String, File> kv : files.entrySet()) {
				if(kv.getValue().exists()&&kv.getValue().isFile()){
					partList.add(new FilePart(kv.getKey(),kv.getValue()));
				}
			 }
		 }
		 Part[] parts = partList.toArray(new Part[0]);
		 httpRequest.setEntity(new MultipartEntity(parts));
		 //取得HttpResponse  
         HttpResponse httpResponse = httpclient.execute(httpRequest);
		
         //log header
 		 //logheader(httpResponse);
         //HttpStatus.SC_OK表示连接成功  
         if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)  
         {  
        	 if(charset==null||("").equals(charset)){
     			charset=CHARSET;
     		 }
             return EntityUtils.toString(httpResponse.getEntity(), charset);  
         }  
		} catch (Exception e) {
 			e.printStackTrace();
 		}
		return null;
	}
	
	
	/**
	 * 设置缓存目录
	 * @param cachepath  缓存目录,结尾不用/
	 * */
	public static void setCachePath(String cachepath){
		File cachefile=new File(cachepath);
		if(cachefile.exists()){
			CACHEPATH=cachepath;
		}
	}
	
	/**
	 * 缓存获取。注意!!!！使用该函数之前请确认调用过setCachePath()方法设置缓存目录，否则程序将总是从网络获取
	 * @param url 网络地址
	 * @param cachename 缓存文件名称
	 * @param cachetype 缓存类型 0:总是从网络获取，1总是从缓存获取，2有缓存就从缓存获取，没缓存就从网络获取，
	 * @param timeout 	过期时间(分钟) ,0不过期，
	 * */
	public static String getCache(String url,String cachename,int cachetype,long timeout){
		//检查是否存在缓存目录
		if(cachetype!=0){
			if(CACHEPATH==null){
				cachetype=0;
				Log.d(TAG,">没有设定缓存目录！！！");
			}
		}
		String ret=null;
		if(cachetype==0){
 			ret=getString(url, null, null);
		}else{
			File f=new File(CACHEPATH+File.separator+cachename);
			if(f.isFile()){
				Log.d(TAG,">读取缓存文件>地址>"+CACHEPATH+File.separator+cachename+">创建时间:"+f.lastModified()+">当前时间:"+System.currentTimeMillis()+">过期时间:"+timeout*60*1000);

				if(!(timeout>0&&(System.currentTimeMillis()-f.lastModified()>timeout*60*1000))){
					try {
						is = new FileInputStream(f);
						byte[] buffer=new byte[is.available()];
						is.read(buffer);
						ret=new String(buffer);
					} catch (FileNotFoundException e) {
	 					e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if(ret==null&&cachetype==2){
				ret=getString(url, null, null);
				if(ret!=null){
					try {
						fout = new FileOutputStream(f);
						fout.write(ret.getBytes());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
	 					e.printStackTrace();
					}
				}
			}
		}
 
		return ret;
	}
	/******************private common*****************/
//	private static void logheader(HttpResponse rsp){
//		Header[] headers=rsp.getAllHeaders();
//		StringBuffer buffer=new StringBuffer(">>>>>>>输出 header信息>>>>");
//		for(int i=0;i<headers.length;i++){
//			buffer.append("\n>>"+headers[i].getName()+"="+headers[i].getValue());
//		}
//		HttpEntity entity=rsp.getEntity();
//		buffer.append("\n>>Content-Encoding="+entity.getContentEncoding());
//		Log.d(TAG, new String(buffer));
//	}
}
