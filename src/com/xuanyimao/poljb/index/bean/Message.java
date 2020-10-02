/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月28日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.bean;

/**
 * @Description: 处理结果对象
 * @author liuming
 */
public class Message {
	/**是否成功*/
	public Boolean suc;
	/**消息描述*/
	public String msg;
	/**数据内容*/
	public Object data;
	/**
	 * @param success 是否成功
	 * @param msg 消息描述
	 * @param data 数据内容
	 */
	public Message(Boolean suc, String msg, Object data) {
		super();
		this.suc = suc;
		this.msg = msg;
		this.data = data;
	}
	/**
	 * 操作成功
	 * @author:liuming
	 * @return
	 */
	public static Message success() {
		return success("操作成功",null);
	}
	/**
	 * 操作成功
	 * @author:liuming
	 * @param msg 消息
	 * @return
	 */
	public static Message success(String msg) {
		return success(msg,null);
	}
	/**
	 * 操作成功
	 * @author:liuming
	 * @param msg 消息
	 * @param data 数据
	 * @return
	 */
	public static Message success(String msg,Object data) {
		return new Message(true, msg, data);
	}
	
	/**
	 * 操作失败
	 * @author:liuming
	 * @return
	 */
	public static Message error() {
		return error("操作失败",null);
	}
	/**
	 * 操作失败
	 * @author:liuming
	 * @param msg 消息
	 * @return
	 */
	public static Message error(String msg) {
		return error(msg,null);
	}
	/**
	 * 操作失败
	 * @author:liuming
	 * @param msg 消息
	 * @param data 数据
	 * @return
	 */
	public static Message error(String msg,Object data) {
		return new Message(false, msg, data);
	}
}
