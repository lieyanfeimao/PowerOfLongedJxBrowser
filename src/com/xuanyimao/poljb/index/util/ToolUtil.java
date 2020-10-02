/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2018年11月9日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.util;

import java.io.File;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.xuanyimao.poljb.index.bean.DevRepertory;

/**
 * @Description: 工具类
 * @author liuming
 */
public class ToolUtil {
	
	/**
	 * 获取异常的详细信息
	 * @author:liuming
	 * @param e
	 * @return
	 */
	public static String getExceptionMessage(Exception e) {
		return ExceptionUtils.getFullStackTrace(e);
	}
	/**
	 * 获取转换后的字符串，将 {path}、{appPath}、{appId}转换为指定路径
	 * @author:liuming
	 * @param data 数据
	 * @param appId 如果需要转换{appId}或{appPath}，此值必填
	 * @return
	 */
	public static String getTransData(String data,String appId) {
		return data.replace("{path}", DevRepertory.getInstance().getPath())
				.replace("{appPath}", DevRepertory.getInstance().getAppPath()+File.separator+appId)
				.replace("{appId}", appId);
	}
	
//	/**
//	 * 获取24小时后的时间
//	 * @return
//	 */
//	public static Long getLastDayTime() {
//		try {
//			Thread.sleep(1000*3600*24);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return System.currentTimeMillis();
//	}
}
