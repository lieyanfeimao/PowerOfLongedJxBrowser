/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月27日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.handler;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.ContextMenuHandler;
import com.teamdev.jxbrowser.chromium.ContextMenuParams;
import com.xuanyimao.poljb.index.JxbManager;
import com.xuanyimao.poljb.index.bean.DevRepertory;
import com.xuanyimao.poljb.index.bean.Injection;
import com.xuanyimao.poljb.index.bean.Scp;
import com.xuanyimao.poljb.index.bean.ScpConfig;
import com.xuanyimao.poljb.index.bean.TabBrowser;

/**
 * @Description: 鼠标右键菜单
 * @author liuming
 */
public class XymContextMenuHandler implements ContextMenuHandler {

	/* (non-Javadoc)
	 * @see com.teamdev.jxbrowser.chromium.ContextMenuHandler#showContextMenu(com.teamdev.jxbrowser.chromium.ContextMenuParams)
	 */
	@Override
	public void showContextMenu(ContextMenuParams params) {
		TabBrowser tb = JxbManager.getInstance().getSelectTabBrowser();
		Browser browser=tb.getBrowser();
//		System.out.println(tb+"---"+component+".."+tb.getView());
		if(tb!=null) {
//			System.out.println("创建右键菜单");
			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.add(createMenuItem("返回", new Runnable() {
				@Override
				public void run() {
					browser.goBack();
				}
			}, browser.canGoBack()));
			popupMenu.add(createMenuItem("前进", new Runnable() {
				@Override
				public void run() {
					browser.goForward();
				}
			}, browser.canGoForward()));
			
			popupMenu.add(createMenuItem("刷新", new Runnable() {
				@Override
				public void run() {
					browser.reload();
				}
			}, true));
			
			popupMenu.addSeparator();
			popupMenu.add(createMenuItem("首页", new Runnable() {
				@Override
				public void run() {
					Browser bs=new Browser(DevRepertory.getInstance().getBrowserContext());
					bs.loadURL("file:///"+DevRepertory.getInstance().getAppPath()+"/index.html");
					JxbManager.getInstance().createBrowser(bs);
				}
			}, true));
			
			popupMenu.addSeparator();
			
			JMenu scpMenu=new JMenu("脚本注入");
			
			//添加已载入的脚本，设置其动作
			Injection inj=Injection.getInstance();
	        List<ScpConfig> configs = inj.getConfigs();
	        if(configs!=null && !configs.isEmpty()) {
	        	for(ScpConfig sc:configs) {
	        		if(StringUtils.isNotBlank(sc.getName())) {
	        			scpMenu.add(createMenuItem(sc.getName(), new Runnable() {
							@Override
							public void run() {
								Scp scp = Injection.getInstance().getScpInfo().get(sc.getFile());
								browser.executeJavaScript(scp.getScript());
								System.out.println("手动注入脚本...");
							}
						}, true)
	        			);
	        		}
	        	}
	        }
			
			//添加子菜单
			popupMenu.add(scpMenu);
			//显示菜单
//			Point p1=MouseInfo.getPointerInfo().getLocation();
//			System.out.println(p1.x+"..."+p1.y);
			
			Point location = params.getLocation();
//			System.out.println(location.x+"..."+location.y);
			//获取缩放比例
			int scaling=DevRepertory.getInstance().getScaling();
			
			SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   popupMenu.show(tb.getView(), location.x*scaling/100, location.y*scaling/100);
               }
           });
			
		}
	}
	/**
	 * 创建菜单项
	 * @author:liuming
	 * @param title 菜单标题
	 * @param action
	 * @return
	 */
	private static JMenuItem createMenuItem(String title, final Runnable action,boolean enabled) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.setEnabled(enabled);
        if(enabled) {
	        menuItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                action.run();
	            }
	        });
        }
        return menuItem;
    }

}
