#SJSB android-javascript 通讯框架

本框架适用于hybird开发。为js提供各种原生支持api.

当前版本`v1.1`  email:`71752352@qq.com` author:`awen`

##-----------------使用方法：

####初始化
只需要再activity中调用一下方法,载入的网页自动注入SJSB对象，以支持所有api：
		
		/**
		* 初始化webview;
		*@param int webviewid 布局中xml中webview的id
		*@param String url  要引入的html网页（可以是assets中的本地文件也可以是网络的）
		*@param boolean hasSwipeRefresh  是否支持下拉刷新
		*/
		initWebView(int webviewid,String url,boolean hasSwipeRefresh)
		

####AndroidManifest.xml配置

 AndroidManifest.xml中的配置一直

 个人开发需要修改 AndroidManifest.xml中meta里百度sdk的AcessKey AcessKey请参考百度开发文档。
 

##------------------API

####基础部分

* 获取启动次数

		SJSB.getAppCounter();
		
* 获取应用版本号

		SJSB.getAppVersion();

* 打印android调试日志

		SJSB.log(tag,message);
		
* 设置当前页当菜单键按下时的js回调函数，如果为空则执行系统的原生操作

		/**
		*@param string callback  回调js函数名
		*/
		SJSB.setMenuBtnFn(callback);
		
* 设置当前页当返回键按下时的js回调函数，如果为空则执行系统的原生操作

		/**
		*@param string callback  回调js函数名
		*/
		SJSB.setBackBtnFn(callback);
		
* 设置当前页下拉刷新为不可用

		SJSB.disSwipeRefresh();
		
		
* 退出APP

		SJSB.exitApp();
		
* 显示loading

		SJSB.showProgress(showstr);

* 隐藏loading

		SJSB.disProgress();
		
* 获取webview高

		SJSB.getWebHeight();

* 获取webview宽

		SJSB.getWebWidth();

* 显示小tip(toast)

		/**
		* @param String str 提示信息
		* @param int pos 位置 	0居中(默认)  1底部中间，2顶部中间，3左边中间，4右边中间
		* @param int inteval 显示时长 0短(默认) 1长
		*/
		SJSB.toast(str,pos,inteval)

* 检查是否有网络连接

		SJSB.onLine();


* 检查是否有网络连接

		SJSB.onLine();	
		
* 获取网络类型
		
		/**
		* @return 0 无 ;1 wifi ; 2 gprs cmwap
		*/
		SJSB.netType();
		
* 获取设备信息，返回json字符串。其中uuid是根据手机一些设备信息自己生成

		SJSB.getDeviceInfo();

* 清空浏览器缓存

		sjsb.clearWebCache();
		
* 清空应用所有的缓存和数据(包括浏览器)

		SJSB.clearAppCache();

* 抓取当前网页截图
		
		/**
		* @param int quantity 质量 0~100
		*return String 本地文件路径（注意网页缓存）
		*/
		SJSB.getSnapshot(quantity);

####应用变量处理

 `这些变量与网页无关，将在整个应用中一直保存，不管退出与否.另外不建议在此处存储大数据`

* 设置应用级变量,不随应用退出而退出,但不建议大量使用
		
		SJSB.setAppProp(key,val)
		
*  获取应用级变量

		SJSB.getAppProp(key);
	
* 删除应用级变量

		SJSB.removeAppProp(key);

* 清空应用级变量

		SJSB.clearAppProp();

####相机部分

* js相机

		/**
		* @param int  type   返回原图还是缩略图   0  原图  1 缩略图
		* @param int save   0 不保存（其实所有的不保存图片公用一个文件缓存）  1保存（每次生成新的）
		* @param String callback  回调的js方法，传入一个本地文件地址，或者空（失败或者取消）作为参数
		*/
		SJSB.camera(type,save,callback);
		

* 二维码扫描解析

		/**
		*@param string callback  回调js函数名,传入解析结果作为参数
		*/
		SJSB.qrcodeCamera(callback)

####地理信息

* 获取地理信息
	
		/**
		* 获取地理信息，（同步阻塞,想异步请在js中处理）
		* return jsonstring (longitude精度latitude维度，accuracy精度。。省，市，区。。。)
	    */
	    SJSB.getLocation()

####感应器

* 开始震动

		/**
		* @param int continues 持续时间 毫秒
		* @param int betweens 间隔时间 毫秒
		* @param int times  震动循环次数，-1表示不循环 0 循环
		*/
		SJSB.vibrator(continues,betweens ,times);

* 停止震动 

		SJSB.stopVibrator();
