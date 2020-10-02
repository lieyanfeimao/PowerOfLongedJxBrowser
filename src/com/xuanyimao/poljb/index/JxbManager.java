/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月24日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.xuanyimao.poljb.index.bean.TabBrowser;
import com.xuanyimao.poljb.index.handler.XymContextMenuHandler;
import com.xuanyimao.poljb.index.handler.XymPopupHandler;
import com.xuanyimao.poljb.index.listener.TabCloseListener;
import com.xuanyimao.poljb.index.listener.XymConsoleListener;
import com.xuanyimao.poljb.index.listener.XymLoadListener;
import com.xuanyimao.poljb.index.listener.XymScriptContextAdapter;
import com.xuanyimao.poljb.index.listener.XymTitleListener;

/**
 * @Description: 力量管理者，管理浏览器窗口等数据
 * @author liuming
 */
public class JxbManager {
	/**管理类实例*/
	private static JxbManager jmg;
	/**窗口对象*/
	private JFrame frame;
	/**tab栏*/
	private static JTabbedPane tabbedPane;
	/**tab栏集合**/
	private List<TabBrowser> tbList=new ArrayList<TabBrowser>();
	/**tb的索引*/
	private int tbIndex=0; 
	/**默认标题*/
	private final static String TITLE_INFO="正在载入...";
	/**最后打开的浏览器对象*/
	private Browser browser=null;
	
	private XymPopupHandler popupHandler=new XymPopupHandler();
	
	private XymContextMenuHandler menuHandler=new XymContextMenuHandler();
	
	private XymTitleListener titleListener=new XymTitleListener();
	
	private XymConsoleListener consoleListener=new XymConsoleListener();
	
	private XymLoadListener loadListener=new XymLoadListener();
	
	private JxbManager(JFrame frame) {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		tabbedPane=new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
        this.frame=frame;
        
    	this.frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    	this.frame.pack();
    	this.frame.setSize(800, 600);
    	this.frame.setVisible(true);
    	this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option= JOptionPane.showConfirmDialog(
                		frame, "确定退出系统? ", "提示 ",JOptionPane.YES_NO_OPTION);
            	if(option==JOptionPane.YES_OPTION) {
            		closeAllBrowser();
            		frame.dispose();
            	}else {
            		return;
            	}
            }
        });
	}
	/**
	 * 获取JxbManager实例对象
	 * @author:liuming
	 * @param frame JFrame对象
	 * @return
	 */
	public static JxbManager getInstance(JFrame frame) {
		if(jmg==null) {
			jmg=new JxbManager(frame);
		}
		return jmg;
	}
	/**
	 * 获取JxbManager实例对象，调用此方法之前必须在任意位置调用过getInstance(JFrame frame)
	 * @author:liuming
	 * @return
	 */
	public static JxbManager getInstance() {
		return jmg;
	}

	/**
	 * 获取TabBrowser对象集合
	 * @return tbList
	 */
	public List<TabBrowser> getTbList() {
		return tbList;
	}
	/**
	 * 获取JFrame窗口对象
	 * @return frame
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * 获取JTabbedPane对象
	 * @return tabbedPane
	 */
	public static JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	/**
	 * 根据url创建一个新的tab页
	 * @author:liuming
	 * @param url
	 * @return 最后一个tab的索引
	 */
	public void createBrowser(final Browser browser) {
	    this.browser=browser;
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
//				System.out.println("创建浏览器："+browser.getURL());
				BrowserView view = new BrowserView(browser);
				
				//弹窗handler
				browser.setPopupHandler(popupHandler);
				//右键菜单监听
				browser.setContextMenuHandler(menuHandler);
				
				//标题更改监听器
				browser.addTitleListener(titleListener);
				//控制台监听器
				browser.addConsoleListener(consoleListener);
				//添加脚本支持
				browser.addLoadListener(loadListener);
				
				browser.addScriptContextListener(new XymScriptContextAdapter());
				
				tabbedPane.addTab(".",view);
				int lastIndex=tabbedPane.getTabCount()-1;
				tbIndex++;
				
				//创建自定义tab栏
				JPanel jp=new JPanel();
				
				JLabel ltitle=new JLabel(TITLE_INFO);
				JLabel lclose=new JLabel("X");
				jp.setOpaque(false);
				ltitle.setHorizontalAlignment(JLabel.LEFT);
				lclose.setHorizontalAlignment(JLabel.RIGHT);
				jp.add(ltitle);
				jp.add(lclose);
				//添加关闭事件监听
				lclose.addMouseListener(new TabCloseListener(tbIndex));
				
				tabbedPane.setTabComponentAt(lastIndex, jp);
				
				TabBrowser tb=new TabBrowser(tbIndex, browser, ltitle,view);
				tbList.add(tb);
				
				tabbedPane.setSelectedIndex(lastIndex);
			}
		});
	}
	
	/**
	 * 关闭所有浏览器窗口
	 * @author:liuming
	 */
	public void closeAllBrowser() {
		for(int i=tbList.size()-1;i>=0;i--) {
			TabBrowser tb=tbList.get(i);
			tabbedPane.removeTabAt(i);
			tb.getBrowser().dispose();
			System.out.println("移除索引为"+i+"的tab...");
		}
	}
	
	/**
	 * 获取当前展示的TabBrowser对象
	 * @author:liuming
	 * @return
	 */
	public TabBrowser getSelectTabBrowser() {
		BrowserView view=(BrowserView)tabbedPane.getSelectedComponent();
		for(TabBrowser tb:tbList) {
			if(view==tb.getView()) {
				return tb;
			}
		}
		return null;
	}
	
	/**
	 * 修改标题
	 * @author:liuming
	 * @param browser
	 * @param title
	 */
	public void updateTabTitle(Browser browser,String title) {
		if(StringUtils.isNotBlank(title)) {
			if(title.length()>12) title=title.substring(0, 12)+"...";
			for(TabBrowser tb:tbList) {
				if(tb.getBrowser()==browser) {
					tb.getTitle().setText(title);
					break;
				}
			}
		}
	}
	/**
	 * 移除tab
	 * @author:liuming
	 * @param browser
	 * @param index
	 */
	public void removeTab(Browser browser,int index) {
		if(browser!=null) {
			for(int i=0;i<tbList.size();i++) {
				TabBrowser tb=tbList.get(i);
				if(tb.getBrowser()==browser) {
					tabbedPane.removeTabAt(i);
					tb.getBrowser().dispose();
					tbList.remove(i);
					break;
				}
			}
			
		}else {
			
			for(int i=0;i<tbList.size();i++) {
				TabBrowser tb=tbList.get(i);
				if(tb.getIndex()==index) {
					tabbedPane.removeTabAt(i);
					tb.getBrowser().dispose();
					tbList.remove(i);
					break;
				}
			}
		}
	}
    /**
     * browser
     * @return browser
     */
    public Browser getBrowser() {
        return browser;
    }
}
