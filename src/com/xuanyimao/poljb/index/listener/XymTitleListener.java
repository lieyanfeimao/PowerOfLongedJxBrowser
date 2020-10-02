/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月25日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.listener;

import com.teamdev.jxbrowser.chromium.events.TitleEvent;
import com.teamdev.jxbrowser.chromium.events.TitleListener;
import com.xuanyimao.poljb.index.JxbManager;

/**
 * @Description: 标题更改监听器
 * @author liuming
 */
public class XymTitleListener implements TitleListener {

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.events.TitleListener#onTitleChange(com.teamdev.jxbrowser.chromium.events.TitleEvent)
	 */
	@Override
	public void onTitleChange(TitleEvent event) {
		JxbManager.getInstance().updateTabTitle(event.getBrowser(), event.getTitle());
	}

}
