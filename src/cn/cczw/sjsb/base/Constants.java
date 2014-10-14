package cn.cczw.sjsb.base;

public class Constants {
	/*常量部分*/
	final public static String TEXT_PROGRESS="加载中...";
	final public static String TEXT_TAG="cczw";
	//页面加载错误时的html代码
	final public static String HTML_ERRPAGE = "<style>html,body{margin:0;padding0;background-color:#d5d5d5;}</style><div style='width:100%;height:100%;padding:100px 0;min-height:200px;text-align:center;color:#333;'><p><img src='images/icon.png'></p><p>页面不存在或者加载错误。请刷新重试！</p></div>";
	/*以下js部分*/
	final public static String  JS_SCHEME = "sjsb";
	final public static String  JS_BridgeName = "SJSB";

	/*js request_code部分*/
	final public static int JS_REQUEST_CODE_CAMERA = 930001;		//js相机原图 request_code
	final public static int JS_REQUEST_CODE_CAMERA_CAPTURE = 930002;//js相机缩略图 request_code
    final public static int JS_SCANNING_REQUEST_CODE = 930003;   	//js扫二维码 request_code
    /*message部分*/
    final public static int MESSAGE_REFRESHDISABLE = 1000;   //下拉刷新是否可用


}
