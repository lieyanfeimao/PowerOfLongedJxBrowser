/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月28日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.listener;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.xuanyimao.poljb.index.PoljbJsToJava;
import com.xuanyimao.poljb.index.bean.Injection;
import com.xuanyimao.poljb.index.bean.Scp;
import com.xuanyimao.poljb.index.bean.ScpConfig;
import com.xuanyimao.poljb.index.jsimpl.Index;

/**
 * @Description: 用于接收浏览器加载事件的侦听器
 * @author liuming
 */
public class XymLoadListener implements LoadListener {
	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.events.LoadAdapter#onFinishLoadingFrame(com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent)
	 */
	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent event) {
		//输出url
		String url=event.getValidatedURL();
		System.out.println("加载完成："+url+".."+event.getFrameId());
//		if(event.isMainFrame()) {
//			System.out.println("脚本注入的链接："+url);
			//在此处进行脚本注入
			Injection inj=Injection.getInstance();
			List<ScpConfig> clist=inj.getConfigs();
			if(clist!=null && !clist.isEmpty()) {
				for(ScpConfig sc:clist) {
//					System.out.println(url+"////"+sc.getUrl()+"..."+sc.getType()+"---"+url.matches(sc.getUrl())+"+++"+url.matches("https://www.baidu.com"));
					if("1".equals(sc.getType()) && StringUtils.isNotBlank(sc.getUrl()) && url.matches(sc.getUrl()) ) {
						Scp scp = inj.getScpInfo().get(sc.getFile());
						System.out.println(scp);
						if(scp!=null) {
							//执行js脚本
//							event.getBrowser().executeJavaScript(scp.getScript(), null, 0);
							event.getBrowser().executeJavaScript(event.getFrameId(),scp.getScript());
							System.out.println(url+">>>"+sc.getUrl()+"  执行注入脚本...");
						}
					}
				}
//			}
		}
		
	}

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.events.LoadAdapter#onStartLoadingFrame(com.teamdev.jxbrowser.chromium.events.StartLoadingEvent)
	 */
	@Override
	public void onStartLoadingFrame(StartLoadingEvent event) {
		System.out.println("开始加载:"+event.getValidatedURL());
	}

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.events.LoadAdapter#onDocumentLoadedInMainFrame(com.teamdev.jxbrowser.chromium.events.LoadEvent)
	 * 创建主框架的window.document对象后调用
	 */
	@Override
	public void onDocumentLoadedInMainFrame(LoadEvent event) {
	    System.out.println("文档加载完成...");
	}

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.events.LoadAdapter#onDocumentLoadedInFrame(com.teamdev.jxbrowser.chromium.events.FrameLoadEvent)
	 */
	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent event) {
		System.out.println("文档加载完成...");
	}

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.events.LoadAdapter#onFailLoadingFrame(com.teamdev.jxbrowser.chromium.events.FailLoadingEvent)
	 */
	@Override
	public void onFailLoadingFrame(FailLoadingEvent event) {
//		System.out.println("框架加载失败...");
	}

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.events.LoadAdapter#onProvisionalLoadingFrame(com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent)
	 */
	@Override
	public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
//		System.out.println("临时负载已成功提交给指定帧");
	}
	
	
}
