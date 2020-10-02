/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月30日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.xuanyimao.poljb.index.bean.AnnoMethod;
import com.xuanyimao.poljb.index.bean.AnnoRepertory;
import com.xuanyimao.poljb.index.bean.Message;
import com.xuanyimao.poljb.index.bean.MethodParam;
import com.xuanyimao.poljb.index.util.ToolUtil;

/**
 * @Description: JS与JAVA交互的处理类
 * 				为何使用此类做JS交互？
 * 				多次测试发现，在浏览器创建完window.document后调用JsObject的setProperty设置Java对象，在$(document).ready();方法中有时会出现对象未定义
 * 				推测document加载与将Java对象载入JS上下文是同步进行的
 * 				所以使用此类做JS与JAVA交互的总入口，尽量避免对象未定义的事情发生。另一个，也是为了方便js处理数据。
 * 				网页上调用示例：Java.exec("test",JSON.stringify({data:\"测试\"})); 或者 Java.exec("test",{data:\"测试\"});
 * 				绕了一圈，又绕回来了
 * @author liuming
 */
public class PoljbJsToJava {
	//gson对象
	Gson gson=new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
	
	/**
	 * 调用扫描到的Java方法
	 * @author:liuming
	 * @param funcName 函数名
	 * @return
	 */
	public String exec(String funcName) {
		return exec(funcName, "");
	}
	/**
	 * 调用扫描到的Java方法
	 * @author:liuming
	 * @param funcName 函数名
	 * @param jsObject 前端传入的JS对象
	 * @return
	 */
	public String exec(String funcName,JSObject jsObject) {
//		System.out.println(jsObject.toJSONString());
		return exec(funcName, jsObject.toJSONString());
	}
	/**
	 * 调用扫描到的Java方法
	 * @author:liuming
	 * @param funcName 函数名
	 * @param jsObject 前端传入的JSON字符串
	 * @return
	 */
	public String exec(String funcName,String params) {
		System.out.println("函数名："+funcName);
		System.out.println("参数："+params);
		try {
	    	AnnoMethod annoMethod = AnnoRepertory.getInstance().getMethodMap().get(funcName);
	    	if(annoMethod==null) {
	    		System.out.println("函数未在Java代码中声明，调用失败!");
	    		return gson.toJson(Message.error("函数未在Java代码中声明，调用失败!"));
	    	}
	//    	System.out.println(annoMethod.getMethod().getReturnType());
	    	JsonObject jsonObject=null;
	    	if(StringUtils.isNotBlank(params)) {
		    	JsonParser jp=new JsonParser();
		    	jsonObject=jp.parse(params).getAsJsonObject();
	    	}
	    	//获取方法的参数列表
	    	List<MethodParam> methodParam = annoMethod.getMethodParam();
	    	Method method=annoMethod.getMethod();
	    	method.setAccessible(true);
	    	Object result=null;
	    	if(methodParam==null || methodParam.isEmpty()) {//不需要传递参数
	    		result=method.invoke(annoMethod.getAnnoClass().getObj());
	//    		System.out.println(gson.toJson(result));
	    	}else {//对传入的参数进行处理
	    		Object[] objs=new Object[methodParam.size()];
	    		//遍历参数数组是否存在ho类型的参数，标记位置
	    		for(int i=0;i<methodParam.size();i++) {
	    			MethodParam mp=methodParam.get(i);
    				if(jsonObject!=null && jsonObject.get(mp.getName())!=null) {
    					objs[i]=gson.fromJson(jsonObject.get(mp.getName()), mp.getCls());
    				}else {
    					objs[i]=null;
    				}
	    		}
	    		
	    		result=method.invoke(annoMethod.getAnnoClass().getObj(),objs);
	    	}
	    	return gson.toJson(result);
    	}catch(Exception e) {
    		return gson.toJson(Message.error("程序异常:<br/>"+ToolUtil.getExceptionMessage(e)+"<br/><font color='red'>[一位优秀的程序员准备甩锅](๑＞ڡ＜)✿ </font>"));
    	}
	}
	
}
