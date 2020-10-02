/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年10月14日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.handler;

import com.teamdev.jxbrowser.chromium.ResourceHandler;
import com.teamdev.jxbrowser.chromium.ResourceParams;

/**
 * @Description:
 * @author liuming
 */
public class XymResourceHandler implements ResourceHandler {

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.ResourceHandler#canLoadResource(com.teamdev.jxbrowser.chromium.ResourceParams)
	 */
	@Override
	public boolean canLoadResource(ResourceParams params) {
//		System.out.println("资源加载监听："+params.getURL());
		return true;
	}

}
