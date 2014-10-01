package cn.cczw.util;
/**
 * @author awen
 * */
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteUtil {
    public static final String WEBVIEWCACHEDB="webviewCache.db";

    private static final String TAG = "SqliteUtil";
 	private SQLiteOpenHelper helper=null;
 	private SQLiteDatabase db=null;

	private String tableName=null;
	private String primaryKey=null;
	private ArrayList<String> colums=null; //（key）键的名称 
	private Class<?> adpater=null;
	private Field[] fields=null;

	/***/
	public SqliteUtil(Context context,String dbname){
		helper=new SQLiteOpenHelper(context, dbname, null, 1) {
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				//当打开数据库时传入的版本号与当前的版本号不同时会调用该方法。
			}
			@Override
			public void onCreate(SQLiteDatabase db) {
				//当数据库被首次创建时执行该方法，一般将创建表等初始化操作在该方法中执行。
			}
		};
		 //SQLiteDatabase对象
		db=helper.getWritableDatabase();
		colums=new ArrayList<String>();
	}
	/**关闭数据库操作*/
	public void close(){
		db.close();
		helper.close();
	}
	/**返回数据库对象，可以自主的使用一些原生方法*/
	public SQLiteDatabase getSqliteDB(){
		return db;
	}
	/**查看数据库中是否存在某表*/
	public boolean hasTable(String table_name)
	{
		boolean result = false;
		Cursor cur = null;
		try {
			String sql_table = "select count(*) as c from Sqlite_master	where type='table' and name ='"	+ table_name.trim() + "'";
			cur = db.rawQuery(sql_table, null);
			if (cur.moveToNext()) {
				int count = cur.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
			cur.close();
		} catch (Exception e) {
			return result;
		}
		return result;
	}
	/**设定table的名称，和其适配的javabean,如果该表不存在将根据这两个参数建立该表
	 * @param tableName 数据表名称
	 * @param adpater   该数据表对应的javabean  注意，避免使用基本数据类型和数组
	 * @param PRIMARY	主键 ，没有则为null，该值只有创建表的时候管用
	 * */
	public void setTables(String tableName,Class<?> adpater,String primaryKey){
		this.tableName=tableName;
		this.adpater=adpater;
		this.primaryKey=primaryKey;
		this.fields=LangUtil.getFields(adpater);
		if(!hasTable(tableName)){
			StringBuffer createSql = new StringBuffer("CREATE TABLE " + tableName + "(");
			for(int i=0,len=fields.length;i<len;i++){
				Field f=fields[i];
				String fieldName=f.getName();
				String fieldType=getSqliteTypeClass(f.getType()).getSimpleName();
				colums.add(fieldName);
				createSql.append(" "+fieldName+" "+fieldType+" ");
				if(fieldName.equals(primaryKey)){
					createSql.append(" PRIMARY KEY  AUTOINCREMENT  NOT NULL ");
				}
				if(i<len-1){createSql.append(",");}
 			}
			createSql.append(")");
			Log.d(TAG, "创建表："+createSql.toString());
			db.execSQL(createSql.toString());
		}else{
			for(int i=0,len=fields.length;i<len;i++){
				colums.add(fields[i].getName());
			}
		}
 	}
	/**删除数据库中的表*/
	public void delTable(String tablename){
		if(hasTable(tablename)){
			db.execSQL("DROP TABLE "+tablename);
		}
	}
	/**删除表中的所有数据*/
	public void clearTable(String tablename){
		if(hasTable(tablename)){
			db.execSQL("delete from "+tablename);
		}
	}
	/**通用查询接口,无返回值*/
	public void query(String sql){
		db.execSQL(sql);
	}
	/**增加记录,并返回增加记录的索引,否则返回-1
	 * @param  bean table表对应的bean
	 * @return long 插入字段的索引
	 * */
	public long insertRow(Object bean){
		Long ret=-1L;
		if(bean!=null&&bean.getClass()==adpater){
			 ContentValues  cv=BeanAdpater(bean,false);
			 ret=db.insert(this.tableName, this.primaryKey, cv);
		}else{
			Log.d(TAG,"参数为空或者类型错误");
		}
		return ret;
	}
	/**根据查询条件返回数据表中的记录数组arrylist
	 * @param <E>
	 * @param condition 查询的条件语句,将补在全部查询sql语句之后
	 * */
	@SuppressWarnings("unchecked")
	public <E> List<E> getRows(String condition){
		List<E> rows=new ArrayList<E>();
		String sql="select * from "+this.tableName;
		if(!("").equals(condition)&&condition!=null){
			sql+=" "+condition;
		}
		Cursor cursor=db.rawQuery(sql, null);
		Log.d(TAG, "select查询：数据总行数："+cursor.getCount()+"；列数:"+cursor.getColumnNames().length);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			try {
				Object bean=getRow(cursor);
				rows.add((E) bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
			cursor.moveToNext();
		}
		cursor.close();
		return rows;
	}
	
	/**修改指定的bean记录,依据本类的bean类设置的主键的值，所以主键对应的成员变量的值必须存在*/
	public void updateRow(Object bean){
		if(bean!=null&&bean.getClass()==adpater){
			 ContentValues  cv=BeanAdpater(bean,true);
			 db.update(this.tableName, cv, this.primaryKey+"=?", new String[]{cv.getAsString(primaryKey)});
		}else{
			Log.d(TAG,"参数为空或者类型错误");
		}
	}
	/**删除指定的bean记录，依据本类的bean类设置的主键的值，所以主键对应的成员变量的值必须存在*/
	public void deleteRow(Object bean){
		if(bean!=null&&bean.getClass()==adpater){
			 ContentValues  cv=BeanAdpater(bean,true);
			 db.delete(this.tableName, this.primaryKey+"=?", new String[]{cv.getAsString(primaryKey)});
		}else{
			Log.d(TAG,"参数为空或者类型错误");
		}
	}
	/**bean 转换为 ContentValues
	 * @param covertPrimaryKey  返回的ContentValues中是否包含主键
	 * */
	private ContentValues BeanAdpater(Object bean,boolean covertPrimaryKey){
		ContentValues cv=new ContentValues();
		Field[] fields=LangUtil.getFields(bean.getClass());
		for(int i=0,len=fields.length;i<len;i++){
			Field f=fields[i];
			String fieldName=f.getName();
			if(fieldName.equals(this.primaryKey)&&!covertPrimaryKey){
				continue;
			}
            Method getMethod = null;
			try {
				getMethod =LangUtil.getGetter(bean.getClass(), fieldName);
				String returntype=getSqliteTypeClass(f.getType()).getSimpleName().toLowerCase();
				Object val=getMethod.invoke(bean);
				//Log.d(TAG,returntype+":"+fieldName+":"+val);
				if(val==null){continue;}
				if(returntype.equals("string")){
					cv.put(fieldName,(String)val);
				}else if(returntype.equals("character")){
					cv.put(fieldName,val.toString());
				}else if(returntype.equals("boolean")){
					cv.put(fieldName,(Boolean)val);
				}else if(returntype.equals("integer")){
					cv.put(fieldName,(Integer)val);
				}else if(returntype.equals("byte")){
					cv.put(fieldName,(Byte)val);
				}else if(returntype.equals("short")){
					cv.put(fieldName,(Short)val);
				}else if(returntype.equals("double")){
					cv.put(fieldName,(Double)val);
				}else if(returntype.equals("float")){
					cv.put(fieldName,(Float)val);
				}else if(returntype.equals("long")){
					cv.put(fieldName,(Long)val);
				}else{
					cv.putNull(fieldName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cv;
	}
	/**获取当前指针所在位置的一行数据
	 * @throws InstantiationException 
	 * @throws IllegalAccessException */
	private Object getRow(Cursor cursor) throws IllegalAccessException, InstantiationException{
		Object bean=adpater.newInstance();
		if(!cursor.isAfterLast()&&!cursor.isBeforeFirst()&&!cursor.isClosed()){
			for(int i=0,len=cursor.getColumnCount();i<len;i++){
				String fieldName= cursor.getColumnName(i);
				String returntype=getSqliteTypeClass(fields[i].getType()).getSimpleName().toLowerCase();
				//Log.d(TAG, fieldName+"="+cursor.getString(i));
				String val=cursor.getString(i);
				if(val==null){continue;}
				Object oval=null;
				if(returntype.equals("string")){
					oval=val;
				}else if(returntype.equals("character")){
					oval=(val.charAt(0));
				}else if(returntype.equals("boolean")){
					oval=val.equals("1")?true:false;
				}else if(returntype.equals("integer")){
					oval=Integer.parseInt(val);
				}else if(returntype.equals("byte")){
					oval=Byte.parseByte(val);
				}else if(returntype.equals("short")){
					oval=Short.parseShort(val);
				}else if(returntype.equals("double")){
					oval=Double.parseDouble(val);
				}else if(returntype.equals("float")){
					oval=Float.parseFloat(val);
				}else if(returntype.equals("long")){
					oval=Long.parseLong(val);
				}
				LangUtil.setValue(bean, fieldName, oval);
			}
		}
		return bean;
	}
	/**获取传入类型的非基本数据类型表示方式*/
	private Class<?> getSqliteTypeClass(Class<?> classz){
		return classz.isPrimitive()?LangUtil.getWrapperClass(classz):classz;
	}
}
