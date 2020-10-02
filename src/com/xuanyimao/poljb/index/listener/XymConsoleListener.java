/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月26日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.listener;

import com.teamdev.jxbrowser.chromium.events.ConsoleEvent;
import com.teamdev.jxbrowser.chromium.events.ConsoleListener;

/**
 * @Description: 控制台输出
 * @author liuming
 */
public class XymConsoleListener implements ConsoleListener{

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.events.ConsoleListener#onMessage(com.teamdev.jxbrowser.chromium.events.ConsoleEvent)
	 */
	@Override
	public void onMessage(ConsoleEvent event) {
		System.out.println(event.getSource()+"(行:"+event.getLineNumber()+")["+event.getLevel()+"]:"+event.getMessage()+"");
	}
	
}
