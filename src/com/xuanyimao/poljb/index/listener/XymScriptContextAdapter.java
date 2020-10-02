/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2020年3月23日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.listener;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextEvent;
import com.xuanyimao.poljb.index.PoljbJsToJava;

/**
 * @Description:
 * @author liuming
 */
public class XymScriptContextAdapter extends ScriptContextAdapter {
    /* (non-Javadoc)
     * @see com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter#onScriptContextCreated(com.teamdev.jxbrowser.chromium.events.ScriptContextEvent)
     */
    @Override
    public void onScriptContextCreated(ScriptContextEvent event) {
        System.out.println("注入公共脚本!");
        Browser browser = event.getBrowser();
        JSValue value = browser.executeJavaScriptAndReturnValue("window");
        value.asObject().setProperty("Java", new PoljbJsToJava());
    }
}
