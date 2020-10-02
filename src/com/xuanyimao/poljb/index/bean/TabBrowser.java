/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月25日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.bean;

import javax.swing.JLabel;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

/**
 * @Description: Tab中的浏览器对象
 * @author liuming
 */
public class TabBrowser {
	/**索引*/
	private int index;
	/**浏览器对象*/
	private Browser browser;
	/**浏览器UI对象*/
	private BrowserView view;
	/**浏览器标题-JLabel对象*/
	private JLabel title;
	/**
	 * 获取 索引
	 * @return index 索引
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * 设置 索引
	 * @param index 索引
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * 获取 浏览器对象
	 * @return browser 浏览器对象
	 */
	public Browser getBrowser() {
		return browser;
	}
	/**
	 * 设置 浏览器对象
	 * @param browser 浏览器对象
	 */
	public void setBrowser(Browser browser) {
		this.browser = browser;
	}
	/**
	 * 获取 浏览器标题-JLabel对象
	 * @return ltitle 浏览器标题-JLabel对象
	 */
	public JLabel getTitle() {
		return title;
	}
	/**
	 * 设置 浏览器标题-JLabel对象
	 * @param ltitle 浏览器标题-JLabel对象
	 */
	public void setTitle(JLabel title) {
		this.title = title;
	}
	
	
	/**
	 * 获取 浏览器UI对象
	 * @return view 浏览器UI对象
	 */
	public BrowserView getView() {
		return view;
	}
	/**
	 * 设置 浏览器UI对象
	 * @param view 浏览器UI对象
	 */
	public void setView(BrowserView view) {
		this.view = view;
	}
	
	public TabBrowser() {}
	/**
	 * @param index 索引
	 * @param browser 浏览器对象
	 * @param ltitle 浏览器标题-JLabel对象
	 */
	public TabBrowser(int index, Browser browser, JLabel title,BrowserView view) {
		super();
		this.index = index;
		this.browser = browser;
		this.title = title;
		this.view=view;
	}
}
