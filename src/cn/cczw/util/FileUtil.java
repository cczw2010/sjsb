package cn.cczw.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

/**
 * class FileUtil<BR>
 * class description：android文件的一些读取操作<BR>
 * @author awen
 */
public class FileUtil {
	/**读取sd中的文件*/
	public static String readSDCardFile(String path) throws IOException {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		String result = streamRead(fis);
		return result;
	}
	/**移动文件
	 * @throws IOException */
	public static void copyFileTo(File source,File dest) throws IOException{
		if(!dest.exists()){
			dest.createNewFile();
		}
		FileOutputStream fout=new FileOutputStream(dest);
		FileInputStream fin=new FileInputStream(source);
		byte[] buffer=new byte[fin.available()];
		fin.read(buffer);
		fout.write(buffer);
		fin.close();
		fout.close();
	}
	/**在res目录下建立一个raw资源文件夹，这里的文件只能读不能写入*/
	public static String readRawFile(Context context,int fileId) throws IOException {
		// 取得输入流
		InputStream is = context.getResources().openRawResource(fileId);
		String result = streamRead(is);// 返回一个字符串
		return result;
	}

	/**从InputStream读取字符串，utf-8*/
	private static String streamRead(InputStream is) throws IOException {
		int buffersize = is.available();// 取得输入流的字节长度
		byte buffer[] = new byte[buffersize];
		is.read(buffer);// 将数据读入数组
		is.close();// 读取完毕后要关闭流。
		String result = EncodingUtils.getString(buffer, "UTF-8");// 设置取得的数据编码，防止乱码
		return result;
	}

	/** 在assets文件夹下的文件，同样是只能读取不能写入*/
	public static String readAssetsFile(Context context,String filename) throws IOException {
		// 取得输入流
		InputStream is = context.getResources().getAssets().open(filename);
		String result = streamRead(is);// 返回一个字符串
		return result;
	}

	/** 往sd卡中写入文件*/
	public static void writeSDCardFile(String path, byte[] buffer) throws IOException {
		File file = new File(path);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(buffer);// 写入buffer数组。如果想写入一些简单的字符，可以将String.getBytes()再写入文件;
		fos.close();
	}

	/** 将文件写入应用的data/data的files目录下*/
	public static void writeDateFile(Context context,String fileName, byte[] buffer) throws Exception {
		byte[] buf = fileName.getBytes("iso8859-1");
		fileName = new String(buf, "utf-8");
		// Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
		// Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
		// Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
		// MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
		// 如果希望文件被其他应用读和写，可以传入：
		// openFileOutput("output.txt", Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_APPEND);// 添加在文件后面
		fos.write(buffer);
		fos.close();
	}

	/** 读取应用的data/data的files目录下文件数据*/
	public static String readDateFile(Context context,String fileName) throws Exception {
		FileInputStream fis = context.openFileInput(fileName);
		String result = streamRead(fis);// 返回一个字符串
		return result;
	}
	
	/**获取文件类型*/
	 public static String getMimeType(String fileUrl){
	      FileNameMap fileNameMap = URLConnection.getFileNameMap();
 	      String type = fileNameMap.getContentTypeFor(fileUrl); 
	      return type;  
	}
}
