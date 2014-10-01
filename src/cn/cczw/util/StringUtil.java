package cn.cczw.util;

/**
 * by awen
 * */
public class StringUtil {
	
	/**首字母大写*/
	public static String capitalize(CharSequence s) {
		if (null == s) {
			return null;
		}
		int len = s.length();
		if (len == 0) {
			return "";
		}
		char char0 = s.charAt(0);
		if (Character.isUpperCase(char0)) {
			return s.toString();
		}
		StringBuilder sb = new StringBuilder(len);
		sb.append(Character.toUpperCase(char0)).append(s.subSequence(1, len));
		return sb.toString();
	}
}
