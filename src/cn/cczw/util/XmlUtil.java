package cn.cczw.util;
/**for limit android sdk:2.2*/
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;
/**
 * @author awen
 * xml的document操作,不适用于大数据量的xml操作
 * */
public class XmlUtil{
	DocumentBuilder builder;
	Document doc;

	public XmlUtil(){			
		try {
			builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	public void initNew(){
		this.doc=builder.newDocument();
		this.doc.setXmlStandalone(true);
	}
	public void initFromPath(String filepath){
		File file=new File(filepath);
		try {
			doc=builder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void initFromInputStream(InputStream is){
		try {
			doc=builder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void initFromXmlString(String xml){
		try {
			ByteArrayInputStream is=new ByteArrayInputStream(xml.getBytes()); 
			doc=builder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**转换javabean为xml文档
	 * @param  bean 要转换的java bean ,仅包括基本数据类型或者String或者自定义类，
	 * @param  root 指定根节点，如果为null则不创建根节点
	 * */
	public void initFromBean(Object bean,String root){
		initNew();
		doc.appendChild(transferFromBean(bean,root));
	}
	
	
	public Document getDoc(){
		return doc;
	}
	public void setDoc(Document doc){
		this.doc=doc;
	}
	/**根据querySelector来检索节点，并返回所有检索到符合要求的节点值, "/"代表从根节点开始检索<br>
	 * example: getNode("/info/name");
	 * */
	public ArrayList<String> getValues(String querySelector){
		ArrayList<String> list=new ArrayList<String>();
		ArrayList<Node> nlist= getNodes(querySelector);
		//获取值
		for(int j=0,l=nlist.size();j<l;j++){
			switch(nlist.get(j).getFirstChild().getNodeType()){
			case 4:
			case 3:
				list.add(nlist.get(j).getTextContent());
				break;
			}
		}
		//Log.d("cczw", ">结果="+list.size()+">"+Arrays.toString(list.toArray()));
		return list;
	}
	
	/**根据querySelector来检索节点，并返回第一个检索到符合要求的节点值, "/"代表从根节点开始检索<br>
	 * example: getNode("/info/name");
	 * */
	public String getValue(String querySelector){
		String val=null;
		ArrayList<String> ls =getValues(querySelector);
		if(ls.size()>0){
			val=ls.get(0);
		}
		return val;
	}
	public String getNodeValue(Node node){
		if(node.getFirstChild()!=null){
			switch(node.getFirstChild().getNodeType()){
			case 4:
			case 3:
				return node.getTextContent();
			}
		}
		return null;
	}
	/**根据querySelector来检索节点，并返回所有检索到符合要求的节点, "/"代表从根节点开始检索<br>
	 * example: getNode("/info/name");
	 * */
	public ArrayList<Node> getNodes(String querySelector){
		ArrayList<Node> nlist=new ArrayList<Node>();
		//确定检索的起始的第一个元素节点
		if(querySelector.equals("/")){
			nlist.add(doc);
		}else{
			String[] keys=querySelector.split("/");
			if(keys.length>0){
				if(keys[0].equals("")){
					nlist.add(doc);
				}else{
					nlist=getNodes(doc, keys[0]);
				}
				//轮训层级遍历
				for(int i=1,len=keys.length;i<len;i++){
					//Log.d("cczw", ">>>>>"+keys[i]+">>>当前查询");
					ArrayList<Node> tmplist=new ArrayList<Node>();
					for(int j=0,l=nlist.size();j<l;j++){
						tmplist.addAll(getChilds(nlist.get(j),keys[i]));
					}
					nlist=tmplist;
				}
			}
		}
		return nlist;
	}
	/**
	 * 获取某节点的指定子节点 
	 * @param parentNode 父节点
	 * @param childNode  要查询的子节点 ，如果childNode为null则返回所有子节点
	 * */
	public ArrayList<Node> getChilds(Node parentNode,String childNode){
		ArrayList<Node> list=new ArrayList<Node>();
		Log.d("cczw",">>getChild>"+parentNode.getNodeName());
		
		NodeList ls=parentNode.getChildNodes();
		for(int j=0,l=ls.getLength();j<l;j++){
			if(ls.item(j).getNodeName().equals(childNode)||childNode==null){
				list.add(ls.item(j));
			}
		}
		return list;
	}
	/**
	 * 获取某节点的node类型子节点 不包括textnode和cdata等
	 * @param parentNode 父节点
	 * */
	public ArrayList<Node> getNodeChilds(Node parentNode){
		ArrayList<Node> list=new ArrayList<Node>();
		NodeList ls=parentNode.getChildNodes();
		for(int j=0,l=ls.getLength();j<l;j++){
			if(ls.item(j).getNodeType()==Node.ELEMENT_NODE){
				list.add(ls.item(j));
			}
		}
		return list;
	}
	/**
	 * 获取某节点的node类型子节点 不包括textnode和cdata等
	 * @param parentNode 父节点
	 * */
	public ArrayList<Node> getNodeChilds(String parentNode){
		ArrayList<Node> list=new ArrayList<Node>();
		NodeList pnode=doc.getElementsByTagName(parentNode);
		for(int j=0,l=pnode.getLength();j<l;j++){
			list.addAll(getNodeChilds(pnode.item(j)));
		}
		return list;
	}
	/**
	 * 获取某节点的指定子节点 
	 * @param parentNode 父节点
	 * @param childNode  要查询的子节点 ，如果childNode为null则返回所有子节点
	 * */
	public ArrayList<Node> getChilds(String parentNode,String childNode){
		ArrayList<Node> list=new ArrayList<Node>();
		NodeList ls=doc.getElementsByTagName(parentNode);
		for(int j=0,l=ls.getLength();j<l;j++){
			list.addAll(getChilds(ls.item(j),childNode));
		}
		return list;
	}
	/**
	 * 获取某节点的指定子节点的值 
	 * @param parentNode 父节点
	 * @param childNode  要查询的子节点 ，如果childNode为null则返回所有子节点
	 * */
	public ArrayList<Node> getChildsValues(String parentNode,String childNode){
		ArrayList<Node> list=new ArrayList<Node>();
		NodeList ls=doc.getElementsByTagName(parentNode);
		for(int j=0,l=ls.getLength();j<l;j++){
			list.addAll(getChilds(ls.item(j),childNode));
		}
		return list;
	}
	/**
	 * 获取某节点的所有后代节点
	 * @param parentNode 父节点
	 * @param childNode  要查询的后代节点
	 * */
	public ArrayList<Node> getNodes(Node parentNode,String childNode){
		ArrayList<Node> list=new ArrayList<Node>();
		NodeList ls=parentNode==doc?doc.getElementsByTagName(childNode):((Element)parentNode).getElementsByTagName(childNode);
		for(int j=0,l=ls.getLength();j<l;j++){
			list.add(ls.item(j));
		}
		return list;
	}
	/**
	 * 创建Element对象，合并了createElement和createTextNode
	 * 参数elementName 为元素名
	 * 参数innerText	  为元素内部文字，如果为空或null则不创建
	 * */
	public  Element createElement(String elementName,String innerText)
	{
		Element element=doc.createElement(elementName);
		if(innerText!=null)
		{
			element.setTextContent(innerText);
			//Node textNode=doc.createTextNode(innerText);
			//element.appendChild(textNode);
		}
		return element;
	}
	/**将doc 转换成bean,bean的值的类型目前只是String，int, ArrayList<String>或者ArrayList<内部类>，其它类型将当做自定义类来操作
	 * *注意1  内部类请定义为static关键字。
	 * @param index 如果值为多个，选择的索引，一般为0
	 * @return 传入的class类型
	 * @throws  Exception */
	public <T> T transferToBean(String rootnode,Class<T> cls) throws Exception{
		return transferBean("/"+rootnode,cls,0);
	}
	private <T> T transferBean(String querySelector,Class<T> cls, int index) throws Exception{
		//Log.d("cczw","cls>"+cls.getName()+">"+cls.getSimpleName()+"||>>"+querySelector);
		T obj=cls.newInstance();
		Field[] fields=cls.getDeclaredFields();
		for(int i=0,len=fields.length;i<len;i++){
			fields[i].setAccessible(true);
			Class<?> fcls=fields[i].getType();
			String filedname=fields[i].getName();
			//先获取第index个根元素 
			ArrayList<Node> pnodes=getNodes(querySelector);
			//Log.d("cczw",filedname+">"+querySelector+">>"+pnodes.size());
			if(pnodes.size()>index){
				ArrayList<Node> childs=getChilds(pnodes.get(index),filedname);
				//Log.d("cczw",pnodes.size()+">"+index+">>"+filedname+">"+childs.size());
				if(childs.size()>0){
					if(fcls.isArray()){
						fcls=fcls.getComponentType();
						Object vals=Array.newInstance(fcls, childs.size());
						if(fcls.isPrimitive()){
							if(fcls==int.class){
								for(int j=0,l=childs.size();j<l;j++){
									String val=getNodeValue(childs.get(j));
									Array.set(vals, j, val==null?0:Integer.parseInt(val));
								}
							}else if(fcls==short.class){
								for(int j=0,l=childs.size();j<l;j++){
									String val=getNodeValue(childs.get(j));
									Array.set(vals, j, val==null?0:Short.parseShort(val));
								}
							}else if(fcls==long.class){
								for(int j=0,l=childs.size();j<l;j++){
									String val=getNodeValue(childs.get(j));
									Array.set(vals, j, val==null?0:Long.parseLong(val));
								}
							}else if(fcls==char.class){
								for(int j=0,l=childs.size();j<l;j++){
									String val=getNodeValue(childs.get(j));
									Array.set(vals, j, val==null?0:val.charAt(0));
								}
							}else if(fcls==boolean.class){
								for(int j=0,l=childs.size();j<l;j++){
									String val=getNodeValue(childs.get(j));
									Array.set(vals, j, Boolean.parseBoolean(val));
								}
							}else if(fcls==double.class){
								for(int j=0,l=childs.size();j<l;j++){
									String val=getNodeValue(childs.get(j));
									Array.set(vals, j, val==null?0.0:Double.parseDouble(val));
								}
							}else if(fcls==float.class){
								for(int j=0,l=childs.size();j<l;j++){
									String val=getNodeValue(childs.get(j));
									Array.set(vals, j, val==null?0.0f:Float.parseFloat(val));
								}
							}
						}else if(fcls==String.class){
							for(int j=0,l=childs.size();j<l;j++){
								String val=getNodeValue(childs.get(j));
								Array.set(vals, j, val==null?"":val);
							}
						}else if(fcls==Integer.class){
							for(int j=0,l=childs.size();j<l;j++){
								String val=getNodeValue(childs.get(j));
								Array.set(vals, j, val==null?0:Integer.parseInt(val));
							}
						}else{
							for(int j=0,l=childs.size();j<l;j++){
								Array.set(vals, j, transferBean(querySelector+"/"+filedname,fcls,j));
							}
						}
						fields[i].set(obj,vals);
					}else if(fcls.isPrimitive()){
						String val=getNodeValue(childs.get(0));
						if(fcls==int.class){
							fields[i].set(obj, val==null?0:Integer.parseInt(val));
						}else if(fcls==short.class){
							fields[i].set(obj, val==null?0:Short.parseShort(val));
						}else if(fcls==long.class){
							fields[i].set(obj, val==null?0:Long.parseLong(val));
						}else if(fcls==char.class){
							fields[i].set(obj, val==null?0:val.charAt(0));
						}else if(fcls==boolean.class){
							fields[i].set(obj, Boolean.parseBoolean(val));
						}else if(fcls==double.class){
							fields[i].set(obj, val==null?0:Double.parseDouble(val));
						}else if(fcls==float.class){
							fields[i].set(obj, val==null?0:Float.parseFloat(val));
						}
					}else if(fcls==String.class){
						fields[i].set(obj, getNodeValue(childs.get(0)));
					}else if(fcls==Integer.class){
						String val=getNodeValue(childs.get(0));
						fields[i].set(obj, val==null?0:Integer.parseInt(val));
					}else if(fcls==ArrayList.class||fcls==List.class){
						//获取第一个node子节点的名称
						ArrayList<Node> cnodes=getNodeChilds(childs.get(0));
						if(cnodes.size()>0){
							String fnodename=cnodes.get(0).getNodeName();
							StringBuffer buffer=new StringBuffer(querySelector);
							if(querySelector.charAt(querySelector.length()-1)!='/'){
								buffer.append("/");
							}
							buffer.append(filedname+"/"+fnodename);
							//获取参数化类型
							Class<?> type = (Class<?>) ((ParameterizedType)fields[i].getGenericType()).getActualTypeArguments()[0];
							String fname=type.getSimpleName().toLowerCase();
							if(fname.equals("string")){
								//将其childs的值当做该field的值
								ArrayList<String> ls=getValues(buffer.toString());
								fields[i].set(obj, ls);
							}else{
								ArrayList<Object> ils=new ArrayList<Object>();
								for(int j=0,l=cnodes.size();j<l;j++){
									ils.add(transferBean(buffer.toString(),type,j));
								}
								fields[i].set(obj, ils);
							}
						}else{
							//获取参数化类型
							fields[i].set(obj, new ArrayList<Object>());
						}
					}else{
						fields[i].set(obj, transferBean(querySelector+"/"+filedname,fields[i].getType(),0));
					}
				}
			}
		}
		return obj;
	}
	
	/**转换javabean为xml文档
	 * @param  bean 要转换的java bean ,仅包括基本数据类型或者String，Integer,数组，List，ArrayList或者自定义的bean内部静态类，
	 * @param  root 指定容器节点，直属于pNode，所有产生的节点都是该节点的子节点，如果为null则不创建容器节点
	 * */
	@SuppressWarnings("unchecked")
	private Node transferFromBean(Object bean,String root){
		Node nroot=null;
		//创建根节点
		if(root!=null&&!("").equals(root)){
			nroot=createElement(root, null);
		}else{
			nroot=doc.createDocumentFragment();
		}
		//解析bean
		if(bean!=null){
			Field[] fields=bean.getClass().getFields();
			try {
				for(int i=0,len=fields.length;i<len;i++){
					fields[i].setAccessible(true);
					Class<?> fcls=fields[i].getType();
					String filedname=fields[i].getName();
					if(fcls.isArray()){
						fcls=fcls.getComponentType();
						Object vals=fields[i].get(bean);
						if(vals!=null){
							for(int j=0,l=Array.getLength(vals);j<l;j++){
								if(vals!=null){
									if(fcls.isPrimitive()||fcls==String.class||fcls==Integer.class){
										nroot.appendChild(createElement(filedname,Array.get(vals, j)+""));
									}else{
										Node croot=createElement(filedname,null);
										croot.appendChild(transferFromBean(Array.get(vals, j),null));
										nroot.appendChild(croot);
									}
								}
							}
						}
					}else if(fcls.isPrimitive()||fcls==String.class||fcls==Integer.class){
						Object val=fields[i].get(bean);
						if(val==null){
							nroot.appendChild(createElement(filedname,null));
						}
						else{
							nroot.appendChild(createElement(filedname,val.toString()));
						}
					}else if(fcls==List.class||fcls==ArrayList.class){
						Node croot=createElement(filedname,null);
						ArrayList<Object> ls =(ArrayList<Object>)fields[i].get(bean);
						if(ls==null){
							continue;
						}
						Class<?> pcls = (Class<?>) ((ParameterizedType)fields[i].getGenericType()).getActualTypeArguments()[0];
						if(pcls.isPrimitive()||pcls==String.class){
							for(int j=0,l=ls.size();j<l;j++){
								croot.appendChild(createElement("listitem",ls.get(j).toString()));
							}
						}else{
							for(int j=0,l=ls.size();j<l;j++){
								croot.appendChild(transferFromBean(ls.get(j),"listitem"));
							}
						}
						nroot.appendChild(croot);
					}else{
						Node croot=createElement(filedname,null);
						croot.appendChild(transferFromBean(fields[i].get(bean),null));
						nroot.appendChild(croot);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nroot;
	}
	/**
	 * 保存dom树到xml文件  UTF-8
	 * */
	public void save(String filepath){		
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer transformer = null;
		try {
			transformer = tfactory.newTransformer();
		} catch (TransformerConfigurationException e){
			e.printStackTrace();
		}
		// 将DOM对象转化为DOMSource类对象，该对象表现为转化成别的表达形式的信息容器。
        DOMSource source = new DOMSource(doc);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		// 获得一个StreamResult类对象，该对象是DOM文档转化成的其他形式的文档的容器，可以是XML文件，文本文件，HTML文件。这里为一个XML文件。
        StreamResult result = new StreamResult(filepath);
		// 调用API，将DOM文档转化成XML文件。
		try {
			transformer.transform(source,result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}
	/**
	 * 返回xml的字符串
	 * */
	public String getAsString(){
		String xmlstr="";
		TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer transformer = null;
		try {
			transformer = tfactory.newTransformer();
		} catch (TransformerConfigurationException e){
			e.printStackTrace();
		}
		// 将DOM对象转化为DOMSource类对象，该对象表现为转化成别的表达形式的信息容器。
        DOMSource source = new DOMSource(doc);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		StringWriter strWtr = new StringWriter();
        StreamResult strResult = new StreamResult(strWtr);
		// 调用API，将DOM文档转化成XML文件。
		try {
			transformer.transform(source,strResult);
			xmlstr=strResult.getWriter().toString();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return xmlstr;
	}
	
	/**
	 * 序列化所给类所有成员变量,显示排序还待优化
	 * @param clazz  传入的类
	 * @param prefix 输出前缀，如果为null则为<<
	 * */
	@SuppressWarnings("unchecked")
	public static String toString(Object obj,String prefix){
		StringBuffer buffer=new StringBuffer();
		prefix=prefix==null?">>":prefix;
		buffer.append("\n"+prefix+obj);
		Field[] fields=obj.getClass().getDeclaredFields();
		for(int i=0,len=fields.length;i<len;i++){
			fields[i].setAccessible(true);
			String filedname=fields[i].getName();
			Class<?> fcls=fields[i].getType();
			try {
				if(fcls.isArray()){
					fcls=fcls.getComponentType();
					Object vals=fields[i].get(obj);
					if(fcls.isPrimitive()||fcls==String.class||fcls==Integer.class){
						for(int j=0,l=Array.getLength(vals);j<l;j++){
							buffer.append("\n"+prefix+filedname+"="+Array.get(vals, j));
						}
					}else{
						for(int j=0,l=Array.getLength(vals);j<l;j++){
							buffer.append("\n"+toString(Array.get(vals, j),prefix+prefix));
						}
					}
					
				}
				if(fcls.isPrimitive()||fcls==String.class||fcls==Integer.class){
					buffer.append("\n"+prefix+filedname+"="+fields[i].get(obj));
				}else if(fcls==ArrayList.class||fcls==List.class){
					//获取参数化类型
					Class<?> type =   (Class<?>) ((ParameterizedType)fields[i].getGenericType()).getActualTypeArguments()[0];
					String fname=type.getSimpleName().toLowerCase();
					if(fname.equals("string")||fname.equals("integer")){
						buffer.append("\n"+prefix+filedname+"="+Arrays.toString(((ArrayList<String>)fields[i].get(obj)).toArray()));
					}else{
						ArrayList<?> tls=(ArrayList<?>) fields[i].get(obj);
						for(int j=0,l=tls.size();j<l;j++){
							buffer.append(toString(tls.get(j),prefix+prefix));
						}
					}
				}else{
					buffer.append("\n"+toString(fields[i].get(obj),prefix+prefix));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		buffer.append("\n");
		String[] tempval=buffer.toString().split("\n");
		Arrays.sort(tempval);
		buffer.setLength(0);
		for(int i=tempval.length-1;i>=0;i--){
			buffer.append("\n"+tempval[i]);
		}
 		return buffer.toString();
	}
	public void close(){
		doc=null;
	}
}
