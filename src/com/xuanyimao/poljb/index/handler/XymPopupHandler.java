/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月25日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.handler;

import java.awt.Rectangle;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.xuanyimao.poljb.index.JxbManager;

/**
 * @Description: 弹窗handler
 * @author liuming
 */
public class XymPopupHandler implements PopupHandler {

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.swing.DefaultPopupHandler#handlePopup(com.teamdev.jxbrowser.chromium.PopupParams)
	 */
	@Override
	public PopupContainer handlePopup(PopupParams params) {
//		JxbManager.getInstance().createBrowser(params.getURL());
		return new PopupContainer() {
			@Override
			public void insertBrowser(final Browser browser, Rectangle initialBounds) {
				JxbManager.getInstance().createBrowser(browser);
			}
		};
	}
	
	
}
