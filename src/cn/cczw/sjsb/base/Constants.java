package cn.cczw.sjsb.base;

public class Constants {
	
	final public static String TEXT_PROGRESS="加载中...";
	final public static String TEXT_TAG="cczw";
	//以下js部分
	final public static String  JS_SCHEME = "sjsb";
	final public static String  JS_BridgeName = "SJSB";
	public static String JS_CAMERA_JPG = null;	//相机照的临时文件路径
	final public static String JS_SNAPSHOT_JPG = "snapshot_js.jpg";//快照临时文件路径
	//js request_code部分
	final public static int JS_REQUEST_CODE_CAMERA = 930001;		//js相机原图 request_code
	final public static int JS_REQUEST_CODE_CAMERA_CAPTURE = 930002;//js相机缩略图 request_code
    final public static int JS_SCANNING_REQUEST_CODE = 930003;   	//js扫二维码 request_code
    // message部分
    final public static int MESSAGE_REFRESHDISABLE = 1000;   //下拉刷新是否可用
	//js回调函数
	public static String JS_CAMERA_CALLBACK = null;		//相机回调
	public static String JS_MENUBTN_CALLBACK = null;	//菜单键回调
	public static String JS_BACKBTN_CALLBACK = null;	//返回按钮回调
	public static String JS_SWIPEREFRESH_CALLBACK = null;	//下拉刷新时的回调

}
