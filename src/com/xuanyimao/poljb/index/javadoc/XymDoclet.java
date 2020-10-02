/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年10月14日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.javadoc;

import com.sun.javadoc.RootDoc;

/**
 * @Description: 文档处理类
 * @author liuming
 */
public class XymDoclet {
	
	public static RootDoc rootDoc;
	
	public XymDoclet() {
	}
	/**
	 * 不知道为什么，反正这么写就对了
	 * @author:liuming
	 * @param rootDoc
	 * @return
	 */
	public static boolean start(RootDoc rootDoc) {
		XymDoclet.rootDoc = rootDoc;
        return true;
    }

}
