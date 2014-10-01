package cn.cczw.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Title: liunian
 * 
 * Description: liunian
 * 
 * Copyright: Copyright (c) 2011
 * 
 * Organization: God
 * 
 * @author liunian [xteamln@gmail.com]
 * @version 1.0
 */

public class LangUtil {

	private static boolean isIgnoredField(Field f) {
		if (Modifier.isStatic(f.getModifiers())) {
			return true;
		}
		if (Modifier.isFinal(f.getModifiers())) {
			return true;
		}
		if (f.getName().startsWith("this$")) {
			return true;
		}
		return false;
	}

	public static Class<?> getClass(String clazzName) throws Exception {
		// Class.forName(className, initializeBoolean, classLoader);
		return Class.forName(clazzName);
	}
	// 获得指定类的所有属性，包括私有属性但不包括Object的属性
	public static Field[] getFields(Class<?> clazz) {
		Class<?> theClass = clazz;
		Map<String, Field> map = new HashMap<String, Field>();
		while (null != theClass && !(theClass == Object.class)) {
			Field[] fs = theClass.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				if (isIgnoredField(fs[i])) {
					continue;
				}
				if (map.containsKey(fs[i].getName())) {
					continue;
				}
				map.put(fs[i].getName(), fs[i]);
			}
			theClass = theClass.getSuperclass();
		}
		return map.values().toArray(new Field[map.size()]);
	}

	// 根据字段名称，获取该类或者其父类的字段
	public static Field getField(Class<?> clazz, String name) {
		Class<?> theClass = clazz;
		Field f = null;
		while (null != theClass && !(theClass == Object.class)) {
			try {
				f = theClass.getDeclaredField(name);
				return f;
			} catch (NoSuchFieldException e) {
				theClass = theClass.getSuperclass();
			}
		}
		return f;
	}

	// 获得指定类的所有方法，包括私有方法但不包括Object的方法
	public static Method[] getMethods(Class<?> clazz) {
		Class<?> theClass = clazz;
		List<Method> list = new LinkedList<Method>();
		while (null != theClass && !(theClass == Object.class)) {
			Method[] ms = theClass.getDeclaredMethods();
			for (int i = 0; i < ms.length; i++) {
				list.add(ms[i]);
			}
			theClass = theClass.getSuperclass();
		}
		return list.toArray(new Method[list.size()]);
	}

	// 获得指定类的所有静态方法
	public static Method[] getStaticMethods(Class<?> clazz) {
		List<Method> list = new LinkedList<Method>();
		for (Method m : clazz.getMethods()) {
			if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
				list.add(m);
			}
		}
		return list.toArray(new Method[list.size()]);
	}

	// 根据名称获取一个getter
	public static Method getGetter(Class<?> clazz, String fieldName) {
		try {
			String fn = StringUtil.capitalize(fieldName);
			try {
				try {
					return clazz.getMethod("get" + fn);
				} catch (NoSuchMethodException e) {
					Method m = clazz.getMethod("is" + fn);
					if (!(m.getReturnType().equals(Boolean.TYPE))) {
						m = null;
					}
					return m;
				}
			} catch (NoSuchMethodException e) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 根据字段获取一个getter
	public static Method getGetter(Class<?> clazz, Field field) {
		try {
			try {
				String fn = StringUtil.capitalize(field.getName());
				if (field.getType().equals(Boolean.TYPE)) {
					return clazz.getMethod("is" + fn);
				} else {
					return clazz.getMethod("get" + fn);
				}
			} catch (NoSuchMethodException e) {
				return clazz.getMethod(field.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 根据名称获取一个setter
	public static Method getSetter(Class<?> clazz, String fieldName, Class<?> paramType) {
		try {
			String setterName = "set" + StringUtil.capitalize(fieldName);
			try {
				return clazz.getMethod(setterName, paramType);
			} catch (Exception e) {
				try {
					return clazz.getMethod(fieldName, paramType);
				} catch (Exception e1) {
					for (Method method : clazz.getMethods()) {
						if (method.getParameterTypes().length == 1) {
							if (method.getName().equals(setterName) || method.getName().equals(fieldName)) {
								if (null == paramType || canCastToDirectly(paramType, method.getParameterTypes()[0])) {
									return method;
								}
							}
						}
					}
					throw new Exception();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 根据字段获取一个setter
	public static Method getSetter(Class<?> clazz, Field field) throws NoSuchMethodException {
		try {
			try {
				return clazz.getMethod("set" + StringUtil.capitalize(field.getName()), field.getType());
			} catch (Exception e) {
				try {
					if (field.getName().startsWith("is") && field.getType().equals(Boolean.TYPE)) {
						return clazz.getMethod("set" + field.getName().substring(2), field.getType());
					}
					return clazz.getMethod(field.getName(), field.getType());
				} catch (Exception e1) {
					return clazz.getMethod(field.getName(), field.getType());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 为对象的一个字段设置值，不调用setter
	public static void setValue(Object obj, Field field, Object value) {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		Class<?> ft = field.getType();
		if (null == value) {
			if (isNumber(ft)) {
				value = 0;
			} else if (isChar(ft)) {
				value = (char) 0;
			}
		}
		try {
			field.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 为对象的一个字段设置值
	public static void setValue(Object obj, String fieldName, Object value) {
		try {
			getSetter(obj.getClass(), fieldName, value.getClass()).invoke(obj, value);
		} catch (Exception e) {
			try {
				Field field = obj.getClass().getField(fieldName);
				setValue(obj, field, value);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	// 获得字段的值,不调用getter
	public static Object getValue(Object obj, Field f) {
		try {
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}
			return f.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// //获得字段的值
	public static Object getValue(Object obj, String name) {
		try {
			return getGetter(obj.getClass(), name).invoke(obj);
		} catch (Exception e) {
			try {
				Field f = getField(obj.getClass(), name);
				return getValue(obj, f);
			} catch (Exception ex) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Map<?, ?> objectToMap(Object obj) {
		return objectToMap(obj, null);
	}

	public static Map<String, Object> objectToMap(Object obj, Field[] flds) {
		if (null == obj) {
			return null;
		}
		if (null == flds) {
			flds = getFields(obj.getClass());
		}
		Map<String, Object> row = new HashMap<String, Object>();
		for (Field fld : flds) {
			Object v = getValue(obj, fld);
			row.put(fld.getName(), v);
		}
		return row;
	}

	public static boolean canCastToDirectly(Class<?> clazz, Class<?> type) {
		if (clazz == type) {
			return true;
		}
		if (type.isAssignableFrom(clazz)) {
			return true;
		}
		if (clazz.isPrimitive() && type.isPrimitive()) {
			return true;
		}
		try {
			return getWrapperClass(type) == getWrapperClass(clazz);
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean is(Class<?> clazz, Class<?> type) {
		if (null == type) {
			return false;
		}
		if (clazz == type) {
			return true;
		}
		return false;
	}

	public static boolean is(Class<?> clazz, String className) {
		return clazz.getName().equals(className);
	}

	public static boolean isOf(Class<?> clazz, Class<?> type) {
		return type.isAssignableFrom(clazz);
	}

	public static boolean isString(Class<?> clazz) {
		return is(clazz, String.class);
	}

	public static boolean isStringLike(Class<?> clazz) {
		return CharSequence.class.isAssignableFrom(clazz);
	}

	public static boolean isChar(Class<?> clazz) {
		return is(clazz, char.class) || is(clazz, Character.class);
	}

	public static boolean isEnum(Class<?> clazz) {
		return clazz.isEnum();
	}

	public static boolean isBoolean(Class<?> clazz) {
		return is(clazz, boolean.class) || is(clazz, Boolean.class);
	}

	public static boolean isFloat(Class<?> clazz) {
		return is(clazz, float.class) || is(clazz, Float.class);
	}

	public static boolean isDouble(Class<?> clazz) {
		return is(clazz, double.class) || is(clazz, Double.class);
	}

	public static boolean isInt(Class<?> clazz) {
		return is(clazz, int.class) || is(clazz, Integer.class);
	}

	public static boolean isIntLike(Class<?> clazz) {
		return isInt(clazz) || isLong(clazz) || isShort(clazz) || isByte(clazz) || is(clazz, BigDecimal.class);
	}

	public static boolean isDecimal(Class<?> clazz) {
		return isFloat(clazz) || isDouble(clazz);
	}

	public static boolean isLong(Class<?> clazz) {
		return is(clazz, long.class) || is(clazz, Long.class);
	}

	public static boolean isShort(Class<?> clazz) {
		return is(clazz, short.class) || is(clazz, Short.class);
	}

	public static boolean isByte(Class<?> clazz) {
		return is(clazz, byte.class) || is(clazz, Byte.class);
	}

	public static boolean isPrimitiveNumber(Class<?> clazz) {
		return isInt(clazz) || isLong(clazz) || isFloat(clazz) || isDouble(clazz) || isByte(clazz) || isShort(clazz);
	}

	public static boolean isNumber(Class<?> clazz) {
		return Number.class.isAssignableFrom(clazz) || clazz.isPrimitive() && !is(clazz, boolean.class)
				&& !is(clazz, char.class);
	}

	public static boolean isWrpperOf(Class<?> clazz, Class<?> type) {
		try {
			return getWrapperClass(type) == clazz;
		} catch (Exception e) {
		}
		return false;
	}

	public static Class<?> getWrapperClass(Class<?> clazz) {
		if (!clazz.isPrimitive()) {
			if (isPrimitiveNumber(clazz) || is(clazz, Boolean.class) || is(clazz, Character.class)) {
				return clazz;
			}
		}
		if (is(clazz, int.class)) {
			return Integer.class;
		}
		if (is(clazz, char.class)) {
			return Character.class;
		}
		if (is(clazz, boolean.class)) {
			return Boolean.class;
		}
		if (is(clazz, long.class)) {
			return Long.class;
		}
		if (is(clazz, float.class)) {
			return Float.class;
		}
		if (is(clazz, byte.class)) {
			return Byte.class;
		}
		if (is(clazz, short.class)) {
			return Short.class;
		}
		if (is(clazz, double.class)) {
			return Double.class;
		}
		return null;
	}

}
